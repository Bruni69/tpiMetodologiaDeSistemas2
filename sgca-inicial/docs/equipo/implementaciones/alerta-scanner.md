# Implementacion: cierre del Scanner

- Tarea: resolver la alerta de recurso no cerrado en `SGCA.java`.
- Rol: desarrollador.
- Estado: completado.
- Version: SGCA prototipo inicial, ajuste del 2026-11-03.

## Cambio

Se envolvio el `Scanner` que lee la entrada de consola en un bloque `try-with-resources`. Se cierra automaticamente al salir del menu, incluso al usar la opcion 0, sin alterar las opciones disponibles.

## Verificacion

- Diagnosticos de VS Code para `SGCA.java`: sin errores.
- Java 17: `java SGCA.java` inicio correctamente y finalizo con la entrada `0` (codigo de salida 0).

## Pendientes

Ninguno para esta alerta.
