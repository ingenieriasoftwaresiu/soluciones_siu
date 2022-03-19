/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.UsuarioTotalProyectosDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.ProyectoTotalProyectos;
import co.edu.udea.solucionessiu.dto.UsuarioTotalProyectos;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author jorge.correaj
 */
public class UsuarioTotalProyectosDAOImpl extends JDBCConnectionPool implements UsuarioTotalProyectosDAO{
    
    private static final String OBTENER_UNO = "SELECT * FROM totalproyectos.projects p WHERE p.id = ?";
    private static final String COLUMNA_ID = "id";    
    private static final String COLUMNA_NOMBRE_USUARIO = "username";
    private static final String COLUMNA_EMAIL = "email";
    private static final String COLUMNA_PRIMER_NOMBRE = "firstname";
    private static final String COLUMNA_ULTIMO_NOMBRE = "lastname";
    private static final String COLUMNA_GRUPOS = "_groups";
    private static final String ID_BASE_DATOS = "totalproyectos";

    @Override
    public UsuarioTotalProyectos obtenerUno(Integer intId) throws GIDaoException {
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        UsuarioTotalProyectos usuario = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_UNO);
            ps.setInt(1, intId);
            
            rs = ps.executeQuery();
            
            if (rs.next()){            
                
                usuario = new UsuarioTotalProyectos();  
                usuario.setId(rs.getInt(COLUMNA_ID));
                usuario.setUsername(rs.getString(COLUMNA_NOMBRE_USUARIO));
                usuario.setFirstname(rs.getString(COLUMNA_PRIMER_NOMBRE));
                usuario.setLastname(rs.getString(COLUMNA_ULTIMO_NOMBRE));
                usuario.setEmail(rs.getString(COLUMNA_EMAIL));
                usuario.setGroups(rs.getString(COLUMNA_GRUPOS));                               
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
        
        return usuario;

    }
    
}
