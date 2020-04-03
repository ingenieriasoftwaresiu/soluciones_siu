/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.ParametrosASIUDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.Coordinacion;
import co.edu.udea.solucionessiu.dto.Persona;
import co.edu.udea.solucionessiu.dto.Proceso;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author jorge.correa
 */
public class ParametrosASIUDAOImpl extends JDBCConnectionPool implements ParametrosASIUDAO{
    
    private static final String OBTENER_PROCESO_X_ID = "SELECT * FROM users.users_procesos p WHERE p.txtCodigo = ?";
    private static final String OBTENER_COORDINACION_X_ID = "SELECT * FROM users.users_coordinaciones c WHERE c.txtCodigo = ?";
    private static final String OBTENER_PERSONA_X_ID = "SELECT p.txtIdentificacion, p.txtNombreCompleto, p.txtEmailC, p.txtEstadoActual FROM users.users_personas p WHERE p.txtIdentificacion = ? and p.txtEstadoActual = 'A'";
    private static final String OBTENER_PERSONA_X_CARGO = "SELECT p.txtIdentificacion, p.txtNombreCompleto, p.txtEmailC, p.txtEstadoActual FROM users.users_personas p WHERE p.txtCargo = ? and p.txtEstadoActual = 'A'";
    private static final String OBTENER_JEFE_INMEDIATO_X_ID_EMPLEADO = "SELECT p.txtJefeInmediato FROM users.users_personas p where p.txtIdentificacion = ?";
    private static final String OBTENER_JEFE_INMEDIATO_X_CARGO_EMPLEADO = "SELECT p.txtJefeInmediato FROM users.users_personas p where p.txtCargo = ?";    
    private static final String COLUMNA_CODIGO = "txtCodigo";
    private static final String COLUMNA_NOMBRE = "txtNombre";
    private static final String COLUMNA_RESPONSABLE = "txtResponsable";    
    private static final String COLUMNA_IDENTIFICACION = "txtIdentificacion";
    private static final String COLUMNA_NOMBRE_COMPLETO = "txtNombreCompleto";
    private static final String COLUMNA_EMAIL_C = "txtEmailC";
    private static final String COLUMNA_ESTADO_ACTUAL = "txtEstadoActual";
    private static final String COLUMNA_JEFE_INMEDIATO = "txtJefeInmediato";

    @Override
    public Proceso obtenerProcesoXId(String strCodigo) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Proceso proceso = null;
        
        try{
            con = getConexion();
            ps = con.prepareCall(OBTENER_PROCESO_X_ID);
            ps.setString(1, strCodigo);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                
                proceso = new Proceso();
                proceso.setCodigo(rs.getString(COLUMNA_CODIGO));
                proceso.setNombre(rs.getString(COLUMNA_NOMBRE));
                proceso.setResponsable(rs.getString(COLUMNA_RESPONSABLE));
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
        
        return proceso;
    }

    @Override
    public Persona obtenerPersonaXId(String strCodigo) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Persona persona = null;
        
        try{
            con = getConexion();
            ps = con.prepareCall(OBTENER_PERSONA_X_ID);
            ps.setString(1, strCodigo);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                
                persona = new Persona();
                persona.setIdentificacion(rs.getString(COLUMNA_IDENTIFICACION));
                persona.setNombreCompleto(rs.getString(COLUMNA_NOMBRE_COMPLETO));
                persona.setCorreoInstitucional(rs.getString(COLUMNA_EMAIL_C));
                persona.setEstadoActual(rs.getString(COLUMNA_ESTADO_ACTUAL));                
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
        
        return persona;
    }

    @Override
    public Coordinacion obtenerCoordinacionXId(String strCodigo) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Coordinacion coordinacion = null;
        
        try{
            con = getConexion();
            ps = con.prepareCall(OBTENER_COORDINACION_X_ID);
            ps.setString(1, strCodigo);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                
                coordinacion = new Coordinacion();
                coordinacion.setCodigo(rs.getString(COLUMNA_CODIGO));
                coordinacion.setNombre(rs.getString(COLUMNA_NOMBRE));
                coordinacion.setResponsable(rs.getString(COLUMNA_RESPONSABLE));
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
        
        return coordinacion;
    }

    @Override
    public Persona obtenerPersonaXCargo(String strIdCargo) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Persona persona = null;
        
        try{
            con = getConexion();
            ps = con.prepareCall(OBTENER_PERSONA_X_CARGO);
            ps.setString(1, strIdCargo);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                
                persona = new Persona();
                persona.setIdentificacion(rs.getString(COLUMNA_IDENTIFICACION));
                persona.setNombreCompleto(rs.getString(COLUMNA_NOMBRE_COMPLETO));
                persona.setCorreoInstitucional(rs.getString(COLUMNA_EMAIL_C));
                persona.setEstadoActual(rs.getString(COLUMNA_ESTADO_ACTUAL));                
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
        
        return persona;
    }

    @Override
    public String obtenerJefeInmediatoXIdEmpleado(String strIdEmpleado) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String strIdJefeInmediato = null;
        
        try{
            con = getConexion();
            ps = con.prepareCall(OBTENER_JEFE_INMEDIATO_X_ID_EMPLEADO);
            ps.setString(1, strIdEmpleado);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                
              strIdJefeInmediato = rs.getString(COLUMNA_JEFE_INMEDIATO);    
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
        
        return strIdJefeInmediato;
    }

    @Override
    public String obtenerJefeInmediatoXCargoEmpleado(String strIdCargo) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String strIdJefeInmediato = null;
        
        try{
            con = getConexion();
            ps = con.prepareCall(OBTENER_JEFE_INMEDIATO_X_CARGO_EMPLEADO);
            ps.setString(1, strIdCargo);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                
              strIdJefeInmediato = rs.getString(COLUMNA_JEFE_INMEDIATO);    
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
        
        return strIdJefeInmediato;
    }
    
}
