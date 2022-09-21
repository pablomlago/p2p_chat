package GUI;

import Modelos.Usuario;
import Modelos.UsuarioRelacion;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class ModeloListaUsuariosRelacion extends AbstractTableModel {
    private List<UsuarioRelacion> usuariosRelacion;

    public ModeloListaUsuariosRelacion(){
        this.usuariosRelacion=new ArrayList<>();
    }
    public UsuarioRelacion getRow(int index){
        return usuariosRelacion.get(index);
    }

    public int getColumnCount (){
        return 2;
    }

    public int getRowCount(){
        return this.usuariosRelacion.size();
    }

    @Override
    public String getColumnName(int col){
        String nombre="";

        switch (col){
            case 0: nombre= "Nome"; break;
            case 1: nombre="Relacion";break;
        }
        return nombre;
    }

    private String tipoRelacion(UsuarioRelacion usuarioRelacion) {
        if(usuarioRelacion.isAmigo()) return "É amigo";
        if(usuarioRelacion.isSolicitado()) return "Solicitouselle amizado";
        if(usuarioRelacion.isPendente()) return "Solicitude pendente deste usuario";
        return "Non é amigo";
     }

    @Override
    public Class getColumnClass(int col){
        Class clase=null;

        switch (col){
            case 0: clase= java.lang.String.class; break;
            case 1: clase= java.lang.String.class; break;
        }
        return clase;
    }

    public Object getValueAt(int row, int col){
        Object resultado=null;

        switch (col){
            case 0: resultado= usuariosRelacion.get(row).getNome(); break;
            case 1: resultado= this.tipoRelacion(this.usuariosRelacion.get(row)); break;
        }
        return resultado;
    }

    @Override
    public boolean isCellEditable(int row, int col){
        return false;
    }

    public void setFilas(List<UsuarioRelacion> usuariosRelacion){
        this.usuariosRelacion=usuariosRelacion;
        fireTableDataChanged();
    }

    public void nuevoItem(UsuarioRelacion e){
        this.usuariosRelacion.add(e);
        fireTableRowsInserted(this.usuariosRelacion.size()-1, this.usuariosRelacion.size()-1);
    }

    public void borrarItem(int indice){
        this.usuariosRelacion.remove(indice);
        fireTableRowsDeleted(indice, indice);
    }

    public List<UsuarioRelacion> getFilas(){
        return this.usuariosRelacion;
    }
}