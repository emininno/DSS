/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cyberdyne.dss.utils;

import it.cyberdyne.dss.users.ManageUsers;
import it.cyberdyne.dss.users.User;
import java.util.Iterator;
import java.util.List;



/**
 *
 * @author ern
 */
public class Test1 {
    
    public static void main(String[] args)  {
        
        String user="admin";
        String pass="admin";
        ManageUsers manager = new ManageUsers();
        manager.addUser(user, pass);
        

       
        int id = manager.getUserId("admin");
        byte[] hash = manager.getPassword(id);
        byte[] salt = manager.getSalt(id);
        System.out.println("Logged:"+Password.isExpectedPassword(pass.toCharArray(), salt, hash));
}
    
}
