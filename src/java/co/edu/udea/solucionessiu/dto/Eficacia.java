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
public class Eficacia {
    private String estado;
    private Date fechaPRevision;
    private String accionNotificar;

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Date getFechaPRevision() {
        return fechaPRevision;
    }

    public void setFechaPRevision(Date fechaPRevision) {
        this.fechaPRevision = fechaPRevision;
    }

    public String getAccionNotificar() {
        return accionNotificar;
    }

    public void setAccionNotificar(String accionNotificar) {
        this.accionNotificar = accionNotificar;
    }
        
}
