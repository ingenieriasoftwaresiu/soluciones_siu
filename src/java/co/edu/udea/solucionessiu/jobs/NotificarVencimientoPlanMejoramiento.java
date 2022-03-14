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
import co.edu.udea.solucionessiu.dto.Accion;
import co.edu.udea.solucionessiu.dto.Correccion;
import co.edu.udea.solucionessiu.dto.Eficacia;
import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.dto.RegistroPlanMejoramiento;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StreamCorruptedException;
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
public class NotificarVencimientoPlanMejoramiento implements Job {
    
     private FileInputStream fis;

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
               
        /*
            IMPORTANTE: Esta clase difiere de la clase PruebaNotificarVencimientoPlanMejoramiento.java en el método cerrarArchivo() y la variable de la FileInputStream fis. NO reemplazar directamente el contenido!.
        */
        
        new GIDaoException("Iniciando tarea NotificarVencimientoPlanMejoramiento");
        
        String strFechaActual, strCodigoNotificacion, strRutaArchivo, strNomHoja, strCodigoRegistro, strProceso, strFuente, strFechaRegistro, strResponsableCorrecion, strPlazoCorreccion, strFechaSegCorrecion1;
        String strFechaSegCorrecion2, strEstadoCorrecion, strTipoAccion, strResponsableAccion, strPlazoAccion, strFechaSegAccion1, strFechaSegAccion2, strFechaSegAccion3, strFechaSegAccion4, strEstadoAccion;
        String strEstadoEficacia, strFechaRevisionEficacia, strAccionNotificarCorreccion, strAccionNotificarAccion, strAccionNotificarEficacia, strNroDiasDespues;
        Integer intFila, intFilaInicio, intColumna, intDocsAlertados;
        Long lgDiasDiferenciaCorrecion, lgDiasDiferenciaAccion, lgDiasDiferenciaEficacia, lgDiasNotificar;
        Double dblCodigo;
        Date dtFechaActual, dtFechaRegistro, dtPlazoCorreccion, dtFechaSegCorrecion1, dtFechaSegCorrecion2, dtPlazoAccion, dtFechaSegAccion1, dtFechaSegAccion2, dtFechaSegAccion3, dtFechaSegAccion4;
        Date dtFechaRevisionEficacia;
        Notificacion notificacion = null;
        Row row = null;
        Cell cell = null;
        File myFile = null;
        FileInputStream fis = null;
        XSSFWorkbook myWorkBook = null;
        RegistroPlanMejoramiento registro = null;
        Correccion correccion = null;
        Accion accion = null;
        Eficacia eficacia = null;
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        NotificacionMailSiuWebDAO notificacionMailDAO = new NotificacionMailSiuWebDAOImpl();
        
        strCodigoNotificacion = "PLANMEJ";
        intFilaInicio = 6;
        dtFechaActual = null;
        lgDiasNotificar = 0L;
        strFechaActual = null;
        strRutaArchivo = "";
        intDocsAlertados = 0;
                
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
                                dblCodigo = 0D;
                                strCodigoRegistro = "";
                                strProceso = "";
                                strFuente = "";
                                strFechaRegistro = "";
                                strResponsableCorrecion = "";
                                strPlazoCorreccion = "";
                                dtPlazoCorreccion = null;
                                strFechaSegCorrecion1 = "";
                                dtFechaSegCorrecion1 = null;
                                strFechaSegCorrecion2 = "";
                                dtFechaSegCorrecion2 = null;
                                strEstadoCorrecion = "";
                                strTipoAccion = "";
                                strResponsableAccion = "";
                                strPlazoAccion = "";
                                dtPlazoAccion = null;
                                strFechaSegAccion1 = "";
                                strFechaSegAccion2 = "";
                                strFechaSegAccion3 = "";
                                strFechaSegAccion4 = "";
                                dtFechaSegAccion1 = null;
                                dtFechaSegAccion2 = null;
                                dtFechaSegAccion3 = null;
                                dtFechaSegAccion4 = null;
                                strEstadoAccion = "";
                                strEstadoEficacia = "";
                                strFechaRevisionEficacia = "";
                                dtFechaRevisionEficacia = null;
                                strAccionNotificarCorreccion = "";
                                strAccionNotificarAccion = "";
                                strAccionNotificarEficacia = "";
                                cell = null;
                                registro = null;
                                correccion = null;
                                accion = null;
                                eficacia = null;
                                lgDiasDiferenciaCorrecion = 0L;
                                lgDiasDiferenciaAccion = 0L;
                                lgDiasDiferenciaEficacia = 0L;

                                while (cellIterator.hasNext()) {

                                    cell = cellIterator.next();
                                    intColumna = cell.getColumnIndex();              
                                    
                                     switch(intColumna){
                                            case 0:
                                                dblCodigo = cell.getNumericCellValue();          
                                                strCodigoRegistro = String.valueOf(dblCodigo.intValue());                                                
                                                break;
                                            case 1:
                                                strProceso = cell.getStringCellValue();
                                                break;
                                            case 7:
                                                strFuente = cell.getStringCellValue();
                                                break;
                                            case 8:
                                                if (cell.getDateCellValue() != null){
                                                    dtFechaRegistro = cell.getDateCellValue();
                                                    strFechaRegistro = funcionesComunesDAO.convertirFechaLarga(dtFechaRegistro.toString());                                                                
                                                }else{
                                                    dtFechaRegistro = null;
                                                    strFechaRegistro = "";
                                                }                                               
                                                break;
                                             case 10:
                                                strResponsableCorrecion = cell.getStringCellValue();
                                                break;
                                             case 11:
                                                  if (cell.getCellType() == Cell.CELL_TYPE_STRING){
                                                     strPlazoCorreccion = "N/A";
                                                 }else{
                                                       if (cell.getDateCellValue() != null){
                                                            dtPlazoCorreccion =  cell.getDateCellValue();
                                                            strPlazoCorreccion = funcionesComunesDAO.convertirFechaLarga(dtPlazoCorreccion.toString());       
                                                         }else{
                                                             dtPlazoCorreccion = null;
                                                             strPlazoCorreccion = "";
                                                         }
                                                  }                                                
                                                break;
                                             case 12:
                                                 if (cell.getCellType() == Cell.CELL_TYPE_STRING){
                                                     strFechaSegCorrecion1 = "N/A";
                                                 }else{
                                                     if (cell.getDateCellValue() != null){
                                                        dtFechaSegCorrecion1 =  cell.getDateCellValue();
                                                        strFechaSegCorrecion1 = funcionesComunesDAO.convertirFechaLarga(dtFechaSegCorrecion1.toString());       
                                                     }else{
                                                         dtFechaSegCorrecion1 = null;
                                                         strFechaSegCorrecion1 = "";
                                                     }
                                                 }                                                 
                                                break;
                                             case 14:
                                                 if (cell.getCellType() == Cell.CELL_TYPE_STRING){
                                                     strFechaSegCorrecion2 = "N/A";
                                                 }else{
                                                     if (cell.getDateCellValue() != null){
                                                        dtFechaSegCorrecion2 =  cell.getDateCellValue();
                                                        strFechaSegCorrecion2 = funcionesComunesDAO.convertirFechaLarga(dtFechaSegCorrecion2.toString());       
                                                     }else{
                                                         dtFechaSegCorrecion2 = null;
                                                         strFechaSegCorrecion2 = "";
                                                     }
                                                 }
                                                                                                  
                                                break;
                                             case 16:
                                                strEstadoCorrecion = cell.getStringCellValue();
                                                break;
                                             case 22:
                                                strTipoAccion = cell.getStringCellValue();
                                                break;
                                             case 24:
                                                strResponsableAccion = cell.getStringCellValue();
                                                break;
                                              case 25:
                                                  if (cell.getCellType() == Cell.CELL_TYPE_STRING){
                                                     strPlazoAccion = "N/A";
                                                 }else{
                                                       if (cell.getDateCellValue() != null){
                                                            dtPlazoAccion =  cell.getDateCellValue();
                                                            strPlazoAccion = funcionesComunesDAO.convertirFechaLarga(dtPlazoAccion.toString());       
                                                         }else{
                                                             dtPlazoAccion = null;
                                                             strPlazoAccion = "";
                                                         }
                                                  }                                                
                                                break;
                                             case 27:
                                                    if (cell.getCellType() == Cell.CELL_TYPE_STRING){
                                                        strFechaSegAccion1 = "N/A";
                                                    }else{
                                                        if (cell.getDateCellValue() != null){
                                                           dtFechaSegAccion1 =  cell.getDateCellValue();
                                                           strFechaSegAccion1 = funcionesComunesDAO.convertirFechaLarga(dtFechaSegAccion1.toString());       
                                                        }else{
                                                            dtFechaSegAccion1 = null;
                                                            strFechaSegAccion1 = "";
                                                        }
                                                    }                                                 
                                                   break;
                                               case 29:
                                                    if (cell.getCellType() == Cell.CELL_TYPE_STRING){
                                                        strFechaSegAccion2 = "N/A";
                                                    }else{
                                                        if (cell.getDateCellValue() != null){
                                                           dtFechaSegAccion2 =  cell.getDateCellValue();
                                                           strFechaSegAccion2 = funcionesComunesDAO.convertirFechaLarga(dtFechaSegAccion2.toString());       
                                                        }else{
                                                            dtFechaSegAccion2 = null;
                                                            strFechaSegAccion2 = "";
                                                        }
                                                    }                                                 
                                                   break;
                                                case 31:
                                                    if (cell.getCellType() == Cell.CELL_TYPE_STRING){
                                                        strFechaSegAccion3 = "N/A";
                                                    }else{
                                                        if (cell.getDateCellValue() != null){
                                                           dtFechaSegAccion3 =  cell.getDateCellValue();
                                                           strFechaSegAccion3 = funcionesComunesDAO.convertirFechaLarga(dtFechaSegAccion3.toString());       
                                                        }else{
                                                            dtFechaSegAccion3 = null;
                                                            strFechaSegAccion3 = "";
                                                        }
                                                    }                                                 
                                                   break;
                                                case 33:
                                                    if (cell.getCellType() == Cell.CELL_TYPE_STRING){
                                                        strFechaSegAccion4 = "N/A";
                                                    }else{
                                                        if (cell.getDateCellValue() != null){
                                                           dtFechaSegAccion4 =  cell.getDateCellValue();
                                                           strFechaSegAccion4 = funcionesComunesDAO.convertirFechaLarga(dtFechaSegAccion4.toString());       
                                                        }else{
                                                            dtFechaSegAccion4 = null;
                                                            strFechaSegAccion4 = "";
                                                        }
                                                    }                                                 
                                                   break;
                                                case 35:
                                                    strEstadoAccion = cell.getStringCellValue();
                                                    break;
                                                case 38:
                                                    strEstadoEficacia = cell.getStringCellValue();
                                                    break;
                                                case 39:
                                                    if (cell.getCellType() == Cell.CELL_TYPE_FORMULA){
                                                        try{
                                                            if (cell.getStringCellValue() != null){
                                                                strFechaRevisionEficacia = "Pendiente";
                                                            }
                                                        }catch(IllegalStateException ise){
                                                            if (cell.getDateCellValue() != null){
                                                               dtFechaRevisionEficacia =  cell.getDateCellValue();
                                                               strFechaRevisionEficacia = funcionesComunesDAO.convertirFechaLarga(dtFechaRevisionEficacia.toString());                                                                   
                                                            }else{
                                                                dtFechaRevisionEficacia = null;
                                                                strFechaRevisionEficacia = "";
                                                            }  
                                                        }                                                       
                                                    }else{       
                                                        if (cell.getCellType() == Cell.CELL_TYPE_STRING){
                                                            strFechaRevisionEficacia = "N/A";
                                                        }
                                                    }         
                                                    break;
                                     }
                                                                        
                                } // Fin del iterador de celdas.
                                
                                // Validaciones para notificación.
                                
                                registro = new RegistroPlanMejoramiento();
                                registro.setNroRegistro(strCodigoRegistro);
                                registro.setNombreProceso(strProceso);
                                registro.setFuente(strFuente);
                                
                                dtFechaRegistro =  null;
                                if (!strFechaRegistro.equals("")){                                    
                                    try{
                                        dtFechaRegistro = sdf.parse(strFechaRegistro);
                                    }catch(ParseException pe){
                                        new GIDaoException("Se generó un error parseando la fecha de registro", pe);
                                        cerrarArchivo();
                                    }
                                }
                                
                                registro.setFechaReporte(dtFechaRegistro);
                                
                                if (strEstadoCorrecion.equals("Abierta")){                                                                                                            
                                    if (!strPlazoCorreccion.equals("")){
                                        
                                        dtPlazoCorreccion=  null;

                                        try{
                                            dtPlazoCorreccion = sdf.parse(strPlazoCorreccion);
                                        }catch(ParseException pe){
                                            new GIDaoException("Se generó un error parseando la fecha de plazo de la corrección", pe);
                                            cerrarArchivo();
                                        }
                                        
                                        lgDiasDiferenciaCorrecion = (Long)(funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaActual, dtPlazoCorreccion));
                                                                                
                                        if ((lgDiasDiferenciaCorrecion.toString().equals("0")) || (lgDiasDiferenciaCorrecion.toString().equals(lgDiasNotificar.toString())) || (lgDiasDiferenciaCorrecion.toString().equals(strNroDiasDespues))){
                                            
                                            if (lgDiasDiferenciaCorrecion.toString().equals("0")){
                                                strAccionNotificarCorreccion = "CORRECCCIONDIAVENC";
                                            }
                                            
                                            if (lgDiasDiferenciaCorrecion.toString().equals(lgDiasNotificar.toString())){
                                                 strAccionNotificarCorreccion = "CORRECCCIONAVENCER";
                                            }
                                            
                                            if (lgDiasDiferenciaCorrecion.toString().equals(strNroDiasDespues)){
                                                strAccionNotificarCorreccion = "CORRECCCIONVENCIDA";
                                            }          
                                            
                                            correccion = new Correccion();
                                            correccion.setResponsable(strResponsableCorrecion);
                                            correccion.setPlazo(dtPlazoCorreccion);

                                            dtFechaSegCorrecion1 = null;
                                            if ((!strFechaSegCorrecion1.equals("")) && (!strFechaSegCorrecion1.equals("N/A"))){                                            
                                                try{
                                                    dtFechaSegCorrecion1 = sdf.parse(strFechaSegCorrecion1);
                                                }catch(ParseException pe){
                                                    new GIDaoException("Se generó un error parseando la fecha de seguimiento 1 de la corrección", pe);
                                                    cerrarArchivo();
                                                }                                            
                                            }
                                            
                                            dtFechaSegCorrecion2 = null;
                                            if ((!strFechaSegCorrecion2.equals("")) && (!strFechaSegCorrecion2.equals("N/A"))){
                                                try{
                                                    dtFechaSegCorrecion2 = sdf.parse(strFechaSegCorrecion2);
                                                }catch(ParseException pe){
                                                    new GIDaoException("Se generó un error parseando la fecha de seguimiento 2 de la corrección", pe);
                                                    cerrarArchivo();
                                                }                                            
                                            }                                       
                                            
                                            correccion.setFechaSeguimiento1(dtFechaSegCorrecion1);
                                            correccion.setFechaSeguimiento2(dtFechaSegCorrecion2);
                                            correccion.setEstado(strEstadoCorrecion);
                                            correccion.setAccionNotificar(strAccionNotificarCorreccion); 
                                        }                                                                                                                                                              
                                        
                                    }                                                                                                    
                                }                                        
                                
                                registro.setCorreccion(correccion);
                                
                                if ((strEstadoAccion.equals("Abierta")) || (strEstadoAccion.equals("Cerrada"))){
                                    
                                    accion = new Accion();
                                    accion.setTipoAccion(strTipoAccion);
                                    accion.setResponsable(strResponsableAccion);
                                    accion.setPlazo(dtPlazoAccion);
                                    
                                    if (strEstadoAccion.equals("Abierta")){
                                        if (!strPlazoAccion.equals("") && !strPlazoAccion.equals("N/A") && !strPlazoAccion.equals("No aplica")){
                                                                                        
                                            dtPlazoAccion =  null;

                                            try{
                                                dtPlazoAccion = sdf.parse(strPlazoAccion);
                                            }catch(ParseException pe){
                                                new GIDaoException("Se generó un error parseando la fecha de plazo de la acción #" + strCodigoRegistro, pe);
                                                cerrarArchivo();
                                            }

                                            lgDiasDiferenciaAccion = (Long)(funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaActual, dtPlazoAccion));
                                            
                                            if ((lgDiasDiferenciaAccion.toString().equals("0")) || (lgDiasDiferenciaAccion.toString().equals(lgDiasNotificar.toString())) || (lgDiasDiferenciaAccion.toString().equals(strNroDiasDespues))){

                                                if (lgDiasDiferenciaAccion.toString().equals("0")){
                                                    strAccionNotificarAccion = "ACCIONDIAVENC";
                                                }

                                                if (lgDiasDiferenciaAccion.toString().equals(lgDiasNotificar.toString())){
                                                     strAccionNotificarAccion = "ACCIONAVENCER";
                                                }

                                                if (lgDiasDiferenciaAccion.toString().equals(strNroDiasDespues)){
                                                    strAccionNotificarAccion = "ACCIONVENCIDA";
                                                }              
                                               
                                                dtFechaSegAccion1 = null;
                                                if ((!strFechaSegAccion1.equals("")) && (!strFechaSegAccion1.equals("N/A"))){                                            
                                                    try{
                                                        dtFechaSegAccion1 = sdf.parse(strFechaSegAccion1);
                                                    }catch(ParseException pe){
                                                        new GIDaoException("Se generó un error parseando la fecha de seguimiento 1 de la acción", pe);
                                                        cerrarArchivo();
                                                    }                                            
                                                }

                                                dtFechaSegAccion2 = null;
                                                if ((!strFechaSegAccion2.equals("")) && (!strFechaSegAccion2.equals("N/A"))){
                                                    try{
                                                        dtFechaSegAccion2 = sdf.parse(strFechaSegAccion2);
                                                    }catch(ParseException pe){
                                                        new GIDaoException("Se generó un error parseando la fecha de seguimiento 2 de la acción", pe);
                                                        cerrarArchivo();
                                                    }                                            
                                                }

                                                dtFechaSegAccion3 = null;
                                                if ((!strFechaSegAccion3.equals("")) && (!strFechaSegAccion3.equals("N/A"))){
                                                    try{
                                                        dtFechaSegAccion3 = sdf.parse(strFechaSegAccion3);
                                                    }catch(ParseException pe){
                                                        new GIDaoException("Se generó un error parseando la fecha de seguimiento 3 de la acción", pe);
                                                        cerrarArchivo();
                                                    }                                            
                                                }

                                                dtFechaSegAccion4 = null;
                                                if ((!strFechaSegAccion4.equals("")) && (!strFechaSegAccion4.equals("N/A"))){
                                                    try{
                                                        dtFechaSegAccion4 = sdf.parse(strFechaSegAccion4);
                                                    }catch(ParseException pe){
                                                        new GIDaoException("Se generó un error parseando la fecha de seguimiento 4 de la acción", pe);
                                                        cerrarArchivo();
                                                    }                                            
                                                }

                                                accion.setFechaSeguimiento1(dtFechaSegAccion1);
                                                accion.setFechaSeguimiento2(dtFechaSegAccion2);
                                                accion.setFechaSeguimiento3(dtFechaSegAccion3);
                                                accion.setFechaSeguimiento4(dtFechaSegAccion4);
                                                accion.setEstado(strEstadoAccion);
                                                accion.setAccionNotificar(strAccionNotificarAccion);                                            
                                            }else{
                                                accion=null;
                                            }                                        
                                        }else{
                                            accion=null;
                                        }
                                    }else{                                    
                                        if (strEstadoEficacia.equals("Pendiente")){
                                            if ((!strFechaRevisionEficacia.equals("")) && (!strFechaRevisionEficacia.equals("Pendiente"))){
                                                                                                
                                                 dtFechaRevisionEficacia =  null;

                                                try{
                                                    dtFechaRevisionEficacia = sdf.parse(strFechaRevisionEficacia);
                                                }catch(ParseException pe){
                                                    new GIDaoException("Se generó un error parseando la fecha de revisión de eficacia", pe);
                                                    cerrarArchivo();
                                                }

                                                lgDiasDiferenciaEficacia = (Long)(funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaActual, dtFechaRevisionEficacia));
                                                
                                                if ((lgDiasDiferenciaEficacia.toString().equals("0")) || (lgDiasDiferenciaEficacia.toString().equals(lgDiasNotificar.toString())) || (lgDiasDiferenciaEficacia.toString().equals(strNroDiasDespues))){

                                                    if (lgDiasDiferenciaEficacia.toString().equals("0")){
                                                        strAccionNotificarEficacia = "EFICACIADIAVENC";
                                                    }

                                                    if (lgDiasDiferenciaEficacia.toString().equals(lgDiasNotificar.toString())){
                                                         strAccionNotificarEficacia = "EFICACIAAVENCER";
                                                    }

                                                    if (lgDiasDiferenciaEficacia.toString().equals(strNroDiasDespues)){
                                                        strAccionNotificarEficacia = "EFICACIAVENCIDA";
                                                    }                                                                                                           

                                                    eficacia = new Eficacia();
                                                    eficacia.setEstado(strEstadoEficacia);
                                                    eficacia.setFechaPRevision(dtFechaRevisionEficacia);
                                                    eficacia.setAccionNotificar(strAccionNotificarEficacia);                                            
                                                }else{                                                    
                                                     accion = null;
                                                }
                                            }else{                                                
                                                accion = null;
                                            }
                                        }else{                                            
                                            accion = null;
                                        }                                                                        
                                    }
                                }                                                                                               
                                
                                registro.setAccion(accion);                                                     
                                registro.setEficacia(eficacia);
                                
                                if ((registro.getCorreccion() != null) || (registro.getAccion() != null) || (registro.getEficacia() != null)){
                                   
                                    // Envío de notificación
                                    
                                    try{
                                        notificacionMailDAO.notificarVencimientoRegistroPlanMejoramiento(registro);
                                        
                                         if ((registro.getCorreccion() != null) && (registro.getAccion() != null)){
                                             intDocsAlertados = intDocsAlertados + 2;
                                         }else{
                                             intDocsAlertados++;
                                         }                                                                                 
                                    }catch(GIDaoException g){
                                        new GIDaoException("Se generó un error enviando la notificación para el registro del plan de mejoramiento con código " + strCodigoRegistro,g);
                                        cerrarArchivo();
                                    }                                                                
                                }                                
                                
                                registro = null;
                                
                            }                            
                            
                        } // Fin del iterador de registros.                  
                        
                        new GIDaoException("Total de documentos alertados: " + intDocsAlertados);

                        row = null;
                        strNomHoja = "";
                        intDocsAlertados = 0;
                    
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
        
        new GIDaoException("Finalizando tarea NotificarVencimientoPlanMejoramiento");        
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
