//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package dominio;

public class Sala {
    private boolean[][] asientos = new boolean[5][5];
    private int capacidadMaxima = 25;
    private int asientosOcupados = 0;

    public Sala() {
    }

    public boolean estaDisponible(int numeroAsiento) {
        if (numeroAsiento >= 1 && numeroAsiento <= 25) {
            int fila = (numeroAsiento - 1) / 5;
            int columna = (numeroAsiento - 1) % 5;
            return !this.asientos[fila][columna];
        } else {
            return false;
        }
    }

    public boolean ocuparAsiento(int numeroAsiento) {
        if (!this.estaDisponible(numeroAsiento)) {
            return false;
        } else {
            int fila = (numeroAsiento - 1) / 5;
            int columna = (numeroAsiento - 1) % 5;
            this.asientos[fila][columna] = true;
            ++this.asientosOcupados;
            return true;
        }
    }

    public String obtenerMapaAsientos() {
        StringBuilder sb = new StringBuilder();
        sb.append("         [Asientos]\n");
        int contador = 1;

        for(int i = 0; i < 5; ++i) {
            for(int j = 0; j < 5; ++j) {
                if (this.asientos[i][j]) {
                    sb.append("[  x ] ");
                } else {
                    sb.append(String.format("[%3d ] ", contador));
                }

                ++contador;
            }

            sb.append("\n");
        }

        return sb.toString();
    }

    public int getAsientosDisponibles() {
        return this.capacidadMaxima - this.asientosOcupados;
    }

    public int getAsientosOcupados() {
        return this.asientosOcupados;
    }

    public int getCapacidadMaxima() {
        return this.capacidadMaxima;
    }
}
