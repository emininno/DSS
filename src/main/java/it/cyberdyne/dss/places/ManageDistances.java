/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.places;

import it.cyberdyne.dss.utils.DataHelper;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ern
 */
public class ManageDistances {

    private final DataHelper helper;
    private final int userId;

    public ManageDistances(int userId) {

        helper = new DataHelper();
        this.userId = userId;

    }

    public Integer addDistance(int id1, int id2, double dist) {
        if (!isDistance(id1, id2)) {
            helper.openSession();
            Distance distance = new Distance(id1, id2, dist);
            int id = this.helper.pushData(distance);
            helper.closeSession();
            return id;
        } else {
            System.out.println("Distance already created!");
            return getDistanceId(id1, id2);
        }
    }

    public Integer addDistance(Distance v) {
        if (!isDistance(v.getPlaceId1(), v.getPlaceId2())) {
            helper.openSession();
            Distance distance = new Distance(v);
            int id = this.helper.pushData(distance);
            helper.closeSession();
            return id;
        } else {
            System.out.println("Distance already created!");
            return getDistanceId(v.getPlaceId1(), v.getPlaceId2());
        }
    }

    public List<Distance> listDistances() {
        System.out.println("listDistances...");
        helper.openSession();
        Session session = helper.getSession();
        Query query = session.createSQLQuery("SELECT * FROM PlaceDistances WHERE (Place1Id IN (SELECT id FROM Places WHERE userId= :uId) AND Place2Id IN (SELECT id FROM Places WHERE userId= :uId))").addEntity(Distance.class).setParameter("uId", userId);
        List<Distance> list = query.list();
        
        helper.closeSession();
        return list;
    }

    public boolean isDistance(int id1, int id2) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr = session.createCriteria(Distance.class);
        cr.add(Restrictions.eq("Place1Id", id1));
        cr.add(Restrictions.eq("Place2Id", id2));
        List list = cr.list();
        helper.closeSession();

        return list.size() > 0;
    }

    public int getDistanceId(int id1, int id2) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr = session.createCriteria(Distance.class);
        cr.add(Restrictions.eq("Place1Id", id1));
        cr.add(Restrictions.eq("Place2Id", id2));
        List<Distance> list = cr.list();
        helper.closeSession();

        return list.get(0).getId();
    }
    
    public double getDistance(int id1, int id2) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr = session.createCriteria(Distance.class);
        cr.add(Restrictions.eq("Place1Id", id1));
        cr.add(Restrictions.eq("Place2Id", id2));
        List<Distance> list = cr.list();
        helper.closeSession();

        return list.get(0).getDistance();
    }

}
