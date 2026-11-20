//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package dominio;

public abstract class Funcion {
    protected int codigo;
    protected String nombre;
    protected int duracion;
    protected double precio;
    protected String horario;
    protected Sala sala;

    public Funcion(int codigo, String nombre, int duracion, double precio, String horario) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.duracion = duracion;
        this.precio = precio;
        this.horario = horario;
        this.sala = new Sala();
    }

    public abstract double calcularPrecioConDescuento();

    public abstract String getTipo();

    public abstract String getDetallesEspecificos();

    public int getCodigo() {
        return this.codigo;
    }

    public String getNombre() {
        return this.nombre;
    }

    public int getDuracion() {
        return this.duracion;
    }

    public double getPrecio() {
        return this.precio;
    }

    public String getHorario() {
        return this.horario;
    }

    public Sala getSala() {
        return this.sala;
    }
}
