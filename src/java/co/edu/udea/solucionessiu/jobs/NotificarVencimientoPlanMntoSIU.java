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
import co.edu.udea.solucionessiu.dto.EquipoMnto;
import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
 * @author jorge.correaj
 */
public class NotificarVencimientoPlanMntoSIU implements Job{
    
    private FileInputStream fis;
    
    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        
        new GIDaoException("Iniciando tarea NotificarVencimientoPlanMntoSIU");
        
        String strFechaActual, strCodigoNotificacion, strRutaArchivo, strNomHoja, strTipoEquipo, strNomEquipo, strTorre, strPiso, strMesFechaActual, strServicio;
        String strEnero, strFebrero, strMarzo, strAbril, strMayo, strJunio, strJulio, strAgosto, strSeptiembre, strOctubre, strNoviembre, strDiciembre;
        String strCodigosNotificacion[] = {"PLANMNTOSIUINFRAES","PLANMNTOSIUEQINDUS","PLANMNTOSIUEQCIENT","PLANMNTOSIUEQCOMP","PLANMNTOSIUSOFTWARE","PLANMNTOSIUTELCO"};
        String strTemp[] = null;
        Integer intFila, intFilaInicio, intColumna;       
        Double dblTorre, dblPiso;
        Boolean programado; 
        Notificacion notificacion = null;
        Row row = null;
        Cell cell = null;
        File myFile = null;      
        XSSFWorkbook myWorkBook = null;
        XSSFSheet mySheet = null;
        Iterator<Row> rowIterator = null;
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        NotificacionMailSiuWebDAO notificacionMailDAO = new NotificacionMailSiuWebDAOImpl();
        ArrayList<EquipoMnto> equipos = new ArrayList<EquipoMnto>();
        
        strFechaActual = null;
                
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        strFechaActual = funcionesComunesDAO.getFechaActual();                                       
        strTemp = strFechaActual.split("-");
        strMesFechaActual = strTemp[1];
                        
        for(int i=0;i<strCodigosNotificacion.length;i++){            
            
            strCodigoNotificacion = strCodigosNotificacion[i];
            strRutaArchivo = "";           
            notificacion = null;
            myFile = null;
            this.fis = null;
            
            try{
                notificacion = notificacionDAO.obtenerUnoSiuWeb(strCodigoNotificacion);
            }catch(GIDaoException gde){
                new GIDaoException("Se generó un error recuperando la información de la notificación con código " + strCodigoNotificacion, gde);            
                notificacion = null;
            }
            
            if (notificacion != null){
                
                strRutaArchivo = notificacion.getRuta().trim();                                           

                try{            
                    myFile = new File(strRutaArchivo);
                    this.fis = new FileInputStream(myFile);
                }catch(FileNotFoundException fnfe){
                    new GIDaoException("Se generó un error cargando el objeto desde el archivo", fnfe);            
                    this.fis = null;
                }
                
                if (this.fis != null){
                    
                    myWorkBook = null;
                    
                    try{
                        myWorkBook = new XSSFWorkbook (this.fis);
                    }catch(IOException ioe){
                        new GIDaoException("Se generó un error abriendo el objeto de Excel desde el archivo", ioe);
                        cerrarArchivo();
                    }

                    if (myWorkBook != null){
                        
                        strNomHoja = null;
                        mySheet = null;
                        rowIterator = null;
                        
                        strNomHoja = notificacion.getNombreHoja().trim();                        
                        mySheet = myWorkBook.getSheet(strNomHoja);
                        rowIterator = mySheet.iterator();
                        intFilaInicio = 7;
                        
                        while (rowIterator.hasNext()) {
                            
                            intFila = 0;                                   
                            
                            row = rowIterator.next();
                            intFila = row.getRowNum();                            
                            
                            if (intFila >= intFilaInicio){
                               Iterator<Cell> cellIterator = row.cellIterator();                                
                              
                               intColumna = 0;
                               cell = null;                  
                               strTipoEquipo = "";  
                               strNomEquipo = "";
                               strServicio = "";
                               strTorre = "";                               
                               dblTorre = 0d;
                               strPiso = "";
                               dblPiso = 0d;
                               strEnero = "";
                               strFebrero = "";
                               strMarzo = "";
                               strAbril = "";
                               strMayo = "";
                               strJunio = "";
                               strJulio = "";
                               strAgosto = "";
                               strSeptiembre = "";
                               strOctubre = "";
                               strNoviembre = "";
                               strDiciembre = "";
                               programado = false;
                               
                               while (cellIterator.hasNext()) {
                                   
                                    cell = cellIterator.next();
                                    intColumna = cell.getColumnIndex(); 
                                    
                                    switch(intColumna){                    
                                        case 2:
                                                strTipoEquipo = cell.getStringCellValue().trim();                                               
                                            break;
                                        case 3:
                                                strNomEquipo = cell.getStringCellValue().trim();                                               
                                            break;   
                                        case 7:                                                
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strTorre = cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         dblTorre = cell.getNumericCellValue();
                                                         strTorre = String.valueOf(dblTorre.intValue());
                                                     }else{
                                                         strTorre = "*";
                                                     }
                                                 }                                                
                                            break;  
                                        case 8:                                                
                                               try{
                                                    if (cell.getStringCellValue() != null){
                                                        strPiso = cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         dblPiso = cell.getNumericCellValue();
                                                         strPiso = String.valueOf(dblPiso.intValue());
                                                     }else{
                                                         strPiso = "*";
                                                     }
                                                 }                                                 
                                            break;  
                                        case 9:                                                                                                
                                            strServicio = cell.getStringCellValue().trim();                                           
                                            break;
                                        case 28:                                            
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strEnero = cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         strEnero = String.valueOf(cell.getNumericCellValue());                                                         
                                                     }else{
                                                         strPiso = "*";
                                                     }
                                                 }                                                                                               
                                            break;
                                        case 29:                                                
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strFebrero = cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         strFebrero = String.valueOf(cell.getNumericCellValue());                                                         
                                                     }else{
                                                         strFebrero = "*";
                                                     }
                                                 }                                                       
                                            break;
                                        case 30:                                                
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strMarzo = cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         strMarzo = String.valueOf(cell.getNumericCellValue());                                                         
                                                     }else{
                                                         strMarzo = "*";
                                                     }
                                                 }                                                
                                            break;
                                        case 31:
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strAbril = cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         strAbril = String.valueOf(cell.getNumericCellValue());                                                         
                                                     }else{
                                                         strAbril = "*";
                                                     }
                                                 }                                               
                                            break;
                                        case 32:
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strMayo = cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         strMayo = String.valueOf(cell.getNumericCellValue());                                                         
                                                     }else{
                                                         strMayo = "*";
                                                     }
                                                 }                                               
                                            break;
                                        case 33:
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strJunio = cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         strJunio = String.valueOf(cell.getNumericCellValue());                                                         
                                                     }else{
                                                         strJunio = "*";
                                                     }
                                                 }                                                                                                
                                            break;
                                        case 34:
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strJulio = cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         strJulio = String.valueOf(cell.getNumericCellValue());                                                         
                                                     }else{
                                                         strJulio = "*";
                                                     }
                                                 }                                                                                                
                                            break;
                                        case 35:                                            
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strAgosto = cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         strAgosto = String.valueOf(cell.getNumericCellValue());                                                         
                                                     }else{
                                                         strAgosto = "*";
                                                     }
                                                 }                                                                    
                                            break;
                                        case 36:                                            
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strSeptiembre = cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         strSeptiembre = String.valueOf(cell.getNumericCellValue());                                                         
                                                     }else{
                                                         strSeptiembre = "*";
                                                     }
                                                 }                                                                                                
                                            break;
                                        case 37:                                            
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strOctubre = cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         strOctubre = String.valueOf(cell.getNumericCellValue());                                                         
                                                     }else{
                                                         strOctubre = "*";
                                                     }
                                                 }                                                
                                            break;
                                        case 38:                                            
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strNoviembre = cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         strNoviembre = String.valueOf(cell.getNumericCellValue());                                                         
                                                     }else{
                                                         strNoviembre = "*";
                                                     }
                                                 }                                                
                                            break;
                                        case 39:                                            
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strDiciembre = cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         strDiciembre = String.valueOf(cell.getNumericCellValue());                                                         
                                                     }else{
                                                         strDiciembre = "*";
                                                     }
                                                 }                                                
                                            break;                                            
                                        
                                    }                                   
                               }// Fin del iterador de celdas.                                
                              
                                // Validaciones para notificación.                  
                               
                               if (strNomEquipo != null && !strNomEquipo.equals("")){                                   
                                       
                                    if (strMesFechaActual.equals("01") && strEnero.equals("X")){
                                        programado = true;
                                    }
                                    
                                    if (strMesFechaActual.equals("02") && strFebrero.equals("X")){
                                        programado = true;
                                    }
                                    
                                    if (strMesFechaActual.equals("03") && strMarzo.equals("X")){
                                        programado = true;
                                    }
                                    
                                    if (strMesFechaActual.equals("04") && strAbril.equals("X")){
                                        programado = true;
                                    }
                                    
                                    if (strMesFechaActual.equals("05") && strMayo.equals("X")){
                                        programado = true;
                                    }
                                    
                                    if (strMesFechaActual.equals("06") && strJunio.equals("X")){
                                        programado = true;
                                    }
                                    
                                    if (strMesFechaActual.equals("07") && strJulio.equals("X")){
                                        programado = true;
                                    }
                                    
                                    if (strMesFechaActual.equals("08") && strAgosto.equals("X")){
                                        programado = true;
                                    }
                                    
                                    if (strMesFechaActual.equals("09") && strSeptiembre.equals("X")){
                                        programado = true;
                                    }
                                    
                                    if (strMesFechaActual.equals("10") && strOctubre.equals("X")){
                                        programado = true;
                                    }
                                    
                                    if (strMesFechaActual.equals("11") && strNoviembre.equals("X")){
                                        programado = true;
                                    }
                                    
                                    if (strMesFechaActual.equals("12") && strDiciembre.equals("X")){
                                        programado = true;
                                    }
                                    
                                    if (programado == true){
                                        
                                        EquipoMnto equipo = new EquipoMnto();
                                        equipo.setTipoEquipo(strTipoEquipo);
                                        equipo.setNombreEquipo(strNomEquipo);
                                        equipo.setServicio(strServicio);
                                        equipo.setTorre(strTorre);
                                        equipo.setPiso(strPiso);                       
                                        equipos.add(equipo);
                                        equipo = null;
                                    }                                                                                      
                                }                            
                            }                                                                            
                        }
                        
                        if (equipos.size() > 0){
                            
                            try{
                                new GIDaoException("Notificando " + equipos.size() + " equipos al código de notificación " + strCodigoNotificacion);
                                notificacionMailDAO.notificarEquiposMnto(equipos, strCodigoNotificacion);
                            }catch(GIDaoException gi){
                                new GIDaoException("Se generó un error al enviar la notificación con código " + strCodigoNotificacion, gi);
                            }
                            
                            equipos.clear();
                        }                                                   
                    }else{
                        new GIDaoException("Se generó un error al abrir el archivo de trabajo");
                    }      
                    
                    cerrarArchivo();
                }
            }                 
        }
                
        new GIDaoException("Finalizando tarea NotificarVencimientoPlanMntoSIU");
        
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
