# Decisiones de Diseño — BiblioTech

## 1. Arquitectura en Capas

El sistema se organizó en cuatro capas bien diferenciadas:

- **model**: contiene las entidades del dominio (Recurso, Libro, Ebook, Socio, Prestamo)
- **repository**: maneja el acceso a los datos (in-memory)
- **service**: contiene toda la lógica de negocio
- **Main**: punto de entrada y presentación (CLI)

Esta separación permite que cada capa tenga una única responsabilidad
y que los cambios en una no afecten a las demás. Por ejemplo, si en el
futuro se reemplaza el almacenamiento en memoria por una base de datos,
solo se modifica la capa repository sin tocar nada del resto.

---

## 2. Interfaz Recurso y Polimorfismo

Se definió `Recurso` como interfaz base para todos los tipos de material
de la biblioteca. Tanto `Libro` como `Ebook` la implementan.

Esta decisión permite que `RecursoService` y `Prestamo` trabajen con
cualquier tipo de recurso sin conocer su implementación concreta.
Si en el futuro se agrega un nuevo tipo (por ejemplo, `Revista`), solo
se crea el nuevo record que implemente `Recurso` sin modificar ninguna
clase existente.

Esto aplica directamente el **principio Open/Closed (OCP)**: el sistema
está abierto para extensión pero cerrado para modificación.

---

## 3. Records para Entidades Inmutables

Se usaron `record` de Java para `Libro`, `Ebook` y `Prestamo` porque
representan datos que no cambian una vez creados.

Un libro siempre tendrá el mismo ISBN, título y autor. Un préstamo
registra un hecho histórico: en qué fecha se realizó y entre quién
y qué recurso. Usar records garantiza inmutabilidad, elimina código
repetitivo (getters, equals, toString) y expresa claramente la
intención del diseño.

---

## 4. Optional en lugar de null

En los métodos de búsqueda de repositorios y servicios se usa
`Optional<T>` como tipo de retorno en lugar de retornar `null`.

Esto obliga a quien llama al método a considerar explícitamente el
caso en que no se encuentre el resultado, evitando NullPointerException.
También hace el código más legible: `Optional.empty()` comunica
claramente "no encontré nada", mientras que `null` es ambiguo.

---

## 5. Jerarquía de Excepciones

Todas las excepciones de negocio del sistema extienden de
`BibliotecaException`, que a su vez extiende `RuntimeException`.
Esta jerarquía permite capturar cualquier error del sistema con un
único `catch (BibliotecaException e)`, o capturar errores específicos
cuando se necesita un manejo diferenciado. Evita el uso de
`RuntimeException` genéricas que no comunican nada sobre el error.
```
RuntimeException
    └── BibliotecaException
            ├── DniDuplicadoException
            ├── EmailInvalidoException
            ├── LimitePrestamosExcedidoException
            ├── LibroNoDisponibleException
            ├── SocioSancionadoException
            ├── SocioNoEncontradoException
            └── PrestamoNoEncontradoException
```
---

## 6. Inyección de Dependencias por Constructor

Los servicios no crean sus propias dependencias. Las reciben por
constructor:

```java
public PrestamoService(Repository<Prestamo, String> prestamoRepository,
                       SocioService socioService,
                       RecursoService recursoService) { ... }
```

Esto aplica el **principio Dependency Inversion (DIP)**: los servicios
dependen de abstracciones (la interfaz `Repository`) y no de
implementaciones concretas. Si se cambia `RecursoRepositoryMemoria`
por una implementación que persiste en base de datos, el servicio
no necesita modificarse.

---

## 7. Socio como Record con TipoSocio Enum interno

Se decidió modelar `Socio` como un `record` con un enum interno
`TipoSocio` que encapsula el límite de préstamos de cada categoría.

```java
public enum TipoSocio {
    ESTUDIANTE(3),
    DOCENTE(5);
}
```

Esto centraliza la lógica de límites en el modelo: si el límite de
un tipo cambia, se modifica en un único lugar. Evita que esa lógica
esté dispersa en múltiples servicios o condiciones.

La gestión de cuántos préstamos activos tiene un socio vive en
`PrestamoService`, que es quien tiene acceso al repositorio de
préstamos. Esta decisión mantiene `Socio` inmutable y delega la
lógica de conteo a quien corresponde.

---

## 8. RecursoService en lugar de LibroService

Inicialmente se implementó un `LibroService` que trabajaba con la
clase concreta `Libro`. Esto se refactorizó a `RecursoService` que
trabaja con la interfaz `Recurso`.

El motivo fue aplicar correctamente el **principio Dependency
Inversion**: el servicio no debe conocer si está manejando un `Libro`
o un `Ebook`. Al trabajar con la abstracción `Recurso`, el sistema
puede registrar y buscar cualquier tipo de recurso sin modificar
el servicio.

---

## 9. Método actualizar en Repository

Se agregó el método `actualizar(T entidad, ID id)` a la interfaz
`Repository` para manejar el caso de la devolución de préstamos.

Como `Prestamo` es un record inmutable, no se puede modificar su
`fechaDevolucion` directamente. En cambio, se crea un nuevo objeto
`Prestamo` con la fecha de devolución cargada (mediante el factory
method `conDevolucion()`) y se reemplaza el anterior en el repositorio.
Sin el método `actualizar`, el sistema acumulaba préstamos duplicados.

## 10. Sistema de Sanciones (Bonus)

Se implementó un sistema de sanciones para socios que devuelven
recursos con retraso. La duración del bloqueo es el doble de los
días de retraso, como penalización proporcional y disuasiva.

La lógica se encapsuló en un `SancionService` independiente que
recibe su repositorio por constructor, manteniendo la consistencia
arquitectónica del resto del sistema.

`PrestamoService` delega en `SancionService` tanto la verificación
de sanciones activas (antes de registrar un préstamo) como la
creación de nuevas sanciones (al registrar una devolución tardía).
Esto respeta el **principio Single Responsibility**: cada servicio
tiene una única razón para cambiar.

La excepción `SocioSancionadoException` se integra a la jerarquía
existente extendiendo `BibliotecaException`, manteniendo la
consistencia del manejo de errores en todo el sistema.