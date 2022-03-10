/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.ParametroGeneralDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.ParametroGeneral;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Jorge.correa
 */
public class ParametroGeneralDAOImpl extends JDBCConnectionPool implements ParametroGeneralDAO{
    
    private static final String OBTENER_PARAMETROS_GENERALES = "SELECT * FROM soluciones_siu.tbl_parametros_generales WHERE txtCodigo = ?";    
    private static final String CODIGO_FORM = "frmGeneral";
    private static final String COLUMNA_CODIGO = "txtCodigo";
    private static final String COLUMNA_NOMBRE_SERVIDOR = "txtNombreServidor";
    private static final String COLUMNA_NUMERO_PUERTO = "intNumeroPuerto";
    private static final String COLUMNA_USUARIO_CONEXION = "txtUsuario";
    private static final String COLUMNA_CLAVE_CONEXION = "txtPassword";
    private static final String COLUMNA_MODO_PDN = "txtModoProduccion";
    private static final String COLUMNA_EMAIL_DLLO = "txtEmailDllo";
    
            
    @Override
    public ParametroGeneral obtenerParametrosGenerales() throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        ParametroGeneral parametroGeneral = null;
        String strIdBD = "siuweb";
        
        try{
            con = getConexion(strIdBD);
            ps = con.prepareCall(OBTENER_PARAMETROS_GENERALES);
            ps.setString(1, CODIGO_FORM);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                
                    parametroGeneral = new ParametroGeneral();                    
                    
                    parametroGeneral.setCodigo(rs.getString(COLUMNA_CODIGO));         
                    parametroGeneral.setNombreServidor(rs.getString(COLUMNA_NOMBRE_SERVIDOR));
                    parametroGeneral.setNumeroPuerto(rs.getInt(COLUMNA_NUMERO_PUERTO));
                    parametroGeneral.setUsuarioConexion(rs.getString(COLUMNA_USUARIO_CONEXION));
                    parametroGeneral.setClaveConexion(rs.getString(COLUMNA_CLAVE_CONEXION));                    
                    parametroGeneral.setModoProduccion(rs.getString(COLUMNA_MODO_PDN));
                    parametroGeneral.setEmailDllo(rs.getString(COLUMNA_EMAIL_DLLO));
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
        
        return parametroGeneral;
    }

    @Override
    public Boolean verificarNotificacionProyecto(String strIdProyecto) throws GIDaoException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
        
}
