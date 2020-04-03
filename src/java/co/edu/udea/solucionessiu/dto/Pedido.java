/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package co.edu.udea.solucionessiu.dto;

/**
 *
 * @author jorge.correa
 */
public class Pedido {
    private String nombreGrupo;
    private String numeroPedido;
    private String nombreProveedor;
    private String fechaEnvioProveedor;
    private Integer numeroDiasAcordados;
    private String fechaAcordada;
    private Long diasDiferencia;
    private String fechaRecibido;
    private String codigoNotificacion;

    public String getCodigoNotificacion() {
        return codigoNotificacion;
    }

    public void setCodigoNotificacion(String codigoNotificacion) {
        this.codigoNotificacion = codigoNotificacion;
    }
        
    public String getFechaRecibido() {
        return fechaRecibido;
    }

    public void setFechaRecibido(String fechaRecibido) {
        this.fechaRecibido = fechaRecibido;
    }
       
    public String getNombreGrupo() {
        return nombreGrupo;
    }

    public void setNombreGrupo(String nombreGrupo) {
        this.nombreGrupo = nombreGrupo;
    }

    public String getNumeroPedido() {
        return numeroPedido;
    }

    public void setNumeroPedido(String numeroPedido) {
        this.numeroPedido = numeroPedido;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public String getFechaEnvioProveedor() {
        return fechaEnvioProveedor;
    }

    public void setFechaEnvioProveedor(String fechaEnvioProveedor) {
        this.fechaEnvioProveedor = fechaEnvioProveedor;
    }

    public Integer getNumeroDiasAcordados() {
        return numeroDiasAcordados;
    }

    public void setNumeroDiasAcordados(Integer numeroDiasAcordados) {
        this.numeroDiasAcordados = numeroDiasAcordados;
    }

    public String getFechaAcordada() {
        return fechaAcordada;
    }

    public void setFechaAcordada(String fechaAcordada) {
        this.fechaAcordada = fechaAcordada;
    }

    public Long getDiasDiferencia() {
        return diasDiferencia;
    }

    public void setDiasDiferencia(Long diasDiferencia) {
        this.diasDiferencia = diasDiferencia;
    }
        
}
