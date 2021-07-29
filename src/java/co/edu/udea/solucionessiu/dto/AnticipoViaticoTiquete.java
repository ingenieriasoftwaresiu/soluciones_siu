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
public class AnticipoViaticoTiquete {
    private String reserva;
    private String grupo;
    private String solicitante;
    private String tipoSolicitud;
    private String nroComprobante;
    private String fechaLimiteEntrega;
    private String valorLegalizado;
    private String accionNotificar;
    private String observacion;
    private String codigoNotificacion;
    private String responsable;
    private String lugarComision;
    private String fechaInicioComision;
    private String nroTicket;

    public String getNroTicket() {
        return nroTicket;
    }

    public void setNroTicket(String nroTicket) {
        this.nroTicket = nroTicket;
    }    
    
    public String getFechaInicioComision() {
        return fechaInicioComision;
    }

    public void setFechaInicioComision(String fechaInicioComision) {
        this.fechaInicioComision = fechaInicioComision;
    }

    public String getLugarComision() {
        return lugarComision;
    }

    public void setLugarComision(String lugarComision) {
        this.lugarComision = lugarComision;
    }
        
    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }       

    public String getCodigoNotificacion() {
        return codigoNotificacion;
    }

    public void setCodigoNotificacion(String codigoNotificacion) {
        this.codigoNotificacion = codigoNotificacion;
    }
        
    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
        
    public String getAccionNotificar() {
        return accionNotificar;
    }

    public String getFechaLimiteEntrega() {
        return fechaLimiteEntrega;
    }

    public void setFechaLimiteEntrega(String fechaLimiteEntrega) {
        this.fechaLimiteEntrega = fechaLimiteEntrega;
    }

    public void setAccionNotificar(String accionNotificar) {
        this.accionNotificar = accionNotificar;
    }
    
    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getReserva() {
        return reserva;
    }

    public void setReserva(String reserva) {
        this.reserva = reserva;
    }

    public String getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(String solicitante) {
        this.solicitante = solicitante;
    }

    public String getTipoSolicitud() {
        return tipoSolicitud;
    }

    public void setTipoSolicitud(String tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    public String getNroComprobante() {
        return nroComprobante;
    }

    public void setNroComprobante(String nroComprobante) {
        this.nroComprobante = nroComprobante;
    }

    public String getValorLegalizado() {
        return valorLegalizado;
    }

    public void setValorLegalizado(String valorLegalizado) {
        this.valorLegalizado = valorLegalizado;
    }   
    
}
