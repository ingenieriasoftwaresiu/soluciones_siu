/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dao;

import co.edu.udea.solucionessiu.dto.Coordinacion;
import co.edu.udea.solucionessiu.dto.Persona;
import co.edu.udea.solucionessiu.dto.Proceso;
import co.edu.udea.solucionessiu.exception.GIDaoException;

/**
 *
 * @author jorge.correa
 */
public interface ParametrosASIUDAO {
    public Proceso obtenerProcesoXId(String strCodigo) throws GIDaoException;
    public Persona obtenerPersonaXId(String strCodigo) throws GIDaoException;    
    public Persona obtenerPersonaXCargo(String strIdCargo) throws GIDaoException;
    public String obtenerJefeInmediatoXIdEmpleado(String strIdEmpleado)  throws GIDaoException;
    public String obtenerJefeInmediatoXCargoEmpleado(String strIdCargo)  throws GIDaoException;
    public Coordinacion obtenerCoordinacionXId(String strCodigo) throws GIDaoException;
}
