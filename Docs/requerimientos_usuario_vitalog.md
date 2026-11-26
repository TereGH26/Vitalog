# Requerimientos de usuario — VitaLog

## Propósito

Este documento describe las funcionalidades que VitaLog ofrecerá a sus usuarios. Se utilizará como punto de partida para definir y desarrollar el producto; el detalle de cada funcionalidad podrá ampliarse durante el diseño y la implementación.

## Descripción del producto

VitaLog es una aplicación para crear hábitos, registrar su cumplimiento y visualizar el progreso. La aplicación distingue entre hábitos «en construcción» y «consolidados» para ayudar a los usuarios a desarrollar rutinas y reconocer sus avances.

## Requerimientos funcionales

### RU-01 — Crear una cuenta e iniciar sesión

El usuario podrá crear una cuenta e iniciar sesión con correo electrónico y contraseña o mediante una cuenta de Google. Su cuenta le permitirá guardar y consultar sus hábitos y su progreso.

### RU-02 — Crear un hábito

El usuario podrá crear un hábito indicando su nombre, frecuencia y, si corresponde, los días de la semana en que desea realizarlo. Los hábitos nuevos se mostrarán como «en construcción».

### RU-03 — Consultar hábitos

El usuario podrá consultar sus hábitos organizados en las categorías «en construcción» y «consolidados», e identificar cuáles están programados para el día actual.

### RU-04 — Registrar el cumplimiento de un hábito

El usuario podrá marcar un hábito como completado en una fecha y retirar esa marca si la registró por error. El estado de cumplimiento se reflejará en el progreso del hábito.

### RU-05 — Consolidar un hábito

El usuario podrá cambiar un hábito de «en construcción» a «consolidado». La aplicación mostrará el hábito en la categoría correspondiente y conservará su progreso.

### RU-06 — Consultar el progreso

El usuario podrá consultar un calendario mensual que muestre los días en que completó hábitos. También podrá filtrar la vista para consultar el progreso de un hábito específico.

### RU-07 — Configurar recordatorios

El usuario podrá activar o desactivar recordatorios para un hábito y seleccionar la hora en que desea recibirlos en los días programados. El recordatorio identificará el hábito correspondiente.

### RU-08 — Explorar hábitos sugeridos

El usuario podrá consultar una biblioteca de hábitos sugeridos y seleccionar una sugerencia para crear un hábito a partir de ella.

### RU-09 — Archivar un hábito

El usuario podrá archivar un hábito que ya no desea mantener entre sus hábitos activos. Los hábitos archivados dejarán de aparecer en las listas activas.

### RU-10 — Eliminar un hábito

El usuario podrá eliminar un hábito después de confirmar la acción. Al eliminarlo, el hábito dejará de estar disponible en sus listas y en su historial.

### RU-11 — Completar un hábito desde un recordatorio

Desde un recordatorio, el usuario podrá marcar como completado el hábito asociado. El cumplimiento aparecerá en el progreso del hábito.
