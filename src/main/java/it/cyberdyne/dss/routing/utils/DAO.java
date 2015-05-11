package it.cyberdyne.dss.routing.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

public class DAO
{
  public static void matrixToLinear(Double[][] matrix, String directory, String fileBasename, int call_id, boolean considerSimmetry)
  {
    System.out.println("matrixToLinear");
    if ((matrix != null) && (matrix.length > 0) && (matrix[0].length > 0)) {
      String fileName = fileBasename + "_" + call_id + ".txt";
      File f = new File(new File(directory), fileName);
      try {
        PrintStream ps = new PrintStream(new FileOutputStream(f));
        for (int i = 0; i < matrix.length; i++) {
          for (int j = 0; j < matrix[i].length; j++) {
            if (i != j) {
              Double elem = matrix[i][j];
              String outStr = "" + i + " " + j + " " + elem;
              if (considerSimmetry) {
                try {
                  Double sim = matrix[j][i];
                  if (i < j) {
                    if (elem.equals(sim)) {
                      outStr = outStr + " s";
                    } else {
                      outStr = outStr + " a";
                    }
                  }
                  else if (!elem.equals(sim)) {
                    outStr = outStr + " a";
                  } else {
                    outStr = null;
                  }
                }
                catch (ArrayIndexOutOfBoundsException obe) {
                  outStr = outStr + " a";
                }
              }
              if (outStr != null) {
                System.out.println(outStr);
                ps.println(outStr);
              }
            }
          }
        }
        ps.close();
      }
      catch (FileNotFoundException fnfe) {
        System.err.println("ERRORE. Impossibile creare il file " + fileName + " in " + directory);
      }
    }
    else
    {
      System.out.println("DAO.matrixToLinear: matrice nulla o vuota");
    }
  }
  
  public static void linearToMatrix(String directory, String fileBasename, int call_id)
  {
    String inFileName = fileBasename + "_" + call_id + ".txt";
    File inFile = new File(new File(directory), inFileName);
    try {
      BufferedReader br = new BufferedReader(new FileReader(inFile));
      String line = null;
      int order = 0;
      while ((line = br.readLine()) != null) {
        String[] elems = line.split("\\s");
        if (elems.length > 2) {
          Integer i = Integer.valueOf(Integer.parseInt(elems[0]));
          Integer j = Integer.valueOf(Integer.parseInt(elems[1]));
          if (i.intValue() > order)
            order = i.intValue();
          if (j.intValue() > order)
            order = j.intValue();
        }
      }
      order += 1;
      Double[][] matrix = new Double[order][order];
      

      for (int r = 0; r < order; r++) {
        for (int c = 0; c < order; c++) {
          if (r == c) {
            matrix[r][c] = Double.valueOf(0.0D);
          } else {
            matrix[r][c] = new Double(9.999999999E9D);
          }
        }
      }
      BufferedReader br2 = new BufferedReader(new FileReader(inFile));
      line = null;
      while ((line = br2.readLine()) != null) {
        String[] elems = line.split("\\s");
        if (elems.length > 2) {
          Integer i = Integer.valueOf(Integer.parseInt(elems[0]));
          Integer j = Integer.valueOf(Integer.parseInt(elems[1]));
          Double dist = Double.valueOf(Double.parseDouble(elems[2]));
          
          String sim = null;
          if (elems.length > 3) {
            sim = elems[3].trim();
          }
          matrix[i.intValue()][j.intValue()] = dist;
          if ((sim != null) && (sim.equals("s")))
            matrix[j.intValue()][i.intValue()] = dist;
        }
      }
      br.close();
      br2.close();
      

      String outFileName = fileBasename + "_" + call_id + "_matrix" + ".txt";
      File outFile = new File(new File(directory), outFileName);
      PrintStream ps = new PrintStream(new FileOutputStream(outFile));
      for (int r = 0; r < order; r++) {
        for (int c = 0; c < order - 1; c++) {
          ps.print(Utilities.format3fract(matrix[r][c].doubleValue()) + ", ");
        }
        ps.println(Utilities.format3fract(matrix[r][(order - 1)].doubleValue()) + ";");
      }
      ps.close();
    }
    catch (FileNotFoundException fnfe) {
      System.err.println("ERRORE: Impossibile aprire il file " + inFileName + " in " + directory);
      
      fnfe.printStackTrace();
    }
    catch (IOException ioe) {
      System.err.println("ERRORE in lettura del file" + inFileName);
      ioe.printStackTrace();
    }
    catch (NumberFormatException nfe) {
      System.err.println("ERRORE in conversione numero " + nfe + ". Riga ignorata.");
    }
  }
}