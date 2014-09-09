/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.vehicles;

import it.cyberdyne.dss.utils.DataHelper;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ern
 */
public class ManageVehicles {

    private final DataHelper helper;
    private final int userId;
    
    public ManageVehicles(int userId) {

        helper = new DataHelper();
        this.userId = userId;
        
    }
    
    public Integer addVehicle(String code, Integer quantity, String model, Double capacity, Double time, Double distance, Double start, Integer userId, boolean enabled) {
        if (!isVehicle(code)) {
            helper.openSession();
            Vehicle vehicle = new Vehicle(code, quantity, model, capacity, time, distance, start, userId, enabled);
            int id = this.helper.pushData(vehicle);
            helper.closeSession();
            return id;
        } else {
            System.out.println("Vehicle already created!");
            return getVehicleId(code);
        }
    }
    
    public Integer addVehicle(Vehicle v) {
        if (!isVehicle(v.getCode())) {
            helper.openSession();
            Vehicle vehicle = new Vehicle(v);
            int id = this.helper.pushData(vehicle);
            helper.closeSession();
            return id;
        } else {
            System.out.println("Vehicle already created!");
            return getVehicleId(v.getCode());
        }
    }

    public List<Vehicle> listVehicles() {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Vehicle.class);
        cr.add(Restrictions.eq("userId", userId));
        List<Vehicle> list = cr.list();
        helper.closeSession();
        return list;
    }

    public boolean isVehicle(String code) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr = session.createCriteria(Vehicle.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("code", code));
        List list = cr.list();
        helper.closeSession();
       
        return list.size() > 0;
    }

    public int getVehicleId(String code) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Vehicle.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("code", code));
        List<Vehicle> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getId();
    }

    public int getQuantity(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Vehicle.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Vehicle> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getQuantity();
    }

    public String getModel(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Vehicle.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Vehicle> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getModel();
    }

    public Double getCapacity(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Vehicle.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Vehicle> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getCapacity();
    }

    public Double getDistance(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Vehicle.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Vehicle> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getDistance();
    }

    public Double getStart(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Vehicle.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Vehicle> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getStart();
    }

    public Integer getUserId(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Vehicle.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Vehicle> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getUserId();
    }

    public boolean isEnabled(int id){
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Vehicle.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Vehicle> list = cr.list();
        helper.closeSession();
        
        return list.get(0).isEnabled();
    }
    
    
}
