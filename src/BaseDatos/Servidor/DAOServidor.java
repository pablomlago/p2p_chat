package BaseDatos.Servidor;

import Aplicacion.Servidor.FachadaAplicacionServidor;
import Encriptacion.Contrasinal;
import Modelos.Usuario;
import Modelos.UsuarioRelacion;
import Modelos.UsuarioServidor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DAOServidor extends AbstractDAOServidor{
    public DAOServidor(FachadaAplicacionServidor fachadaAplicacionServidor, Connection conexion) {
        super(fachadaAplicacionServidor, conexion);
    }

    public Usuario validarUsuario(String nome, String contrasinal, byte[] key) {
        Usuario resultado = null;
        Connection con = this.getConexion();
        PreparedStatement stmtUsuario = null;
        ResultSet rsUsuario;
        String contrasinalAlmacenado;

        try{
            con.setAutoCommit(false);
            stmtUsuario = con.prepareStatement("select * from Usuarios where nome = ?");

            stmtUsuario.setString(1,nome);
            rsUsuario = stmtUsuario.executeQuery();
            if(rsUsuario.next()){
                contrasinalAlmacenado = rsUsuario.getString("contrasinal");
                if(Contrasinal.comprobarContrasinal(contrasinal, contrasinalAlmacenado)) {
                    stmtUsuario = con.prepareStatement("update Usuarios set key = ? where nome = ? ");
                    stmtUsuario.setBytes(1, key);
                    stmtUsuario.setString(2, nome);
                    stmtUsuario.executeUpdate();
                    resultado=new Usuario(rsUsuario.getString("nome"),rsUsuario.getString("estado"));
                }
            }
            con.commit();
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
            try{
                con.rollback();
            }catch(SQLException e2){
                System.out.println(e2.getMessage());
            }
        }   finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
        return resultado;
    }

    public List<UsuarioServidor> obterAmigosOnline(String nome) {
        List<UsuarioServidor> resultado = new ArrayList<>();
        Connection con = this.getConexion();
        PreparedStatement stmtUsuario;
        ResultSet rsUsuario;

        try{
            stmtUsuario = con.prepareStatement("select us.nome, us.estado, us.key " +
                    "from Usuarios as us join Amigos as am on us.nome = am.amigo " +
                    "where am.usuario = ? and us.key is not null ");

            stmtUsuario.setString(1, nome);

            rsUsuario = stmtUsuario.executeQuery();

            while(rsUsuario.next()) {
                resultado.add(new UsuarioServidor(rsUsuario.getString("nome"), rsUsuario.getString("estado"), rsUsuario.getBytes("key")));
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }

        return resultado;
    }

    public Usuario insertarUsuario(String nome, String contrasinal, String estado) {
        Usuario resultado = null;
        Connection con = this.getConexion();
        PreparedStatement stmtUsuario;
        PreparedStatement stmtIdUsuario;
        ResultSet rsCheck;

        try{
            con.setAutoCommit(false);

            stmtIdUsuario = con.prepareStatement("select nome from Usuarios where nome = ?");
            stmtIdUsuario.setString(1, nome);
            rsCheck = stmtIdUsuario.executeQuery();
            if(rsCheck.next()) {
                return null;
            }

            stmtUsuario=con.prepareStatement("insert into Usuarios(nome, contrasinal, estado) values (?,?,?)");
            stmtUsuario.setString(1, nome);
            stmtUsuario.setString(2, contrasinal);
            stmtUsuario.setString(3, estado);
            stmtUsuario.executeUpdate();

            con.commit();

            resultado = new Usuario(nome, estado);
        }catch (SQLException e){
            e.printStackTrace();
            try{
                con.rollback();
            }catch(SQLException e2){
                e2.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {}
        }
        return resultado;
    }

    public UsuarioServidor obterUsuarioServidor(String nome) {
        UsuarioServidor resultado = null;
        Connection con = this.getConexion();
        PreparedStatement stmtUsuario;
        ResultSet rsUsuario;
        try{
            stmtUsuario = con.prepareStatement("select * " +
                    "from Usuarios " +
                    "where nome = ? ");

            stmtUsuario.setString(1, nome);
            rsUsuario = stmtUsuario.executeQuery();
            if(rsUsuario.next()) {
                resultado = new UsuarioServidor(rsUsuario.getString("nome"), rsUsuario.getString("estado"), rsUsuario.getBytes("key"));
            }
        }catch(SQLException ex){
            System.out.println(ex.getMessage());
        }
        return resultado;
    }

    public void desconectarUsuario(String nome) {
        Connection con = this.getConexion();
        PreparedStatement stmtUsuario;

        try{
            stmtUsuario=con.prepareStatement("update usuarios set key = null where nome = ? ;");
            stmtUsuario.setString(1, nome);
            stmtUsuario.executeUpdate();
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    public boolean rexeitarSolicitude(String solicitante, String nome) {
        Connection con = this.getConexion();
        PreparedStatement stmtUsuario;
        boolean resultado = false;
        try{
            stmtUsuario=con.prepareStatement("delete from solicitudes " +
                    "where emisor = ? and receptor = ?");
            stmtUsuario.setString(1, solicitante);
            stmtUsuario.setString(2, nome);
            resultado = stmtUsuario.executeUpdate() > 0;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultado;
    }

    public boolean aceptarSolicitude(String solicitante, String nome) {
        Connection con = this.getConexion();
        PreparedStatement stmtUsuario;
        boolean resultado = false;
        try{
            con.setAutoCommit(false);

            stmtUsuario=con.prepareStatement("delete from solicitudes " +
                    "where emisor = ? and receptor = ?");
            stmtUsuario.setString(1, solicitante);
            stmtUsuario.setString(2, nome);

            if(stmtUsuario.executeUpdate() == 0)
                throw new SQLException();

            stmtUsuario=con.prepareStatement("insert into amigos " +
                    "values (?, ?)");
            stmtUsuario.setString(1, solicitante);
            stmtUsuario.setString(2, nome);
            stmtUsuario.executeUpdate();

            stmtUsuario.setString(1, nome);
            stmtUsuario.setString(2, solicitante);
            int rows = stmtUsuario.executeUpdate();

            con.commit();
            resultado = rows > 0;
        }catch (SQLException e){
            e.printStackTrace();
            try{
                con.rollback();
            }catch(SQLException e2){
                e2.printStackTrace();
            }
        } finally {
            try {
                con.setAutoCommit(true);
            } catch (SQLException e) {}
        }
        return resultado;
    }

    public boolean enviarSolicitude(String solicitude, String nome) {
        Connection con = this.getConexion();
        PreparedStatement stmtUsuario;
        boolean resultado = false;
        try{
            stmtUsuario=con.prepareStatement("insert into solicitudes " +
                    "values (?, ?)");
            stmtUsuario.setString(1, nome);
            stmtUsuario.setString(2, solicitude);
            resultado = stmtUsuario.executeUpdate() > 0;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return resultado;
    }


    public List<UsuarioRelacion> buscarUsuarios(String nome, String busqueda) {
        List<UsuarioRelacion> resultado = new ArrayList<>();
        Connection con = this.getConexion();
        PreparedStatement stmtUsuario;
        ResultSet rsUsuario;

        try{
            stmtUsuario = con.prepareStatement("select us.nome, us.estado, "+
                    "case when am.amigo is NULL then False else True end as amigo, "+
                    "case when so.emisor = ? then True else False end as solicitado, "+
                    "case when so.receptor = ? then True else False end as pendente "+
                    "from (usuarios as us left join amigos as am on us.nome = am.amigo and am.usuario = ?) "+
                    "left join solicitudes as so on us.nome = so.receptor and so.emisor = ? or us.nome = so.emisor "+
                    "where us.nome != ? and us.nome like ? "
            );

            stmtUsuario.setString(1, nome);
            stmtUsuario.setString(2, nome);
            stmtUsuario.setString(3, nome);
            stmtUsuario.setString(4, nome);
            stmtUsuario.setString(5, nome);
            stmtUsuario.setString(6, '%'+busqueda+'%');

            rsUsuario = stmtUsuario.executeQuery();

            while(rsUsuario.next()) {
                resultado.add(new UsuarioRelacion(rsUsuario.getString("nome"), rsUsuario.getString("estado"),
                        rsUsuario.getBoolean("amigo"), rsUsuario.getBoolean("solicitado"), rsUsuario.getBoolean("pendente")));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return resultado;
    }


    public List<String> obterSolicitantes(String nome) {
        List<String> resultado = new ArrayList<>();
        Connection con = this.getConexion();
        PreparedStatement stmtUsuario;
        ResultSet rsUsuario;
        try{
            stmtUsuario = con.prepareStatement("select us.nome, us.estado " +
                    "from Usuarios as us join Solicitudes as so on us.nome = so.emisor " +
                    "where so.receptor = ? ");

            stmtUsuario.setString(1, nome);
            rsUsuario = stmtUsuario.executeQuery();
            while(rsUsuario.next()) {
                resultado.add(rsUsuario.getString("nome"));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return resultado;
    }
}
