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
public class MatrixRemover {

    public static Double[][] columnRemover(Double[][] InputM, int col) {
        Double[][] OutPutM = new Double[InputM.length][InputM[0].length - 1];
        for (int i = 0; i < InputM.length; i++) {
            int newColIdx = 0;
            for (int j = 0; j < InputM[i].length; j++) {
                if (j != col) {
                    OutPutM[i][newColIdx++] = InputM[i][j];
                }
            }

        }
        return OutPutM;
    }

    public static Double[][] rowRemover(Double[][] InputM, int row) {
        Double[][] OutPutM = new Double[InputM.length -1][InputM[0].length];
        int newRowIdx = 0;
        for (int i = 0; i < InputM.length; i++) {
            if (i != row) {
                
                for (int j = 0; j < InputM[i].length; j++) {
                    OutPutM[newRowIdx][j] = InputM[i][j];
                }
                newRowIdx++;
            }

        }
        return OutPutM;
    }
}