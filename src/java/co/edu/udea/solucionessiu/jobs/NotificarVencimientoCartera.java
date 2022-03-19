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
import co.edu.udea.solucionessiu.dto.Cartera;
import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import co.edu.udea.solucionessiu.dao.NotificacionMailSiuWebDAO;

/**
 *
 * @author jorge.correaj
 */
public class NotificarVencimientoCartera implements Job{
    
    private FileInputStream fis;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        
        new GIDaoException("Iniciando tarea NotificarVencimientoCartera");
        
        String strCodigoNotificacion, strRutaArchivo, strNomHoja, strNroFactura, strDiasCartera;
        Integer intFila, intFilaInicio, intColumna, intRegsAlertados, intDiasCartera;   
        Long lgDiasNotificar, lgDiasNotificarMas;
        Notificacion notificacion = null;
        Row row = null;
        Cell cell = null;
        File myFile = null;
        FileInputStream fis = null;
        XSSFWorkbook myWorkBook = null;
        Cartera cartera = null;
        List<Cartera> carteras = new ArrayList<Cartera>();
                
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        NotificacionMailSiuWebDAO notificacionMailDAO = new NotificacionMailSiuWebDAOImpl();
        
        
        strCodigoNotificacion = "NOTIFVENCCARTERA";
        intFilaInicio = 7;
        lgDiasNotificar = 0L;     
        strRutaArchivo = "";
        intRegsAlertados = 0;
                                    
        try{
            notificacion =notificacionDAO.obtenerUnoSiuWeb(strCodigoNotificacion);
        }catch(GIDaoException gde){
            new GIDaoException("Se generó un error recuperando la información de la notificación con código " + strCodigoNotificacion, gde);            
            notificacion = null;
        }
        
        if (notificacion != null){
            strRutaArchivo = notificacion.getRuta().trim();
            lgDiasNotificar = new Long(notificacion.getDiasNotificar());
            lgDiasNotificarMas = lgDiasNotificar * 2;
                        
            try{            
                myFile = new File(strRutaArchivo);
                this.fis = new FileInputStream(myFile);
            }catch(FileNotFoundException fnfe){
                new GIDaoException("Se generó un error cargando el objeto desde el archivo", fnfe);            
                this.fis = null;
            }
            
            if (this.fis != null){
                try{
                    myWorkBook = new XSSFWorkbook (this.fis);
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
                               strNroFactura = "";                             
                               intDiasCartera = 0;
                               strDiasCartera = "";
                               
                               while (cellIterator.hasNext()) {
                                   
                                   cell = cellIterator.next();
                                    intColumna = cell.getColumnIndex(); 
                                    
                                    switch(intColumna){
                                        case 4:
                                            try{
                                                if (cell.getStringCellValue() != null){
                                                    strNroFactura = cell.getStringCellValue();
                                                }
                                            }catch(IllegalStateException ise){
                                                if (cell.getNumericCellValue() != 0){
                                                    strNroFactura = funcionesComunesDAO.convertirNotacionEACadena(cell.getNumericCellValue());                                              
                                                }else{                    
                                                    strNroFactura = "-";
                                                }         
                                            }                                      
                                            break;
                                        case 13:
                                            try{
                                                if (cell.getStringCellValue() != null){
                                                    strDiasCartera = cell.getStringCellValue();
                                                }
                                            }catch(IllegalStateException ise){
                                                if (cell.getNumericCellValue() != 0D){
                                                    strDiasCartera = String.valueOf(cell.getNumericCellValue());                                                                                               
                                                }else{                                   
                                                    strDiasCartera = "";
                                                }         
                                            }                    
                                            break;                                           
                                    }                                   
                               }// Fin del iterador de celdas.                                
                              
                                // Validaciones para notificación.                  
                               
                               if (!strDiasCartera.equals("") && !strDiasCartera.equals("0")) {
                                                                                                                                                                                                                       
                                    intDiasCartera = Integer.parseInt(strDiasCartera.replace(".0", ""));                               
                            
                                    if ((intDiasCartera.toString().equals(lgDiasNotificar.toString())) || (intDiasCartera.toString().equals(lgDiasNotificarMas.toString()))){                                            
                         
                                         cartera = new Cartera();
                                         cartera.setNroFactura(strNroFactura);
                                         cartera.setNroDiasCartera(intDiasCartera);
                                         carteras.add(cartera);  
                                         intRegsAlertados++;
                                     }                                                                                                                    
                                }
                               
                                cartera = null;
                               
                            }
                        
                    }// Fin del iterador de registros.
                    
                    if (carteras.size() > 0){                    
                        try{
                            notificacionMailDAO.notificarVencimientoCartera(carteras);                             
                         } catch(GIDaoException g){
                            new GIDaoException("Se generó un error enviando la notificación para las facturas",g);
                            carteras.clear();
                            cerrarArchivo();
                        }
                    }
                    
                    new GIDaoException("Total de facturas alertadas: " + intRegsAlertados);

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
        
        new GIDaoException("Finalizando tarea NotificarVencimientoCartera");  
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
