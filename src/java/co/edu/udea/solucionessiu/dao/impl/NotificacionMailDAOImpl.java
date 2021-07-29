/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.FuncionesComunesDAO;
import co.edu.udea.solucionessiu.dao.NotificacionDAO;
import co.edu.udea.solucionessiu.dao.NotificacionMailDAO;
import co.edu.udea.solucionessiu.dao.ParametroGeneralDAO;
import co.edu.udea.solucionessiu.dao.ParametrosASIUDAO;
import co.edu.udea.solucionessiu.dto.Accion;
import co.edu.udea.solucionessiu.dto.AnticipoViaticoTiquete;
import co.edu.udea.solucionessiu.dto.Calibracion;
import co.edu.udea.solucionessiu.dto.CalibracionEquipo;
import co.edu.udea.solucionessiu.dto.Cartera;
import co.edu.udea.solucionessiu.dto.Contrato;
import co.edu.udea.solucionessiu.dto.ControlInsumo;
import co.edu.udea.solucionessiu.dto.Coordinacion;
import co.edu.udea.solucionessiu.dto.Correccion;
import co.edu.udea.solucionessiu.dto.Documento;
import co.edu.udea.solucionessiu.dto.Eficacia;
import co.edu.udea.solucionessiu.dto.EquipoMnto;
import co.edu.udea.solucionessiu.dto.MntoPrtvoEqCi;
import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.dto.ParametroGeneral;
import co.edu.udea.solucionessiu.dto.ParametroMail;
import co.edu.udea.solucionessiu.dto.Pedido;
import co.edu.udea.solucionessiu.dto.Persona;
import co.edu.udea.solucionessiu.dto.RegistroPlanCalibracion;
import co.edu.udea.solucionessiu.dto.RegistroPlanMejoramiento;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jorge.correa
 */
public class NotificacionMailDAOImpl extends EnvioMailDAOimpl implements NotificacionMailDAO{
    private ParametroMail parametroMail;
    private String strDestinatario;
    private String strAsunto;
    private String strMensaje;
    private String strRutaArchivo;
    ParametroGeneral parametroGeneral;
    private String strModoPdn;
    private String  strEmailDllo;   
    
    public NotificacionMailDAOImpl(){
        ParametroGeneralDAO parametroGeneralDAO = new ParametroGeneralDAOImpl();
        this.parametroGeneral = null;
        
        try{
            this.parametroGeneral = parametroGeneralDAO.obtenerParametrosGenerales();
            
            if (this.parametroGeneral != null){
               this.strModoPdn = this.parametroGeneral.getModoProduccion().trim();
               this.strEmailDllo = this.parametroGeneral.getEmailDllo().trim();
            }else{
                this.strModoPdn = "N";
                this.strEmailDllo = "ingenieria.software.siu@gmail.com";
            }
            
        }catch(GIDaoException e){
            new GIDaoException("Se generó un error recuperando los parametros generales", e);
        }
    }

    @Override
    public void notificarVencimientoPedidos(Pedido pedido) throws GIDaoException {
        
        String strNombreDestinatario=null, strEmailDestinatario=null, strGrupo=null, strNumPedido=null, strNombreProveedor=null, strFechaAcordada=null, strFechaEnvioProveedor=null;
        String strCodigoNotificacion=null;
        Long lgNumDiasDiferencia=null;    
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        Notificacion notificacion = null;
        
        strCodigoNotificacion = pedido.getCodigoNotificacion();
        
        try{
            notificacion =notificacionDAO.obtenerUno(strCodigoNotificacion);
        }catch(GIDaoException gde){
            new GIDaoException("Se generó un error recuperando la información de la notificación con código " + strCodigoNotificacion, gde);            
            notificacion = null;
        }        
        
        if (notificacion != null){            

            strNombreDestinatario = notificacion.getNombreDestinario().trim();         
            strEmailDestinatario = notificacion.getEmailDestinario().trim();
            lgNumDiasDiferencia = pedido.getDiasDiferencia();
            strGrupo = pedido.getNombreGrupo();
            strNumPedido = pedido.getNumeroPedido();
            strNombreProveedor = pedido.getNombreProveedor();
            strFechaEnvioProveedor = pedido.getFechaEnvioProveedor();
            strFechaAcordada = pedido.getFechaAcordada();
            this.strDestinatario = null;
            this.strAsunto = null;
            this.strMensaje = "";
            
            if (this.strModoPdn.equals("N")){
                this.strDestinatario = this.strEmailDllo;            
            }else{
                this.strDestinatario = strEmailDestinatario;            
            }                        
            
            if (lgNumDiasDiferencia.equals(Long.parseLong("0"))){
                this.strAsunto = "ALERTA: El pedido #" + strNumPedido.trim() + " asociado al grupo " + strGrupo.trim() + " ha cumplido la fecha acordada de entrega";                 
            }else{
                this.strAsunto = "ALERTA: El pedido #" + strNumPedido.trim() + " asociado al grupo " + strGrupo.trim() + " está próximo a cumplir la fecha acordada de entrega";
            }
        
            this.strMensaje += "Cordial saludo Señores " + strNombreProveedor + ".<br /><br />";
            this.strMensaje += "Los datos asociados con el pedido #" + strNumPedido.trim() + " son:<br /><br />";
            this.strMensaje += "- <b>Nombre del grupo:</b> " + strGrupo.trim() + ".<br />";
            //this.strMensaje += "- <b>Nombre del proveedor:</b> " + strNombreProveedor + ".<br />";
            this.strMensaje += "- <b>Fecha de envío al proveedor [aaaa-mm-dd]:</b> " + strFechaEnvioProveedor + ".<br />";
            this.strMensaje += "- <b>Fecha acordada de entrega [aaaa-mm-dd]:</b> " + strFechaAcordada + ".<br /><br />";
            this.strMensaje += "Atentamente,<br /><br />";
            this.strMensaje += "Administración de la SIU<br /><br />";
            this.strMensaje += "<b>NOTA</b>: Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";
                          
            this.parametroMail = new ParametroMail();
            this.parametroMail.setDestinatario(this.strDestinatario);
            this.parametroMail.setAsunto(this.strAsunto);
            this.parametroMail.setMensaje(this.strMensaje);

            sendMailHTML(this.parametroMail);
            new GIDaoException("Notificación enviada correctamente a " + strNombreDestinatario + " al correo " + strEmailDestinatario);
        }                
    }

    @Override
    public void notificarVencimientoDocumentos(Documento documento) throws GIDaoException {
        
        String strNombreDestinatario=null, strEmailDestinatario=null, strCodigo=null, strIdCoordinacion=null, strIdDestinatario=null, strTipo=null, strNomDocumento=null, strProceso=null, strTipoDocumento=null;
        String strVigencia=null, strComplementoMsg=null, strComplementoAsunto=null;
        String[] strTemp= null;
        Coordinacion coordinacion = null;
        Persona persona = null;
        
        ParametrosASIUDAO parametrosASIUDAO = new ParametrosASIUDAOImpl();       
                      
        this.strDestinatario = null;
        this.strAsunto = null;
        this.strMensaje = "";

        strIdCoordinacion = documento.getCoordinacion().trim();     
        strTemp = strIdCoordinacion.split("\\(");

        if (strTemp != null){

            strIdCoordinacion = strTemp[1].replace(")", "").trim();                
            coordinacion = parametrosASIUDAO.obtenerCoordinacionXId(strIdCoordinacion);

            if (coordinacion != null){

                strIdDestinatario = coordinacion.getResponsable().trim();                                        
                persona = parametrosASIUDAO.obtenerPersonaXId(strIdDestinatario);

                if (persona != null){

                    strNombreDestinatario = persona.getNombreCompleto();
                    strEmailDestinatario = persona.getCorreoInstitucional();               
                    
                    if (this.strModoPdn.equals("N")){
                        this.strDestinatario = this.strEmailDllo;            
                    }else{
                        this.strDestinatario = strEmailDestinatario;            
                    }

                    strTipo = documento.getTipo().trim();
                    strNomDocumento = documento.getNombre();
                    strCodigo = documento.getCodigo();
                    strProceso = documento.getProceso();
                    strVigencia = documento.getVigencia();

                    if (strVigencia.equals("VIG")){
                        strComplementoAsunto = " próximo(a) a perder su vigencia";
                        strComplementoMsg = " se encuentra próximo(a) a perder su vigencia";
                    }

                    if (strVigencia.equals("NEU")){
                        strComplementoAsunto = " pierde su vigencia el día de hoy";
                        strComplementoMsg = " pierde su vigencia el día de hoy";
                    }

                    if (strVigencia.equals("VEN")){
                        strComplementoAsunto = " superó el tiempo recomendado para su revisión";
                        strComplementoMsg = " superó el tiempo recomendado para su revisión";
                    }

                    this.strMensaje += "Cordial saludo Señor(a). <b>" + strNombreDestinatario + "</b>.<br /><br />";

                    if (strTipo.equals("FTS")){
                        this.strAsunto = "ALERTA: Ficha técnica de servicio " + strNomDocumento + strComplementoAsunto;
                        this.strMensaje += "La ficha técnica del servicio <b>" + strNomDocumento + " (" + strCodigo +")</b> asociada al proceso <b>" + strProceso + "</b>" + strComplementoMsg + ".<br /><br />";
                    }

                    if (strTipo.equals("DOC")){                            

                        strTipoDocumento = strCodigo.substring(0, 1).trim();

                        if (strTipoDocumento.equals("D")){
                            this.strAsunto = "ALERTA: Documento " + strNomDocumento + strComplementoAsunto;
                            this.strMensaje += "El documento <b>" + strNomDocumento + " (" + strCodigo +")</b> asociado al proceso <b>" + strProceso + "</b>" + strComplementoMsg + ".<br /><br />";
                        }

                        if (strTipoDocumento.equals("M")){
                            this.strAsunto = "ALERTA: " + strNomDocumento + strComplementoAsunto;
                            this.strMensaje += "El <b>" + strNomDocumento + " (" + strCodigo +")</b> asociado al proceso <b>" + strProceso + "</b>" + strComplementoMsg + ".<br /><br />";
                        }

                        if (strTipoDocumento.equals("P")){
                            this.strAsunto = "ALERTA: " + strNomDocumento + strComplementoAsunto;
                            this.strMensaje += "El <b>" + strNomDocumento + " (" + strCodigo +")</b> asociado al proceso <b>" + strProceso + "</b>" + strComplementoMsg + ".<br /><br />";
                        }
                    }

                    if (strTipo.equals("CMI")){
                        this.strAsunto = "ALERTA: Ficha técnica de indicador " + strNomDocumento + strComplementoAsunto;
                        this.strMensaje += "La ficha técnica del indicador <b>" + strNomDocumento + " (" + strCodigo +")</b> asociada al proceso <b>" + strProceso + "</b>" + strComplementoMsg + ".<br /><br />";
                    }

                    this.strMensaje += "Por favor gestione oportunamente su actualización para garantizar la vigencia del documento.<br /><br />";
                    this.strMensaje += "Atentamente,<br /><br />";
                    this.strMensaje += "<b>Administración de la SIU</b><br /><br />";
                    this.strMensaje += "<b>NOTA</b>: Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";

                    this.parametroMail = new ParametroMail();
                    this.parametroMail.setDestinatario(this.strDestinatario);
                    this.parametroMail.setAsunto(this.strAsunto);
                    this.parametroMail.setMensaje(this.strMensaje);

                    sendMailHTML(this.parametroMail);
                    new GIDaoException("Notificación enviada correctamente a " + strNombreDestinatario + " al correo " + strEmailDestinatario + " para el documento " + strNomDocumento + " (" + strCodigo +").");
                }

            }else{
                new GIDaoException("No se pudo recuperar la información de la coordinacion con código " + strIdCoordinacion);
            }

        }else{
            new GIDaoException("No se pudo descomponer el código del documento");
        }        
    }

    @Override
    public void notificarVencimientoRegistroPlanMejoramiento(RegistroPlanMejoramiento registro) throws GIDaoException {
        
        String strCodigoRegistro, strNomProceso, strFuente, strAccionNotificarCorreccion, strAccionNotificarAccion, strAccionNotificarEficacia, strResponsableCorrecion, strResponsableAccion;
        String strEmailRespCorreccion, strNomRespCorreccion, strEmailRespAccion, strNomRespAccion, strEventoNotificado, strMsgComplementarioCorreccion, strFirma, strCodigoNotificacion;
        String strMsgComplementarioAccion, strMsgComplementarioEficacia, strIdJefeInmediato;
        String[] strTemp = null, strTemp2 = null;
        Date dtFechaReporte;
        ParametrosASIUDAO parametrosASIUDAO = new ParametrosASIUDAOImpl();  
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        Correccion correccion = null;
        Accion accion = null;
        Eficacia eficacia = null;
        Persona persona = null;
        Notificacion notificacion = null;
                      
        this.strDestinatario = null;
        this.strAsunto = null;
        this.strMensaje = "";
        
        strCodigoNotificacion = "PLANMEJ";
        
        if (registro != null){        
            
            strCodigoRegistro = registro.getNroRegistro();
            strNomProceso = registro.getNombreProceso();
            strFuente = registro.getFuente();
            dtFechaReporte = registro.getFechaReporte();
            strMsgComplementarioCorreccion = "Por favor gestione su implementación, en la medida de lo posible, para garantizar su eficacia.<br /><br />";
            strMsgComplementarioAccion = "Por favor gestione su implementación, en la medida de lo posible, para garantizar su eficacia.<br /><br />";
            strMsgComplementarioEficacia = "Por favor gestione su revisión, en la medida de lo posible, para verificar o no la eficacia de la acción.<br /><br />";
            strFirma = "Administración de la SIU";

            try{
                notificacion =notificacionDAO.obtenerUno(strCodigoNotificacion);
            }catch(GIDaoException gde){
                new GIDaoException("Se generó un error recuperando la información de la notificación con código " + strCodigoNotificacion, gde);            
                notificacion = null;
            }

            if (registro.getCorreccion() != null){
                correccion = registro.getCorreccion();
                strAccionNotificarCorreccion = correccion.getAccionNotificar();
                strResponsableCorrecion = correccion.getResponsable();

                if ((strResponsableCorrecion != null) && (!strResponsableCorrecion.equals("")) && (!strResponsableCorrecion.equals("No aplica (N/A)"))){

                    strTemp = strResponsableCorrecion.split("\\(");

                    if (strTemp != null){

                        strResponsableCorrecion = strTemp[1].replace(")", "").trim();                                  
                        strTemp2 = strResponsableCorrecion.split("-");                    

                        if (strTemp2 != null){

                            if (strTemp2.length == 1){
                                strResponsableCorrecion = strTemp2[0].trim();      
                            }else{
                                 strResponsableCorrecion = strTemp2[1].trim();              
                            }

                            persona = parametrosASIUDAO.obtenerPersonaXCargo(strResponsableCorrecion);

                            if (persona != null){
                                strNomRespCorreccion = persona.getNombreCompleto();
                                strEmailRespCorreccion = persona.getCorreoInstitucional();
                            }else{
                                
                                // No encontró el responsable por el cargo o no se encuentra activo en la ASIU. Se busca el jefe inmediato para notificarlo.
                                
                                strIdJefeInmediato = parametrosASIUDAO.obtenerJefeInmediatoXCargoEmpleado(strResponsableCorrecion);                      
                                persona = parametrosASIUDAO.obtenerPersonaXId(strIdJefeInmediato);
                                
                                if (persona != null){
                                    strNomRespCorreccion = persona.getNombreCompleto();
                                    strEmailRespCorreccion = persona.getCorreoInstitucional();
                                }else{
                                    
                                    // No encontró el jefe inmediato del responsable o se trata de un responsable general: Comité de Calidad, Coordinadores ASIU. Notifica al responsable por defecto.
                                    
                                    strNomRespCorreccion = notificacion.getNombreDestinario().trim();
                                    strEmailRespCorreccion = notificacion.getEmailDestinario().trim();
                                }                                                                
                            }                            

                            if (this.strModoPdn.equals("N")){
                                this.strDestinatario = this.strEmailDllo;            
                            }else{
                                this.strDestinatario = strEmailRespCorreccion;            
                            }
                            
                            this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomRespCorreccion + "</b>.<br /><br />";

                            if (strAccionNotificarCorreccion.equals("CORRECCCIONDIAVENC")){
                                this.strAsunto = "ALERTA: Corrección con código #" + strCodigoRegistro + " cumple plazo establecido";                                
                                this.strMensaje += "La corrección con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, cumple hoy el plazo establecido para su implementación y aún se encuentra Abierta.<br /><br />";
                            }

                             if (strAccionNotificarCorreccion.equals("CORRECCCIONAVENCER")){
                                this.strAsunto = "ALERTA: Corrección con código #" + strCodigoRegistro + " próxima a cumplir plazo establecido";
                                this.strMensaje += "La corrección con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, está próxima a cumplir el plazo establecido para su implementación y aún se encuentra Abierta.<br /><br />";
                            }

                             if (strAccionNotificarCorreccion.equals("CORRECCCIONVENCIDA")){
                                this.strAsunto = "ALERTA: Corrección con código " + strCodigoRegistro + " cumplió plazo establecido";
                                this.strMensaje += "La corrección con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, cumplió el plazo establecido para su implementación y aún se encuentra Abierta.<br /><br />";
                            }

                            this.strMensaje += strMsgComplementarioCorreccion;

                            this.strMensaje += "Atentamente,<br /><br />";
                            this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
                            this.strMensaje += "<b>NOTA</b>: Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";

                            this.parametroMail = new ParametroMail();
                            this.parametroMail.setDestinatario(this.strDestinatario);
                            this.parametroMail.setAsunto(this.strAsunto);
                            this.parametroMail.setMensaje(this.strMensaje);

                            sendMailHTML(this.parametroMail);
                            new GIDaoException("Notificación de corrección enviada correctamente a " + strNomRespCorreccion + " al correo " + strEmailRespCorreccion + " para la correción con código #" + strCodigoRegistro);

                        }else{
                            new GIDaoException("CORRECCION: No se pudo dividir el responsable 2.");
                        }
                    }else{
                        new GIDaoException("CORRECCION: No se pudo dividir el responsable 1.");
                    }
                }
            }

            this.strDestinatario = null;
            this.strAsunto = null;
            this.strMensaje = "";
            persona = null;
            strTemp = null;
            strTemp2 = null;

            if (registro.getAccion() != null){
                accion = registro.getAccion();
                strAccionNotificarAccion = accion.getAccionNotificar();
                strResponsableAccion = accion.getResponsable();

                if ((strResponsableAccion != null) && (!strResponsableAccion.equals("")) && (!strResponsableAccion.equals("No aplica (N/A)"))){
                     strTemp = strResponsableAccion.split("\\(");

                    if (strTemp != null){

                        strResponsableAccion = strTemp[1].replace(")", "").trim();                                  
                        strTemp2 = strResponsableAccion.split("-");                    

                        if (strTemp2 != null){

                            if (strTemp2.length == 1){
                                strResponsableAccion = strTemp2[0].trim();      
                            }else{
                                 strResponsableAccion = strTemp2[1].trim();              
                            }

                            persona = parametrosASIUDAO.obtenerPersonaXCargo(strResponsableAccion);

                            if (persona != null){
                                strNomRespAccion = persona.getNombreCompleto();
                                strEmailRespAccion = persona.getCorreoInstitucional();
                            }else{
                                
                                // No encontró el responsable por el cargo o no se encuentra activo en la ASIU. Se busca el jefe inmediato para notificarlo.
                                                                                        
                                strIdJefeInmediato = parametrosASIUDAO.obtenerJefeInmediatoXCargoEmpleado(strResponsableAccion);          
                                persona = parametrosASIUDAO.obtenerPersonaXId(strIdJefeInmediato);
                                
                                if (persona != null){        
                                    strNomRespAccion = persona.getNombreCompleto();
                                    strEmailRespAccion = persona.getCorreoInstitucional();
                                }else{                          
                                    
                                    // No encontró el jefe inmediato del responsable o se trata de un responsable general: Comité de Calidad, Coordinadores ASIU. Notifica al responsable por defecto.
                                    
                                    strNomRespAccion = notificacion.getNombreDestinario().trim();
                                    strEmailRespAccion = notificacion.getEmailDestinario().trim();
                                }                                
                            }

                           if (this.strModoPdn.equals("N")){
                                this.strDestinatario = this.strEmailDllo;            
                            }else{
                                this.strDestinatario = strEmailRespAccion;            
                            }
                                               
                            this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomRespAccion + "</b>.<br /><br />";

                            if (registro.getEficacia() == null){            

                                strEventoNotificado = "acción";

                                if (strAccionNotificarAccion.equals("ACCIONDIAVENC")){
                                    this.strAsunto = "ALERTA: La acción con código #" + strCodigoRegistro + " cumple plazo establecido";                                
                                    this.strMensaje += "La acción con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, cumple hoy el plazo establecido para su implementación y aún se encuentra Abierta.<br /><br />";
                                }

                                 if (strAccionNotificarAccion.equals("ACCIONAVENCER")){
                                    this.strAsunto = "ALERTA: La acción con código #" + strCodigoRegistro + " próxima a cumplir plazo establecido";
                                    this.strMensaje += "La acción con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, está próxima a cumplir el plazo establecido para su implementación y aún se encuentra Abierta.<br /><br />";
                                }

                                 if (strAccionNotificarAccion.equals("ACCIONVENCIDA")){
                                    this.strAsunto = "ALERTA: La acción con código #" + strCodigoRegistro + " cumplió plazo establecido";
                                    this.strMensaje += "La acción con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, cumplió el plazo establecido para su implementación y aún se encuentra Abierta.<br /><br />";
                                }

                                 this.strMensaje += strMsgComplementarioAccion;
                            }else{
                                eficacia = registro.getEficacia();
                                strAccionNotificarEficacia = eficacia.getAccionNotificar();
                                strEventoNotificado = "eficacia";

                                if (strAccionNotificarEficacia.equals("EFICACIADIAVENC")){     
                                    this.strAsunto = "ALERTA: Eficacia de la acción con código #" + strCodigoRegistro + " cumple plazo establecido";                                
                                    this.strMensaje += "La eficacia de la acción con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, cumple hoy el plazo establecido para su revisión y aún se encuentra Pendiente.<br /><br />";
                                }

                                 if (strAccionNotificarEficacia.equals("EFICACIAAVENCER")){
                                    this.strAsunto = "ALERTA: Eficacia de la acción con código #" + strCodigoRegistro + " próxima a cumplir plazo establecido";
                                    this.strMensaje += "La eficacia de la acción con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, está próxima a cumplir el plazo establecido para su revisión y aún se encuentra Pendiente.<br /><br />";
                                }

                                 if (strAccionNotificarEficacia.equals("EFICACIAVENCIDA")){
                                     this.strAsunto = "ALERTA: Eficacia de la acción con código #" + strCodigoRegistro + " cumplió plazo establecido";
                                    this.strMensaje += "La eficacia de la acción con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, cumplió el plazo establecido para su revisión y aún se encuentra Pendiente.<br /><br />";
                                }

                                 this.strMensaje += strMsgComplementarioEficacia;
                            }

                            this.strMensaje += "Atentamente,<br /><br />";
                            this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
                            this.strMensaje += "<b>NOTA</b>: Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";

                            this.parametroMail = new ParametroMail();
                            this.parametroMail.setDestinatario(this.strDestinatario);
                            this.parametroMail.setAsunto(this.strAsunto);
                            this.parametroMail.setMensaje(this.strMensaje);

                            sendMailHTML(this.parametroMail);
                            new GIDaoException("Notificación enviada correctamente a " + strNomRespAccion + " al correo " + strEmailRespAccion + " para la " + strEventoNotificado + " con código #" + strCodigoRegistro);                            

                        }else{
                            new GIDaoException("ACCIÓN: No se pudo dividir el responsable 2.");
                        }
                    }else{
                        new GIDaoException("ACCIÓN: No se pudo dividir el responsable 1.");
                    }   
                }                     
            }    
        }
    }

    @Override
    public void notificarVencimientoRegistroPlanCalibEqASIU(RegistroPlanCalibracion registro) throws GIDaoException {
        
        String strCodigoNotificacion, strEquipo, strAreaLab, strMarca, strModelo, strMsgComplementario, strFirma, strRespCalibracion, strRespMnto, strAccionNotificarCalibracion, strAccionNotificarMnto;
        String strNomRespCalibracion, strEmailRespCalibracion, strNomRespMnto, strEmailRespMnto;
        String[] strTemp = null, strTemp2 = null;
        ParametrosASIUDAO parametrosASIUDAO = new ParametrosASIUDAOImpl();  
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        Notificacion notificacion = null;
        Calibracion calibracion = null;
        Persona persona = null;
        
        this.strDestinatario = null;
        this.strAsunto = null;
        this.strMensaje = "";
        
        strCodigoNotificacion = "PLANCALIBEQASIU";
        
        if (registro != null){ 
            strAreaLab = registro.getAreaLab();
            strEquipo = registro.getEquipo();
            strMarca = registro.getMarca();
            strModelo = registro.getModelo();
            
            strMsgComplementario = "Por favor gestione su ejecución, en la medida de lo posible, para garantizar el buen funcionamiento del equipo.<br /><br />";
            strFirma = "Administración de la SIU";

            try{
                notificacion =notificacionDAO.obtenerUno(strCodigoNotificacion);
            }catch(GIDaoException gde){
                new GIDaoException("Se generó un error recuperando la información de la notificación con código " + strCodigoNotificacion, gde);            
                notificacion = null;
            }
            
            if (registro.getCalibracion() != null){
                calibracion = registro.getCalibracion();
                strRespCalibracion = calibracion.getResponsable();
                strAccionNotificarCalibracion = calibracion.getAccionNotificar();
                
                strTemp = strRespCalibracion.split("\\(");

                if (strTemp != null){
                    strRespCalibracion = strTemp[1].replace(")", "").trim();                                  
                        strTemp2 = strRespCalibracion.split("-");                    

                        if (strTemp2 != null){

                            if (strTemp2.length == 1){
                                strRespCalibracion = strTemp2[0].trim();      
                            }else{
                                 strRespCalibracion = strTemp2[1].trim();              
                            }
                            
                            persona = parametrosASIUDAO.obtenerPersonaXCargo(strRespCalibracion);

                            if (persona != null){
                                strNomRespCalibracion = persona.getNombreCompleto();
                                strEmailRespCalibracion = persona.getCorreoInstitucional();
                            }else{
                                strNomRespCalibracion = notificacion.getNombreDestinario().trim();
                                strEmailRespCalibracion = notificacion.getEmailDestinario().trim();
                            }
                                                        
                            if (this.strModoPdn.equals("N")){
                                this.strDestinatario = this.strEmailDllo;            
                            }else{
                                this.strDestinatario = strEmailRespCalibracion;            
                            }
                           
                            this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomRespCalibracion + "</b>.<br /><br />";

                            if (strAccionNotificarCalibracion.equals("CALIBRACIONDIAVENC")){
                                this.strAsunto = "ALERTA: Calibración del equipo " + strEquipo + " cumple plazo establecido";                                
                                this.strMensaje += "La calibración del equipo <b>" + strEquipo + "</b> registrada en el Plan de Mantenimiento y Calibración de Equipos ASIU, cumple hoy el plazo establecido para su ejecución y aún se encuentra Pendiente.<br /><br />";
                            }

                             if (strAccionNotificarCalibracion.equals("CALIBRACIONAVENCER")){
                                this.strAsunto = "ALERTA: Calibración del equipo " + strEquipo + " próxima a cumplir plazo establecido";
                                this.strMensaje += "La calibración del equipo <b>" + strEquipo + "</b> registrada en el Plan de Mantenimiento y Calibración de Equipos ASIU, está próxima a cumplir el plazo establecido para su ejecución y aún se encuentra Pendiente.<br /><br />";
                            }

                             if (strAccionNotificarCalibracion.equals("CALIBRACIONVENCIDA")){
                                this.strAsunto = "ALERTA: Calibración del equipo " + strEquipo + " cumplió plazo establecido";
                                this.strMensaje += "La calibración del equipo <b>" + strEquipo + "</b> registrada en el Plan de Mantenimiento y Calibración de Equipos ASIU, cumplió el plazo establecido para su ejecución y aún se encuentra Pendiente.<br /><br />";
                            }

                            this.strMensaje += strMsgComplementario;

                            this.strMensaje += "Atentamente,<br /><br />";
                            this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
                            this.strMensaje += "<b>NOTA</b>: Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";
                            
                            this.parametroMail = new ParametroMail();
                            this.parametroMail.setDestinatario(this.strDestinatario);
                            this.parametroMail.setAsunto(this.strAsunto);
                            this.parametroMail.setMensaje(this.strMensaje);

                            sendMailHTML(this.parametroMail);
                            new GIDaoException("Notificación de calibración enviada correctamente a " + strNomRespCalibracion + " al correo " + strEmailRespCalibracion + " para el equipo " + strEquipo);                           
                        }else{
                            new GIDaoException("CALIBRACIÓN: No se pudo dividir el responsable 2.");
                        }
                    }else{
                        new GIDaoException("CALIBRACIÓN: No se pudo dividir el responsable 1.");
                }      
            }                                               
        }
    }

    @Override
    public void notificarVencimientoContrato(Contrato contrato) throws GIDaoException {
        
        String strCodigoNotificacion, strTipoContrato, strFirma, strNomDestinario, strCodigoContrato, strAccionNotificar, strFechaFinalizacion, strGrupo, strContratista;     
        Notificacion notificacion = null;
        strCodigoNotificacion = "";
        strFechaFinalizacion = null;
        strAccionNotificar = null;
        strGrupo = null;
        strContratista = null;
                
        strTipoContrato = contrato.getTipoContrato();
        strFirma = "Administración de la SIU";
        
        if (strTipoContrato.equals("NALANT")){
            strCodigoNotificacion = "CONTRATOSNALESANT";
        }
        
        if (strTipoContrato.equals("NALACT")){
            strCodigoNotificacion = "CONTRATOSNALESACT";
        }
        
        if (strTipoContrato.equals("INTERNAL")){        
            strCodigoNotificacion = "CONTRATOSINTERN";
        }
        
         if (strTipoContrato.equals("PS")){        
            strCodigoNotificacion = "CONTRATOSPS";
        }
        
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        
        try{
            notificacion =notificacionDAO.obtenerUno(strCodigoNotificacion);
        }catch(GIDaoException gde){
            new GIDaoException("Se generó un error recuperando la información de la notificación con código " + strCodigoNotificacion, gde);            
            notificacion = null;
        }        
        
        if (notificacion != null){

            this.strDestinatario = null;
            this.strAsunto = null;
            this.strMensaje = "";

            if (this.strModoPdn.equals("N")){
                this.strDestinatario = this.strEmailDllo;            
            }else{
                this.strDestinatario = notificacion.getEmailDestinario().trim();            
            }
            
            strNomDestinario = notificacion.getNombreDestinario();
            strCodigoContrato = contrato.getCodigoContrato();
            strFechaFinalizacion = contrato.getFechaTerminacion();
            strAccionNotificar = contrato.getAccionNotificar();
            strGrupo = contrato.getGrupo();
            strContratista = contrato.getContratista();
            
            if (strCodigoContrato.equals("")){
                strCodigoContrato = "[Sin código]";
            }
                                                          
            this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomDestinario + "</b>.<br /><br />";       
            
             if ((strTipoContrato.equals("NALANT")) || (strTipoContrato.equals("NALACT"))){                 
                if (strAccionNotificar.equals("DIAVENC")){
                     this.strAsunto = "ALERTA: Contrato nacional ha finalizado";
                     this.strMensaje += "El contrato nacional con código " + strCodigoContrato + " ha finalizado el " + strFechaFinalizacion + ".<br /><br />";
                }
                
                if (strAccionNotificar.equals("AVENCER")){
                     this.strAsunto = "ALERTA: Contrato nacional próximo a finalizar";
                     this.strMensaje += "El contrato nacional con código " + strCodigoContrato + " finalizará el próximo " + strFechaFinalizacion + ".<br /><br />";
                }               
            }
            
            if (strTipoContrato.equals("INTERNAL")){                 
                if (strAccionNotificar.equals("DIAVENC")){
                     this.strAsunto = "ALERTA: Contrato internacional ha finalizado";
                     this.strMensaje += "El contrato internacional con código " + strCodigoContrato + " ha finalizado el  " + strFechaFinalizacion+ ".<br /><br />";
                }
                
                if (strAccionNotificar.equals("AVENCER")){
                     this.strAsunto = "ALERTA: Contrato internacional próximo a finalizar";
                     this.strMensaje += "El contrato internacional con código " + strCodigoContrato + " finalizará el próximo " + strFechaFinalizacion + ".<br /><br />";
                }               
            }
            
             if (strTipoContrato.equals("PS")){                 
                if (strAccionNotificar.equals("DIAVENC")){
                     this.strAsunto = "ALERTA: Contrato de Prestación de Servicios ha finalizado";
                     this.strMensaje += "El contrato de prestación de servicios con código " + strCodigoContrato + " para el contratista " + strContratista + " del grupo " + strGrupo + " ha finalizado el  " + strFechaFinalizacion+ ".<br /><br />";
                }
                
                if (strAccionNotificar.equals("AVENCER")){
                     this.strAsunto = "ALERTA: Contrato de Prestación de Servicios próximo a finalizar";
                     this.strMensaje += "El contrato de prestación de servicios con código " + strCodigoContrato + " para el contratista " + strContratista + " del grupo " + strGrupo + " finalizará el próximo " + strFechaFinalizacion + ".<br /><br />";
                }               
            }
                        
            this.strMensaje += "Atentamente,<br /><br />";
            this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
            this.strMensaje += "<b>NOTA</b>: Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";

            this.parametroMail = new ParametroMail();
            this.parametroMail.setDestinatario(this.strDestinatario);
            this.parametroMail.setAsunto(this.strAsunto);
            this.parametroMail.setMensaje(this.strMensaje);

            sendMailHTML(this.parametroMail);
            
            if ((strTipoContrato.equals("NALANT")) || (strTipoContrato.equals("NALACT"))){
                new GIDaoException("Notificación enviada correctamente a " + strNomDestinario + " al correo electrónico " + this.strDestinatario + " para el contrato nacional con código " + strCodigoContrato);
            }   
            
             if (strTipoContrato.equals("INTERNAL")){                 
                new GIDaoException("Notificación enviada correctamente a " + strNomDestinario + " al correo electrónico " + this.strDestinatario + " para el contrato internacional con código " + strCodigoContrato);
            }   
             
             if (strTipoContrato.equals("PS")){                 
                new GIDaoException("Notificación enviada correctamente a " + strNomDestinario + " al correo electrónico " + this.strDestinatario + " para el contrato de prestación de servicios con código " + strCodigoContrato);
            }   
        }
    }

    @Override
    public void notificarVencimientoRegistroPlanMejoramientoLEC(RegistroPlanMejoramiento registro) throws GIDaoException {
        String strCodigoRegistro, strNomProceso, strFuente, strAccionNotificarCorreccion, strAccionNotificarAccion, strAccionNotificarEficacia, strResponsableCorrecion, strResponsableAccion;
        String strEmailRespCorreccion, strNomRespCorreccion, strEmailRespAccion, strNomRespAccion, strEventoNotificado, strMsgComplementarioCorreccion, strFirma, strCodigoNotificacion;
        String strMsgComplementarioAccion, strMsgComplementarioEficacia, strIdJefeInmediato;
        String[] strTemp = null, strTemp2 = null;
        Date dtFechaReporte;
        ParametrosASIUDAO parametrosASIUDAO = new ParametrosASIUDAOImpl();  
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        Correccion correccion = null;
        Accion accion = null;
        Eficacia eficacia = null;
        Persona persona = null;
        Notificacion notificacion = null;
                      
        this.strDestinatario = null;
        this.strAsunto = null;
        this.strMensaje = "";
        
        strCodigoNotificacion = "PLANMEJLEC";
        
        if (registro != null){        
            
            strCodigoRegistro = registro.getNroRegistro();
            strNomProceso = registro.getNombreProceso();
            strFuente = registro.getFuente();
            dtFechaReporte = registro.getFechaReporte();
            strMsgComplementarioCorreccion = "Por favor gestione su implementación, en la medida de lo posible, para garantizar su eficacia.<br /><br />";
            strMsgComplementarioAccion = "Por favor gestione su implementación, en la medida de lo posible, para garantizar su eficacia.<br /><br />";
            strMsgComplementarioEficacia = "Por favor gestione su revisión, en la medida de lo posible, para verificar o no la eficacia de la acción.<br /><br />";
            strFirma = "Administración de la SIU";

            try{
                notificacion =notificacionDAO.obtenerUno(strCodigoNotificacion);
            }catch(GIDaoException gde){
                new GIDaoException("Se generó un error recuperando la información de la notificación con código " + strCodigoNotificacion, gde);            
                notificacion = null;
            }

            if (registro.getCorreccion() != null){
                correccion = registro.getCorreccion();
                strAccionNotificarCorreccion = correccion.getAccionNotificar();
                strResponsableCorrecion = correccion.getResponsable();

                if ((strResponsableCorrecion != null) && (!strResponsableCorrecion.equals("")) && (!strResponsableCorrecion.equals("No aplica (N/A)"))){

                    strTemp = strResponsableCorrecion.split("\\(");

                    if (strTemp != null){

                        strResponsableCorrecion = strTemp[1].replace(")", "").trim();                                  
                        strTemp2 = strResponsableCorrecion.split("-");                    

                        if (strTemp2 != null){

                            if (strTemp2.length == 1){
                                strResponsableCorrecion = strTemp2[0].trim();      
                            }else{
                                 strResponsableCorrecion = strTemp2[1].trim();              
                            }

                            persona = parametrosASIUDAO.obtenerPersonaXCargo(strResponsableCorrecion);

                            if (persona != null){
                                strNomRespCorreccion = persona.getNombreCompleto();
                                strEmailRespCorreccion = persona.getCorreoInstitucional();
                            }else{
                                
                                // No encontró el responsable por el cargo o no se encuentra activo en la ASIU. Se busca el jefe inmediato para notificarlo.
                                
                                strIdJefeInmediato = parametrosASIUDAO.obtenerJefeInmediatoXCargoEmpleado(strResponsableCorrecion);                      
                                persona = parametrosASIUDAO.obtenerPersonaXId(strIdJefeInmediato);
                                
                                if (persona != null){
                                    strNomRespCorreccion = persona.getNombreCompleto();
                                    strEmailRespCorreccion = persona.getCorreoInstitucional();
                                }else{
                                    
                                    // No encontró el jefe inmediato del responsable o se trata de un responsable general: Comité de Calidad, Coordinadores ASIU. Notifica al responsable por defecto.
                                    
                                    strNomRespCorreccion = notificacion.getNombreDestinario().trim();
                                    strEmailRespCorreccion = notificacion.getEmailDestinario().trim();
                                }                                                                
                            }                            

                            if (this.strModoPdn.equals("N")){
                                this.strDestinatario = this.strEmailDllo;            
                            }else{
                                this.strDestinatario = strEmailRespCorreccion;            
                            }
                            
                            this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomRespCorreccion + "</b>.<br /><br />";

                            if (strAccionNotificarCorreccion.equals("CORRECCCIONDIAVENC")){
                                this.strAsunto = "ALERTA: Corrección con código #" + strCodigoRegistro + " cumple plazo establecido";                                
                                this.strMensaje += "La corrección con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, cumple hoy el plazo establecido para su implementación y aún se encuentra Abierta.<br /><br />";
                            }

                             if (strAccionNotificarCorreccion.equals("CORRECCCIONAVENCER")){
                                this.strAsunto = "ALERTA: Corrección con código #" + strCodigoRegistro + " próxima a cumplir plazo establecido";
                                this.strMensaje += "La corrección con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, está próxima a cumplir el plazo establecido para su implementación y aún se encuentra Abierta.<br /><br />";
                            }

                             if (strAccionNotificarCorreccion.equals("CORRECCCIONVENCIDA")){
                                this.strAsunto = "ALERTA: Corrección con código " + strCodigoRegistro + " cumplió plazo establecido";
                                this.strMensaje += "La corrección con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, cumplió el plazo establecido para su implementación y aún se encuentra Abierta.<br /><br />";
                            }

                            this.strMensaje += strMsgComplementarioCorreccion;

                            this.strMensaje += "Atentamente,<br /><br />";
                            this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
                            this.strMensaje += "<b>NOTA</b>: Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";

                            this.parametroMail = new ParametroMail();
                            this.parametroMail.setDestinatario(this.strDestinatario);
                            this.parametroMail.setAsunto(this.strAsunto);
                            this.parametroMail.setMensaje(this.strMensaje);

                            sendMailHTML(this.parametroMail);
                            new GIDaoException("Notificación de corrección enviada correctamente a " + strNomRespCorreccion + " al correo " + strEmailRespCorreccion + " para la correción con código #" + strCodigoRegistro);

                        }else{
                            new GIDaoException("CORRECCION: No se pudo dividir el responsable 2.");
                        }
                    }else{
                        new GIDaoException("CORRECCION: No se pudo dividir el responsable 1.");
                    }
                }
            }

            this.strDestinatario = null;
            this.strAsunto = null;
            this.strMensaje = "";
            persona = null;
            strTemp = null;
            strTemp2 = null;

            if (registro.getAccion() != null){
                accion = registro.getAccion();
                strAccionNotificarAccion = accion.getAccionNotificar();
                strResponsableAccion = accion.getResponsable();

                if ((strResponsableAccion != null) && (!strResponsableAccion.equals("")) && (!strResponsableAccion.equals("No aplica (N/A)"))){
                     strTemp = strResponsableAccion.split("\\(");

                    if (strTemp != null){

                        strResponsableAccion = strTemp[1].replace(")", "").trim();                                  
                        strTemp2 = strResponsableAccion.split("-");                    

                        if (strTemp2 != null){

                            if (strTemp2.length == 1){
                                strResponsableAccion = strTemp2[0].trim();      
                            }else{
                                 strResponsableAccion = strTemp2[1].trim();              
                            }

                            persona = parametrosASIUDAO.obtenerPersonaXCargo(strResponsableAccion);

                            if (persona != null){
                                strNomRespAccion = persona.getNombreCompleto();
                                strEmailRespAccion = persona.getCorreoInstitucional();
                            }else{
                                
                                // No encontró el responsable por el cargo o no se encuentra activo en la ASIU. Se busca el jefe inmediato para notificarlo.
                                                                                        
                                strIdJefeInmediato = parametrosASIUDAO.obtenerJefeInmediatoXCargoEmpleado(strResponsableAccion);          
                                persona = parametrosASIUDAO.obtenerPersonaXId(strIdJefeInmediato);
                                
                                if (persona != null){        
                                    strNomRespAccion = persona.getNombreCompleto();
                                    strEmailRespAccion = persona.getCorreoInstitucional();
                                }else{                          
                                    
                                    // No encontró el jefe inmediato del responsable o se trata de un responsable general: Comité de Calidad, Coordinadores ASIU. Notifica al responsable por defecto.
                                    
                                    strNomRespAccion = notificacion.getNombreDestinario().trim();
                                    strEmailRespAccion = notificacion.getEmailDestinario().trim();
                                }                                
                            }

                           if (this.strModoPdn.equals("N")){
                                this.strDestinatario = this.strEmailDllo;            
                            }else{
                                this.strDestinatario = strEmailRespAccion;            
                            }
                                               
                            this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomRespAccion + "</b>.<br /><br />";

                            if (registro.getEficacia() == null){            

                                strEventoNotificado = "acción";

                                if (strAccionNotificarAccion.equals("ACCIONDIAVENC")){
                                    this.strAsunto = "ALERTA: La acción con código #" + strCodigoRegistro + " cumple plazo establecido";                                
                                    this.strMensaje += "La acción con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, cumple hoy el plazo establecido para su implementación y aún se encuentra Abierta.<br /><br />";
                                }

                                 if (strAccionNotificarAccion.equals("ACCIONAVENCER")){
                                    this.strAsunto = "ALERTA: La acción con código #" + strCodigoRegistro + " próxima a cumplir plazo establecido";
                                    this.strMensaje += "La acción con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, está próxima a cumplir el plazo establecido para su implementación y aún se encuentra Abierta.<br /><br />";
                                }

                                 if (strAccionNotificarAccion.equals("ACCIONVENCIDA")){
                                    this.strAsunto = "ALERTA: La acción con código #" + strCodigoRegistro + " cumplió plazo establecido";
                                    this.strMensaje += "La acción con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, cumplió el plazo establecido para su implementación y aún se encuentra Abierta.<br /><br />";
                                }

                                 this.strMensaje += strMsgComplementarioAccion;
                            }else{
                                eficacia = registro.getEficacia();
                                strAccionNotificarEficacia = eficacia.getAccionNotificar();
                                strEventoNotificado = "eficacia";

                                if (strAccionNotificarEficacia.equals("EFICACIADIAVENC")){     
                                    this.strAsunto = "ALERTA: Eficacia de la acción con código #" + strCodigoRegistro + " cumple plazo establecido";                                
                                    this.strMensaje += "La eficacia de la acción con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, cumple hoy el plazo establecido para su revisión y aún se encuentra Pendiente.<br /><br />";
                                }

                                 if (strAccionNotificarEficacia.equals("EFICACIAAVENCER")){
                                    this.strAsunto = "ALERTA: Eficacia de la acción con código #" + strCodigoRegistro + " próxima a cumplir plazo establecido";
                                    this.strMensaje += "La eficacia de la acción con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, está próxima a cumplir el plazo establecido para su revisión y aún se encuentra Pendiente.<br /><br />";
                                }

                                 if (strAccionNotificarEficacia.equals("EFICACIAVENCIDA")){
                                     this.strAsunto = "ALERTA: Eficacia de la acción con código #" + strCodigoRegistro + " cumplió plazo establecido";
                                    this.strMensaje += "La eficacia de la acción con código #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, cumplió el plazo establecido para su revisión y aún se encuentra Pendiente.<br /><br />";
                                }

                                 this.strMensaje += strMsgComplementarioEficacia;
                            }

                            this.strMensaje += "Atentamente,<br /><br />";
                            this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
                            this.strMensaje += "<b>NOTA</b>: Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";

                            this.parametroMail = new ParametroMail();
                            this.parametroMail.setDestinatario(this.strDestinatario);
                            this.parametroMail.setAsunto(this.strAsunto);
                            this.parametroMail.setMensaje(this.strMensaje);

                            sendMailHTML(this.parametroMail);
                            new GIDaoException("Notificación enviada correctamente a " + strNomRespAccion + " al correo " + strEmailRespAccion + " para la " + strEventoNotificado + " con código #" + strCodigoRegistro);                            

                        }else{
                            new GIDaoException("ACCIÓN: No se pudo dividir el responsable 2.");
                        }
                    }else{
                        new GIDaoException("ACCIÓN: No se pudo dividir el responsable 1.");
                    }   
                }                     
            }    
        }
    }

    @Override
    public void notificarVencimientoAnticipoViaticoTiquete(AnticipoViaticoTiquete anticipoViaticoTiquete) throws GIDaoException {
        
        String strNombreDestinatario=null, strEmailDestinatario=null, strReserva, strSolicitante, strTipoSolicitud, strNroComprobante, strAccionNotificar, strValorLegalizado, strFechaLimiteEntrega;
        String strCodigoNotificacion=null, strObs, strResponsable, strLugarComision, strFechaInicioComision, strNroTicket, strGrupo;
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        Notificacion notificacion = null;
                
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        
        strCodigoNotificacion = anticipoViaticoTiquete.getCodigoNotificacion().trim();
        strReserva = "";
        strSolicitante = "";
        strTipoSolicitud = "";
        strNroComprobante = "";
        strAccionNotificar = "";
        strValorLegalizado = "";
        strFechaLimiteEntrega = "";
        strObs = "";
        strResponsable = "";
        strLugarComision = "";
        strFechaInicioComision = "";
        strNroTicket = "";
        strGrupo = "";
        
        try{
            notificacion =notificacionDAO.obtenerUno(strCodigoNotificacion);
        }catch(GIDaoException gde){
            new GIDaoException("Se generó un error recuperando la información de la notificación con código " + strCodigoNotificacion, gde);            
            notificacion = null;
        }        
        
        if (notificacion != null){            

            strNombreDestinatario = notificacion.getNombreDestinario().trim();         
            strEmailDestinatario = notificacion.getEmailDestinario().trim();
            strReserva = anticipoViaticoTiquete.getReserva();            
            strSolicitante = anticipoViaticoTiquete.getSolicitante();
            strTipoSolicitud = anticipoViaticoTiquete.getTipoSolicitud();
            strNroComprobante = anticipoViaticoTiquete.getNroComprobante();
            strFechaLimiteEntrega = anticipoViaticoTiquete.getFechaLimiteEntrega();
            strAccionNotificar = anticipoViaticoTiquete.getAccionNotificar();
            strValorLegalizado = anticipoViaticoTiquete.getValorLegalizado().trim();            
            strObs = anticipoViaticoTiquete.getObservacion().trim();
            strResponsable = anticipoViaticoTiquete.getResponsable().trim();
            strLugarComision = anticipoViaticoTiquete.getLugarComision().trim();
            strFechaInicioComision = anticipoViaticoTiquete.getFechaInicioComision().trim();
            strNroTicket = anticipoViaticoTiquete.getNroTicket().trim();
            
            if (strCodigoNotificacion.equals("ANTVIATTIQACT")){
                strGrupo = anticipoViaticoTiquete.getGrupo();
            }
            
            if ((strValorLegalizado != null) && (!strValorLegalizado.equals(""))){                
                strValorLegalizado = "$" + funcionesComunesDAO.marcarMiles(strValorLegalizado);
            }else{
                strValorLegalizado = "$0";
            }               
                        
            this.strDestinatario = null;
            this.strAsunto = null;
            this.strMensaje = "";
            
            if (this.strModoPdn.equals("N")){
                this.strDestinatario = this.strEmailDllo;            
            }else{
                this.strDestinatario = strEmailDestinatario;            
            }    
                                   
            if (strAccionNotificar.equals("DIAVENC")){
                this.strAsunto = "ALERTA: La solicitud de " + strTipoSolicitud.toUpperCase() + " con comprobante #" + strNroComprobante+ " ha cumplido la fecha límite de entrega";                 
            }
            
            /*if (strAccionNotificar.equals("AVENCER")){
                this.strAsunto = "ALERTA: La solicitud de " + strTipoSolicitud.toUpperCase() + " con comprobante #" + strNroComprobante+ " está próxima a cumplir la fecha límite de entrega";
            }*/
        
            this.strMensaje += "Cordial saludo.<br /><br />";
            this.strMensaje += "Los datos asociados con la solicitud de <b>" + strTipoSolicitud.toUpperCase() + "</b> con comprobante #" + strNroComprobante + " son:<br /><br />";
            this.strMensaje += "- <b>Nombre del responsable:</b> " + strResponsable.trim() + ".<br />";
            
            if (strCodigoNotificacion.equals("ANTVIATTIQACT")){
                this.strMensaje += "- <b>Nombre del grupo:</b> " + strGrupo.trim() + ".<br />";
            }
                        
            this.strMensaje += "- <b>Nombre del solicitante:</b> " + strSolicitante.trim() + ".<br />";
            this.strMensaje += "- <b>Lugar de comisión:</b> " + strLugarComision.trim() + ".<br />";
            this.strMensaje += "- <b>Valor legalizado:</b> " + strValorLegalizado + ".<br />";
            this.strMensaje += "- <b>Fecha inicio comisión [aaaa-mm-dd]:</b> " + strFechaInicioComision + ".<br />";
            this.strMensaje += "- <b>Fecha fin comisión [aaaa-mm-dd]:</b> " + strFechaLimiteEntrega + ".<br />";
            this.strMensaje += "- <b>Nro. del ticket:</b> " + strNroTicket + ".<br />";
            this.strMensaje += "- <b>Observación:</b> " + strObs + ".<br /><br />";
            this.strMensaje += "Si ya hizo entrega de la documentación requerida en la Administración SIU, por favor hace caso omiso de este mensaje.<br /><br />";
            this.strMensaje += "Recuerde que:<br /><br />";
            this.strMensaje += "- Para legalizar Anticipos, debe adjuntar las facturas que sumen el total del valor otorgado.<br /><br />";
            this.strMensaje += "- Para legalizar Pasajes, debe adjuntar los pasabordos.<br /><br />";
            this.strMensaje += "- Para legalizar Viáticos, debe adjuntar el cumplido y la carta donde se indique que se recibió el dinero.<br /><br />";
            this.strMensaje += "Atentamente,<br /><br />";
            this.strMensaje += "Administración de la SIU<br /><br />";
            this.strMensaje += "<b>NOTA</b>: Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";
                          
            this.parametroMail = new ParametroMail();
            this.parametroMail.setDestinatario(this.strDestinatario);
            this.parametroMail.setAsunto(this.strAsunto);
            this.parametroMail.setMensaje(this.strMensaje);            

            sendMailHTML(this.parametroMail);
            new GIDaoException("Notificación enviada correctamente a " + strNombreDestinatario + " al correo " + strEmailDestinatario);
        }
    }

    @Override
    public void notificarVencimientoCalibracionEquipo(List<CalibracionEquipo> calibraciones) throws GIDaoException {
        
            String strSolicitante, strSolicitanteAnterior, strNroSerie, strProducto, strEmail, strFechaProxCalib, strMsgInicial, strMsgFinal, strTipoEquipo;
            Date dtFechaRecepcion = null;
            Integer intCont = 0;
            strSolicitante = "";
            strSolicitanteAnterior = "";
            strEmail = "";
            strNroSerie = "";
            strProducto = "";
            strMsgFinal = "";
            strMsgInicial = "";
            strTipoEquipo = "";
            String[] strTemp;
            
            FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
                                    
            //strMsgFinal += "Le informo que a partir de este año acreditamos el nuevo servicio de calibración de balanzas en el rango de 1 mg a 19 kg, para que por favor nos tenga en cuenta en el momento que requiera calibrar sus balanzas. Adjunto certificado de acreditación con ONAC.<br /><br />";
            strMsgFinal += "Atentamente,<br /><br />";
            strMsgFinal += "<b>Juan Felipe Gallego Sierra</b><br />";
            strMsgFinal += "Ingeniero de Equipos Científicos<br />";
            strMsgFinal += "Sede de Investigación Universitaria -SIU<br />";
            strMsgFinal += "Teléfono: (574) 219 64 27<br />";
            strMsgFinal += "Correo: lecsiu@udea.edu.co<br />";
            strMsgFinal += "Carrera 53 No. 61 - 30<br />";            
            strMsgFinal += "Laboratorio de Equipos Científicos -LEC<br /><br />";
            strMsgFinal += "<b>NOTA</b>: Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";       
            
            this.strMensaje = "";
            this.strRutaArchivo = "";
            this.strDestinatario = "";
            //this.strRutaArchivo = "C:\\WebApps\\soluciones_siu\\Certificado_ONAC.pdf";
            
            for (CalibracionEquipo calibracionEquipo : calibraciones){                                
                
                strSolicitante = calibracionEquipo.getSolicitante().trim();
                
                if (!(strSolicitante.equals(strSolicitanteAnterior)) && (intCont >0)){
                    
                    strMsgInicial = "Cordial saludo Señores <b> " + strSolicitanteAnterior + "</b>.<br /><br />";             
                    strMsgInicial = strMsgInicial + "Como parte del servicio y compromiso con nuestros clientes, nuestro principal propósito es contribuir con el excelente desempeño de sus equipos, para su comodidad estamos remitiendo los siguientes equipos que cumplirán su vigencia de calibración:<br /><br />";
                    
                    this.strMensaje = strMsgInicial + this.strMensaje;
                    this.strMensaje = this.strMensaje + strMsgFinal;
                    
                    this.strAsunto = "ALERTA: Vencimiento de calibración de equipos " + strSolicitanteAnterior;     
                    
                    if (this.strModoPdn.equals("N")){
                        this.strDestinatario = this.strEmailDllo;            
                    }else{
                        this.strDestinatario = strEmail;            
                    }
                                                              
                    if (!strEmail.equals("")){
                        strTemp = strEmail.split(";");

                        if (strTemp.length > 1){
                            strEmail = strTemp[0];       
                            strEmail = strEmail.replace("null", "").trim();
                            this.strDestinatario = strEmail;
                        }                        
                    }                    
                            
                    notificarMsg(strSolicitante);                    
                    strMsgInicial = "";
                    
                }
                                    
                strNroSerie = calibracionEquipo.getSerie();
                strProducto = calibracionEquipo.getProducto();
                strEmail = calibracionEquipo.getEmail();   
                dtFechaRecepcion = calibracionEquipo.getFechaRecepcion();
                strTipoEquipo = calibracionEquipo.getTipoEquipo();
                
                strFechaProxCalib = funcionesComunesDAO.convertirFechaLarga(dtFechaRecepcion.toString()); 
                this.strMensaje += strTipoEquipo + " - Serie: " + strNroSerie + ", certificado de calibración: " + strProducto + ", cumplimiento de vigencia: " + strFechaProxCalib + ".<br /><br />";
                                                    
                strSolicitanteAnterior = strSolicitante;
                intCont++;
            }        
            
            strMsgInicial = "Cordial saludo Señores <b> " + strSolicitanteAnterior + "</b>.<br /><br />"; 
            strMsgInicial = strMsgInicial + "Los siguientes equipos cumplirán su vigencia de calibración:<br /><br />";
            this.strMensaje = strMsgInicial + this.strMensaje;
            this.strMensaje = this.strMensaje + strMsgFinal;
            this.strAsunto = "ALERTA: Vencimiento de calibración de equipos " + strSolicitanteAnterior;     

            if (this.strModoPdn.equals("N")){
                this.strDestinatario = this.strEmailDllo;            
            }else{
                this.strDestinatario = strEmail;            
            }                       
                                    
            if (!strEmail.equals("")){
                strTemp = strEmail.split(";");

                if (strTemp.length > 1){
                    strEmail = strTemp[0];    
                    strEmail = strEmail.replace("null", "").trim();
                    this.strDestinatario = strEmail;
                }                
            }                        
            
            notificarMsg(strSolicitante);            
            strMsgInicial = "";

    }
    
    private void notificarMsg(String strSolicitante) throws GIDaoException{
                        
        this.parametroMail = new ParametroMail(); 
        this.parametroMail.setDestinatario(this.strDestinatario.replace("null", "").trim());             
        this.parametroMail.setAsunto(this.strAsunto);
        this.parametroMail.setMensaje(this.strMensaje);      
        this.parametroMail.setRutaArchivo(this.strRutaArchivo);
        
        if (this.strRutaArchivo.trim().equals("")){
            sendMailHTML(this.parametroMail);
        }else{
            sendMailHTMLAttach(this.parametroMail);
        }
              
        new GIDaoException("Notificación enviada correctamente a " + strSolicitante + " al correo " + this.strDestinatario);  

        this.strDestinatario = "";
        this.strAsunto = "";
        this.strMensaje = "";
        this.parametroMail = null;        
    }

    @Override
    public void notificarControlInsumos(ControlInsumo controlInsumo) throws GIDaoException {
        
        String strCodigoNotificacion, strFirma, strResponsable, strNomResp, strEmailResp, strArea, strInsumo, strDescripcion, strNumId, strLote, strCantidad, strRango, strFechaVencimiento, strAccionNotificar;
        String[] strTemp = null, strTemp2 = null;
        Date dtFechaCompra = null;
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        ParametrosASIUDAO parametrosASIUDAO = new ParametrosASIUDAOImpl();  
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        Notificacion notificacion = null;
        Persona persona = null;
        
        strFirma = "Administración de la SIU";
        
        this.strDestinatario = null;
        this.strAsunto = null;
        this.strMensaje = "";
        strNomResp = "";
        strEmailResp = "";
        strArea = "";
        strInsumo = "";
        strDescripcion = "";
        strNumId = "";
        strLote = "";
        strCantidad = "";
        strRango = "";
        strFechaVencimiento = "";
        strAccionNotificar = "";
        
        strCodigoNotificacion = "CONTROLINSUMOS";
        
        try{
            notificacion =notificacionDAO.obtenerUno(strCodigoNotificacion);
        }catch(GIDaoException gde){
            new GIDaoException("Se generó un error recuperando la información de la notificación con código " + strCodigoNotificacion, gde);            
            notificacion = null;
        }        
        
        if (notificacion != null){
            
            strArea = controlInsumo.getArea();
            strInsumo = controlInsumo.getInsumo();
            strDescripcion = controlInsumo.getDescripcion();
            strNumId = controlInsumo.getNumeroIdentificacion();
            strLote = controlInsumo.getLote();
            strCantidad = controlInsumo.getCantidad();
            strRango = controlInsumo.getRangoMedicion();
            strFechaVencimiento = funcionesComunesDAO.convertirFechaLarga(controlInsumo.getFechaVencimiento().toString());
            strAccionNotificar = controlInsumo.getAccionNotificar();
            strResponsable = controlInsumo.getResponsable();
            dtFechaCompra = controlInsumo.getFechaCompra();
            
            if (strFechaVencimiento != null){            
                if ((strResponsable != null) && (!strResponsable.equals(""))){
                    strTemp = strResponsable.split("\\(");

                   if (strTemp != null){

                       strResponsable = strTemp[1].replace(")", "").trim();                                  
                       strTemp2 = strResponsable.split("-");                    

                       if (strTemp2 != null){

                           if (strTemp2.length == 1){
                               strResponsable = strTemp2[0].trim();      
                           }else{
                                strResponsable = strTemp2[1].trim();              
                           }

                           persona = parametrosASIUDAO.obtenerPersonaXCargo(strResponsable);

                           if (persona != null){
                               strNomResp = persona.getNombreCompleto();
                               strEmailResp= persona.getCorreoInstitucional();
                           }else{                           
                               strNomResp = notificacion.getNombreDestinario();
                               strEmailResp = notificacion.getEmailDestinario();
                           }
                       }
                   }

                    if (this.strModoPdn.equals("N")){
                        this.strDestinatario = this.strEmailDllo;            
                    }else{
                        this.strDestinatario = strEmailResp;            
                    }

                    this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomResp + "</b>.<br /><br />";       

                    if (strAccionNotificar.equals("DIAVENC")){
                         this.strAsunto = "ALERTA: Finaliza fecha para la compra de insumo";
                         this.strMensaje += "La fecha para la compra del insumo  " + strInsumo + " del área " + strArea + " finaliza el día de hoy.<br /><br />";
                    }

                    if (strAccionNotificar.equals("AVENCER")){
                         this.strAsunto = "ALERTA: Fecha para la compra de insumo próxima a finalizar";
                         this.strMensaje += "La fecha para la compra del insumo  " + strInsumo + " del área " + strArea + " finalizará el próximo [aaaa-mm-dd] " + strFechaVencimiento + ".<br /><br />";
                    }               

                    this.strMensaje += "Atentamente,<br /><br />";
                    this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
                    this.strMensaje += "<b>NOTA</b>: Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";

                    this.parametroMail = new ParametroMail();
                    this.parametroMail.setDestinatario(this.strDestinatario);
                    this.parametroMail.setAsunto(this.strAsunto);
                    this.parametroMail.setMensaje(this.strMensaje);

                    sendMailHTML(this.parametroMail);
                    new GIDaoException("Notificación enviada correctamente a " + strNomResp + " al correo " + strEmailResp + " para el insumo " + strInsumo);               
               }
           }
        }        
    }    

    @Override
    public void notificarEquiposMnto(List<EquipoMnto> equipos, String strCodNotificacion) throws GIDaoException {
        
        String strFirma="", strFirmaGeneral, strMsgFinal, strNomDestinatario="", strEmailDestinatario="", strDescripcion="";
        Integer intTotalEquipos;        
                       
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        Notificacion notificacion =null;
        
        try{
            notificacion = notificacionDAO.obtenerUno(strCodNotificacion);
        }catch(GIDaoException gi){
            new GIDaoException("Se generó un obteniendo la configuración de la notificación", gi);
        }
        
        strFirmaGeneral = "<b>Coordinación Proceso Gestión Mantenimiento</b>";
        strFirma += strFirmaGeneral + "<br />";
        strFirma += "Administración SIU<br /><br />";       
        strMsgFinal = "<b>Nota:</b> Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";
        
        if (notificacion != null){
            
            this.strDestinatario  = null;
            this.strMensaje = "";
            this.parametroMail  = null;
            strDescripcion = "";
            
            if (notificacion.getCodigo().equals("PLANMNTOSIUEQCIENT")){
                strDescripcion = "Equipos Científicos";
            }
            
            if (notificacion.getCodigo().equals("PLANMNTOSIUEQCOMP")){
                strDescripcion = "Equipos de Cómputo";
            }
            
            if (notificacion.getCodigo().equals("PLANMNTOSIUEQINDUS")){
                strDescripcion = "Equipos Industriales";
            }
            
            if (notificacion.getCodigo().equals("PLANMNTOSIUINFRAES")){
                strDescripcion = "Infraestructura";
            }
            
            if (notificacion.getCodigo().equals("PLANMNTOSIUSOFTWARE")){
                strDescripcion = "Software";
            }
            
            if (notificacion.getCodigo().equals("PLANMNTOSIUTELCO")){
                strDescripcion = "Telecomunicaciones";
            }
            
            strNomDestinatario = notificacion.getNombreDestinario();   
            strEmailDestinatario = notificacion.getEmailDestinario();
            this.strAsunto = "ALERTA: Mantenimiento preventivo de " + strDescripcion + " próximo a cumplir su programación";                                    

            if (strEmailDestinatario != null && !strEmailDestinatario.equals("")){
                
                if (this.strModoPdn.equals("N")){
                    this.strDestinatario = this.strEmailDllo;            
                }else{
                    this.strDestinatario = strEmailDestinatario;            
                }                

                this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomDestinatario + "</b>.<br /><br />";
                this.strMensaje += "Los siguientes equipos/sistemas cumplen su programación de mantenimiento preventivo en el presente mes:<br /><br />";
                intTotalEquipos = 0;
                
                for(EquipoMnto equipo : equipos){                                                                            
               
                    this.strMensaje += "- <b>Tipo de equipo</b>: " + equipo.getTipoEquipo() + "<br />";
                    this.strMensaje += "- <b>Nombre del equipo</b>: " + equipo.getNombreEquipo() + "<br />";
                    this.strMensaje += "- <b>Servicio/grupo</b>: " + equipo.getServicio() + "<br />";
                    this.strMensaje += "- <b>Torre</b>: " + equipo.getTorre() + "<br />";                    
                    this.strMensaje += "- <b>Piso</b>: " + equipo.getPiso() + "<br /><br />";                    
                }
                
                intTotalEquipos = equipos.size();
                
                this.strMensaje += "Atentamente,<br /><br />";
                this.strMensaje += strFirma;           
                this.strMensaje += strMsgFinal;

                this.parametroMail = new ParametroMail();
                this.parametroMail.setDestinatario(this.strDestinatario);
                this.parametroMail.setAsunto(this.strAsunto);
                this.parametroMail.setMensaje(this.strMensaje);

                sendMailHTML(this.parametroMail);
                new GIDaoException("Notificación enviada a " + strNomDestinatario + " al correo " + strEmailDestinatario + " con " + intTotalEquipos.toString() + " equipos!");
            }else{
                new GIDaoException("El correo del destinatario ASIU es nulo!.");
            }        
        }else{
            new GIDaoException("El objeto de notificación es nulo!");
        }
    }

    @Override
    public void notificarVencimientoMntoPrtvoEquCi(List<MntoPrtvoEqCi> MntosPrtivos) throws GIDaoException {
        
            String strSolicitante, strSolicitanteAnterior, strEmail, strFechaProxMnto, strMsgInicial, strMsgFinal, strNomEquipo;
            
            Date dtFechaRecepcion = null;
            Integer intCont = 0;
            strSolicitante = "";
            strSolicitanteAnterior = "";
            strEmail = "";        
            strMsgFinal = "";
            strMsgInicial = "";
            strNomEquipo = "";
            String[] strTemp;
            
            FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
                                               
            strMsgFinal += "Atentamente,<br /><br />";
            strMsgFinal += "<b>Coordinación Gestión Mantenimiento</b><br /><br />";
            strMsgFinal += "<b>NOTA</b>: Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.";       
            
            this.strMensaje = "";
            this.strRutaArchivo = "";
            
            for (MntoPrtvoEqCi mntoPrtvoEqCi : MntosPrtivos){                                
                
                strSolicitante = mntoPrtvoEqCi.getNombreUsuario().trim();
                
                if (!(strSolicitante.equals(strSolicitanteAnterior)) && (intCont >0)){
                    
                    strMsgInicial = "Cordial saludo Señor(a) <b> " + strSolicitanteAnterior + "</b>.<br /><br />";             
                    strMsgInicial = strMsgInicial + "Como parte del servicio y compromiso con nuestros clientes, nuestro principal propósito es contribuir con el excelente desempeño de sus equipos, para su comodidad estamos remitiendo los siguientes equipos que cumplirán su vigencia de mantenimiento preventivo:<br /><br />";
                    
                    this.strMensaje = strMsgInicial + this.strMensaje;
                    this.strMensaje = this.strMensaje + strMsgFinal;
                    
                    this.strAsunto = "ALERTA: Vencimiento de mantenimiento preventivo de equipos";     
                    
                    if (this.strModoPdn.equals("N")){
                        this.strDestinatario = this.strEmailDllo;            
                    }else{
                        this.strDestinatario = strEmail;            
                    }
                                                              
                    if (!strEmail.equals("")){
                        strTemp = strEmail.split(";");

                        if (strTemp.length > 1){
                            strEmail = strTemp[0];
                            this.strDestinatario = strEmail;
                        }
                    }       
                                                          
                    notificarMsg(strSolicitante);                    
                    strMsgInicial = "";
                    
                }                                    
                          
                strEmail = mntoPrtvoEqCi.getEmail();   
                dtFechaRecepcion = mntoPrtvoEqCi.getFechaRecepcion();
                strNomEquipo = mntoPrtvoEqCi.getNombreEquipo();
                
                strFechaProxMnto = funcionesComunesDAO.convertirFechaLarga(dtFechaRecepcion.toString()); 
                this.strMensaje += " - Equipo: " + strNomEquipo + ", cumplimiento de vigencia: " + strFechaProxMnto + ".<br /><br />";
                                                    
                strSolicitanteAnterior = strSolicitante;
                intCont++;
            }        
            
            strMsgInicial = "Cordial saludo Señor(a) <b> " + strSolicitanteAnterior + "</b>.<br /><br />"; 
            strMsgInicial = strMsgInicial + "Los siguientes equipos cumplirán su vigencia de mantenimiento preventivo:<br /><br />";
            this.strMensaje = strMsgInicial + this.strMensaje;
            this.strMensaje = this.strMensaje + strMsgFinal;
            this.strAsunto = "ALERTA: Vencimiento de mantenimiento preventivo de equipos";     

            if (this.strModoPdn.equals("N")){
                this.strDestinatario = this.strEmailDllo;            
            }else{
                this.strDestinatario = strEmail;            
            }                       
                                                            
            if (!strEmail.equals("")){
                strTemp = strEmail.split(";");

                if (strTemp.length > 1){
                    strEmail = strTemp[0];    
                    this.strDestinatario = strEmail;
                }
            }                        
            
            notificarMsg(strSolicitante);            
            strMsgInicial = "";
    }

    @Override
    public void notificarVencimientoCartera(List<Cartera> carteras) throws GIDaoException {
        
        String strCodigoNotificacion, strNroFactura, strNroDiasCartera, strFirma, strNomDestinario;    
        Integer intTotalFacturas;
        Notificacion notificacion = null;
        strCodigoNotificacion = "NOTIFVENCCARTERA";
        strFirma = "Administración de la SIU";
                                            
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        
        try{
            notificacion =notificacionDAO.obtenerUno(strCodigoNotificacion);
        }catch(GIDaoException gde){
            new GIDaoException("Se generó un error recuperando la información de la notificación con código " + strCodigoNotificacion, gde);            
            notificacion = null;
        }        
        
        if (notificacion != null){

            this.strDestinatario = null;
            this.strAsunto = null;
            this.strMensaje = "";

            strNomDestinario = notificacion.getNombreDestinario();
            
            if (this.strModoPdn.equals("N")){
                this.strDestinatario = this.strEmailDllo;            
            }else{
                this.strDestinatario = notificacion.getEmailDestinario().trim();            
            }
                        
            this.strAsunto = "ALERTA: Facturas que requieren gestión de cobro";                                                          
            this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomDestinario + "</b>.<br /><br />";                  
            this.strMensaje += "Las siguientes facturas requieren gestión de cobro: <br /><br />";      
            
            intTotalFacturas = 0;
            
            for(Cartera cartera : carteras){            
                strNroFactura = cartera.getNroFactura();
                strNroDiasCartera = cartera.getNroDiasCartera().toString();      
                                
                this.strMensaje += "- Nro. de factura: " + strNroFactura + ", Nro de días: " + strNroDiasCartera + "<br />";
                strNroFactura = "";
                strNroDiasCartera = "";
                intTotalFacturas++;
            }
            
            this.strMensaje += "<br />";
            this.strMensaje += "Atentamente,<br /><br />";
            this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
            this.strMensaje += "<b>NOTA</b>: Éste es un mensaje enviado automáticamente. Por favor no dé respuesta al mismo.<br />";

            this.parametroMail = new ParametroMail();
            this.parametroMail.setDestinatario(this.strDestinatario);
            this.parametroMail.setAsunto(this.strAsunto);
            this.parametroMail.setMensaje(this.strMensaje);

            sendMailHTML(this.parametroMail);                  
            new GIDaoException("Notificación enviada correctamente a " + strNomDestinario + " al correo electrónico " + this.strDestinatario + " con " + intTotalFacturas.toString() + " facturas!.");
              
        }
    }
}
