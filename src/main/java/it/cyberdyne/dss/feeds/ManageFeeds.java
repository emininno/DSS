/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.feeds;

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
public class ManageFeeds {

    private final DataHelper helper;
    private final int userId;
    
    public ManageFeeds(int userId) {

        helper = new DataHelper();
        this.userId = userId;
        
    }
//    
//    public Integer addFeed(Integer duration, Integer workingTime, double start, boolean uniformed, Integer userId) {
//        if (!isFeed()) {
//            helper.openSession();
//            Feed feed = new Feed(duration, workingTime, start, uniformed, userId);
//            int id = this.helper.pushData(feed);
//            helper.closeSession();
//            return id;
//        } else {
//            System.out.println("Vehicle already created!");
//            return getVehicleId(code);
//        }
//    }
//    
//    public Integer addVehicle(Feed v) {
//        if (!isVehicle(v.getDuration())) {
//            helper.openSession();
//            Feed vehicle = new Feed(v);
//            int id = this.helper.pushData(vehicle);
//            helper.closeSession();
//            return id;
//        } else {
//            System.out.println("Vehicle already created!");
//            return getVehicleId(v.getDuration());
//        }
//    }

    public List<Feed> listFeeds() {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Feed.class);
        System.out.println("ID:"+userId);
        cr.add(Restrictions.eq("userId", userId));
        System.out.println("Query:"+cr.toString());
        List<Feed> list = cr.list();
        helper.closeSession();
        return list;
    }

//    public boolean isFeed() {
//        helper.openSession();
//        Session session = helper.getSession();
//        Criteria cr = session.createCriteria(Feed.class);
//        cr.add(Restrictions.eq("userId", userId));
//        List list = cr.list();
//        helper.closeSession();
//       
//        return list.size() > 0;
//    }

    public int getFeedId(String code) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Feed.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("code", code));
        List<Feed> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getId();
    }

    public int getWorkingTime(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Feed.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Feed> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getWorkingTime();
    }

    public String getCode(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Feed.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Feed> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getCode();
    }

    public Integer getDuration(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Feed.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Feed> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getDuration();
    }

    public String getStart(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Feed.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Feed> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getStart();
    }

    public Integer getUserId(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Feed.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Feed> list = cr.list();
        helper.closeSession();
        
        return list.get(0).getUserId();
    }

    public boolean isUniformed(int id){
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr=session.createCriteria(Feed.class);
        cr.add(Restrictions.eq("userId", userId));
        cr.add(Restrictions.eq("id", id));
        List<Feed> list = cr.list();
        helper.closeSession();
        
        return list.get(0).isUniformed();
    }
    
    
}
