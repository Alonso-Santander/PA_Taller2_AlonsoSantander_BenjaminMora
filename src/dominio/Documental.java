
package dominio;

public class Documental extends Funcion {
    private String tema;
    private int anioProduccion;

    public Documental(int codigo, String nombre, int duracion, double precio, String horario, String tema, int anioProduccion) {
        super(codigo, nombre, duracion, precio, horario);
        this.tema = tema;
        this.anioProduccion = anioProduccion;
    }

    public double calcularPrecioConDescuento() {
        return this.anioProduccion < 2018 ? this.precio * 0.9 : this.precio;
    }

    public String getTipo() {
        return "Documental";
    }

    public String getDetallesEspecificos() {
        return "Tema: " + this.tema + " | Año de Producción: " + this.anioProduccion;
    }
}
