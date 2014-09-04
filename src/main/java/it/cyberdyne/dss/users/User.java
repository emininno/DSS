/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.users;

import it.cyberdyne.dss.utils.Password;

/**
 *
 * @author ern
 */
public class User {

    private int id;
    private String username;
    private byte[] password;
    private byte[] salt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public User() {

    }

    public User(String name, String pass) {
        this.username = name;
        this.salt = Password.getNextSalt();
        this.password = Password.hash(pass.toCharArray(), this.salt);
        //this.password = Password.hash(password.toCharArray(), this.salt);
    }
}
