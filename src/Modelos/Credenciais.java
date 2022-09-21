package Modelos;

import java.io.Serializable;

public class Credenciais implements Serializable {
    private final String nome;
    private final String contrasinal;
    private final String estado;

    public Credenciais(String nome, String contrasinal, String estado) {
        this.nome = nome;
        this.contrasinal = contrasinal;
        this.estado = estado;
    }

    public String getNome() {
        return this.nome;
    }

    public String getContrasinal() {
        return this.contrasinal;
    }

    public String getEstado() {
        return this.estado;
    }
}
