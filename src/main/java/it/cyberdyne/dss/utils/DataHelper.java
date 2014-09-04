/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

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

    public Integer pushData(Object data) {
        session.beginTransaction();
        Integer id = (Integer) session.save(data);
        session.getTransaction().commit();

        return id;
    }

    public void openSession() {
        session = s.openSession();
    }

    public void closeSession() {
        session.close();
    }
}
