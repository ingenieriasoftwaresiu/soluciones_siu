/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.ReservasNotificadasDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.ReservaNotificada;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author jorge.correaj
 */
public class ReservasNotificadasDAOImpl extends JDBCConnectionPool implements ReservasNotificadasDAO {
    private static final String OBTENER_UNO = "SELECT * from sigap.sigap_reservas_notificadas WHERE codigoReserva = ?";    
    private static final String INSERTAR = "INSERT INTO sigap.sigap_reservas_notificadas(codigoReserva,fecha) VALUES(?,?)";
    private static final String COLUMNA_CODIGO = "codigoReserva";
    private static final String COLUMNA_FECHA = "fecha";
    private static final String ID_BASE_DATOS = "sigep";

    @Override
    public ReservaNotificada obtenerUna(String strCodigoReserva) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ReservaNotificada reserva = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_UNO);
            ps.setString(1, strCodigoReserva);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                
                   reserva = new ReservaNotificada();                    
                   reserva.setCodigoReserva(rs.getString(COLUMNA_CODIGO));
                   reserva.setFechaNotificacion(rs.getDate(COLUMNA_FECHA));                
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
        
        return reserva;
    }

    @Override
    public void insertar(ReservaNotificada reserva) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        java.sql.Date dtFecha = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(INSERTAR);
            ps.setString(1, reserva.getCodigoReserva());        
            dtFecha = new java.sql.Date(reserva.getFechaNotificacion().getTime());
            ps.setDate(2, dtFecha);
            ps.executeUpdate();
            
        }catch(SQLException e){
            throw new GIDaoException(e);
        }finally{
            try{                                              
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
    }    
}
