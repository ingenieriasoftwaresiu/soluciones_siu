/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.Actividad;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.List;

/**
 *
 * @author jorge.correaj
 */
public interface ActividadDAO {
    public Actividad obtenerPorCodigo(Integer strIdActividad) throws GIDaoException;
    public List<Actividad> obtenerTodas() throws GIDaoException;
    public List<Actividad> obtenerPorFecha(String strFechaBase, String strCriterio) throws GIDaoException;
}