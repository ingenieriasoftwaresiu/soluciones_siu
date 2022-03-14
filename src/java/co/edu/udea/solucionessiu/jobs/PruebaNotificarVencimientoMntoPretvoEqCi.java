/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.jobs;

import co.edu.udea.solucionessiu.dao.FuncionesComunesDAO;
import co.edu.udea.solucionessiu.dao.NotificacionDAO;
import co.edu.udea.solucionessiu.dao.impl.FuncionesComunesDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.NotificacionDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.NotificacionMailSiuWebDAOImpl;
import co.edu.udea.solucionessiu.dto.CalibracionEquipo;
import co.edu.udea.solucionessiu.dto.MntoPrtvoEqCi;
import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import co.edu.udea.solucionessiu.dao.NotificacionMailSiuWebDAO;

/**
 *
 * @author jorge.correa
 */
public class PruebaNotificarVencimientoMntoPretvoEqCi {
    
    public static void main(String args[]) throws IOException{
        
        new GIDaoException("Iniciando tarea NotificarVencimientoMntoPretvoEqCi");
        
        String strCodigoNotificacion, strFechaActual, strRutaArchivo, strNomHoja, strFechaRecepcion, strTipoSolicitud, strUsuario, strEmail, strNomEquipo;
        Integer intFila, intFilaInicio, intColumna;    
        Date dtFechaActual, dtFechaRecepcion;
        Long lgDiasNotificar, lgDiasDiferencia;
        Notificacion notificacion = null;
        Row row = null;
        Cell cell = null;
        File myFile = null;
        FileInputStream fis = null;
        XSSFWorkbook myWorkBook = null;
                
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        NotificacionMailSiuWebDAO notificacionMailDAO = new NotificacionMailSiuWebDAOImpl();
        ArrayList<MntoPrtvoEqCi> MntosPrtivos = new ArrayList<MntoPrtvoEqCi>();
        
        strCodigoNotificacion = "NOTIFMNTOPRTVOEQCI";
        intFilaInicio = 6;
        dtFechaActual = null;
        lgDiasNotificar = 0L;
        strFechaActual = null;
        strRutaArchivo = "";
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //strFechaActual = funcionesComunesDAO.getFechaActual();
        strFechaActual = "2019-09-30";
                
        try{
            dtFechaActual = sdf.parse(strFechaActual);
        }catch(ParseException pe){
            new GIDaoException("Se generó un error parseando la fecha actual", pe);
        }
        
        try{
            notificacion =notificacionDAO.obtenerUno(strCodigoNotificacion);
        }catch(GIDaoException gde){
            new GIDaoException("Se generó un error recuperando la información de la notificación con código " + strCodigoNotificacion, gde);            
            notificacion = null;
        }
        
        if (notificacion != null){
            strRutaArchivo = notificacion.getRuta().trim();
            lgDiasNotificar = new Long(notificacion.getDiasNotificar());
            //lgDiasNotificar = 187L;
            System.out.println("strRutaArchivo: " + strRutaArchivo);
            System.out.println("Días a notificar: " + lgDiasNotificar);
            
            try{            
                myFile = new File(strRutaArchivo);
                fis = new FileInputStream(myFile);
            }catch(FileNotFoundException fnfe){
                new GIDaoException("Se generó un error cargando el objeto desde el archivo", fnfe);            
                fis = null;
            }
            
            if (fis != null){
                try{
                    myWorkBook = new XSSFWorkbook (fis);
                }catch(IOException ioe){
                    new GIDaoException("Se generó un error abriendo el objeto de Excel desde el archivo", ioe);
                }
                
                if (myWorkBook != null){
                    strNomHoja = notificacion.getNombreHoja().trim();
                    System.out.println("strNomHoja: " + strNomHoja);
                    XSSFSheet mySheet = myWorkBook.getSheet(strNomHoja);
                    Iterator<Row> rowIterator = mySheet.iterator();
                    
                    while (rowIterator.hasNext()) {
                        
                            intFila = 0;                                   
                            
                            row = rowIterator.next();
                            intFila = row.getRowNum();

                            System.out.println("Fila: " + new Integer(intFila+1));

                            if (intFila >= intFilaInicio){
                              Iterator<Cell> cellIterator = row.cellIterator();                                
                              
                               intColumna = 0;
                               cell = null;
                               strFechaRecepcion = "";
                               dtFechaRecepcion = null;
                               lgDiasDiferencia = 0L;                     
                               strEmail = null;
                               strTipoSolicitud = "";
                               strUsuario = "";
                               strNomEquipo = "";
                               
                               while (cellIterator.hasNext()) {
                                   
                                   cell = cellIterator.next();
                                    intColumna = cell.getColumnIndex(); 
                                    
                                    switch(intColumna){      
                                         case 1:
                                            try{
                                                if (cell.getStringCellValue() != null){
                                                    strFechaRecepcion = "";
                                                }
                                            }catch(IllegalStateException ise){
                                                if (cell.getDateCellValue() != null){
                                                    dtFechaRecepcion = cell.getDateCellValue();
                                                    strFechaRecepcion = funcionesComunesDAO.convertirFechaLarga(dtFechaRecepcion.toString());                                                                
                                                }else{
                                                    dtFechaRecepcion = null;
                                                    strFechaRecepcion = "";
                                                }                                                       
                                            }
                                             
                                                System.out.println("strFechaRecepcion: " + strFechaRecepcion);                                         
                                            break;
                                        case 2:
                                                strUsuario = cell.getStringCellValue().trim();
                                                System.out.println("strSolicitante: " + strUsuario);
                                            break;
                                        case 4:
                                                strNomEquipo = cell.getStringCellValue().trim();
                                                System.out.println("strTipoEquipo: " + strNomEquipo);
                                            break;                                       
                                        case 5:
                                                strTipoSolicitud = cell.getStringCellValue().trim();
                                                System.out.println("strNroSerie: " + strTipoSolicitud);
                                                break;                                
                                         case 11:
                                             strEmail = cell.getStringCellValue().trim();
                                             System.out.println("strEmail: " + strEmail);
                                            break;
                                    }                                   
                               }// Fin del iterador de celdas.                                
                              
                                // Validaciones para notificación.                  
                               
                               if (strEmail != null && !strEmail.equals("") && !strEmail.equals("N/A") && !strEmail.equals("-")){                                   
                                   if (strTipoSolicitud.equals("Preventivo")){
                                        System.out.println("SE PROCESA");
                                        if (!strFechaRecepcion.equals("")){

                                            dtFechaRecepcion = null;

                                            try{
                                                 dtFechaRecepcion = sdf.parse(strFechaRecepcion);
                                             }catch(ParseException pe){
                                                 new GIDaoException("Se generó un error parseando la fecha de recepción de los equipos", pe);
                                             }

                                            dtFechaRecepcion = funcionesComunesDAO.aumentarAniosFecha(dtFechaRecepcion, 1);
                                            System.out.println("dtFechaRecepcion: " + dtFechaRecepcion.toString());

                                            lgDiasDiferencia= (Long)(funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaActual, dtFechaRecepcion));
                                             System.out.println("lgDiasDiferencia: " + lgDiasDiferencia);

                                            if ((lgDiasDiferencia >= 0) && (lgDiasDiferencia <= lgDiasNotificar)){ 
                                                System.out.println("SI CUMPLE EL TIEMPO");
                                                MntoPrtvoEqCi mntoPrtvoEqCi = new MntoPrtvoEqCi();
                                                mntoPrtvoEqCi.setFechaRecepcion(dtFechaRecepcion);
                                                mntoPrtvoEqCi.setNombreUsuario(strUsuario);                                       
                                                mntoPrtvoEqCi.setNombreEquipo(strNomEquipo);
                                                mntoPrtvoEqCi.setEmail(strEmail);
                                                MntosPrtivos.add(mntoPrtvoEqCi);                                        
                                            }else{
                                                System.out.println("NO CUMPLE EL TIEMPO");
                                            }                                                    
                                        }
                                    }else{
                                        System.out.println("NO SE PROCESA, NO ES UN MNTO PREVENTIVO");
                                   }                                       
                                }else{
                                   System.out.println("NO SE PROCESA, NO TIENE CORREO ELECTRÓNICO");
                               }                               
                            }
                        
                    }// Fin del iterador de registros.
                    
                    Collections.sort(MntosPrtivos, new Comparator<MntoPrtvoEqCi>(){
                        public int compare(MntoPrtvoEqCi obj1, MntoPrtvoEqCi obj2){
                            return obj1.getNombreUsuario().compareTo(obj2.getNombreUsuario());
                        }
                    });                    
                    
                   try{
                        notificacionMailDAO.notificarVencimientoMntoPrtvoEquCi(MntosPrtivos); 
                     } catch(GIDaoException g){
                        new GIDaoException("Se generó un error enviando las notificaciones!",g);
                        MntosPrtivos.clear();
                    }
                    
                    new GIDaoException("Total de registros alertados: " + MntosPrtivos.size());

                    row = null;
                    strNomHoja = "";        
                    
                }else{
                    new GIDaoException("El objeto de Excel del archivo es nulo");
                }
                
                try{
                    if (fis != null){
                        fis.close();
                    }  
                }catch(IOException e){
                   new GIDaoException("Se generó un error cerrando el FileInputStream Object", e);
               }      
            }else{
                new GIDaoException("El objeto del archivo es nulo");
            }            
        }else{
            new GIDaoException("El objeto de notificación es nulo");
        }
        
        new GIDaoException("Finalizando tarea NotificarVencimientoMntoPretvoEqCi");        
        
    }
}
