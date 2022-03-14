/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.ParametroGeneral;
import co.edu.udea.solucionessiu.exception.GIDaoException;

/**
 *
 * @author Jorge.correa
 */
public interface ParametroGeneralDAO {
    public ParametroGeneral obtenerParametrosGeneralesSiuWeb() throws GIDaoException;
    public ParametroGeneral obtenerParametrosGeneralesSigep() throws GIDaoException;
    public Boolean verificarNotificacionProyecto(String strIdProyecto) throws GIDaoException;
}
