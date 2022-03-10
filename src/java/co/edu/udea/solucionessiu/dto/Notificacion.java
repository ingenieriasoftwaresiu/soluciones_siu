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
    private String nombreDestinario;
    private String emailDestinario;
    private Integer diasNotificar;
    private Integer diasDespuesNotificar;
    private String nombreHoja;
    private Integer diasPreviosNotificacion;

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

    public String getNombreDestinario() {
        return nombreDestinario;
    }

    public void setNombreDestinario(String nombreDestinario) {
        this.nombreDestinario = nombreDestinario;
    }

    public String getEmailDestinario() {
        return emailDestinario;
    }

    public void setEmailDestinario(String emailDestinario) {
        this.emailDestinario = emailDestinario;
    }

    public Integer getDiasNotificar() {
        return diasNotificar;
    }

    public void setDiasNotificar(Integer diasNotificar) {
        this.diasNotificar = diasNotificar;
    }    
    
}


