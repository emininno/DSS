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
public class TravelTime implements Serializable{

    private Integer id;
    private Integer placeId1;
    private Integer placeId2;
    private Double time;

    private boolean edit = false;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getPlaceId1() {
        return placeId1;
    }

    public void setPlaceId1(Integer placeId1) {
        this.placeId1 = placeId1;
    }

    public Integer getPlaceId2() {
        return placeId2;
    }

    public void setPlaceId2(Integer placeId2) {
        this.placeId2 = placeId2;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }

    public String editAction() {
        setEdit(true);
        return null;
    }

    public TravelTime(int id1, int id2, double time) {
        this.id = -1;
        this.placeId1 = id1;
        this.placeId2 = id2;
        this.time = time;

    }

    public TravelTime(TravelTime v) {
        this.id = -1;
        this.placeId1 = v.getPlaceId1();
        this.placeId2 = v.getPlaceId2();
        this.time = v.getTime();
    }

    public void copy(TravelTime v) {
        this.id = -1;
        this.placeId1 = v.getPlaceId1();
        this.placeId2 = v.getPlaceId2();
        this.time = v.getTime();
    }

    public TravelTime() {

    }
    
    @Override
    public String toString(){
        String s = "id:"+this.id + " id1:"+this.placeId1+" id2:"+this.placeId2+" Time:"+this.time;
        return s;
    }
}
