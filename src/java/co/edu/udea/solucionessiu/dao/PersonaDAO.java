/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.PersonaSIGEP;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.List;

/**
 *
 * @author jorge.correaj
 */
public interface PersonaDAO {
    public List<PersonaSIGEP> obtenerPorNivel(Integer intNivel) throws GIDaoException;
    public PersonaSIGEP obtenerUna(String strIdPersona) throws GIDaoException;  
}
