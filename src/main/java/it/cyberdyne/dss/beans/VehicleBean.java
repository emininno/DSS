package it.cyberdyne.dss.beans;

import it.cyberdyne.dss.vehicles.ManageVehicles;
import it.cyberdyne.dss.vehicles.Vehicle;
import java.io.Serializable;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

/**
 * Simple login bean.
 *
 * @author itcuties
 */
@ManagedBean(name = "vehicleBean")
@SessionScoped
public class VehicleBean implements Serializable {

    private static final long serialVersionUID = 7765876811740798583L;

    private String code;
    private Integer quantity;
    private String model;
    private Double capacity;
    private Double distance;
    private Double start;
    private Integer userId;
    private boolean enabled;
    private Double time;

    @ManagedProperty(value = "#{navigationBean}")
    private NavigationBean navigationBean;
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    private int loggedId;

    //TODO - Non serve?
    public String addVehicle() {

        ManageVehicles manager = new ManageVehicles(loginBean.getLoggedId());

        if (code != null && quantity != null && model != null && capacity != null && distance != null && start != null && time != null) {
            Vehicle v = new Vehicle(code, quantity, model, capacity, time, distance, start, loginBean.getLoggedId(), enabled);
            manager.addVehicle(v);
            return navigationBean.redirectToVehicles();
        } else {

            FacesMessage msg = new FacesMessage("Vehicle error: all fields are required", "ERROR MSG");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return navigationBean.redirectToWelcome();
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public Double getStart() {
        return start;
    }

    public void setStart(Double start) {
        this.start = start;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
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
