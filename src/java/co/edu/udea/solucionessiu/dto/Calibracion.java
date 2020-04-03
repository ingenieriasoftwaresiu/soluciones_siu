/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dto;

import java.util.Date;

/**
 *
 * @author jorge.correa
 */
public class Calibracion {
    private String responsable;
    private Date fechaProgramada;
    private String accionNotificar;

    public String getAccionNotificar() {
        return accionNotificar;
    }

    public void setAccionNotificar(String accionNotificar) {
        this.accionNotificar = accionNotificar;
    }
        
    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }
    
    public Date getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(Date fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }                
}
