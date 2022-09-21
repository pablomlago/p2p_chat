package GUI;

import Aplicacion.Cliente.FachadaAplicacion;
import Modelos.Solicitude;
import Modelos.Usuario;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VSocial {
    private final FachadaAplicacion fachadaAplicacion;

    private JPanel panel1;
    private JComboBox comboBox1;
    private JTextArea textArea1;
    private JButton button1;
    private JButton atrasButton;
    private JList list1;
    private JButton button2;

    private final DefaultListModel<Solicitude> modeloSolicitudes;

    private void refrescarLista() {
        String opcion = (String) this.comboBox1.getSelectedItem();
        switch (opcion) {
            case "Solicitudes pendentes":
                this.modeloSolicitudes.clear();
                this.modeloSolicitudes.addAll(this.fachadaAplicacion.obterSolicitudesPendentes());
                this.button1.setEnabled(false);
                this.button2.setEnabled(false);
                break;
        }
    }

    private void initComponents() {
        atrasButton.addActionListener(actionEvent -> {
            fachadaAplicacion.popFrame();
        });

        comboBox1.addItem("Solicitudes pendentes");

        this.button1.setText("Aceptar");
        this.button1.setEnabled(false);
        this.button2.setText("Rexeitar");
        this.button2.setEnabled(false);

        this.modeloSolicitudes.addAll(this.fachadaAplicacion.obterSolicitudesPendentes());
        this.list1.setModel(modeloSolicitudes);

        comboBox1.addActionListener(actionEvent -> {
            this.refrescarLista();
        });

        button1.addActionListener(actionEvent -> {
            Solicitude solicitude = (Solicitude) list1.getSelectedValue();
            if(solicitude != null) {
                String opcion = (String) this.comboBox1.getSelectedItem();
                switch (opcion) {
                    case "Solicitudes pendentes":
                        this.fachadaAplicacion.aceptarSolicitude(solicitude.getEmisor());
                        break;
                }
                this.refrescarLista();
            }
        });

        button2.addActionListener(actionEvent -> {
            Solicitude solicitude = (Solicitude) list1.getSelectedValue();
            if(solicitude != null) {
                String opcion = (String) this.comboBox1.getSelectedItem();
                switch (opcion) {
                    case "Solicitudes pendentes":
                        this.fachadaAplicacion.rexeitarSolicitude(solicitude.getEmisor());
                        break;
                }
                this.refrescarLista();
            }
        });

        list1.getSelectionModel().addListSelectionListener(actionEvent -> {
            Solicitude solicitude = (Solicitude) list1.getSelectedValue();
            if(solicitude != null) {
                this.button1.setEnabled(true);
                this.button2.setEnabled(true);
            } else {
                this.button1.setEnabled(false);
                this.button2.setEnabled(false);
            }
        });
    }

    public VSocial(FachadaAplicacion fachadaAplicacion) {
        this.fachadaAplicacion = fachadaAplicacion;
        this.modeloSolicitudes = new DefaultListModel<>();
        //Inicializamos os componentes graficos
        this.initComponents();
    }

    public static JFrame iniciarVentana(VSocial vs) {
        JFrame frame = new JFrame("VSocial");
        frame.setContentPane(vs.panel1);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.pack();

        frame.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                vs.fachadaAplicacion.popFrame();
            }
        });

        return frame;
    }
}
