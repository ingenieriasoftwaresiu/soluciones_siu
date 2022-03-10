/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.Proyecto;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.List;

/**
 *
 * @author jorge.correaj
 */
public interface ProyectoDAO {
    public List<Proyecto> obtenerTodos() throws GIDaoException;
    public List<Proyecto> obtenerPorEstado(String strIdEstado) throws GIDaoException;
    public List<Proyecto> obtenerPorEstadoYTipoProyectoDiferente(String strIdEstado, String strIdTipoProyecto) throws GIDaoException;
    public Proyecto obtenerUno(String strCodigo) throws GIDaoException;
    public Boolean verificarNotificacionProyecto(String strIdProyecto) throws GIDaoException;
}
