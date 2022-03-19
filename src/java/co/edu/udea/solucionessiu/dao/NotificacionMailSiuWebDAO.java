/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.Actividad;
import co.edu.udea.solucionessiu.dto.AnticipoViaticoTiquete;
import co.edu.udea.solucionessiu.dto.CalibracionEquipo;
import co.edu.udea.solucionessiu.dto.Cartera;
import co.edu.udea.solucionessiu.dto.Contrato;
import co.edu.udea.solucionessiu.dto.ControlInsumo;
import co.edu.udea.solucionessiu.dto.Documento;
import co.edu.udea.solucionessiu.dto.EjecucionPptalProyecto;
import co.edu.udea.solucionessiu.dto.EquipoMnto;
import co.edu.udea.solucionessiu.dto.MntoPrtvoEqCi;
import co.edu.udea.solucionessiu.dto.Movimiento;
import co.edu.udea.solucionessiu.dto.Pedido;
import co.edu.udea.solucionessiu.dto.PersonaSIGEP;
import co.edu.udea.solucionessiu.dto.ProyectoSIGEP;
import co.edu.udea.solucionessiu.dto.RegistroPlanCalibracion;
import co.edu.udea.solucionessiu.dto.RegistroPlanMejoramiento;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.List;

/**
 *
 * @author jorge.correa
 */
public interface NotificacionMailSiuWebDAO {
    public void notificarVencimientoPedidos(Pedido pedido) throws GIDaoException;
    public void notificarVencimientoDocumentos(Documento documento) throws GIDaoException;
    public void notificarVencimientoRegistroPlanMejoramiento(RegistroPlanMejoramiento registro) throws GIDaoException;
    public void notificarVencimientoRegistroPlanMejoramientoLEC(RegistroPlanMejoramiento registro) throws GIDaoException;
    public void notificarVencimientoRegistroPlanCalibEqASIU(RegistroPlanCalibracion registro) throws GIDaoException;
    public void notificarVencimientoContrato(Contrato contrato) throws GIDaoException;
    public void notificarVencimientoAnticipoViaticoTiquete(AnticipoViaticoTiquete anticipoViaticoTiquete) throws GIDaoException;
    public void notificarVencimientoCalibracionEquipo(List<CalibracionEquipo> calibraciones) throws GIDaoException;
    public void notificarVencimientoMntoPrtvoEquCi(List<MntoPrtvoEqCi> MntosPrtivos) throws GIDaoException;
    public void notificarControlInsumos(ControlInsumo controlInsumo) throws GIDaoException;
    public void notificarEquiposMnto(List<EquipoMnto> equipos, String strCodNotificacion)  throws GIDaoException;
    public void notificarVencimientoCartera(List<Cartera> carteras) throws GIDaoException;
}
