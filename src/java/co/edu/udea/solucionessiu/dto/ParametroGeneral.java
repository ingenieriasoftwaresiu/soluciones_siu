/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dto;

/**
 *
 * @author Jorge.correa
 */
public class ParametroGeneral {
    private String codigo;
    private String nombreServidor;
    private Integer numeroPuerto;
    private String usuarioConexion;
    private String claveConexion;
    private String modoProduccion;
   private String emailDllo;

    public String getEmailDllo() {
        return emailDllo;
    }

    public void setEmailDllo(String emailDllo) {
        this.emailDllo = emailDllo;
    }   
   
    public String getModoProduccion() {
        return modoProduccion;
    }

    public void setModoProduccion(String modoProduccion) {
        this.modoProduccion = modoProduccion;
    }
        
    public String getNombreServidor() {
        return nombreServidor;
    }

    public void setNombreServidor(String nombreServidor) {
        this.nombreServidor = nombreServidor;
    }

    public Integer getNumeroPuerto() {
        return numeroPuerto;
    }

    public void setNumeroPuerto(Integer numeroPuerto) {
        this.numeroPuerto = numeroPuerto;
    }

    public String getUsuarioConexion() {
        return usuarioConexion;
    }

    public void setUsuarioConexion(String usuarioConexion) {
        this.usuarioConexion = usuarioConexion;
    }

    public String getClaveConexion() {
        return claveConexion;
    }

    public void setClaveConexion(String claveConexion) {
        this.claveConexion = claveConexion;
    }
     
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
        
}
