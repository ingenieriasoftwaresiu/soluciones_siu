/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.FuncionesComunesDAO;
import co.edu.udea.solucionessiu.dao.NotificacionDAO;
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
import co.edu.udea.solucionessiu.dto.PersonaSIGEP;
import co.edu.udea.solucionessiu.dto.RegistroPlanCalibracion;
import co.edu.udea.solucionessiu.dto.RegistroPlanMejoramiento;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.Date;
import java.util.List;
import co.edu.udea.solucionessiu.dao.NotificacionMailSiuWebDAO;

/**
 *
 * @author jorge.correa
 */
public class NotificacionMailSiuWebDAOImpl extends EnvioMailDAOimpl implements NotificacionMailSiuWebDAO{
    private ParametroMail parametroMail;
    private String strDestinatario;
    private String strAsunto;
    private String strMensaje;
    private String strRutaArchivo;
    ParametroGeneral parametroGeneral;
    private ParametroGeneralDAO parametroGeneralDAO;
    private String strModoPdn;
    private String  strEmailDllo;   
    
    public NotificacionMailSiuWebDAOImpl(){
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
            new GIDaoException("Se gener?? un error recuperando los parametros generales", e);
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
            notificacion =notificacionDAO.obtenerUnoSiuWeb(strCodigoNotificacion);
        }catch(GIDaoException gde){
            new GIDaoException("Se gener?? un error recuperando la informaci??n de la notificaci??n con c??digo " + strCodigoNotificacion, gde);            
            notificacion = null;
        }        
        
        if (notificacion != null){            

            strNombreDestinatario = notificacion.getNombreDestinatario().trim();         
            strEmailDestinatario = notificacion.getEmailDestinatario().trim();
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
                this.strAsunto = "ALERTA: El pedido #" + strNumPedido.trim() + " asociado al grupo " + strGrupo.trim() + " est?? pr??ximo a cumplir la fecha acordada de entrega";
            }
        
            this.strMensaje += "Cordial saludo Se??ores " + strNombreProveedor + ".<br /><br />";
            this.strMensaje += "Los datos asociados con el pedido #" + strNumPedido.trim() + " son:<br /><br />";
            this.strMensaje += "- <b>Nombre del grupo:</b> " + strGrupo.trim() + ".<br />";
            //this.strMensaje += "- <b>Nombre del proveedor:</b> " + strNombreProveedor + ".<br />";
            this.strMensaje += "- <b>Fecha de env??o al proveedor [aaaa-mm-dd]:</b> " + strFechaEnvioProveedor + ".<br />";
            this.strMensaje += "- <b>Fecha acordada de entrega [aaaa-mm-dd]:</b> " + strFechaAcordada + ".<br /><br />";
            this.strMensaje += "Atentamente,<br /><br />";
            this.strMensaje += "Administraci??n de la SIU<br /><br />";
            this.strMensaje += "<b>NOTA</b>: ??ste es un mensaje enviado autom??ticamente. Por favor no d?? respuesta al mismo.";
                          
            this.parametroMail = new ParametroMail();
            this.parametroMail.setDestinatario(this.strDestinatario);
            this.parametroMail.setAsunto(this.strAsunto);
            this.parametroMail.setMensaje(this.strMensaje);

            sendMailHTML(this.parametroMail);
            new GIDaoException("Notificaci??n enviada correctamente a " + strNombreDestinatario + " al correo " + strEmailDestinatario);
        }                
    }

    @Override
    public void notificarVencimientoDocumentos(Documento documento) throws GIDaoException {
        
        String strNombreDestinatario=null, strEmailDestinatario=null, strCodigo=null, strIdCoordinacion=null, strIdDestinatario=null, strTipo=null, strNomDocumento=null, strProceso=null, strTipoDocumento=null;
        String strVigencia=null, strComplementoMsg=null, strComplementoAsunto=null;
        String[] strTemp= null;
        Coordinacion coordinacion = null;
        PersonaSIGEP persona = null;
        
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
                        strComplementoAsunto = " pr??ximo(a) a perder su vigencia";
                        strComplementoMsg = " se encuentra pr??ximo(a) a perder su vigencia";
                    }

                    if (strVigencia.equals("NEU")){
                        strComplementoAsunto = " pierde su vigencia el d??a de hoy";
                        strComplementoMsg = " pierde su vigencia el d??a de hoy";
                    }

                    if (strVigencia.equals("VEN")){
                        strComplementoAsunto = " super?? el tiempo recomendado para su revisi??n";
                        strComplementoMsg = " super?? el tiempo recomendado para su revisi??n";
                    }

                    this.strMensaje += "Cordial saludo Se??or(a). <b>" + strNombreDestinatario + "</b>.<br /><br />";

                    if (strTipo.equals("FTS")){
                        this.strAsunto = "ALERTA: Ficha t??cnica de servicio " + strNomDocumento + strComplementoAsunto;
                        this.strMensaje += "La ficha t??cnica del servicio <b>" + strNomDocumento + " (" + strCodigo +")</b> asociada al proceso <b>" + strProceso + "</b>" + strComplementoMsg + ".<br /><br />";
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
                        this.strAsunto = "ALERTA: Ficha t??cnica de indicador " + strNomDocumento + strComplementoAsunto;
                        this.strMensaje += "La ficha t??cnica del indicador <b>" + strNomDocumento + " (" + strCodigo +")</b> asociada al proceso <b>" + strProceso + "</b>" + strComplementoMsg + ".<br /><br />";
                    }

                    this.strMensaje += "Por favor gestione oportunamente su actualizaci??n para garantizar la vigencia del documento.<br /><br />";
                    this.strMensaje += "Atentamente,<br /><br />";
                    this.strMensaje += "<b>Administraci??n de la SIU</b><br /><br />";
                    this.strMensaje += "<b>NOTA</b>: ??ste es un mensaje enviado autom??ticamente. Por favor no d?? respuesta al mismo.";

                    this.parametroMail = new ParametroMail();
                    this.parametroMail.setDestinatario(this.strDestinatario);
                    this.parametroMail.setAsunto(this.strAsunto);
                    this.parametroMail.setMensaje(this.strMensaje);

                    sendMailHTML(this.parametroMail);
                    new GIDaoException("Notificaci??n enviada correctamente a " + strNombreDestinatario + " al correo " + strEmailDestinatario + " para el documento " + strNomDocumento + " (" + strCodigo +").");
                }

            }else{
                new GIDaoException("No se pudo recuperar la informaci??n de la coordinacion con c??digo " + strIdCoordinacion);
            }

        }else{
            new GIDaoException("No se pudo descomponer el c??digo del documento");
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
        PersonaSIGEP persona = null;
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
            strMsgComplementarioCorreccion = "Por favor gestione su implementaci??n, en la medida de lo posible, para garantizar su eficacia.<br /><br />";
            strMsgComplementarioAccion = "Por favor gestione su implementaci??n, en la medida de lo posible, para garantizar su eficacia.<br /><br />";
            strMsgComplementarioEficacia = "Por favor gestione su revisi??n, en la medida de lo posible, para verificar o no la eficacia de la acci??n.<br /><br />";
            strFirma = "Administraci??n de la SIU";

            try{
                notificacion =notificacionDAO.obtenerUnoSiuWeb(strCodigoNotificacion);
            }catch(GIDaoException gde){
                new GIDaoException("Se gener?? un error recuperando la informaci??n de la notificaci??n con c??digo " + strCodigoNotificacion, gde);            
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
                                
                                // No encontr?? el responsable por el cargo o no se encuentra activo en la ASIU. Se busca el jefe inmediato para notificarlo.
                                
                                strIdJefeInmediato = parametrosASIUDAO.obtenerJefeInmediatoXCargoEmpleado(strResponsableCorrecion);                      
                                persona = parametrosASIUDAO.obtenerPersonaXId(strIdJefeInmediato);
                                
                                if (persona != null){
                                    strNomRespCorreccion = persona.getNombreCompleto();
                                    strEmailRespCorreccion = persona.getCorreoInstitucional();
                                }else{
                                    
                                    // No encontr?? el jefe inmediato del responsable o se trata de un responsable general: Comit?? de Calidad, Coordinadores ASIU. Notifica al responsable por defecto.
                                    
                                    strNomRespCorreccion = notificacion.getNombreDestinatario().trim();
                                    strEmailRespCorreccion = notificacion.getEmailDestinatario().trim();
                                }                                                                
                            }                            

                            if (this.strModoPdn.equals("N")){
                                this.strDestinatario = this.strEmailDllo;            
                            }else{
                                this.strDestinatario = strEmailRespCorreccion;            
                            }
                            
                            this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomRespCorreccion + "</b>.<br /><br />";

                            if (strAccionNotificarCorreccion.equals("CORRECCCIONDIAVENC")){
                                this.strAsunto = "ALERTA: Correcci??n con c??digo #" + strCodigoRegistro + " cumple plazo establecido";                                
                                this.strMensaje += "La correcci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, cumple hoy el plazo establecido para su implementaci??n y a??n se encuentra Abierta.<br /><br />";
                            }

                             if (strAccionNotificarCorreccion.equals("CORRECCCIONAVENCER")){
                                this.strAsunto = "ALERTA: Correcci??n con c??digo #" + strCodigoRegistro + " pr??xima a cumplir plazo establecido";
                                this.strMensaje += "La correcci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, est?? pr??xima a cumplir el plazo establecido para su implementaci??n y a??n se encuentra Abierta.<br /><br />";
                            }

                             if (strAccionNotificarCorreccion.equals("CORRECCCIONVENCIDA")){
                                this.strAsunto = "ALERTA: Correcci??n con c??digo " + strCodigoRegistro + " cumpli?? plazo establecido";
                                this.strMensaje += "La correcci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, cumpli?? el plazo establecido para su implementaci??n y a??n se encuentra Abierta.<br /><br />";
                            }

                            this.strMensaje += strMsgComplementarioCorreccion;

                            this.strMensaje += "Atentamente,<br /><br />";
                            this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
                            this.strMensaje += "<b>NOTA</b>: ??ste es un mensaje enviado autom??ticamente. Por favor no d?? respuesta al mismo.";

                            this.parametroMail = new ParametroMail();
                            this.parametroMail.setDestinatario(this.strDestinatario);
                            this.parametroMail.setAsunto(this.strAsunto);
                            this.parametroMail.setMensaje(this.strMensaje);

                            sendMailHTML(this.parametroMail);
                            new GIDaoException("Notificaci??n de correcci??n enviada correctamente a " + strNomRespCorreccion + " al correo " + strEmailRespCorreccion + " para la correci??n con c??digo #" + strCodigoRegistro);

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
                                
                                // No encontr?? el responsable por el cargo o no se encuentra activo en la ASIU. Se busca el jefe inmediato para notificarlo.
                                                                                        
                                strIdJefeInmediato = parametrosASIUDAO.obtenerJefeInmediatoXCargoEmpleado(strResponsableAccion);          
                                persona = parametrosASIUDAO.obtenerPersonaXId(strIdJefeInmediato);
                                
                                if (persona != null){        
                                    strNomRespAccion = persona.getNombreCompleto();
                                    strEmailRespAccion = persona.getCorreoInstitucional();
                                }else{                          
                                    
                                    // No encontr?? el jefe inmediato del responsable o se trata de un responsable general: Comit?? de Calidad, Coordinadores ASIU. Notifica al responsable por defecto.
                                    
                                    strNomRespAccion = notificacion.getNombreDestinatario().trim();
                                    strEmailRespAccion = notificacion.getEmailDestinatario().trim();
                                }                                
                            }

                           if (this.strModoPdn.equals("N")){
                                this.strDestinatario = this.strEmailDllo;            
                            }else{
                                this.strDestinatario = strEmailRespAccion;            
                            }
                                               
                            this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomRespAccion + "</b>.<br /><br />";

                            if (registro.getEficacia() == null){            

                                strEventoNotificado = "acci??n";

                                if (strAccionNotificarAccion.equals("ACCIONDIAVENC")){
                                    this.strAsunto = "ALERTA: La acci??n con c??digo #" + strCodigoRegistro + " cumple plazo establecido";                                
                                    this.strMensaje += "La acci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, cumple hoy el plazo establecido para su implementaci??n y a??n se encuentra Abierta.<br /><br />";
                                }

                                 if (strAccionNotificarAccion.equals("ACCIONAVENCER")){
                                    this.strAsunto = "ALERTA: La acci??n con c??digo #" + strCodigoRegistro + " pr??xima a cumplir plazo establecido";
                                    this.strMensaje += "La acci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, est?? pr??xima a cumplir el plazo establecido para su implementaci??n y a??n se encuentra Abierta.<br /><br />";
                                }

                                 if (strAccionNotificarAccion.equals("ACCIONVENCIDA")){
                                    this.strAsunto = "ALERTA: La acci??n con c??digo #" + strCodigoRegistro + " cumpli?? plazo establecido";
                                    this.strMensaje += "La acci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, cumpli?? el plazo establecido para su implementaci??n y a??n se encuentra Abierta.<br /><br />";
                                }

                                 this.strMensaje += strMsgComplementarioAccion;
                            }else{
                                eficacia = registro.getEficacia();
                                strAccionNotificarEficacia = eficacia.getAccionNotificar();
                                strEventoNotificado = "eficacia";

                                if (strAccionNotificarEficacia.equals("EFICACIADIAVENC")){     
                                    this.strAsunto = "ALERTA: Eficacia de la acci??n con c??digo #" + strCodigoRegistro + " cumple plazo establecido";                                
                                    this.strMensaje += "La eficacia de la acci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, cumple hoy el plazo establecido para su revisi??n y a??n se encuentra Pendiente.<br /><br />";
                                }

                                 if (strAccionNotificarEficacia.equals("EFICACIAAVENCER")){
                                    this.strAsunto = "ALERTA: Eficacia de la acci??n con c??digo #" + strCodigoRegistro + " pr??xima a cumplir plazo establecido";
                                    this.strMensaje += "La eficacia de la acci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, est?? pr??xima a cumplir el plazo establecido para su revisi??n y a??n se encuentra Pendiente.<br /><br />";
                                }

                                 if (strAccionNotificarEficacia.equals("EFICACIAVENCIDA")){
                                     this.strAsunto = "ALERTA: Eficacia de la acci??n con c??digo #" + strCodigoRegistro + " cumpli?? plazo establecido";
                                    this.strMensaje += "La eficacia de la acci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento ASIU, cumpli?? el plazo establecido para su revisi??n y a??n se encuentra Pendiente.<br /><br />";
                                }

                                 this.strMensaje += strMsgComplementarioEficacia;
                            }

                            this.strMensaje += "Atentamente,<br /><br />";
                            this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
                            this.strMensaje += "<b>NOTA</b>: ??ste es un mensaje enviado autom??ticamente. Por favor no d?? respuesta al mismo.";

                            this.parametroMail = new ParametroMail();
                            this.parametroMail.setDestinatario(this.strDestinatario);
                            this.parametroMail.setAsunto(this.strAsunto);
                            this.parametroMail.setMensaje(this.strMensaje);

                            sendMailHTML(this.parametroMail);
                            new GIDaoException("Notificaci??n enviada correctamente a " + strNomRespAccion + " al correo " + strEmailRespAccion + " para la " + strEventoNotificado + " con c??digo #" + strCodigoRegistro);                            

                        }else{
                            new GIDaoException("ACCI??N: No se pudo dividir el responsable 2.");
                        }
                    }else{
                        new GIDaoException("ACCI??N: No se pudo dividir el responsable 1.");
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
        PersonaSIGEP persona = null;
        
        this.strDestinatario = null;
        this.strAsunto = null;
        this.strMensaje = "";
        
        strCodigoNotificacion = "PLANCALIBEQASIU";
        
        if (registro != null){ 
            strAreaLab = registro.getAreaLab();
            strEquipo = registro.getEquipo();
            strMarca = registro.getMarca();
            strModelo = registro.getModelo();
            
            strMsgComplementario = "Por favor gestione su ejecuci??n, en la medida de lo posible, para garantizar el buen funcionamiento del equipo.<br /><br />";
            strFirma = "Administraci??n de la SIU";

            try{
                notificacion =notificacionDAO.obtenerUnoSiuWeb(strCodigoNotificacion);
            }catch(GIDaoException gde){
                new GIDaoException("Se gener?? un error recuperando la informaci??n de la notificaci??n con c??digo " + strCodigoNotificacion, gde);            
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
                                strNomRespCalibracion = notificacion.getNombreDestinatario().trim();
                                strEmailRespCalibracion = notificacion.getEmailDestinatario().trim();
                            }
                                                        
                            if (this.strModoPdn.equals("N")){
                                this.strDestinatario = this.strEmailDllo;            
                            }else{
                                this.strDestinatario = strEmailRespCalibracion;            
                            }
                           
                            this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomRespCalibracion + "</b>.<br /><br />";

                            if (strAccionNotificarCalibracion.equals("CALIBRACIONDIAVENC")){
                                this.strAsunto = "ALERTA: Calibraci??n del equipo " + strEquipo + " cumple plazo establecido";                                
                                this.strMensaje += "La calibraci??n del equipo <b>" + strEquipo + "</b> registrada en el Plan de Mantenimiento y Calibraci??n de Equipos ASIU, cumple hoy el plazo establecido para su ejecuci??n y a??n se encuentra Pendiente.<br /><br />";
                            }

                             if (strAccionNotificarCalibracion.equals("CALIBRACIONAVENCER")){
                                this.strAsunto = "ALERTA: Calibraci??n del equipo " + strEquipo + " pr??xima a cumplir plazo establecido";
                                this.strMensaje += "La calibraci??n del equipo <b>" + strEquipo + "</b> registrada en el Plan de Mantenimiento y Calibraci??n de Equipos ASIU, est?? pr??xima a cumplir el plazo establecido para su ejecuci??n y a??n se encuentra Pendiente.<br /><br />";
                            }

                             if (strAccionNotificarCalibracion.equals("CALIBRACIONVENCIDA")){
                                this.strAsunto = "ALERTA: Calibraci??n del equipo " + strEquipo + " cumpli?? plazo establecido";
                                this.strMensaje += "La calibraci??n del equipo <b>" + strEquipo + "</b> registrada en el Plan de Mantenimiento y Calibraci??n de Equipos ASIU, cumpli?? el plazo establecido para su ejecuci??n y a??n se encuentra Pendiente.<br /><br />";
                            }

                            this.strMensaje += strMsgComplementario;

                            this.strMensaje += "Atentamente,<br /><br />";
                            this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
                            this.strMensaje += "<b>NOTA</b>: ??ste es un mensaje enviado autom??ticamente. Por favor no d?? respuesta al mismo.";
                            
                            this.parametroMail = new ParametroMail();
                            this.parametroMail.setDestinatario(this.strDestinatario);
                            this.parametroMail.setAsunto(this.strAsunto);
                            this.parametroMail.setMensaje(this.strMensaje);

                            sendMailHTML(this.parametroMail);
                            new GIDaoException("Notificaci??n de calibraci??n enviada correctamente a " + strNomRespCalibracion + " al correo " + strEmailRespCalibracion + " para el equipo " + strEquipo);                           
                        
                            // Env??o de copias del mensaje
                            
                            this.strDestinatario = "luis.gonzalez13@udea.edu.co";
                            this.parametroMail.setDestinatario(this.strDestinatario);
                            sendMailHTML(this.parametroMail);
                            new GIDaoException("Notificaci??n de calibraci??n enviada correctamente a Luis Gonz??lez al correo " + this.strDestinatario + " para el equipo " + strEquipo);
                            
                            this.strDestinatario = "yenny.lezcano@udea.edu.co";
                            this.parametroMail.setDestinatario(this.strDestinatario);
                            sendMailHTML(this.parametroMail);                     
                            new GIDaoException("Notificaci??n de calibraci??n enviada correctamente a Yenny Lezcano al correo " + this.strDestinatario + " para el equipo " + strEquipo);
                        
                        }else{
                            new GIDaoException("CALIBRACI??N: No se pudo dividir el responsable 2.");
                        }
                    }else{
                        new GIDaoException("CALIBRACI??N: No se pudo dividir el responsable 1.");
                }      
            }                                               
        }
    }

    @Override
    public void notificarVencimientoContrato(Contrato contrato) throws GIDaoException {
        
        String strCodigoNotificacion, strTipoContrato, strFirma, strNomDestinatario, strCodigoContrato, strAccionNotificar, strFechaFinalizacion, strGrupo, strContratista;     
        Notificacion notificacion = null;
        strCodigoNotificacion = "";
        strFechaFinalizacion = null;
        strAccionNotificar = null;
        strGrupo = null;
        strContratista = null;
                
        strTipoContrato = contrato.getTipoContrato();
        strFirma = "Administraci??n de la SIU";
        
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
            notificacion =notificacionDAO.obtenerUnoSiuWeb(strCodigoNotificacion);
        }catch(GIDaoException gde){
            new GIDaoException("Se gener?? un error recuperando la informaci??n de la notificaci??n con c??digo " + strCodigoNotificacion, gde);            
            notificacion = null;
        }        
        
        if (notificacion != null){

            this.strDestinatario = null;
            this.strAsunto = null;
            this.strMensaje = "";

            if (this.strModoPdn.equals("N")){
                this.strDestinatario = this.strEmailDllo;            
            }else{
                this.strDestinatario = notificacion.getEmailDestinatario().trim();            
            }
            
            strNomDestinatario = notificacion.getNombreDestinatario();
            strCodigoContrato = contrato.getCodigoContrato();
            strFechaFinalizacion = contrato.getFechaTerminacion();
            strAccionNotificar = contrato.getAccionNotificar();
            strGrupo = contrato.getGrupo();
            strContratista = contrato.getContratista();
            
            if (strCodigoContrato.equals("")){
                strCodigoContrato = "[Sin c??digo]";
            }
                                                          
            this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomDestinatario + "</b>.<br /><br />";       
            
             if ((strTipoContrato.equals("NALANT")) || (strTipoContrato.equals("NALACT"))){                 
                if (strAccionNotificar.equals("DIAVENC")){
                     this.strAsunto = "ALERTA: Contrato nacional ha finalizado";
                     this.strMensaje += "El contrato nacional con c??digo " + strCodigoContrato + " ha finalizado el " + strFechaFinalizacion + ".<br /><br />";
                }
                
                if (strAccionNotificar.equals("AVENCER")){
                     this.strAsunto = "ALERTA: Contrato nacional pr??ximo a finalizar";
                     this.strMensaje += "El contrato nacional con c??digo " + strCodigoContrato + " finalizar?? el pr??ximo " + strFechaFinalizacion + ".<br /><br />";
                }               
            }
            
            if (strTipoContrato.equals("INTERNAL")){                 
                if (strAccionNotificar.equals("DIAVENC")){
                     this.strAsunto = "ALERTA: Contrato internacional ha finalizado";
                     this.strMensaje += "El contrato internacional con c??digo " + strCodigoContrato + " ha finalizado el  " + strFechaFinalizacion+ ".<br /><br />";
                }
                
                if (strAccionNotificar.equals("AVENCER")){
                     this.strAsunto = "ALERTA: Contrato internacional pr??ximo a finalizar";
                     this.strMensaje += "El contrato internacional con c??digo " + strCodigoContrato + " finalizar?? el pr??ximo " + strFechaFinalizacion + ".<br /><br />";
                }               
            }
            
             if (strTipoContrato.equals("PS")){                 
                if (strAccionNotificar.equals("DIAVENC")){
                     this.strAsunto = "ALERTA: Contrato de Prestaci??n de Servicios ha finalizado";
                     this.strMensaje += "El contrato de prestaci??n de servicios con c??digo " + strCodigoContrato + " para el contratista " + strContratista + " del grupo " + strGrupo + " ha finalizado el  " + strFechaFinalizacion+ ".<br /><br />";
                }
                
                if (strAccionNotificar.equals("AVENCER")){
                     this.strAsunto = "ALERTA: Contrato de Prestaci??n de Servicios pr??ximo a finalizar";
                     this.strMensaje += "El contrato de prestaci??n de servicios con c??digo " + strCodigoContrato + " para el contratista " + strContratista + " del grupo " + strGrupo + " finalizar?? el pr??ximo " + strFechaFinalizacion + ".<br /><br />";
                }               
            }
                        
            this.strMensaje += "Atentamente,<br /><br />";
            this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
            this.strMensaje += "<b>NOTA</b>: ??ste es un mensaje enviado autom??ticamente. Por favor no d?? respuesta al mismo.";

            this.parametroMail = new ParametroMail();
            this.parametroMail.setDestinatario(this.strDestinatario);
            this.parametroMail.setAsunto(this.strAsunto);
            this.parametroMail.setMensaje(this.strMensaje);

            sendMailHTML(this.parametroMail);
            
            if ((strTipoContrato.equals("NALANT")) || (strTipoContrato.equals("NALACT"))){
                new GIDaoException("Notificaci??n enviada correctamente a " + strNomDestinatario + " al correo electr??nico " + this.strDestinatario + " para el contrato nacional con c??digo " + strCodigoContrato);
            }   
            
             if (strTipoContrato.equals("INTERNAL")){                 
                new GIDaoException("Notificaci??n enviada correctamente a " + strNomDestinatario + " al correo electr??nico " + this.strDestinatario + " para el contrato internacional con c??digo " + strCodigoContrato);
            }   
             
             if (strTipoContrato.equals("PS")){                 
                new GIDaoException("Notificaci??n enviada correctamente a " + strNomDestinatario + " al correo electr??nico " + this.strDestinatario + " para el contrato de prestaci??n de servicios con c??digo " + strCodigoContrato);
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
        PersonaSIGEP persona = null;
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
            strMsgComplementarioCorreccion = "Por favor gestione su implementaci??n, en la medida de lo posible, para garantizar su eficacia.<br /><br />";
            strMsgComplementarioAccion = "Por favor gestione su implementaci??n, en la medida de lo posible, para garantizar su eficacia.<br /><br />";
            strMsgComplementarioEficacia = "Por favor gestione su revisi??n, en la medida de lo posible, para verificar o no la eficacia de la acci??n.<br /><br />";
            strFirma = "Administraci??n de la SIU";

            try{
                notificacion =notificacionDAO.obtenerUnoSiuWeb(strCodigoNotificacion);
            }catch(GIDaoException gde){
                new GIDaoException("Se gener?? un error recuperando la informaci??n de la notificaci??n con c??digo " + strCodigoNotificacion, gde);            
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
                                
                                // No encontr?? el responsable por el cargo o no se encuentra activo en la ASIU. Se busca el jefe inmediato para notificarlo.
                                
                                strIdJefeInmediato = parametrosASIUDAO.obtenerJefeInmediatoXCargoEmpleado(strResponsableCorrecion);                      
                                persona = parametrosASIUDAO.obtenerPersonaXId(strIdJefeInmediato);
                                
                                if (persona != null){
                                    strNomRespCorreccion = persona.getNombreCompleto();
                                    strEmailRespCorreccion = persona.getCorreoInstitucional();
                                }else{
                                    
                                    // No encontr?? el jefe inmediato del responsable o se trata de un responsable general: Comit?? de Calidad, Coordinadores ASIU. Notifica al responsable por defecto.
                                    
                                    strNomRespCorreccion = notificacion.getNombreDestinatario().trim();
                                    strEmailRespCorreccion = notificacion.getEmailDestinatario().trim();
                                }                                                                
                            }                            

                            if (this.strModoPdn.equals("N")){
                                this.strDestinatario = this.strEmailDllo;            
                            }else{
                                this.strDestinatario = strEmailRespCorreccion;            
                            }
                            
                            this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomRespCorreccion + "</b>.<br /><br />";

                            if (strAccionNotificarCorreccion.equals("CORRECCCIONDIAVENC")){
                                this.strAsunto = "ALERTA: Correcci??n con c??digo #" + strCodigoRegistro + " cumple plazo establecido";                                
                                this.strMensaje += "La correcci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, cumple hoy el plazo establecido para su implementaci??n y a??n se encuentra Abierta.<br /><br />";
                            }

                             if (strAccionNotificarCorreccion.equals("CORRECCCIONAVENCER")){
                                this.strAsunto = "ALERTA: Correcci??n con c??digo #" + strCodigoRegistro + " pr??xima a cumplir plazo establecido";
                                this.strMensaje += "La correcci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, est?? pr??xima a cumplir el plazo establecido para su implementaci??n y a??n se encuentra Abierta.<br /><br />";
                            }

                             if (strAccionNotificarCorreccion.equals("CORRECCCIONVENCIDA")){
                                this.strAsunto = "ALERTA: Correcci??n con c??digo " + strCodigoRegistro + " cumpli?? plazo establecido";
                                this.strMensaje += "La correcci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, cumpli?? el plazo establecido para su implementaci??n y a??n se encuentra Abierta.<br /><br />";
                            }

                            this.strMensaje += strMsgComplementarioCorreccion;

                            this.strMensaje += "Atentamente,<br /><br />";
                            this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
                            this.strMensaje += "<b>NOTA</b>: ??ste es un mensaje enviado autom??ticamente. Por favor no d?? respuesta al mismo.";

                            this.parametroMail = new ParametroMail();
                            this.parametroMail.setDestinatario(this.strDestinatario);
                            this.parametroMail.setAsunto(this.strAsunto);
                            this.parametroMail.setMensaje(this.strMensaje);

                            sendMailHTML(this.parametroMail);
                            new GIDaoException("Notificaci??n de correcci??n enviada correctamente a " + strNomRespCorreccion + " al correo " + strEmailRespCorreccion + " para la correci??n con c??digo #" + strCodigoRegistro);

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
                                
                                // No encontr?? el responsable por el cargo o no se encuentra activo en la ASIU. Se busca el jefe inmediato para notificarlo.
                                                                                        
                                strIdJefeInmediato = parametrosASIUDAO.obtenerJefeInmediatoXCargoEmpleado(strResponsableAccion);          
                                persona = parametrosASIUDAO.obtenerPersonaXId(strIdJefeInmediato);
                                
                                if (persona != null){        
                                    strNomRespAccion = persona.getNombreCompleto();
                                    strEmailRespAccion = persona.getCorreoInstitucional();
                                }else{                          
                                    
                                    // No encontr?? el jefe inmediato del responsable o se trata de un responsable general: Comit?? de Calidad, Coordinadores ASIU. Notifica al responsable por defecto.
                                    
                                    strNomRespAccion = notificacion.getNombreDestinatario().trim();
                                    strEmailRespAccion = notificacion.getEmailDestinatario().trim();
                                }                                
                            }

                           if (this.strModoPdn.equals("N")){
                                this.strDestinatario = this.strEmailDllo;            
                            }else{
                                this.strDestinatario = strEmailRespAccion;            
                            }
                                               
                            this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomRespAccion + "</b>.<br /><br />";

                            if (registro.getEficacia() == null){            

                                strEventoNotificado = "acci??n";

                                if (strAccionNotificarAccion.equals("ACCIONDIAVENC")){
                                    this.strAsunto = "ALERTA: La acci??n con c??digo #" + strCodigoRegistro + " cumple plazo establecido";                                
                                    this.strMensaje += "La acci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, cumple hoy el plazo establecido para su implementaci??n y a??n se encuentra Abierta.<br /><br />";
                                }

                                 if (strAccionNotificarAccion.equals("ACCIONAVENCER")){
                                    this.strAsunto = "ALERTA: La acci??n con c??digo #" + strCodigoRegistro + " pr??xima a cumplir plazo establecido";
                                    this.strMensaje += "La acci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, est?? pr??xima a cumplir el plazo establecido para su implementaci??n y a??n se encuentra Abierta.<br /><br />";
                                }

                                 if (strAccionNotificarAccion.equals("ACCIONVENCIDA")){
                                    this.strAsunto = "ALERTA: La acci??n con c??digo #" + strCodigoRegistro + " cumpli?? plazo establecido";
                                    this.strMensaje += "La acci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, cumpli?? el plazo establecido para su implementaci??n y a??n se encuentra Abierta.<br /><br />";
                                }

                                 this.strMensaje += strMsgComplementarioAccion;
                            }else{
                                eficacia = registro.getEficacia();
                                strAccionNotificarEficacia = eficacia.getAccionNotificar();
                                strEventoNotificado = "eficacia";

                                if (strAccionNotificarEficacia.equals("EFICACIADIAVENC")){     
                                    this.strAsunto = "ALERTA: Eficacia de la acci??n con c??digo #" + strCodigoRegistro + " cumple plazo establecido";                                
                                    this.strMensaje += "La eficacia de la acci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, cumple hoy el plazo establecido para su revisi??n y a??n se encuentra Pendiente.<br /><br />";
                                }

                                 if (strAccionNotificarEficacia.equals("EFICACIAAVENCER")){
                                    this.strAsunto = "ALERTA: Eficacia de la acci??n con c??digo #" + strCodigoRegistro + " pr??xima a cumplir plazo establecido";
                                    this.strMensaje += "La eficacia de la acci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, est?? pr??xima a cumplir el plazo establecido para su revisi??n y a??n se encuentra Pendiente.<br /><br />";
                                }

                                 if (strAccionNotificarEficacia.equals("EFICACIAVENCIDA")){
                                     this.strAsunto = "ALERTA: Eficacia de la acci??n con c??digo #" + strCodigoRegistro + " cumpli?? plazo establecido";
                                    this.strMensaje += "La eficacia de la acci??n con c??digo #" + strCodigoRegistro + " registrada en el Plan de Mejoramiento LEC, cumpli?? el plazo establecido para su revisi??n y a??n se encuentra Pendiente.<br /><br />";
                                }

                                 this.strMensaje += strMsgComplementarioEficacia;
                            }

                            this.strMensaje += "Atentamente,<br /><br />";
                            this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
                            this.strMensaje += "<b>NOTA</b>: ??ste es un mensaje enviado autom??ticamente. Por favor no d?? respuesta al mismo.";

                            this.parametroMail = new ParametroMail();
                            this.parametroMail.setDestinatario(this.strDestinatario);
                            this.parametroMail.setAsunto(this.strAsunto);
                            this.parametroMail.setMensaje(this.strMensaje);

                            sendMailHTML(this.parametroMail);
                            new GIDaoException("Notificaci??n enviada correctamente a " + strNomRespAccion + " al correo " + strEmailRespAccion + " para la " + strEventoNotificado + " con c??digo #" + strCodigoRegistro);                            

                        }else{
                            new GIDaoException("ACCI??N: No se pudo dividir el responsable 2.");
                        }
                    }else{
                        new GIDaoException("ACCI??N: No se pudo dividir el responsable 1.");
                    }   
                }                     
            }    
        }
    }

    @Override
    public void notificarVencimientoAnticipoViaticoTiquete(AnticipoViaticoTiquete anticipoViaticoTiquete) throws GIDaoException {
        
        String strNombreDestinatario=null, strEmailDestinatario=null, strSolicitante, strTipoSolicitud, strNroComprobante, strAccionNotificar, strValorLegalizado, strFechaLimiteEntrega;
        String strCodigoNotificacion=null, strLugarComision, strFechaInicioComision, strNroTicket, strGrupo;
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        Notificacion notificacion = null;
                
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        
        strCodigoNotificacion = anticipoViaticoTiquete.getCodigoNotificacion().trim();     
        strSolicitante = "";
        strTipoSolicitud = "";
        strNroComprobante = "";
        strAccionNotificar = "";
        strValorLegalizado = "";
        strFechaLimiteEntrega = "";
        strLugarComision = "";
        strFechaInicioComision = "";
        strNroTicket = "";
        strGrupo = "";
        
        try{
            notificacion =notificacionDAO.obtenerUnoSiuWeb(strCodigoNotificacion);
        }catch(GIDaoException gde){
            new GIDaoException("Se gener?? un error recuperando la informaci??n de la notificaci??n con c??digo " + strCodigoNotificacion, gde);            
            notificacion = null;
        }        
        
        if (notificacion != null){            

            strNombreDestinatario = notificacion.getNombreDestinatario().trim();         
            strEmailDestinatario = notificacion.getEmailDestinatario().trim();            
            strSolicitante = anticipoViaticoTiquete.getSolicitante();
            strTipoSolicitud = anticipoViaticoTiquete.getTipoSolicitud();
            strNroComprobante = anticipoViaticoTiquete.getNroComprobante();
            strFechaLimiteEntrega = anticipoViaticoTiquete.getFechaLimiteEntrega();
            strAccionNotificar = anticipoViaticoTiquete.getAccionNotificar();
            strValorLegalizado = anticipoViaticoTiquete.getValorLegalizado().trim();            
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
                this.strAsunto = "ALERTA: La solicitud de " + strTipoSolicitud.toUpperCase() + " con comprobante #" + strNroComprobante+ " ha cumplido la fecha l??mite de entrega";                 
            }
            
            /*if (strAccionNotificar.equals("AVENCER")){
                this.strAsunto = "ALERTA: La solicitud de " + strTipoSolicitud.toUpperCase() + " con comprobante #" + strNroComprobante+ " est?? pr??xima a cumplir la fecha l??mite de entrega";
            }*/
        
            this.strMensaje += "Cordial saludo.<br /><br />";
            this.strMensaje += "Los datos asociados con la solicitud de <b>" + strTipoSolicitud.toUpperCase() + "</b> con comprobante #" + strNroComprobante + " son:<br /><br />";
            
            if (strCodigoNotificacion.equals("ANTVIATTIQACT")){
                this.strMensaje += "- <b>Nombre del grupo:</b> " + strGrupo.trim() + ".<br />";
            }
                        
            this.strMensaje += "- <b>Nombre del solicitante:</b> " + strSolicitante.trim() + ".<br />";
            this.strMensaje += "- <b>Lugar de comisi??n:</b> " + strLugarComision.trim() + ".<br />";
            this.strMensaje += "- <b>Valor legalizado:</b> " + strValorLegalizado + ".<br />";
            this.strMensaje += "- <b>Fecha inicio comisi??n [aaaa-mm-dd]:</b> " + strFechaInicioComision + ".<br />";
            this.strMensaje += "- <b>Fecha fin comisi??n [aaaa-mm-dd]:</b> " + strFechaLimiteEntrega + ".<br />";
            this.strMensaje += "- <b>Nro. del ticket:</b> " + strNroTicket + ".<br />";
   
            this.strMensaje += "Si ya hizo entrega de la documentaci??n requerida en la Administraci??n SIU, por favor hace caso omiso de este mensaje.<br /><br />";
            this.strMensaje += "Recuerde que:<br /><br />";
            this.strMensaje += "- Para legalizar Anticipos, debe adjuntar las facturas que sumen el total del valor otorgado.<br /><br />";
            this.strMensaje += "- Para legalizar Pasajes, debe adjuntar los pasabordos.<br /><br />";
            this.strMensaje += "- Para legalizar Vi??ticos, debe adjuntar el cumplido y la carta donde se indique que se recibi?? el dinero.<br /><br />";
            this.strMensaje += "Atentamente,<br /><br />";
            this.strMensaje += "Administraci??n de la SIU<br /><br />";
            this.strMensaje += "<b>NOTA</b>: ??ste es un mensaje enviado autom??ticamente. Por favor no d?? respuesta al mismo.";
                          
            this.parametroMail = new ParametroMail();
            this.parametroMail.setDestinatario(this.strDestinatario);
            this.parametroMail.setAsunto(this.strAsunto);
            this.parametroMail.setMensaje(this.strMensaje);            

            sendMailHTML(this.parametroMail);
            new GIDaoException("Notificaci??n enviada correctamente a " + strNombreDestinatario + " al correo " + strEmailDestinatario);
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
                                    
            //strMsgFinal += "Le informo que a partir de este a??o acreditamos el nuevo servicio de calibraci??n de balanzas en el rango de 1 mg a 19 kg, para que por favor nos tenga en cuenta en el momento que requiera calibrar sus balanzas. Adjunto certificado de acreditaci??n con ONAC.<br /><br />";
            strMsgFinal += "Atentamente,<br /><br />";
            strMsgFinal += "<b>Juan Felipe Gallego Sierra</b><br />";
            strMsgFinal += "Ingeniero de Equipos Cient??ficos<br />";
            strMsgFinal += "Sede de Investigaci??n Universitaria -SIU<br />";
            strMsgFinal += "Tel??fono: (574) 219 64 27<br />";
            strMsgFinal += "Correo: lecsiu@udea.edu.co<br />";
            strMsgFinal += "Carrera 53 No. 61 - 30<br />";            
            strMsgFinal += "Laboratorio de Equipos Cient??ficos -LEC<br /><br />";
            strMsgFinal += "<b>NOTA</b>: ??ste es un mensaje enviado autom??ticamente. Por favor no d?? respuesta al mismo.";       
            
            this.strMensaje = "";
            this.strRutaArchivo = "";
            this.strDestinatario = "";
            //this.strRutaArchivo = "C:\\WebApps\\soluciones_siu\\Certificado_ONAC.pdf";
            
            for (CalibracionEquipo calibracionEquipo : calibraciones){                                
                
                strSolicitante = calibracionEquipo.getSolicitante().trim();
                
                if (!(strSolicitante.equals(strSolicitanteAnterior)) && (intCont >0)){
                    
                    strMsgInicial = "Cordial saludo Se??ores <b> " + strSolicitanteAnterior + "</b>.<br /><br />";             
                    strMsgInicial = strMsgInicial + "Como parte del servicio y compromiso con nuestros clientes, nuestro principal prop??sito es contribuir con el excelente desempe??o de sus equipos, para su comodidad estamos remitiendo los siguientes equipos que cumplir??n su vigencia de calibraci??n:<br /><br />";
                    
                    this.strMensaje = strMsgInicial + this.strMensaje;
                    this.strMensaje = this.strMensaje + strMsgFinal;
                    
                    this.strAsunto = "ALERTA: Vencimiento de calibraci??n de equipos " + strSolicitanteAnterior;     
                    
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
                this.strMensaje += strTipoEquipo + " - Serie: " + strNroSerie + ", certificado de calibraci??n: " + strProducto + ", cumplimiento de vigencia: " + strFechaProxCalib + ".<br /><br />";
                                                    
                strSolicitanteAnterior = strSolicitante;
                intCont++;
            }        
            
            strMsgInicial = "Cordial saludo Se??ores <b> " + strSolicitanteAnterior + "</b>.<br /><br />"; 
            strMsgInicial = strMsgInicial + "Los siguientes equipos cumplir??n su vigencia de calibraci??n:<br /><br />";
            this.strMensaje = strMsgInicial + this.strMensaje;
            this.strMensaje = this.strMensaje + strMsgFinal;
            this.strAsunto = "ALERTA: Vencimiento de calibraci??n de equipos " + strSolicitanteAnterior;     

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
              
        new GIDaoException("Notificaci??n enviada correctamente a " + strSolicitante + " al correo " + this.strDestinatario);  

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
        PersonaSIGEP persona = null;
        
        strFirma = "Administraci??n de la SIU";
        
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
            notificacion =notificacionDAO.obtenerUnoSiuWeb(strCodigoNotificacion);
        }catch(GIDaoException gde){
            new GIDaoException("Se gener?? un error recuperando la informaci??n de la notificaci??n con c??digo " + strCodigoNotificacion, gde);            
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
                               strNomResp = notificacion.getNombreDestinatario();
                               strEmailResp = notificacion.getEmailDestinatario();
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
                         this.strMensaje += "La fecha para la compra del insumo  " + strInsumo + " del ??rea " + strArea + " finaliza el d??a de hoy.<br /><br />";
                    }

                    if (strAccionNotificar.equals("AVENCER")){
                         this.strAsunto = "ALERTA: Fecha para la compra de insumo pr??xima a finalizar";
                         this.strMensaje += "La fecha para la compra del insumo  " + strInsumo + " del ??rea " + strArea + " finalizar?? el pr??ximo [aaaa-mm-dd] " + strFechaVencimiento + ".<br /><br />";
                    }               

                    this.strMensaje += "Atentamente,<br /><br />";
                    this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
                    this.strMensaje += "<b>NOTA</b>: ??ste es un mensaje enviado autom??ticamente. Por favor no d?? respuesta al mismo.";

                    this.parametroMail = new ParametroMail();
                    this.parametroMail.setDestinatario(this.strDestinatario);
                    this.parametroMail.setAsunto(this.strAsunto);
                    this.parametroMail.setMensaje(this.strMensaje);

                    sendMailHTML(this.parametroMail);
                    new GIDaoException("Notificaci??n enviada correctamente a " + strNomResp + " al correo " + strEmailResp + " para el insumo " + strInsumo);               
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
            notificacion = notificacionDAO.obtenerUnoSiuWeb(strCodNotificacion);
        }catch(GIDaoException gi){
            new GIDaoException("Se gener?? un obteniendo la configuraci??n de la notificaci??n", gi);
        }
        
        strFirmaGeneral = "<b>Coordinaci??n Proceso Gesti??n Mantenimiento</b>";
        strFirma += strFirmaGeneral + "<br />";
        strFirma += "Administraci??n SIU<br /><br />";       
        strMsgFinal = "<b>Nota:</b> ??ste es un mensaje enviado autom??ticamente. Por favor no d?? respuesta al mismo.";
        
        if (notificacion != null){
            
            this.strDestinatario  = null;
            this.strMensaje = "";
            this.parametroMail  = null;
            strDescripcion = "";
            
            if (notificacion.getCodigo().equals("PLANMNTOSIUEQCIENT")){
                strDescripcion = "Equipos Cient??ficos";
            }
            
            if (notificacion.getCodigo().equals("PLANMNTOSIUEQCOMP")){
                strDescripcion = "Equipos de C??mputo";
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
            
            strNomDestinatario = notificacion.getNombreDestinatario();   
            strEmailDestinatario = notificacion.getEmailDestinatario();
            this.strAsunto = "ALERTA: Mantenimiento preventivo de " + strDescripcion + " pr??ximo a cumplir su programaci??n";                                    

            if (strEmailDestinatario != null && !strEmailDestinatario.equals("")){
                
                if (this.strModoPdn.equals("N")){
                    this.strDestinatario = this.strEmailDllo;            
                }else{
                    this.strDestinatario = strEmailDestinatario;            
                }                

                this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomDestinatario + "</b>.<br /><br />";
                this.strMensaje += "Los siguientes equipos/sistemas cumplen su programaci??n de mantenimiento preventivo en el presente mes:<br /><br />";
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
                new GIDaoException("Notificaci??n enviada a " + strNomDestinatario + " al correo " + strEmailDestinatario + " con " + intTotalEquipos.toString() + " equipos!");
            }else{
                new GIDaoException("El correo del destinatario ASIU es nulo!.");
            }        
        }else{
            new GIDaoException("El objeto de notificaci??n es nulo!");
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
            strMsgFinal += "<b>Coordinaci??n Gesti??n Mantenimiento</b><br /><br />";
            strMsgFinal += "<b>NOTA</b>: ??ste es un mensaje enviado autom??ticamente. Por favor no d?? respuesta al mismo.";       
            
            this.strMensaje = "";
            this.strRutaArchivo = "";
            
            for (MntoPrtvoEqCi mntoPrtvoEqCi : MntosPrtivos){                                
                
                strSolicitante = mntoPrtvoEqCi.getNombreUsuario().trim();
                
                if (!(strSolicitante.equals(strSolicitanteAnterior)) && (intCont >0)){
                    
                    strMsgInicial = "Cordial saludo Se??or(a) <b> " + strSolicitanteAnterior + "</b>.<br /><br />";             
                    strMsgInicial = strMsgInicial + "Como parte del servicio y compromiso con nuestros clientes, nuestro principal prop??sito es contribuir con el excelente desempe??o de sus equipos, para su comodidad estamos remitiendo los siguientes equipos que cumplir??n su vigencia de mantenimiento preventivo:<br /><br />";
                    
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
            
            strMsgInicial = "Cordial saludo Se??or(a) <b> " + strSolicitanteAnterior + "</b>.<br /><br />"; 
            strMsgInicial = strMsgInicial + "Los siguientes equipos cumplir??n su vigencia de mantenimiento preventivo:<br /><br />";
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
        
        String strCodigoNotificacion, strNroFactura, strNroDiasCartera, strFirma, strNomDestinatario;    
        Integer intTotalFacturas;
        Notificacion notificacion = null;
        strCodigoNotificacion = "NOTIFVENCCARTERA";
        strFirma = "Administraci??n de la SIU";
                                            
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        
        try{
            notificacion =notificacionDAO.obtenerUnoSiuWeb(strCodigoNotificacion);
        }catch(GIDaoException gde){
            new GIDaoException("Se gener?? un error recuperando la informaci??n de la notificaci??n con c??digo " + strCodigoNotificacion, gde);            
            notificacion = null;
        }        
        
        if (notificacion != null){

            this.strDestinatario = null;
            this.strAsunto = null;
            this.strMensaje = "";

            strNomDestinatario = notificacion.getNombreDestinatario();
            
            if (this.strModoPdn.equals("N")){
                this.strDestinatario = this.strEmailDllo;            
            }else{
                this.strDestinatario = notificacion.getEmailDestinatario().trim();            
            }
                        
            this.strAsunto = "ALERTA: Facturas que requieren gesti??n de cobro";                                                          
            this.strMensaje += "Cordial saludo Sr(a). <b>" + strNomDestinatario + "</b>.<br /><br />";                  
            this.strMensaje += "Las siguientes facturas requieren gesti??n de cobro: <br /><br />";      
            
            intTotalFacturas = 0;
            
            for(Cartera cartera : carteras){            
                strNroFactura = cartera.getNroFactura();
                strNroDiasCartera = cartera.getNroDiasCartera().toString();      
                                
                this.strMensaje += "- Nro. de factura: " + strNroFactura + ", Nro de d??as: " + strNroDiasCartera + "<br />";
                strNroFactura = "";
                strNroDiasCartera = "";
                intTotalFacturas++;
            }
            
            this.strMensaje += "<br />";
            this.strMensaje += "Atentamente,<br /><br />";
            this.strMensaje += "<b>" + strFirma + "</b><br /><br />";
            this.strMensaje += "<b>NOTA</b>: ??ste es un mensaje enviado autom??ticamente. Por favor no d?? respuesta al mismo.<br />";

            this.parametroMail = new ParametroMail();
            this.parametroMail.setDestinatario(this.strDestinatario);
            this.parametroMail.setAsunto(this.strAsunto);
            this.parametroMail.setMensaje(this.strMensaje);

            sendMailHTML(this.parametroMail);                  
            new GIDaoException("Notificaci??n enviada correctamente a " + strNomDestinatario + " al correo electr??nico " + this.strDestinatario + " con " + intTotalFacturas.toString() + " facturas!.");
              
        }
    }
    
}
