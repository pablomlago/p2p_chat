package RMI;

import Modelos.Usuario;

import javax.crypto.SecretKey;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MensaxeRemoto extends Remote {
    void recibirMensaxe(String emisor, String mensaxe, String mac, byte[] iv) throws RemoteException;
}
