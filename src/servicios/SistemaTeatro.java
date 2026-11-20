package servicios;


import dominio.Funcion;
import dominio.Usuario;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;


public interface SistemaTeatro {


    void cargarFunciones(String rutaArchivo) throws FileNotFoundException;

    boolean registrarUsuario(String nombre, String rut, String contrasena);

    Usuario iniciarSesion(String rut, String contrasena);

    void cerrarSesion();

    boolean unirseAlClub(String rut, String contrasena);

    List<Funcion> obtenerFuncionesDisponibles();

    Funcion buscarPorCodigo(int codigo);

    List<Funcion> buscarPorNombre(String nombre);

    List<Funcion> buscarPorHorario(String horario);

    String comprarEntradas(Funcion funcion, int[] asientos);

    void generarArchivoEstadisticas(String rutaSalida) throws IOException;

    Usuario getUsuarioActual();
}