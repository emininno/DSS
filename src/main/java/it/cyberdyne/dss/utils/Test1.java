/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.utils;

import it.cyberdyne.dss.users.ManageUsers;
import it.cyberdyne.dss.vehicles.ManageVehicles;
import it.cyberdyne.dss.vehicles.Vehicle;

/**
 *
 * @author ern
 */
public class Test1 {

    public static void main(String[] args) {

        String user = "admin";
        String pass = "admin";
        ManageVehicles vManager = new ManageVehicles(12);

        Vehicle v = new Vehicle("001", 8, "Furgonetta", 7.5, 240.0, 1000.0, 7.30, 12, true);
        
        vManager.addVehicle(v);
    }

}
