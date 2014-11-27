/*     */ package it.cyberdyne.dss.routing.utils;
/*     */ 
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class DAO
/*     */ {
/*     */   public static void matrixToLinear(Double[][] matrix, String directory, String fileBasename, int call_id, boolean considerSimmetry)
/*     */   {
/*  39 */     System.out.println("matrixToLinear");
/*  40 */     if ((matrix != null) && (matrix.length > 0) && (matrix[0].length > 0)) {
/*  41 */       String fileName = fileBasename + "_" + call_id + ".txt";
/*  42 */       File f = new File(new File(directory), fileName);
/*     */       try {
/*  44 */         PrintStream ps = new PrintStream(new FileOutputStream(f));
/*  45 */         for (int i = 0; i < matrix.length; i++) {
/*  46 */           for (int j = 0; j < matrix[i].length; j++) {
/*  47 */             if (i != j) {
/*  48 */               Double elem = matrix[i][j];
/*  49 */               String outStr = "" + i + " " + j + " " + elem;
/*  50 */               if (considerSimmetry) {
/*     */                 try {
/*  52 */                   Double sim = matrix[j][i];
/*  53 */                   if (i < j) {
/*  54 */                     if (elem.equals(sim))
/*  55 */                       outStr = outStr + " s";
/*     */                     else {
/*  57 */                       outStr = outStr + " a";
/*     */                     }
/*     */                   }
/*  60 */                   else if (!elem.equals(sim))
/*  61 */                     outStr = outStr + " a";
/*     */                   else
/*  63 */                     outStr = null;
/*     */                 }
/*     */                 catch (ArrayIndexOutOfBoundsException obe)
/*     */                 {
/*  67 */                   outStr = outStr + " a";
/*     */                 }
/*     */               }
/*  70 */               if (outStr != null) {
/*  71 */                 System.out.println(outStr);
/*  72 */                 ps.println(outStr);
/*     */               }
/*     */             }
/*     */           }
/*     */         }
/*  77 */         ps.close();
/*     */       }
/*     */       catch (FileNotFoundException fnfe) {
/*  80 */         System.err.println("ERRORE. Impossibile creare il file " + fileName + " in " + directory);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*  85 */       System.out.println("DAO.matrixToLinear: matrice nulla o vuota");
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void linearToMatrix(String directory, String fileBasename, int call_id)
/*     */   {
/* 112 */     String inFileName = fileBasename + "_" + call_id + ".txt";
/* 113 */     File inFile = new File(new File(directory), inFileName);
/*     */     try {
/* 115 */       BufferedReader br = new BufferedReader(new FileReader(inFile));
/* 116 */       String line = null;
/* 117 */       int order = 0;
/* 118 */       while ((line = br.readLine()) != null) {
/* 119 */         String[] elems = line.split("\\s");
/* 120 */         if (elems.length > 2) {
/* 121 */           Integer i = Integer.valueOf(Integer.parseInt(elems[0]));
/* 122 */           Integer j = Integer.valueOf(Integer.parseInt(elems[1]));
/* 123 */           if (i.intValue() > order)
/* 124 */             order = i.intValue();
/* 125 */           if (j.intValue() > order)
/* 126 */             order = j.intValue();
/*     */         }
/*     */       }
/* 129 */       order += 1;
/*     */ 
/* 133 */       Double[][] matrix = new Double[order][order];
/*     */ 
/* 136 */       for (int r = 0; r < order; r++) {
/* 137 */         for (int c = 0; c < order; c++) {
/* 138 */           if (r == c)
/* 139 */             matrix[r][c] = Double.valueOf(0.0D);
/*     */           else {
/* 141 */             matrix[r][c] = new Double(9999999999.0D);
/*     */           }
/*     */         }
/*     */       }
/* 145 */       BufferedReader br2 = new BufferedReader(new FileReader(inFile));
/* 146 */       line = null;
/* 147 */       while ((line = br2.readLine()) != null) {
/* 148 */         String[] elems = line.split("\\s");
/* 149 */         if (elems.length > 2) {
/* 150 */           Integer i = Integer.valueOf(Integer.parseInt(elems[0]));
/* 151 */           Integer j = Integer.valueOf(Integer.parseInt(elems[1]));
/* 152 */           Double dist = Double.valueOf(Double.parseDouble(elems[2]));
/*     */ 
/* 154 */           String sim = null;
/* 155 */           if (elems.length > 3) {
/* 156 */             sim = elems[3].trim();
/*     */           }
/* 158 */           matrix[i.intValue()][j.intValue()] = dist;
/* 159 */           if ((sim != null) && (sim.equals("s")))
/* 160 */             matrix[j.intValue()][i.intValue()] = dist;
/*     */         }
/*     */       }
/* 163 */       br.close();
/* 164 */       br2.close();
/*     */ 
/* 167 */       String outFileName = fileBasename + "_" + call_id + "_matrix" + ".txt";
/* 168 */       File outFile = new File(new File(directory), outFileName);
/* 169 */       PrintStream ps = new PrintStream(new FileOutputStream(outFile));
/* 170 */       for (int r = 0; r < order; r++) {
/* 171 */         for (int c = 0; c < order - 1; c++) {
/* 172 */           ps.print(Utilities.format3fract(matrix[r][c].doubleValue()) + ", ");
/*     */         }
/* 174 */         ps.println(Utilities.format3fract(matrix[r][(order - 1)].doubleValue()) + ";");
/*     */       }
/* 176 */       ps.close();
/*     */     }
/*     */     catch (FileNotFoundException fnfe) {
/* 179 */       System.err.println("ERRORE: Impossibile aprire il file " + inFileName + " in " + directory);
/*     */ 
/* 181 */       fnfe.printStackTrace();
/*     */     }
/*     */     catch (IOException ioe) {
/* 184 */       System.err.println("ERRORE in lettura del file" + inFileName);
/* 185 */       ioe.printStackTrace();
/*     */     }
/*     */     catch (NumberFormatException nfe) {
/* 188 */       System.err.println("ERRORE in conversione numero " + nfe + ". Riga ignorata.");
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.poliba.lca.routing.utils.DAO
 * JD-Core Version:    0.6.2
 */