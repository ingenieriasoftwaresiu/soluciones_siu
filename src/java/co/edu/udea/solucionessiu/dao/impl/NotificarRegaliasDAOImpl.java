/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.NotificarRegaliasDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.ParticipanteRegalias;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jorge.correaj
 */
public class NotificarRegaliasDAOImpl extends JDBCConnectionPool implements NotificarRegaliasDAO {
    private static final String OBTENER_POR_ESTADO = "SELECT * FROM sigap.sigap_notificarregalias WHERE estado = ?";
    private static final String COLUMNA_CORREO = "correo";
    private static final String COLUMNA_NOMBRE = "nombre";
    private static final String COLUMNA_ESTADO = "estado";
    private static final String ID_BASE_DATOS = "sigep";

    @Override
    public List<ParticipanteRegalias> obtenerPorEstado(String strIdEstado) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ParticipanteRegalias> objetos = new ArrayList<ParticipanteRegalias>();
        ParticipanteRegalias objeto = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_POR_ESTADO);
            ps.setString(1, strIdEstado);
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){
                    objeto = new ParticipanteRegalias();
                    
                    objeto.setCorreo(rs.getString(COLUMNA_CORREO));
                    objeto.setNombre(rs.getString(COLUMNA_NOMBRE));
                    objeto.setEstado(rs.getString(COLUMNA_ESTADO));
                    objetos.add(objeto);
                }
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
        
        return objetos;
    }
}
