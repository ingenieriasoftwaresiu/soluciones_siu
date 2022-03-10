/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.Persona;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.List;

/**
 *
 * @author jorge.correaj
 */
public interface PersonaDAO {
    public List<Persona> obtenerPorNivel(Integer intNivel) throws GIDaoException;
    public Persona obtenerUna(String strIdPersona) throws GIDaoException;  
}
