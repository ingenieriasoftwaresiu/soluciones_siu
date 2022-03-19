/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.Notificacion;
import co.edu.udea.solucionessiu.exception.GIDaoException;

/**
 *
 * @author jorge.correa
 */
public interface NotificacionDAO {
    public Notificacion obtenerUnoSiuWeb(String strCodigo) throws GIDaoException;
    public Notificacion obtenerUnoSIGEP(String strCodigo) throws GIDaoException;
}
