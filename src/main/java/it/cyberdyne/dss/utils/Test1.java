/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.utils;

import it.cyberdyne.dss.users.ManageUsers;
import it.cyberdyne.dss.vehicles.ManageVehicles;
import it.cyberdyne.dss.vehicles.Vehicle;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author ern
 */
public class Test1 {

    private static void printM(Double[][] M) {
        int n = M.length;
        int m = M[0].length;
        for (int i = 0; i < n; i++) {
            System.out.println("");
            for (int j = 0; j < m; j++) {
                System.out.print(M[i][j] + ";");
            }
        }
    }

    public static void main(String[] args) {

        Double[][] M = new Double[10][10];
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                M[i][j] = i * 100.0 + j;
            }
        }
        printM(M);
        ArrayList<Integer> indices = new ArrayList<>();
        indices.add(3);
        indices.add(7);
        indices.add(9);
        Collections.sort(indices);
        System.out.println();
        for (int i = indices.size() - 1; i >= 0; i--) {
            System.out.println("");
            M = MatrixRemover.columnRemover(M, indices.get(i));
            M = MatrixRemover.rowRemover(M, indices.get(i));

        }
        printM(M);
    }

}
