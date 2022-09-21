package BaseDatos.Servidor;

import Aplicacion.Servidor.FachadaAplicacionServidor;
import Modelos.Usuario;
import Modelos.UsuarioRelacion;
import Modelos.UsuarioServidor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

public class FachadaBaseDatosServidor {
    private final FachadaAplicacionServidor fachadaAplicacionServidor;
    private Connection conexion;

    private DAOServidor daoServidor;

    public FachadaBaseDatosServidor (FachadaAplicacionServidor fachadaAplicacionServidor){
        Properties configuracion = new Properties();
        this.fachadaAplicacionServidor = fachadaAplicacionServidor;
        InputStream arqConfiguracion;

        try {
            arqConfiguracion = getClass().getClassLoader().getResourceAsStream("baseDatos.properties");
            configuracion.load(arqConfiguracion);
            arqConfiguracion.close();

            Properties usuario = new Properties();
            String xestor = configuracion.getProperty("gestor");

            usuario.setProperty("user", configuracion.getProperty("usuario"));
            usuario.setProperty("password", configuracion.getProperty("clave"));
            this.conexion=java.sql.DriverManager.getConnection("jdbc:"+xestor+"://"+
                            configuracion.getProperty("servidor")+":"+
                            configuracion.getProperty("puerto")+"/"+
                            configuracion.getProperty("baseDatos"),
                    usuario);

            this.daoServidor = new DAOServidor(fachadaAplicacionServidor, conexion);

        } catch (FileNotFoundException f){
            System.out.println(f.getMessage());
        } catch (IOException i){
            System.out.println(i.getMessage());
        }
        catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public Usuario insertarUsuario(String nome, String contrasinal, String estado) {
        return this.daoServidor.insertarUsuario(nome, contrasinal, estado);
    }
    public Usuario validarUsuario(String nome, String contrasinal, byte[] key) {
        return this.daoServidor.validarUsuario(nome, contrasinal, key);
    }

    public List<UsuarioServidor> obterAmigosOnline(String nome) {
        return this.daoServidor.obterAmigosOnline(nome);
    }

    public UsuarioServidor obterUsuarioServidor(String nome) {
        return this.daoServidor.obterUsuarioServidor(nome);
    }

    public void desconectarUsuario(String nome) {
        this.daoServidor.desconectarUsuario(nome);
    }

    public boolean rexeitarSolicitude(String solicitante, String nome) {
        return this.daoServidor.rexeitarSolicitude(solicitante, nome);
    }

    public boolean aceptarSolicitude(String solicitante, String nome) {
        return this.daoServidor.aceptarSolicitude(solicitante, nome);
    }

    public boolean enviarSolicitude(String solicitado, String nome) {
        return this.daoServidor.enviarSolicitude(solicitado, nome);
    }

    public List<UsuarioRelacion> buscarUsuarios(String nome, String busqueda) {
        return this.daoServidor.buscarUsuarios(nome, busqueda);
    }

    public List<String> obterSolicitantes(String nome) {
        return this.daoServidor.obterSolicitantes(nome);
    }
}
