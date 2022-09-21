package GUI;

import Aplicacion.Cliente.FachadaAplicacion;
import Encriptacion.Contrasinal;
import Modelos.Usuario;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class VRexistro {
    private final FachadaAplicacion fachadaAplicacion;

    private JPanel panel1;
    private JTextField textField1;
    private JPasswordField passwordField1;
    private JPasswordField passwordField2;
    private JTextField textField2;
    private JButton rexistrarseButton;
    private JButton atrasButton;

    private void initComponents() {
        rexistrarseButton.addActionListener(actionEvent -> {
            if(textField1.getText().isEmpty()) {
                fachadaAplicacion.muestraExcepcion("Introduce un nome de usuario");
                return;
            }
            if(passwordField1.getPassword().length == 0) {
                fachadaAplicacion.muestraExcepcion("Introduce unha contrasinal");
                return;
            }
            if(passwordField2.getPassword().length == 0) {
                fachadaAplicacion.muestraExcepcion("Introduce a confirmacion do contrasinal");
                return;
            }
            if(!Arrays.equals(passwordField1.getPassword(), passwordField2.getPassword())) {
                fachadaAplicacion.muestraExcepcion("As contrasinais non coinciden");
                return;
            }

            Usuario usuario = fachadaAplicacion.insertarUsuario(textField1.getText(), Contrasinal.hashContrasinal(String.valueOf(passwordField1.getPassword())), textField2.getText());
            if(usuario != null) {
                VAviso va = new VAviso(fachadaAplicacion, "Usuario rexistrado correctamente");
                fachadaAplicacion.switchFrame(VAviso.iniciarVentana(va));
            } else {
                fachadaAplicacion.muestraExcepcion("Imposible rexistrar o usuario");
            }
        });

        atrasButton.addActionListener(actionEvent -> {
            fachadaAplicacion.popFrame();
        });
    }

    public VRexistro(FachadaAplicacion fachadaAplicacion) {
        this.fachadaAplicacion = fachadaAplicacion;
        //Inicializamos os componentes graficos
        this.initComponents();
    }

    public static JFrame iniciarVentana(VRexistro vr) {
        JFrame frame = new JFrame("VRexistro");
        frame.setContentPane(vr.panel1);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.pack();

        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                vr.fachadaAplicacion.popFrame();
            }
        });

        return frame;
    }
}
