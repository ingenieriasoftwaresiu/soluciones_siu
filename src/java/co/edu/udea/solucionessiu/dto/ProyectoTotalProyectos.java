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
public class ProyectoTotalProyectos {
    
    private Integer id;
    private String name;
    private String siucode;
    private String startdate;
    private String enddate;
    private Integer statusid;
    private String commitments;
    private String enddatedef;
    private Integer managerid;

    public Integer getManagerid() {
        return managerid;
    }

    public void setManagerid(Integer managerid) {
        this.managerid = managerid;
    }    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSiucode() {
        return siucode;
    }

    public void setSiucode(String siucode) {
        this.siucode = siucode;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public Integer getStatusid() {
        return statusid;
    }

    public void setStatusid(Integer statusid) {
        this.statusid = statusid;
    }

    public String getCommitments() {
        return commitments;
    }

    public void setCommitments(String commitments) {
        this.commitments = commitments;
    }

    public String getEnddatedef() {
        return enddatedef;
    }

    public void setEnddatedef(String enddatedef) {
        this.enddatedef = enddatedef;
    }    
                
}
