/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.EjecucionPptalProyectoDAO;
import co.edu.udea.solucionessiu.dao.FuncionesComunesDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.dto.EjecucionPptalProyecto;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jorge.correaj
 */
public class EjecucionPptalProyectoDAOImpl extends JDBCConnectionPool implements EjecucionPptalProyectoDAO {
    
    private static final String OBTENER_DATOS_RUBRO = "select m.Rubro as Cod_Rubro, r.Nombre as Rubro from sigap_movimientos m, sigap_rubros r where (m.Rubro = r.Codigo) and m.Proyecto = ? and m.Tipo = 'Presupuesto Inicial' and NOT(m.Observacion like '%especie%') group by m.Rubro, r.Nombre ORDER BY r.Nombre";
    private static final String OBTENER_DATOS_SUBRUBRO = "select m.Subrubro as Cod_Subrubro, sr.Nombre as Subrubro from sigap_movimientos m, sigap_subrubros sr where (m.Subrubro = sr.Codigo) and m.Rubro = ? and m.Proyecto = ? and m.Tipo = 'Presupuesto Inicial' and NOT(m.Observacion like '%especie%')  group by m.Subrubro, sr.Nombre ORDER BY sr.Nombre";
    private static final String OBTENER_DATOS_MOVIMIENTO = "select m.Codigo as Cod_Mov, m.Valor as Valor from sigap_movimientos m where m.Proyecto = ? and m.Rubro = ? and m.Subrubro = ? and m.Tipo = 'Presupuesto Inicial' and NOT(m.Observacion like '%especie%') ORDER BY m.Codigo";
    private static final String OBTENER_MOVIMIENTOS = "select m.Codigo as Cod_Mov from sigap_movimientos m where m.Proyecto = ? and m.Reserva = ?";                                                                       
    private static final String OBTENER_TOTAL= "select sum(m.Valor) as Total from sigap_movimientos m where m.Proyecto = ? and m.Reserva = ?";
    private static final String OBTENER_VALOR_MOVIMIENTO = "select m.Valor as Valor from sigap_movimientos m where m.Proyecto = ? and m.Codigo = ?";
    private static final String COLUMNA_CODIGO_RUBRO = "Cod_Rubro";
    private static final String COLUMNA_NOMBRE_RUBRO = "Rubro";
    private static final String COLUMNA_CODIGO_SUBRUBRO = "Cod_Subrubro";
    private static final String COLUMNA_NOMBRE_SUBRUBRO = "Subrubro";
    private static final String COLUMNA_CODIGO_MOVIMIENTO = "Cod_Mov";
    private static final String COLUMNA_VALOR_MOVIMIENTO = "Valor";
    private static final String COLUMNA_TOTAL = "Total";
    private static final String ID_BASE_DATOS = "sigep";
    
    @Override
    public List<EjecucionPptalProyecto> calcularEjecucionPresupuestal(String strIdProyecto) throws GIDaoException {
        String strIdRubro= "", strNombreRubro="", strIdSubrubro="", strNombreSubrubro="", strIdMovimiento="";
        List<EjecucionPptalProyecto> ejecucionesPptalesProyecto = new ArrayList<EjecucionPptalProyecto>();
        EjecucionPptalProyecto ejecucionPptalProyecto = null;
        Map<String, String> valoresRubro = null;
        Map<String, String> valoresSubrubro = null;
        Map<String, Double> valoresMovimiento = null;        
        Double dblValorMovimiento=Double.parseDouble("0"), dblValorEgresos=Double.parseDouble("0"), dblValorReservas=Double.parseDouble("0"), dblValorDisp=Double.parseDouble("0"), dblValorComp=Double.parseDouble("0");
        Double dblTotalSubrubroPpto=Double.parseDouble("0"), dblTotalSubrubroEgresos=Double.parseDouble("0"), dblTotalSubrubroReservas=Double.parseDouble("0"), dblTotalSubrubroDisp=Double.parseDouble("0"), dblTotalSubrubroComp=Double.parseDouble("0");
        Double dblTotalRubroPpto=Double.parseDouble("0"), dblTotalRubroEgresos=Double.parseDouble("0"), dblTotalRubroReservas=Double.parseDouble("0"), dblTotalRubroDisp=Double.parseDouble("0");
        Double dblPorcEjecucion=Double.parseDouble("0"), dblTotalRubroCompr=Double.parseDouble("0");
        Double dblTotalSubrubroPorc=Double.parseDouble("0"), dblTotalRubroPorc=Double.parseDouble("0");
        Iterator itRubros = null;
        Iterator itSubrubros = null;
        Iterator itMovimientos = null;
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();              
        
        valoresRubro = obtenerRubros(strIdProyecto);        
        itRubros = valoresRubro.keySet().iterator();
        
        while(itRubros.hasNext()){
          strIdRubro = itRubros.next().toString();
          strNombreRubro = valoresRubro.get(strIdRubro);
                                                  
          valoresSubrubro = obtenerSubrubros(strIdRubro, strIdProyecto);
          itSubrubros = valoresSubrubro.keySet().iterator();
          
          while(itSubrubros.hasNext()){
              strIdSubrubro = itSubrubros.next().toString();
              strNombreSubrubro = valoresSubrubro.get(strIdSubrubro);                      
                           
              valoresMovimiento = obtenerMovimientos(strIdProyecto, strIdRubro, strIdSubrubro);
              itMovimientos = valoresMovimiento.keySet().iterator();
              
              while(itMovimientos.hasNext()){
                    strIdMovimiento = itMovimientos.next().toString();                  
                    
                    // Valor del presupuesto inicial.
                    dblValorMovimiento = valoresMovimiento.get(strIdMovimiento);               
                    dblTotalSubrubroPpto = dblTotalSubrubroPpto + dblValorMovimiento;
                                                            
                    // Cálculo del total de Egresos.

                    dblValorEgresos = obtenerTotalEgresos(strIdProyecto, strIdMovimiento);                        
                    dblTotalSubrubroEgresos = dblTotalSubrubroEgresos + dblValorEgresos;
                                        
                    // Cálculo del total de Reservas.

                    dblValorReservas = obtenerTotalReservas(strIdProyecto, strIdMovimiento);         
                    dblTotalSubrubroReservas = dblTotalSubrubroReservas + dblValorReservas;
                                                            
                    dblValorComp = (dblValorEgresos + dblValorReservas);
                    dblTotalSubrubroComp = dblTotalSubrubroComp + dblValorComp;
                                        
                    // Cálculo del valor disponible.

                    dblValorDisp = (dblValorMovimiento - dblValorComp);           
                    dblTotalSubrubroDisp = dblTotalSubrubroDisp + dblValorDisp;
                                                            
                    // Cálculo del porcentaje de ejecución.
                    
                    if (dblValorMovimiento == 0){
                        dblPorcEjecucion = Double.parseDouble("0");
                    }else{
                        dblPorcEjecucion = Double.parseDouble(String.valueOf((dblValorComp/dblValorMovimiento)*100));
                    }
                                                                                
                    dblTotalSubrubroPorc = dblTotalSubrubroPorc + dblPorcEjecucion;
                    
                    strIdMovimiento = "";
                    dblValorMovimiento = Double.parseDouble("0");            
                    dblValorEgresos = Double.parseDouble("0");
                    dblValorReservas = Double.parseDouble("0");
                    dblValorDisp = Double.parseDouble("0");
                    dblValorComp = Double.parseDouble("0");
                    dblPorcEjecucion = Double.parseDouble("0");
              }                            
                                                       
              dblTotalRubroPpto = dblTotalRubroPpto + dblTotalSubrubroPpto;
              dblTotalRubroEgresos = dblTotalRubroEgresos + dblTotalSubrubroEgresos;
              dblTotalRubroReservas = dblTotalRubroReservas + dblTotalSubrubroReservas;
              dblTotalRubroCompr = dblTotalRubroCompr + dblTotalSubrubroComp;
              dblTotalRubroDisp = dblTotalRubroDisp + dblTotalSubrubroDisp;     
              dblTotalRubroPorc = funcionesComunesDAO.redondear(((dblTotalRubroCompr/dblTotalRubroPpto)*100),1);
              
              strIdSubrubro = "";
              strNombreSubrubro = "";
              valoresMovimiento = null;
              itMovimientos = null;              
              dblTotalSubrubroPpto = Double.parseDouble("0");
              dblTotalSubrubroEgresos  = Double.parseDouble("0");
              dblTotalSubrubroReservas = Double.parseDouble("0");
              dblTotalSubrubroDisp = Double.parseDouble("0");
              dblTotalSubrubroPorc = Double.parseDouble("0");       
              dblTotalSubrubroComp = Double.parseDouble("0"); 
          }                     
                              
          // Crear objetos de Ejecución presupuestal.
          
          ejecucionPptalProyecto = new EjecucionPptalProyecto();
          ejecucionPptalProyecto.setCodigoRubro(strIdRubro);
          ejecucionPptalProyecto.setNombreRubro(strNombreRubro);
          ejecucionPptalProyecto.setPresupuesto(dblTotalRubroPpto);
          ejecucionPptalProyecto.setReservas(dblTotalRubroReservas);
          ejecucionPptalProyecto.setValorEjecucion(dblTotalRubroEgresos);          
          ejecucionPptalProyecto.setDisponibilidad(dblTotalRubroDisp);
          ejecucionPptalProyecto.setPorcentajeEjecucion(dblTotalRubroPorc);
          ejecucionesPptalesProyecto.add(ejecucionPptalProyecto);
          
          strIdRubro = "";
          strNombreRubro = "";
          valoresSubrubro = null;
          itSubrubros = null;
          dblTotalRubroPpto = Double.parseDouble("0");
          dblTotalRubroEgresos  = Double.parseDouble("0");
          dblTotalRubroReservas = Double.parseDouble("0");
          dblTotalRubroDisp = Double.parseDouble("0");
          dblTotalRubroPorc = Double.parseDouble("0"); 
          dblTotalRubroCompr  = Double.parseDouble("0"); 
          ejecucionPptalProyecto = null;
        }
        
        return ejecucionesPptalesProyecto;
    }
    
    private Map<String, String> obtenerRubros(String strIdProyecto) throws GIDaoException{
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;       
        Map<String, String> valoresRubro = new HashMap<String, String>();
        String strIdRubro=null, strNombreRubro=null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_DATOS_RUBRO);
            ps.setString(1, strIdProyecto);
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){                         
                    strIdRubro = rs.getString(COLUMNA_CODIGO_RUBRO);
                    strNombreRubro = rs.getString(COLUMNA_NOMBRE_RUBRO);
                    valoresRubro.put(strIdRubro, strNombreRubro);
                    
                    strIdRubro = null;
                    strNombreRubro = null;
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
        
        return valoresRubro;
    }
    
    private Map<String, String> obtenerSubrubros(String strIdRubro, String strIdProyecto) throws GIDaoException{
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;       
        Map<String, String> valoresSubrubro = new HashMap<String, String>();
        String strIdSubrubro=null, strNombreSubrubro=null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_DATOS_SUBRUBRO);
            ps.setString(1, strIdRubro);
            ps.setString(2, strIdProyecto);
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){                         
                    strIdSubrubro = rs.getString(COLUMNA_CODIGO_SUBRUBRO);
                    strNombreSubrubro = rs.getString(COLUMNA_NOMBRE_SUBRUBRO);
                    valoresSubrubro.put(strIdSubrubro, strNombreSubrubro);
                    
                    strIdSubrubro = null;
                    strNombreSubrubro = null;
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
        
        return valoresSubrubro;
    }
    
    private Map<String, Double> obtenerMovimientos(String strIdProyecto, String strIdRubro, String strIdSubrubro) throws GIDaoException{
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;       
        Map<String, Double> valoresMovimiento = new HashMap<String, Double>();
        String strIdMovimiento=null;
        Double dblValorMovimiento=null;
        
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_DATOS_MOVIMIENTO);
            ps.setString(1, strIdProyecto);
            ps.setString(2, strIdRubro);
            ps.setString(3, strIdSubrubro);
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){                         
                    strIdMovimiento = rs.getString(COLUMNA_CODIGO_MOVIMIENTO);
                    dblValorMovimiento = rs.getDouble(COLUMNA_VALOR_MOVIMIENTO);
                    valoresMovimiento.put(strIdMovimiento, dblValorMovimiento);
                    
                    strIdMovimiento = null;
                    dblValorMovimiento = null;
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
        
        return valoresMovimiento;
    }
    
    private Double obtenerTotalReservas(String strIdProyecto, String strIdReserva) throws GIDaoException{
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;       
        Double dblTotalReservas=Double.valueOf("0"), dblTotalEgresos = null, dblValor=Double.valueOf("0");    
        String strIdMovimiento = null;

        try{
          con = getConexion(ID_BASE_DATOS);
          ps = con.prepareCall(OBTENER_MOVIMIENTOS);
          ps.setString(1, strIdProyecto);
          ps.setString(2, strIdReserva);

          rs = ps.executeQuery();

          if (rs != null){
              while (rs.next()){                         
                  strIdMovimiento = rs.getString(COLUMNA_CODIGO_MOVIMIENTO);
                  
                  dblValor = obtenerValorMovimiento(strIdProyecto, strIdMovimiento);
                  
                  if (dblValor > 0){
                      dblTotalReservas = dblTotalReservas + dblValor;
                  }else{
                      dblTotalEgresos = obtenerTotal(strIdProyecto, strIdMovimiento);
                      
                      if (dblTotalEgresos == null){
                           dblTotalReservas = dblTotalReservas + dblTotalEgresos;                                      
                      }   
                  }
                        
                  strIdMovimiento = null;
                  dblValor=Double.valueOf("0");                 
                  dblTotalEgresos = null;
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
        
        return dblTotalReservas;
    }
    
    private Double obtenerTotalEgresos(String strIdProyecto, String strIdReserva) throws GIDaoException{                
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;       
        Double dblTotalEgresos=Double.valueOf("0"), dblTotal=Double.valueOf("0");
        String strIdEgreso= null;
        
          try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_MOVIMIENTOS);
            ps.setString(1, strIdProyecto);
            ps.setString(2, strIdReserva);
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){                         
                    strIdEgreso = rs.getString(COLUMNA_CODIGO_MOVIMIENTO);
                    
                    dblTotal = obtenerTotal(strIdProyecto, strIdEgreso);
                    dblTotalEgresos = dblTotalEgresos + dblTotal;
                    
                    strIdEgreso = null;
                    dblTotal = Double.valueOf("0");
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
        
        return dblTotalEgresos;
    }
            
    private Double obtenerTotal(String strIdProyecto, String strIdReserva) throws GIDaoException{                
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;       
        Double dblTotal = Double.valueOf("0");  
        
          try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_TOTAL);
            ps.setString(1, strIdProyecto);
            ps.setString(2, strIdReserva);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                           
                    dblTotal = rs.getDouble(COLUMNA_TOTAL);                                   
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
        
        return dblTotal;
    }
    
    private Double obtenerValorMovimiento(String strIdProyecto, String strIdMovimiento) throws GIDaoException{                
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;       
        Double dblValor= Double.valueOf("0");  
        
          try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(OBTENER_VALOR_MOVIMIENTO);
            ps.setString(1, strIdProyecto);
            ps.setString(2, strIdMovimiento);
            
            rs = ps.executeQuery();
            
            if (rs.next()){                           
                    dblValor = rs.getDouble(COLUMNA_VALOR_MOVIMIENTO);                                   
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
        
        return dblValor;
    }
}
