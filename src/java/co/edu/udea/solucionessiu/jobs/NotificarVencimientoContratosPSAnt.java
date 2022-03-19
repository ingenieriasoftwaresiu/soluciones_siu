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
import co.edu.udea.solucionessiu.dao.NotificacionMailSiuWebDAO;

/**
 *
 * @author jorge.correa
 */
public class NotificarVencimientoContratosPSAnt  implements Job{
    
    private FileInputStream fis;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        
        new GIDaoException("Iniciando tarea NotificarVencimientoContratosPSAnt");
        
        String strCodigoNotificacion, strFechaActual, strRutaArchivo, strNroDiasDespues, strNomHoja, strTipoSolicitud, strNroContrato, strGrupo, strContratista, strFechaFinalizacion;
        String strTipoContrato, strAccionNotificar;
        Integer intFila, intFilaInicio, intColumna, intRegsAlertados;
        Long lgDiasNotificar, lgDiasDiferencia;
        Date dtFechaActual, dtFechaFinalizacion;
        Notificacion notificacion = null;
        Row row = null;
        Cell cell = null;
        File myFile = null;
        XSSFWorkbook myWorkBook = null;
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        NotificacionMailSiuWebDAO notificacionMailDAO = new NotificacionMailSiuWebDAOImpl();
        Contrato contrato = null;
        
        strCodigoNotificacion = "CONTRATOSPSANT";
        intFilaInicio = 7;
        dtFechaActual = null;
        lgDiasNotificar = 0L;
        strFechaActual = null;
        strRutaArchivo = "";
        intRegsAlertados = 0;
        strTipoContrato = "Prestación servicios personales";
        
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
            strNroDiasDespues = "-" + String.valueOf(notificacion.getDiasDespuesNotificar());
            
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
                                strGrupo = null;
                                strTipoSolicitud = null;
                                strNroContrato = null;
                                strGrupo = null;
                                strContratista = null;
                                strFechaFinalizacion = null;
                                strAccionNotificar = null;
                                
                                while (cellIterator.hasNext()) {

                                    cell = cellIterator.next();
                                    intColumna = cell.getColumnIndex(); 
                                    
                                    switch(intColumna){
                                        case 2:
                                            strGrupo = cell.getStringCellValue().trim();
                                            break;
                                        case 10:
                                            strTipoSolicitud = cell.getStringCellValue().trim();
                                            break;
                                        case 11:
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
                                        case 12:
                                            strContratista = cell.getStringCellValue().trim();
                                            break;
                                        case 28:
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
                                    
                                } // Fin del iterador de celdas.
                                
                                // Validaciones para notificación.
                                
                                if (strTipoSolicitud != null && !strTipoSolicitud.equals("")){
                                    if (strTipoSolicitud.equals(strTipoContrato)){                       
                                        if (strFechaFinalizacion != null){
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
                                                     contrato.setTipoContrato("PS");
                                                     contrato.setGrupo(strGrupo);
                                                     contrato.setContratista(strContratista);
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
                                    }           
                                }                    
                                
                                contrato = null;
                             }                            
                            
                        } // Fin del iterador de registros.
                    
                    new GIDaoException("Total de contratos alertados: " + intRegsAlertados);

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
        
        new GIDaoException("Finalizando tarea NotificarVencimientoContratosPSAnt");    
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
