package servicios;


import dominio.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class SistemaTeatroImpl implements SistemaTeatro {
    private List<Funcion> funciones;
    private List<Usuario> usuarios;
    private Usuario usuarioActual;
    private double recaudacionNoche;
    private double gananciasTotales;


    public SistemaTeatroImpl() {
        this.funciones = new ArrayList<>();
        this.usuarios = new ArrayList<>();
        this.usuarioActual = null;
        this.recaudacionNoche = 0;
        this.gananciasTotales = 0;
    }

    @Override
    public void cargarFunciones(String rutaArchivo) throws FileNotFoundException {
        File archivo = new File(rutaArchivo);
        Scanner lector = new Scanner(archivo);

        while (lector.hasNextLine()) {
            String linea = lector.nextLine().trim();
            if (linea.isEmpty()) {
                continue;
            }
            String[] partes = linea.split(",");
            int codigo = Integer.parseInt(partes[0].trim());
            String nombre = partes[1].trim();
            int duracion = Integer.parseInt(partes[2].trim());
            double precio = Double.parseDouble(partes[3].trim());
            String horario = partes[4].trim();
            String atr1 = partes[5].trim();
            String atr2 = partes[6].trim();

            if (codigo >= 100 && codigo <= 199) {
                boolean nominada = atr2.equalsIgnoreCase("si");
                funciones.add(new Pelicula(codigo, nombre, duracion, precio, horario, atr1, nominada));
            } else if (codigo >= 200 && codigo <= 299) {
                int actos = Integer.parseInt(atr2);
                funciones.add(new ObraTeatro(codigo, nombre, duracion, precio, horario, atr1, actos));
            } else if (codigo >= 300 && codigo <= 399) {
                int anio = Integer.parseInt(atr2);
                funciones.add(new Documental(codigo, nombre, duracion, precio, horario, atr1, anio));
            }
        }
        lector.close();
    }

    @Override
    public boolean registrarUsuario(String nombre, String rut, String contrasena) {
        if (nombre == null || nombre.trim().isEmpty() ||
                rut == null || rut.trim().isEmpty() ||
                contrasena == null || contrasena.trim().isEmpty()) {
            return false;
        }
        for (Usuario u : usuarios) {
            if (u.getRut().equalsIgnoreCase(rut.trim())) {
                return false;
            }
        }
        usuarios.add(new Usuario(nombre.trim(), rut.trim(), contrasena.trim()));
        return true;
    }

    @Override
    public Usuario iniciarSesion(String rut, String contrasena) {
        for (Usuario u : usuarios) {
            if (u.getRut().equalsIgnoreCase(rut.trim()) && u.getContrasena().equals(contrasena)) {
                this.usuarioActual = u;
                return u;
            }
        }
        return null;
    }

    @Override
    public void cerrarSesion() {
        this.usuarioActual = null;
    }

    @Override
    public boolean unirseAlClub(String rut, String contrasena) {
        if (this.usuarioActual == null) {
            return false;
        }
        if (this.usuarioActual.getRut().equalsIgnoreCase(rut.trim()) &&
                this.usuarioActual.getContrasena().equals(contrasena)) {
            this.usuarioActual.setMiembroClub(true);
            return true;
        }
        return false;
    }

    @Override
    public List<Funcion> obtenerFuncionesDisponibles() {
        List<Funcion> disponibles = new ArrayList<>();
        for (Funcion f : funciones) {
            if (f.getSala().getAsientosDisponibles() > 0) {
                disponibles.add(f);
            }
        }
        return disponibles;
    }

    @Override
    public Funcion buscarPorCodigo(int codigo) {
        for (Funcion f : funciones) {
            if (f.getCodigo() == codigo) {
                return f;
            }
        }
        return null;
    }

    @Override
    public List<Funcion> buscarPorNombre(String nombre) {
        List<Funcion> resultado = new ArrayList<>();
        for (Funcion f : funciones) {
            if (f.getNombre().toLowerCase().contains(nombre.trim().toLowerCase())) {
                resultado.add(f);
            }
        }
        return resultado;
    }

    @Override
    public List<Funcion> buscarPorHorario(String horario) {
        List<Funcion> resultado = new ArrayList<>();
        for (Funcion f : funciones) {
            if (f.getHorario().equalsIgnoreCase(horario.trim())) {
                resultado.add(f);
            }
        }
        return resultado;
    }

    @Override
    public String comprarEntradas(Funcion funcion, int[] asientos) {
        for (int asiento : asientos) {
            funcion.getSala().ocuparAsiento(asiento);
        }

        double precioUnitario = funcion.calcularPrecioConDescuento();
        if (usuarioActual.isMiembroClub()) {
            precioUnitario = precioUnitario * 0.85; // 15% descuento adicional por Club
        }

        double montoTotal = precioUnitario * asientos.length;
        this.gananciasTotales += montoTotal;
        if (funcion.getHorario().equalsIgnoreCase("noche")) {
            this.recaudacionNoche += montoTotal;
        }

        StringBuilder boleta = new StringBuilder();
        boleta.append("\n========================================\n");
        boleta.append("           BOLETA DE COMPRA             \n");
        boleta.append("========================================\n");
        boleta.append("Usuario: ").append(usuarioActual.getNombre()).append("\n");
        boleta.append("RUT: ").append(usuarioActual.getRut()).append("\n");
        boleta.append("Función: ").append(funcion.getNombre()).append(" (").append(funcion.getTipo()).append(")\n");
        boleta.append("Duración: ").append(funcion.getDuracion()).append(" min\n");
        boleta.append("Horario: ").append(funcion.getHorario()).append("\n");
        boleta.append("Precio base: $").append((int) funcion.getPrecio()).append("\n");
        boleta.append(funcion.getDetallesEspecificos()).append("\n");
        boleta.append("Cantidad de entradas: ").append(asientos.length).append("\n");
        boleta.append("Monto total: $").append((int) Math.round(montoTotal)).append("\n");
        boleta.append("----------------------------------------\n");
        boleta.append("¡Compra realizada con éxito!\n");
        boleta.append("========================================\n");

        return boleta.toString();
    }

    @Override
    public void generarArchivoEstadisticas(String rutaSalida) throws IOException {
        int totalEntradas = 0;
        int entradasPeliculas = 0;
        int entradasObras = 0;
        int entradasDocumentales = 0;

        Funcion masPopular = null;
        int maxEntradasFuncion = -1;

        for (Funcion f : funciones) {
            int vendidas = f.getSala().getAsientosOcupados();
            totalEntradas += vendidas;

            if (f instanceof Pelicula) {
                entradasPeliculas += vendidas;
            } else if (f instanceof ObraTeatro) {
                entradasObras += vendidas;
            } else if (f instanceof Documental) {
                entradasDocumentales += vendidas;
            }

            if (vendidas > maxEntradasFuncion) {
                maxEntradasFuncion = vendidas;
                masPopular = f;
            }
        }

        int miembrosClub = 0;
        for (Usuario u : usuarios) {
            if (u.isMiembroClub()) {
                miembrosClub++;
            }
        }
        double porcentajeClub = usuarios.isEmpty() ? 0.0 : (miembrosClub * 100.0) / usuarios.size();

        String tipoMasVendido = "Ninguno";
        int maxTipo = Math.max(entradasPeliculas, Math.max(entradasObras, entradasDocumentales));
        if (maxTipo > 0) {
            if (maxTipo == entradasPeliculas) {
                tipoMasVendido = "Película";
            } else if (maxTipo == entradasObras) {
                tipoMasVendido = "Obra de Teatro";
            } else {
                tipoMasVendido = "Documental";
            }
        }

        String nombreMasPopular = (masPopular != null && maxEntradasFuncion > 0)
                ? masPopular.getNombre() + " (" + maxEntradasFuncion + " entradas)"
                : "Sin ventas registradas";

        PrintWriter escritor = new PrintWriter(new FileWriter(rutaSalida));
        escritor.println("=== ESTADÍSTICAS TEATRO DE ANTOFAGASTA ===");
        escritor.println("1. Cantidad total de entradas vendidas: " + totalEntradas);
        escritor.printf("2. Porcentaje de usuarios en ClubTeatroAntofagasta: %.2f%%\n", porcentajeClub);
        escritor.println("3. Función más popular: " + nombreMasPopular);
        escritor.println("4. Tipo de función que más entradas vendió: " + tipoMasVendido);
        escritor.println("5. Dinero total recaudado en funciones de la noche: $" + (int) Math.round(recaudacionNoche));
        escritor.println("6. Ganancias totales en la inauguración del Teatro: $" + (int) Math.round(gananciasTotales));
        escritor.close();
    }

    @Override
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }
}
