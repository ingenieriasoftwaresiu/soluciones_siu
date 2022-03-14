/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.ComunesDAO;
import co.edu.udea.solucionessiu.dao.FuncionesComunesDAO;
import co.edu.udea.solucionessiu.dao.NotificacionDAO;
import co.edu.udea.solucionessiu.dao.NotificacionMailSigepDAO;
import co.edu.udea.solucionessiu.dao.ParametroGeneralDAO;
import co.edu.udea.solucionessiu.dao.PersonaDAO;
import co.edu.udea.solucionessiu.dto.Actividad;
import co.edu.udea.solucionessiu.dto.EjecucionPptalProyecto;
import co.edu.udea.solucionessiu.dto.Movimiento;
import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.dto.ParametroGeneral;
import co.edu.udea.solucionessiu.dto.ParametroMail;
import co.edu.udea.solucionessiu.dto.Persona;
import co.edu.udea.solucionessiu.dto.Proyecto;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jorge.correaj
 */
public class NotificacionMailSigepDAOImpl extends EnvioMailDAOimpl implements NotificacionMailSigepDAO {
    
    private ParametroMail parametroMail;
    private String strDestinatario;
    private String strAsunto;
    private String strMensaje;
    private ParametroGeneralDAO parametroGeneralDAO;
    private ParametroGeneral parametroGeneral;
    private String strModoPdn;
    
    public NotificacionMailSigepDAOImpl(){
        this.parametroGeneralDAO = new ParametroGeneralDAOImpl();
        try{
            this.parametroGeneral = this.parametroGeneralDAO.obtenerParametrosGeneralesSigep();
            this.strModoPdn = this.parametroGeneral.getModoPDN();
        }catch(GIDaoException e){
            new GIDaoException("No se pudieron recuperar los parámetros generales de la solución!.");
            this.parametroGeneral = null;
            this.strModoPdn = "N";
        }
    }

    @Override
    public void notificarVencimientoProyecto(String strAccion, Proyecto proyecto, List<EjecucionPptalProyecto> ejecucionesPptales, Persona admonDependencia, Persona investigadorPpal, Persona admonProyecto) throws GIDaoException {
        String strCodigoProyecto=null, strNombreProyecto=null, strEmail=null, strFirma="", strTiempoFaltante="", strComplementoAdmonDependencia="", strComplementoInvPpal="";        
        String strEjecucionPptal="", strFirmaGeneral="", strFechaEmision=null, strNombreInvPpal="", strNombreAdmonProyecto="", strAdvertenciaMailInvPpal="", strMsgFinal="";
        String[] strTemp = null;
        Double dblTotalPpto=Double.parseDouble("0"), dblTotalReservas=Double.parseDouble("0"), dblTotalEjecucion=Double.parseDouble("0"), dblTotalDisponible=Double.parseDouble("0"), dblTotaPorcentaje=Double.parseDouble("0");
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();        
        strFechaEmision = funcionesComunesDAO.getFechaActual();                                
        
        if (this.parametroGeneral != null){
            if (ejecucionesPptales != null){
                strComplementoInvPpal = this.parametroGeneral.getCuerpoMensaje();
                strComplementoInvPpal += ".<br /><br />";
            }
            strFirmaGeneral = this.parametroGeneral.getFirmaMensaje();
        }
        
        if (investigadorPpal != null){
            strNombreInvPpal = investigadorPpal.getNombre();
        }
        
        if (admonProyecto != null){
            strNombreAdmonProyecto = admonProyecto.getNombre();
        }
                               
        this.strDestinatario = null;
        this.strAsunto = null;
        this.strMensaje = "";
        
        strCodigoProyecto = proyecto.getCodigo().substring(2).trim();
        strNombreProyecto = proyecto.getNombre();               
        
        strFirma += strFirmaGeneral + "<br />";
        strFirma += "Administración SIU<br /><br />";
        strFirma += "<sub><u>Fecha de emisión</u>: [aaaa-mm-dd] " + strFechaEmision + ".</sub><br /><br />";
        
        strMsgFinal = "<b>Nota:</b> Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";
        
        if (investigadorPpal != null){
            strComplementoAdmonDependencia = "Nombre del Investigador Principal: <b>" + strNombreInvPpal + "</b>.<br /><br />";            
        }
        
        if (strAccion.equals("SEMESTRE")){
            strTiempoFaltante = "finalizará dentro de 6 meses.";            
        }
        
        if (strAccion.equals("TRIMESTRE")){
            strTiempoFaltante = "finalizará dentro de 3 meses.";            
        }
        
        /*if (strAccion.equals("BIMESTRE")){
            strTiempoFaltante = "finalizará dentro de 2 meses.";
            strComplementoAdmonDependencia = "Nombre del Investigador Principal: <b>" + strNombreInvPpal + "</b>.<br /><br />";            
        }*/
        
         if (strAccion.equals("MES")){
            strTiempoFaltante = "finalizará dentro de 1 mes.";            
        }
         
         if (strAccion.equals("FINALIZADO")){
            strTiempoFaltante = "ha finalizado.";            
        }
         
         if (investigadorPpal != null){
             if ((investigadorPpal.getEmail() == null) || (investigadorPpal.getEmail().equals(""))){
                 strAdvertenciaMailInvPpal = "<b>Nota:</b>El Profesor no tiene configurado un correo electrónico para el envío de la notificación.<br /><br />";
            }
         }
                     
        this.strAsunto = "ALERTA: El proyecto con código " + strCodigoProyecto + " " + strTiempoFaltante;           
                
        if ((ejecucionesPptales != null) && (ejecucionesPptales.size() > 0)){
            strEjecucionPptal += "<table cellspacing='0' cellpadding='2' border='1' width='100%'>";
            strEjecucionPptal += "<td style='text-align: center;background: #F2F2F2;'><b>Rubro</b></td>";
            strEjecucionPptal += "<td style='text-align: center;background: #F2F2F2;'><b>Presupuesto</b></td>";
            strEjecucionPptal += "<td style='text-align: center;background: #F2F2F2;'><b>Ejecución legalizada</b></td>";    
            strEjecucionPptal += "<td style='text-align: center;background: #F2F2F2;'><b>Reservas</b></td>";                    
            strEjecucionPptal += "<td style='text-align: center;background: #F2F2F2;'><b>Valor disponible</b></td>";
            strEjecucionPptal += "<td style='text-align: center;background: #F2F2F2;'><b>% ejecución</b></td>";
            strEjecucionPptal += "</tr>";
        
            for(EjecucionPptalProyecto ejecucionPptalProyecto : ejecucionesPptales){                           
                strEjecucionPptal += "<tr>";
                strEjecucionPptal += "<td style='text-align: justify;'>" + ejecucionPptalProyecto.getNombreRubro() + "</td>";
                strEjecucionPptal += "<td style='text-align: center;'>$"+ funcionesComunesDAO.marcarMiles(String.valueOf(ejecucionPptalProyecto.getPresupuesto().longValue())) + "</td>";                
                strEjecucionPptal += "<td style='text-align: center;'>$" + funcionesComunesDAO.marcarMiles(String.valueOf(ejecucionPptalProyecto.getValorEjecucion().longValue())) + "</td>";
                strEjecucionPptal += "<td style='text-align: center;'>$"+ funcionesComunesDAO.marcarMiles(String.valueOf(ejecucionPptalProyecto.getReservas().longValue())) + "</td>";
                strEjecucionPptal += "<td style='text-align: center;'>$" + funcionesComunesDAO.marcarMiles(String.valueOf(ejecucionPptalProyecto.getDisponibilidad().longValue()))+ "</td>";
                strEjecucionPptal += "<td style='text-align: center;'>" + ejecucionPptalProyecto.getPorcentajeEjecucion().toString() + "%</td>";                
                strEjecucionPptal += "</tr>";                
                dblTotalPpto = dblTotalPpto + ejecucionPptalProyecto.getPresupuesto();
                dblTotalReservas = dblTotalReservas + ejecucionPptalProyecto.getReservas();
                dblTotalEjecucion = dblTotalEjecucion + ejecucionPptalProyecto.getValorEjecucion();
                dblTotalDisponible = dblTotalDisponible + ejecucionPptalProyecto.getDisponibilidad();
            }
            
            dblTotaPorcentaje = (((dblTotalReservas + dblTotalEjecucion)/dblTotalPpto)*100);
            
            strEjecucionPptal += "<tr>";
            strEjecucionPptal += "<td style='text-align: center;font-weight: bold;background: #F2F2F2;'>TOTALES</td>";
            strEjecucionPptal += "<td style='text-align: center;font-weight: bold;background: #F2F2F2;'>$"+ funcionesComunesDAO.marcarMiles(String.valueOf(dblTotalPpto.longValue())) + "</td>";            
            strEjecucionPptal += "<td style='text-align: center;font-weight: bold;background: #F2F2F2;'>$" + funcionesComunesDAO.marcarMiles(String.valueOf(dblTotalEjecucion.longValue())) + "</td>";         
            strEjecucionPptal += "<td style='text-align: center;font-weight: bold;background: #F2F2F2;'>$" + funcionesComunesDAO.marcarMiles(String.valueOf(dblTotalReservas.longValue())) + "</td>";
            strEjecucionPptal += "<td style='text-align: center;font-weight: bold;background: #F2F2F2;'>$" + funcionesComunesDAO.marcarMiles(String.valueOf(dblTotalDisponible.longValue()))+ "</td>";
            strEjecucionPptal += "<td style='text-align: center;font-weight: bold;background: #F2F2F2;'>" + funcionesComunesDAO.redondear(dblTotaPorcentaje, 1) + "%</td>";
            strEjecucionPptal += "</tr>";
        
            strEjecucionPptal += "</table>";
            strEjecucionPptal += "<br />";
        }
                
        if ((strAccion.equals("SEMESTRE")) || (strAccion.equals("TRIMESTRE")) || (strAccion.equals("MES")) || (strAccion.equals("FINALIZADO"))){
            /*
                Notificación para el Administrador de la Dependencia.
            */

            if (admonDependencia != null){
                
                if (this.strModoPdn.equals("S")){
                    strEmail = admonDependencia.getEmail().trim();            
                }else{
                     strEmail = "ingenieriasoftwaresiu@udea.edu.co";   
                }
               
                if ((strEmail != null) && (!strEmail.equals(""))){
                    this.strDestinatario = strEmail;                

                    this.strMensaje += "Cordial saludo Sr(a). " + admonDependencia.getNombre() + ".<br /><br />";
                    this.strMensaje += "El proyecto <b>" + strCodigoProyecto + "-" + strNombreProyecto + "</b> " + strTiempoFaltante + "<br /><br />";
                    this.strMensaje +=  strComplementoAdmonDependencia;
                    if (!(strAccion.equals("FINALIZADO"))){
                        this.strMensaje += strComplementoInvPpal;
                    }               
                    this.strMensaje += strEjecucionPptal;                           
                    if (!(strAdvertenciaMailInvPpal.equals(""))){                    
                        this.strMensaje += strAdvertenciaMailInvPpal;     
                    }
                    this.strMensaje += "Atentamente,<br /><br />";
                    this.strMensaje += strFirma;           
                    this.strMensaje += strMsgFinal;
                                       
                    this.parametroMail = new ParametroMail();
                    this.parametroMail.setDestinatario(this.strDestinatario);
                    this.parametroMail.setAsunto(this.strAsunto);
                    this.parametroMail.setMensaje(this.strMensaje);

                    sendMailHTML(this.parametroMail);
                }
            }

            this.strDestinatario = null;
            this.strMensaje = "";
            this.parametroMail = null;        
            strEmail = null;

            /*
                Notificación para el Investigador Principal.
            */

            if (investigadorPpal != null){
                
                if (this.strModoPdn.equals("S")){
                    strEmail = investigadorPpal.getEmail().trim();                     
                }else{
                     strEmail = "ingenieriasoftwaresiu@udea.edu.co";  
                }                                          

                if ((strEmail != null) && (!strEmail.equals(""))){
                    this.strDestinatario = strEmail;                        

                    this.strMensaje += "Cordial saludo Profesor(a). " + strNombreInvPpal + ".<br /><br />";
                    this.strMensaje += "El proyecto <b>" + strCodigoProyecto + "-" + strNombreProyecto + "</b> " + strTiempoFaltante + "<br /><br />";
                    if (!(strAccion.equals("FINALIZADO"))){
                        this.strMensaje += strComplementoInvPpal;
                    }                
                    this.strMensaje += strEjecucionPptal;
                    this.strMensaje += "Atentamente,<br /><br />";
                    this.strMensaje += strFirma;                    
                    this.strMensaje += strMsgFinal;

                    this.parametroMail = new ParametroMail();
                    this.parametroMail.setDestinatario(this.strDestinatario);
                    this.parametroMail.setAsunto(this.strAsunto);
                    this.parametroMail.setMensaje(this.strMensaje);

                    sendMailHTML(this.parametroMail);
                }
            }

            this.strDestinatario = null;
            this.strMensaje = "";
            this.parametroMail = null;        
            strEmail = null;

            /*
                Notificación para el Administrador del Proyecto.
            */

            if (admonProyecto != null){          
                
                if (this.strModoPdn.equals("S")){
                    strEmail = admonProyecto.getEmail().trim();
                }else{
                     strEmail = "ingenieriasoftwaresiu@udea.edu.co";  
                }  
                
                if ((strEmail != null) && (!strEmail.equals(""))){
                    this.strDestinatario = strEmail;                        

                    this.strMensaje += "Cordial saludo Sr(a). " + strNombreAdmonProyecto + ".<br /><br />";
                    this.strMensaje += "El proyecto <b>" + strCodigoProyecto + "-" + strNombreProyecto + "</b> " + strTiempoFaltante + "<br /><br />";
                    this.strMensaje +=  strComplementoAdmonDependencia;
                    if (!(strAccion.equals("FINALIZADO"))){
                        this.strMensaje += strComplementoInvPpal;
                    }               
                    this.strMensaje += strEjecucionPptal;                           
                    if (!(strAdvertenciaMailInvPpal.equals(""))){                    
                        this.strMensaje += strAdvertenciaMailInvPpal;     
                    }                
                    this.strMensaje += "Atentamente,<br /><br />";
                    this.strMensaje += strFirma;                 
                    this.strMensaje += strMsgFinal;

                    this.parametroMail = new ParametroMail();
                    this.parametroMail.setDestinatario(this.strDestinatario);
                    this.parametroMail.setAsunto(this.strAsunto);
                    this.parametroMail.setMensaje(this.strMensaje);

                    sendMailHTML(this.parametroMail);
                }            
            }

            this.strDestinatario = null;
            this.strMensaje = "";
            this.parametroMail = null;        
            strEmail = null;
        }
        
         /*
            Notificación para el Coordinador de Compras.
        */
        
        /*
        if (strAccion.equals("BIMESTRE")){                           
            if (coordCompras != null){
                //strEmail = coordCompras.getEmail().trim();
                strEmail = "jorge.correa01@gmail.com";
                
                if ((strEmail != null) && (!strEmail.equals(""))){
                    this.strDestinatario = strEmail;                

                    this.strMensaje += "Cordial saludo Sr(a). " + coordCompras.getNombre() + ".<br /><br />";
                    this.strMensaje += "El proyecto <b>" + strCodigoProyecto + "-" + strNombreProyecto + "</b> " + strTiempoFaltante + "<br /><br />";
                    this.strMensaje +=  strComplementoAdmonDependencia;                                
                    this.strMensaje += "Atentamente,<br /><br />";
                    this.strMensaje += strFirma;           
                    this.strMensaje += strMsgFinal;

                    this.parametroMail = new ParametroMail();
                    this.parametroMail.setDestinatario(this.strDestinatario);
                    this.parametroMail.setAsunto(this.strAsunto);
                    this.parametroMail.setMensaje(this.strMensaje);

                    sendMailHTML(this.parametroMail);
                }
            }
        }*/
    }

    @Override
    public void notificarRegalias(List<String> destinatarios) throws GIDaoException {
        
        String strFechaEmision="", strFirmaGeneral="", strFirma="", strMsgFinal="", strEmail=null, strNombre="", strMensajeRegalias=null, strDiaLimite="", strFechaLimite="", strAnio="", strMes="";
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        ParametroGeneralDAO parametroGeneralDAO = new ParametroGeneralDAOImpl();
        ParametroGeneral parametroGeneral = null;
        
        parametroGeneral = parametroGeneralDAO.obtenerParametrosGeneralesSigep();
        
        if (parametroGeneral != null){
            strFirmaGeneral = parametroGeneral.getFirmaMensaje().trim();
            strMensajeRegalias =  parametroGeneral.getMensajeRegalias().trim();
            strDiaLimite = parametroGeneral.getDiaLimiteEnvioColilla().trim();
        }
        
        this.strDestinatario = null;
        this.strAsunto = null;
        this.strMensaje = "";
        
        strFechaEmision = funcionesComunesDAO.getFechaActual();
        strMsgFinal = "<b>Nota:</b> Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";
        
        strAnio = strFechaEmision.substring(0, 4);
        strMes = strFechaEmision.substring(5, 7);
        strFechaLimite = strAnio + "-" + strMes + "-" + strDiaLimite;
                
        strFirma += strFirmaGeneral + "<br />";
        strFirma += "Administración SIU<br /><br />";
        strFirma += "<sub><u>Fecha de emisión</u>: [aaaa-mm-dd] " + strFechaEmision + ".</sub><br /><br />";                    
        
        if (destinatarios.size() > 0){
        
            this.strDestinatario = strEmail;
            this.strAsunto = "ALERTA: Envío de colilla de pago";
            
            this.strMensaje += "Cordial saludo.<br /><br />";
            this.strMensaje += strMensajeRegalias + ".<br /><br />";
            this.strMensaje += "La fecha límite [aaaa-mm-dd] para el envío de la misma es " + strFechaLimite + ".<br /><br />";
            this.strMensaje += "Atentamente,<br /><br />";
            this.strMensaje += strFirma;           
            this.strMensaje += strMsgFinal;

            this.parametroMail = new ParametroMail();
            this.parametroMail.setDestinatario(this.strDestinatario);
            this.parametroMail.setAsunto(this.strAsunto);
            this.parametroMail.setMensaje(this.strMensaje);

            sendBCCMultiple(this.parametroMail, destinatarios);
            
            for(int i=0;i<destinatarios.size();i++){                
                new GIDaoException("Se envió notificación al correo electrónico " + destinatarios.get(i));
            }            
        }
    }

    @Override
    public void notificarActividades(Actividad actividad, String strAccionNotificar) throws GIDaoException {
        
        String strCodigoProyecto, strEmailGestor, strNomGestor, strEmailInv, strNomInv, strFechaEmision, strFirma, strFirmaGeneral, strMsgFinal, strDescripcion, strProducto;
        String strNomAdmon, strEmailAdmon;
        List<Persona> administradores_dependencia = null;
        Persona admonDependencia = null;
        Date dtFechaFinal = null;
        final Integer CODIGO_INVESTIGADOR_PPAL = 4;
        final Integer CODIGO_ADMON_DEPENDENCIA = 2;
        
        this.strDestinatario = null;
        this.strAsunto = null;
        this.strMensaje = "";
        strCodigoProyecto = null;
        strNomGestor = null;
        strEmailGestor = null;
        strFechaEmision = null;
        strFirma = "";
        strFirmaGeneral = null;
        strMsgFinal = null;
        strDescripcion = null;
        strProducto = null;
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();        
        strFechaEmision = funcionesComunesDAO.getFechaActual();
        
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
        
        strFirmaGeneral = "<b>Coordinación Proceso Administración de Proyectos</b>";
        strFirma += strFirmaGeneral + "<br />";
        strFirma += "Administración SIU<br /><br />";
        strFirma += "<sub><u>Fecha de emisión</u>: [aaaa-mm-dd] " + strFechaEmision + ".</sub><br /><br />";        
        strMsgFinal = "<b>Nota:</b> Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";
        
        strCodigoProyecto = actividad.getProyecto();
        
        ComunesDAO comunesDAO = new ComunesDAOImpl();
        Persona gestorProyecto = null;        
        gestorProyecto = comunesDAO.obtenerGestorGrupo(strCodigoProyecto);
        
        Persona investigador = null;        
        investigador = comunesDAO.obtenerParticipanteProyecto(strCodigoProyecto, CODIGO_INVESTIGADOR_PPAL);
             
        strCodigoProyecto = actividad.getProyecto().replace("4-", "");
        strDescripcion = actividad.getDescripcion();
        strProducto = actividad.getProducto();
        dtFechaFinal = actividad.getFechaFin();

        if (strAccionNotificar.equals("ALDIA")){
             this.strAsunto = "ALERTA:  Actividad del proyecto con código " + strCodigoProyecto + " cumple su fecha de finalización";
         }

        if (strAccionNotificar.equals("ANTES")){
             this.strAsunto = "ALERTA:  Actividad del proyecto con código " + strCodigoProyecto + " cumplirá próximamente su fecha de finalización";
         }                     
            
        if (gestorProyecto != null){           
            
             strNomGestor = gestorProyecto.getNombre();
             
             if (this.strModoPdn.equals("S")){
                strEmailGestor = gestorProyecto.getEmail();         
            }else{
                 strEmailGestor = "ingenieriasoftwaresiu@udea.edu.co";
            }
            
            if (strEmailGestor != null && !strEmailGestor.equals("")){
                 this.strDestinatario = strEmailGestor;

                this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomGestor + "</b>.<br /><br />";
                this.strMensaje += "La actividad con descripción '<i>" + strDescripcion + "</i>' y producto '<i>" +  strProducto + "</i>' asociada al proyecto con código " + strCodigoProyecto + " finaliza el <b>" + dtFechaFinal.toString() + "</b>.<br /><br />";
                this.strMensaje += "Por favor realice la respectiva gestión con el Investigador, con el fin de dar cumplimiento oportuno a la misma.<br /><br />";
                this.strMensaje += "Atentamente,<br /><br />";
                this.strMensaje += strFirma;           
                this.strMensaje += strMsgFinal;

                this.parametroMail = new ParametroMail();
                this.parametroMail.setDestinatario(this.strDestinatario);
                this.parametroMail.setAsunto(this.strAsunto);
                this.parametroMail.setMensaje(this.strMensaje);

                sendMailHTML(this.parametroMail);
                new GIDaoException("Notificación enviada a " + strNomGestor + " al correo " + strEmailGestor);
            }else{
                new GIDaoException("El email del Gestor de Proyecto es nulo!.");
            }                  
        }else{
            new GIDaoException("No se pudo recuperar el objeto Persona Gestor Grupo!.");
        }                           
        
        this.strDestinatario  = null;
         this.strMensaje = "";
         this.parametroMail  = null;

        if (investigador != null){                

            strNomInv = investigador.getNombre();
            
            if (this.strModoPdn.equals("S")){
                strEmailInv = investigador.getEmail();
            }else{
                 strEmailInv= "ingenieriasoftwaresiu@udea.edu.co";    
            }
            
            if (strEmailInv != null && !strEmailInv.equals("")){
                 this.strDestinatario = strEmailInv;

                this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomInv + "</b>.<br /><br />";
                this.strMensaje += "La actividad con descripción '<i>" + strDescripcion + "</i>' y producto '<i>" +  strProducto + "</i>' asociada al proyecto con código " + strCodigoProyecto + " finaliza el <b>" + dtFechaFinal.toString() + "</b>.<br /><br />";
                this.strMensaje += "Por favor realice la respectiva gestión, con el fin de dar cumplimiento oportuno a la misma.<br /><br />";
                this.strMensaje += "Atentamente,<br /><br />";
                this.strMensaje += strFirma;           
                this.strMensaje += strMsgFinal;

                this.parametroMail = new ParametroMail();
                this.parametroMail.setDestinatario(this.strDestinatario);
                this.parametroMail.setAsunto(this.strAsunto);
                this.parametroMail.setMensaje(this.strMensaje);

                sendMailHTML(this.parametroMail);
                new GIDaoException("Notificación enviada a " + strNomInv + " al correo " + strEmailInv);
            }else{
                new GIDaoException("El email del Investigador Principal es nulo!.");
            }                  
        }else{
            new GIDaoException("No se pudo recuperar el objeto Persona Investigador Principal !.");
        }          
        
         this.strDestinatario  = null;
         this.strMensaje = "";
         this.parametroMail  = null;
        
        if (admonDependencia != null){                

            strNomAdmon = admonDependencia.getNombre();
            
            if (this.strModoPdn.equals("S")){
                strEmailAdmon = admonDependencia.getEmail();
            }else{
                 strEmailAdmon= "ingenieriasoftwaresiu@udea.edu.co";    
            }

            if (strEmailAdmon != null && !strEmailAdmon.equals("")){
                 this.strDestinatario = strEmailAdmon;

                this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomAdmon + "</b>.<br /><br />";
                this.strMensaje += "La actividad con descripción '<i>" + strDescripcion + "</i>' y producto '<i>" +  strProducto + "</i>' asociada al proyecto con código " + strCodigoProyecto + " finaliza el <b>" + dtFechaFinal.toString() + "</b>.<br /><br />";
                this.strMensaje += "Atentamente,<br /><br />";
                this.strMensaje += strFirma;           
                this.strMensaje += strMsgFinal;

                this.parametroMail = new ParametroMail();
                this.parametroMail.setDestinatario(this.strDestinatario);
                this.parametroMail.setAsunto(this.strAsunto);
                this.parametroMail.setMensaje(this.strMensaje);

                sendMailHTML(this.parametroMail);
                new GIDaoException("Notificación enviada a " + strNomAdmon + " al correo " + strEmailAdmon);
            }else{
                new GIDaoException("El email del Administrador de la Dependencia es nulo!.");
            }                  
        }else{
            new GIDaoException("No se pudo recuperar el objeto Persona Investigador Principal !.");
        }
    }

    @Override
    public void notificarReservas(List<Movimiento> movimientos, String strCodNotificacion) throws GIDaoException {
        
        String strEmailColaborador = null, strNomColaborador="", strFechaEmision, strFirma="", strFirmaGeneral, strMsgFinal, strCodProyecto, strIdTercero, strTercero, strTipoMov, strFecha;
        String strNumSoporte, strTipoSoporte;
        Integer intTotalMovs;
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();        
        strFechaEmision = funcionesComunesDAO.getFechaActual();
        
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        Notificacion notificacion =null;
        
        PersonaDAO personaDAO = new PersonaDAOImpl();
        Persona persona = null;
        
        try{
            notificacion = notificacionDAO.obtenerUna(strCodNotificacion);
        }catch(GIDaoException gi){
            new GIDaoException("Se generó un error parseando la fecha actual", gi);
        }
        
        strFirmaGeneral = "<b>Coordinación Proceso Administración de Proyectos</b>";
        strFirma += strFirmaGeneral + "<br />";
        strFirma += "Administración SIU<br /><br />";
        strFirma += "<sub><u>Fecha de emisión</u>: [aaaa-mm-dd] " + strFechaEmision + ".</sub><br /><br />";        
        strMsgFinal = "<b>Nota:</b> Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";
        
        if (notificacion != null){
            
            this.strDestinatario  = null;
            this.strMensaje = "";
            this.parametroMail  = null;
            
            strNomColaborador = notificacion.getNombreDestinatario();            
            this.strAsunto = "ALERTA:  Creación de movimientos desde el área de Administración de Proyectos de Investigación y Extensión";
                        
            if (this.strModoPdn.equals("S")){
                    strEmailColaborador = notificacion.getEmailDestinatario();
            }else{
                     strEmailColaborador = "ingenieriasoftwaresiu@udea.edu.co";
            }

            if (strEmailColaborador != null && !strEmailColaborador.equals("")){
                this.strDestinatario = strEmailColaborador;
                this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomColaborador + "</b>.<br /><br />";
                intTotalMovs = 0;
                
                for(Movimiento mov : movimientos){
                
                    strCodProyecto = mov.getCodProyecto();
                    strCodProyecto = strCodProyecto.substring(2, strCodProyecto.length());                    
                    strIdTercero = mov.getEntidadFinanciadora();
                    strTipoMov = mov.getTipoMov();
                    strFecha = mov.getFecha().toString();
                    strTipoSoporte = mov.getTipoSoporte();
                    strNumSoporte = mov.getNumeroSoporte();
                    intTotalMovs = movimientos.size();
                    
                    try{
                        persona = personaDAO.obtenerUna(strIdTercero);
                        strTercero = persona.getIdentificacion() + " - " + persona.getNombre();
                    }catch(GIDaoException gi){
                        new GIDaoException("Se generó un error al recuperar la información del tercero con NIT " + strIdTercero, gi);              
                        strTercero = "-";
                    }

                    this.strMensaje += "* Se creó el movimiento #" + mov.getCodigoMov()  + " con los siguientes datos:<br /><br />";
                    this.strMensaje += "- <b>Tipo de movimiento</b>: " + strTipoMov + "<br />";
                    this.strMensaje += "- <b>Proyecto</b>: " + strCodProyecto + "<br />";
                    this.strMensaje += "- <b>Tercero</b>: " + strTercero + "<br />";
                    this.strMensaje += "- <b>Centro de costos</b>: " + mov.getCentroCostos() + "<br />";
                    this.strMensaje += "- <b>Fecha</b>: " + strFecha + "<br />";
                    this.strMensaje += "- <b>Valor</b>: $" + funcionesComunesDAO.marcarMiles(mov.getValor().toString()) + "<br />";
                    this.strMensaje += "- <b>Tipo de soporte</b>: " + strTipoSoporte + "<br />";
                    this.strMensaje += "- <b>Número de soporte</b>: " + strNumSoporte + "<br />";
                    this.strMensaje += "- <b>Observación</b>: " + mov.getObservacion() + "<br /><br />";
                                        
                    strCodProyecto = "";
                    strIdTercero = "";
                    strTercero = "";
                    strTipoMov = "";
                    strFecha = "";
                    persona = null;
                }
                
                this.strMensaje += "Atentamente,<br /><br />";
                this.strMensaje += strFirma;           
                this.strMensaje += strMsgFinal;

                this.parametroMail = new ParametroMail();
                this.parametroMail.setDestinatario(this.strDestinatario);
                this.parametroMail.setAsunto(this.strAsunto);
                this.parametroMail.setMensaje(this.strMensaje);

                sendMailHTML(this.parametroMail);
                new GIDaoException("Notificación enviada a " + strNomColaborador + " al correo " + strEmailColaborador + " con " + intTotalMovs.toString() + " movimientos!");
            }else{
                new GIDaoException("El correo del colaborador ASIU es nulo!.");
            }        
        }else{
            new GIDaoException("El objeto de notificación es nulo!");
        }        
    }
    
}
