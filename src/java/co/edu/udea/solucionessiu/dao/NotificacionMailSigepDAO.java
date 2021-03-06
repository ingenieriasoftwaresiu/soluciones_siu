/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.Actividad;
import co.edu.udea.solucionessiu.dto.EjecucionPptalProyecto;
import co.edu.udea.solucionessiu.dto.Movimiento;
import co.edu.udea.solucionessiu.dto.PersonaSIGEP;
import co.edu.udea.solucionessiu.dto.ProyectoSIGEP;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.List;

/**
 *
 * @author jorge.correaj
 */
public interface NotificacionMailSigepDAO {
    // Se inhabilita notificación a la Coordinadora de Compras.
    //public void notificarVencimientoProyecto(String strAccion, ProyectoSIGEP proyecto, List<EjecucionPptalProyecto> ejecucionPptal, PersonaSIGEP admonDependencia, PersonaSIGEP investigadorPpal, PersonaSIGEP admonProyecto, PersonaSIGEP coordCompras) throws GIDaoException;
    public void notificarVencimientoProyecto(String strAccion, ProyectoSIGEP proyecto, List<EjecucionPptalProyecto> ejecucionPptal, PersonaSIGEP admonDependencia, PersonaSIGEP investigadorPpal, PersonaSIGEP admonProyecto) throws GIDaoException;
    public void notificarRegalias(List<String> participantes) throws GIDaoException;
    public void notificarActividades(Actividad actividad, String strAccionNotificar) throws GIDaoException;
    public void notificarReservas(List<Movimiento> movimientos, String strCodNotificacion)  throws GIDaoException;
}
