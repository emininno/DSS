/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.beans;

import it.cyberdyne.dss.feeds.Feed;
import it.cyberdyne.dss.feeds.ManageFeeds;
import it.cyberdyne.dss.utils.HibernateUtil;
import it.cyberdyne.dss.vehicles.Vehicle;
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
@ManagedBean(name = "feedTableBean")
@SessionScoped

public class FeedTableBean {
    
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;

    private ArrayList<Feed> feedList;
    private FeedBean service;
    private Feed selectedFeed;
    
    public Feed getSelectedFeed() {
        return selectedFeed;
    }
    
    public void toggleUniformed() {
        if (selectedFeed.isUniformed()) {
            selectedFeed.setUniformed(false);
        } else {
            selectedFeed.setUniformed(true);
        }
    }
    
    public void setSelectedFeed(Feed selectedFeed) {
        this.selectedFeed = selectedFeed;
    }
    
    public ArrayList<Feed> getFeedList() {
        return feedList;
    }

    /**
     * Creates a new instance of VehicleTableBean
     */
    public FeedTableBean() {
        
    }
    
    private Feed[] getFeedArrayFromDB() {
        
        ManageFeeds manager = new ManageFeeds(loginBean.getLoggedId());
        List<Feed> list = manager.listFeeds();
        Feed[] v = new Feed[list.size()];
        return list.toArray(v);
        
    }
    
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }
    
    private List<Feed> getFeedListFromDB() {
        System.out.println("Get Feed From DB");
        ManageFeeds manager = new ManageFeeds(loginBean.getLoggedId());
        List<Feed> list = manager.listFeeds();
        return list;
    }
    
    public String saveAction() {
        
        SessionFactory s = HibernateUtil.getSessionFactory();
        
        for (Feed feedList1 : feedList) {
            System.out.println("v(id):" + feedList1.getId() + ", v(edit):" + feedList1.isEdit() + " v(start):" + feedList1.getStart());
            int id = feedList1.getId();
            if (id < 0) {
                Session session = s.openSession();
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    session.save(feedList1);
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
                if (feedList1.isEdit()) {
                    Session session = s.openSession();
                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();
                        Feed v = (Feed) session.get(Feed.class, id);
                        
                        v.copy(feedList1);
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

        //return to current page
        return null;
        
    }
    
    public String editAction(Feed feed) {
        feed.setEdit(true);
        return null;
    }
    
    @PostConstruct
    public void init() {
        this.feedList = (ArrayList<Feed>) getFeedListFromDB();
        
    }
    
    public void setService(FeedBean service) {
        this.service = service;
    }
    
    public void onEdit(RowEditEvent event) {
        System.out.println("xxx:");
    }
    
    public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Feed Edited", ((Feed) event.getObject()).getCode());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        ((Feed) event.getObject()).setEdit(true);
        System.out.println("xxx:" + ((Feed) event.getObject()).getCode());
    }
    
    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Feed) event.getObject()).getCode());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        ((Feed) event.getObject()).setEdit(false);
    }
    
    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        int id = event.getRowIndex();
        //((Vehicle) event.getSource()).setEdit(true);
        feedList.get(id).setEdit(true);
        //if (newValue != null && !newValue.equals(oldValue)) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Row:" + id);
        FacesContext.getCurrentInstance().addMessage(null, msg);
        //}
    }
    
}
