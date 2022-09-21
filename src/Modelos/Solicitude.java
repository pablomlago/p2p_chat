package Modelos;

public class Solicitude {
    private final String emisor;

    public Solicitude(String emisor) {
        this.emisor = emisor;
    }

    public String getEmisor() {
        return emisor;
    }

    @Override
    public String toString() {
        return this.emisor;
    }
}
