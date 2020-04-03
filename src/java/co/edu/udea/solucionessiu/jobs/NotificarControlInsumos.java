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
import co.edu.udea.solucionessiu.dto.ControlInsumo;
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
public class NotificarControlInsumos implements Job{
    
     private FileInputStream fis;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        
       new GIDaoException("Iniciando tarea NotificarControlInsumos");
        
        String strCodigoNotificacion, strFechaActual, strRutaArchivo, strNomHoja, strArea, strInsumo, strDescripcion, strNumId, strLote, strCantidad, strRango, strFechaVencimiento, strAccionNotificar;
        String strResponsable, strFechaCompra, strObs;
        Integer intFila, intFilaInicio, intColumna, intRegsAlertados;
        Date dtFechaActual, dtFechaVencimiento, dtFechaCompra;
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
        ControlInsumo controlInsumo = null;
        
        strCodigoNotificacion = "CONTROLINSUMOS";
        intFilaInicio = 5;
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
                               strArea = "";
                               strInsumo = "";
                               strDescripcion = ""; 
                               strNumId = ""; 
                               strLote = ""; 
                               strCantidad =  ""; 
                               strRango = "";
                               strResponsable = "";
                               strFechaVencimiento = "";
                               strFechaCompra = "";
                               strObs = "";
                               dtFechaVencimiento = null;
                               dtFechaCompra = null;
                               lgDiasDiferencia = 0L;
                               strAccionNotificar = "";
                               
                               while (cellIterator.hasNext()) {
                                   
                                   cell = cellIterator.next();
                                    intColumna = cell.getColumnIndex(); 
                                    
                                    switch(intColumna){
                                        case 0:
                                            strArea = cell.getStringCellValue().trim();                                            
                                            break;
                                        case 1:
                                            strInsumo = cell.getStringCellValue().trim();                                           
                                            break;
                                        case 2:
                                            strDescripcion = cell.getStringCellValue().trim();                                           
                                            break;
                                        case 3:                                                                                     
                                           try{
                                                if (cell.getStringCellValue() != null){
                                                    strNumId = cell.getStringCellValue();
                                                }
                                            }catch(IllegalStateException ise){
                                                if (cell.getNumericCellValue() != 0D){
                                                    strNumId = String.valueOf(cell.getNumericCellValue());                                                                                               
                                                }else{                                   
                                                    strNumId = "";
                                                }         
                                            }                                           
                                           break;
                                        case 4:
                                            try{
                                                if (cell.getStringCellValue() != null){
                                                    strLote = cell.getStringCellValue();
                                                }
                                            }catch(IllegalStateException ise){
                                                if (cell.getNumericCellValue() != 0D){
                                                    strLote = String.valueOf(cell.getNumericCellValue());                                                                                               
                                                }else{                                   
                                                    strLote = "";
                                                }         
                                            }                                                                                
                                           break;
                                        case 5:
                                            try{
                                                if (cell.getStringCellValue() != null){
                                                    strCantidad = cell.getStringCellValue();
                                                }
                                            }catch(IllegalStateException ise){
                                                if (cell.getNumericCellValue() != 0D){
                                                    strCantidad = String.valueOf(cell.getNumericCellValue());                                                                                               
                                                }else{                                   
                                                    strCantidad = "";
                                                }         
                                            }                                                             
                                           break;
                                        case 6:
                                            try{
                                                if (cell.getStringCellValue() != null){
                                                    strRango = cell.getStringCellValue();
                                                }
                                            }catch(IllegalStateException ise){
                                                if (cell.getNumericCellValue() != 0D){
                                                    strRango = String.valueOf(cell.getNumericCellValue());                                                                                               
                                                }else{                                   
                                                    strRango = "";
                                                }         
                                            }                                                                               
                                           break;
                                        case 7:
                                           strResponsable = cell.getStringCellValue().trim();
                                           break;
                                        case 8:
                                            try{
                                                if (cell.getStringCellValue() != null){
                                                    strFechaVencimiento = "";
                                                }
                                            }catch(IllegalStateException ise){
                                                if (cell.getDateCellValue() != null){
                                                    dtFechaVencimiento = cell.getDateCellValue();
                                                    strFechaVencimiento = funcionesComunesDAO.convertirFechaLarga(dtFechaVencimiento.toString());                                                                
                                                }else{
                                                    dtFechaVencimiento = null;
                                                    strFechaVencimiento = "";
                                                }         
                                            }                                           
                                            break;
                                        case 9:
                                            try{
                                                if (cell.getStringCellValue() != null){
                                                    strFechaCompra= "";
                                                }
                                            }catch(IllegalStateException ise){
                                                if (cell.getDateCellValue() != null){
                                                    dtFechaCompra = cell.getDateCellValue();
                                                    strFechaCompra = funcionesComunesDAO.convertirFechaLarga(dtFechaCompra.toString());                                                                
                                                }else{
                                                    dtFechaCompra = null;
                                                    strFechaCompra = "";
                                                }         
                                            }                                          
                                            break;
                                        case 11:
                                           strObs = cell.getStringCellValue().trim();                                          
                                           break;                                            
                                    }                                   
                               }// Fin del iterador de celdas.                                
                              
                                // Validaciones para notificación.                  
                               
                               if (!strFechaVencimiento.equals("")) {                                      
                                                                                                                  
                                    dtFechaVencimiento=  null;

                                     try{
                                         dtFechaVencimiento = sdf.parse(strFechaVencimiento);
                                     }catch(ParseException pe){
                                         new GIDaoException("Se generó un error parseando la fecha de vencimiento del insumo", pe);
                                         cerrarArchivo();
                                     }

                                    lgDiasDiferencia= (Long)(funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaActual, dtFechaVencimiento));                                   

                                     if ((lgDiasDiferencia.toString().equals("0")) || (lgDiasDiferencia.toString().equals(lgDiasNotificar.toString()))){                                            

                                         if (lgDiasDiferencia.toString().equals("0")){
                                             strAccionNotificar = "DIAVENC";
                                         }

                                         if (lgDiasDiferencia.toString().equals(lgDiasNotificar.toString())){
                                              strAccionNotificar = "AVENCER";
                                         }

                                         controlInsumo = new ControlInsumo();
                                         controlInsumo.setAccionNotificar(strAccionNotificar);
                                         controlInsumo.setArea(strArea);
                                         controlInsumo.setCantidad(strCantidad);
                                         controlInsumo.setDescripcion(strDescripcion);
                                         controlInsumo.setFechaCompra(dtFechaCompra);
                                         controlInsumo.setFechaVencimiento(dtFechaVencimiento);
                                         controlInsumo.setInsumo(strInsumo);
                                         controlInsumo.setLote(strLote);
                                         controlInsumo.setNumeroIdentificacion(strNumId);
                                         controlInsumo.setObservacion(strObs);
                                         controlInsumo.setRangoMedicion(strRango);
                                         controlInsumo.setResponsable(strResponsable);

                                         try{
                                             notificacionMailDAO.notificarControlInsumos(controlInsumo);                      
                                             intRegsAlertados++;
                                          } catch(GIDaoException g){
                                             new GIDaoException("Se generó un error enviando la notificación para el insumo " + strInsumo,g);
                                         }                                                  
                                     }            
                                   
                                }
                               
                                controlInsumo = null;
                               
                            }
                        
                    }// Fin del iterador de registros.
                    
                    new GIDaoException("Total de insumos alertados: " + intRegsAlertados);

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
        
        new GIDaoException("Finalizando tarea NotificarControlInsumos");  
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
