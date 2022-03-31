/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dto;

import java.util.Date;

/**
 *
 * @author jorge.correaj
 */
public class FechaProyectoTotalProyectos {
    
    private Integer idProyecto;
    private String fecha;
    private String tipoFecha;
    private String nombreInforme;

    public String getNombreInforme() {
        return nombreInforme;
    }

    public void setNombreInforme(String nombreInforme) {
        this.nombreInforme = nombreInforme;
    }    
    
    public Integer getIdProyecto() {
        return idProyecto;
    }

    public void setIdProyecto(Integer idProyecto) {
        this.idProyecto = idProyecto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    
    public String getTipoFecha() {
        return tipoFecha;
    }

    public void setTipoFecha(String tipoFecha) {
        this.tipoFecha = tipoFecha;
    }    
    
}
