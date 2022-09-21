package GUI;

import Aplicacion.Cliente.FachadaAplicacion;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VAviso {
    private final FachadaAplicacion fachadaAplicacion;

    private JPanel panel1;
    private JTextArea textArea1;
    private JButton cerrarButton;

    private void initComponents(String erro) {
        this.textArea1.setText(erro);

        cerrarButton.addActionListener(actionEvent -> {
            fachadaAplicacion.popFrame();
        });
    }

    public VAviso(FachadaAplicacion fachadaAplicacion, String erro) {
        this.fachadaAplicacion = fachadaAplicacion;
        //Inicializamos os componentes graficos
        this.initComponents(erro);
    }

    public static JFrame iniciarVentana(VAviso va) {
        JFrame frame = new JFrame("VAviso");
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
