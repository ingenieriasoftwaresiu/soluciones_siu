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
import co.edu.udea.solucionessiu.dao.impl.ComunesDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.EjecucionPptalProyectoDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.EtapaDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.FuncionesComunesDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.NotificacionMailSigepDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.ParametroGeneralDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.PersonaDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.ProyectoSIGEPDAOImpl;
import co.edu.udea.solucionessiu.dto.EjecucionPptalProyecto;
import co.edu.udea.solucionessiu.dto.Etapa;
import co.edu.udea.solucionessiu.dto.PersonaSIGEP;
import co.edu.udea.solucionessiu.dto.ProyectoSIGEP;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import co.edu.udea.solucionessiu.dao.ProyectoSIGEPDAO;

/**
 *
 * @author jorge.correa
 */
public class PruebaNotificarVencimientoProyectos {
    
    private static final String CODIGO_ESTADO_ACTIVO = "1";
    private static final String TIPO_PROYECTO_DIFERENTE = "11";
    private static final Integer CODIGO_ADMON_DEPENDENCIA = 2; // Se debe cambiar por 2 al momento de desplegar.
    private static final Integer CODIGO_ROL_INVESTIGADOR_PRINCIPAL = 4;
    //private static final String CODIGO_COORDINADOR_COMPRAS = "31575127";
    
    public static void main(String[] args){
        
        new GIDaoException("Iniciando tarea NotificarVencimientoProyectos");
        
        Date dtFechaActual = null;
        List<PersonaSIGEP> administradores_dependencia = null;
        List<ProyectoSIGEP> proyectos = null;
        String strIdProyecto = null, strMensaje = null, strAccion=null;
        Integer intTotalProyectosNotificados=0, intTotalProyectoExentos=0;
        Date dtFechaFinalizacion = null;
        Long lgNumDias = null;
        Boolean notificarProyecto = Boolean.FALSE;
        PersonaSIGEP investigadorPpal = null;
        PersonaSIGEP admonProyecto = null;
        PersonaSIGEP admonDependencia = null;
        //Persona coordCompras = null;
        final String NUMERO_DIAS_SEMESTRE ="180"; // 258 d??as para proyectos de extensi??n 2015. Fecha: 17-04-2015.
        final String NUMERO_DIAS_TRIMESTRE ="90"; // 89 d??as para el proyecto: 4-401490. Fecha: 15-04-2015.
        //final String NUMERO_DIAS_BIMESTRE ="60";
        final String NUMERO_DIAS_MES ="30"; // 29 d??as para el proyecto: 171443 y 382468. Fecha: 15-04-2015.
        final String NUMERO_DIAS_FINALIZADO = "0"; // 5 d??as para el proyecto: . Fecha: 15-04-2015.
        
        EtapaDAO etapaDAO = new EtapaDAOImpl();
        Etapa etapa = null;
        
        ParametroGeneralDAO parametroGeneralDAO = new ParametroGeneralDAOImpl();
               
        ComunesDAO comunesDAO = new ComunesDAOImpl();
        
        EjecucionPptalProyectoDAO ejecucionPptalProyectoDAO = new EjecucionPptalProyectoDAOImpl();
        List<EjecucionPptalProyecto> ejecucionesPptalesProyecto = null;
        
        NotificacionMailSigepDAO notificacionMailDAO = new NotificacionMailSigepDAOImpl();
        
        /*
            Obtener todos los proyectos que se encuentren en estado 1=Activo.
        */

        ProyectoSIGEPDAO proyectoDAO = new ProyectoSIGEPDAOImpl();
        proyectos = new ArrayList<ProyectoSIGEP>();

        try{
            proyectos = proyectoDAO.obtenerPorEstadoYTipoProyectoDiferente(CODIGO_ESTADO_ACTIVO,TIPO_PROYECTO_DIFERENTE);
        }catch(GIDaoException e){
            new GIDaoException("Se gener?? un error obteniendo los proyectos en estado Activo", e);
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
        administradores_dependencia = new ArrayList<PersonaSIGEP>();
        
        try{
            administradores_dependencia = personaDAO.obtenerPorNivel(CODIGO_ADMON_DEPENDENCIA);
        }catch(GIDaoException e){
            new GIDaoException("Se gener?? un error obteniendo el Administrador de la Dependencia", e);
            return;
        }               
        
        if (administradores_dependencia != null){
            admonDependencia = administradores_dependencia.get(0);
        }
        
        /*
            Obtener el Coordinador de Compras.
        */
        
       /* try{
            coordCompras = personaDAO.obtenerUna(CODIGO_COORDINADOR_COMPRAS);
        }catch(GIDaoException e){
            new GIDaoException("Se gener?? un error obteniendo el Coordinador de Compras", e);
            return;
        } */
                                        
        /*
            Procesamiento de cada proyecto activo recuperado.
        */
        
        if ((proyectos != null) && (proyectos.size() > 0)){
            
            for (ProyectoSIGEP proyecto : proyectos){
                
                strIdProyecto = proyecto.getCodigo().trim();
                System.out.println("C??digo proyecto: " + strIdProyecto);
                
                /*
                    Verificar si el proyecto se notifica o no.
                */
                
                try{                    
                    notificarProyecto = parametroGeneralDAO.verificarNotificacionProyecto(strIdProyecto);
                }catch(GIDaoException e){
                    new GIDaoException("Se gener?? un error al verificar la notificaci??n del proyecto con c??digo " + strIdProyecto, e);
                }
                
                if (notificarProyecto == Boolean.TRUE){                                        
                    
                    /*
                        Obtener la fecha de finalizaci??n del proyecto actual.
                    */

                    try{
                        etapa = etapaDAO.obtenerPorProyecto(strIdProyecto);
                     }catch(GIDaoException e){
                        new GIDaoException("Se gener?? un error obteniendo la etapa para el proyecto con c??digo " + strIdProyecto, e);                    
                    }

                    if (etapa != null){
                        dtFechaFinalizacion = etapa.getFechaFin();                                                            
                        lgNumDias = funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaActual, dtFechaFinalizacion);
                        
                        System.out.println("Nro. de d??as: " + lgNumDias);
                                                
                       if (lgNumDias >= 0){      // Si el proyecto ya finaliz??, se debe poner comentario en esta instrucci??n ya que los d??as dar??n negativos.                                                    

                           if ((lgNumDias.toString().equals(NUMERO_DIAS_SEMESTRE)) || (lgNumDias.toString().equals(NUMERO_DIAS_TRIMESTRE)) || (lgNumDias.toString().equals(NUMERO_DIAS_MES)) || (lgNumDias.toString().equals(NUMERO_DIAS_FINALIZADO))){

                                /*
                                    Obtener el Investigador Principal del proyecto.
                                */

                                try{
                                    investigadorPpal = comunesDAO.obtenerParticipanteProyecto(strIdProyecto, CODIGO_ROL_INVESTIGADOR_PRINCIPAL);
                                }catch(GIDaoException e){
                                    new GIDaoException("Se gener?? un error obteniendo el Investigador Principal para el proyecto con c??digo " + strIdProyecto, e);                    
                                }

                                /*
                                    Obtener el Administrador del proyecto.
                                */

                                try{
                                    admonProyecto = comunesDAO.obtenerGestorGrupo(strIdProyecto);
                                }catch(GIDaoException e){
                                    new GIDaoException("Se gener?? un error obteniendo el Gestor del proyecto con c??digo " + strIdProyecto, e);                    
                                }                

                                /*
                                    Calcular ejecuci??n presupuestal del proyecto.
                                */                  

                                try{
                                    ejecucionesPptalesProyecto = ejecucionPptalProyectoDAO.calcularEjecucionPresupuestal(strIdProyecto);
                                }catch(GIDaoException e){
                                    new GIDaoException("Se gener?? un error obteniendo la ejecuci??n presupuestal del proyecto con c??digo " + strIdProyecto, e);                    
                                }

                                /*
                                    Enviar la notificaci??n de vencimiento a los interesados.
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
                                        Traza de env??o de la notificaci??n.
                                    */
                                    
                                    if ((lgNumDias.toString().equals(NUMERO_DIAS_SEMESTRE)) || (lgNumDias.toString().equals(NUMERO_DIAS_TRIMESTRE)) || (lgNumDias.toString().equals(NUMERO_DIAS_MES)) || (lgNumDias.toString().equals(NUMERO_DIAS_FINALIZADO))){
                                        
                                        strMensaje = "Se envi?? notificaci??n del proyecto con codigo " + strIdProyecto + " a: ";
                                        
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
                                    new GIDaoException("Se gener?? un error enviando la notificaci??n para el proyecto con c??digo " + strIdProyecto, e);                    
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
