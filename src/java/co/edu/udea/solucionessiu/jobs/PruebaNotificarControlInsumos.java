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
import co.edu.udea.solucionessiu.dao.NotificacionMailSiuWebDAO;

/**
 *
 * @author jorge.correa
 */
public class PruebaNotificarControlInsumos {
    
    public static void main(String args[]) throws IOException{
        
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
        NotificacionMailSiuWebDAO notificacionMailDAO = new NotificacionMailSiuWebDAOImpl();
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
            notificacion =notificacionDAO.obtenerUnoSiuWeb(strCodigoNotificacion);
        }catch(GIDaoException gde){
            new GIDaoException("Se generó un error recuperando la información de la notificación con código " + strCodigoNotificacion, gde);            
            notificacion = null;
        }
        
        if (notificacion != null){
            strRutaArchivo = notificacion.getRuta().trim();
            lgDiasNotificar = new Long(notificacion.getDiasNotificar());
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
                                            System.out.println("strArea: " + strArea);
                                            break;
                                        case 1:
                                            strInsumo = cell.getStringCellValue().trim();
                                            System.out.println("strInsumo: " + strInsumo);
                                            break;
                                        case 2:
                                            strDescripcion = cell.getStringCellValue().trim();
                                            System.out.println("strDescripcion: " + strDescripcion);
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
                                           System.out.println("strNumId: " + strNumId);
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
                                           System.out.println("strLote: " + strLote);
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
                                           System.out.println("strCantidad: " + strCantidad);
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
                                           System.out.println("strRango: " + strRango);
                                           break;
                                        case 7:
                                           strResponsable = cell.getStringCellValue().trim();
                                           System.out.println("strResponsable: " + strResponsable);
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
                                            System.out.println("strFechaVencimiento: " + strFechaVencimiento);
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
                                            System.out.println("strFechaCompra: " + strFechaCompra);
                                            break;
                                        case 11:
                                           strObs = cell.getStringCellValue().trim();
                                           System.out.println("strObs: " + strObs);
                                           break;                                            
                                    }                                   
                               }// Fin del iterador de celdas.                                
                              
                                // Validaciones para notificación.                  
                               
                               if (!strFechaVencimiento.equals("")) {
                                   System.out.println("strInsumo: " + strInsumo);
                                   System.out.println("strLote: " + strLote);                                                                           
                                                                                                                  
                                    dtFechaVencimiento=  null;

                                     try{
                                         dtFechaVencimiento = sdf.parse(strFechaVencimiento);
                                     }catch(ParseException pe){
                                         new GIDaoException("Se generó un error parseando la fecha de vencimiento del insumo", pe);
                                     }

                                    lgDiasDiferencia= (Long)(funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaActual, dtFechaVencimiento));
                                     System.out.println("lgDiasDiferencia: " + lgDiasDiferencia);

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
        
        new GIDaoException("Finalizando tarea NotificarControlInsumos");        
        
    }
}
