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
public class Correccion {
    private String responsable;
    private Date plazo;  
    private Date fechaSeguimiento1;
    private Date fechaSeguimiento2;
    private String estado;
    private String accionNotificar;

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public Date getPlazo() {
        return plazo;
    }

    public void setPlazo(Date plazo) {
        this.plazo = plazo;
    }

    public Date getFechaSeguimiento1() {
        return fechaSeguimiento1;
    }

    public void setFechaSeguimiento1(Date fechaSeguimiento1) {
        this.fechaSeguimiento1 = fechaSeguimiento1;
    }

    public Date getFechaSeguimiento2() {
        return fechaSeguimiento2;
    }

    public void setFechaSeguimiento2(Date fechaSeguimiento2) {
        this.fechaSeguimiento2 = fechaSeguimiento2;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getAccionNotificar() {
        return accionNotificar;
    }

    public void setAccionNotificar(String accionNotificar) {
        this.accionNotificar = accionNotificar;
    }
        
}
