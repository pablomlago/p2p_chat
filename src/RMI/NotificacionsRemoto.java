package RMI;

import Modelos.Amigo;
import Modelos.Usuario;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface NotificacionsRemoto extends Remote {
    //void notificarConexion(String usuario) throws RemoteException;
    void notificarDesconexion(String usuario) throws RemoteException;
    void notificarConexion(Amigo amigo, byte[] key, byte[] iv) throws RemoteException;
    void notificarConexion(List<Amigo> amigos, List<byte[]> keys, List<byte[]> ivs) throws RemoteException;
    void recibirSolicitude(String solicitante) throws RemoteException;
    void recibirSolicitude(List<String> solicitudes) throws RemoteException;
}
