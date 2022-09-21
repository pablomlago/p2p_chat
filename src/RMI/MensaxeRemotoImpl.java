package RMI;

import Aplicacion.Cliente.FachadaAplicacion;
import Modelos.Usuario;

import javax.crypto.SecretKey;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MensaxeRemotoImpl extends UnicastRemoteObject implements MensaxeRemoto, Serializable {
    private final transient FachadaAplicacion fachadaAplicacion;

    public MensaxeRemotoImpl(FachadaAplicacion fachadaAplicacion) throws RemoteException {
        this.fachadaAplicacion = fachadaAplicacion;
    }

    @Override
    public void recibirMensaxe(String emisor, String mensaxe, String mac, byte[] iv) throws RemoteException {
        this.fachadaAplicacion.recibirMensaxe(emisor, mensaxe, mac, iv);
    }
}
