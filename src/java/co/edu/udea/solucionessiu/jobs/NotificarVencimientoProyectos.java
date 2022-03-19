/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.jobs;

import co.edu.udea.solucionessiu.dao.ComunesDAO;
import co.edu.udea.solucionessiu.dao.EjecucionPptalProyectoDAO;
import co.edu.udea.solucionessiu.dao.EtapaDAO;
import co.edu.udea.solucionessiu.dao.FuncionesComunesDAO;
import co.edu.udea.solucionessiu.dao.NotificacionMailSigepDAO;
import co.edu.udea.solucionessiu.dao.ParametroGeneralDAO;
import co.edu.udea.solucionessiu.dao.PersonaDAO;
import co.edu.udea.solucionessiu.dao.ProyectoDAO;
import co.edu.udea.solucionessiu.dao.impl.ComunesDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.EjecucionPptalProyectoDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.EtapaDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.FuncionesComunesDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.NotificacionMailSigepDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.ParametroGeneralDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.PersonaDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.ProyectoDAOImpl;
import co.edu.udea.solucionessiu.dto.EjecucionPptalProyecto;
import co.edu.udea.solucionessiu.dto.Etapa;
import co.edu.udea.solucionessiu.dto.Persona;
import co.edu.udea.solucionessiu.dto.ProyectoSIGEP;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author jorge.correa
 */
public class NotificarVencimientoProyectos implements Job{
       
    private static final String CODIGO_ESTADO_ACTIVO = "1";
    private static final String TIPO_PROYECTO_DIFERENTE = "11";
    private static final Integer CODIGO_ADMON_DEPENDENCIA = 2;
    private static final Integer CODIGO_ROL_INVESTIGADOR_PRINCIPAL = 4;
    //private static final String CODIGO_COORDINADOR_COMPRAS = "31575127";
        
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        
        new GIDaoException("Iniciando tarea NotificarVencimientoProyectos");
                    
        Date dtFechaActual = null;
        List<Persona> administradores_dependencia = null;
        List<ProyectoSIGEP> proyectos = null;
        String strIdProyecto = null, strMensaje = null, strAccion=null;
        Integer intTotalProyectosNotificados=0, intTotalProyectoExentos=0;
        Date dtFechaFinalizacion = null;
        Long lgNumDias = null;
        Boolean notificarProyecto = Boolean.FALSE;
        Persona investigadorPpal = null;
        Persona admonProyecto = null;
        Persona admonDependencia = null;
        //Persona coordCompras = null;
        final String NUMERO_DIAS_SEMESTRE ="180"; // 258 días para proyectos de extensión 2015. Fecha: 17-04-2015.
        final String NUMERO_DIAS_TRIMESTRE ="90"; // 89 días para el proyecto: 4-401490. Fecha: 15-04-2015.
        //final String NUMERO_DIAS_BIMESTRE ="60";
        final String NUMERO_DIAS_MES ="30"; // 29 días para el proyecto: 171443 y 382468. Fecha: 15-04-2015.
        final String NUMERO_DIAS_FINALIZADO = "0"; // 5 días para el proyecto: . Fecha: 15-04-2015.
        
        EtapaDAO etapaDAO = new EtapaDAOImpl();
        Etapa etapa = null;
        
        ParametroGeneralDAO parametroGeneralDAO = new ParametroGeneralDAOImpl();      
        
        EjecucionPptalProyectoDAO ejecucionPptalProyectoDAO = new EjecucionPptalProyectoDAOImpl();
        List<EjecucionPptalProyecto> ejecucionesPptalesProyecto = null;
        
        NotificacionMailSigepDAO notificacionMailDAO = new NotificacionMailSigepDAOImpl();
        
        ComunesDAO comunesDAO = new ComunesDAOImpl();
        
        /*
            Obtener todos los proyectos que se encuentren en estado 1=Activo.
        */

        ProyectoDAO proyectoDAO = new ProyectoDAOImpl();
        proyectos = new ArrayList<ProyectoSIGEP>();

        try{
            proyectos = proyectoDAO.obtenerPorEstadoYTipoProyectoDiferente(CODIGO_ESTADO_ACTIVO,TIPO_PROYECTO_DIFERENTE);
        }catch(GIDaoException e){
            new GIDaoException("Se generó un error obteniendo los proyectos en estado Activo", e);
            return;
        }                
        
        /*
            Obtener la fecha actual del sistema.
        */
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        dtFechaActual = funcionesComunesDAO.getFechaActualDate();
        
        /*
            Obtener el Administrador de la Dependencia.
        */
        
        PersonaDAO personaDAO = new PersonaDAOImpl();
        administradores_dependencia = new ArrayList<Persona>();
        
        try{
            administradores_dependencia = personaDAO.obtenerPorNivel(CODIGO_ADMON_DEPENDENCIA);
        }catch(GIDaoException e){
            new GIDaoException("Se generó un error obteniendo el Administrador de la Dependencia", e);
            return;
        }               
        
        if (administradores_dependencia != null){
            admonDependencia = administradores_dependencia.get(0);
        }
        
        /*
            Obtener el Coordinador de Compras.
        */
        
        /*try{
            coordCompras = personaDAO.obtenerUna(CODIGO_COORDINADOR_COMPRAS);
        }catch(GIDaoException e){
            new GIDaoException("Se generó un error obteniendo el Coordinador de Compras", e);
            return;
        }*/ 
                                        
        /*
            Procesamiento de cada proyecto activo recuperado.
        */
        
        if ((proyectos != null) && (proyectos.size() > 0)){
            
            for (ProyectoSIGEP proyecto : proyectos){
                
                strIdProyecto = proyecto.getCodigo().trim();                
                                
                /*
                    Verificar si el proyecto se notifica o no.
                */
                
                try{                    
                    notificarProyecto = parametroGeneralDAO.verificarNotificacionProyecto(strIdProyecto);
                }catch(GIDaoException e){
                    new GIDaoException("Se generó un error al verificar la notificación del proyecto con código " + strIdProyecto, e);
                }
                
                if (notificarProyecto == Boolean.TRUE){                                        
                    
                    /*
                        Obtener la fecha de finalización del proyecto actual.
                    */

                    try{
                        etapa = etapaDAO.obtenerPorProyecto(strIdProyecto);
                     }catch(GIDaoException e){
                        new GIDaoException("Se generó un error obteniendo la etapa para el proyecto con código " + strIdProyecto, e);                    
                    }

                    if (etapa != null){
                        dtFechaFinalizacion = etapa.getFechaFin();                                        
                        lgNumDias = funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaActual, dtFechaFinalizacion);

                       if (lgNumDias >= 0){                               

                           if ((lgNumDias.toString().equals(NUMERO_DIAS_SEMESTRE)) || (lgNumDias.toString().equals(NUMERO_DIAS_TRIMESTRE)) || (lgNumDias.toString().equals(NUMERO_DIAS_MES)) || (lgNumDias.toString().equals(NUMERO_DIAS_FINALIZADO))){

                                /*
                                    Obtener el Investigador Principal del proyecto.
                                */
                               
                               try{
                                    investigadorPpal = comunesDAO.obtenerParticipanteProyecto(strIdProyecto, CODIGO_ROL_INVESTIGADOR_PRINCIPAL);
                                }catch(GIDaoException e){
                                    new GIDaoException("Se generó un error obteniendo el Investigador Principal para el proyecto con código " + strIdProyecto, e);                    
                                }                               

                                /*
                                    Obtener el Administrador del proyecto.
                                */
                                
                                try{
                                    admonProyecto = comunesDAO.obtenerGestorGrupo(strIdProyecto);
                                }catch(GIDaoException e){
                                    new GIDaoException("Se generó un error obteniendo el Gestor del proyecto con código " + strIdProyecto, e);                    
                                }
                                               
                                /*
                                    Calcular ejecución presupuestal del proyecto.
                                */                  

                                try{
                                    ejecucionesPptalesProyecto = ejecucionPptalProyectoDAO.calcularEjecucionPresupuestal(strIdProyecto);
                                }catch(GIDaoException e){
                                    new GIDaoException("Se generó un error obteniendo la ejecución presupuestal del proyecto con código " + strIdProyecto, e);                    
                                }

                                /*
                                    Enviar la notificación de vencimiento a los interesados.
                                */

                                try{

                                    if (lgNumDias.toString().equals(NUMERO_DIAS_SEMESTRE)){
                                        strAccion = "SEMESTRE";                                    
                                    }

                                    if (lgNumDias.toString().equals(NUMERO_DIAS_TRIMESTRE)){
                                        strAccion = "TRIMESTRE";                                    
                                    }
                                    
                                    /*if (lgNumDias.toString().equals(NUMERO_DIAS_BIMESTRE)){
                                        strAccion = "BIMESTRE";                                    
                                    }*/

                                    if (lgNumDias.toString().equals(NUMERO_DIAS_MES)){
                                        strAccion = "MES";                                    
                                    }

                                    if (lgNumDias.toString().equals(NUMERO_DIAS_FINALIZADO)){
                                        strAccion = "FINALIZADO";                                    
                                    }

                                    //notificacionMailDAO.notificarVencimientoProyecto(strAccion,proyecto,ejecucionesPptalesProyecto,admonDependencia,investigadorPpal,admonProyecto,coordCompras);
                                    notificacionMailDAO.notificarVencimientoProyecto(strAccion,proyecto,ejecucionesPptalesProyecto,admonDependencia,investigadorPpal,admonProyecto);
                                    intTotalProyectosNotificados++;

                                    /*
                                        Traza de envío de la notificación.
                                    */

                                    if ((lgNumDias.toString().equals(NUMERO_DIAS_SEMESTRE)) || (lgNumDias.toString().equals(NUMERO_DIAS_TRIMESTRE)) || (lgNumDias.toString().equals(NUMERO_DIAS_MES)) || (lgNumDias.toString().equals(NUMERO_DIAS_FINALIZADO))){
                                       strMensaje = "Se envió notificación del proyecto con codigo " + strIdProyecto + " a: ";
                                        
                                        if (admonDependencia != null){
                                            strMensaje += admonDependencia.getNombre() + " (" + admonDependencia.getEmail() + ") ";
                                        }
                                        
                                        if (investigadorPpal != null){
                                            strMensaje += investigadorPpal.getNombre() + " (" + investigadorPpal.getEmail() +") ";
                                        }
                                        
                                        if (admonProyecto != null){
                                            strMensaje += admonProyecto.getNombre() + " (" + admonProyecto.getEmail() + ")";                                                                                            
                                        }         
                                    }
                                    
                                    /*if (lgNumDias.toString().equals(NUMERO_DIAS_BIMESTRE)){
                                        strMensaje = "Se envio notificacion del proyecto con codigo " + strIdProyecto + " a: " + coordCompras.getNombre() + " (" + coordCompras.getEmail() + ")";                                                                                            
                                    }*/
                                    
                                    new GIDaoException(strMensaje);                                  

                                }catch(GIDaoException e){
                                    new GIDaoException("Se generó un error enviando la notificación para el proyecto con código " + strIdProyecto, e);                    
                                } 
                           }     
                       }                                                                             

                    }else{
                        new GIDaoException("El proyecto con codigo " + strIdProyecto + " no tiene configurada una etapa.");
                    }
                }else{
                    intTotalProyectoExentos++;
                    new GIDaoException("El proyecto con codigo " + strIdProyecto + " esta configurado como exento de notificacion.");
                }
                
                strIdProyecto = null;
                etapa = null;
                dtFechaFinalizacion = null;
                proyecto = null;
                lgNumDias = null;
                admonProyecto = null;
                investigadorPpal = null;              
                ejecucionesPptalesProyecto = null;
                strAccion = null;
                notificarProyecto = Boolean.FALSE;
            }
            
            new GIDaoException("Total proyectos notificados: " + intTotalProyectosNotificados);            
            new GIDaoException("Total proyectos exentos: " + intTotalProyectoExentos);
            new GIDaoException("Finalizando tarea NotificarVencimientoProyectos");
        }
    }    
}
    

