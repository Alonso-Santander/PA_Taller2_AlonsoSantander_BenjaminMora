package dominio;


public class Pelicula extends Funcion {
    private String genero;
    private boolean nominadaOscar;


    public Pelicula(int codigo, String nombre, int duracion, double precio, String horario,
                    String genero, boolean nominadaOscar) {
        super(codigo, nombre, duracion, precio, horario);
        this.genero = genero;
        this.nominadaOscar = nominadaOscar;
    }

    @Override
    public double calcularPrecioConDescuento() {
        if (this.nominadaOscar) {
            return this.precio * 0.80; // 20% de descuento
        }
        return this.precio;
    }

    @Override
    public String getTipo() {
        return "Película";
    }

    @Override
    public String getDetallesEspecificos() {
        return "Género: " + genero + " | Nominada al Oscar: " + (nominadaOscar ? "Sí" : "No");
    }
}