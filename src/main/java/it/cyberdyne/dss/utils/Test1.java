/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package it.cyberdyne.dss.utils;



/**
 *
 * @author ern
 */
public class Test1 {
    
    public static void main(String[] args)  {
        
        String pass="ciao";
        char[] password = Password.generateRandomPassword(10).toCharArray();
        byte[] salt = Password.getNextSalt();
        byte[] hash =  Password.hash(password, salt);
        hash = Password.hash(password, salt);
        byte[] hash3 = Password.hash(password, salt);
        byte[] hash4 = Password.hash(password, salt);
        System.out.println(new String(hash));
        System.out.println(new String(hash3));
        System.out.println(new String(hash4));
        System.out.println(Password.isExpectedPassword(password, salt, hash));
}
    
}
