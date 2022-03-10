/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.edu.udea.solucionessiu.dto;

/**
 *
 * @author jorge.correaj
 */
public class EjecucionPptalProyecto {
    private String codigoRubro;
    private String nombreRubro;
    private Double presupuesto;
    private Double reservas;
    private Double valorEjecucion;
    private Double porcentajeEjecucion;
    private Double disponibilidad;

    public Double getReservas() {
        return reservas;
    }

    public void setReservas(Double reservas) {
        this.reservas = reservas;
    }    
    
    public Double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(Double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public Double getValorEjecucion() {
        return valorEjecucion;
    }

    public void setValorEjecucion(Double valorEjecucion) {
        this.valorEjecucion = valorEjecucion;
    }

    public Double getDisponibilidad() {
        return disponibilidad;
    }

    public void setDisponibilidad(Double disponibilidad) {
        this.disponibilidad = disponibilidad;
    }
        
    public Double getPorcentajeEjecucion() {
        return porcentajeEjecucion;
    }

    public void setPorcentajeEjecucion(Double porcentajeEjecucion) {
        this.porcentajeEjecucion = porcentajeEjecucion;
    }           
        
    public String getCodigoRubro() {
        return codigoRubro;
    }

    public void setCodigoRubro(String codigoRubro) {
        this.codigoRubro = codigoRubro;
    }

    public String getNombreRubro() {
        return nombreRubro;
    }

    public void setNombreRubro(String nombreRubro) {
        this.nombreRubro = nombreRubro;
    }           
        
}
