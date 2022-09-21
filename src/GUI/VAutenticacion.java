package GUI;

import Aplicacion.Cliente.FachadaAplicacion;
import Modelos.Usuario;
import Modelos.UsuarioServidor;
import RMI.MensaxeRemotoImpl;
import RMI.NotificacionsRemotoImpl;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;

public class VAutenticacion {
    private final FachadaAplicacion fachadaAplicacion;

    private JPanel panel1;
    private JTextField textField1;
    private JButton entrarButton;
    private JButton rexistrarseButton;
    private JPasswordField passwordField1;

    private void initComponents() {
        entrarButton.addActionListener(actionEvent -> {
            if(textField1.getText().isEmpty()) {
                fachadaAplicacion.muestraExcepcion("Introduce un nome de usuario");
                return;
            }
            if(passwordField1.getPassword().length == 0) {
                fachadaAplicacion.muestraExcepcion("Introduce unha contrasinal");
                return;
            }
            Usuario usuario = fachadaAplicacion.validarUsuario(textField1.getText(), String.valueOf(passwordField1.getPassword()));
            if(usuario != null) {
                this.fachadaAplicacion.iniciarVentanaPrincipal(usuario);
            } else {
                fachadaAplicacion.muestraExcepcion("Imposible autenticar o usuario");
            }
        });

        rexistrarseButton.addActionListener(actionEvent -> {
            VRexistro vr = new VRexistro(fachadaAplicacion);
            fachadaAplicacion.pushFrame(VRexistro.iniciarVentana(vr));
        });
    }

    public VAutenticacion(FachadaAplicacion fachadaAplicacion) {
        this.fachadaAplicacion = fachadaAplicacion;
        //Inicializamos os componentes graficos
        this.initComponents();
    }

    public static JFrame iniciarVentana(VAutenticacion va) {
        JFrame frame = new JFrame("VAutenticacion");
        frame.setContentPane(va.panel1);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.pack();

        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                va.fachadaAplicacion.popFrame();
            }
        });

        return frame;
    }
}
