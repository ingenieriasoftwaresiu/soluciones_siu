/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.NotificacionDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author jorge.correa
 */
public class NotificacionDAOImpl extends JDBCConnectionPool implements NotificacionDAO{
    
    private static final String BD_SIUWEB_OBTENER_UNO = "SELECT * FROM soluciones_siu.tbl_notificaciones WHERE txtCodigo = ?";
    private static final String BD_SIUWEB_COLUMNA_CODIGO = "txtCodigo";
    private static final String BD_SIUWEB_COLUMNA_NOMBRE = "txtNombre";
    private static final String BD_SIUWEB_COLUMNA_RUTA_ARCHIVO = "txtRutaArchivo";
    private static final String BD_SIUWEB_COLUMNA_NOMBRE_DESTINATARIO = "txtNombreDestinatario";
    private static final String BD_SIUWEB_COLUMNA_EMAIL_DESTINATARIO = "txtEmailDestinatario";
    private static final String BD_SIUWEB_COLUMNA_DIAS_NOTIFICAR = "intDiasNotificar";
    private static final String BD_SIUWEB_COLUMNA_DIAS__DESPUES_NOTIFICAR = "intDiasDespuesNotificar";
    private static final String BD_SIUWEB_COLUMNA_NOMBRE_HOJA = "txtNombreHoja";
    
    private static final String BD_SIGEP_OBTENER_UNO = "SELECT * from sigap.sigap_notificaciones WHERE codigo = ?";
    private static final String BD_SIGEP_COLUMNA_CODIGO = "codigo";
    private static final String BD_SIGEP_COLUMNA_NOMBRE = "nombre";
    private static final String BD_SIGEP_COLUMNA_NOMBRE_DESTINATARIO = "nombreDestinatario";
    private static final String BD_SIGEP_COLUMNA_EMAIL_DESTINATARIO = "emailDestinatario";
    private static final String BD_SIGEP_COLUMNA_DIAS_PREVIOS_NOTIFICACION = "diasPreviosNotificacion";

    @Override
    public Notificacion obtenerUno(String strCodigo) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Notificacion notificacion = null;
        String strIdBD = "siuweb";
        
        try{
            con = getConexion(strIdBD);
            ps = con.prepareCall(BD_SIUWEB_OBTENER_UNO);
            ps.setString(1, strCodigo);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                
                    notificacion = new Notificacion();
                    
                    notificacion.setCodigo(rs.getString(BD_SIUWEB_COLUMNA_CODIGO));
                    notificacion.setNombre(rs.getString(BD_SIUWEB_COLUMNA_NOMBRE));
                    notificacion.setRuta(rs.getString(BD_SIUWEB_COLUMNA_RUTA_ARCHIVO));
                    notificacion.setNombreDestinatario(rs.getString(BD_SIUWEB_COLUMNA_NOMBRE_DESTINATARIO));
                    notificacion.setEmailDestinatario(rs.getString(BD_SIUWEB_COLUMNA_EMAIL_DESTINATARIO));
                    notificacion.setDiasNotificar(rs.getInt(BD_SIUWEB_COLUMNA_DIAS_NOTIFICAR));
                    notificacion.setDiasDespuesNotificar(rs.getInt(BD_SIUWEB_COLUMNA_DIAS__DESPUES_NOTIFICAR));
                    notificacion.setNombreHoja(rs.getString(BD_SIUWEB_COLUMNA_NOMBRE_HOJA));
            }
            
        }catch(SQLException e){
            throw new GIDaoException(e);
        }finally{
            try{
                
                if (rs != null){
                    rs.close();
                }
                
                 if (ps != null){
                    ps.close();
                }
                 
                  if (con != null){
                    con.close();
                }
                  
            }catch(SQLException e){
                throw new GIDaoException(e);
            }
        }
        
        return notificacion;
    }
    
    @Override
    public Notificacion obtenerUna(String strCodigo) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Notificacion notificacion = null;
        String strIdBD = "sigep";
        
        try{
            con = getConexion(strIdBD);
            ps = con.prepareCall(BD_SIGEP_OBTENER_UNO);
            ps.setString(1, strCodigo);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                
                    notificacion = new Notificacion();                    
                    notificacion.setCodigo(rs.getString(BD_SIGEP_COLUMNA_CODIGO));
                    notificacion.setNombre(rs.getString(BD_SIGEP_COLUMNA_NOMBRE));     
                    notificacion.setNombreDestinatario(rs.getString(BD_SIGEP_COLUMNA_NOMBRE_DESTINATARIO));
                    notificacion.setEmailDestinatario(rs.getString(BD_SIGEP_COLUMNA_EMAIL_DESTINATARIO));
                    notificacion.setDiasPreviosNotificacion(rs.getInt(BD_SIGEP_COLUMNA_DIAS_PREVIOS_NOTIFICACION));                    
            }
            
        }catch(SQLException e){
            throw new GIDaoException(e);
        }finally{
            try{
                
                if (rs != null){
                    rs.close();
                }
                
                 if (ps != null){
                    ps.close();
                }
                 
                  if (con != null){
                    con.close();
                }
                  
            }catch(SQLException e){
                throw new GIDaoException(e);
            }
        }
        
        return notificacion;
    }
    
}
