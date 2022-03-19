/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.ProyectoSIGEP;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.List;

/**
 *
 * @author jorge.correaj
 */
public interface ProyectoSIGEPDAO {
    public List<ProyectoSIGEP> obtenerTodos() throws GIDaoException;
    public List<ProyectoSIGEP> obtenerPorEstado(String strIdEstado) throws GIDaoException;
    public List<ProyectoSIGEP> obtenerPorEstadoYTipoProyectoDiferente(String strIdEstado, String strIdTipoProyecto) throws GIDaoException;
    public ProyectoSIGEP obtenerUno(String strCodigo) throws GIDaoException;
    public Boolean verificarNotificacionProyecto(String strIdProyecto) throws GIDaoException;
}
