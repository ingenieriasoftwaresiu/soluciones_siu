/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.jobs;

import co.edu.udea.solucionessiu.dao.FuncionesComunesDAO;
import co.edu.udea.solucionessiu.dao.ProyectoTotalProyectosDAO;
import co.edu.udea.solucionessiu.dao.impl.FuncionesComunesDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.ProyectoTotalProyectosDAOImpl;
import co.edu.udea.solucionessiu.dto.ProyectoTotalProyectos;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jorge.correa
 */
public class PruebaActualizarEstadoProyectosTotalProyectos {       
    
    public static void main(String[] args){
        
        new GIDaoException("Iniciando tarea ActualizarEstadoProyectosTotalProyectos");
        
        String strFechaActual, strFechaFinalizacionDef, strFechaInicio;
        Integer intIdProyecto, intIdEstadoProyecto, intAfectados, intTotalAfectados = 0;
        Date dtFechaActual = null, dtFechainicioProyecto = null, dtFechaFinDefProyecto = null;       
        Long lngEpochTimeFechaFin = 0L, lngEpochTimeFechaInicio = 0L;
                        
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
        List<ProyectoTotalProyectos> proyectosTodos = null;               
        
        try{
            proyectosTodos = proyectoTotalProyectosDAO.obtenerTodos();
        }catch(GIDaoException gi){
            new GIDaoException("Se generó un error al intentar recuperar todos los proyectos desde la base de datos");
            proyectosTodos = null;
        }
        
        if (proyectosTodos != null){    
            
            new GIDaoException("Total proyectos recuperados: " + proyectosTodos.size());
            
            for (ProyectoTotalProyectos proyecto : proyectosTodos){
                
                intIdProyecto = proyecto.getId();
                intIdEstadoProyecto = proyecto.getStatusid();
                strFechaInicio = proyecto.getStartdate();
                strFechaFinalizacionDef = proyecto.getEnddatedef();     
                
                lngEpochTimeFechaInicio = Long.parseLong(strFechaInicio);
                strFechaInicio = funcionesComunesDAO.getDateFromEpochTime(lngEpochTimeFechaInicio, sdf);
                dtFechainicioProyecto = funcionesComunesDAO.getDateFromString(strFechaInicio);
                
                lngEpochTimeFechaFin = Long.parseLong(strFechaFinalizacionDef);
                strFechaFinalizacionDef = funcionesComunesDAO.getDateFromEpochTime(lngEpochTimeFechaFin, sdf);
                dtFechaFinDefProyecto = funcionesComunesDAO.getDateFromString(strFechaFinalizacionDef);
                
                new GIDaoException("El proyecto con id " + intIdProyecto + " se encuentra en el estado " + intIdEstadoProyecto);
                new GIDaoException("Fecha de inicio " + strFechaInicio + " y fecha de finalización " + strFechaFinalizacionDef);
                
                // Proyectos con fecha de inicio menor a la actual y en estado Creado (5), deben pasar a Activo (1).
                if (dtFechainicioProyecto.before(dtFechaActual) && intIdEstadoProyecto.toString().equals("5")){
                    try{
                        intAfectados = proyectoTotalProyectosDAO.actualizarEstadoProyecto(proyecto, 1);
                        
                        if (intAfectados > 0){
                            new GIDaoException("Se cambió el estado del proyecto con código " + proyecto.getSiucode() + " de Creado a Activo!");
                            intTotalAfectados++;
                        }
                        
                    }catch(GIDaoException gi){
                        new GIDaoException("No se pudo actualizar el estado del proyecto con id " + intIdProyecto.toString());
                    } 
                }
                
                // Proyectos con fecha de finalización definitiva menor a la actual y en estado Activo (1), deben pasar a Finalizado (2).
                if (dtFechaFinDefProyecto.before(dtFechaActual) && intIdEstadoProyecto.toString().equals("1")){
                    try{
                        intAfectados = proyectoTotalProyectosDAO.actualizarEstadoProyecto(proyecto, 2);
                        
                        if (intAfectados > 0){
                            new GIDaoException("Se cambió el estado del proyecto con código " + proyecto.getSiucode() + " de Activo a Finalizado!");
                            intTotalAfectados++;
                        }
                        
                    }catch(GIDaoException gi){
                        new GIDaoException("No se pudo actualizar el estado del proyecto con id " + intIdProyecto.toString());
                    }
                }
                               
                // Proyectos con fecha de finalización definitiva mayor a la actual y en estado Finalizado (2), deben pasar a Activo (1) (Adición de prórroga).
                if (dtFechaActual.before(dtFechaFinDefProyecto) && intIdEstadoProyecto.toString().equals("2")){
                    
                    try{
                        intAfectados = proyectoTotalProyectosDAO.actualizarEstadoProyecto(proyecto, 1);
                        
                        if (intAfectados > 0){
                            new GIDaoException("Se cambió el estado del proyecto con código " + proyecto.getSiucode() + " de Finalizado a Activo!");
                            intTotalAfectados++;
                        }
                        
                    }catch(GIDaoException gi){
                        new GIDaoException("No se pudo actualizar el estado del proyecto con id " + intIdProyecto.toString());
                    }                         
                }
                
                intIdProyecto = 0;
                intIdEstadoProyecto = 0;
                strFechaFinalizacionDef = "";
                lngEpochTimeFechaFin = 0L;
                lngEpochTimeFechaInicio = 0L;
                dtFechaFinDefProyecto = null;
                dtFechainicioProyecto = null;               
            }
        }                                                   
        
        new GIDaoException("Total de proyectos afectados: " + intTotalAfectados);
        new GIDaoException("Finalizando tarea ActualizarEstadoProyectosTotalProyectos ");
    }
    
}