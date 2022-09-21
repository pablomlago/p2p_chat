package BaseDatos.Cliente;

import Aplicacion.Cliente.FachadaAplicacion;
import Encriptacion.AsymmetricCryptography;
import Encriptacion.HashCryptography;
import Encriptacion.SymmetricCryptography;
import Modelos.*;
import RMI.MensaxeRemotoImpl;
import RMI.NotificacionsRemotoImpl;
import RMI.ServidorServicios;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

public class FachadaClienteServidor {
    private final FachadaAplicacion fachadaAplicacion;
    private ServidorServicios conexion;
    private AsymmetricCryptography crypt;
    private PublicKey publicKeyServidor;
    private SecretKey secretKeyCliente;

    public FachadaClienteServidor(FachadaAplicacion fachadaAplicacion){
        Properties configuracion = new Properties();
        this.fachadaAplicacion = fachadaAplicacion;
        InputStream arqConfiguracion;

        try {
            arqConfiguracion = getClass().getClassLoader().getResourceAsStream("servidor.properties");
            configuracion.load(arqConfiguracion);
            arqConfiguracion.close();

            String host = configuracion.getProperty("host");
            String port = configuracion.getProperty("port");
            int intPort = Integer.parseInt(port);

            String urlServidor = "rmi://" +
                    host + ":" + port +
                    "/" + configuracion.getProperty("name");

            System.setProperty("java.rmi.server.hostname", host);
            Registry registry = LocateRegistry.getRegistry(host, intPort);
            this.conexion = (ServidorServicios) registry.lookup(urlServidor);
            this.publicKeyServidor = this.conexion.obterClavePublicaServidor();
            this.secretKeyCliente = null;

            this.crypt = new AsymmetricCryptography();
        } catch (FileNotFoundException f){
            f.printStackTrace();
            System.out.println(f.getMessage());
            fachadaAplicacion.muestraExcepcion(f.getMessage());
        } catch (IOException i){
            i.printStackTrace();
            System.out.println(i.getMessage());
            fachadaAplicacion.muestraExcepcion(i.getMessage());
        } catch (NotBoundException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            fachadaAplicacion.muestraExcepcion(e.getMessage());
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            fachadaAplicacion.muestraExcepcion(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            fachadaAplicacion.muestraExcepcion(e.getMessage());
        }
    }

    public Usuario validarUsuario(String nome, String contrasinal) {
        Usuario usuario = null;
        try {
            this.secretKeyCliente = SymmetricCryptography.createAESKey();
            byte[] encryptedSecretKey = this.crypt.encryptKey(secretKeyCliente, this.publicKeyServidor);
            byte[] iv = SymmetricCryptography.createInitializationVector();
            String credenciaisEncoded = parseObject(new Credenciais(nome, contrasinal, ""));

            usuario = this.conexion.validarUsuario(
                    encryptedSecretKey,
                    iv,
                    SymmetricCryptography.do_AESEncryption(credenciaisEncoded, secretKeyCliente, iv),
                    new MensaxeRemotoImpl(fachadaAplicacion),
                    new NotificacionsRemotoImpl(fachadaAplicacion)
            );
            if(usuario == null) {
                this.secretKeyCliente = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuario;
    }

    public String xerarMACClienteServidor(String base, SecretKey secretKey, byte[] iv) throws Exception {
        String hashMensaxe = HashCryptography.computeHash(base);
        return SymmetricCryptography.do_AESEncryption(hashMensaxe, secretKey, iv);
    }

    public boolean desconectarUsuario(String nome) {
        boolean desconectado = false;
        try {
            byte[] iv = SymmetricCryptography.createInitializationVector();
            String mac = xerarMACClienteServidor(nome, this.secretKeyCliente, iv);
            desconectado = this.conexion.desconectarUsuario(
                    this.crypt.encryptText(nome, this.publicKeyServidor),
                    mac,
                    iv
            );
        } catch (Exception e) {}
        return desconectado;
    }

    private String parseObject(Object o) throws Exception {
        String parsedObject;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        parsedObject = Base64.getEncoder().encodeToString(baos.toByteArray());
        return parsedObject;
    }

    public Usuario insertarUsuario(String nome, String contrasinal, String estado) {
        Usuario usuario = null;
        try {
            SecretKey secretKey = SymmetricCryptography.createAESKey();
            byte[] encryptedSecretKey = this.crypt.encryptKey(secretKey, this.publicKeyServidor);
            byte[] iv = SymmetricCryptography.createInitializationVector();
            String credenciaisEncoded = parseObject(new Credenciais(nome, contrasinal, estado));

            usuario = this.conexion.insertarUsuario(
                    encryptedSecretKey,
                    iv,
                    SymmetricCryptography.do_AESEncryption(credenciaisEncoded, secretKey, iv)
            );
        } catch (Exception e) {}
        return usuario;
    }

    public boolean enviarSolicitude(String solicitante, String nome) {
        boolean flag = false;
        try {
            byte[] iv = SymmetricCryptography.createInitializationVector();
            String mac = xerarMACClienteServidor(nome, this.secretKeyCliente, iv);
            flag = this.conexion.enviarSolicitude(
                    solicitante,
                    this.crypt.encryptText(nome, this.publicKeyServidor),
                    mac,
                    iv
            );
        } catch (Exception e) {
            fachadaAplicacion.muestraExcepcion("Imposible enviar a solicitude");
        }
        return flag;
    }

    public boolean aceptarSolicitude(String solicitante, String nome) {
        boolean flag = false;
        try {
            byte[] iv = SymmetricCryptography.createInitializationVector();
            String mac = xerarMACClienteServidor(nome, this.secretKeyCliente, iv);
            flag = this.conexion.aceptarSolicitude(
                    solicitante,
                    this.crypt.encryptText(nome, this.publicKeyServidor),
                    mac,
                    iv
            );
        } catch (Exception e) {
            fachadaAplicacion.muestraExcepcion("Imposible aceptar a solicitude");
        }
        return flag;
    }

    public boolean rexeitarSolicitude(String solicitante, String nome) {
        boolean flag = false;
        try {
            byte[] iv = SymmetricCryptography.createInitializationVector();
            String mac = xerarMACClienteServidor(nome, this.secretKeyCliente, iv);
            flag = this.conexion.rexeitarSolicitude(
                    solicitante,
                    this.crypt.encryptText(nome, this.publicKeyServidor),
                    mac,
                    iv
            );
        } catch (Exception e) {
            fachadaAplicacion.muestraExcepcion("Imposible rexeitar a solicitude");
        }
        return flag;
    }

    public Amigo obterAmigoConectado(Amigo amigo, byte[] key, byte[] iv) {
        Amigo amigoConectado = null;
        try {
            SecretKey secretKey = SymmetricCryptography.decryptKey(key, this.secretKeyCliente, iv);
            amigo.setSecretKey(secretKey);
            amigoConectado = amigo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return amigoConectado;
    }

    public List<Amigo> obterAmigoConectado(List<Amigo> amigos, List<byte[]> keys, List<byte[]> ivs) {
        List<Amigo> amigosConectados = null;
        try {
            for(int i = 0; i < amigos.size(); i++) {
                SecretKey secretKey = SymmetricCryptography.decryptKey(keys.get(i), this.secretKeyCliente, ivs.get(i));
                amigos.get(i).setSecretKey(secretKey);
            }
            amigosConectados = amigos;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return amigosConectados;
    }

    public String verificarEmisor(String mensaxe, String mac, SecretKey secretKey, byte[] iv) {
        String mensaxeVerificado = null;
        try {
            String mensaxeDecrypted = SymmetricCryptography.do_AESDecryption(mensaxe, secretKey, iv);
            String hash = SymmetricCryptography.do_AESDecryption(mac, secretKey, iv);
            String hashDecrypted = HashCryptography.computeHash(mensaxeDecrypted);
            if(hash.equals(hashDecrypted)) {
                mensaxeVerificado = mensaxeDecrypted;
            }
        } catch (Exception e) { }
        return mensaxeVerificado;
    }

    public boolean enviarMensaxe(Usuario usuarioActual, Amigo receptor, String mensaxe) {
        boolean foiEnviado = false;
        try {
            String hashMensaxe = HashCryptography.computeHash(mensaxe);
            byte[] iv = SymmetricCryptography.createInitializationVector();
            String mac = SymmetricCryptography.do_AESEncryption(hashMensaxe, receptor.getSecretKey(), iv);
            String encryptedMensaxe = SymmetricCryptography.do_AESEncryption(mensaxe, receptor.getSecretKey(), iv);
            receptor.getMensaxeRemoto().recibirMensaxe(usuarioActual.getNome(), encryptedMensaxe, mac, iv);
            foiEnviado = true;
        } catch (Exception e) {}
        return foiEnviado;
    }

    public List<UsuarioRelacion> buscarUsuarios(String busqueda, String nome) {
        List<UsuarioRelacion> resultado = null;
        try {
            byte[] iv = SymmetricCryptography.createInitializationVector();
            String mac = xerarMACClienteServidor(nome, this.secretKeyCliente, iv);
            resultado = this.conexion.buscarUsuarios(
                    busqueda,
                    this.crypt.encryptText(nome, this.publicKeyServidor),
                    mac,
                    iv
            );
        } catch (Exception e) {
            fachadaAplicacion.muestraExcepcion("Imposible realizar a busqueda");
        }
        return resultado;
    }
}
