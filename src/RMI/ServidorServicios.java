package RMI;

import Modelos.Usuario;
import Modelos.UsuarioRelacion;
import Modelos.UsuarioServidor;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.List;

public interface ServidorServicios extends Remote {
    PublicKey obterClavePublicaServidor() throws RemoteException;
    Usuario insertarUsuario(byte[] key, byte[] iv, String credenciais) throws RemoteException;
    Usuario validarUsuario(byte[] key, byte[] iv, String credenciais, MensaxeRemoto mensaxeRemoto, NotificacionsRemoto notificacionsRemoto) throws RemoteException;
    boolean desconectarUsuario(String nome, String mac, byte[] iv) throws RemoteException;
    boolean enviarSolicitude(String solicitado, String nome, String mac, byte[] iv) throws RemoteException;
    boolean aceptarSolicitude(String solicitante, String nome, String mac, byte[] iv) throws RemoteException;
    boolean rexeitarSolicitude(String solicitante, String nome, String mac, byte[] iv) throws RemoteException;

    List<UsuarioRelacion> buscarUsuarios(String busqueda, String nome, String mac, byte[] iv) throws RemoteException;
}
