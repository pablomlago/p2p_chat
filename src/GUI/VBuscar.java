package GUI;

import Aplicacion.Cliente.FachadaAplicacion;
import Modelos.UsuarioRelacion;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class VBuscar {
    private final FachadaAplicacion fachadaAplicacion;
    private List<UsuarioRelacion> usuariosBusqueda;
    private final ModeloListaUsuariosRelacion modeloListaUsuariosRelacion;

    private JPanel panel1;
    private JTextField textField1;
    private JButton buscarButton;
    private JTextArea textArea1;
    private JButton button1;
    private JButton atrasButton;
    private JTable table1;
    private JButton button2;

    private void accionBoton1() {
        if(table1.getSelectedRow() != -1) {
            UsuarioRelacion usuarioRelacion = ((ModeloListaUsuariosRelacion)table1.getModel()).getRow(table1.getSelectedRow());
            if(usuarioRelacion.isAmigo()) {
                // SEN IMPLEMENTAR
            } else if(usuarioRelacion.isSolicitado()) {
                // SEN IMPLEMENTAR
            } else if(usuarioRelacion.isPendente())  {
                this.fachadaAplicacion.aceptarSolicitude(usuarioRelacion.getNome());
            } else {
                this.fachadaAplicacion.enviarSolicitude(usuarioRelacion.getNome());
            }
            this.refrescarTabla();
        } else {
            this.button1.setEnabled(false);
        }
    }

    private void accionBoton2() {
        if(table1.getSelectedRow() != -1) {
            UsuarioRelacion usuarioRelacion = ((ModeloListaUsuariosRelacion)table1.getModel()).getRow(table1.getSelectedRow());
            if(usuarioRelacion.isPendente())  {
                this.fachadaAplicacion.rexeitarSolicitude(usuarioRelacion.getNome());
            }
            this.refrescarTabla();
        } else {
            this.button1.setEnabled(false);
        }
    }

    private void refrescarTabla() {
        this.button1.setEnabled(false);
        this.usuariosBusqueda = fachadaAplicacion.buscarUsuarios(textField1.getText());
        if(this.usuariosBusqueda != null) {
            this.modeloListaUsuariosRelacion.setFilas(this.usuariosBusqueda);
        }
    }

    private void initComponents() {
        atrasButton.addActionListener(actionEvent -> {
            fachadaAplicacion.popFrame();
        });

        buscarButton.addActionListener(actionEvent -> {
            this.refrescarTabla();
        });

        button1.addActionListener(actionEvent -> {
            this.accionBoton1();
        });

        button2.addActionListener(actionEvent -> {
            this.accionBoton2();
        });

        table1.setModel(this.modeloListaUsuariosRelacion);

        table1.getSelectionModel().addListSelectionListener(actionEvent -> {
            if(table1.getSelectedRow() != -1) {
                UsuarioRelacion usuarioRelacion = ((ModeloListaUsuariosRelacion)table1.getModel()).getRow(table1.getSelectedRow());
                textArea1.setText(usuarioRelacion.getEstado());
                if(usuarioRelacion.isAmigo()) {
                    this.button1.setText("Desamigar");
                    this.button1.setEnabled(false);
                } else if(usuarioRelacion.isSolicitado()) {
                    this.button1.setText("Desolicitar");
                    this.button1.setEnabled(false);
                } else if(usuarioRelacion.isPendente())  {
                    this.button1.setText("Aceptar");
                    this.button1.setEnabled(true);
                } else {
                    this.button1.setText("Solicitar");
                    this.button1.setEnabled(true);
                }
                //Boton 2
                if(usuarioRelacion.isPendente()) {
                    this.button2.setText("Rexeitar");
                    this.button2.setEnabled(true);
                } else {
                    this.button2.setText("-");
                    this.button2.setEnabled(false);
                }
            } else {
                this.button1.setEnabled(false);
                this.button2.setEnabled(false);
            }
        });
    }

    public VBuscar(FachadaAplicacion fachadaAplicacion) {
        this.fachadaAplicacion = fachadaAplicacion;
        this.usuariosBusqueda = new ArrayList<>();
        this.modeloListaUsuariosRelacion = new ModeloListaUsuariosRelacion();
        //Inicializamos os componentes graficos
        this.initComponents();
    }

    public static JFrame iniciarVentana(VBuscar vb) {
        JFrame frame = new JFrame("VBuscar");
        frame.setContentPane(vb.panel1);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.pack();

        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                vb.fachadaAplicacion.popFrame();
            }
        });

        return frame;
    }
}
