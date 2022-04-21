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
        
        String strFechaActual, strFechaFinalizacionDef;
        Integer intIdProyecto, intIdEstadoProyecto, intAfectados, intTotalAfectados = 0;
        Date dtFechaActual = null, dtFechaFinDefProyecto = null;       
        Long lngEpochTime = 0L;
                        
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
        List<ProyectoTotalProyectos> proyectosFinalizados = null;               
        
        try{
            proyectosFinalizados = proyectoTotalProyectosDAO.obtenerFinalizados();
        }catch(GIDaoException gi){
            new GIDaoException("Se generó un error al intentar recuperar los proyectos finalizados desde la base de datos");
            proyectosFinalizados = null;
        }
        
        if (proyectosFinalizados != null){    
            
            new GIDaoException("Total proyectos finalizados recuperados: " + proyectosFinalizados.size());
            
            for (ProyectoTotalProyectos proyecto : proyectosFinalizados){
                
                intIdProyecto = proyecto.getId();
                intIdEstadoProyecto = proyecto.getStatusid();
                strFechaFinalizacionDef = proyecto.getEnddatedef();                         
                
                lngEpochTime = Long.parseLong(strFechaFinalizacionDef);
                strFechaFinalizacionDef = funcionesComunesDAO.getDateFromEpochTime(lngEpochTime, sdf);
                dtFechaFinDefProyecto = funcionesComunesDAO.getDateFromString(strFechaFinalizacionDef);
                
                new GIDaoException("El proyecto con id " + intIdProyecto + " se encuentra en el estado " + intIdEstadoProyecto + " y fecha de finalización " + strFechaFinalizacionDef);
               
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
                lngEpochTime = 0L;
                dtFechaFinDefProyecto = null;
               
            }
        }                                                   
        
        new GIDaoException("Total de proyectos afectados: " + intTotalAfectados);
        new GIDaoException("Finalizando tarea ActualizarEstadoProyectosTotalProyectos ");
    }
    
}