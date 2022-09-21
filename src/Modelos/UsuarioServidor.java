package Modelos;

public class UsuarioServidor extends Usuario{
    private final byte[] keyDB;

    public UsuarioServidor(String nome, String estado, byte[] keyDB) {
        super(nome, estado);
        this.keyDB = keyDB;
    }

    public byte[] getKeyDB() { return this.keyDB; }
}
