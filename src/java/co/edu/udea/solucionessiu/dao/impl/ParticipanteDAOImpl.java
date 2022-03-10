/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.ParticipanteDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.Participante;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author jorge.correaj
 */
public class ParticipanteDAOImpl extends JDBCConnectionPool implements ParticipanteDAO {
    
    private static final String OBTENER_POR_PROYECTO_Y_ROL = "SELECT * FROM sigap_participantes WHERE Proyecto = ? AND Rol = ? limit 1";
    private static final String COLUMNA_PERSONA = "Persona";
    private static final String COLUMNA_PROYECTO = "Proyecto";
    private static final String COLUMNA_ROL = "Rol";
    private static final String COLUMNA_RESPONSABILIDAD = "Responsabilidad";
    private static final String COLUMNA_DISPONIBILIDAD = "Disponibilidad";
    private static final String COLUMNA_PERMISOS = "Permisos";  
    private static final String ID_BASE_DATOS = "sigep";

    @Override
    public Participante obtenerPorProyectoYRol(String strIdProyecto, Integer intRol) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Participante participante = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_POR_PROYECTO_Y_ROL);
            ps.setString(1, strIdProyecto);
            ps.setInt(2, intRol);
            
            rs = ps.executeQuery();
            
            if (rs.next()){           
                    participante = new Participante();                    
                    participante.setPersona(rs.getString(COLUMNA_PERSONA));
                    participante.setProyecto(rs.getString(COLUMNA_PROYECTO));
                    participante.setRol(rs.getInt(COLUMNA_ROL));
                    participante.setResponsabilidad(rs.getString(COLUMNA_RESPONSABILIDAD));
                    participante.setDisponibilidad(rs.getInt(COLUMNA_DISPONIBILIDAD));
                    participante.setPermisos(rs.getString(COLUMNA_PERMISOS));                    
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
        
        return participante;
    }
}
