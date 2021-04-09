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
import co.edu.udea.solucionessiu.dto.Calibracion;
import co.edu.udea.solucionessiu.dto.Mantenimiento;
import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.dto.RegistroPlanCalibracion;
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
public class NotificarVencimientoPlanCalibracionEquiposASIU implements Job {
    
    private FileInputStream fis;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        
        new GIDaoException("Iniciando tarea NotificarVencimientoPlanCalibracionEquiposASIU");
        
        String strCodigoNotificacion, strFechaActual, strRutaArchivo, strNroDiasDespues, strNomHoja, strAreaLab, strEquipo, strMarca, strModelo, strRespCalibracion, strFechaProgCalibracion, strRespMnto;
        String strFechaProgMnto, strFechaEjecCalibracion, strFechaEjecMnto, strAccionNotificarCalibracion, strAccionNotificarMnto;
        Integer intFila, intFilaInicio, intColumna, intRegsAlertados;
        Long lgDiasNotificar, lgDiasDiferenciaCalibracion, lgDiasDiferenciaMnto;
        Date dtFechaActual, dtFechaProgCalibración, dtFechaProgMnto;
        Notificacion notificacion = null;
        Row row = null;
        Cell cell = null;
        File myFile = null;
        FileInputStream fis = null;
        XSSFWorkbook myWorkBook = null;
        RegistroPlanCalibracion registro;
        Calibracion calibracion;
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        NotificacionMailDAO notificacionMailDAO = new NotificacionMailDAOImpl();
        
        strCodigoNotificacion = "PLANCALIBEQASIU";
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
                                
                                 intColumna = 0;
                                 cell = null;
                                 strAreaLab = "";
                                 strEquipo = "";
                                 strMarca = "";
                                 strModelo = "";
                                 strRespCalibracion = "";
                                 dtFechaProgCalibración = null;
                                 strFechaProgCalibracion = "";
                                 strRespMnto = "";
                                 dtFechaProgMnto = null;
                                 strFechaProgMnto = "";
                                 registro = null;
                                 calibracion = null;        
                                 strFechaEjecCalibracion = "";
                                 strFechaEjecMnto = "";
                                 lgDiasDiferenciaCalibracion = 0L;
                                 strAccionNotificarCalibracion = "";
                                 lgDiasDiferenciaMnto = 0L;
                                 strAccionNotificarMnto = "";
                                
                                while (cellIterator.hasNext()) {

                                    cell = cellIterator.next();
                                    intColumna = cell.getColumnIndex(); 
                                    
                                    switch(intColumna){
                                        case 0: 
                                            strAreaLab = cell.getStringCellValue();
                                            break;
                                        case 1: 
                                            strEquipo = cell.getStringCellValue();                                          
                                            break;
                                        case 2: 
                                            strMarca = cell.getStringCellValue();
                                            break;
                                        case 3: 
                                            try{
                                                    if (cell.getStringCellValue() != null){
                                                         strModelo = cell.getStringCellValue();
                                                    }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getNumericCellValue() != 0D){
                                                        strModelo = String.valueOf(cell.getNumericCellValue());
                                                    }else{
                                                        strModelo = "";
                                                    }                                                    
                                                }                                           
                                            break;
                                        case 9: 
                                            strRespCalibracion = cell.getStringCellValue();
                                            break;
                                        case 10:
                                             try{
                                                    if (cell.getStringCellValue() != null){
                                                        strFechaProgCalibracion = "";
                                                    }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getDateCellValue() != null){
                                                        dtFechaProgCalibración = cell.getDateCellValue();
                                                        strFechaProgCalibracion = funcionesComunesDAO.convertirFechaLarga(dtFechaProgCalibración.toString());                                                                
                                                    }else{
                                                        dtFechaProgCalibración = null;
                                                        strFechaProgCalibracion = "";
                                                    }         
                                                }
                                                break;
                                        case 11:
                                            try{
                                                    if (cell.getStringCellValue() != null){
                                                        if (!cell.getStringCellValue().equals("No aplica")){
                                                            strFechaEjecCalibracion = "";
                                                        }else{
                                                            strFechaEjecCalibracion = "N/A";
                                                        }                                                        
                                                    }
                                                }catch(IllegalStateException ise){
                                                    if (cell.getDateCellValue() != null){
                                                        strFechaEjecCalibracion = cell.getDateCellValue().toString();
                                                    }else{
                                                        strFechaEjecCalibracion = "";
                                                    }  
                                            }                           
                                            break;                                        
                                    }
                                    
                                } // Fin del iterador de celdas.
                                
                                // Validaciones para notificación.
                                
                                if ((!strRespCalibracion.equals("No aplica (N/A)")) && (!strRespCalibracion.equals(""))){
                                    
                                    registro = new RegistroPlanCalibracion();
                                    registro.setAreaLab(strAreaLab);
                                    registro.setEquipo(strEquipo);
                                    registro.setMarca(strMarca);
                                    registro.setModelo(strModelo);
                                    
                                    if ((!strRespCalibracion.equals("No aplica (N/A)")) && (!strRespCalibracion.equals(""))){                                                             
                                        if (strFechaEjecCalibracion.equals("")){                                                                            
                                            if ((!strFechaProgCalibracion.equals("")) && (!strFechaProgCalibracion.equals("No aplica"))){
                                                
                                                dtFechaProgCalibración=  null;

                                                try{
                                                    dtFechaProgCalibración = sdf.parse(strFechaProgCalibracion);
                                                }catch(ParseException pe){
                                                    new GIDaoException("Se generó un error parseando la fecha programada de calibración", pe);
                                                    cerrarArchivo();
                                                }
                                                
                                                lgDiasDiferenciaCalibracion = (Long)(funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaActual, dtFechaProgCalibración));
                                                                                                
                                                if ((lgDiasDiferenciaCalibracion.toString().equals("0")) || (lgDiasDiferenciaCalibracion.toString().equals(lgDiasNotificar.toString())) || (lgDiasDiferenciaCalibracion.toString().equals(strNroDiasDespues))){
                                                    
                                                    if (lgDiasDiferenciaCalibracion.toString().equals("0")){
                                                        strAccionNotificarCalibracion = "CALIBRACIONDIAVENC";
                                                    }

                                                    if (lgDiasDiferenciaCalibracion.toString().equals(lgDiasNotificar.toString())){
                                                         strAccionNotificarCalibracion = "CALIBRACIONAVENCER";
                                                    }

                                                    if (lgDiasDiferenciaCalibracion.toString().equals(strNroDiasDespues)){
                                                        strAccionNotificarCalibracion = "CALIBRACIONVENCIDA";
                                                    }
                                                    
                                                    calibracion = new Calibracion();
                                                    calibracion.setResponsable(strRespCalibracion);
                                                    calibracion.setFechaProgramada(dtFechaProgCalibración);
                                                    calibracion.setAccionNotificar(strAccionNotificarCalibracion);
                                                }
                                            }                                            
                                        }                                        
                                    }
                                    
                                    registro.setCalibracion(calibracion);                                                                        
                                    
                                    if (registro.getCalibracion() != null){
                                        
                                          // Envío de notificación
                                    
                                        try{
                                            notificacionMailDAO.notificarVencimientoRegistroPlanCalibEqASIU(registro);

                                             if (registro.getCalibracion() != null){
                                                 intRegsAlertados = intRegsAlertados + 2;
                                             }else{
                                                 intRegsAlertados++;
                                             }                                                                                 
                                        }catch(GIDaoException g){
                                            new GIDaoException("Se generó un error enviando la notificación para el registro del plan de calibración del equipo " + strEquipo,g);
                                            cerrarArchivo();
                                        }                                          
                                    }                                    
                                }
                                
                                registro = null;
                                
                            }                                                                                   
                        } // Fin del iterador de registros.
                    
                    new GIDaoException("Total de documentos alertados: " + intRegsAlertados);

                    row = null;
                    strNomHoja = "";
                    intRegsAlertados = 0;
                    
                }else{
                    new GIDaoException("El objeto de Excel del archivo es nulo");
                    cerrarArchivo();
                }
                
                cerrarArchivo();     
            }else{
                new GIDaoException("El objeto del archivo es nulo");
            }
            
        }else{
            new GIDaoException("El objeto de notificación es nulo");
        }
        
        new GIDaoException("Finalizando tarea NotificarVencimientoPlanCalibracionEquiposASIU");
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
