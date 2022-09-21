package RMI;

import Aplicacion.Servidor.FachadaAplicacionServidor;
import Modelos.Usuario;
import Modelos.UsuarioRelacion;
import Modelos.UsuarioServidor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.PublicKey;
import java.util.List;

public class ServidorServiciosImpl extends UnicastRemoteObject implements ServidorServicios {
    private final FachadaAplicacionServidor fachadaAplicacionServidor;

    public ServidorServiciosImpl(FachadaAplicacionServidor fachadaAplicacionServidor) throws RemoteException {
        this.fachadaAplicacionServidor = fachadaAplicacionServidor;
    }
    @Override
    public PublicKey obterClavePublicaServidor() throws RemoteException {
        return this.fachadaAplicacionServidor.obterClavePublicaServidor();
    }
    @Override
    public Usuario insertarUsuario(byte[] key, byte[] iv, String credenciais) {
        return this.fachadaAplicacionServidor.insertarUsuario(key, iv, credenciais);
    }
    @Override
    public Usuario validarUsuario(byte[] key, byte[] iv, String credenciais, MensaxeRemoto mensaxeRemoto, NotificacionsRemoto notificacionsRemoto) {
        return this.fachadaAplicacionServidor.validarUsuario(key, iv, credenciais, mensaxeRemoto, notificacionsRemoto);
    }
    @Override
    public boolean desconectarUsuario(String nome, String mac, byte[] iv) throws RemoteException {
        return this.fachadaAplicacionServidor.desconectarUsuario(nome, mac, iv);
    }
    @Override
    public boolean enviarSolicitude(String solicitado, String nome, String mac, byte[] iv) throws RemoteException {
        return this.fachadaAplicacionServidor.enviarSolicitude(solicitado, nome, mac, iv);
    }
    @Override
    public boolean aceptarSolicitude(String solicitante, String nome, String mac, byte[] iv) throws RemoteException {
        return this.fachadaAplicacionServidor.aceptarSolicitude(solicitante, nome, mac, iv);
    }
    @Override
    public boolean rexeitarSolicitude(String solicitante, String nome, String mac, byte[] iv) throws RemoteException {
        return this.fachadaAplicacionServidor.rexeitarSolicitude(solicitante, nome, mac, iv);
    }

    @Override
    public List<UsuarioRelacion> buscarUsuarios(String busqueda, String nome, String mac, byte[] iv) throws RemoteException {
        return this.fachadaAplicacionServidor.buscarUsuarios(busqueda, nome, mac, iv);
    }
}
