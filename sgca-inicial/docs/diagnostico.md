# Diagnóstico del código inicial del SGCA

## Alcance y referencia

Analizamos el archivo sgca-inicial/SGCA.java de 149 líneas,
antes de aplicar las mejoras progresivas.

El prototipo permite registrar solicitudes, emitir certificados,
consultar una solicitud y listar las solicitudes.
Trabaja con las estructuras Alumno, Solicitud y Certificado,
utilizando datos ficticios en memoria.

Las ubicaciones indicadas corresponden a esta versión inicial.
Pueden cambiar después del refactoring.

## Calidad externa e interna

Desde la mirada del usuario, verificamos la emisión de certificados
para Ana, el bloqueo por deuda para Luis y el bloqueo del certificado
regular para Eva. También comprobamos consulta, listado y rechazo
de una segunda emisión. Guardamos capturas de estas ejecuciones.

Aunque estas operaciones funcionan en los casos probados,
la organización interna dificulta entender y modificar el código.
Encontramos responsabilidades mezcladas, duplicación, nombres
poco claros y datos que pueden modificarse directamente.

## D1 Método largo con varias responsabilidades

- Ubicación: método hacer, líneas 51 a 122.
- Evidencia: las condiciones op == 1, op == 2, op == 3 y op == 4
  seleccionan registro, emisión, consulta y listado.
- Impacto: para comprender una operación debemos recorrer un método
  que también contiene las demás.
- Riesgo de mantenimiento: modificar una operación puede afectar
  accidentalmente otras partes del mismo método.
- Prioridad: alta.
- Mejora propuesta: aplicar Extract Method para separar operaciones.
  Después evaluar la reubicación de responsabilidades según SRP.

## D2 Nombres poco descriptivos

- Ubicación: líneas 7 a 10, 51 a 54 y 57, entre otras.
- Evidencia: a, s, c, n, hacer, x, y y z.
- Impacto: los nombres no explican qué representan las variables
  ni qué operación realiza el método.
- Riesgo de mantenimiento: confundir entidades o interpretar mal
  el propósito de una variable al modificar el código.
- Prioridad: media.
- Mejora propuesta: utilizar nombres del dominio, como alumnos,
  solicitudes, certificados, siguienteId y registrarSolicitud.
- Relación con la materia: nombres claros de Clean Code.

## D3 Código duplicado

- Ubicación: líneas 80 a 86 y 101 a 107.
  También líneas 114 y 119.
- Evidencia: ambos tipos de certificado repiten la creación del objeto,
  la asignación de fecha y texto, el guardado y el cambio de estado.
  Consulta y listado repiten el formato de una solicitud.
- Impacto: una misma modificación debe hacerse en varios lugares.
- Riesgo de mantenimiento: corregir un bloque y dejar otro diferente.
- Prioridad: alta.
- Mejora propuesta: extraer y reutilizar las tareas comunes.
- Relación con la materia: Extract Method y principio DRY.

## D4 Lista de parámetros confusa

- Ubicación: firma de hacer en la línea 51 y llamadas
  de las líneas 32, 35, 38 y 40.
- Evidencia: hacer recibe seis parámetros. Las llamadas completan
  argumentos que no necesitan con 0, texto vacío y false.
  nombre y deuda se sobrescriben en las líneas 73 y 74.
- Impacto: cuesta reconocer qué información necesita cada operación.
- Riesgo de mantenimiento: pasar valores en posiciones equivocadas
  o conservar parámetros que no cumplen una función.
- Prioridad: alta.
- Mejora propuesta: separar las operaciones y simplificar sus firmas.
  Evaluar Introduce Parameter Object solo si hay datos relacionados
  que deban viajar juntos.

## D5 Estado global mutable

- Ubicación: líneas 7 a 10.
- Evidencia: las listas a, s y c y el contador n son static
  y pueden modificarse desde distintos métodos.
- Impacto: las operaciones comparten datos durante toda la ejecución.
- Riesgo de mantenimiento: efectos inesperados entre operaciones
  y dificultad para aislar escenarios de prueba.
- Prioridad: alta.
- Mejora propuesta: encapsular el almacenamiento y controlar
  qué componentes pueden modificarlo.
- Relación con la materia: encapsulación, cohesión y acoplamiento.

## D6 Encapsulación insuficiente

- Ubicación: campos de Alumno en las líneas 126 a 131,
  Solicitud en las líneas 139 a 143 y Certificado
  en las líneas 146 a 148.
- Evidencia: los campos no tienen private y se modifican directamente,
  por ejemplo y.estado = "EMITIDA" en las líneas 85 y 106.
- Impacto: las clases del mismo paquete pueden cambiar los datos
  sin pasar por métodos que protejan las reglas.
- Riesgo de mantenimiento: dejar objetos con datos o estados
  inconsistentes.
- Prioridad: media.
- Mejora propuesta: encapsular campos y ofrecer métodos adecuados,
  por ejemplo marcarEmitida.
- Aclaración: estos campos tienen acceso de paquete; no son públicos.

## D7 Arreglos paralelos

- Ubicación: líneas 130 y 131 y recorrido de las líneas 92 a 98.
- Evidencia: materias[i] y notas[i] representan información relacionada
  mediante la coincidencia de sus posiciones.
- Impacto: debemos mantener manualmente el mismo orden y tamaño
  en ambos arreglos.
- Riesgo de mantenimiento: asociar una nota con otra materia
  o provocar un acceso fuera del arreglo.
- Prioridad: alta.
- Mejora propuesta: representar cada materia y su calificación
  mediante un objeto Nota y utilizar una colección de esos objetos.

## D8 Tipos y estados representados mediante literales

- Ubicación: tipos en las líneas 56, 77 y 88;
  estados en las líneas 62, 69, 85 y 106.
- Evidencia: 1 y 2 representan tipos de certificado;
  "REGISTRADA" y "EMITIDA" representan estados.
- Impacto: el significado de los números depende de conocer
  convenciones y los estados se escriben directamente como texto.
- Riesgo de mantenimiento: introducir un número incorrecto
  o escribir un estado distinto sin que el compilador lo detecte.
- Prioridad: media.
- Mejora propuesta: utilizar enums para representar tipos y estados.
  Conservar la correspondencia con las opciones del menú.

## Orden propuesto de mejora

1. Preparar pruebas de referencia antes de modificar Java.
2. Mejorar los nombres.
3. Extraer métodos para separar operaciones y tareas comunes.
4. Simplificar parámetros.
5. Reubicar responsabilidades entre componentes.
6. Mejorar encapsulación y representación de los datos.
7. Revisar dependencias y aplicar abstracciones donde se justifiquen.

Este orden puede ajustarse según las dependencias entre cambios.
Después de cada mejora ejecutaremos las pruebas de regresión.

## Criterio de verificación

Compararemos ambas versiones utilizando las mismas entradas.
Revisaremos mensajes, contenido de certificados y estados.

Mantendremos las reglas y el orden de validaciones de la base.
Por ejemplo, si el alumno y el tipo son inválidos al mismo tiempo,
la versión inicial informa primero "Alumno inexistente".

Las capturas iniciales serán evidencia visual complementaria.
Los resultados finales se registrarán después de ejecutar las pruebas
sobre la versión refactorizada.

## Uso de IA y revisión

El diagnóstico se elaboró con apoyo de ChatGPT.
Los integrantes deben revisarlo y comprender sus observaciones.