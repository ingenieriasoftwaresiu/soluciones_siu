/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dao;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 *
 * @author George
 */
public interface FuncionesComunesDAO {
    public Calendar incrementarDiasHabiles(Calendar fechaInicial, int intNroDias);
    public int getDiasHabiles(Calendar fechaInicial, Calendar fechaFinal);
    public String aumentarDiasFecha(String strFechaBase, int intNumDias);
    public Date aumentarAniosFecha(Date dtFechaBase, int intNumAnios);
    public double redondearDecimales(double dblValor, int intNumDecimales);
    public String getUltimaDiaFecha(String strAnio, String strMes);
    public Date getDateFromString(String strFecha);
    public long getDiasDiferenciaFechas(Date dtFechaInicio, Date dtFechaFin);
    public long getDiasDiferenciaFechasEspecial(Date dtFechaInicio, Date dtFechaFin);
    public int getDiaFromFecha(String strFecha);
    public int getMesFromFecha(String strFecha);
    public Calendar calcularFechaActual();
    public String getFechaActual();
    public Date getFechaActualDate();
    public String getFechaHoraActual();
    public String marcarMiles(String strValor);
    public double redondear(double valueToRound, int numberOfDecimalPlaces);    
    public Vector obtenerDiasFeriados();
    public Boolean esDiaHabil(String strFecha);
    public String reemplazarUltimoCaracterCadena(String strCadena);
    public Boolean tieneDecimales(BigDecimal bdValor);
    public String convertirFechaLarga(String strFecha);
    public String convertirNotacionEACadena(double valor);
}
