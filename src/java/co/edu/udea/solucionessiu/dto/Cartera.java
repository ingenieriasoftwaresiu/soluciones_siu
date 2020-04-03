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
public class Cartera {
    private String nroFactura;
    private Integer nroDiasCartera;

    public String getNroFactura() {
        return nroFactura;
    }

    public void setNroFactura(String nroFactura) {
        this.nroFactura = nroFactura;
    }

    public Integer getNroDiasCartera() {
        return nroDiasCartera;
    }

    public void setNroDiasCartera(Integer nroDiasCartera) {
        this.nroDiasCartera = nroDiasCartera;
    }        
}
