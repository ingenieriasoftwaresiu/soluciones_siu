/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.Actividad;
import co.edu.udea.solucionessiu.dto.EjecucionPptalProyecto;
import co.edu.udea.solucionessiu.dto.Movimiento;
import co.edu.udea.solucionessiu.dto.Persona;
import co.edu.udea.solucionessiu.dto.Proyecto;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.List;

/**
 *
 * @author jorge.correaj
 */
public interface NotificacionMailSigepDAO {
    // Se inhabilita notificación a la Coordinadora de Compras.
    //public void notificarVencimientoProyecto(String strAccion, Proyecto proyecto, List<EjecucionPptalProyecto> ejecucionPptal, Persona admonDependencia, Persona investigadorPpal, Persona admonProyecto, Persona coordCompras) throws GIDaoException;
    public void notificarVencimientoProyecto(String strAccion, Proyecto proyecto, List<EjecucionPptalProyecto> ejecucionPptal, Persona admonDependencia, Persona investigadorPpal, Persona admonProyecto) throws GIDaoException;
    public void notificarRegalias(List<String> participantes) throws GIDaoException;
    public void notificarActividades(Actividad actividad, String strAccionNotificar) throws GIDaoException;
    public void notificarReservas(List<Movimiento> movimientos, String strCodNotificacion)  throws GIDaoException;
}
