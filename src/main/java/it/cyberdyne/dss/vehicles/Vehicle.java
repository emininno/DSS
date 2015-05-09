/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.vehicles;

import java.sql.Time;

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
    private Time start;
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

    public Time getStart() {
        return start;
    }

    public void setStart(Time start) {
        this.start = start;
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

    public Vehicle(String type, Integer quantity, String model, Double capacity, Double time, Double distance, Time start, Integer userId, boolean enabled) {
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
