/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.Movimiento;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.List;

/**
 *
 * @author jorge.correaj
 */
public interface MovimientoDAO {
    public Movimiento obtenerUno(String strCodigo) throws GIDaoException;
    public List<Movimiento> obtenerMovimientosFechaActualXTipoSoporte(String strTipoMovimiento, String strTipoSoporte1, String strTipoSoporte2) throws GIDaoException;
    public List<Movimiento> obtenerMovimientosRentasPropiasFechaActual(Integer intNumDiasNotificar) throws GIDaoException;
}
