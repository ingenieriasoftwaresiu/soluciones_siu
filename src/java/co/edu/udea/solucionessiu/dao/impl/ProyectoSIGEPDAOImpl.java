/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.ParametroGeneralDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.ParametroGeneral;
import co.edu.udea.solucionessiu.dto.ProyectoSIGEP;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import co.edu.udea.solucionessiu.dao.ProyectoSIGEPDAO;

/**
 *
 * @author jorge.correaj
 */
public class ProyectoSIGEPDAOImpl extends JDBCConnectionPool implements ProyectoSIGEPDAO {
    private static final String OBTENER_TODOS = "SELECT * from sigap.sigap_proyectos WHERE Codigo <> '' ORDER BY Codigo";
    private static final String OBTENER_POR_ESTADO = "SELECT * from sigap.sigap_proyectos WHERE Codigo <> '' and Estado = ? ORDER BY Codigo";
    private static final String OBTENER_POR_ESTADO_Y_TIPO_PROYECTODIFERENTE = "SELECT * from sigap.sigap_proyectos WHERE Codigo <> '' and Estado = ? and TipoProyectos <> ? ORDER BY Codigo";
    private static final String OBTENER_UNO = "SELECT * from sigap.sigap_proyectos WHERE Codigo = ?";
    private static final String COLUMNA_CODIGO = "Codigo";
    private static final String COLUMNA_NOMBRE = "Nombre";
    private static final String COLUMNA_DESCRIPCION = "Descripcion";
    private static final String COLUMNA_DESCRIPTORES = "Descriptores";
    private static final String COLUMNA_OBSERVACION = "Observacion";
    private static final String COLUMNA_TIPO_CONTRIBUYENTE = "TipoContribuyente";
    private static final String COLUMNA_LUGAR_TRABAJO = "LugarTrabajo";
    private static final String COLUMNA_ESTADO = "Estado";
    private static final String COLUMNA_DEPENDENCIA = "Dependencia";
    private static final String COLUMNA_TIPO_PROYECTO = "TipoProyectos";
    private static final String SEPARADOR_CODIGOS_PROYECTOS = ",";
    private static final String ID_BASE_DATOS = "sigep";
    
    @Override
    public List<ProyectoSIGEP> obtenerTodos() throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ProyectoSIGEP> proyectos = new ArrayList<ProyectoSIGEP>();
        ProyectoSIGEP proyecto = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_TODOS);
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){
                    proyecto = new ProyectoSIGEP();
                    
                    proyecto.setCodigo(rs.getString(COLUMNA_CODIGO));
                    proyecto.setNombre(rs.getString(COLUMNA_NOMBRE));
                    proyecto.setDescripcion(rs.getString(COLUMNA_DESCRIPCION));
                    proyecto.setDescriptores(rs.getString(COLUMNA_DESCRIPTORES));
                    proyecto.setObservacion(rs.getString(COLUMNA_OBSERVACION));
                    proyecto.setTipoContribuyente(rs.getString(COLUMNA_TIPO_CONTRIBUYENTE));
                    proyecto.setLugarTrabajo(rs.getString(COLUMNA_LUGAR_TRABAJO));
                    proyecto.setEstado(rs.getString(COLUMNA_ESTADO));
                    proyecto.setDependencia(rs.getInt(COLUMNA_DEPENDENCIA));
                    proyecto.setTipoProyecto(COLUMNA_TIPO_PROYECTO);
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
    public ProyectoSIGEP obtenerUno(String strCodigo) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ProyectoSIGEP proyecto = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_UNO);
            ps.setString(1, strCodigo);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                
                    proyecto = new ProyectoSIGEP();                    
                    proyecto.setCodigo(rs.getString(COLUMNA_CODIGO));
                    proyecto.setNombre(rs.getString(COLUMNA_NOMBRE));
                    proyecto.setDescripcion(rs.getString(COLUMNA_DESCRIPCION));
                    proyecto.setDescriptores(rs.getString(COLUMNA_DESCRIPTORES));
                    proyecto.setObservacion(rs.getString(COLUMNA_OBSERVACION));
                    proyecto.setTipoContribuyente(rs.getString(COLUMNA_TIPO_CONTRIBUYENTE));
                    proyecto.setLugarTrabajo(rs.getString(COLUMNA_LUGAR_TRABAJO));
                    proyecto.setEstado(rs.getString(COLUMNA_ESTADO));
                    proyecto.setDependencia(rs.getInt(COLUMNA_DEPENDENCIA));               
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
    public List<ProyectoSIGEP> obtenerPorEstado(String strIdEstado) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ProyectoSIGEP> proyectos = new ArrayList<ProyectoSIGEP>();
        ProyectoSIGEP proyecto = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_POR_ESTADO);
            ps.setString(1, strIdEstado);
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){
                    proyecto = new ProyectoSIGEP();
                    
                    proyecto.setCodigo(rs.getString(COLUMNA_CODIGO));
                    proyecto.setNombre(rs.getString(COLUMNA_NOMBRE));
                    proyecto.setDescripcion(rs.getString(COLUMNA_DESCRIPCION));
                    proyecto.setDescriptores(rs.getString(COLUMNA_DESCRIPTORES));
                    proyecto.setObservacion(rs.getString(COLUMNA_OBSERVACION));
                    proyecto.setTipoContribuyente(rs.getString(COLUMNA_TIPO_CONTRIBUYENTE));
                    proyecto.setLugarTrabajo(rs.getString(COLUMNA_LUGAR_TRABAJO));
                    proyecto.setEstado(rs.getString(COLUMNA_ESTADO));
                    proyecto.setDependencia(rs.getInt(COLUMNA_DEPENDENCIA));
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
    public Boolean verificarNotificacionProyecto(String strIdProyecto) throws GIDaoException {
        Boolean notificarProyecto = Boolean.TRUE;
        String strIdsProyectos = null, strCodigo="";
        String[] strCodigos = null;
        ParametroGeneralDAO parametroGeneralDAO = new ParametroGeneralDAOImpl();
        ParametroGeneral parametroGeneral = null;
        Integer intCont=0;
        
        parametroGeneral = parametroGeneralDAO.obtenerParametrosGeneralesSigep();
        
        if (parametroGeneral != null){
            strIdsProyectos = parametroGeneral.getProyectosExentosNotificacion();
            
            if (strIdsProyectos != null){
                strCodigos =  strIdsProyectos.split(SEPARADOR_CODIGOS_PROYECTOS);
                
                if (strCodigos != null){
                    while(intCont < strCodigos.length){
                        
                        strCodigo = "4-" + strCodigos[intCont].trim();
                               
                        if (strCodigo.equals(strIdProyecto)){
                            notificarProyecto = Boolean.FALSE;
                            break;
                        }
                        
                        intCont++;
                        strCodigo = "";
                    }
                }
            }
        }
        
        return notificarProyecto;
    }                

    @Override
    public List<ProyectoSIGEP> obtenerPorEstadoYTipoProyectoDiferente(String strIdEstado, String strIdTipoProyecto) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<ProyectoSIGEP> proyectos = new ArrayList<ProyectoSIGEP>();
        ProyectoSIGEP proyecto = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_POR_ESTADO_Y_TIPO_PROYECTODIFERENTE);
            ps.setString(1, strIdEstado);
            ps.setString(2, strIdTipoProyecto);
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){
                    proyecto = new ProyectoSIGEP();
                    
                    proyecto.setCodigo(rs.getString(COLUMNA_CODIGO));
                    proyecto.setNombre(rs.getString(COLUMNA_NOMBRE));
                    proyecto.setDescripcion(rs.getString(COLUMNA_DESCRIPCION));
                    proyecto.setDescriptores(rs.getString(COLUMNA_DESCRIPTORES));
                    proyecto.setObservacion(rs.getString(COLUMNA_OBSERVACION));
                    proyecto.setTipoContribuyente(rs.getString(COLUMNA_TIPO_CONTRIBUYENTE));
                    proyecto.setLugarTrabajo(rs.getString(COLUMNA_LUGAR_TRABAJO));
                    proyecto.setEstado(rs.getString(COLUMNA_ESTADO));
                    proyecto.setDependencia(rs.getInt(COLUMNA_DEPENDENCIA));
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
}
