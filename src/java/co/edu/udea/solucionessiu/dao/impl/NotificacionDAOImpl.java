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
    
    private static final String OBTENER_UNO = "SELECT * FROM soluciones_siu.tbl_notificaciones WHERE txtCodigo = ?";
    private static final String COLUMNA_CODIGO = "txtCodigo";
    private static final String COLUMNA_NOMBRE = "txtNombre";
    private static final String COLUMNA_RUTA_ARCHIVO = "txtRutaArchivo";
    private static final String COLUMNA_NOMBRE_DESTINATARIO = "txtNombreDestinatario";
    private static final String COLUMNA_EMAIL_DESTINATARIO = "txtEmailDestinatario";
    private static final String COLUMNA_DIAS_NOTIFICAR = "intDiasNotificar";
    private static final String COLUMNA_DIAS__DESPUES_NOTIFICAR = "intDiasDespuesNotificar";
    private static final String COLUMNA_NOMBRE_HOJA = "txtNombreHoja";

    @Override
    public Notificacion obtenerUno(String strCodigo) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Notificacion notificacion = null;
        
        try{
            con = getConexion();
            ps = con.prepareCall(OBTENER_UNO);
            ps.setString(1, strCodigo);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                
                    notificacion = new Notificacion();
                    
                    notificacion.setCodigo(rs.getString(COLUMNA_CODIGO));
                    notificacion.setNombre(rs.getString(COLUMNA_NOMBRE));
                    notificacion.setRuta(rs.getString(COLUMNA_RUTA_ARCHIVO));
                    notificacion.setNombreDestinario(rs.getString(COLUMNA_NOMBRE_DESTINATARIO));
                    notificacion.setEmailDestinario(rs.getString(COLUMNA_EMAIL_DESTINATARIO));
                    notificacion.setDiasNotificar(rs.getInt(COLUMNA_DIAS_NOTIFICAR));
                    notificacion.setDiasDespuesNotificar(rs.getInt(COLUMNA_DIAS__DESPUES_NOTIFICAR));
                    notificacion.setNombreHoja(rs.getString(COLUMNA_NOMBRE_HOJA));
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
