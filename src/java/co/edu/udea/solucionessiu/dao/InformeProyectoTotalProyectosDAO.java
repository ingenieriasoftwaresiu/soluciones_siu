/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.InformeProyectoTotalProyectos;
import co.edu.udea.solucionessiu.exception.GIDaoException;
import java.util.List;

/**
 *
 * @author jorge.correaj
 */
public interface InformeProyectoTotalProyectosDAO {
    
    public InformeProyectoTotalProyectos obtenerUno(Integer intId) throws GIDaoException;
    public List<InformeProyectoTotalProyectos> obtenerPorProyecto(Integer intIdProyecto) throws GIDaoException;
}
