/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.ActividadDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.Actividad;
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
public class ActividadDAOImpl extends JDBCConnectionPool implements ActividadDAO{
    
    private static final String OBTENER_UNA = "SELECT * FROM sigap.sigap_actividades WHERE Codigo = ?";
    private static final String OBTENER_TODAS = "SELECT * FROM sigap.sigap_actividades";
    private static final String OBTENER_POR_FECHA_IGUAL = "SELECT a.Codigo as Codigo, a.Etapa as Etapa, a.Proyecto as Proyecto, a.Descripcion as Descripcion, a.Inicio as Inicio, a.Fin as Fin, a.Observacion as Observacion, a.Producto as Producto FROM sigap.sigap_actividades a, sigap_proyectos p WHERE (a.Proyecto = p.Codigo) and a.Fin = ? and p.TipoProyectos <> '11';";
    private static final String OBTENER_POR_FECHA_MENOR = "SELECT a.Codigo as Codigo, a.Etapa as Etapa, a.Proyecto as Proyecto, a.Descripcion as Descripcion, a.Inicio as Inicio, a.Fin as Fin, a.Observacion as Observacion, a.Producto as Producto FROM sigap.sigap_actividades a, sigap_proyectos p WHERE (a.Proyecto = p.Codigo) and a.Fin <= ? and p.TipoProyectos <> '11';";
    private static final String OBTENER_POR_FECHA_MAYOR = "SELECT a.Codigo as Codigo, a.Etapa as Etapa, a.Proyecto as Proyecto, a.Descripcion as Descripcion, a.Inicio as Inicio, a.Fin as Fin, a.Observacion as Observacion, a.Producto as Producto FROM sigap.sigap_actividades a, sigap_proyectos p WHERE (a.Proyecto = p.Codigo) and a.Fin >= ? and p.TipoProyectos <> '11';";
    private static final String COLUMNA_CODIGO = "Codigo";
    private static final String COLUMNA_ETAPA = "Etapa";
    private static final String COLUMNA_PROYECTO = "Proyecto";
    private static final String COLUMNA_DESCRIPCION = "Descripcion";
    private static final String COLUMNA_FECHA_INICIO = "Inicio";
    private static final String COLUMNA_FECHA_FIN = "Fin";
    private static final String COLUMNA_OBSERVACION = "Observacion";
    private static final String COLUMNA_PRODUCTO = "Producto";
    private static final String ID_BASE_DATOS = "sigep";

    @Override
    public Actividad obtenerPorCodigo(Integer strIdActividad) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Actividad actividad = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_UNA);
            ps.setInt(1, strIdActividad);
            
            rs = ps.executeQuery();
            
            if (rs.next()){           
                   actividad = new Actividad();                    
                   actividad.setCodigo(rs.getInt(COLUMNA_CODIGO));
                   actividad.setEtapa(rs.getInt(COLUMNA_ETAPA));
                   actividad.setProyecto(rs.getString(COLUMNA_PROYECTO));
                   actividad.setDescripcion(rs.getString(COLUMNA_DESCRIPCION));
                   actividad.setFechaInicio(rs.getDate(COLUMNA_FECHA_INICIO));
                   actividad.setFechaFin(rs.getDate(COLUMNA_FECHA_FIN));
                   actividad.setObservacion(rs.getString(COLUMNA_OBSERVACION));
                   actividad.setProducto(rs.getString(COLUMNA_PRODUCTO));                   
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
        
        return actividad;
    }

    @Override
    public List<Actividad> obtenerTodas() throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Actividad> actividades = new ArrayList<Actividad>();
        Actividad actividad = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_TODAS);
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){
                   actividad = new Actividad();
                   actividad.setCodigo(rs.getInt(COLUMNA_CODIGO));
                   actividad.setEtapa(rs.getInt(COLUMNA_ETAPA));
                   actividad.setProyecto(rs.getString(COLUMNA_PROYECTO));
                   actividad.setDescripcion(rs.getString(COLUMNA_DESCRIPCION));
                   actividad.setFechaInicio(rs.getDate(COLUMNA_FECHA_INICIO));
                   actividad.setFechaFin(rs.getDate(COLUMNA_FECHA_FIN));
                   actividad.setObservacion(rs.getString(COLUMNA_OBSERVACION));
                   actividad.setProducto(rs.getString(COLUMNA_PRODUCTO));                    
                    actividades.add(actividad);
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
        
        return actividades;
    }

    @Override
    public List<Actividad> obtenerPorFecha(String strFechaBase, String strCriterio) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Actividad> actividades = new ArrayList<Actividad>();
        Actividad actividad = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            
            if (strCriterio.equals("=")){
                ps = con.prepareCall(OBTENER_POR_FECHA_IGUAL);
            }
            
            if (strCriterio.equals(">")){
                ps = con.prepareCall(OBTENER_POR_FECHA_MAYOR);
            }
            
            if (strCriterio.equals("<")){
                ps = con.prepareCall(OBTENER_POR_FECHA_MENOR);
            }
            
            ps.setString(1, strFechaBase);
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){
                   actividad = new Actividad();
                   actividad.setCodigo(rs.getInt(COLUMNA_CODIGO));
                   actividad.setEtapa(rs.getInt(COLUMNA_ETAPA));
                   actividad.setProyecto(rs.getString(COLUMNA_PROYECTO));
                   actividad.setDescripcion(rs.getString(COLUMNA_DESCRIPCION));
                   actividad.setFechaInicio(rs.getDate(COLUMNA_FECHA_INICIO));
                   actividad.setFechaFin(rs.getDate(COLUMNA_FECHA_FIN));
                   actividad.setObservacion(rs.getString(COLUMNA_OBSERVACION));
                   actividad.setProducto(rs.getString(COLUMNA_PRODUCTO));                    
                    actividades.add(actividad);
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
        
        return actividades;
    }
    
}
