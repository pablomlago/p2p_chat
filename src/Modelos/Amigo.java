package Modelos;

import RMI.MensaxeRemoto;

import javax.crypto.SecretKey;

public class Amigo extends Usuario {
    private final MensaxeRemoto mensaxeRemoto;
    private SecretKey secretKey;

    public Amigo(String nome, String estado, MensaxeRemoto mensaxeRemoto) {
        super(nome, estado);
        this.mensaxeRemoto = mensaxeRemoto;
        this.secretKey = null;
    }

    public Amigo(Usuario usuario, MensaxeRemoto mensaxeRemoto) {
        super(usuario.getNome(), usuario.getEstado());
        this.mensaxeRemoto = mensaxeRemoto;
        this.secretKey = null;
    }

    public MensaxeRemoto getMensaxeRemoto() {
        return this.mensaxeRemoto;
    }

    public SecretKey getSecretKey() {
        return this.secretKey;
    }

    public void setSecretKey(final SecretKey secretKey) {
        this.secretKey = secretKey;
    }
}
