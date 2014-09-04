/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.beans;

import it.cyberdyne.dss.users.ManageUsers;
import it.cyberdyne.dss.users.User;
import it.cyberdyne.dss.utils.DataHelper;
import it.cyberdyne.dss.utils.HibernateUtil;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
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
    private DataHelper helper;
    private String userName;
    private String password;
    private String confirmedPassword;
    private boolean confirmed = false;

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getConfirmedPassword() {
        return confirmedPassword;
    }

    public void setConfirmedPassword(String confirmedPassword) {
        this.confirmedPassword = confirmedPassword;
    }

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

    public void addUser() {
        ManageUsers manager = new ManageUsers();
        if (confirmedPassword.equals(this.password)) {
            setConfirmed(true);
            if (!manager.isUser(userName)) {
                manager.addUser(userName, password);
                FacesMessage msg = new FacesMessage("User successfully created. Please go to login page", "ERROR MSG");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            } else {
                FacesMessage msg = new FacesMessage("User already created!", "ERROR MSG");
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                FacesContext.getCurrentInstance().addMessage(null, msg);
            }
        } else {
            setConfirmed(false);
            FacesMessage msg = new FacesMessage("Password mismatch!", "ERROR MSG");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public RegisterBean() {

        this.helper = new DataHelper();
    }

}
