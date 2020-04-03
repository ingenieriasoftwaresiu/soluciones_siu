/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dao.impl;

import co.edu.udea.solucionessiu.dao.EnvioMailDAO;
import co.edu.udea.solucionessiu.dao.ParametroGeneralDAO;
import co.edu.udea.solucionessiu.dto.ParametroGeneral;
import co.edu.udea.solucionessiu.dto.ParametroMail;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.io.File;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author jorge.correa
 */
public class EnvioMailDAOimpl  implements EnvioMailDAO{
    private String mailSMTPServer;
    private String mailSMTPServerPort;
    private String mailSenha;
    private String from;
    private Properties props;
    private ParametroGeneralDAO parametroGeneralDAO;
    private ParametroGeneral parametroGeneral;
    
    public EnvioMailDAOimpl(){
        this.props = new Properties();        
        parametroGeneralDAO = new ParametroGeneralDAOImpl();
        
        try{
            parametroGeneral = parametroGeneralDAO.obtenerParametrosGenerales();
        }catch(GIDaoException e){
            new GIDaoException("Se generó un error al obtener los parámetros generales", e);
        }
        
        if (parametroGeneral != null){
            mailSMTPServer = parametroGeneral.getNombreServidor();
            mailSMTPServerPort = parametroGeneral.getNumeroPuerto().toString();
            mailSenha = parametroGeneral.getClaveConexion();
            from = parametroGeneral.getUsuarioConexion();
                        
            props.put("mail.transport.protocol","smtp");
            props.put("mail.smtp.starttls.enable","true");
            props.put("mail.smtp.host",mailSMTPServer);        
            props.put("mail.smtp.auth","true");
            props.put("mail.smtp.user",from);
            props.put("mail.smtp.debug","true");       
            props.put("mail.smtp.port",mailSMTPServerPort);                       
            props.put("mail.smtp.socketFactory.port",mailSMTPServerPort);            
            props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback","false");
        }
    }

    @Override
    public void sendMail(ParametroMail parametroMail) throws GIDaoException {       
        
        if (parametroGeneral != null){
            SimpleAuth auth = new SimpleAuth(from,mailSenha);           
            Session session = Session.getDefaultInstance(props,auth);
            session.setDebug(false);

            Message msg = new MimeMessage(session);

            try{                
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress(parametroMail.getDestinatario()));
                msg.setFrom(new InternetAddress(from));                
                msg.setSubject(parametroMail.getAsunto());                            
                msg.setText(parametroMail.getMensaje());

            }catch(Exception e){
                new GIDaoException("Se generó un error  preparando el objeto Mail.", e);
            }

            Transport tr=null;
            try{                
                tr = session.getTransport("smtp");
                tr.connect(mailSMTPServer,from,mailSenha);
                msg.saveChanges();
                tr.sendMessage(msg, msg.getAllRecipients());
                tr.close();
            }catch(Exception e){
                new GIDaoException("Se generó un error enviando el mail al destinatario " + parametroMail.getDestinatario(), e);
            }finally{
                try{
                    
                    if (tr != null){
                        tr.close();
                    }
                }catch(MessagingException me){
                    new GIDaoException("Se generó un error cerrando el objeto del transporte.", me);
                }
            }                    
        }     
    }        

    @Override
    public void sendMailHTML(ParametroMail parametroMail) throws GIDaoException {
                                              
        if (parametroGeneral != null){               

            SimpleAuth auth = new SimpleAuth(from,mailSenha);           
            Session session = Session.getDefaultInstance(props,auth);
            session.setDebug(false);

            MimeMessage msg = new MimeMessage(session);

            try{                
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress(parametroMail.getDestinatario()));
                msg.setFrom(new InternetAddress(from));                
                msg.setSubject(parametroMail.getAsunto());              
                msg.setContent(parametroMail.getMensaje(),"text/html" );                

            }catch(Exception e){
                new GIDaoException("Se generó un error  preparando el objeto Mail.", e);
            }

            Transport tr = null;
            try{                
                tr = session.getTransport("smtp");
                tr.connect(mailSMTPServer,from,mailSenha);
                msg.saveChanges();
                tr.sendMessage(msg, msg.getAllRecipients());
                tr.close();
            }catch(Exception e){
                new GIDaoException("Se generó un error enviando el Mail al destinatario " + parametroMail.getDestinatario(), e);
            }finally{
                try{
                    
                    if (tr != null){
                        tr.close();
                    }
                }catch(MessagingException me){
                    new GIDaoException("Se generó un error cerrando el objeto del transporte.", me);
                }
            }        
        }
    }

    @Override
    public void sendMailAttach(ParametroMail parametroMail) throws GIDaoException {        
        
        if (parametroGeneral != null){             
            SimpleAuth auth = new SimpleAuth(from,mailSenha);           
            Session session = Session.getDefaultInstance(props,auth);
            session.setDebug(false);

            MimeMessage msg = new MimeMessage(session);

            try{                
                msg.setRecipient(Message.RecipientType.TO, new InternetAddress(parametroMail.getDestinatario()));
                msg.setFrom(new InternetAddress(from));                
                msg.setSubject(parametroMail.getAsunto());              
                            
                BodyPart cuerpoMensaje = new MimeBodyPart();
                cuerpoMensaje.setText(parametroMail.getMensaje());
                
                Multipart multiparte = new MimeMultipart();
                multiparte.addBodyPart(cuerpoMensaje);
                
                cuerpoMensaje = new MimeBodyPart();
                String nombreArchivo = parametroMail.getRutaArchivo();
                DataSource fuente = new FileDataSource(nombreArchivo);                
                cuerpoMensaje.setDataHandler(new DataHandler(fuente));
                cuerpoMensaje.setFileName(nombreArchivo);
                multiparte.addBodyPart(cuerpoMensaje);
                
                msg.setContent(multiparte);

            }catch(Exception e){
                new GIDaoException("Se generó un error  preparando el objeto Mail.", e);
            }

            Transport tr = null;
            try{                
                tr = session.getTransport("smtp");
                tr.connect(mailSMTPServer,from,mailSenha);
                msg.saveChanges();
                tr.sendMessage(msg, msg.getAllRecipients());
                tr.close();
            }catch(Exception e){
                new GIDaoException("Se generó un error enviando el Mail al destinatario " + parametroMail.getDestinatario(), e);
            }finally{
                try{
                    
                    if (tr != null){
                        tr.close();
                    }
                }catch(MessagingException me){
                    new GIDaoException("Se generó un error cerrando el objeto del transporte.", me);
                }
            }        
        }
    }

    @Override
    public void sendBCCMultiple(ParametroMail parametroMail, List<String> destinatarios) throws GIDaoException {
        
        String strEmail;
        
        if (parametroGeneral != null){            
            SimpleAuth auth = new SimpleAuth(from,mailSenha);           
            Session session = Session.getDefaultInstance(props,auth);
            session.setDebug(false);

            MimeMessage msg = new MimeMessage(session);
            
            if (destinatarios.size() > 0){
                
                for(int i=0;i<destinatarios.size();i++){
                    strEmail = "";
                    try{
                        strEmail = destinatarios.get(i);
                        msg.addRecipient(Message.RecipientType.BCC, new InternetAddress(strEmail));
                    }catch(AddressException ae){
                        new GIDaoException("Se generó un error  agregando la dirección de correo " + strEmail, ae);
                    }catch(MessagingException me){
                        new GIDaoException("Se generó un error agregando las dirección de correo " + strEmail, me);
                    }
                }
                
                try{                                    
                    msg.setFrom(new InternetAddress(from));                
                    msg.setSubject(parametroMail.getAsunto());              
                    msg.setContent(parametroMail.getMensaje(),"text/html" );                

                }catch(Exception e){
                    new GIDaoException("Se generó un error  preparando el objeto Mail.", e);
                }

                Transport tr = null;
                try{                
                    tr = session.getTransport("smtp");
                    tr.connect(mailSMTPServer,from,mailSenha);
                    msg.saveChanges();
                    tr.sendMessage(msg, msg.getAllRecipients());
                    tr.close();
                }catch(Exception e){
                    new GIDaoException("Se generó un error enviando el Mail al destinatario " + parametroMail.getDestinatario(), e);
                }finally{
                    try{

                        if (tr != null){
                            tr.close();
                        }
                    }catch(MessagingException me){
                        new GIDaoException("Se generó un error cerrando el objeto del transporte.", me);
                    }
                }  
            }                  
        }
    }

    @Override
    public void sendMailHTMLAttach(ParametroMail parametroMail) throws GIDaoException {
        
        if (parametroGeneral != null){          
        
            try {
                    SimpleAuth auth = new SimpleAuth(from,mailSenha);           
                    Session session = Session.getDefaultInstance(props,auth);
                    session.setDebug(false);

                    MimeMessage msg = new MimeMessage(session);
                    msg.setFrom(new InternetAddress(from));
                    msg.setRecipients(Message.RecipientType.TO, parametroMail.getDestinatario());
                    msg.setSubject(parametroMail.getAsunto());
                    
                    Multipart multipart = new MimeMultipart();

                    MimeBodyPart htmlPart = new MimeBodyPart();
                    String htmlContent = parametroMail.getMensaje();
                    htmlPart.setContent(htmlContent, "text/html");
                    multipart.addBodyPart(htmlPart);

                    MimeBodyPart attachementPart = new MimeBodyPart();
                    attachementPart.attachFile(new File(parametroMail.getRutaArchivo()));
                    multipart.addBodyPart(attachementPart);

                    msg.setContent(multipart);
                    Transport.send(msg);                    
           } catch (Exception ex) {
                ex.printStackTrace();
           }
        }
    }
}

class SimpleAuth extends Authenticator{
    
    public String username = null;
    public String password = null;
    
    public SimpleAuth(String user, String pwd){
        username = user;
        password = pwd;
    }
    
    @Override
    protected PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(username,password);
    }
    
}