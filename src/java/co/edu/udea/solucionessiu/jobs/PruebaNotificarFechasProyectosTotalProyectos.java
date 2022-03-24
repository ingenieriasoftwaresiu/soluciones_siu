/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.jobs;

import co.edu.udea.solucionessiu.dao.FuncionesComunesDAO;
import co.edu.udea.solucionessiu.dao.MovimientoDAO;
import co.edu.udea.solucionessiu.dao.NotificacionDAO;
import co.edu.udea.solucionessiu.dao.NotificacionMailSigepDAO;
import co.edu.udea.solucionessiu.dao.ProyectoTotalProyectosDAO;
import co.edu.udea.solucionessiu.dao.ReservasNotificadasDAO;
import co.edu.udea.solucionessiu.dao.impl.FuncionesComunesDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.MovimientoDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.NotificacionDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.NotificacionMailSigepDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.ProyectoTotalProyectosDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.ReservasNotificadasDAOImpl;
import co.edu.udea.solucionessiu.dto.Movimiento;
import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.dto.ProyectoTotalProyectos;
import co.edu.udea.solucionessiu.dto.ReservaNotificada;
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
        Integer intNumDiasNotificar;
        Date dtFechaActual = null;
        
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

            }
        }                                                    
        
        new GIDaoException("Total de proyectos recuperados: " + proyectosActivos.size());
        new GIDaoException("Finalizando tarea NotificarFechasProyectosTotalProyectos ");
    }
    
}