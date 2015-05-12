/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.places;

import it.cyberdyne.dss.utils.DataHelper;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ern
 */
public class ManageTravelTimes {

    private final DataHelper helper;
    private final int userId;

    public ManageTravelTimes(int userId) {

        helper = new DataHelper();
        this.userId = userId;

    }

    public Integer addTime(int id1, int id2, double dist) {
        if (!isTime(id1, id2)) {
            helper.openSession();
            TravelTime time = new TravelTime(id1, id2, dist);
            int id = this.helper.pushData(time);
            helper.closeSession();
            return id;
        } else {
            System.out.println("Time already created!");
            return getTimeId(id1, id2);
        }
    }

    public Integer addTime(TravelTime v) {
        if (!isTime(v.getPlaceId1(), v.getPlaceId2())) {
            helper.openSession();
            TravelTime time = new TravelTime(v);
            int id = this.helper.pushData(time);
            helper.closeSession();
            return id;
        } else {
            System.out.println("Time already created!");
            return getTimeId(v.getPlaceId1(), v.getPlaceId2());
        }
    }

    public List<TravelTime> listTimes() {
        helper.openSession();
        Session session = helper.getSession();
        SQLQuery query = session.createSQLQuery("SELECT * FROM TravelTimes WHERE 1");
        query.addEntity(TravelTime.class);
        //TODO filter for userId
        List<TravelTime> list = query.list();
        
        helper.closeSession();
        return list;
    }

    public boolean isTime(int id1, int id2) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr = session.createCriteria(TravelTime.class);
        cr.add(Restrictions.eq("Place1Id", id1));
        cr.add(Restrictions.eq("Place2Id", id2));
        List list = cr.list();
        helper.closeSession();

        return list.size() > 0;
    }

    public int getTimeId(int id1, int id2) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr = session.createCriteria(TravelTime.class);
        cr.add(Restrictions.eq("Place1Id", id1));
        cr.add(Restrictions.eq("Place2Id", id2));
        List<TravelTime> list = cr.list();
        helper.closeSession();

        return list.get(0).getId();
    }
    
    public double getTime(int id1, int id2) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr = session.createCriteria(TravelTime.class);
        cr.add(Restrictions.eq("Place1Id", id1));
        cr.add(Restrictions.eq("Place2Id", id2));
        List<TravelTime> list = cr.list();
        helper.closeSession();

        return list.get(0).getTime();
    }

}
