package dominio;


public class ObraTeatro extends Funcion {
    private String autor;
    private int cantidadActos;


    public ObraTeatro(int codigo, String nombre, int duracion, double precio, String horario,
                      String autor, int cantidadActos) {
        super(codigo, nombre, duracion, precio, horario);
        this.autor = autor;
        this.cantidadActos = cantidadActos;
    }

    @Override
    public double calcularPrecioConDescuento() {
        if (this.precio > 15000) {
            return this.precio * 0.90; // 10% de descuento
        }
        return this.precio;
    }

    @Override
    public String getTipo() {
        return "Obra de Teatro";
    }

    @Override
    public String getDetallesEspecificos() {
        return "Autor: " + autor + " | Actos: " + cantidadActos;
    }
}