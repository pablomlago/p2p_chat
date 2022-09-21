package BaseDatos.Servidor;

import Aplicacion.Servidor.FachadaAplicacionServidor;

import java.sql.Connection;

public abstract class AbstractDAOServidor {
    private final FachadaAplicacionServidor fachadaAplicacionServidor;
    private final Connection conexion;

    public AbstractDAOServidor(FachadaAplicacionServidor fachadaAplicacionServidor, Connection conexion) {
        this.fachadaAplicacionServidor = fachadaAplicacionServidor;
        this.conexion = conexion;
    }

    public FachadaAplicacionServidor getFachadaAplicacionServidor() {
        return fachadaAplicacionServidor;
    }

    public Connection getConexion() {
        return conexion;
    }
}
