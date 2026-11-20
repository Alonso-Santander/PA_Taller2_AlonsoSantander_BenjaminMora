package vista;

import dominio.Funcion;
import servicios.SistemaTeatro;
import servicios.SistemaTeatroImpl;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Clase principal con los menús de interacción por consola.
 */
public class Main {

    /**
     * Punto de entrada del programa.
     *
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        SistemaTeatro sistema = new SistemaTeatroImpl();
        Scanner sc = new Scanner(System.in);

        try {
            sistema.cargarFunciones("Funciones.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Error: No se encontró el archivo Funciones.txt.");
            return;
        }

        boolean ejecutando = true;
        while (ejecutando) {
            mostrarMenuInicial();
            int opcion = leerEntero(sc);

            switch (opcion) {
                case 1:
                    procesarRegistro(sistema, sc);
                    break;
                case 2:
                    if (procesarInicioSesion(sistema, sc)) {
                        ejecutarMenuPrincipal(sistema, sc);
                    }
                    break;
                case 3:
                    try {
                        sistema.generarArchivoEstadisticas("Estadisticas.txt");
                        System.out.println("Estadísticas generadas en Estadisticas.txt.");
                    } catch (IOException e) {
                        System.out.println("Error al generar el archivo de estadísticas.");
                    }
                    System.out.println("¡Gracias por visitar el Teatro de Antofagasta! Hasta pronto.");
                    ejecutando = false;
                    break;
                default:
                    System.out.println("Opción inválida. Intente nuevamente.");
            }
        }
        sc.close();
    }

    private static void mostrarMenuInicial() {
        System.out.println("====================");
        System.out.println("=   MENU INICIAL   =");
        System.out.println("====================");
        System.out.println("[1] Registrarse");
        System.out.println("[2] Inicia Sesión");
        System.out.println("[3] Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void procesarRegistro(SistemaTeatro sistema, Scanner sc) {
        System.out.print("Ingrese su nombre: ");
        String nombre = sc.nextLine();
        System.out.print("Ingrese su RUT: ");
        String rut = sc.nextLine();
        System.out.print("Ingrese su contraseña: ");
        String contrasena = sc.nextLine();

        if (sistema.registrarUsuario(nombre, rut, contrasena)) {
            System.out.println("Usuario registrado correctamente.");
        } else {
            System.out.println("Error: Datos inválidos o el RUT ya se encuentra registrado.");
        }
    }

    private static boolean procesarInicioSesion(SistemaTeatro sistema, Scanner sc) {
        System.out.print("Ingrese su RUT: ");
        String rut = sc.nextLine();
        System.out.print("Ingrese su contraseña: ");
        String contrasena = sc.nextLine();

        if (sistema.iniciarSesion(rut, contrasena) != null) {
            System.out.println("Inicio de sesión exitoso. Bienvenido/a " + sistema.getUsuarioActual().getNombre() + ".");
            return true;
        } else {
            System.out.println("RUT o contraseña incorrectos.");
            return false;
        }
    }

    private static void ejecutarMenuPrincipal(SistemaTeatro sistema, Scanner sc) {
        boolean enSesion = true;
        while (enSesion) {
            System.out.println("========================");
            System.out.println("=    MENU PRINCIPAL    =");
            System.out.println("=  TEATRO ANTOFAGASTA  =");
            System.out.println("========================");
            System.out.println("[1] Comprar Entrada");
            System.out.println("[2] Ver Funciones");
            System.out.println("[3] Buscar una Función");
            System.out.println("[4] Cerrar Sesión");
            System.out.print("Seleccione una opción: ");

            int opcion = leerEntero(sc);
            switch (opcion) {
                case 1:
                    procesarCompra(sistema, sc);
                    break;
                case 2:
                    mostrarFuncionesDisponibles(sistema);
                    break;
                case 3:
                    procesarBusqueda(sistema, sc);
                    break;
                case 4:
                    sistema.cerrarSesion();
                    System.out.println("Sesión cerrada.");
                    enSesion = false;
                    break;
                default:
                    System.out.println("Opción inválida.");
            }
        }
    }

    private static void procesarCompra(SistemaTeatro sistema, Scanner sc) {
        if (!sistema.getUsuarioActual().isMiembroClub()) {
            System.out.print("¿Desea unirse al ClubTeatroAntofagasta para obtener un 15% de descuento? (si/no): ");
            String resp = sc.nextLine().trim();
            if (resp.equalsIgnoreCase("si")) {
                System.out.print("Confirme su RUT: ");
                String rut = sc.nextLine();
                System.out.print("Confirme su contraseña: ");
                String pass = sc.nextLine();
                if (sistema.unirseAlClub(rut, pass)) {
                    System.out.println("¡Felicidades! Ahora es miembro del ClubTeatroAntofagasta.");
                } else {
                    System.out.println("Datos incorrectos. Continuará la compra sin descuento del club.");
                }
            }
        }

        List<Funcion> disponibles = sistema.obtenerFuncionesDisponibles();
        if (disponibles.isEmpty()) {
            System.out.println("No hay funciones con asientos disponibles.");
            return;
        }

        mostrarFuncionesDisponibles(sistema);
        System.out.print("Ingrese el código de la función que desea: ");
        int codigo = leerEntero(sc);
        Funcion seleccionada = sistema.buscarPorCodigo(codigo);

        if (seleccionada == null || seleccionada.getSala().getAsientosDisponibles() == 0) {
            System.out.println("Función no válida o sin asientos disponibles.");
            return;
        }

        System.out.println("Función: " + seleccionada.getNombre());
        System.out.println(seleccionada.getSala().obtenerMapaAsientos());

        System.out.print("Ingrese la cantidad de entradas que desea comprar: ");
        int cantidad = leerEntero(sc);

        if (cantidad <= 0 || cantidad > seleccionada.getSala().getAsientosDisponibles()) {
            System.out.println("Cantidad de entradas incorrecta.");
            return;
        }

        int[] asientosSeleccionados = new int[cantidad];
        for (int i = 0; i < cantidad; i++) {
            boolean asientoValido = false;
            while (!asientoValido) {
                System.out.print("Ingrese el número de asiento para la entrada " + (i + 1) + ": ");
                int numAsiento = leerEntero(sc);

                boolean repetido = false;
                for (int j = 0; j < i; j++) {
                    if (asientosSeleccionados[j] == numAsiento) {
                        repetido = true;
                        break;
                    }
                }

                if (!repetido && seleccionada.getSala().estaDisponible(numAsiento)) {
                    asientosSeleccionados[i] = numAsiento;
                    asientoValido = true;
                } else {
                    System.out.println("El asiento ingresado está ocupado, repetido o no existe. Ingrese otro.");
                }
            }
        }

        String boleta = sistema.comprarEntradas(seleccionada, asientosSeleccionados);
        System.out.println(boleta);
    }

    private static void mostrarFuncionesDisponibles(SistemaTeatro sistema) {
        List<Funcion> disponibles = sistema.obtenerFuncionesDisponibles();
        if (disponibles.isEmpty()) {
            System.out.println("No quedan entradas disponibles para ninguna función.");
            return;
        }
        System.out.println("\n--- FUNCIONES DISPONIBLES ---");
        for (Funcion f : disponibles) {
            System.out.println("Código: " + f.getCodigo() +
                    " | Nombre: " + f.getNombre() +
                    " | Precio: $" + (int) f.getPrecio() +
                    " | Horario: " + f.getHorario() +
                    " | Disponibles: " + f.getSala().getAsientosDisponibles());
        }
        System.out.println("-----------------------------\n");
    }

    private static void procesarBusqueda(SistemaTeatro sistema, Scanner sc) {
        System.out.println("Buscar función por:");
        System.out.println("[1] Código");
        System.out.println("[2] Nombre");
        System.out.println("[3] Horario");
        System.out.print("Seleccione una opción: ");
        int opcion = leerEntero(sc);

        switch (opcion) {
            case 1:
                System.out.print("Ingrese el código: ");
                int cod = leerEntero(sc);
                Funcion f = sistema.buscarPorCodigo(cod);
                if (f != null) {
                    imprimirDetalleBusqueda(f);
                } else {
                    System.out.println("No se encontró ninguna función con ese código.");
                }
                break;
            case 2:
                System.out.print("Ingrese el nombre: ");
                String nom = sc.nextLine();
                List<Funcion> porNombre = sistema.buscarPorNombre(nom);
                if (porNombre.isEmpty()) {
                    System.out.println("No se encontraron funciones con ese nombre.");
                } else {
                    for (Funcion func : porNombre) {
                        imprimirDetalleBusqueda(func);
                    }
                }
                break;
            case 3:
                System.out.print("Ingrese el horario (mañana, tarde, noche): ");
                String hor = sc.nextLine();
                List<Funcion> porHorario = sistema.buscarPorHorario(hor);
                if (porHorario.isEmpty()) {
                    System.out.println("No se encontraron funciones en ese horario.");
                } else {
                    for (Funcion func : porHorario) {
                        imprimirDetalleBusqueda(func);
                    }
                }
                break;
            default:
                System.out.println("Opción de búsqueda inválida.");
        }
    }

    private static void imprimirDetalleBusqueda(Funcion f) {
        System.out.println("Código: " + f.getCodigo() +
                " | Nombre: " + f.getNombre() +
                " | Precio: $" + (int) f.getPrecio() +
                " | Horario: " + f.getHorario() +
                " | Disponibles: " + f.getSala().getAsientosDisponibles());
    }

    private static int leerEntero(Scanner sc) {
        while (true) {
            try {
                String entrada = sc.nextLine();
                return Integer.parseInt(entrada.trim());
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Por favor ingrese un número: ");
            }
        }
    }
}