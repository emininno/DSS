/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.beans;

import it.cyberdyne.dss.vehicles.ManageVehicles;
import it.cyberdyne.dss.vehicles.Vehicle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author ern
 */
@ManagedBean(name = "vehicleTableBean")
@SessionScoped

public class VehicleTableBean {

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    //private Vehicle[] vehicleList;

    /*private static final ArrayList<Vehicle> vehicleList = 
		new ArrayList<>(Arrays.asList(
		
		new Vehicle("001", 3, "Pippo", 0.0, 0.0, 0.0, 0.0, 1, true),
		new Vehicle("002", 3, "Pluto", 0.0, 0.0, 0.0, 0.0, 1, true),
                new Vehicle("003", 3, "Paperino", 0.0, 0.0, 0.0, 0.0, 1, true)
	));*/

    private final ArrayList<Vehicle> vehicleList = (ArrayList<Vehicle>) getVehicleListFromDB();
    public ArrayList<Vehicle> getVehicleList() {
        return vehicleList;
    }
    
    /**
     * Creates a new instance of VehicleTableBean
     */
    public VehicleTableBean() {

    }

    private Vehicle[] getVehicleArrayFromDB() {
        
            ManageVehicles manager = new ManageVehicles(loginBean.getLoggedId());
            List<Vehicle> list = manager.listVehicles();
            Vehicle[] v = new Vehicle[list.size()];
            return list.toArray(v);
       
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    private  List<Vehicle> getVehicleListFromDB() {
        ManageVehicles manager = new ManageVehicles(loginBean.getLoggedId());
        List<Vehicle> list = manager.listVehicles();
        return list;
    }

    public String saveAction() {

        for (Vehicle vehicleList1 : vehicleList) {
            vehicleList1.setEdit(false);
        }
        //return to current page
        return null;

    }

    public String editAction(Vehicle vehicle) {

        vehicle.setEdit(true);
        return null;
    }

}
