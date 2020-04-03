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

/**
 *
 * @author jorge.correaj
 */
public class PruebaNotificarVencimientoPlanMntoSIU {
       
    public static void main(String args[]) throws IOException{
        
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
        FileInputStream fis = null;
        XSSFWorkbook myWorkBook = null;
        XSSFSheet mySheet = null;
        Iterator<Row> rowIterator = null;
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        NotificacionMailDAO notificacionMailDAO = new NotificacionMailDAOImpl();
        ArrayList<EquipoMnto> equipos = new ArrayList<EquipoMnto>();
        
        strFechaActual = null;
                
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        strFechaActual = funcionesComunesDAO.getFechaActual();
        System.out.println("strFechaActual: " + strFechaActual);
                               
        strTemp = strFechaActual.split("-");
        strMesFechaActual = strTemp[1];
        System.out.println("strMesFechaActual: " + strMesFechaActual);
                
        for(int i=0;i<strCodigosNotificacion.length;i++){
            
            System.out.println("Código de notificación: " + strCodigosNotificacion[i]);
            strCodigoNotificacion = strCodigosNotificacion[i];
            strRutaArchivo = "";           
            notificacion = null;
            myFile = null;
            fis = null;
            
            try{
                notificacion = notificacionDAO.obtenerUno(strCodigoNotificacion);
            }catch(GIDaoException gde){
                new GIDaoException("Se generó un error recuperando la información de la notificación con código " + strCodigoNotificacion, gde);            
                notificacion = null;
            }
            
            if (notificacion != null){
                
                strRutaArchivo = notificacion.getRuta().trim();                
                System.out.println("strRutaArchivo: " + strRutaArchivo);

                try{            
                    myFile = new File(strRutaArchivo);
                    fis = new FileInputStream(myFile);
                }catch(FileNotFoundException fnfe){
                    new GIDaoException("Se generó un error cargando el objeto desde el archivo", fnfe);            
                    fis = null;
                }
                
                if (fis != null){
                    
                    myWorkBook = null;
                    
                    try{
                        myWorkBook = new XSSFWorkbook (fis);
                    }catch(IOException ioe){
                        new GIDaoException("Se generó un error abriendo el objeto de Excel desde el archivo", ioe);
                    }

                    if (myWorkBook != null){
                        
                        strNomHoja = null;
                        mySheet = null;
                        rowIterator = null;
                        
                        strNomHoja = notificacion.getNombreHoja().trim();
                        System.out.println("strNomHoja: " + strNomHoja);
                        mySheet = myWorkBook.getSheet(strNomHoja);
                        rowIterator = mySheet.iterator();
                        intFilaInicio = 7;
                        
                        while (rowIterator.hasNext()) {
                            
                            intFila = 0;                                   
                            
                            row = rowIterator.next();
                            intFila = row.getRowNum();
                            
                            System.out.println("Fila: " + new Integer(intFila+1));

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
                                                System.out.println("strTipoEquipo: " + strTipoEquipo);
                                            break;
                                        case 3:
                                                strNomEquipo = cell.getStringCellValue().trim();
                                                System.out.println("strNomEquipo: " + strNomEquipo);
                                            break;   
                                        case 7:                                                
                                                try{
                                                    if (cell.getStringCellValue() != null){
                                                        strTorre = strTorre + cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         dblTorre = cell.getNumericCellValue();
                                                         strTorre = strTorre + String.valueOf(dblTorre.intValue());
                                                     }else{
                                                         strTorre = strTorre + "*";
                                                     }
                                                 }
                                                                                                
                                                System.out.println("strTorre: " + strTorre);
                                            break;  
                                        case 8:                                                
                                               try{
                                                    if (cell.getStringCellValue() != null){
                                                        strPiso = strPiso + cell.getStringCellValue();
                                                    }
                                                 }catch(IllegalStateException ise){
                                                     if (cell.getNumericCellValue() != 0){
                                                         dblPiso = cell.getNumericCellValue();
                                                         strPiso = strPiso + String.valueOf(dblPiso.intValue());
                                                     }else{
                                                         strPiso = strPiso + "*";
                                                     }
                                                 }                                            
                                     
                                                System.out.println("strPiso: " + strPiso);
                                            break;  
                                        case 9:                                                                                                
                                            strServicio = cell.getStringCellValue().trim();
                                            System.out.println("strServicio: " + strServicio);
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
                                                
                                                System.out.println("strEnero: " + strEnero);
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
                                                                                           
                                                System.out.println("strFebrero: " + strFebrero);
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
                                        
                                                System.out.println("strMarzo: " + strMarzo);
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
                                                
                                                System.out.println("strAbril: " + strAbril);
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
                                                
                                                System.out.println("strMayo: " + strMayo);
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
                                                
                                                System.out.println("strJunio: " + strJunio);
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
                                                
                                                System.out.println("strJulio: " + strJulio);
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
                                                
                                                System.out.println("strAgosto: " + strAgosto);
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
                                                
                                                System.out.println("strSeptiembre: " + strSeptiembre);
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
                                                
                                                System.out.println("strOctubre: " + strOctubre);
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
                                                
                                                System.out.println("strNoviembre: " + strNoviembre);
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
                                                
                                                System.out.println("strDiciembre: " + strDiciembre);
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
                    }     
                    
                    try{
                        if (fis != null){
                            fis.close();
                        }                        
                    }catch(IOException e){
                        new GIDaoException("Se generó un error cerrando el FileInputStream Object", e);
                    }                    
                }
            }                 
        }
                
        new GIDaoException("Finalizando tarea NotificarVencimientoPlanMntoSIU");
    }
    
}
