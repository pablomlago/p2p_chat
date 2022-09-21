package GUI;

import Aplicacion.Cliente.FachadaAplicacion;

import javax.swing.*;
import java.util.Stack;

public class FachadaGUI {
    private final FachadaAplicacion fachadaAplicacion;
    private final Stack<JFrame> stackFrames;
    private final Object lock;

    public FachadaGUI(FachadaAplicacion fachadaAplicacion){
        this.fachadaAplicacion = fachadaAplicacion;
        this.stackFrames = new Stack<>();
        this.lock = new Object();
    }

    public void iniciaVista(){
        VAutenticacion va = new VAutenticacion(fachadaAplicacion);
        this.pushFrame(VAutenticacion.iniciarVentana(va));
    }

    public void pushFrame(JFrame frame) {
        synchronized (lock) {
            if(!stackFrames.isEmpty()) {
                JFrame currentFrame = stackFrames.peek();
                currentFrame.setVisible(false);
            }
            stackFrames.add(frame);
            frame.setVisible(true);
        }
    }

    public void popFrame() {
        synchronized (lock) {
            JFrame currentFrame = stackFrames.pop();
            currentFrame.dispose();
            currentFrame.setVisible(false);

            if(stackFrames.isEmpty()) {
                this.fachadaAplicacion.finalizarAplicacion();
            } else {
                JFrame previousFrame = stackFrames.peek();
                previousFrame.setVisible(true);
            }
        }
    }

    public void switchFrame(JFrame frame) {
        synchronized (lock) {
            JFrame currentFrame = stackFrames.pop();
            currentFrame.dispose();
            currentFrame.setVisible(false);

            stackFrames.add(frame);
            frame.setVisible(true);
        }
    }

    public void switchFrame(String txtExcepcion) {
        synchronized (lock) {
            JFrame currentFrame = stackFrames.pop();
            currentFrame.dispose();
            currentFrame.setVisible(false);

            VAviso va = new VAviso(fachadaAplicacion, txtExcepcion);
            JFrame frame = VAviso.iniciarVentana(va);
            stackFrames.add(frame);
            frame.setVisible(true);
        }
    }

    public void muestraExcepcion(String txtExcepcion){
        VAviso va = new VAviso(fachadaAplicacion, txtExcepcion);
        fachadaAplicacion.pushFrame(VAviso.iniciarVentana(va));
    }
}
