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
import co.edu.udea.solucionessiu.dto.Contrato;
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
 * @author jorge.correa
 */
public class NotificarVencimientoContratosNalesAct implements Job{
    
    private FileInputStream fis;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        
         /*
            IMPORTANTE: Esta clase difiere de la clase PruebaNotificarVencimientoDocumentos.java en el método cerrarArchivo() y la variable de la FileInputStream fis. NO reemplazar directamente el contenido!.
        */
        
        new GIDaoException("Iniciando tarea NotificarVencimientoContratosNalesAct");
        
        String strCodigoNotificacion, strFechaActual, strRutaArchivo, strNomHoja, strTipoSolicitud, strNroContrato, strFechaFinalizacion, strTipoContrato, strAccionNotificar;
        Integer intFila, intFilaInicio, intColumna, intRegsAlertados;
        Date dtFechaActual, dtFechaFinalizacion;
        Long lgDiasNotificar, lgDiasDiferencia;
        Notificacion notificacion = null;
        Row row = null;
        Cell cell = null;
        File myFile = null;
        FileInputStream fis = null;
        XSSFWorkbook myWorkBook = null;
                
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        NotificacionMailDAO notificacionMailDAO = new NotificacionMailDAOImpl();
        Contrato contrato = null;
        
        strCodigoNotificacion = "CONTRATOSNALESACT";
         intFilaInicio = 7;
        dtFechaActual = null;
        lgDiasNotificar = 0L;
        strFechaActual = null;
        strRutaArchivo = "";
        intRegsAlertados = 0;
        strTipoContrato = "Minuta servicios";
                
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
                              
                               intColumna = 0;
                               cell = null;
                               strTipoSolicitud = "";
                               strNroContrato = "";
                               strFechaFinalizacion = "";
                               dtFechaFinalizacion = null;
                               lgDiasDiferencia = 0L;
                               strAccionNotificar = "";
                               
                               while (cellIterator.hasNext()) {
                                   
                                   cell = cellIterator.next();
                                    intColumna = cell.getColumnIndex(); 
                                    
                                    switch(intColumna){
                                        case 7:
                                            strTipoSolicitud = cell.getStringCellValue().trim();
                                            break;
                                        case 8:
                                            try{
                                                    if (cell.getStringCellValue() != null){
                                                        strNroContrato = cell.getStringCellValue();
                                                    }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getNumericCellValue() != 0){
                                                        strNroContrato = funcionesComunesDAO.convertirNotacionEACadena(cell.getNumericCellValue());                                                 
                                                    }else{                    
                                                        strNroContrato = "-";
                                                    }         
                                                }                    
                                            break;
                                        case 25:
                                            try{
                                                    if (cell.getStringCellValue() != null){
                                                        strFechaFinalizacion = "";
                                                    }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getDateCellValue() != null){
                                                        dtFechaFinalizacion = cell.getDateCellValue();
                                                        strFechaFinalizacion = funcionesComunesDAO.convertirFechaLarga(dtFechaFinalizacion.toString());                                                                
                                                    }else{
                                                        dtFechaFinalizacion = null;
                                                        strFechaFinalizacion = "";
                                                    }         
                                                }
                                                break;
                                    }                                   
                               }// Fin del iterador de celdas.
                                
                                // Validaciones para notificación.                                           
                               
                               if (strTipoSolicitud.equals(strTipoContrato)){       
                                   if (!strFechaFinalizacion.equals("")){
                                                                                                                  
                                       dtFechaFinalizacion=  null;

                                        try{
                                            dtFechaFinalizacion = sdf.parse(strFechaFinalizacion);
                                        }catch(ParseException pe){
                                            new GIDaoException("Se generó un error parseando la fecha de finalización", pe);
                                            cerrarArchivo();
                                        }
                                       
                                       lgDiasDiferencia= (Long)(funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaActual, dtFechaFinalizacion));
                                        
                                        if ((lgDiasDiferencia.toString().equals("0")) || (lgDiasDiferencia.toString().equals(lgDiasNotificar.toString()))){
                                            
                                            if (lgDiasDiferencia.toString().equals("0")){
                                                strAccionNotificar = "DIAVENC";
                                            }

                                            if (lgDiasDiferencia.toString().equals(lgDiasNotificar.toString())){
                                                 strAccionNotificar = "AVENCER";
                                            }
                                            
                                            contrato = new Contrato();
                                            contrato.setCodigoContrato(strNroContrato);
                                            contrato.setFechaTerminacion(strFechaFinalizacion);
                                            contrato.setTipoContrato("NALACT");
                                            contrato.setAccionNotificar(strAccionNotificar);
                                            
                                             try{
                                                notificacionMailDAO.notificarVencimientoContrato(contrato);                      
                                                intRegsAlertados++;
                                             } catch(GIDaoException g){
                                                new GIDaoException("Se generó un error enviando la notificación para la " + strTipoContrato + " con código " + strNroContrato,g);
                                                cerrarArchivo();
                                            }                                                  
                                        }            
                                       
                                   }                             
                               }                 
                               
                               contrato = null;
                            }
                        
                    }// Fin del iterador de registros.
                    
                    new GIDaoException("Total de documentos alertados: " + intRegsAlertados);

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
        
        new GIDaoException("Finalizando tarea NotificarVencimientoContratosNalesAct");
        
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
