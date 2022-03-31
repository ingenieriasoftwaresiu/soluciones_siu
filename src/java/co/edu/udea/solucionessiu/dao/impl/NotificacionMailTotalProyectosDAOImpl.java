/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.NotificacionDAO;
import co.edu.udea.solucionessiu.dao.NotificacionMailTotalProyectosDAO;
import co.edu.udea.solucionessiu.dao.ParametroGeneralDAO;
import co.edu.udea.solucionessiu.dao.ProyectoTotalProyectosDAO;
import co.edu.udea.solucionessiu.dao.UsuarioTotalProyectosDAO;
import co.edu.udea.solucionessiu.dto.FechaProyectoTotalProyectos;
import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.dto.ParametroGeneral;
import co.edu.udea.solucionessiu.dto.ParametroMail;
import co.edu.udea.solucionessiu.dto.ProyectoTotalProyectos;
import co.edu.udea.solucionessiu.dto.UsuarioTotalProyectos;
import co.edu.udea.solucionessiu.exception.GIDaoException;

/**
 *
 * @author jorge.correaj
 */
public class NotificacionMailTotalProyectosDAOImpl extends EnvioMailDAOimpl implements NotificacionMailTotalProyectosDAO{
    
    private ParametroMail parametroMail;
    private String strDestinatario;
    private String strAsunto;
    private String strMensaje;
    ParametroGeneral parametroGeneral;
    private String strModoPdn;
    private String strEmailDllo;
    
    public NotificacionMailTotalProyectosDAOImpl(){
        
        ParametroGeneralDAO parametroGeneralDAO = new ParametroGeneralDAOImpl();
        this.parametroGeneral = null;
        
        try{
            this.parametroGeneral = parametroGeneralDAO.obtenerParametrosGeneralesSiuWeb();
            
            if (this.parametroGeneral != null){
               this.strModoPdn = this.parametroGeneral.getModoProduccion().trim();
               this.strEmailDllo = this.parametroGeneral.getEmailDllo().trim();
            }else{
                this.strModoPdn = "N";
                this.strEmailDllo = "ingenieriasoftwaresiu@udea.edu.co";
            }
            
        }catch(GIDaoException e){
            new GIDaoException("Se generó un error recuperando los parametros generales", e);
        }
    }
    
    @Override
    public void notificarFechasProyectos(FechaProyectoTotalProyectos fechaProyecto, String strIdCodigoNotificacion) throws GIDaoException {
        
        Integer intIdProyecto = 0, intIdGestor = 0;
        String strFecha, strTipoFecha, strSIUCode, strNombreGestor, strCorreoGestor, strNombreInforme, strNotificacionActiva = "false";
                
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        Notificacion notificacion = null;
        
        ProyectoTotalProyectosDAO proyectoTotalProyectosDAO = new ProyectoTotalProyectosDAOImpl();
        ProyectoTotalProyectos proyectoTotalProyectos = null;
        
        UsuarioTotalProyectosDAO usuarioTotalProyectosDAO = new UsuarioTotalProyectosDAOImpl();
        UsuarioTotalProyectos usuarioTotalProyectos = null;
        
        try{
            notificacion = notificacionDAO.obtenerUnoTotalProyectos(strIdCodigoNotificacion);
        }catch(GIDaoException gi){
            new GIDaoException("No se pudo recuperar la notificación con código " + strIdCodigoNotificacion,gi);
            notificacion = null;
        }
        
        if (notificacion != null){
            
            strNotificacionActiva = notificacion.getEstado();
            
            if (strNotificacionActiva.toUpperCase().equals("TRUE")){
                
                this.strAsunto = notificacion.getAsunto();
                this.strMensaje = notificacion.getMensaje();
                
                intIdProyecto = fechaProyecto.getIdProyecto();
                strFecha = fechaProyecto.getFecha();
                strTipoFecha = fechaProyecto.getTipoFecha();

                try{
                    proyectoTotalProyectos = proyectoTotalProyectosDAO.obtenerUno(intIdProyecto);
                }catch(GIDaoException gi){
                    new GIDaoException("No se pudo recuperar el proyecto con id " + intIdProyecto,gi);
                    proyectoTotalProyectos = null;
                }

                if (proyectoTotalProyectos != null){
                    
                    strSIUCode = proyectoTotalProyectos.getSiucode();
                    intIdGestor = proyectoTotalProyectos.getManagerid();
                                                            
                    try{
                        usuarioTotalProyectos = usuarioTotalProyectosDAO.obtenerUno(intIdGestor);
                    }catch(GIDaoException gi){
                        new GIDaoException("No se pudo recuperar la información del gestor con id " + intIdGestor,gi);
                        usuarioTotalProyectos = null;
                    }
                    
                    if (usuarioTotalProyectos != null){
                        
                        strNombreGestor = usuarioTotalProyectos.getFirstname() + " " + usuarioTotalProyectos.getLastname();
                        strCorreoGestor = usuarioTotalProyectos.getEmail();
                        
                        this.strAsunto = this.strAsunto.replace("{projcode}", strSIUCode);
                        this.strMensaje = this.strMensaje.replace("{name}", strNombreGestor);
                        this.strMensaje = this.strMensaje.replace("{projcode}", strSIUCode);     
                        this.strMensaje = this.strMensaje.replace("{date}", strFecha);   

                        if (strTipoFecha.equals("FECHAINFORME")){                                                     
                            strNombreInforme = fechaProyecto.getNombreInforme();
                            this.strMensaje = this.strMensaje.replace("{reportname}", strNombreInforme);   
                        }
                        
                        this.strMensaje = this.strMensaje + "<br />";
                        this.strMensaje = this.strMensaje + "Atentamente,";
                        this.strMensaje = this.strMensaje + "<br /><br />";
                        this.strMensaje = this.strMensaje + "<b>Solución para la Gestión de Información de Proyectos<br />(TOTAL PROYECTOS)</b>";
                        this.strMensaje = this.strMensaje + "<br />";
                        
                        if (this.strModoPdn.equals("N")){
                            this.strDestinatario = this.strEmailDllo;            
                        }else{
                            this.strDestinatario = strCorreoGestor;            
                        }  
                        
                        this.parametroMail = new ParametroMail();          
                        this.parametroMail.setDestinatario(this.strDestinatario);
                        this.parametroMail.setAsunto(this.strAsunto);
                        this.parametroMail.setMensaje(this.strMensaje);

                        sendMailHTML(this.parametroMail);
                        new GIDaoException("Notificación enviada correctamente a " + strNombreGestor + " al correo " + strCorreoGestor + " para alertar " + strTipoFecha + " en el proyecto con código " + strSIUCode);                            
                        
                        strNombreGestor = "";
                        strCorreoGestor = "";
                        strNombreInforme = "";
                    }
                    
                    strSIUCode = "";
                    intIdGestor = 0;
                }
                
                intIdProyecto = 0;
                strFecha = "";
                strTipoFecha = "";
            }      
            
            strNotificacionActiva = "";
        }
    }    
}
