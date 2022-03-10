/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.Etapa;
import co.edu.udea.solucionessiu.exception.GIDaoException;

/**
 *
 * @author jorge.correaj
 */
public interface EtapaDAO {
    public Etapa obtenerPorProyecto(String strIdProyecto) throws GIDaoException;
}
