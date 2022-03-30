/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.jobs;

import co.edu.udea.solucionessiu.dao.FuncionesComunesDAO;
import co.edu.udea.solucionessiu.dao.InformeProyectoTotalProyectosDAO;
import co.edu.udea.solucionessiu.dao.NotificacionDAO;
import co.edu.udea.solucionessiu.dao.ProyectoTotalProyectosDAO;
import co.edu.udea.solucionessiu.dao.impl.FuncionesComunesDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.InformeProyectoTotalProyectosDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.NotificacionDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.ProyectoTotalProyectosDAOImpl;
import co.edu.udea.solucionessiu.dto.FechaProyectoTotalProyectos;
import co.edu.udea.solucionessiu.dto.InformeProyectoTotalProyectos;
import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.dto.ProyectoTotalProyectos;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jorge.correa
 */
public class PruebaNotificarFechasProyectosTotalProyectos {
    
    private static final String CODIGO_FECHA_INICIO = "FINI";
    private static final String CODIGO_FECHA_FIN = "FFIN";
    private static final String CODIGO_FECHA_COMPROMISOS = "FPLAZ";
    private static final String CODIGO_FECHA_REPORTE = "FENTR";
    
    public static void main(String[] args){
        
        new GIDaoException("Iniciando tarea NotificarFechasProyectosTotalProyectos");
        
        String strFechaActual;
        Integer intNumDiasNotificar, intIdProyecto;
        Date dtFechaActual = null;       
        String strIdBD = "totalproyectos";
                
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setTimeZone(java.util.TimeZone.getTimeZone("GMT-5"));
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        strFechaActual = funcionesComunesDAO.getFechaActual();
               
        System.out.println("Fecha actual: " + strFechaActual);
        
        try{
            dtFechaActual = sdf.parse(strFechaActual);
        }catch(ParseException pe){
            new GIDaoException("Se generó un error parseando la fecha actual", pe);
        }
        
        ProyectoTotalProyectosDAO proyectoTotalProyectosDAO = new ProyectoTotalProyectosDAOImpl();
        List<ProyectoTotalProyectos> proyectosActivos = null;
        
        InformeProyectoTotalProyectosDAO informeProyectoTotalProyectosDAO = new InformeProyectoTotalProyectosDAOImpl();
        List<InformeProyectoTotalProyectos> informesProyectos = null;
        
        List<FechaProyectoTotalProyectos> fechasProyectos = new ArrayList<FechaProyectoTotalProyectos>();
        
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        Notificacion notificacion = null;
        
        try{
            proyectosActivos = proyectoTotalProyectosDAO.obtenerActivos();
        }catch(GIDaoException gi){
            new GIDaoException("Se generó un error al intentar recuperar los proyectos activos");
            proyectosActivos = null;
        }
        
        if (proyectosActivos != null){            
            for (ProyectoTotalProyectos proyecto : proyectosActivos){
                
                intIdProyecto = proyecto.getId();
                
                FechaProyectoTotalProyectos fechaInicioProyecto = new FechaProyectoTotalProyectos();       
                fechaInicioProyecto.setIdProyecto(intIdProyecto);
                fechaInicioProyecto.setFecha(proyecto.getStartdate());
                fechaInicioProyecto.setTipoFecha("FECHAINICIO");
                fechasProyectos.add(fechaInicioProyecto);
                
                FechaProyectoTotalProyectos fechaFinProyecto = new FechaProyectoTotalProyectos();       
                fechaFinProyecto.setIdProyecto(intIdProyecto);
                fechaFinProyecto.setFecha(proyecto.getEnddatedef());
                fechaFinProyecto.setTipoFecha("FECHAFIN");
                fechasProyectos.add(fechaFinProyecto);
                
                FechaProyectoTotalProyectos fechaCompromisosProyecto = new FechaProyectoTotalProyectos();       
                fechaCompromisosProyecto.setIdProyecto(intIdProyecto);
                fechaCompromisosProyecto.setFecha(proyecto.getCommitments());
                fechaCompromisosProyecto.setTipoFecha("FECHACOMPROMISOS");
                fechasProyectos.add(fechaCompromisosProyecto);
                
                try{
                    informesProyectos = informeProyectoTotalProyectosDAO.obtenerPorProyecto(intIdProyecto);
                }catch(GIDaoException gi){
                    new GIDaoException("Se generó un error al intentar recuperar los informes para el proyecto con ID: " + intIdProyecto.toString());
                    informesProyectos = null;
                }
                
                if (informesProyectos != null){
                    
                    for (InformeProyectoTotalProyectos informe : informesProyectos){
                        
                        FechaProyectoTotalProyectos fechaInformeProyecto = new FechaProyectoTotalProyectos(); 
                        fechaInformeProyecto.setIdProyecto(intIdProyecto);
                        fechaInformeProyecto.setFecha(informe.getReportdate());
                        fechaInformeProyecto.setTipoFecha("FECHAINFORME");
                        fechasProyectos.add(fechaInformeProyecto);
                    }
                }                
            }
        }                                                    
        
        if (fechasProyectos.size() > 0){
            
            Integer intDiasAntes = 0;
            Long lngEpochTime = 0L, lgDiasDiferencia= 0L;
            String strFecha = "", strIdCodigoNotificacion = "";
            Date dtFechaProyecto;
            
            for (FechaProyectoTotalProyectos fechaProyecto : fechasProyectos){
                
                lngEpochTime = Long.parseLong(fechaProyecto.getFecha());
                strFecha = funcionesComunesDAO.getDateFromEpochTime(lngEpochTime, sdf);
                dtFechaProyecto = funcionesComunesDAO.getDateFromString(strFecha);
                
                if (dtFechaActual.before(dtFechaProyecto)){
                    if (fechaProyecto.getTipoFecha().equals("FECHAINICIO")){
                        strIdCodigoNotificacion = CODIGO_FECHA_INICIO;
                    }
                    
                    if (fechaProyecto.getTipoFecha().equals("FECHAFIN")){
                        strIdCodigoNotificacion = CODIGO_FECHA_FIN;
                    }
                    
                    if (fechaProyecto.getTipoFecha().equals("FECHACOMPROMISOS")){
                        strIdCodigoNotificacion = CODIGO_FECHA_COMPROMISOS;
                    }
                    
                    if (fechaProyecto.getTipoFecha().equals("FECHAINFORME")){
                        strIdCodigoNotificacion = CODIGO_FECHA_REPORTE;
                    }
                    
                    try{
                        notificacion = notificacionDAO.obtenerUnoTotalProyectos(strIdCodigoNotificacion);
                    }catch(GIDaoException gi){
                        new GIDaoException("No se pudo recuperar la notificación con código " + strIdCodigoNotificacion,gi);
                        notificacion = null;
                    }
                    
                    if (notificacion != null){
                        intDiasAntes = notificacion.getDiasNotificar();                                                                   
                        lgDiasDiferencia = (Long)(funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaActual, dtFechaProyecto));
                                            
                        if (lgDiasDiferencia.toString().equals(intDiasAntes.toString())){
                            
                        }
                    }
                }
               
                lngEpochTime = 0L;
                strFecha = "";
                dtFechaProyecto = null;
            }
        }
        
        new GIDaoException("Total de proyectos recuperados: " + proyectosActivos.size());
        new GIDaoException("Finalizando tarea NotificarFechasProyectosTotalProyectos ");
    }
    
}