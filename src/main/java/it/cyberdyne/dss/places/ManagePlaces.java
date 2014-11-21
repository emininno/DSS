/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.places;

import it.cyberdyne.dss.utils.DataHelper;
import java.sql.Time;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ern
 */
public class ManagePlaces {

    private final DataHelper helper;
    private final int userId;
    
    public ManagePlaces(int userId) {

        helper = new DataHelper();
        this.userId = userId;
        
    }
    
    public Integer addPlace(String label, Double demand, Integer serviceTime, Time open, Time close, String address, Integer userId) {
        if (!isPlace(label)) {
            helper.openSession();
            Place place = new Place(label,  demand,  serviceTime,  open,  close,  address,  userId);
            int id = this.helper.pushData(place);
            helper.closeSession();
            return id;
        } else {
            System.out.println("Place already created!");
            return getPlaceId(label);
        }
    }
    
    public Integer addPlace(Place v) {
        if (!isPlace(v.getLabel())) {
            helper.openSession();
            Place place = new Place(v);
            int id = this.helper.pushData(place);
            helper.closeSession();
            return id;
        } else {
            System.out.println("Place already created!");
            return getPlaceId(v.getLabel());
        }
    }

    public List<Place> listPlaces() {
        System.out.println("ListPlaces...");
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Place.class);
        cr.add(Restrictions.eq("userId", userId));
        List<Place> list = cr.list();
        helper.closeSession();
        return list;
    }

    public boolean isPlace(String label) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr = session.createCriteria(Place.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("label", label));
        List list = cr.list();
        helper.closeSession();
       
        return list.size() > 0;
    }

    public int getPlaceId(String label) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Place.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("label", label));
        List<Place> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getId();
    }

    public Double getDemand(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Place.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Place> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getDemand();
    }

    public Integer getServiceTime(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Place.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Place> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getServiceTime();
    }

    public Time getOpenTime(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Place.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Place> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getOpen();
    }
    
    public Time getCloseTime(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Place.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Place> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getClose();
    }

    public String getAddress(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Place.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Place> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getAddress();
    }

    public Integer getUserId(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Place.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Place> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getUserId();
    }

    public boolean isEnabled(int id){
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Place.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Place> list = cr.list();
        helper.closeSession();
        
        return list.get(0).isEnabled();
    }
    
    
}
