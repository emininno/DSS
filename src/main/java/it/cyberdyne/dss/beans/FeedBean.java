package it.cyberdyne.dss.beans;

import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 * Simple login bean.
 *
 * @author itcuties
 */
@ManagedBean(name = "feedBean")
@SessionScoped
public class FeedBean implements Serializable {

    private static final long serialVersionUID = 7765876811740798583L;

    private Integer duration;
    private Integer workingTime;
    private Date start;
    private boolean uniformed;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getWorkingTime() {
        return workingTime;
    }

    public void setWorkingTime(Integer workingTime) {
        this.workingTime = workingTime;
    }

    public boolean isUniformed() {
        return uniformed;
    }

    public void setUniformed(boolean uniformed) {
        this.uniformed = uniformed;
    }
    private Integer userId;
    private String code;

    @ManagedProperty(value = "#{navigationBean}")
    private NavigationBean navigationBean;
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    private int loggedId;



    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }




    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }


    public NavigationBean getNavigationBean() {
        return navigationBean;
    }

    public void setNavigationBean(NavigationBean navigationBean) {
        this.navigationBean = navigationBean;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public int getLoggedId() {
        return loggedId;
    }

    public void setLoggedId(int loggedId) {
        this.loggedId = loggedId;
    }

}
