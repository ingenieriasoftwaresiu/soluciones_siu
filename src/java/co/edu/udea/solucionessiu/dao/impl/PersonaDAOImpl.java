/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.PersonaDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.Persona;
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
public class PersonaDAOImpl extends JDBCConnectionPool implements PersonaDAO {
    
    private static final String OBTENER_POR_NIVEL = "SELECT * FROM sigap.sigap_personas WHERE Nivel = ? ORDER BY Identificacion";
    private static final String OBTENER_UNA = "SELECT * FROM sigap.sigap_personas WHERE Identificacion = ?";
    private static final String COLUMNA_IDENTIFICACION = "Identificacion";
    private static final String COLUMNA_NOMBRE = "Nombre";
    private static final String COLUMNA_DEPENDENCIA = "Dependencia";
    private static final String COLUMNA_EMAIL = "Email";
    private static final String COLUMNA_DIRECCION = "Direccion";
    private static final String COLUMNA_TELEFONO1 = "Telefono1";
    private static final String COLUMNA_TELEFONO2 = "Telefono2";
    private static final String COLUMNA_SEXO = "Sexo";
    private static final String COLUMNA_USUARIO = "Usuario";
    private static final String COLUMNA_CONTRASENA = "Contrasena";
    private static final String COLUMNA_NIVEL = "Nivel";
    private static final String ID_BASE_DATOS = "sigep";

    @Override
    public List<Persona> obtenerPorNivel(Integer intNivel) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Persona> personas = new ArrayList<Persona>();
        Persona persona = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_POR_NIVEL);
            ps.setInt(1, intNivel);
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){
                    persona = new Persona();
                    
                    persona.setIdentificacion(rs.getString(COLUMNA_IDENTIFICACION));
                    persona.setNombre(rs.getString(COLUMNA_NOMBRE));
                    persona.setDependencia(rs.getString(COLUMNA_DEPENDENCIA));
                    persona.setEmail(rs.getString(COLUMNA_EMAIL));
                    persona.setDireccion(rs.getString(COLUMNA_DIRECCION));
                    persona.setTelefono1(rs.getString(COLUMNA_TELEFONO1));
                    persona.setTelefono2(rs.getString(COLUMNA_TELEFONO2));
                    persona.setSexo(rs.getString(COLUMNA_SEXO));
                    persona.setUsuario(rs.getString(COLUMNA_USUARIO));
                    persona.setContrasena(rs.getString(COLUMNA_CONTRASENA));
                    persona.setNivel(rs.getInt(COLUMNA_NIVEL));                            
                    personas.add(persona);
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
        
        return personas;
    }

    @Override
    public Persona obtenerUna(String strIdPersona) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Persona persona = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_UNA);
            ps.setString(1, strIdPersona);
            
            rs = ps.executeQuery();
            
            if (rs.next()){           
                    persona = new Persona();                    
                    persona.setIdentificacion(rs.getString(COLUMNA_IDENTIFICACION));
                    persona.setNombre(rs.getString(COLUMNA_NOMBRE));
                    persona.setDependencia(rs.getString(COLUMNA_DEPENDENCIA));
                    persona.setEmail(rs.getString(COLUMNA_EMAIL));
                    persona.setDireccion(rs.getString(COLUMNA_DIRECCION));
                    persona.setTelefono1(rs.getString(COLUMNA_TELEFONO1));
                    persona.setTelefono2(rs.getString(COLUMNA_TELEFONO2));
                    persona.setSexo(rs.getString(COLUMNA_SEXO));
                    persona.setUsuario(rs.getString(COLUMNA_USUARIO));
                    persona.setContrasena(rs.getString(COLUMNA_CONTRASENA));
                    persona.setNivel(rs.getInt(COLUMNA_NIVEL));                             
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
}
