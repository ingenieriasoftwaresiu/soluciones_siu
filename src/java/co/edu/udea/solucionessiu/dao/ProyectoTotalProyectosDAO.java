/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.ProyectoTotalProyectos;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.List;

/**
 *
 * @author jorge.correaj
 */
public interface ProyectoTotalProyectosDAO {
    
    public ProyectoTotalProyectos obtenerUno(Integer intId) throws GIDaoException;
    public List<ProyectoTotalProyectos> obtenerActivos() throws GIDaoException;
}
