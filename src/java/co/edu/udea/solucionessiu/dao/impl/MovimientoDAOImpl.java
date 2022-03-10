/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.FuncionesComunesDAO;
import co.edu.udea.solucionessiu.dao.MovimientoDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.Movimiento;
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
public class MovimientoDAOImpl extends JDBCConnectionPool implements MovimientoDAO{
    
    private static final String OBTENER_UNO = "SELECT * from sigap.sigap_movimientos WHERE Codigo = ?";
    private static final String OBTENER_MOVIMIENTOS_RENTASPROPIAS = "SELECT * from sigap.sigap_movimientos WHERE  (Tipo = ? and Rubro = ? and (Subrubro = ? or Subrubro = ?)) and (Fecha BETWEEN ? and ?);";
    private static final String OBTENER_RESERVAS_TIPOSOPORTE = "SELECT * from sigap.sigap_movimientos WHERE Tipo = ? and Fecha = ? and (TipoSoporte = ? or TipoSoporte = ?) ORDER BY Codigo";
    private static final String COLUMNA_CODIGO = "Codigo";
    private static final String COLUMNA_PROYECTO = "Proyecto";
    private static final String COLUMNA_RUBRO = "Rubro";
    private static final String COLUMNA_SUBRUBRO = "Subrubro";
    private static final String COLUMNA_ENTIDADFINANCIADORA = "EntidadFinanciadora";
    private static final String COLUMNA_CENTROCOSTO = "CentroCosto";
    private static final String COLUMNA_TIPO = "Tipo";
    private static final String COLUMNA_RESERVA = "Reserva";
    private static final String COLUMNA_FECHA = "Fecha";
    private static final String COLUMNA_VALOR = "Valor";
    private static final String COLUMNA_NUMEROSOPORTE = "NumeroSoporte";
    private static final String COLUMNA_TIPOSOPORTE = "TipoSoporte";
    private static final String COLUMNA_OBSERVACION = "Observacion";   
    private static final String ID_BASE_DATOS = "sigep";
    private static String FECHA_ACTUAL;
    
    public MovimientoDAOImpl(){
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        FECHA_ACTUAL = funcionesComunesDAO.getFechaActual();        
    }

    @Override
    public Movimiento obtenerUno(String strCodigo) throws GIDaoException {
        
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        Movimiento movimiento = null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_UNO);
            ps.setString(1, strCodigo);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                
                    movimiento = new Movimiento();                    
                    movimiento.setCodigoMov(rs.getString(COLUMNA_CODIGO));
                    movimiento.setCodProyecto(rs.getString(COLUMNA_PROYECTO));
                    movimiento.setRubro(rs.getInt(COLUMNA_RUBRO));
                    movimiento.setSubrubro(rs.getInt(COLUMNA_SUBRUBRO));
                    movimiento.setEntidadFinanciadora(rs.getString(COLUMNA_ENTIDADFINANCIADORA));
                    movimiento.setCentroCostos(rs.getString(COLUMNA_CENTROCOSTO));
                    movimiento.setTipoMov(rs.getString(COLUMNA_TIPO));
                    movimiento.setCodReserva(rs.getString(COLUMNA_RESERVA));
                    movimiento.setFecha(rs.getDate(COLUMNA_FECHA));
                    movimiento.setValor(rs.getLong(COLUMNA_VALOR));
                    movimiento.setNumeroSoporte(rs.getString(COLUMNA_NUMEROSOPORTE));
                    movimiento.setTipoSoporte(rs.getString(COLUMNA_TIPOSOPORTE));
                    movimiento.setObservacion(rs.getString(COLUMNA_OBSERVACION));                   
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
        
        return movimiento;
        
    }    

    @Override
    public List<Movimiento> obtenerMovimientosFechaActualXTipoSoporte(String strTipoMovimiento, String strTipoSoporte1, String strTipoSoporte2) throws GIDaoException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Movimiento> movimientos = new ArrayList<Movimiento>();
        Movimiento movimiento = null;
                
        // Orden de pedido e importaciones -> Myriam.
        // CDP y Ordenes de Servicio -> Sabina.
        
         try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_RESERVAS_TIPOSOPORTE);
            ps.setString(1, strTipoMovimiento);
            ps.setString(2, FECHA_ACTUAL);
            ps.setString(3, strTipoSoporte1);
            ps.setString(4, strTipoSoporte2);            
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){                    
                    movimiento = new Movimiento();
                    movimiento.setCodigoMov(rs.getString(COLUMNA_CODIGO));
                    movimiento.setCodProyecto(rs.getString(COLUMNA_PROYECTO));
                    movimiento.setRubro(rs.getInt(COLUMNA_RUBRO));
                    movimiento.setSubrubro(rs.getInt(COLUMNA_SUBRUBRO));
                    movimiento.setEntidadFinanciadora(rs.getString(COLUMNA_ENTIDADFINANCIADORA));
                    movimiento.setCentroCostos(rs.getString(COLUMNA_CENTROCOSTO));
                    movimiento.setTipoMov(rs.getString(COLUMNA_TIPO));
                    movimiento.setCodReserva(rs.getString(COLUMNA_RESERVA));
                    movimiento.setFecha(rs.getDate(COLUMNA_FECHA));
                    movimiento.setValor(rs.getLong(COLUMNA_VALOR));
                    movimiento.setNumeroSoporte(rs.getString(COLUMNA_NUMEROSOPORTE));
                    movimiento.setTipoSoporte(rs.getString(COLUMNA_TIPOSOPORTE));
                    movimiento.setObservacion(rs.getString(COLUMNA_OBSERVACION));                   
                    movimientos.add(movimiento);
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
         
        return movimientos;
        
    }

    @Override
    public List<Movimiento> obtenerMovimientosRentasPropiasFechaActual(Integer intNumDiasNotificar) throws GIDaoException {
        
        String strTipoMov1, strTipoMov2, strRubro1, strRubro2, strSubrubro1, strSubrubro2, strFechaBase, strCodigoCxC;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Movimiento> movimientos = new ArrayList<Movimiento>();
        Movimiento movimiento = null, movimientoCxC=null;
        //strTipoMov1 = "CuentasXCobrar";
        strTipoMov2 = "Ingreso";
        //strRubro1 = "10";
        strRubro2 = "1";
        strSubrubro1 = "208";
        strSubrubro2 = "6094";
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        
        if (!intNumDiasNotificar.toString().equals("0")){
            intNumDiasNotificar = Integer.parseInt("-" + intNumDiasNotificar.toString());
        }        
        
        strFechaBase = funcionesComunesDAO.aumentarDiasFecha(FECHA_ACTUAL, intNumDiasNotificar);
                        
         try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_MOVIMIENTOS_RENTASPROPIAS);           
            ps.setString(1, strTipoMov2);
            ps.setString(2, strRubro2);
            ps.setString(3, strSubrubro1);
            ps.setString(4, strSubrubro2);
            ps.setString(5, strFechaBase);
            ps.setString(6, FECHA_ACTUAL);
              
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){   
                    
                    strCodigoCxC = rs.getString(COLUMNA_RESERVA);                   
                                        
                    try{
                        movimientoCxC = this.obtenerUno(strCodigoCxC);
                    }catch(GIDaoException gi){
                        new GIDaoException("Se generó un error al intentar recuperar la CxC con código " + strCodigoCxC, gi);
                        movimientoCxC = null;
                    }
                    
                    if (movimientoCxC != null){
                    
                        movimiento = new Movimiento();
                        movimiento.setCodigoMov(rs.getString(COLUMNA_CODIGO));
                        movimiento.setCodProyecto(rs.getString(COLUMNA_PROYECTO));
                        movimiento.setRubro(rs.getInt(COLUMNA_RUBRO));
                        movimiento.setSubrubro(rs.getInt(COLUMNA_SUBRUBRO));
                        movimiento.setEntidadFinanciadora(rs.getString(COLUMNA_ENTIDADFINANCIADORA));
                        movimiento.setCentroCostos(rs.getString(COLUMNA_CENTROCOSTO));
                        movimiento.setTipoMov(rs.getString(COLUMNA_TIPO));
                        movimiento.setCodReserva(rs.getString(COLUMNA_RESERVA));
                        movimiento.setFecha(rs.getDate(COLUMNA_FECHA));
                        movimiento.setValor(rs.getLong(COLUMNA_VALOR));
                        movimiento.setNumeroSoporte(rs.getString(COLUMNA_NUMEROSOPORTE));
                        movimiento.setTipoSoporte(rs.getString(COLUMNA_TIPOSOPORTE));
                        movimiento.setObservacion(movimientoCxC.getObservacion() + " / "+ rs.getString(COLUMNA_OBSERVACION));                   
                        movimientos.add(movimiento);
                    }
                    
                    movimientoCxC = null;
                    strCodigoCxC = "";
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
         
        return movimientos;
        
    }
}
