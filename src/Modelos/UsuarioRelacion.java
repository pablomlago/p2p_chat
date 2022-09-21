package Modelos;

public class UsuarioRelacion extends Usuario {
    private final boolean isAmigo;
    private final boolean isSolicitado;
    private final boolean isPendente;

    public UsuarioRelacion(String nome, String estado, boolean isAmigo, boolean isSolicitado, boolean isPendente) {
        super(nome, estado);
        this.isAmigo = isAmigo;
        this.isSolicitado = isSolicitado;
        this.isPendente = isPendente;
    }

    public boolean isAmigo() {
        return isAmigo;
    }

    public boolean isSolicitado() {
        return isSolicitado;
    }

    public boolean isPendente() {
        return isPendente;
    }
}
