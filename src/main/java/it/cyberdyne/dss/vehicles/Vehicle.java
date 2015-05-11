/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.vehicles;

import it.cyberdyne.dss.utils.Utilities;
import java.sql.Time;
import java.util.Date;

/**
 *
 * @author ern
 */
public class Vehicle {

    private Integer id;

    private String code;
    private Integer quantity;
    private String model;
    private Double capacity;
    private Double distance;
    private String start;
    private Integer userId;
    private boolean enabled;
    private Double time;
    private boolean edit=false;

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        if (start!=""){
        String[] s= start.split(":");
        if (s.length == 3)
            if(Utilities.isInteger(s[0]) && Utilities.isInteger(s[1]) && Utilities.isInteger(s[2])){
                this.start = start;
            }
                
            else
            {
                this.start="05:00:00";
            }
        else
            this.start="05:00:00";
        }
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public Vehicle(String type, Integer quantity, String model, Double capacity, Double time, Double distance, String start, Integer userId, boolean enabled) {
        this.id = -1;
        this.code = type;
        this.quantity = quantity;
        this.model = model;
        this.capacity = capacity;
        this.distance = distance;
        this.start = start;
        this.userId = userId;
        this.time = time;
        this.enabled = enabled;
    }

    public Vehicle(Vehicle v) {
        this.id = v.getId();
        this.code = v.getCode();
        this.quantity = v.getQuantity();
        this.model = v.getModel();
        this.capacity = v.getCapacity();
        this.distance = v.getDistance();
        this.start = v.getStart();
        this.userId = v.getUserId();
        this.time = v.getTime();
        this.enabled = v.isEnabled();

    }
    
    public void copy(Vehicle v){
        this.id = v.getId();
        this.code = v.getCode();
        this.quantity = v.getQuantity();
        this.model = v.getModel();
        this.capacity = v.getCapacity();
        this.distance = v.getDistance();
        this.start = v.getStart();
        this.userId = v.getUserId();
        this.time = v.getTime();
        this.enabled = v.isEnabled();
    }

    public Vehicle() {

    }

    /**
     * Get the value of code
     *
     * @return the value of code
     */
    public String getCode() {
        return code;
    }

    /**
     * Set the value of code
     *
     * @param code new value of code
     */
    public void setCode(String code) {
        this.code = code;
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
