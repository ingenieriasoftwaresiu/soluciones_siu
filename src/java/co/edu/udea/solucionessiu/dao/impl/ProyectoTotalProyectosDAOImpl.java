/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.ProyectoTotalProyectosDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.ProyectoSIGEP;
import co.edu.udea.solucionessiu.dto.ProyectoTotalProyectos;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jorge.correaj
 */
public class ProyectoTotalProyectosDAOImpl extends JDBCConnectionPool implements ProyectoTotalProyectosDAO {
    
    private static final String OBTENER_UNO = "SELECT * FROM totalproyectos.projects p WHERE p.id = ?";
    private static final String OBTENER_ACTIVOS = "SELECT * FROM totalproyectos.projects p WHERE (p.statusid = ? OR p.statusid = ?);";
    private static final String OBTENER_FINALIZADOS = "SELECT * FROM totalproyectos.projects p WHERE (p.statusid = ?);";
    private static final String ACTUALIZAR_ESTADO = "UPDATE totalproyectos.projects p SET p.statusid = ? WHERE (p.id = ?);";
    private static final String COLUMNA_ID = "id";
    private static final String COLUMNA_NOMBRE = "name";
    private static final String COLUMNA_CODIGO_SIU = "siucode";
    private static final String COLUMNA_FECHA_INICIO = "startdate";
    private static final String COLUMNA_FECHA_FIN = "enddate";
    private static final String COLUMNA_ID_ESTADO = "statusid";
    private static final String COLUMNA_COMPROMISOS = "commitments";
    private static final String COLUMNA_FECHA_FIN_DEFINITIVA = "enddatedef";
    private static final String COLUMNA_ID_GESTOR = "managerid";
    private static final String ID_BASE_DATOS = "totalproyectos";

    @Override
    public ProyectoTotalProyectos obtenerUno(Integer intId) throws GIDaoException {
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ProyectoTotalProyectos proyecto = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_UNO);
            ps.setInt(1, intId);
            
            rs = ps.executeQuery();
            
            if (rs.next()){            
                
                proyecto = new ProyectoTotalProyectos();           
                proyecto.setId(rs.getInt(COLUMNA_ID));
                proyecto.setName(rs.getString(COLUMNA_NOMBRE));
                proyecto.setSiucode(rs.getString(COLUMNA_CODIGO_SIU));
                proyecto.setStartdate(rs.getString(COLUMNA_FECHA_INICIO));
                proyecto.setEnddate(rs.getString(COLUMNA_FECHA_FIN));
                proyecto.setEnddatedef(rs.getString(COLUMNA_FECHA_FIN_DEFINITIVA));
                proyecto.setCommitments(rs.getString(COLUMNA_COMPROMISOS));
                proyecto.setStatusid(rs.getInt(COLUMNA_ID_ESTADO));
                proyecto.setManagerid(rs.getInt(COLUMNA_ID_GESTOR));
                               
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
        
        return proyecto;
    }

    @Override
    public List<ProyectoTotalProyectos> obtenerActivos() throws GIDaoException {
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ProyectoTotalProyectos> proyectos = new ArrayList<ProyectoTotalProyectos>();
        ProyectoTotalProyectos proyecto = null;
        Integer intIdEstadoCreado = 5, intIdEstadoActivo =  1;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_ACTIVOS);
            ps.setInt(1, intIdEstadoActivo);
            ps.setInt(2, intIdEstadoCreado);
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){
                    proyecto = new ProyectoTotalProyectos();                    
                    proyecto.setId(rs.getInt(COLUMNA_ID));
                    proyecto.setName(rs.getString(COLUMNA_NOMBRE));
                    proyecto.setSiucode(rs.getString(COLUMNA_CODIGO_SIU));
                    proyecto.setStartdate(rs.getString(COLUMNA_FECHA_INICIO));
                    proyecto.setEnddate(rs.getString(COLUMNA_FECHA_FIN));
                    proyecto.setEnddatedef(rs.getString(COLUMNA_FECHA_FIN_DEFINITIVA));
                    proyecto.setCommitments(rs.getString(COLUMNA_COMPROMISOS));
                    proyecto.setStatusid(rs.getInt(COLUMNA_ID_ESTADO));
                    proyecto.setManagerid(rs.getInt(COLUMNA_ID_GESTOR));
                    proyectos.add(proyecto);
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
        
        return proyectos;
    }

    @Override
    public List<ProyectoTotalProyectos> obtenerFinalizados() throws GIDaoException {
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ProyectoTotalProyectos> proyectos = new ArrayList<ProyectoTotalProyectos>();
        ProyectoTotalProyectos proyecto = null;
        Integer intIdEstadoFinalizado = 2;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_FINALIZADOS);
            ps.setInt(1, intIdEstadoFinalizado);
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){
                    proyecto = new ProyectoTotalProyectos();                    
                    proyecto.setId(rs.getInt(COLUMNA_ID));
                    proyecto.setName(rs.getString(COLUMNA_NOMBRE));
                    proyecto.setSiucode(rs.getString(COLUMNA_CODIGO_SIU));
                    proyecto.setStartdate(rs.getString(COLUMNA_FECHA_INICIO));
                    proyecto.setEnddate(rs.getString(COLUMNA_FECHA_FIN));
                    proyecto.setEnddatedef(rs.getString(COLUMNA_FECHA_FIN_DEFINITIVA));
                    proyecto.setCommitments(rs.getString(COLUMNA_COMPROMISOS));
                    proyecto.setStatusid(rs.getInt(COLUMNA_ID_ESTADO));
                    proyecto.setManagerid(rs.getInt(COLUMNA_ID_GESTOR));
                    proyectos.add(proyecto);
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
        
        return proyectos;
    }

    @Override
    public Integer actualizarEstadoProyecto(ProyectoTotalProyectos proyecto, Integer intIdNuevoEstado) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;         
        Integer intAfectados = 0;
        
         try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(ACTUALIZAR_ESTADO);
            ps.setInt(1, intIdNuevoEstado);
            ps.setInt(2, proyecto.getId());
            
            intAfectados = ps.executeUpdate();
            
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
        
        return intAfectados;
    }
    
}
