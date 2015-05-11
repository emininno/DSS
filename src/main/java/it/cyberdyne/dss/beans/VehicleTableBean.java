/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.beans;

import it.cyberdyne.dss.utils.HibernateUtil;
import it.cyberdyne.dss.vehicles.ManageVehicles;
import it.cyberdyne.dss.vehicles.Vehicle;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author ern
 */
@ManagedBean(name = "vehicleTableBean")
@SessionScoped

public class VehicleTableBean {
    
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    private ArrayList<Vehicle> vehicleList;
    private ArrayList<Vehicle> deletedVehicles;
    private VehicleBean service;
    private Vehicle selectedVehicle;
    
    public Vehicle getSelectedVehicle() {
        return selectedVehicle;
    }
    
    public void toggleEnabled() {
        if (selectedVehicle.isEnabled()) {
            selectedVehicle.setEnabled(false);
        } else {
            selectedVehicle.setEnabled(true);
        }
    }
    
    public void setSelectedVehicle(Vehicle selectedVehicle) {
        this.selectedVehicle = selectedVehicle;
    }
    
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
    
    private List<Vehicle> getVehicleListFromDB() {
        System.out.println("Get Vehicles From DB");
        ManageVehicles manager = new ManageVehicles(loginBean.getLoggedId());
        List<Vehicle> list = manager.listVehicles();
        return list;
    }
    
    public String saveAction() {
        
        SessionFactory s = HibernateUtil.getSessionFactory();
        
        for (Vehicle vehicleList1 : vehicleList) {
            System.out.println("v(id):" + vehicleList1.getId() + ", v(edit):" + vehicleList1.isEdit() + " v(capacity):" + vehicleList1.getCapacity());
            int id = vehicleList1.getId();
            if (id < 0) {
                Session session = s.openSession();
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    session.save(vehicleList1);
                    tx.commit();
                } catch (HibernateException e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                    e.printStackTrace();
                } finally {
                    session.close();
                }
                
            } else {
                if (vehicleList1.isEdit()) {
                    Session session = s.openSession();
                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();
                        Vehicle v = (Vehicle) session.get(Vehicle.class, id);
                        
                        v.copy(vehicleList1);
                        session.update(v);
                        tx.commit();
                    } catch (HibernateException e) {
                        if (tx != null) {
                            tx.rollback();
                        }
                        e.printStackTrace();
                    } finally {
                        session.close();
                    }
                    
                }
            }
        }
        for (Vehicle v : deletedVehicles) {
            int id = v.getId();
            Session session = s.openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Vehicle tmpV = (Vehicle) session.get(Vehicle.class, id);
                session.delete(tmpV);
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) {
                    tx.rollback();
                }
                e.printStackTrace();
            } finally {
                session.close();
            }
        }
        deletedVehicles.removeAll(deletedVehicles);
        FacesMessage msg = new FacesMessage("Vehicles Saved");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        //return to current page
        return null;
        
    }
    
    public String editAction(Vehicle vehicle) {
        vehicle.setEdit(true);
        return null;
    }
    
    @PostConstruct
    public void init() {
        this.vehicleList = (ArrayList<Vehicle>) getVehicleListFromDB();
        this.deletedVehicles = new ArrayList<>();
        
    }
    
    public void setService(VehicleBean service) {
        this.service = service;
    }
    
    public void onEdit(RowEditEvent event) {
        System.out.println("xxx:");
    }
    
    public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Vehicle Edited", ((Vehicle) event.getObject()).getCode());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        ((Vehicle) event.getObject()).setEdit(true);
        System.out.println("xxx:" + ((Vehicle) event.getObject()).getCode());
    }
    
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Vehicle) event.getObject()).getCode());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        ((Vehicle) event.getObject()).setEdit(false);
    }
    
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        int id = event.getRowIndex();
        //((Vehicle) event.getSource()).setEdit(true);
        vehicleList.get(id).setEdit(true);
        //if (newValue != null && !newValue.equals(oldValue)) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Row:" + id);
        FacesContext.getCurrentInstance().addMessage(null, msg);
        //}
    }
    
    public void deleteVehicle() {
        deletedVehicles.add(selectedVehicle);
        vehicleList.remove(selectedVehicle);
        selectedVehicle = null;
    }
    
    public void addVehicle() {
        vehicleList.add(new Vehicle("000", 0, "Model", 0.0, 0.0, 0.0, "5:0:0", loginBean.getLoggedId(), true));
    }
}
