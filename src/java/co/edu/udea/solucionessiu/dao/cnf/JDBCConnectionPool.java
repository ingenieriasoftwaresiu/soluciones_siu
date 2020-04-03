/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dao.cnf;

import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author George
 */
public class JDBCConnectionPool {
    
    private static final String BASE_DATOS = "bd";
    private static final String TAG_DRIVER = "driver";
    private static final String TAG_URL = "url";
    private static final String TAG_USER = "userBD";
    private static final String TAG_PASSWORD = "passwdBD";
    
    /*SIU-WEB1*/
    //private static final String RUTA_XML = "D:\\Program Files\\Apache Software Foundation\\Tomcat 6.0\\webapps\\parametros_sigep\\WEB-INF\\confBD.xml";
    
    /*SIU-WEB*/
   //private static final String RUTA_XML = "C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0_Tomcat7041\\webapps\\soluciones_siu\\WEB-INF\\confBD.xml";
    
    /*ING-SOFTWARE*/
   private static final String RUTA_XML = "C:\\WebApps\\soluciones_siu\\web\\WEB-INF\\confBD.xml";    
    
     /*SIU-PRUEBAS*/
    //private static final String RUTA_XML = "C:\\Program Files\\Apache Software Foundation\\Tomcat 7.0\\webapps\\soluciones_siu\\WEB-INF\\confBD.xml";
    
    /*ING-SOFTWARE APACHE TOMCAT 7*/    
    //private static final String RUTA_XML = "C:\\apache-tomcat-7.0.29\\webapps\\parametros_sigep\\WEB-INF\\confBD.xml";
    
    /*GEORGE*/
     //private static final String RUTA_XML = "C:\\WebApps\\facturacion_servicios\\parametros_sigep\\web\\WEB-INF\\confBD.xml";
        
    public Connection getConexion() throws GIDaoException{
        
        Connection cn = null;
        String[] strDatos = null;
        String strDriver;
        String strURL;
        String strLogin;
        String strPwd;
        
        try{            
            
            strDatos = getParametrosConexion();
            
            if (strDatos != null){
                strDriver = strDatos[0];
                strURL = strDatos[1];
                strLogin = strDatos[2];
                strPwd = strDatos[3];                                         
                
                Class.forName(strDriver);
                cn = DriverManager.getConnection(strURL, strLogin, strPwd);
            }else{
                throw new GIDaoException("No se pudieron recuperar los parámetros del archivo XML.");
            }            
        }catch(ClassNotFoundException cnfe){
            throw new GIDaoException("No se encontró el driver de conexión.",cnfe);
        }catch(SQLException e){
            throw new GIDaoException("No se pudo conectar a la base de datos.",e);    
        }catch(Exception e){
            throw new GIDaoException("Se generó un error con el archivo XML de parámetros.",e);  
        }
        
        return cn;
    }
    
    private String[] getParametrosConexion() throws GIDaoException{
        
        String[] strDatos = null;
             
        try {
                                                                           
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse (new File(RUTA_XML));
            
            // normalize text representation
            doc.getDocumentElement ().normalize ();
            
            NodeList listOfPersons = doc.getElementsByTagName(BASE_DATOS);     
            Node firstPersonNode = listOfPersons.item(0);
            
            if(firstPersonNode.getNodeType() == Node.ELEMENT_NODE){
                
                strDatos = new String[4];
                
                Element firstPersonElement = (Element)firstPersonNode;                              
      
                NodeList firstNameList = firstPersonElement.getElementsByTagName(TAG_DRIVER);
                Element firstNameElement = (Element)firstNameList.item(0);

                NodeList textFNList = firstNameElement.getChildNodes();
                strDatos[0] = ((Node)textFNList.item(0)).getNodeValue().trim();
      
                NodeList lastNameList = firstPersonElement.getElementsByTagName(TAG_URL);
                Element lastNameElement = (Element)lastNameList.item(0);

                NodeList textLNList = lastNameElement.getChildNodes();                
                strDatos[1] = ((Node)textLNList.item(0)).getNodeValue().trim();
 
                NodeList ageList = firstPersonElement.getElementsByTagName(TAG_USER);
                Element ageElement = (Element)ageList.item(0);

                NodeList textAgeList = ageElement.getChildNodes();                
                strDatos[2] = ((Node)textAgeList.item(0)).getNodeValue().trim();
                
                NodeList passwdBD = firstPersonElement.getElementsByTagName(TAG_PASSWORD);
                Element pwdElement = (Element)passwdBD.item(0);

                NodeList textPwdList = pwdElement.getChildNodes();                 
                if ((Node)textPwdList.item(0) != null){
                    String strTemp = ((Node)textPwdList.item(0)).getNodeValue();
                
                    if (strTemp.equals("")){
                        strDatos[3] = "";
                    }else{
                        strDatos[3] = strTemp.trim();
                    } 
                }else{
                    strDatos[3] = "";
                }                                                        
            }
        }catch (SAXParseException err) {
            throw new GIDaoException("Se presentó un error parseando el XML.",err);
        }catch (SAXException e) {
            throw new GIDaoException("Se presentó un error con el XML.",e);
        }catch(ParserConfigurationException pce){
            throw new GIDaoException("Se presentó un error parseando los valores en el XML.",pce);
        }catch(IOException ioe){
            throw new GIDaoException("Se presentó un error cargando el archivo XML.",ioe);
        }
            
        return strDatos;
    }
}
