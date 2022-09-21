package Encriptacion;

public class Contrasinal {

    private static final int log_rounds = 10;

    public static String hashContrasinal(String contrasinalTexto) {
        String salt = BCrypt.gensalt(log_rounds);

        return BCrypt.hashpw(contrasinalTexto, salt);
    }

    public static boolean comprobarContrasinal(String contrasinalTexto, String contrasinalAlmacenada)
    {
        boolean contrasinalVerificado = false;
        if(contrasinalAlmacenada == null || !contrasinalAlmacenada.startsWith("$2a$"))
            throw new java.lang.IllegalArgumentException("O hash proporcionado non e valido");

        contrasinalVerificado = BCrypt.checkpw(contrasinalTexto, contrasinalAlmacenada);

        return contrasinalVerificado;
    }
}
