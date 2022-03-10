/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.FuncionesComunesDAO;
import co.edu.udea.solucionessiu.dao.cnf.JDBCConnectionPool;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

/**
 *
 * @author George
 */
public class FuncionesComunesDAOImpl extends JDBCConnectionPool implements FuncionesComunesDAO{
    
    private static final String ID_BASE_DATOS = "siuweb";

    @Override
    public Calendar incrementarDiasHabiles(Calendar fechaInicial, int intNroDias) {
        Calendar fechaFeriada = Calendar.getInstance();
        String[] strTemp=null;
        Boolean feriado = Boolean.FALSE;
        Vector arrFeriados = obtenerDiasFeriados();
        int cont=0;
        
        while (cont<=intNroDias){                        
            if (fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY){               
                feriado=Boolean.FALSE;
                
                for (int i=0;i<arrFeriados.size();i++){
                    
                     strTemp = arrFeriados.get(i).toString().split("-");
                     fechaFeriada.set(Integer.parseInt(strTemp[0]), Integer.parseInt(strTemp[1]) - 1, Integer.parseInt(strTemp[2]));
                     
                     fechaFeriada.set(Calendar.SECOND, 0);
                     fechaFeriada.set(Calendar.MILLISECOND, 0);                   
                                                           
                    if (fechaFeriada.getTime().toString().equals(fechaInicial.getTime().toString())){        
                        feriado=Boolean.TRUE;
                        break;
                    }
                    
                    strTemp=null;
                    fechaFeriada = Calendar.getInstance();                    
                }
                
                if (feriado.equals(Boolean.FALSE)){                                  
                    cont++;       
                }
            }else{
                if (cont==0){
                    cont=1;
                }   
            }            
                   
            if (cont<=intNroDias){
                fechaInicial.add(Calendar.DATE, 1);
            }            
        }
  
        return fechaInicial;
    }

    @Override
    public int getDiasHabiles(Calendar fechaInicial, Calendar fechaFinal) {
        int diffDays= 0;
        boolean feriado=false;
        Calendar fechaFeriada = Calendar.getInstance();
        String[] strTemp;
        Vector arrFeriados = obtenerDiasFeriados();
                
        fechaInicial.set(Calendar.SECOND, 0);
        fechaInicial.set(Calendar.MILLISECOND, 0);
        fechaFinal.set(Calendar.SECOND, 0);
        fechaFinal.set(Calendar.MILLISECOND, 0);
        
        //mientras la fecha inicial sea menor o igual que la fecha final se cuentan los dias
        
        while (fechaInicial.before(fechaFinal) || fechaInicial.equals(fechaFinal)) {

            //si el dia de la semana de la fecha minima es diferente de sabado o domingo
            if (fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {               
                feriado=false;
                
                for (int i=0;i<arrFeriados.size();i++){
                    
                     strTemp = arrFeriados.get(i).toString().split("-");
                     fechaFeriada.set(Integer.parseInt(strTemp[0]), Integer.parseInt(strTemp[1]) - 1, Integer.parseInt(strTemp[2]));
                     
                     fechaFeriada.set(Calendar.SECOND, 0);
                     fechaFeriada.set(Calendar.MILLISECOND, 0);                   
                                                           
                    if (fechaFeriada.getTime().toString().equals(fechaInicial.getTime().toString())){        
                        feriado=true;
                        break;
                    }
                    
                    strTemp=null;
                    fechaFeriada = Calendar.getInstance();
                }
                
                if (!feriado){                    
                    diffDays++;             
                }
            }
            
            //se suma 1 dia para hacer la validacion del siguiente dia.
            fechaInicial.add(Calendar.DATE, 1);

        }
        return diffDays;
    }

    @Override
    public String aumentarDiasFecha(String strFechaBase, int intNumDias) {     
        String strAnio, strMes, strDia;
        
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
              
        strAnio = strFechaBase.substring(0,4);
        strMes= strFechaBase.substring(5,7);
        strDia = strFechaBase.substring(8,10);      
              
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(strAnio), Integer.parseInt(strMes)-1, Integer.parseInt(strDia));
        cal.add(Calendar.DATE,intNumDias);        
        
        return formato.format(cal.getTime());
    }

    @Override
    public double redondearDecimales(double dblValor, int intNumDecimales) {
        double dblNuevoValor = 0;
        
        if (intNumDecimales == 1){
            dblNuevoValor = Math.rint(dblValor*10)/10; 
        }
        
        if (intNumDecimales == 2){
            dblNuevoValor = Math.rint(dblValor*100)/100; 
        }
        
        if (intNumDecimales == 3){
            dblNuevoValor = Math.rint(dblValor*1000)/1000; 
        }
        
        return dblNuevoValor;        
    }

    @Override
    public String getUltimaDiaFecha(String strAnio, String strMes) {
        String strDia, strFecha="";
        Date dtFecha = null;
        
        Calendar calBase = Calendar.getInstance();
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        
        calBase.set(Integer.parseInt(strAnio), Integer.parseInt(strMes)-1, 1);
        calBase.set(Integer.parseInt(strAnio), Integer.parseInt(strMes)-1, calBase.getActualMaximum(Calendar.DAY_OF_MONTH));
        
        strDia = Integer.toString(calBase.get(Calendar.DATE));
        strMes = Integer.toString(calBase.get(Calendar.MONTH)+1);
        strAnio = Integer.toString(calBase.get(Calendar.YEAR));
        
        if (Integer.parseInt(strMes) < 10){
            strMes = "0" + strMes;
        }
        
        try{
            dtFecha = formato.parse(strAnio + "-" + strMes + "-" + strDia);
        }catch(ParseException  pe){
            pe.printStackTrace();
        }

        strFecha = formato.format(dtFecha);
        
        return strFecha;
    }

    @Override
    public Date getDateFromString(String strFecha) {
        Date dtFecha = null;
        
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        
        try{
            dtFecha = formato.parse(strFecha);
        }catch(ParseException  pe){
            pe.printStackTrace();
        }
        
        return dtFecha;
    }

    @Override
    public long getDiasDiferenciaFechas(Date dtFechaInicio, Date dtFechaFin) {
        long lgDiferencia = 0;
        
        final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; //Milisegundos al día 
        lgDiferencia = ( dtFechaFin.getTime() - dtFechaInicio.getTime() )/MILLSECS_PER_DAY;
                  
        return lgDiferencia;
    }

    @Override
    public long getDiasDiferenciaFechasEspecial(Date dtFechaInicio, Date dtFechaFin) {
        long lgDiferencia = 0;
        
        final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000; //Milisegundos al día 
        lgDiferencia = ( dtFechaFin.getTime() - dtFechaInicio.getTime() )/MILLSECS_PER_DAY;
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(dtFechaInicio);
        int month = cal.get(Calendar.MONTH);
                        
        if ((month == 1) && (lgDiferencia == 27 || lgDiferencia == 28 || lgDiferencia == 29 )){
            lgDiferencia = 30;
        }else{
            if (lgDiferencia >= 29){
                lgDiferencia = 30;
            }
        }
        
        return lgDiferencia;
    }

    @Override
    public int getDiaFromFecha(String strFecha) {
        Date dtFecha = null;        
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        
        try{
            dtFecha = formato.parse(strFecha);
        }catch(ParseException  pe){
            pe.printStackTrace();
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(dtFecha);
        int dia = cal.get(Calendar.DATE);
        
        return dia;
    }

    @Override
    public Calendar calcularFechaActual() {
        String strFechaActual= null;
        String[] strTemp = null;

        Calendar fechaActual = Calendar.getInstance();
        strFechaActual = getFechaActual();
        strTemp = strFechaActual.split("-");                           
        fechaActual.set(Integer.parseInt(strTemp[0]),(Integer.parseInt(strTemp[1])-1),Integer.parseInt(strTemp[2]));

        fechaActual.set(Calendar.SECOND, 0);
        fechaActual.set(Calendar.MILLISECOND, 0);

        return fechaActual;
    }

    @Override
    public String getFechaActual() {
        //Obtener la fecha actual.
                
        Calendar c = Calendar.getInstance();
        String strDia, strMes, strAnio, strFechaActual;

        strDia = Integer.toString(c.get(Calendar.DATE));
        strMes = Integer.toString(c.get(Calendar.MONTH)+1);
        strAnio = Integer.toString(c.get(Calendar.YEAR));                

        if (Integer.parseInt(strMes) < 10){
            strMes = "0" + strMes;
        }
        
        if (Integer.parseInt(strDia) <=9){
            strDia = "0" + strDia;
        }

        strFechaActual = strAnio + "-" + strMes + "-" + strDia;
        
        return strFechaActual;
    }

    @Override
    public String getFechaHoraActual() {
        String strFechaHora = "";
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        Date date = new Date(); 
        strFechaHora = dateFormat.format(date);
                        
        return strFechaHora;
    }

    @Override
    public String marcarMiles(String strValor) {
        int intValor;
        DecimalFormat formateador = new DecimalFormat("###,###.##");
        
        intValor = Integer.parseInt(strValor);

        return (formateador.format (intValor).toString());
    }

    @Override
    public double redondear(double valueToRound, int numberOfDecimalPlaces) {
        double multipicationFactor = Math.pow(10, numberOfDecimalPlaces);
        double interestedInZeroDPs = valueToRound * multipicationFactor;
        return Math.round(interestedInZeroDPs) / multipicationFactor;
    }

    @Override
    public Vector obtenerDiasFeriados() {
        Vector arrFechas = new Vector();
        String strSQL = "SELECT dtFecha from users.users_dias_no_habiles order by dtFecha";
        String COLUMNA_FECHA = "dtFecha";
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;     
             
        try{
            con = getConexion(ID_BASE_DATOS);
            ps = con.prepareCall(strSQL);
            
            rs = ps.executeQuery();
            
            if (rs != null){
                while (rs.next()){                                      
                    arrFechas.add(rs.getDate(COLUMNA_FECHA));
                }
            }
            
        }catch(SQLException e){
            new GIDaoException("No se recuperaron los días feriados.",e);
        }catch(GIDaoException e){
            new GIDaoException("No se recuperaron los días feriados.",e);
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
                new GIDaoException("No se cerraron correctamente los objetos de conexión.",e);
            }
        }
        
        return arrFechas;
    }

    @Override
    public Boolean esDiaHabil(String strFecha) {
        
        String strAnio, strMes, strDia;
        String[] strTemp = null;
        Boolean diaHabil = Boolean.FALSE;
        Vector arrFeriados = obtenerDiasFeriados();
        Calendar fechaFeriada = Calendar.getInstance();        
                     
        strAnio = strFecha.substring(0,4);
        strMes= strFecha.substring(5,7);
        strDia = strFecha.substring(8,10);      
              
        Calendar fechaBase = Calendar.getInstance();
        fechaBase.set(Integer.parseInt(strAnio), Integer.parseInt(strMes)-1, Integer.parseInt(strDia));
        fechaBase.set(Calendar.SECOND, 0);
        fechaBase.set(Calendar.MILLISECOND, 0);              
         
        if (fechaBase.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fechaBase.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY){               
            diaHabil=Boolean.TRUE;            
                        
            for (int i=0;i<arrFeriados.size();i++){

                 strTemp = arrFeriados.get(i).toString().split("-");
                 fechaFeriada.set(Integer.parseInt(strTemp[0]), Integer.parseInt(strTemp[1]) - 1, Integer.parseInt(strTemp[2]));

                 fechaFeriada.set(Calendar.SECOND, 0);
                 fechaFeriada.set(Calendar.MILLISECOND, 0);                                   

                if (fechaFeriada.getTime().toString().equals(fechaBase.getTime().toString())){                    
                    diaHabil=Boolean.FALSE;
                    break;
                }

                strTemp = null;
                fechaFeriada = Calendar.getInstance();                    
            }                                
        }else{                     
            diaHabil = Boolean.FALSE;
        }
        
        return diaHabil;
    }    

    @Override
    public Date getFechaActualDate() {
        String strFechaActual = getFechaActual();
        Date dtFechaActual = null;
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        
        try{
            dtFechaActual = formato.parse(strFechaActual);
        }catch(ParseException pe){
            new GIDaoException("Se generó un error parseando la fecha actual",pe);
        }
        
        return dtFechaActual;
        
    }

    @Override
    public int getMesFromFecha(String strFecha) {
        Date dtFecha = null;        
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        
        try{
            dtFecha = formato.parse(strFecha);
        }catch(ParseException  pe){
            pe.printStackTrace();
        }
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(dtFecha);
        int mes = cal.get(Calendar.MONTH);
        
        return mes;
    }

    @Override
    public String reemplazarUltimoCaracterCadena(String strCadena) {
        String strNuevaCadena = "";
        
        strNuevaCadena = strCadena.substring(0, strCadena.length()-1); 
               
        return strNuevaCadena;
    }

    @Override
    public Boolean tieneDecimales(BigDecimal bdValor) {
        Boolean tieneDecimales = Boolean.FALSE;
       BigDecimal result = null;
               
      result = bdValor.subtract(bdValor.setScale(0, RoundingMode.FLOOR)).movePointRight(bdValor.scale());      
            
        if (result.compareTo(BigDecimal.ZERO) > 0){
            tieneDecimales = Boolean.TRUE;
        }
                              
       return tieneDecimales;
    }

    @Override
    public String convertirFechaLarga(String strFecha) {
        String strAnio=null, strMes=null, strDia=null;
        String[] strTemp = null;
        DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy", Locale.US);
        Date date = null;
        
        try{
            date = (Date)formatter.parse(strFecha);
        }catch(ParseException pe){
            new GIDaoException("", pe);
        }         

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String formatedDate = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" +  cal.get(Calendar.DATE);
        
        strTemp = formatedDate.split("-");
        strAnio = strTemp[0];
        strMes = strTemp[1];
        strDia = strTemp[2];
        
        if (Integer.parseInt(strMes) <=9){
            strMes = "0" + strMes;
        }
        
        if (Integer.parseInt(strDia) <=9){
            strDia = "0" + strDia;
        }

        formatedDate = strAnio + "-" + strMes + "-" + strDia;
        
        return formatedDate;
    }

    @Override
    public String convertirNotacionEACadena(double valor) {
        
        String strCadena = "";
        
        strCadena = String.valueOf(new Double(valor).longValue());
        
        return strCadena;        
    }

    @Override
    public Date aumentarAniosFecha(Date dtFechaBase, int intNumAnios) {
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dtFechaBase);
        calendar.add(Calendar.YEAR, intNumAnios);
        return calendar.getTime();        
    }
    
}
