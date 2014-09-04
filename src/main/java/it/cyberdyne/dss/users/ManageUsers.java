/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.users;

import it.cyberdyne.dss.utils.DataHelper;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ern
 */
public class ManageUsers {

    private DataHelper helper;

    public ManageUsers() {
        
        helper = new DataHelper();
        
    }

    public Integer addUser(String userName, String password) {
        if (!isUser(userName))
        {
        helper.openSession();
        User user = new User(userName, password);
        Integer id = this.helper.pushData(user);
        helper.closeSession();
        return id;
        }
        else{
            System.out.println("User already created!");
            return getUserId(userName);
        }
    }

    public List makeQuery(String query) {
        helper.openSession();
        Session session = this.helper.getSession();
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
            helper.closeSession();
        }
        return users;
    }

    public List listUsers() {
       List<User> list = makeQuery("FROM User");
        return list;
    }

    public boolean isUser(String name) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr = session.createCriteria(User.class);
        cr.add(Restrictions.eq("username",name));
        List list = cr.list();
        helper.closeSession();
        return list.size() > 0;
    }

    public int getUserId(String name) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr = session.createCriteria(User.class);
        cr.add(Restrictions.eq("username",name));
        List<User> list = cr.list();
        helper.closeSession();
        return list.get(0).getId();
    }

    public byte[] getPassword(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr = session.createCriteria(User.class);
        cr.add(Restrictions.eq("id",id));
        List<User> list = cr.list();
        helper.closeSession();
        return list.get(0).getPassword();
    }

    public byte[] getSalt(int id) {
        helper.openSession();
        Session session = helper.getSession();
        Criteria cr = session.createCriteria(User.class);
        cr.add(Restrictions.eq("id",id));
        List<User> list = cr.list();
        helper.closeSession();
        return list.get(0).getSalt();
    }
}
