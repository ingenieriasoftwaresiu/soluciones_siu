/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.Participante;
import co.edu.udea.solucionessiu.exception.GIDaoException;

/**
 *
 * @author jorge.correaj
 */
public interface ParticipanteDAO {
    public Participante obtenerPorProyectoYRol(String strIdProyecto, Integer intRol) throws GIDaoException;
}
