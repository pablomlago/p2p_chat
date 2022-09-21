package Aplicacion.Servidor;

import BaseDatos.Servidor.FachadaBaseDatosServidor;
import Encriptacion.AsymmetricCryptography;
import Encriptacion.GenerateKeys;
import Encriptacion.HashCryptography;
import Encriptacion.SymmetricCryptography;
import Modelos.*;
import RMI.MensaxeRemoto;
import RMI.NotificacionsRemoto;
import RMI.ServidorServiciosImpl;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FachadaAplicacionServidor {
    public static final String RMI_HOST = "127.0.0.1";
    public static final int RMI_PORT = 1532;

    private final FachadaBaseDatosServidor fachadaBaseDatosServidor;

    private final AsymmetricCryptography crypt;
    private final String pathClaves;

    private final ConcurrentHashMap<String, NotificacionsRemoto> notificacionsRemotoHashMap;
    private final ConcurrentHashMap<String, MensaxeRemoto> mensaxeRemotoHashMap;

    public FachadaAplicacionServidor() throws NoSuchPaddingException, NoSuchAlgorithmException {
        this.fachadaBaseDatosServidor = new FachadaBaseDatosServidor(this);
        this.crypt = new AsymmetricCryptography();
        this.pathClaves = "KeyPair/";
        this.notificacionsRemotoHashMap = new ConcurrentHashMap<>();
        this.mensaxeRemotoHashMap = new ConcurrentHashMap<>();
    }

    private String getPathClaves() {
        return this.pathClaves;
    }

    public static String generateUrl(String suffix) {
        return "rmi://" +
                RMI_HOST + ":" + RMI_PORT +
                "/" + suffix;
    }

    public void startRegistry(int RMIPortNum)
            throws RemoteException {
        try {
            Registry registry = LocateRegistry.getRegistry(RMIPortNum);
            registry.list( );  // This call will throw an exception
            // if the registry does not already exist
        }
        catch (RemoteException e) {
            // No valid registry at that port.
            System.out.println("Rexistro RMI non se pode localizar no porto: " + RMIPortNum);
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
            System.out.println("Rexistro RMI creado no porto: " + RMIPortNum);
        }
    } // end startRegistry

    public void rexistrarServidor() throws RemoteException{
        String url = FachadaAplicacionServidor.generateUrl("services");
        Registry registry = LocateRegistry.getRegistry(FachadaAplicacionServidor.RMI_HOST, FachadaAplicacionServidor.RMI_PORT);
        registry.rebind(url, new ServidorServiciosImpl(this));
    }

    public static void main(String[] args) {
        FachadaAplicacionServidor fachadaAplicacionServidor;
        GenerateKeys gk;

        try {
            fachadaAplicacionServidor = new FachadaAplicacionServidor();
            //Xeracion claves privadas e publicas
            gk = new GenerateKeys(2048);
            gk.createKeys();
            gk.writeToFile(fachadaAplicacionServidor.getPathClaves() + "publicKey", gk.getPublicKey().getEncoded());
            gk.writeToFile(fachadaAplicacionServidor.getPathClaves() + "privateKey", gk.getPrivateKey().getEncoded());
            //Iniciamos o rexistro RMI
            fachadaAplicacionServidor.startRegistry(RMI_PORT);
            fachadaAplicacionServidor.rexistrarServidor();
        } catch (NoSuchAlgorithmException | NoSuchProviderException | IOException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    private PrivateKey obterClavePrivadaServidor() {
        PrivateKey privateKey = null;
        try {
            privateKey = this.crypt.getPrivate(this.pathClaves+"privateKey");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return  privateKey;
    }

    private <Clase> Clase parseObject(String o) throws Exception {
        Clase parsedObject = null;
        byte[] data = Base64.getDecoder().decode(o);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        parsedObject = (Clase) ois.readObject();
        ois.close();
        return parsedObject;
    }

    private UsuarioServidor verificarIdentidade(String nome, String mac, byte[] iv) {
        UsuarioServidor usuarioServidor = null;
        try {
            String nomeDecrypted = this.crypt.decryptText(nome, this.obterClavePrivadaServidor());
            usuarioServidor = this.fachadaBaseDatosServidor.obterUsuarioServidor(nomeDecrypted);
            if(usuarioServidor != null) {
                SecretKey secretKeyDB = new SecretKeySpec(usuarioServidor.getKeyDB(), 0, usuarioServidor.getKeyDB().length, "AES");
                String hash = HashCryptography.computeHash(nomeDecrypted);
                String macVerificado = SymmetricCryptography.do_AESEncryption(hash, secretKeyDB, iv);
                if(!mac.equals(macVerificado)) {
                    usuarioServidor = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuarioServidor;
    }

    public PublicKey obterClavePublicaServidor() {
        PublicKey publicKey = null;
        try {
            publicKey = this.crypt.getPublic(this.pathClaves+"publicKey");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  publicKey;
    }

    public Usuario insertarUsuario(byte[] key, byte[] iv, String credenciais) {
        Usuario usuario = null;
        try {
            SecretKey secretKey = this.crypt.decryptKey(key, this.obterClavePrivadaServidor());
            String credenciaisDecrypted = SymmetricCryptography.do_AESDecryption(credenciais, secretKey, iv);
            Credenciais objCredenciais = parseObject(credenciaisDecrypted);
            usuario = this.fachadaBaseDatosServidor.insertarUsuario(
                    objCredenciais.getNome(),
                    objCredenciais.getContrasinal(),
                    objCredenciais.getEstado()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuario;
    }

    public Usuario validarUsuario(byte[] key, byte[] iv, String credenciais, MensaxeRemoto mensaxeRemoto, NotificacionsRemoto notificacionsRemoto) {
        Usuario usuario = null;
        try {
            SecretKey secretKey = this.crypt.decryptKey(key, this.obterClavePrivadaServidor());
            String credenciaisDecrypted = SymmetricCryptography.do_AESDecryption(credenciais, secretKey, iv);
            Credenciais objCredenciais = parseObject(credenciaisDecrypted);
            usuario = this.fachadaBaseDatosServidor.validarUsuario(
                    objCredenciais.getNome(),
                    objCredenciais.getContrasinal(),
                    secretKey.getEncoded()
            );
            if(usuario != null) {
                this.notificacionsRemotoHashMap.put(usuario.getNome(), notificacionsRemoto);
                this.mensaxeRemotoHashMap.put(usuario.getNome(), mensaxeRemoto);
                //Usuario
                Amigo thisUsuario = new Amigo(usuario, mensaxeRemoto);
                //Amigos do usuario
                List<Amigo> amigos = new ArrayList<>();
                List<byte[]> keys = new ArrayList<>();
                List<byte[]> ivs = new ArrayList<>();
                //Notificamos a conexion aos amigos
                List<UsuarioServidor> amigosDB = this.fachadaBaseDatosServidor.obterAmigosOnline(usuario.getNome());
                try {
                    for (UsuarioServidor amigoDB : amigosDB) {
                        SecretKey secretKeyComun = SymmetricCryptography.createAESKey();
                        byte[] ivAmigo = SymmetricCryptography.createInitializationVector();
                        SecretKey secretKeyAmigo = new SecretKeySpec(amigoDB.getKeyDB(), 0, amigoDB.getKeyDB().length, "AES");
                        byte[] encryptedKeyComun = SymmetricCryptography.encryptKey(secretKeyComun, secretKeyAmigo, ivAmigo);
                        Amigo amigo = new Amigo(amigoDB, this.mensaxeRemotoHashMap.get(amigoDB.getNome()));
                        amigos.add(amigo);
                        keys.add(SymmetricCryptography.encryptKey(secretKeyComun, secretKey, ivAmigo));
                        ivs.add(ivAmigo);
                        this.notificacionsRemotoHashMap.get(amigo.getNome()).notificarConexion(thisUsuario, encryptedKeyComun, ivAmigo);
                    }
                    notificacionsRemoto.notificarConexion(amigos, keys, ivs);
                    notificacionsRemoto.recibirSolicitude(this.fachadaBaseDatosServidor.obterSolicitantes(usuario.getNome()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuario;
    }

    public boolean desconectarUsuario(String nome, String mac, byte[] iv) {
        boolean flag = false;
        try {
            UsuarioServidor usuario = verificarIdentidade(nome, mac, iv);
            if(usuario != null) {
                this.fachadaBaseDatosServidor.desconectarUsuario(usuario.getNome());
                this.mensaxeRemotoHashMap.remove(usuario.getNome());
                this.notificacionsRemotoHashMap.remove(usuario.getNome());
                flag = true;
                //Notificamos a conexion aos amigos
                List<UsuarioServidor> amigos = this.fachadaBaseDatosServidor.obterAmigosOnline(usuario.getNome());
                for(UsuarioServidor amigo : amigos) {
                    try {
                        this.notificacionsRemotoHashMap.get(amigo.getNome()).notificarDesconexion(usuario.getNome());
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean enviarSolicitude(String solicitado, String nome, String mac, byte[] iv) {
        boolean flag = false;
        try {
            UsuarioServidor usuario = verificarIdentidade(nome, mac, iv);
            UsuarioServidor usuarioSolicitado = this.fachadaBaseDatosServidor.obterUsuarioServidor(solicitado);
            if(usuario == null || usuarioSolicitado == null) return false;
            if(!this.fachadaBaseDatosServidor.enviarSolicitude(solicitado, usuario.getNome())) return false;
            if(usuarioSolicitado.getKeyDB() != null) {
                this.notificacionsRemotoHashMap.get(usuarioSolicitado.getNome()).recibirSolicitude(usuario.getNome());
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean aceptarSolicitude(String solicitante, String nome, String mac, byte[] iv) {
        boolean flag = false;
        try {
            UsuarioServidor usuario = verificarIdentidade(nome, mac, iv);
            UsuarioServidor usuarioSolicitante = this.fachadaBaseDatosServidor.obterUsuarioServidor(solicitante);
            if(usuario == null || usuarioSolicitante == null) return false;
            if(!this.fachadaBaseDatosServidor.aceptarSolicitude(solicitante, usuario.getNome())) return false;
            if(usuarioSolicitante.getKeyDB() != null) {
                SecretKey secretKeyComun = SymmetricCryptography.createAESKey();
                byte[] ivComun = SymmetricCryptography.createInitializationVector();
                SecretKey secretKey = new SecretKeySpec(usuario.getKeyDB(), 0, usuario.getKeyDB().length, "AES");
                SecretKey secretKeySolicitante = new SecretKeySpec(usuarioSolicitante.getKeyDB(), 0, usuarioSolicitante.getKeyDB().length, "AES");
                byte[] encryptedKey = SymmetricCryptography.encryptKey(secretKeyComun, secretKey, ivComun);
                byte[] encryptedKeySolicitante = SymmetricCryptography.encryptKey(secretKeyComun, secretKeySolicitante, ivComun);
                this.notificacionsRemotoHashMap.get(usuario.getNome()).notificarConexion(new Amigo(usuarioSolicitante, this.mensaxeRemotoHashMap.get(usuarioSolicitante.getNome())), encryptedKey, ivComun);
                this.notificacionsRemotoHashMap.get(usuarioSolicitante.getNome()).notificarConexion(new Amigo(usuario, this.mensaxeRemotoHashMap.get(usuario.getNome())), encryptedKeySolicitante, ivComun);
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean rexeitarSolicitude(String solicitante, String nome, String mac, byte[] iv) {
        boolean flag = false;
        try {
            UsuarioServidor usuario = verificarIdentidade(nome, mac, iv);
            if(usuario != null) {
                flag = this.fachadaBaseDatosServidor.rexeitarSolicitude(solicitante, usuario.getNome());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public List<UsuarioRelacion> buscarUsuarios(String busqueda, String nome, String mac, byte[] iv) {
        List<UsuarioRelacion> resultado = null;
        try {
            UsuarioServidor usuario = verificarIdentidade(nome, mac, iv);
            if(usuario != null) {
                resultado = this.fachadaBaseDatosServidor.buscarUsuarios(usuario.getNome(), busqueda);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultado;
    }
}
