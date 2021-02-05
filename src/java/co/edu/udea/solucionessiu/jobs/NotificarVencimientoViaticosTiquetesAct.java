/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.jobs;

import co.edu.udea.solucionessiu.dao.FuncionesComunesDAO;
import co.edu.udea.solucionessiu.dao.NotificacionDAO;
import co.edu.udea.solucionessiu.dao.NotificacionMailDAO;
import co.edu.udea.solucionessiu.dao.impl.FuncionesComunesDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.NotificacionDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.NotificacionMailDAOImpl;
import co.edu.udea.solucionessiu.dto.AnticipoViaticoTiquete;
import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author jorge.correaj
 */
public class NotificarVencimientoViaticosTiquetesAct implements Job{
    
    private FileInputStream fis;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        
        new GIDaoException("Iniciando tarea NotificarVencimientoViaticosTiquetesActual");
        
        String strCodigoNotificacion, strFechaActual, strRutaArchivo, strNroDiasDespues, strNomHoja, strReserva, strSolicitante, strTipoSolicitud, strNroComprobante, strFechaLimiteEntrega;
        String strAccionNotificar, strValorLegalizado, strObs, strResponsable, strLugarComision, strFechaInicioComision, strNroTicket;
        Double dbValorLegalizado;
        String[] strTemp;
        Integer intFila, intFilaInicio, intColumna, intRegsAlertados;
        Long lgDiasNotificar, lgDiasDiferencia;
        Date dtFechaActual, dtFechaLimiteEntrega, dtFechaInicioComision;
        Notificacion notificacion = null;
        Row row = null;
        Cell cell = null;
        File myFile = null;
        FileInputStream fis = null;
        XSSFWorkbook myWorkBook = null;
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        NotificacionMailDAO notificacionMailDAO = new NotificacionMailDAOImpl();
        
        strCodigoNotificacion = "ANTVIATTIQACT";
        intFilaInicio = 7;
        dtFechaActual = null;
        lgDiasNotificar = 0L;
        strFechaActual = null;
        strRutaArchivo = "";
        intRegsAlertados = 0;
                
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        strFechaActual = funcionesComunesDAO.getFechaActual();
                
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
            strNroDiasDespues = "-" + String.valueOf(notificacion.getDiasDespuesNotificar());
            
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
                     cerrarArchivo();
                }
                
                if (myWorkBook != null){
                    
                    strNomHoja = notificacion.getNombreHoja().trim();
                    XSSFSheet mySheet = myWorkBook.getSheet(strNomHoja);
                    Iterator<Row> rowIterator = mySheet.iterator();
                    
                    while (rowIterator.hasNext()) {
                            
                            intFila = 0;                                   
                            
                            row = rowIterator.next();
                            intFila = row.getRowNum();

                             if (intFila >= intFilaInicio){
                                Iterator<Cell> cellIterator = row.cellIterator();
                                
                                strReserva = "";
                                strNroComprobante="";
                                strSolicitante = "";
                                strTipoSolicitud = "";
                                strLugarComision = "";
                                strFechaLimiteEntrega = "";
                                strValorLegalizado = "";
                                strAccionNotificar = "";
                                dtFechaLimiteEntrega = null;
                                strTemp = null;
                                AnticipoViaticoTiquete anticipoViaticoTiquete  = null;
                                dbValorLegalizado = 0d;
                                strObs = "";
                                strResponsable = "";
                                strFechaInicioComision = "";
                                strNroTicket = "";
                                
                                while (cellIterator.hasNext()) {

                                    cell = cellIterator.next();
                                    intColumna = cell.getColumnIndex(); 
                                    
                                    switch(intColumna){
                                        case 0: 
                                               try{
                                                  if (cell.getStringCellValue() != null){                                                      
                                                        strReserva = cell.getStringCellValue();                                                        
                                                    }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getNumericCellValue() != 0){           
                                                        strReserva = String.valueOf(cell.getNumericCellValue());                                                 
                                                    }else{                    
                                                        strReserva = "-";
                                                    }                                              
                                                }                                
                                            break;
                                            
                                            case 2: 
                                               try{
                                                    if (cell.getStringCellValue() != null){                      
                                                        System.out.println("String");
                                                        strResponsable = cell.getStringCellValue();                                                        
                                                    }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getNumericCellValue() != 0){
                                                        System.out.println("Numeric");
                                                        strResponsable = String.valueOf(cell.getNumericCellValue());                                                 
                                                    }else{                    
                                                        strResponsable = "-";
                                                    }                                              
                                                }
                                                                                    
                                            System.out.println("strResponsable: " + strResponsable);
                                            break;
                                            
                                            case 3:
                                                try{
                                                    if (cell.getStringCellValue() != null){                                                      
                                                            strSolicitante = cell.getStringCellValue().trim();          
                                                        }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getNumericCellValue() != 0){           
                                                        strSolicitante = String.valueOf(cell.getNumericCellValue());                                                 
                                                    }else{                    
                                                        strSolicitante = "-";
                                                    }                   
                                                }                                            
                                            break;
                                                
                                            case 4:
                                                try{
                                                    if (cell.getStringCellValue() != null){                                                      
                                                            strTipoSolicitud = cell.getStringCellValue().trim();          
                                                        }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getNumericCellValue() != 0){           
                                                        strTipoSolicitud = String.valueOf(cell.getNumericCellValue());                                                 
                                                    }else{                    
                                                        strTipoSolicitud = "-";
                                                    }                   
                                                }
                                            break;
                                                
                                             case 8:
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strNroComprobante = cell.getStringCellValue();
                                                    }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getNumericCellValue() != 0){
                                                        strNroComprobante = funcionesComunesDAO.convertirNotacionEACadena(cell.getNumericCellValue());                                                 
                                                    }else{                    
                                                        strNroComprobante = "-";
                                                    }         
                                                }
                                            break;   
                                                 
                                            case 9:
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strLugarComision = cell.getStringCellValue();
                                                    }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getNumericCellValue() != 0){
                                                        strLugarComision = funcionesComunesDAO.convertirNotacionEACadena(cell.getNumericCellValue());                                                 
                                                    }else{                    
                                                        strLugarComision = "-";
                                                    }         
                                                }
                                                
                                                 System.out.println("strLugarComision: " + strLugarComision);
                                            break; 
                                            
                                            case 10:
                                              try{
                                                    if (cell.getStringCellValue() != null){
                                                        strFechaInicioComision = "";
                                                    }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getDateCellValue() != null){
                                                        dtFechaInicioComision = cell.getDateCellValue();
                                                        strFechaInicioComision = funcionesComunesDAO.convertirFechaLarga(dtFechaInicioComision.toString());                                                                
                                                    }else{
                                                        dtFechaInicioComision = null;
                                                        strFechaInicioComision = "";
                                                    }         
                                                }
                                              
                                                System.out.println("strFechaInicioComision: " + strFechaInicioComision);
                                            break;
                                                 
                                            case 11:
                                              try{
                                                    if (cell.getStringCellValue() != null){
                                                        strFechaLimiteEntrega = "";
                                                    }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getDateCellValue() != null){
                                                        dtFechaLimiteEntrega= cell.getDateCellValue();
                                                        strFechaLimiteEntrega = funcionesComunesDAO.convertirFechaLarga(dtFechaLimiteEntrega.toString());                                                                
                                                    }else{
                                                        dtFechaLimiteEntrega = null;
                                                        strFechaLimiteEntrega = "";
                                                    }         
                                                }                              
                                            break;
                                                
                                            case 17:
                                                try{
                                                    if (cell.getStringCellValue() != null){                                    
                                                        strValorLegalizado = cell.getStringCellValue();
                                                    }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getNumericCellValue() != 0){                                  
                                                        dbValorLegalizado = cell.getNumericCellValue();
                                                        strValorLegalizado = String.valueOf(dbValorLegalizado.intValue());
                                                    }else{                                  
                                                        strValorLegalizado = "-";
                                                    }         
                                                }                          
                                            break; 
                                            
                                            case 20:
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        System.out.println("1");
                                                        strNroTicket = cell.getStringCellValue();
                                                    }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getNumericCellValue() != 0){
                                                        System.out.println("2");                   
                                                        dbValorLegalizado = cell.getNumericCellValue();
                                                        strNroTicket = String.valueOf(dbValorLegalizado.intValue());
                                                    }else{                    
                                                        System.out.println("3");
                                                        strNroTicket = "-";
                                                    }         
                                                }
                                                
                                                 System.out.println("strNroTicket: " + strNroTicket);
                                            break; 
                                                
                                            case 21:
                                                try{
                                                if (cell.getStringCellValue() != null){
                                                        strObs = cell.getStringCellValue();
                                                    }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getNumericCellValue() != 0){
                                                        strObs = funcionesComunesDAO.convertirNotacionEACadena(cell.getNumericCellValue());                                                 
                                                    }else{                    
                                                        strObs = "-";
                                                    }         
                                                }                                                
                                            break;
                                                
                                    }                                                                        
                                    
                                } // Fin del iterador de celdas.
                                
                                // Validaciones para notificación.
                                
                                if ((strFechaLimiteEntrega != null) && (!strFechaLimiteEntrega.equals(""))){
                                    dtFechaLimiteEntrega=  null;

                                    try{
                                        dtFechaLimiteEntrega = sdf.parse(strFechaLimiteEntrega);
                                    }catch(ParseException pe){
                                        new GIDaoException("Se generó un error parseando la fecha de finalización", pe);
                                         cerrarArchivo();
                                    }

                                   lgDiasDiferencia= (Long)(funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaActual, dtFechaLimiteEntrega));
                                    
                                   if (lgDiasDiferencia.toString().equals( strNroDiasDespues)){
                                    //if ((lgDiasDiferencia.toString().equals("0")) || (lgDiasDiferencia.toString().equals(lgDiasNotificar.toString()))){

                                            /*if (lgDiasDiferencia.toString().equals("0")){
                                                strAccionNotificar = "DIAVENC";
                                            }

                                            if (lgDiasDiferencia.toString().equals(lgDiasNotificar.toString())){
                                                 strAccionNotificar = "AVENCER";
                                            }*/
                                       
                                            strAccionNotificar = "DIAVENC";

                                            anticipoViaticoTiquete = new AnticipoViaticoTiquete();
                                            anticipoViaticoTiquete.setReserva(strReserva);
                                            anticipoViaticoTiquete.setSolicitante(strSolicitante);
                                            anticipoViaticoTiquete.setTipoSolicitud(strTipoSolicitud);
                                            anticipoViaticoTiquete.setNroComprobante(strNroComprobante);
                                            anticipoViaticoTiquete.setFechaLimiteEntrega(strFechaLimiteEntrega);
                                            anticipoViaticoTiquete.setAccionNotificar(strAccionNotificar);
                                            anticipoViaticoTiquete.setValorLegalizado(strValorLegalizado);
                                            anticipoViaticoTiquete.setObservacion(strObs);
                                            anticipoViaticoTiquete.setCodigoNotificacion(strCodigoNotificacion);
                                            anticipoViaticoTiquete.setResponsable(strResponsable);
                                            anticipoViaticoTiquete.setLugarComision(strLugarComision);
                                            anticipoViaticoTiquete.setFechaInicioComision(strFechaInicioComision);
                                            anticipoViaticoTiquete.setNroTicket(strNroTicket);

                                             try{
                                                notificacionMailDAO.notificarVencimientoAnticipoViaticoTiquete(anticipoViaticoTiquete);                      
                                                intRegsAlertados++;
                                             } catch(GIDaoException g){
                                                new GIDaoException("Se generó un error enviando la notificación para la solicitud " + strReserva,g);
                                            }                                                  
                                        }                                                                                   
                                }   
                                
                                anticipoViaticoTiquete = null;
                             }                            
                            
                        } // Fin del iterador de registros.
                    
                    new GIDaoException("Total de registros alertados: " + intRegsAlertados);

                    row = null;
                    strNomHoja = "";
                    intRegsAlertados = 0;
                    
                }else{
                    new GIDaoException("El objeto de Excel del archivo es nulo");
                }
                
                 cerrarArchivo();                 
            }else{
                new GIDaoException("El objeto del archivo es nulo");
            }
            
        }else{
            new GIDaoException("El objeto de notificación es nulo");
        }
        
        new GIDaoException("Finalizando tarea NotificarVencimientoViaticosTiquetesActual");
    }
    
    private void cerrarArchivo(){
        try{
            if (this.fis != null){
                this.fis.close();
            }                        
        }catch(IOException e){
            new GIDaoException("Se generó un error cerrando el FileInputStream Object", e);
        }  
    }        
}
