/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.places;

import java.io.Serializable;
import java.sql.Time;
/**
 *
 * @author ern
 */
public class Place implements Serializable{

    private Integer id;
    private String label;
    private Double demandA;
    private Double demandB;
    private Integer serviceTime;
    private Time open;
    private Time close;
    private String address;
    private Integer userId;
    private boolean enabled;
    
    private boolean edit=false;

    
    public Double getDemandA() {
        return demandA;
    }

    public void setDemandA(Double demandA) {
        this.demandA = demandA;
    }

    public Double getDemandB() {
        return demandB;
    }

    public void setDemandB(Double demandB) {
        this.demandB = demandB;
    }
    
    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }
    
    public boolean isEnabled() {
        return enabled;
    }

    public String editAction(){
        setEdit(true);
        return null;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

   

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Place(String label, Double demandA, Double demandB, Integer serviceTime, Time open, Time close, String address, Integer userId) {
        this.id = -1;
        this.label = label;
        this.demandA = demandA;
        this.demandB = demandB;
        this.serviceTime = serviceTime;
        this.open = open;
        this.close = close;
        this.address = address;
        this.userId = userId;
        this.enabled = true;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(Integer serviceTime) {
        this.serviceTime = serviceTime;
    }

    public Time getOpen() {
        return open;
    }

    public void setOpen(Time open) {
        this.open = open;
    }

    public Time getClose() {
        return close;
    }

    public void setClose(Time close) {
        this.close = close;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
   

    public Place(Place v) {
        this.id = -1;
        this.label = v.getLabel();
        this.demandA = v.getDemandA();
        this.demandB = v.getDemandB();
        this.serviceTime = v.getServiceTime();
        this.open = v.getOpen();
        this.close = v.getClose();
        this.address = v.getAddress();
        this.userId = v.userId;
        this.enabled = v.isEnabled();

    }
    
    public void copy(Place v){
        this.label = v.getLabel();
        this.demandA = v.getDemandA();
        this.demandB = v.getDemandB();
        this.serviceTime = v.getServiceTime();
        this.open = v.getOpen();
        this.close = v.getClose();
        this.address = v.getAddress();
        this.userId = v.userId;
        this.enabled = v.isEnabled();
    }


    public Place() {

    }

   

    /**
     * Get the value of id
     *
     * @return the value of id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the value of id
     *
     * @param id new value of id
     */
    public void setId(Integer id) {
        this.id = id;
    }

}
