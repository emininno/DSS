/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.feeds;

import it.cyberdyne.dss.utils.Utilities;
import java.sql.Time;

/**
 *
 * @author ern
 */
public class Feed {

    private Integer id;

    private Integer duration;
    private Integer workingTime;
    private String start;
    private boolean uniformed;
    private Integer userId;
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    
    
    

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public boolean isUniformed() {
        return uniformed;
    }

    public void setUniformed(boolean uniformed) {
        this.uniformed = uniformed;
    }
    
    private boolean edit=false;

    public boolean isEdit() {
        return edit;
    }

    public void setEdit(boolean edit) {
        this.edit = edit;
    }
    


    public String editAction(){
        setEdit(true);
        return null;
    }


    public Integer getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Integer workingTime) {
        this.workingTime = workingTime;
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


    public Feed(Integer duration, String code, Integer workingTime, String start, boolean uniformed, Integer userId) {
        this.id = -1;
        this.duration = duration;
        this.workingTime = workingTime;
        this.uniformed = uniformed;
        this.start = start;
        this.userId = userId;
        this.code = code;
    }

    public Feed(Feed v) {
        this.id = v.getId();
        this.duration = v.getDuration();
        this.workingTime = v.getWorkingTime();
        this.uniformed = v.isUniformed();
        this.start = v.getStart();
        this.userId = v.getUserId();
        this.code = code;
    }

    public Feed() {
    }
    
    
    
    public void copy(Feed v){
        this.id = v.getId();
        this.duration = v.getDuration();
        this.workingTime = v.getWorkingTime();
        this.uniformed = v.isUniformed();
        this.start = v.getStart();
        this.userId = v.getUserId();
        this.code = code;
    }


    /**
     * Get the value of duration
     *
     * @return the value of duration
     */
    public Integer getDuration() {
        return duration;
    }

    /**
     * Set the value of duration
     *
     * @param duration new value of duration
     */
    public void setDuration(Integer duration) {
        this.duration = duration;
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
