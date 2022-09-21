package Aplicacion.Cliente;

import BaseDatos.Cliente.FachadaClienteServidor;
import GUI.FachadaGUI;
import GUI.VPrincipalCliente;
import Modelos.*;
import RMI.MensaxeRemotoImpl;
import RMI.NotificacionsRemotoImpl;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class FachadaAplicacion {
    private final FachadaGUI fachadaGUI;
    private final FachadaClienteServidor fachadaBaseDatos;

    private Usuario usuarioActual;
    private final MensaxeRemotoImpl mensaxeRemoto;
    private final NotificacionsRemotoImpl notificacionsRemoto;
    private final ConcurrentHashMap<String, Amigo> amigos;
    private final ConcurrentHashMap<String, Solicitude> solicitudes;

    private VPrincipalCliente vPrincipalCliente;
    public FachadaAplicacion() {
        fachadaGUI = new FachadaGUI(this);
        fachadaBaseDatos = new FachadaClienteServidor(this);
        this.usuarioActual = null;
        this.mensaxeRemoto = null;
        this.notificacionsRemoto = null;
        this.vPrincipalCliente = null;
        this.amigos = new ConcurrentHashMap<>();
        this.solicitudes = new ConcurrentHashMap<>();
    }

    public static void main(String[] args) {
        FachadaAplicacion fachadaAplicacion;

        fachadaAplicacion = new FachadaAplicacion();
        fachadaAplicacion.iniciaInterfazUsuario();
    }

    public void iniciarVentanaPrincipal(Usuario usuarioActual) {
        this.usuarioActual = usuarioActual;
        this.vPrincipalCliente = new VPrincipalCliente(this, usuarioActual.getNome());
        this.switchFrame(VPrincipalCliente.iniciarVentana(this.vPrincipalCliente));
    }

    public void finalizarAplicacion() {
        if(usuarioActual != null) {
            this.fachadaBaseDatos.desconectarUsuario(usuarioActual.getNome());
        }
        System.exit(0);
    }

    public void notificarDesconexion(String usuario) {
        this.amigos.remove(usuario);
        if(this.vPrincipalCliente != null) {
            this.vPrincipalCliente.refrescarAmigosOnline();
            this.vPrincipalCliente.engadirNotificacion("Desconectouse " + usuario);
        }
    }

    public void notificarConexion(Amigo amigo, byte[] key, byte[] iv) {
        Amigo novoAmigo = this.fachadaBaseDatos.obterAmigoConectado(amigo, key, iv);
        if(novoAmigo != null) {
            this.amigos.put(novoAmigo.getNome(), novoAmigo);
            if(this.vPrincipalCliente != null) {
                this.vPrincipalCliente.refrescarAmigosOnline();
                this.vPrincipalCliente.engadirNotificacion("Conectouse " + amigo.getNome());
            }
        }
    }

    public void notificarConexion(List<Amigo> amigos, List<byte[]> keys, List<byte[]> ivs) {
        List<Amigo> novosAmigos = this.fachadaBaseDatos.obterAmigoConectado(amigos, keys, ivs);
        if(novosAmigos != null) {
            for(Amigo novoAmigo : novosAmigos) {
                this.amigos.put(novoAmigo.getNome(), novoAmigo);
            }
            if(vPrincipalCliente != null) {
                this.vPrincipalCliente.refrescarAmigosOnline();
            }
        }
    }

    public Usuario validarUsuario(String nome, String contrasinal) {
        return this.fachadaBaseDatos.validarUsuario(nome, contrasinal);
    }

    public Usuario insertarUsuario(String nome, String contrasinal, String estado) {
        return this.fachadaBaseDatos.insertarUsuario(nome, contrasinal, estado);
    }

    public void rexeitarSolicitude(String solicitante) {
        if(this.fachadaBaseDatos.rexeitarSolicitude(solicitante, usuarioActual.getNome())) {
            this.solicitudes.remove(solicitante);
        } else {
            this.muestraExcepcion("Imposible rexeitar a solicitude");
        }
    }

    public void aceptarSolicitude(String solicitante) {
        if(this.fachadaBaseDatos.aceptarSolicitude(solicitante, usuarioActual.getNome())) {
            this.solicitudes.remove(solicitante);
        } else {
            this.muestraExcepcion("Imposible aceptar a solicitude");
        }
    }

    public void enviarSolicitude(String solicitado) {
       if(!this.fachadaBaseDatos.enviarSolicitude(solicitado, usuarioActual.getNome())) {
           this.muestraExcepcion("Imposible enviar a solicitude");
       }
    }

    public List<UsuarioRelacion> buscarUsuarios(String busqueda) {
        return this.fachadaBaseDatos.buscarUsuarios(busqueda, usuarioActual.getNome());
    }

    public List<Amigo> obterAmigosConectados() {
        return new ArrayList<>(this.amigos.values());
    }

    public List<Solicitude> obterSolicitudesPendentes() {
        return new ArrayList<>(this.solicitudes.values());
    }

    public Usuario getUsuarioActual() {
        return this.usuarioActual;
    }

    public void pushFrame(JFrame frame) {
        this.fachadaGUI.pushFrame(frame);
    }

    public void popFrame() {
        this.fachadaGUI.popFrame();
    }

    public void switchFrame(JFrame frame) {
        this.fachadaGUI.switchFrame(frame);
    }

    public void iniciaInterfazUsuario(){
        fachadaGUI.iniciaVista();
    }

    public void muestraExcepcion(String e){
        fachadaGUI.muestraExcepcion(e);
    }

    public boolean enviarMensaxe(Amigo receptor, String mensaxe) {
        return this.fachadaBaseDatos.enviarMensaxe(this.usuarioActual, receptor, mensaxe);
    }

    public void recibirMensaxe(String emisor, String mensaxe, String mac, byte[] iv) {
        if(!amigos.containsKey(emisor)) return;
        String mensaxeDecrypted = this.fachadaBaseDatos.verificarEmisor(mensaxe, mac, amigos.get(emisor).getSecretKey(), iv);
        if(mensaxeDecrypted != null && this.vPrincipalCliente != null) {
            this.vPrincipalCliente.recibirMensaxe(emisor, mensaxeDecrypted);
        }
    }

    public void recibirSolicitude(String solicitante) {
        if(amigos.containsKey(solicitante)) return;
        this.solicitudes.put(solicitante, new Solicitude(solicitante));
        if(this.vPrincipalCliente != null) {
            this.vPrincipalCliente.engadirNotificacion(solicitante + " solicitou amizade!");
        }
    }

    public void recibirSolicitude(List<String> solicitantes) {
        for(String solicitante : solicitantes) {
            if(!amigos.containsKey(solicitante)) {
                this.solicitudes.put(solicitante, new Solicitude(solicitante));
            }
        }
    }
}
