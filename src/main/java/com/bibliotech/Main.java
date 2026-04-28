package com.bibliotech;

import com.bibliotech.exception.BibliotecaException;
import com.bibliotech.model.*;
import com.bibliotech.repository.*;
import com.bibliotech.service.*;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    private static final Repository<Recurso, String> recursoRepo = new RecursoRepositoryMemoria();
    private static final Repository<Socio, String> socioRepo = new SocioRepositoryMemoria();
    private static final Repository<Prestamo, String> prestamoRepo = new PrestamoRepositoryMemoria();
    private static final Repository<Sancion, String> sancionRepo = new SancionRepositoryMemoria();

    private static final RecursoService recursoService = new RecursoService(recursoRepo);
    private static final SocioService socioService = new SocioService(socioRepo);
    private static final SancionService sancionService = new SancionService(sancionRepo);
    private static final PrestamoService prestamoService = new PrestamoService(
            prestamoRepo, socioService, recursoService, sancionService);

    public static void main(String[] args) {
        cargarDatosDePrueba();

        System.out.println("=================================");
        System.out.println("   BiblioTech - Sistema de Biblioteca");
        System.out.println("=================================");

        boolean activo = true;
        while (activo) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Seleccione una opcion: ");
            switch (opcion) {
                case 1 -> menuRecursos();
                case 2 -> menuSocios();
                case 3 -> menuPrestamos();
                case 4 -> menuSanciones();
                case 0 -> {
                    System.out.println("Hasta luego.");
                    activo = false;
                }
                default -> System.out.println("Opcion invalida. Intente nuevamente.");
            }
        }
    }

    // ---- DATOS DE PRUEBA ----------------------------------------

    private static void cargarDatosDePrueba() {
        // Libros
        recursoService.registrar(new Libro("ISBN-001", "El Senor de los Anillos",
                "J.R.R. Tolkien", 1954, Categoria.NOVELA, 1200));
        recursoService.registrar(new Libro("ISBN-002", "Introduccion a los Algoritmos",
                "Thomas Cormen", 2009, Categoria.TECNOLOGIA, 1292));
        recursoService.registrar(new Libro("ISBN-003", "Sapiens",
                "Yuval Noah Harari", 2011, Categoria.HISTORIA, 443));

        // Ebooks
        recursoService.registrar(new Ebook("ISBN-004", "Clean Code",
                "Robert C. Martin", 2008, Categoria.TECNOLOGIA, "PDF", 4.5));
        recursoService.registrar(new Ebook("ISBN-005", "El Quijote",
                "Miguel de Cervantes", 1605, Categoria.NOVELA, "EPUB", 1.2));

        // Socios
        try {
            socioService.registrarSocio(new Socio("12345678", "Ana", "garcia@uni.edu.ar",
                    Socio.TipoSocio.ESTUDIANTE));
            socioService.registrarSocio(new Socio("87654321", "Carlos",
                    "carlos@uni.edu.ar", Socio.TipoSocio.DOCENTE));
            socioService.registrarSocio(new Socio("11111111", "Maria",
                    "maria@uni.edu.ar", Socio.TipoSocio.ESTUDIANTE));
        } catch (BibliotecaException e) {
            System.out.println("Error cargando datos: " + e.getMessage());
        }

        System.out.println("Datos de prueba cargados correctamente.");
        System.out.println("-----------------------------");
    }

    // ---- MENUS --------------------------------------------------

    private static void mostrarMenuPrincipal() {
        System.out.println("\n--- Menu Principal ---------------");
        System.out.println("1. Recursos (Libros / Ebooks)");
        System.out.println("2. Socios");
        System.out.println("3. Prestamos");
        System.out.println("4. Sanciones");
        System.out.println("0. Salir");
        System.out.println("---------------------------------");
    }

    private static void menuRecursos() {
        System.out.println("\n--- Recursos --------------------");
        System.out.println("1. Registrar libro");
        System.out.println("2. Registrar ebook");
        System.out.println("3. Buscar por titulo");
        System.out.println("4. Buscar por autor");
        System.out.println("5. Buscar por categoria");
        System.out.println("6. Listar todos");
        System.out.println("0. Volver");

        int opcion = leerEntero("Seleccione: ");
        switch (opcion) {
            case 1 -> registrarLibro();
            case 2 -> registrarEbook();
            case 3 -> buscarPorTitulo();
            case 4 -> buscarPorAutor();
            case 5 -> buscarPorCategoria();
            case 6 -> listarRecursos();
            case 0 -> {}
            default -> System.out.println("Opcion invalida.");
        }
    }

    private static void menuSocios() {
        System.out.println("\n--- Socios ----------------------");
        System.out.println("1. Registrar socio");
        System.out.println("2. Buscar por DNI");
        System.out.println("3. Listar todos");
        System.out.println("0. Volver");

        int opcion = leerEntero("Seleccione: ");
        switch (opcion) {
            case 1 -> registrarSocio();
            case 2 -> buscarSocio();
            case 3 -> listarSocios();
            case 0 -> {}
            default -> System.out.println("Opcion invalida.");
        }
    }

    private static void menuPrestamos() {
        System.out.println("\n--- Prestamos -------------------");
        System.out.println("1. Nuevo prestamo");
        System.out.println("2. Registrar devolucion");
        System.out.println("3. Ver historial");
        System.out.println("0. Volver");

        int opcion = leerEntero("Seleccione: ");
        switch (opcion) {
            case 1 -> realizarPrestamo();
            case 2 -> registrarDevolucion();
            case 3 -> verHistorial();
            case 0 -> {}
            default -> System.out.println("Opcion invalida.");
        }
    }

    // ---- RECURSOS -----------------------------------------------

    private static void registrarLibro() {
        System.out.println("\n-- Registrar Libro --------------");
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Titulo: ");
        String titulo = scanner.nextLine();
        System.out.print("Autor: ");
        String autor = scanner.nextLine();
        int anio = leerEntero("Anio: ");
        int paginas = leerEntero("Paginas: ");
        Categoria categoria = leerCategoria();
        if (categoria == null) return;

        recursoService.registrar(new Libro(isbn, titulo, autor, anio, categoria, paginas));
        System.out.println("Libro registrado correctamente.");
    }

    private static void registrarEbook() {
        System.out.println("\n-- Registrar Ebook --------------");
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine();
        System.out.print("Titulo: ");
        String titulo = scanner.nextLine();
        System.out.print("Autor: ");
        String autor = scanner.nextLine();
        int anio = leerEntero("Anio: ");
        System.out.print("Formato (PDF, EPUB, MOBI): ");
        String formato = scanner.nextLine();
        double tamanio = leerDouble("Tamanio (MB): ");
        Categoria categoria = leerCategoria();
        if (categoria == null) return;

        recursoService.registrar(new Ebook(isbn, titulo, autor, anio, categoria, formato, tamanio));
        System.out.println("Ebook registrado correctamente.");
    }

    private static void buscarPorTitulo() {
        System.out.print("Titulo a buscar: ");
        String titulo = scanner.nextLine();
        mostrarRecursos(recursoService.buscarPorTitulo(titulo));
    }

    private static void buscarPorAutor() {
        System.out.print("Autor a buscar: ");
        String autor = scanner.nextLine();
        mostrarRecursos(recursoService.buscarPorAutor(autor));
    }

    private static void buscarPorCategoria() {
        Categoria categoria = leerCategoria();
        if (categoria == null) return;
        mostrarRecursos(recursoService.buscarPorCategoria(categoria));
    }

    private static void listarRecursos() {
        mostrarRecursos(recursoService.listarTodos());
    }

    private static void mostrarRecursos(List<Recurso> recursos) {
        if (recursos.isEmpty()) {
            System.out.println("No se encontraron recursos.");
            return;
        }
        System.out.println("\n-- Resultados -------------------");
        for (Recurso r : recursos) {
            if (r instanceof Libro l) {
                System.out.printf("  [LIBRO]  [%s] %s - %s (%d) | %s | %d paginas%n",
                        l.isbn(), l.titulo(), l.autor(), l.anio(), l.categoria(), l.numeroPaginas());
            } else if (r instanceof Ebook e) {
                System.out.printf("  [EBOOK]  [%s] %s - %s (%d) | %s | %s %.1f MB%n",
                        e.isbn(), e.titulo(), e.autor(), e.anio(), e.categoria(), e.formatoArchivo(), e.tamanioMB());
            }
        }
    }

    // ---- SOCIOS -------------------------------------------------

    private static void registrarSocio() {
        System.out.println("\n-- Registrar Socio --------------");
        System.out.print("DNI: ");
        String dni = scanner.nextLine();
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.println("Tipo: 1. ESTUDIANTE   2. DOCENTE");
        int tipo = leerEntero("Seleccione: ");

        Socio.TipoSocio tipoSocio = (tipo == 2)
                ? Socio.TipoSocio.DOCENTE
                : Socio.TipoSocio.ESTUDIANTE;

        try {
            socioService.registrarSocio(new Socio(dni, nombre, email, tipoSocio));
            System.out.println("Socio registrado correctamente.");
        } catch (BibliotecaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void buscarSocio() {
        System.out.print("DNI: ");
        String dni = scanner.nextLine();
        Optional<Socio> resultado = socioService.buscarPorDni(dni);
        resultado.ifPresentOrElse(
                s -> System.out.printf("  [%s] %s - %s | %s%n",
                        s.dni(), s.nombre(), s.email(), s.tipo()),
                () -> System.out.println("Socio no encontrado.")
        );
    }

    private static void listarSocios() {
        List<Socio> socios = socioService.listarTodos();
        if (socios.isEmpty()) {
            System.out.println("No hay socios registrados.");
            return;
        }
        System.out.println("\n-- Socios -----------------------");
        socios.forEach(s -> System.out.printf(
                "  [%s] %s - %s | %s%n",
                s.dni(), s.nombre(), s.email(), s.tipo()
        ));
    }

    // ---- PRESTAMOS ----------------------------------------------

    private static void realizarPrestamo() {
        System.out.println("\n-- Nuevo Prestamo ---------------");
        System.out.print("DNI del socio: ");
        String dni = scanner.nextLine();
        System.out.print("ISBN del recurso: ");
        String isbn = scanner.nextLine();

        try {
            prestamoService.registrarPrestamo(dni, isbn);
            System.out.println("Prestamo registrado. Vence en " + Prestamo.DIAS_PRESTAMO + " dias.");
        } catch (BibliotecaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void registrarDevolucion() {
        System.out.println("\n-- Registrar Devolucion ---------");
        System.out.print("ID del prestamo: ");
        String id = scanner.nextLine();

        try {
            long diasRetraso = prestamoService.registrarDevolucion(id);
            if (diasRetraso > 0) {
                System.out.println("Devuelto con " + diasRetraso + " dia(s) de retraso.");
            } else {
                System.out.println("Recurso devuelto en termino.");
            }
        } catch (BibliotecaException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void verHistorial() {
        List<Prestamo> historial = prestamoService.obtenerHistorial();
        if (historial.isEmpty()) {
            System.out.println("No hay historial de prestamos.");
            return;
        }
        System.out.println("\n-- Historial de Prestamos -------");
        historial.forEach(p -> System.out.printf(
                "  [%s] %s -> %s | Desde: %s Hasta: %s | %s%n",
                p.id(),
                p.socio().nombre(),
                p.recurso().titulo(),
                p.fechaInicio(),
                p.fechaLimite(),
                p.estaDevuelto() ? "Devuelto" : (p.estaVencido() ? "Vencido" : "Activo")
        ));
    }
// ---- SANCIONES ----------------------------------------------

    private static void menuSanciones() {
        System.out.println("\n--- Sanciones -------------------");
        System.out.println("1. Ver todas las sanciones");
        System.out.println("2. Ver sanciones activas");
        System.out.println("0. Volver");

        int opcion = leerEntero("Seleccione: ");
        switch (opcion) {
            case 1 -> listarSanciones(sancionService.listarTodas());
            case 2 -> listarSanciones(sancionService.listarActivas());
            case 0 -> {}
            default -> System.out.println("Opcion invalida.");
        }
    }

    private static void listarSanciones(List<Sancion> sanciones) {
        if (sanciones.isEmpty()) {
            System.out.println("No hay sanciones registradas.");
            return;
        }
        System.out.println("\n-- Sanciones --------------------");
        sanciones.forEach(s -> System.out.printf(
                "  [%s] %s | Retraso: %d dia(s) | Desde: %s Hasta: %s | %s%n",
                s.id(),
                s.socio().nombre(),
                s.diasRetraso(),
                s.fechaInicio(),
                s.fechaFin(),
                s.estaActiva() ? "ACTIVA" : "VENCIDA"
        ));
    }
    // ---- HELPERS ------------------------------------------------

    private static Categoria leerCategoria() {
        System.out.println("Categorias disponibles: " + java.util.Arrays.toString(Categoria.values()));
        System.out.print("Categoria: ");
        try {
            return Categoria.valueOf(scanner.nextLine().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println("Categoria invalida.");
            return null;
        }
    }

    private static int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero valido.");
            }
        }
    }

    private static double leerDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero valido.");
            }
        }
    }
}