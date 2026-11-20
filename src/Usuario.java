
package dominio;

public class Usuario {
    private String nombre;
    private String rut;
    private String contrasena;
    private boolean miembroClub;

    public Usuario(String nombre, String rut, String contrasena) {
        this.nombre = nombre;
        this.rut = rut;
        this.contrasena = contrasena;
        this.miembroClub = false;
    }

    public String getNombre() {
        return this.nombre;
    }

    public String getRut() {
        return this.rut;
    }

    public String getContrasena() {
        return this.contrasena;
    }

    public boolean isMiembroClub() {
        return this.miembroClub;
    }

    public void setMiembroClub(boolean miembroClub) {
        this.miembroClub = miembroClub;
    }
}
