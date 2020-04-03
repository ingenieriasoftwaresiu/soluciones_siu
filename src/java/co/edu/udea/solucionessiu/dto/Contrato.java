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
public class Contrato {
    private String codigoContrato;
    private String fechaTerminacion;
    private String tipoContrato;
    private String accionNotificar;
    private String grupo;
    private String contratista;

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public String getContratista() {
        return contratista;
    }

    public void setContratista(String contratista) {
        this.contratista = contratista;
    }
        
    public String getAccionNotificar() {
        return accionNotificar;
    }

    public void setAccionNotificar(String accionNotificar) {
        this.accionNotificar = accionNotificar;
    }
        
    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }
        
    public String getCodigoContrato() {
        return codigoContrato;
    }

    public void setCodigoContrato(String codigoContrato) {
        this.codigoContrato = codigoContrato;
    }

    public String getFechaTerminacion() {
        return fechaTerminacion;
    }

    public void setFechaTerminacion(String fechaTerminacion) {
        this.fechaTerminacion = fechaTerminacion;
    }
    
}
