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
public class Movimiento {
    
    private String codigoMov; 
    private String codProyecto;
    private Integer rubro;
    private Integer subrubro;
    private String entidadFinanciadora;
    private String centroCostos;
    private String tipoMov;
    private String codReserva;
    private Date fecha;
    private Long valor;
    private String numeroSoporte;
    private String tipoSoporte;
    private String Observacion;

    public String getCodigoMov() {
        return codigoMov;
    }

    public void setCodigoMov(String codigoMov) {
        this.codigoMov = codigoMov;
    }

    public String getCodProyecto() {
        return codProyecto;
    }

    public void setCodProyecto(String codProyecto) {
        this.codProyecto = codProyecto;
    }

    public Integer getRubro() {
        return rubro;
    }

    public void setRubro(Integer rubro) {
        this.rubro = rubro;
    }

    public Integer getSubrubro() {
        return subrubro;
    }

    public void setSubrubro(Integer subrubro) {
        this.subrubro = subrubro;
    }

    public String getEntidadFinanciadora() {
        return entidadFinanciadora;
    }

    public void setEntidadFinanciadora(String entidadFinanciadora) {
        this.entidadFinanciadora = entidadFinanciadora;
    }

    public String getCentroCostos() {
        return centroCostos;
    }

    public void setCentroCostos(String centroCostos) {
        this.centroCostos = centroCostos;
    }

    public String getTipoMov() {
        return tipoMov;
    }

    public void setTipoMov(String tipoMov) {
        this.tipoMov = tipoMov;
    }

    public String getCodReserva() {
        return codReserva;
    }

    public void setCodReserva(String codReserva) {
        this.codReserva = codReserva;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Long getValor() {
        return valor;
    }

    public void setValor(Long valor) {
        this.valor = valor;
    }

    public String getNumeroSoporte() {
        return numeroSoporte;
    }

    public void setNumeroSoporte(String numeroSoporte) {
        this.numeroSoporte = numeroSoporte;
    }

    public String getTipoSoporte() {
        return tipoSoporte;
    }

    public void setTipoSoporte(String tipoSoporte) {
        this.tipoSoporte = tipoSoporte;
    }

    public String getObservacion() {
        return Observacion;
    }

    public void setObservacion(String Observacion) {
        this.Observacion = Observacion;
    }       
          
}
