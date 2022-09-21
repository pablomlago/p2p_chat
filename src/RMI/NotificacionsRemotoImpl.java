package RMI;

import Aplicacion.Cliente.FachadaAplicacion;
import Modelos.Amigo;
import Modelos.Usuario;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class NotificacionsRemotoImpl extends UnicastRemoteObject implements NotificacionsRemoto, Serializable {
    private final transient FachadaAplicacion fachadaAplicacion;

    public NotificacionsRemotoImpl(FachadaAplicacion fachadaAplicacion) throws RemoteException {
        this.fachadaAplicacion = fachadaAplicacion;
    }

    @Override
    public void notificarDesconexion(String usuario) throws RemoteException {
        this.fachadaAplicacion.notificarDesconexion(usuario);
    }

    @Override
    public void notificarConexion(Amigo amigo, byte[] key, byte[] iv) throws RemoteException {
        this.fachadaAplicacion.notificarConexion(amigo, key, iv);
    }

    @Override
    public void notificarConexion(List<Amigo> amigos, List<byte[]> keys, List<byte[]> ivs) throws RemoteException {
        this.fachadaAplicacion.notificarConexion(amigos, keys, ivs);
    }

    @Override
    public void recibirSolicitude(String solicitante) throws RemoteException {
        this.fachadaAplicacion.recibirSolicitude(solicitante);
    }
    @Override
    public void recibirSolicitude(List<String> solicitudes) throws RemoteException {
        this.fachadaAplicacion.recibirSolicitude(solicitudes);
    }
}
