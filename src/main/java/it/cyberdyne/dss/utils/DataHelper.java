/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.utils;

import java.util.List;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 *
 * @author ern
 */
public class DataHelper {

    private HibernateUtil helper;
    private Session session;
    private SessionFactory s;

    public Session getSession() {
        return session;
    }

    public DataHelper() {

        s = helper.getSessionFactory();
    }

    public List makeQuery(String query) {
        openSession();
        Transaction tx = null;
        List users = null;

        try {
            tx = session.beginTransaction();
            users = session.createQuery(query).list();

            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
            e.printStackTrace();
        } finally {
            closeSession();
        }
        return users;
    }

    public Integer pushData(Object data) {
        session.beginTransaction();
        Integer id = (Integer) session.save(data);
        session.getTransaction().commit();

        return id;
    }
    
    public void updateData(int id, Object data) {
        
    }

    public void openSession() {
        session = s.openSession();
    }

    public void closeSession() {
        session.close();
    }
}
