/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.GrupoDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.Grupo;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author jorge.correaj
 */
public class GrupoDAOImpl extends JDBCConnectionPool implements GrupoDAO {
    
    private static final String OBTENER_UNO = "SELECT * FROM sigap.sigap_grupos WHERE Codigo = ? LIMIT 1";
    private static final String COLUMNA_CODIGO = "Codigo";
    private static final String COLUMNA_TIPO= "Tipo";
    private static final String COLUMNA_DEPENDENCIA = "Dependencia";
    private static final String COLUMNA_NOMBRE = "Nombre";
    private static final String COLUMNA_UBICACION = "Ubicacion";
    private static final String COLUMNA_COORDINADOR = "Coordinador";
    private static final String COLUMNA_OBSERVACION = "Observacion";
    private static final String COLUMNA_ALIAS = "Alias";
    private static final String COLUMNA_DIRECCION = "Direccion";
    private static final String COLUMNA_TELEFONO = "Telefono";
    private static final String COLUMNA_FAX = "Fax";
    private static final String COLUMNA_URL = "Url";
    private static final String COLUMNA_ADMINISTRADOR = "Administrador";
    private static final String ID_BASE_DATOS = "sigep";
    
    @Override
    public Grupo obtenerUno(Integer intCodigo) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Grupo grupo = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_UNO);
            ps.setInt(1, intCodigo);
            
            rs = ps.executeQuery();
            
            if (rs.next()){           
                    grupo = new Grupo();                    
                    grupo.setCodigo(rs.getInt(COLUMNA_CODIGO));
                    grupo.setTipo(rs.getInt(COLUMNA_TIPO));
                    grupo.setDependencia(rs.getInt(COLUMNA_DEPENDENCIA));
                    grupo.setNombre(rs.getString(COLUMNA_NOMBRE));
                    grupo.setUbicacion(rs.getString(COLUMNA_UBICACION));
                    grupo.setCoordinador(rs.getString(COLUMNA_COORDINADOR));
                    grupo.setObservacion(rs.getString(COLUMNA_OBSERVACION));
                    grupo.setAlias(rs.getString(COLUMNA_ALIAS));
                    grupo.setDireccion(rs.getString(COLUMNA_DIRECCION));
                    grupo.setTelefono(rs.getString(COLUMNA_TELEFONO));
                    grupo.setFax(rs.getString(COLUMNA_FAX));
                    grupo.setUrl(rs.getString(COLUMNA_URL));
                    grupo.setAdministrador(rs.getString(COLUMNA_ADMINISTRADOR));
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
        
        return grupo;
    }
}
