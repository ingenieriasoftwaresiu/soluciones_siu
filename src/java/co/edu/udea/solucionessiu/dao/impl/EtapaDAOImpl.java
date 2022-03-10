/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.EtapaDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.Etapa;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author jorge.correaj
 */
public class EtapaDAOImpl extends JDBCConnectionPool implements EtapaDAO{
    
    private static final String OBTENER_POR_PROYECTO = "SELECT * FROM sigap_etapas WHERE Proyecto = ? ORDER BY Fin DESC LIMIT 1";
    private static final String COLUMNA_CODIGO = "Codigo";
    private static final String COLUMNA_PROYECTO = "Proyecto";
    private static final String COLUMNA_DESCRIPCION = "Descripcion";
    private static final String COLUMNA_INICIO = "Inicio";
    private static final String COLUMNA_FIN = "Fin";
    private static final String COLUMNA_OBSERVACIONES = "Observaciones";
    private static final String COLUMNA_PRODUCTO = "Producto";
    private static final String COLUMNA_TIPO = "Tipo";  
    private static final String ID_BASE_DATOS = "sigep";
    
    @Override
    public Etapa obtenerPorProyecto(String strIdProyecto) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Etapa etapa = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_POR_PROYECTO);
            ps.setString(1, strIdProyecto);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                
                    etapa = new Etapa();                    
                    etapa.setCodigo(rs.getInt(COLUMNA_CODIGO));
                    etapa.setProducto(rs.getString(COLUMNA_PROYECTO));
                    etapa.setDescripcion(rs.getString(COLUMNA_DESCRIPCION));
                    etapa.setFechaInicio(rs.getDate(COLUMNA_INICIO));
                    etapa.setFechaFin(rs.getDate(COLUMNA_FIN));
                    etapa.setObservaciones(rs.getString(COLUMNA_OBSERVACIONES));
                    etapa.setProducto(rs.getString(COLUMNA_PRODUCTO));
                    etapa.setTipo(rs.getInt(COLUMNA_TIPO));
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
        
        return etapa;
    }
    
}
