$ErrorActionPreference = "Stop"

$raiz = Split-Path -Parent $PSScriptRoot
$actual = Join-Path $raiz "sgca-inicial\SGCA.java"
$resultados = Join-Path $raiz "sgca-inicial\docs\pruebas\resultados"
$temporal = Join-Path ([IO.Path]::GetTempPath()) (
    "sgca-pruebas-" + [guid]::NewGuid()
)

New-Item -ItemType Directory -Force $resultados | Out-Null
New-Item -ItemType Directory -Force $temporal | Out-Null

$utf8 = New-Object System.Text.UTF8Encoding($false)

function Ejecutar-Java($archivo, $entrada) {
    $config = New-Object System.Diagnostics.ProcessStartInfo
    $config.FileName = "java"
    $config.Arguments = '"' + $archivo + '"'
    $config.UseShellExecute = $false
    $config.RedirectStandardInput = $true
    $config.RedirectStandardOutput = $true
    $config.RedirectStandardError = $true

    $proceso = New-Object System.Diagnostics.Process
    $proceso.StartInfo = $config
    [void]$proceso.Start()

    $salida = $proceso.StandardOutput.ReadToEndAsync()
    $errores = $proceso.StandardError.ReadToEndAsync()

    $proceso.StandardInput.WriteLine($entrada)
    $proceso.StandardInput.Close()

    if (-not $proceso.WaitForExit(30000)) {
        $proceso.Kill()
        throw "La ejecucion supero los 30 segundos."
    }

    $texto = $salida.GetAwaiter().GetResult()
    $errorJava = $errores.GetAwaiter().GetResult()
    $codigo = $proceso.ExitCode
    $proceso.Dispose()

    if ($codigo -ne 0) {
        throw "Java termino con codigo ${codigo}: $errorJava"
    }

    return $texto
}

function Normalizar($texto) {
    # Solo normalizamos saltos de linea y campos de fecha conocidos.
    $texto = $texto.Replace("`r`n", "`n")
    return [regex]::Replace(
        $texto,
        '(Fecha: |fecha=)\d{4}-\d{2}-\d{2}',
        '${1}<FECHA>'
    )
}

$casos = @(
    @{ Nombre="regular"; Entrada="1`n1`n1`n2`n1`n3`n1`n0";
       Esperado="CERTIFICADO DE ALUMNO REGULAR" },
    @{ Nombre="analitico"; Entrada="1`n1`n2`n2`n1`n3`n1`n0";
       Esperado="Promedio: 7.0" },
    @{ Nombre="deuda"; Entrada="1`n2`n1`n2`n1`n0";
       Esperado="No se emite: alumno con deuda" },
    @{ Nombre="no_regular"; Entrada="1`n3`n1`n2`n1`n0";
       Esperado="No se emite: alumno no regular" },
    @{ Nombre="doble_emision"; Entrada="1`n1`n1`n2`n1`n2`n1`n0";
       Esperado="Solicitud ya emitida" },
    @{ Nombre="listar"; Entrada="1`n1`n1`n4`n0";
       Esperado="REGISTRADA" },
    @{ Nombre="alumno_inexistente"; Entrada="1`n99`n1`n0";
       Esperado="Alumno inexistente" },
    @{ Nombre="tipo_invalido"; Entrada="1`n1`n9`n0";
       Esperado="Tipo invalido" },
    @{ Nombre="ambos_invalidos"; Entrada="1`n99`n9`n0";
       Esperado="Alumno inexistente" },
    @{ Nombre="consulta_inexistente"; Entrada="3`n999`n0";
       Esperado="Solicitud inexistente" },
    @{ Nombre="emision_inexistente"; Entrada="2`n999`n0";
       Esperado="Solicitud inexistente" },
    @{ Nombre="lista_vacia"; Entrada="4`n0";
       Esperado="Sin solicitudes" },
    @{ Nombre="entrada_invalida"; Entrada="1`nhola`n0";
       Esperado="Debe ingresar un numero entero" },
    @{ Nombre="opcion_invalida"; Entrada="9`n0";
       Esperado="Opcion invalida" }
)

Push-Location $raiz
try {
    # Leemos el tag sin modificarlo ni cambiar de rama.
    $codigoBase = & git show "estado-inicial:sgca-inicial/SGCA.java"
    if ($LASTEXITCODE -ne 0) {
        throw "No se pudo recuperar SGCA.java del tag estado-inicial."
    }

    $base = Join-Path $temporal "SGCA.java"
    [IO.File]::WriteAllText($base, ($codigoBase -join "`n"), $utf8)

    $resumen = @()
    $fallos = 0

    foreach ($caso in $casos) {
        $nombre = $caso.Nombre
        $salidaBase = Ejecutar-Java $base $caso.Entrada
        $salidaActual = Ejecutar-Java $actual $caso.Entrada

        [IO.File]::WriteAllText(
            (Join-Path $resultados "$nombre-entrada.txt"),
            $caso.Entrada, $utf8
        )
        [IO.File]::WriteAllText(
            (Join-Path $resultados "$nombre-base.txt"),
            $salidaBase, $utf8
        )
        [IO.File]::WriteAllText(
            (Join-Path $resultados "$nombre-actual.txt"),
            $salidaActual, $utf8
        )

        $coinciden = [string]::Equals(
            (Normalizar $salidaBase),
            (Normalizar $salidaActual),
            [StringComparison]::Ordinal
        )
        $baseValida = $salidaBase.Contains($caso.Esperado)

        if ($coinciden -and $baseValida) {
            $linea = "OK - $nombre"
        } else {
            $linea = "FALLO - $nombre"
            $fallos++
        }

        Write-Host $linea
        $resumen += $linea
    }

    $resumen += "Total: $($casos.Count). Fallos: $fallos."
    [IO.File]::WriteAllLines(
        (Join-Path $resultados "resumen.txt"), $resumen, $utf8
    )
    Write-Host $resumen[-1]

    if ($fallos -gt 0) {
        throw "Hay diferencias. Revisar las salidas guardadas."
    }
}
finally {
    Pop-Location
    Remove-Item -Recurse -Force $temporal
}