# Vitalog
Proyecto del curso asistido con IA

# VitaLog

VitaLog es una aplicación web para crear hábitos, registrar su seguimiento y consultar su progreso. El proyecto utiliza React en el frontend, Spring Boot y MongoDB en el backend.

## Estado actual

### Implementado

- **RU-01 — Crear una cuenta e iniciar sesión**
  - Registro con correo electrónico y contraseña.
  - Inicio y cierre de sesión mediante sesión HTTP protegida con CSRF.
  - Validación de contraseña con mínimo de 8 caracteres, mayúscula, minúscula, número y carácter especial.
  - Contraseña almacenada mediante BCrypt.
- **RU-02 — Crear un hábito**
  - Nombre obligatorio de hasta 100 caracteres.
  - Duración inicial de 66 días, con mínimo de 1 día.
  - Frecuencia diaria o semanal.
  - Selección de uno o más días para frecuencia semanal.
  - Fecha de inicio asignada al guardar.
  - Estado inicial «En construcción».
  - Asociación al usuario autenticado, sin depender de un `userId` enviado por el cliente.
  - Formulario oculto hasta pulsar el botón compacto **Crear hábito**, alineado a la derecha.
  - El formulario desaparece después de una creación exitosa y permanece visible si ocurre un error.

### Pendiente o por ampliar

- Registro de cumplimiento diario.
- Visualización de progreso y calendario mensual.
- Categorías de hábitos consolidados.
- Recordatorios, sugerencias, archivado y eliminación.
- Inicio de sesión mediante Google, una vez configuradas las credenciales externas.

## Tecnologías

- **Frontend:** React 18 y Create React App.
- **Backend:** Java 21 y Spring Boot 4.1.2.
- **Seguridad:** Spring Security y protección CSRF.
- **Persistencia:** MongoDB mediante Spring Data MongoDB.
- **Pruebas:** JUnit 5, Mockito, AssertJ y React Testing Library/Jest.
- **Construcción:** npm y Maven.

## Estructura del proyecto

```text
Vitalog/
├── frontend/
│   ├── public/
│   └── src/
│       ├── pages/
│       ├── services/
│       ├── utils/
│       └── App.test.js
├── backend/
│   ├── pom.xml
│   └── src/
│       ├── main/java/com/vitalog/
│       └── test/java/com/vitalog/
├── Docs/
├── start-vitalog.bat
└── README.md
```

## Requisitos

- Windows.
- Java 21.
- Node.js y npm.
- Docker Desktop para MongoDB.
- Maven; el proyecto puede descargar localmente la versión usada por el script.

## Configuración

Copia la plantilla de entorno si existe y configura la URI de MongoDB:

```text
MONGODB_URI=mongodb://usuario:contraseña@localhost:27017/vitalog
```

El archivo local `.env` debe permanecer fuera del control de versiones. No escribas credenciales reales en `application.properties` ni en el código fuente.

MongoDB debe estar disponible en el contenedor `mongodb`. El script `start-vitalog.bat` intenta iniciar el contenedor si está detenido.

## Ejecutar el proyecto

Desde la carpeta `Vitalog`, ejecuta:

```powershell
.\start-vitalog.bat
```

El script:

1. Configura Java 21.
2. Instala Maven localmente si hace falta.
3. Instala las dependencias frontend si no existen.
4. Compila React y copia los archivos al backend.
5. Ejecuta las pruebas y empaqueta Spring Boot.
6. Busca un puerto disponible entre 8080 y 8090.
7. Inicia VitaLog y abre el navegador.

## Ejecutar manualmente

### Frontend

```powershell
cd Vitalog\frontend
npm ci
npm start
npm test -- --watchAll=false
npm run build
```

### Backend

```powershell
cd Vitalog\backend
$env:JAVA_HOME="C:\Program Files\Microsoft\jdk-21.0.13.21-hotspot"
& "C:\Users\airam\proyectos\packer-node-nginx\Vitalog\.tools\apache-maven-3.10.0\bin\mvn.cmd" test
& "C:\Users\airam\proyectos\packer-node-nginx\Vitalog\.tools\apache-maven-3.10.0\bin\mvn.cmd" clean package
```

## Uso de RU-02

1. Inicia sesión en VitaLog.
2. Pulsa el botón **Crear hábito**, ubicado a la derecha de la pantalla.
3. Completa el nombre y la duración.
4. Selecciona frecuencia diaria o semanal.
5. Para frecuencia semanal, marca al menos un día.
6. Pulsa **Crear hábito**.
7. Si la operación termina correctamente, el formulario se oculta y el hábito aparece en la lista.

## API de hábitos

- `GET /api/habits`: lista los hábitos del usuario autenticado.
- `POST /api/habits`: crea un hábito asociado al usuario autenticado.

Ambos endpoints requieren sesión autenticada. La creación también requiere una petición protegida por CSRF.

## Documentación

- `Docs/requerimientos_usuario_vitalog.md`: requerimientos funcionales originales.
- `Docs/documentacion-funcional-RU-02.html`: documentación funcional de creación de hábitos.
- `Docs/reporte-pruebas-unitarias.html`: reporte HTML de pruebas backend y frontend.
- `Docs/mejoras-codigo.html`: auditoría defensiva de seguridad y buenas prácticas.

## Rama Git actual

La rama local usada al actualizar esta documentación es `Visualizar_calendario`.

## Alcance actual

El proyecto contiene el registro e inicio de sesión, la creación y consulta de hábitos asociados a la cuenta autenticada, y el nuevo flujo bajo demanda para el formulario de hábitos. El registro de cumplimiento, la consolidación y el calendario mensual todavía no están implementados.
