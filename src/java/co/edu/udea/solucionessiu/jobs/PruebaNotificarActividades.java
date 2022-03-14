/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.jobs;

import co.edu.udea.solucionessiu.dao.ActividadDAO;
import co.edu.udea.solucionessiu.dao.FuncionesComunesDAO;
import co.edu.udea.solucionessiu.dao.NotificacionMailSigepDAO;
import co.edu.udea.solucionessiu.dao.ParametroGeneralDAO;
import co.edu.udea.solucionessiu.dao.impl.ActividadDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.FuncionesComunesDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.NotificacionMailSigepDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.ParametroGeneralDAOImpl;
import co.edu.udea.solucionessiu.dto.Actividad;
import co.edu.udea.solucionessiu.dto.ParametroGeneral;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jorge.correa
 */
public class PruebaNotificarActividades {
    
    public static void main(String[] args){
        
        new GIDaoException("Iniciando tarea NotificarActividades");
        
        String strFechaActual, strAccionNotificar, strCodigoActividad;
        Integer intTotalActividadesNotificadas;
        Date dtFechaActual = null, dtFechaFin = null;
        Long lgNumDiasDiferencia, LgNumDiasNotificar;
                        
        strAccionNotificar = null;
        intTotalActividadesNotificadas = 0;
        LgNumDiasNotificar = 0L;
        ParametroGeneralDAO parametroGeneralDAO = new ParametroGeneralDAOImpl();
        ParametroGeneral parametroGeneral = null;
        
        NotificacionMailSigepDAO notificacionMailDAO = new NotificacionMailSigepDAOImpl();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        ActividadDAO actividadDAO = new ActividadDAOImpl();
        List<Actividad> actividades = null;
        
        /*
            Obtener la fecha actual del sistema.
        */
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        strFechaActual = funcionesComunesDAO.getFechaActual();
        
        System.out.println("Fecha actual: " + strFechaActual);
        
        try{
            parametroGeneral = parametroGeneralDAO.obtenerParametrosGeneralesSigep();
        }catch(GIDaoException e){
            new GIDaoException("Se generó un error al recuperar los parámetros generales de la solución.", e);
            parametroGeneral = null;
        }
        
        if (parametroGeneral != null){
            
           //LgNumDiasNotificar = Long.valueOf(parametroGeneral.getNumDiasNotificarActividad().toString());
           LgNumDiasNotificar = 15L;
           System.out.println("Número de días: " + LgNumDiasNotificar);
            
            try{
                actividades = actividadDAO.obtenerPorFecha(strFechaActual, ">");
            }catch(GIDaoException e){
                new GIDaoException("Se generó un error al recuperar todas las actividades mayores a la fecha " + strFechaActual + ".", e);
                actividades = null;            
            }
            
            if (actividades != null && actividades.size() > 0){                
                                
                try{
                    dtFechaActual = sdf.parse(strFechaActual);
                }catch(ParseException pe){
                    new GIDaoException("Se generó un error parseando la fecha actual", pe);
                }
                
                for (Actividad actividad : actividades){
                    
                    strCodigoActividad = actividad.getCodigo().toString();
                    System.out.println("Código de la actividad: " + strCodigoActividad);
                    
                    if (actividad.getFechaFin() != null){                    
                        dtFechaFin = actividad.getFechaFin();
                        System.out.println("dtFechaFin: " + dtFechaFin);
                        
                        lgNumDiasDiferencia = funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaActual, dtFechaFin);
                        System.out.println("lgNumDiasDiferencia: " + lgNumDiasDiferencia);
                        
                        if (lgNumDiasDiferencia >= 0){
                            
                            if ((lgNumDiasDiferencia.toString().equals("0")) || (lgNumDiasDiferencia.toString().equals(LgNumDiasNotificar.toString()))){
                               
                                if (lgNumDiasDiferencia.toString().equals("0")){
                                    strAccionNotificar = "ALDIA";
                                }
                                
                                if (lgNumDiasDiferencia.toString().equals(LgNumDiasNotificar.toString())){
                                    strAccionNotificar = "ANTES";
                                }
                                
                                try{
                                    System.out.println("Notificando actividad con código: " + strCodigoActividad + " - " + strAccionNotificar);
                                    notificacionMailDAO.notificarActividades(actividad, strAccionNotificar);
                                    intTotalActividadesNotificadas++;                        
                                }catch(GIDaoException e){
                                  new GIDaoException("Se generó un error al enviar la notificación para la actividad con código " + strCodigoActividad + ".",e);
                                }                                
                            }                            
                        }                        
                                                
                        dtFechaFin = null;
                        lgNumDiasDiferencia = 0L;
                        actividad = null;
                        strAccionNotificar = null;
                        
                    }else{
                        dtFechaFin = null;
                    }                            
                    
                    strCodigoActividad = null;
                }
            }else{
                new GIDaoException("No se recuperaron actividades que cumplan con los criterios de selección.");
            }            
        }else{
            new GIDaoException("No se recuperaron los parámetros generales de la solución!.");
        }
        
        new GIDaoException("Total actividades notificadas: " + intTotalActividadesNotificadas);            
        new GIDaoException("Finalizando tarea NotificarActividades");
    }
    
}
