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
import co.edu.udea.solucionessiu.dto.Documento;
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

/**
 *
 * @author jorge.correa
 */
public class PruebaNotificarVencimientoDocumentos {
    
    public static void main(String args[]) throws IOException{
        
        new GIDaoException("Iniciando tarea NotificarVencimientoDocumentos");
        
        String strFechaActual, strCodigoNotificacion, strRutaArchivo, strProceso, strCodigo, strNomDoc, strEliminado, strFechaAprobación, strCoordinacion;
        String strNomHoja, strTipoDocumento, strEvaluacionVigencia, strDiasDespuesNotificar;
        Date dtFechaActual, dtFechaAprobacion;
        Long lgDiasDiferencia, lgDiasNotificar, lgDiferencia;
        Notificacion notificacion = null;
        Documento documento = null;
        Row row = null;
        Cell cell = null;
        File myFile = null;
        FileInputStream fis = null;
        XSSFWorkbook myWorkBook = null;
        Integer intFila, intFilaInicio, intColumna, intLongitud, intDocsAlertados;
        String[] sheets = {"Listado de servicios", "Listado de documentos", "CMI"};
        intLongitud = sheets.length;
                
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        NotificacionMailDAO notificacionMailDAO = new NotificacionMailDAOImpl();
        
        strCodigoNotificacion = "LISTMAEST";
        intFilaInicio = 6;
        intFila = 0;
        intColumna = 0;
        strProceso = "";
        strCodigo = "";
        strNomDoc = "";
        strFechaAprobación = "";
        strEliminado = "";        
        lgDiasDiferencia = 0L;
        dtFechaActual = null;
        lgDiasNotificar = 0L;
        strCoordinacion = "";
        strNomHoja = "";
        strTipoDocumento = "";
        intDocsAlertados = 0;
        strEvaluacionVigencia = "";
        lgDiferencia = 0L;
               
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
            System.out.println("strRutaArchivo: " + strRutaArchivo);
            strDiasDespuesNotificar = "-" + String.valueOf(notificacion.getDiasDespuesNotificar());
            
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
                    
                    for (int i=0; i<intLongitud; i++){
                        
                        strNomHoja = sheets[i];
                        new GIDaoException("Procesando la hoja: " + strNomHoja);
                        XSSFSheet mySheet = myWorkBook.getSheet(strNomHoja);
                        Iterator<Row> rowIterator = mySheet.iterator();

                        while (rowIterator.hasNext()) {
                            row = rowIterator.next();
                            intFila = row.getRowNum();

                            //System.out.println("intFila: " + intFila);

                            if (intFila >= intFilaInicio){
                                Iterator<Cell> cellIterator = row.cellIterator();

                                while (cellIterator.hasNext()) {

                                    cell = cellIterator.next();
                                    intColumna = cell.getColumnIndex();
                                    
                                    // Obtener las columnas en la hoja Listado de servicios.
                                    
                                    if (strNomHoja.equals("Listado de servicios")){
                                        strTipoDocumento = "FTS";
                                        switch(intColumna){
                                            case 0:
                                                strProceso = cell.getStringCellValue();                                        
                                                break;
                                            case 1:
                                                strCoordinacion = cell.getStringCellValue();                                        
                                                break;
                                           case 2:
                                               strCodigo = cell.getStringCellValue();                                        
                                               break;
                                           case 3:
                                               strNomDoc = cell.getStringCellValue();                                        
                                               break;
                                           case 5:
                                               if (cell.getDateCellValue() != null){
                                                   dtFechaAprobacion = cell.getDateCellValue();
                                                   strFechaAprobación = funcionesComunesDAO.convertirFechaLarga(dtFechaAprobacion.toString());                                                 
                                               }else{
                                                   dtFechaAprobacion = null;
                                                   strFechaAprobación = "";
                                               }                                                                       
                                                break;
                                           case 13:
                                               strEvaluacionVigencia = cell.getStringCellValue();                                               
                                               break;
                                           case 15:
                                               strEliminado = cell.getStringCellValue();
                                               break;
                                        }
                                    }
                                    
                                    // Obtener las columnas en la hoja Listado de documentos.
                                    
                                    if (strNomHoja.equals("Listado de documentos")){
                                        strTipoDocumento = "DOC";
                                        switch(intColumna){
                                            case 0:
                                                strProceso = cell.getStringCellValue();                                        
                                                break;
                                            case 1:
                                                strCoordinacion = cell.getStringCellValue();                                        
                                                break;
                                           case 2:
                                               strCodigo = cell.getStringCellValue();                                        
                                               break;
                                           case 3:
                                               strNomDoc = cell.getStringCellValue();                                        
                                               break;
                                           case 5:
                                               if (cell.getDateCellValue() != null){
                                                   dtFechaAprobacion = cell.getDateCellValue();
                                                   strFechaAprobación = funcionesComunesDAO.convertirFechaLarga(dtFechaAprobacion.toString());                                                 
                                               }else{
                                                   dtFechaAprobacion = null;
                                                   strFechaAprobación = "";
                                               }                                                                       
                                                break;
                                           case 11:
                                               strEvaluacionVigencia = cell.getStringCellValue();                                               
                                               break;
                                           case 13:
                                               strEliminado = cell.getStringCellValue();
                                               break;
                                        }
                                    }
                                    
                                    // Obtener las columnas en la hoja CMI.
                                    
                                    if (strNomHoja.equals("CMI")){
                                        strTipoDocumento = "CMI";
                                        switch(intColumna){
                                            case 0:
                                                strProceso = cell.getStringCellValue();                                        
                                                break;
                                            case 1:
                                                strCoordinacion = cell.getStringCellValue();                                        
                                                break;
                                           case 3:
                                               strCodigo = cell.getStringCellValue();                                        
                                               break;
                                           case 5:
                                               strNomDoc = cell.getStringCellValue();                                        
                                               break;
                                           case 6:
                                               if (cell.getDateCellValue() != null){
                                                   dtFechaAprobacion = cell.getDateCellValue();
                                                   strFechaAprobación = funcionesComunesDAO.convertirFechaLarga(dtFechaAprobacion.toString());                                                 
                                               }else{
                                                   dtFechaAprobacion = null;
                                                   strFechaAprobación = "";
                                               }                                                                       
                                                break;
                                           case 32:
                                               strEvaluacionVigencia = cell.getStringCellValue();                                               
                                               break;
                                           case 34:
                                               strEliminado = cell.getStringCellValue();
                                               break;
                                        }
                                    }
                                                                    
                                } // Fin del iterador de celdas.

                                // Validaciones para notificación.

                                if (((strEliminado == null) || (strEliminado.equals(""))) && (!strEvaluacionVigencia.equals("No aplica"))){
                                    if (!strFechaAprobación.equals("")){

                                        dtFechaAprobacion=  null;

                                        try{
                                            dtFechaAprobacion = sdf.parse(strFechaAprobación);
                                        }catch(ParseException pe){
                                            new GIDaoException("Se generó un error parseando la fecha de aprobación", pe);
                                        }

                                        if (dtFechaAprobacion != null){
                                            lgDiasDiferencia = funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaAprobacion, dtFechaActual);

                                            if (lgDiasDiferencia > 690){
                                                
                                                lgDiferencia = (730 - lgDiasDiferencia);
                                                //System.out.println("lgDiferencia: " + lgDiferencia);
                                                
                                                if ((lgDiferencia.toString().equals(lgDiasNotificar.toString())) || (lgDiferencia.toString().equals("0")) || (lgDiferencia.toString().equals(strDiasDespuesNotificar))){
                                                    documento = new Documento();
                                                    documento.setCodigo(strCodigo);
                                                    documento.setProceso(strProceso);
                                                    documento.setNombre(strNomDoc);
                                                    documento.setTipo(strTipoDocumento);
                                                    documento.setFechaAprobacion(strFechaAprobación);
                                                    documento.setCoordinacion(strCoordinacion);

                                                    if ((lgDiferencia.toString().equals(lgDiasNotificar.toString()))) {
                                                        documento.setVigencia("VIG");
                                                    }
                                                    
                                                    if (lgDiferencia.toString().equals("0")) {
                                                        documento.setVigencia("NEU");
                                                    }

                                                    if (lgDiferencia.toString().equals(strDiasDespuesNotificar)){
                                                        documento.setVigencia("VEN");
                                                    }

                                                    try{                                            
                                                        notificacionMailDAO.notificarVencimientoDocumentos(documento);
                                                        intDocsAlertados++;
                                                    }catch(GIDaoException g){
                                                        new GIDaoException("Se generó un error enviando la notificación para el documento con código " + strCodigo,g);
                                                    }                                        
                                                }
                                            }

                                        }else{
                                            new GIDaoException("La fecha de aprobación para el cálculo es nula");
                                        }
                                    }
                                }

                                intFila = 0;
                                intColumna = 0;
                                strProceso = "";
                                strCodigo = "";
                                strNomDoc = "";
                                strFechaAprobación = "";
                                dtFechaAprobacion = null;
                                strEliminado = "";
                                lgDiasDiferencia = 0L;
                                cell = null;
                                documento = null;
                                strCoordinacion = "";
                                strTipoDocumento = "";
                                lgDiferencia = 0L;

                            }            
                        } // Fin del iterador de registros.                  
                        
                        new GIDaoException("Total de documentos alertados para la hoja " + strNomHoja + ": " + intDocsAlertados);

                        row = null;
                        strNomHoja = "";
                        intDocsAlertados = 0;
                    
                    } // Fin del ciclo for de sheets.
                     
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
        
        new GIDaoException("Finalizando tarea NotificarVencimientoDocumentos");
    }        
    
}