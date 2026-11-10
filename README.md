# A2.2 Mi Kardex

Aplicación escolar Android desarrollada con Kotlin + Jetpack Compose + Room + KSP.

## Requisitos cubiertos

- Datos del alumno: nombre y número de control guardados permanentemente con `SharedPreferences`.
- Base de datos local Room/SQLite.
- Una sola tabla: `materias`.
- Campos: nombre, semestre, calificación Normal, Repetición y Especial.
- Alta de materias.
- Consulta de materias registradas.
- Promedio general.
- Promedio por semestre.
- Eliminación de materias.
- Arquitectura sencilla: UI → ViewModel → Repository → DAO → Room.
- Room se procesa mediante KSP.

## Regla de calificación efectiva

Para obtener los promedios, la aplicación usa la calificación disponible con esta prioridad:

1. Especial, si es mayor que 0.
2. Repetición, si es mayor que 0.
3. Normal, en los demás casos.

Así, una materia no se cuenta tres veces por tener tres campos de calificación.

## Abrir el proyecto

1. Descomprime el ZIP.
2. Abre la carpeta `A22MIKARDEX` desde Android Studio.
3. Espera a que Gradle sincronice las dependencias.
4. Ejecuta la configuración `app` en un emulador o dispositivo Android.

El entorno de esta entrega no tiene acceso a Internet para descargar Gradle/dependencias, por lo que la compilación final debe realizarse en Android Studio con conexión a Internet o con las dependencias previamente cacheadas.
