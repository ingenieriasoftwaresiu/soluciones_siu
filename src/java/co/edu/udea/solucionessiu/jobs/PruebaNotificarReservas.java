/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.jobs;

import co.edu.udea.solucionessiu.dao.FuncionesComunesDAO;
import co.edu.udea.solucionessiu.dao.MovimientoDAO;
import co.edu.udea.solucionessiu.dao.NotificacionDAO;
import co.edu.udea.solucionessiu.dao.NotificacionMailSigepDAO;
import co.edu.udea.solucionessiu.dao.ReservasNotificadasDAO;
import co.edu.udea.solucionessiu.dao.impl.FuncionesComunesDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.MovimientoDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.NotificacionDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.NotificacionMailSigepDAOImpl;
import co.edu.udea.solucionessiu.dao.impl.ReservasNotificadasDAOImpl;
import co.edu.udea.solucionessiu.dto.Movimiento;
import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.dto.ReservaNotificada;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author jorge.correa
 */
public class PruebaNotificarReservas {
    
    public static void main(String[] args){
        
        new GIDaoException("Iniciando tarea NotificarReservas");
        
        String strFechaActual, strTipoMovimiento, strTipoSoporte1, strTipoSoporte2, strCodMov, strCodigoNotificacion;
        Integer intTotalReservasNotificadas, intNumDiasNotificar;
        Date dtFechaActual = null;
        List<Movimiento> movimientos = null;
        List<Movimiento> movANotificar = new ArrayList<Movimiento>();
        String strTiposSoporte[] = {"Orden de Pedido;Importación","Certificado de Disponibilidad Presupuestal;Orden de Servicio","Transferencia Intrauniversitaria;Autorización de Viáticos y Pasajes","Alquiler de auditorios;Servicios de apoyo"};
        String strCodigosNotificacion[] = {"NOTIFRESCOMPINT","NOTIFRESCONTRA","NOTIFTRANSFUNI","NOTIFRENTPROP"};
        String[] strTempTS = null;
        strTipoMovimiento = "Reserva";
        
        NotificacionMailSigepDAO notificacionMailDAO = new NotificacionMailSigepDAOImpl();        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        
        NotificacionDAO notificacionDAO = new NotificacionDAOImpl();
        Notificacion notificacion = null;
        
        FuncionesComunesDAO funcionesComunesDAO = new FuncionesComunesDAOImpl();
        strFechaActual = funcionesComunesDAO.getFechaActual();
        
        System.out.println("Fecha actual: " + strFechaActual);
        
        try{
            dtFechaActual = sdf.parse(strFechaActual);
        }catch(ParseException pe){
            new GIDaoException("Se generó un error parseando la fecha actual", pe);
        }

        MovimientoDAO movimientoDAO = new MovimientoDAOImpl();
        ReservasNotificadasDAO reservasNotificadasDAO = new ReservasNotificadasDAOImpl();
        ReservaNotificada reservaNotificada = null;
        intTotalReservasNotificadas = 0;
        
        for(int i=0;i<strTiposSoporte.length;i++){            
            
            System.out.println("Tipos de soporte: " + strTiposSoporte[i]);
            
            strTempTS = strTiposSoporte[i].split(";");
            strTipoSoporte1 = strTempTS[0];
            strTipoSoporte2 = strTempTS[1];            
                                    
            strCodigoNotificacion =strCodigosNotificacion[i];            
            System.out.println("Código de notificación: " + strCodigoNotificacion);
            
            try{
                notificacion = notificacionDAO.obtenerUna(strCodigoNotificacion);
            }catch(GIDaoException gi){
                new GIDaoException("Se generó un error al intentar recuperar la notificación con código " + strCodigoNotificacion, gi);
                notificacion = null;
            }
            
            if (notificacion != null){
                intNumDiasNotificar = notificacion.getDiasPreviosNotificacion();
            }else{
                intNumDiasNotificar = 0;
            }
            
            try{
                
                if ((strTipoSoporte1.equals("Alquiler de auditorios")) && (strTipoSoporte2.equals("Servicios de apoyo"))){
                    movimientos = movimientoDAO.obtenerMovimientosRentasPropiasFechaActual(intNumDiasNotificar);
                }else{
                    movimientos = movimientoDAO.obtenerMovimientosFechaActualXTipoSoporte(strTipoMovimiento, strTipoSoporte1, strTipoSoporte2);           
                }
                                                      
                System.out.println("Cantidad de registros recuperados: " + movimientos.size());
            }catch(GIDaoException gi){
                new GIDaoException("Se generó un error al intentar recuperar los movimientos a notificar", gi);
            }

            if (movimientos != null){            
                for (Movimiento mov : movimientos){
                    strCodMov = mov.getCodigoMov();

                    try{
                        reservaNotificada = reservasNotificadasDAO.obtenerUna(strCodMov);

                        if (reservaNotificada == null){
                            
                            movANotificar.add(mov);                            
                            reservaNotificada = new ReservaNotificada();
                            reservaNotificada.setCodigoReserva(strCodMov);
                            reservaNotificada.setFechaNotificacion(dtFechaActual);                            
                            reservasNotificadasDAO.insertar(reservaNotificada);                            
                            intTotalReservasNotificadas++;
                            
                        }else{
                            new GIDaoException("El movimiento con código " + strCodMov + " ya fue notificado!.");
                        }

                    }catch(GIDaoException gi){
                        new GIDaoException("Se generó un error al intentar validar la notificación previa del movimiento", gi);
                    }

                    strCodMov = "";
                    reservaNotificada = null;
                    mov = null;
                }
                
                if (movANotificar.size() > 0){                
                    try{
                        new GIDaoException("Notificando " + movANotificar.size() + " movimientos al código de notificación " + strCodigoNotificacion);
                        notificacionMailDAO.notificarReservas(movANotificar, strCodigoNotificacion);
                    }catch(GIDaoException gi){
                        new GIDaoException("Se generó un error al enviar la notificación con código " + strCodigoNotificacion, gi);
                    }                
                }else{
                    new GIDaoException("No se notificó ningún movimiento para el código de notificación " + strCodigoNotificacion);
                }
                
                 movANotificar.clear();
            }        
            
            movimientos = null;
            strTempTS = null;
            strTipoSoporte1 = "";
            strTipoSoporte2 = "";
        }
        
        new GIDaoException("Total de movimientos notificados: " + intTotalReservasNotificadas);
        new GIDaoException("Finalizando tarea NotificarReservas");
    }
    
}