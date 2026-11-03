# SGCA - prototipo inicial de alcance reducido

Version academica para Metodologia de Sistemas II, basada en el caso SGCA de GDS. Codigo preparado con apoyo de ChatGPT para estudiar, diagnosticar y refactorizar. No constituye la implementacion completa del proyecto de GDS.

## Ejecucion
Java 17 o posterior, con soporte para ejecutar codigo fuente. Abrir una terminal en esta carpeta:

```bash
java SGCA.java
```

Con un JDK tambien puede utilizarse `javac SGCA.java` y luego `java SGCA`.

## Alcance acordado
Tres estructuras: Alumno, Solicitud y Certificado. Cuatro operaciones: registrar solicitud, emitir certificado, consultar por numero y listar solicitudes.

Datos ficticios en memoria: al cerrar se pierden. Sin login, correo, integraciones externas, simulacion de caidas ni generacion de PDF. El certificado se muestra como texto en consola y puede volver a consultarse. Todas las materias de los datos de prueba pertenecen a la carrera; no se modelan extracurriculares.

## Reglas
- Registrar: alumno existente y tipo 1 (regular) o 2 (analitico parcial).
- Emitir: solicitud existente, sin emision previa y alumno sin deuda.
- Regular: requiere condicion de alumno regular.
- Analitico: muestra materias, notas y promedio aritmetico simple. Escala 1 a 10. Sin notas se muestra "Promedio: sin notas".
- Emision rechazada: no se crea certificado y la solicitud sigue REGISTRADA.
- Emision aceptada: certificado guardado en memoria y solicitud EMITIDA.
- Solicitud registra fecha de registro; certificado registra fecha de emision.

## Datos de ejemplo
1. Ana: sin deuda, regular, notas 8 y 6 (promedio 7.0).
2. Luis: con deuda, regular.
3. Eva: sin deuda, no regular.

## Estado del codigo
Es la version dificil de mantener, previa al refactoring. Mantiene un metodo con varias responsabilidades, nombres poco claros, parametros innecesarios, duplicacion, estado global mutable, campos sin encapsulacion, codigos numericos y estados literales, arreglos paralelos y mezcla de reglas con salida por consola. Estos problemas deben diagnosticarse con ubicacion e impacto en el informe; no se han corregido aun.

## Primeras evidencias
Guardar capturas reales del menu, del registro/emision/consulta de un analitico de Ana (promedio 7.0), del listado y del bloqueo por deuda de Luis. Registrar tambien el bloqueo del regular de Eva y el rechazo de doble emision. Conservar los mismos casos para comparar despues del refactoring.

verificacion.txt resume diez ejecuciones realizadas al preparar este paquete. No reemplaza las evidencias ni la comprension del equipo. Guardar los intercambios relevantes con IA para el anexo.

## Primer commit
Desde esta carpeta, si aun no existe repositorio:

```bash
git init
```

Despues de ejecutar y comprobar el prototipo:

```bash
git add SGCA.java README.md verificacion.txt .gitignore
git commit -m "feat: agregar prototipo inicial del SGCA con alcance reducido"
git tag estado-inicial
```

Si ya existe el tag estado-inicial de la version anterior, no repetir ni forzar el comando: acordar primero como conservar y distinguir ese hito. Capturar el resultado de `git log --oneline` y `git tag`.
