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
public class RegistroPlanCalibracion {
    private String areaLab;
    private String equipo;
    private String marca;
    private String modelo;
    private Calibracion calibracion;

    public String getAreaLab() {
        return areaLab;
    }

    public void setAreaLab(String areaLab) {
        this.areaLab = areaLab;
    }

    public String getEquipo() {
        return equipo;
    }

    public void setEquipo(String equipo) {
        this.equipo = equipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Calibracion getCalibracion() {
        return calibracion;
    }

    public void setCalibracion(Calibracion calibracion) {
        this.calibracion = calibracion;
    }
  
}
