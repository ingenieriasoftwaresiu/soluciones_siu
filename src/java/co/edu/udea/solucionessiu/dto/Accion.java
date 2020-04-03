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
public class Accion {
    private String tipoAccion;
    private String responsable;
    private Date plazo;
    private Date fechaSeguimiento1;
    private Date fechaSeguimiento2;
    private Date fechaSeguimiento3;
    private Date fechaSeguimiento4;
    private String estado;
    private String accionNotificar;

    public String getTipoAccion() {
        return tipoAccion;
    }

    public void setTipoAccion(String tipoAccion) {
        this.tipoAccion = tipoAccion;
    }

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

    public Date getFechaSeguimiento3() {
        return fechaSeguimiento3;
    }

    public void setFechaSeguimiento3(Date fechaSeguimiento3) {
        this.fechaSeguimiento3 = fechaSeguimiento3;
    }

    public Date getFechaSeguimiento4() {
        return fechaSeguimiento4;
    }

    public void setFechaSeguimiento4(Date fechaSeguimiento4) {
        this.fechaSeguimiento4 = fechaSeguimiento4;
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
