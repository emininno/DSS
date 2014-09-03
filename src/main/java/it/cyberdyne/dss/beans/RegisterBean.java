/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cyberdyne.dss.beans;

import it.cyberdyne.dss.users.User;
import it.cyberdyne.dss.utils.HibernateUtil;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author ern
 */
@ManagedBean(name = "registerBean")
@SessionScoped
public class RegisterBean {

    /**
     * Creates a new instance of RegisterBean
     */
    
    private User user;
    private HibernateUtil helper;
    private Session session;
    private SessionFactory s;
    private String userName;
    private String password;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    
    public void addUser(){
        user = new User("paperino", "pluto");
        session = helper.getSessionFactory().openSession(); 
        session.beginTransaction();
        session.save(user);
        session.getTransaction().commit();
        session.close();
    }
    
    public RegisterBean() {
    }
    
}
