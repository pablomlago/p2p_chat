package GUI;

import Aplicacion.Cliente.FachadaAplicacion;
import Modelos.Amigo;
import Modelos.Solicitude;
import Modelos.Usuario;
import RMI.NotificacionsRemoto;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class VPrincipalCliente {
    private final FachadaAplicacion fachadaAplicacion;
    private final String nomeUsuario;

    private final ConcurrentHashMap<String, StringBuilder> mensaxes;

    private final DefaultListModel notificacions;

    private final Object lockNotificacions;
    private final Object lockText;
    private final Object lockMensaxes;
    private final Object lockAmigos;

    private JPanel panel1;
    private JComboBox comboBox1;
    private JButton socialButton;
    private JButton buscarButton;
    private JTextField textField1;
    private JButton enviarButton;
    private JTextArea textArea1;
    private JLabel labelUsuario;
    private JButton button1;
    private JList list1;

    public void refrescarAmigosOnline() {
        List<Amigo> amigos = fachadaAplicacion.obterAmigosConectados();
        Amigo amigo = (Amigo) comboBox1.getSelectedItem();
        ComboBoxModel<Amigo> amigosOnline = new DefaultComboBoxModel<Amigo>(amigos.toArray(new Amigo[0]));
        synchronized (lockAmigos) {
            comboBox1.setModel(amigosOnline);
            if(amigos.size() > 0) {
                if(amigo == null || !amigos.contains(amigo)) amigo = amigos.get(0);
                comboBox1.setSelectedItem(amigo);
                if(!mensaxes.containsKey(amigo.getNome())) {
                    mensaxes.put(amigo.getNome(), new StringBuilder());
                }
                substituirTextoArea(mensaxes.get(amigo.getNome()).toString());
            } else {
                comboBox1.setSelectedItem(null);
                substituirTextoArea("");
            }
        }
    }
    public void engadirNotificacion(String notificacion) {
        synchronized (lockNotificacions) {
            this.notificacions.insertElementAt(notificacion, 0);
        }
    }
    private void engadirTextoArea(String texto) {
        synchronized (lockText) {
            this.textArea1.append(texto);
        }
    }
    private void substituirTextoArea(String texto) {
        synchronized (lockText) {
            this.textArea1.setText(texto);
        }
    }

    public void recibirMensaxe(String emisor, String mensaxe) {
        if(!mensaxes.containsKey(emisor)) { mensaxes.put(emisor, new StringBuilder()); }
        mensaxes.get(emisor).append(emisor+ "\n\t" + mensaxe + "\n");
        Amigo amigo = (Amigo) comboBox1.getSelectedItem();
        if(amigo != null) {
            if(emisor.equals(amigo.getNome())) {
                this.engadirTextoArea(emisor+ "\n\t" + mensaxe + "\n");
            } else {
                this.engadirNotificacion("Mensaxe de " + emisor);
            }
        }
    }

    private void initComponents() {
        this.labelUsuario.setText(this.nomeUsuario);
        this.list1.setModel(this.notificacions);
        this.refrescarAmigosOnline();

        for(Solicitude solicitude : fachadaAplicacion.obterSolicitudesPendentes()) {
            this.engadirNotificacion(solicitude.getEmisor()+" solicitou amizade");
        }

        buscarButton.addActionListener(actionEvent -> {
            VBuscar vb = new VBuscar(fachadaAplicacion);
            fachadaAplicacion.pushFrame(VBuscar.iniciarVentana(vb));
        });

        button1.addActionListener(actionEvent -> {
            this.refrescarAmigosOnline();
        });

        socialButton.addActionListener(actionEvent -> {
            VSocial vs = new VSocial(fachadaAplicacion);
            fachadaAplicacion.pushFrame(VSocial.iniciarVentana(vs));
        });

        enviarButton.addActionListener(actionEvent -> {
            Amigo amigo = (Amigo) comboBox1.getSelectedItem();
            if(amigo != null && !textField1.getText().isEmpty()) {
                if(this.fachadaAplicacion.enviarMensaxe(amigo, textField1.getText())) {
                    String mensaxe = fachadaAplicacion.getUsuarioActual() + "\n\t" + textField1.getText() +"\n";
                    if(!mensaxes.containsKey(amigo.getNome())) {
                        mensaxes.put(amigo.getNome(), new StringBuilder());
                    }
                    mensaxes.get(amigo.getNome()).append(mensaxe);
                    engadirTextoArea(mensaxe);
                    textField1.setText("");
                }
            }
        });
        comboBox1.addActionListener (actionEvent -> {
            Amigo amigo = (Amigo) comboBox1.getSelectedItem();
            if(amigo != null) {
                if(!mensaxes.containsKey(amigo.getNome())) {
                    mensaxes.put(amigo.getNome(), new StringBuilder());
                }
                substituirTextoArea(mensaxes.get(amigo.getNome()).toString());
            }
        });
    }

    public VPrincipalCliente(FachadaAplicacion fachadaAplicacion, String nomeUsuario) {
        this.fachadaAplicacion = fachadaAplicacion;
        this.nomeUsuario = nomeUsuario;
        this.mensaxes = new ConcurrentHashMap<>();
        this.notificacions = new DefaultListModel();

        this.lockMensaxes = new Object();
        this.lockText = new Object();
        this.lockNotificacions = new Object();
        this.lockAmigos = new Object();
        //Inicializamos os componentes graficos
        this.initComponents();
    }

    public static JFrame iniciarVentana(VPrincipalCliente vp) {
        JFrame frame = new JFrame("VPrincipalCliente");
        frame.setContentPane(vp.panel1);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.pack();

        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                vp.fachadaAplicacion.popFrame();
            }
        });
        return frame;
    }
}
