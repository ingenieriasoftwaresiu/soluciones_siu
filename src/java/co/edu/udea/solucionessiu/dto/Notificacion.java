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
public class Notificacion {
    private String codigo;
    private String nombre;
    private String ruta;
    private String nombreDestinatario;
    private String emailDestinatario;
    private Integer diasNotificar;
    private Integer diasDespuesNotificar;
    private String nombreHoja;
    private Integer diasPreviosNotificacion;
    private String asunto;
    private String mensaje;
    private String estado;
    private String notificaACoordinador;

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getNotificaACoordinador() {
        return notificaACoordinador;
    }

    public void setNotificaACoordinador(String notificaACoordinador) {
        this.notificaACoordinador = notificaACoordinador;
    }    

    public String getNombreDestinatario() {
        return nombreDestinatario;
    }

    public void setNombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
    }

    public String getEmailDestinatario() {
        return emailDestinatario;
    }

    public void setEmailDestinatario(String emailDestinatario) {
        this.emailDestinatario = emailDestinatario;
    }
    

    public Integer getDiasPreviosNotificacion() {
        return diasPreviosNotificacion;
    }

    public void setDiasPreviosNotificacion(Integer diasPreviosNotificacion) {
        this.diasPreviosNotificacion = diasPreviosNotificacion;
    }

    public String getNombreHoja() {
        return nombreHoja;
    }

    public void setNombreHoja(String nombreHoja) {
        this.nombreHoja = nombreHoja;
    }
        
    public Integer getDiasDespuesNotificar() {
        return diasDespuesNotificar;
    }

    public void setDiasDespuesNotificar(Integer diasDespuesNotificar) {
        this.diasDespuesNotificar = diasDespuesNotificar;
    }
        
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRuta() {
        return ruta;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }
   
    public Integer getDiasNotificar() {
        return diasNotificar;
    }

    public void setDiasNotificar(Integer diasNotificar) {
        this.diasNotificar = diasNotificar;
    }    
    
}


