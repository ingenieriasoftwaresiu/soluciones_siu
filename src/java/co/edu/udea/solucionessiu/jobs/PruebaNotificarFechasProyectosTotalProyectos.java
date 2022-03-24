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
    
    public static void main(String[] args){
        
        new GIDaoException("Iniciando tarea NotificarFechasProyectosTotalProyectos");
        
        String strFechaActual;
        Integer intNumDiasNotificar, intIdProyecto;
        Date dtFechaActual = null;       
        String strIdBD = "totalproyectos";
                
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
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
            
        }
        
        new GIDaoException("Total de proyectos recuperados: " + proyectosActivos.size());
        new GIDaoException("Finalizando tarea NotificarFechasProyectosTotalProyectos ");
    }
    
}