/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cyberdyne.dss.utils;

import it.cyberdyne.dss.users.User;



/**
 *
 * @author ern
 */
public class Test1 {
    
    public static void main(String[] args)  {
        
        String user="pippo";
        String pass="ciao";
        User u = new User(user,pass);
        System.out.println("pass:"+ new String(u.getPassword()));
}
    
}
