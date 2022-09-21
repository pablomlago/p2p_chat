package Modelos;

import java.io.Serializable;

public class Usuario implements Serializable {
    private final String nome;
    private final String estado;

    public Usuario(String nome, String estado) {
        this.nome = nome;
        this.estado = estado;
    }

    public String getNome() {
        return nome;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return this.nome;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Usuario)) return false;
        Usuario u = (Usuario) o;
        return u.getNome().equals(this.getNome());
    }
}
