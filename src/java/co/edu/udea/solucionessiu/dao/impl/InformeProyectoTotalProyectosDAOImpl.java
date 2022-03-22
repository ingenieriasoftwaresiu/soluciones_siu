/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.InformeProyectoTotalProyectosDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.InformeProyectoTotalProyectos;
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
public class InformeProyectoTotalProyectosDAOImpl extends JDBCConnectionPool implements InformeProyectoTotalProyectosDAO {
    
    private static final String OBTENER_UNO = "SELECT * FROM totalproyectos.reports r WHERE r.id = ?";
    private static final String OBTENER_POR_PROYECTO = "SELECT * FROM totalproyectos.reports r WHERE r.projectid = ?";
    private static final String COLUMNA_ID = "id";    
    private static final String COLUMNA_ID_PROYECTO = "projectid";
    private static final String COLUMNA_NOMBRE = "name";
    private static final String COLUMNA_FECHA_REPORTE = "reportdate";
    private static final String ID_BASE_DATOS = "totalproyectos";
    
    @Override
    public InformeProyectoTotalProyectos obtenerUno(Integer intId) throws GIDaoException {
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        InformeProyectoTotalProyectos informe = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_UNO);
            ps.setInt(1, intId);
            
            rs = ps.executeQuery();
            
            if (rs.next()){            
                
                informe = new InformeProyectoTotalProyectos();  
                informe.setId(rs.getInt(COLUMNA_ID));
                informe.setProjectid(rs.getInt(COLUMNA_ID_PROYECTO));
                informe.setName(rs.getString(COLUMNA_NOMBRE));
                informe.setReportdate(rs.getString(COLUMNA_FECHA_REPORTE));                                             
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
        
        return informe;
    }

    @Override
    public List<InformeProyectoTotalProyectos> obtenerPorProyecto(Integer intIdProyecto) throws GIDaoException {
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<InformeProyectoTotalProyectos> informes = new ArrayList<InformeProyectoTotalProyectos>();
        InformeProyectoTotalProyectos informe = null;
               
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_POR_PROYECTO);
            ps.setInt(1, intIdProyecto);           
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){
                    informe = new InformeProyectoTotalProyectos();                    
                    informe.setId(rs.getInt(COLUMNA_ID));
                    informe.setProjectid(rs.getInt(COLUMNA_ID_PROYECTO));
                    informe.setName(rs.getString(COLUMNA_NOMBRE));
                    informe.setReportdate(rs.getString(COLUMNA_FECHA_REPORTE)); 
                    informes.add(informe);
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
        
        return informes;
    }
    
}
