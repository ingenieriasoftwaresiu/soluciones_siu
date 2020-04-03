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
import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.dto.Pedido;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 *
 * @author jorge.correa
 */
public class NotificarVencimientoPedidos implements Job{

    @Override
    public void execute(JobExecutionContext jec) throws JobExecutionException {
        
        new GIDaoException("Iniciando tarea NotificarVencimientoPedidos");
        
        String strRutaArchivoPedidos=null, strControl=null, strGrupo=null, strNombreProveedor=null, strFechaEnvioProveedor=null, strFechaActual=null, strValorCelda=null;
        String strOrdenPedido=null, strFechaAcordada=null, strFechaRecibido=null, strCodigoNotificacion="";
        String[] strTemp=null;
        String strCodigosNotificaciones[] = new String[4];
        Integer intFila=null, intFilaInicio=null, intCont=null, intCelda=null, intNroDiasAcordados=null, intContNotificados=null, intContNotificaciones=null;
        Long lgDiasDiferencia=null, lgNumDiasNotificacion=null;
        Notificacion notificacion = null;
        Date dtFechaActual=null, dtFechaAcordada=null;
        FileInputStream fileInputStream=null;
        Iterator<Cell> cellIterator=null;
        Cell celda = null;
        int cellType;
        Integer fila;
               
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        NotificacionMailDAO notificacionMailDAO = new NotificacionMailDAOImpl();
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        
        Pedido pedido = null;
        strCodigosNotificaciones[0] = "PEDIDOSNALESBC";
        strCodigosNotificaciones[1] = "PEDIDOSNALESFM";
        strCodigosNotificaciones[2] = "PEDIDOSNALESPM";
        strCodigosNotificaciones[3] = "PEDIDOSNALESSG";
                               
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        strFechaActual = funcionesComunesDAO.getFechaActual();
                
        try{
            dtFechaActual = sdf.parse(strFechaActual);
        }catch(ParseException pe){
            new GIDaoException("Se generó un error parseando la fecha actual", pe);
        }
        
        intContNotificaciones = 0;
        while (intContNotificaciones < strCodigosNotificaciones.length){        
            
            strCodigoNotificacion = strCodigosNotificaciones[intContNotificaciones];
        
        try{
            notificacion =notificacionDAO.obtenerUno(strCodigoNotificacion);
        }catch(GIDaoException gde){
            new GIDaoException("Se generó un error recuperando la información de la notificación con código " + strCodigoNotificacion, gde);            
            notificacion = null;
        }        
        
        if (notificacion != null){
            strRutaArchivoPedidos = notificacion.getRuta();
            lgNumDiasNotificacion = Long.parseLong(notificacion.getDiasNotificar().toString());
            
            File excel = new File(strRutaArchivoPedidos);
            
            try{
                fileInputStream = new FileInputStream(excel);
            }catch(IOException ioe){
                new GIDaoException("Se generó un error al leer el archivo en la ruta " + strRutaArchivoPedidos, ioe);
            }
            
            if (fileInputStream != null){                
                
                try{                                                     
                    XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
                    XSSFSheet sheet = workbook.getSheetAt(0);
                    Iterator<Row> rowIterator = sheet.iterator();
                    Row row;
                    intFilaInicio = 7;
                    intCont = 0;
                    intContNotificados = 0;
                    
                    while (rowIterator.hasNext()){
                        row = rowIterator.next();
                        
                        intFila = row.getRowNum();
                        
                        if (intFila >= intFilaInicio){                        
                            cellIterator = row.cellIterator();                    

                            while (cellIterator.hasNext()){
                                celda = cellIterator.next();
                                intCelda = celda.getColumnIndex();
                                cellType = celda.getCellType();    
                                                                                               
                                if (celda != null){
                                                                                                
                                    if (HSSFCell.CELL_TYPE_NUMERIC == cellType){
                                            if(DateUtil.isCellDateFormatted(celda)){
                                                strValorCelda = celda.getDateCellValue().toString();
                                            }else{
                                                NumberFormat f = NumberFormat.getInstance();
                                                f.setGroupingUsed(false);
                                                strValorCelda = f.format(celda.getNumericCellValue());
                                            }
                                    }else{
                                        if (HSSFCell.CELL_TYPE_STRING == cellType){
                                            strValorCelda = celda.getStringCellValue();
                                        }else{
                                            if (HSSFCell.CELL_TYPE_BOOLEAN == cellType){

                                            }else{
                                                if (HSSFCell.CELL_TYPE_BLANK == cellType){                   

                                                }else{
                                                    if (HSSFCell.CELL_TYPE_FORMULA == cellType){                                                     
                                                        fila = intFila+1;
                                                        CellReference cellReference = new CellReference("R" + fila.toString());                                                                                         
                                        
                                                        Cell cell = row.getCell(cellReference.getCol());        
                                                        try{
                                                            if (cell != null){
                                                                dtFechaAcordada = cell.getDateCellValue();
                                                                if (dtFechaAcordada != null){
                                                                    strValorCelda = funcionesComunesDAO.convertirFechaLarga(dtFechaAcordada.toString());
                                                                }else{
                                                                    strValorCelda = "";
                                                                }
                                                            }else{
                                                                 strValorCelda = "";
                                                            }
                                                        }catch(IllegalStateException ie){
                                                            strValorCelda = "";
                                                        }                                                                                                                     
                                                    }
                                                }
                                            }
                                        }
                                    }

                                    switch(intCelda){
                                            case 0: strControl = strValorCelda;break;
                                            case 1: strGrupo = strValorCelda;break;
                                            case 6: strOrdenPedido = strValorCelda;break;
                                            case 7: strNombreProveedor = strValorCelda;break;
                                            case 13: 
                                                if ((strValorCelda != null) && (!strValorCelda.trim().equals(""))){
                                                    strFechaEnvioProveedor = strValorCelda;
                                                }else{
                                                    strFechaEnvioProveedor = null;
                                                }                                                
                                                break;
                                            case 16: 
                                                if ((strValorCelda != null) && (!strValorCelda.trim().equals(""))){
                                                    if (strValorCelda.indexOf("-") >=0){
                                                        strTemp = strValorCelda.split("-");
                                                        intNroDiasAcordados = Integer.parseInt(strTemp[1]);
                                                    }else{
                                                        intNroDiasAcordados = Integer.parseInt(strValorCelda);
                                                    }                   
                                                }else{
                                                    intNroDiasAcordados = null;
                                                }
                                                break;
                                            case 17:
                                                if ((strValorCelda != null) && (!strValorCelda.trim().equals(""))){
                                                    strFechaAcordada= strValorCelda;
                                                }else{
                                                    strFechaAcordada = null;
                                                }
                                                break;
                                            case 19:
                                                if ((strValorCelda != null) && (!strValorCelda.trim().equals(""))){
                                                    strFechaRecibido = strValorCelda;
                                                }else{
                                                    strFechaRecibido = null;
                                                }
                                                break;
                                        }                
                                    }

                                    celda = null;
                                    intCelda = null;
                                    strValorCelda = null;
                            }                                                                                   
                                                
                            if ((strControl != null) && (!strControl.trim().equals(""))){
                                
                                if (strFechaRecibido == null || strFechaRecibido.equals("")){ //No se ha recibido el pedido aún, por lo tanto se puede procesar.
                                    
                                    if ((strGrupo == null) || (strGrupo.trim().equals(""))){
                                        strGrupo = "Sin asignar";
                                    }

                                    if ((strOrdenPedido == null) || (strOrdenPedido.trim().equals(""))){
                                        strOrdenPedido = "Sin asignar";
                                    }

                                    if ((strNombreProveedor == null) || (strNombreProveedor.trim().equals(""))){
                                        strNombreProveedor = "Sin asignar";
                                    }

                                    //if ((strFechaEnvioProveedor != null) && (intNroDiasAcordados != null)){
                                    if ((strFechaEnvioProveedor != null) && (strFechaAcordada != null)){
                                        
                                        strFechaEnvioProveedor = funcionesComunesDAO.convertirFechaLarga(strFechaEnvioProveedor);                                    
                                        //strFechaAcordada = funcionesComunesDAO.aumentarDiasFecha(strFechaEnvioProveedor, intNroDiasAcordados);

                                        try{
                                            dtFechaAcordada = sdf.parse(strFechaAcordada);
                                        }catch(ParseException pe){
                                            new GIDaoException("Se generó un error parseando la fecha acordada de entrega", pe);
                                        }

                                        lgDiasDiferencia = funcionesComunesDAO.getDiasDiferenciaFechas(dtFechaActual, dtFechaAcordada);

                                        if ((lgDiasDiferencia >=0) && (lgDiasDiferencia.equals(lgNumDiasNotificacion) || lgNumDiasNotificacion.equals(Long.parseLong("0")))){

                                            pedido = new Pedido();
                                            pedido.setNombreGrupo(strGrupo);
                                            pedido.setNombreProveedor(strNombreProveedor);
                                            pedido.setNumeroDiasAcordados(intNroDiasAcordados);
                                            pedido.setNumeroPedido(strOrdenPedido);
                                            pedido.setDiasDiferencia(lgDiasDiferencia);
                                            pedido.setFechaEnvioProveedor(strFechaEnvioProveedor);
                                            pedido.setFechaAcordada(strFechaAcordada);
                                            pedido.setCodigoNotificacion(strCodigoNotificacion);

                                            notificacionMailDAO.notificarVencimientoPedidos(pedido);

                                            intContNotificados++;
                                        }                                                                       

                                         intCont++;                                                                        
                                    }                                                             

                                    row = null;
                                    intFila = null;
                                    cellIterator = null;                   
                                    strControl = null;
                                    strGrupo = null;                     
                                    strOrdenPedido = null;
                                    strNombreProveedor = null;
                                    strFechaEnvioProveedor = null;
                                    intNroDiasAcordados = null;
                                    strFechaAcordada = null;
                                    lgDiasDiferencia = null;
                                    dtFechaAcordada = null;
                                    pedido = null;
                                }                                                             
                            }                                                                                                                
                        }                        
                    }
                                                    
                    new GIDaoException("Total de registros procesados para notificación " + strCodigoNotificacion + ": " + intCont);
                    new GIDaoException("Total de registros notificados para notificación " + strCodigoNotificacion + ": " + intContNotificados);
                                                                                        
                }catch(Exception e){
                    new GIDaoException("Se generó un error generando la notificación de vencimiento para " + strCodigoNotificacion, e);
                }finally{
                    try{
                        if (fileInputStream != null){
                            fileInputStream.close();
                        }                        
                    }catch(IOException e){
                        new GIDaoException("Se generó un error cerrando el FileInputStream Object", e);
                    }
                }                                
            }else{
                new GIDaoException("El objeto del archivo de Excel es nulo");
            }
        }else{
             new GIDaoException("No se recuperó la configuración de la notificación con código " + strCodigoNotificacion);
        }
            
            strCodigoNotificacion = "";
            intContNotificaciones++;
        }
        
        new GIDaoException("Finalizando tarea NotificarVencimientoPedidos");
    }    
}
