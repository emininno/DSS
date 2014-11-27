/*     */ package it.cyberdyne.dss.routing;
/*     */ 
/*     */ import it.cyberdyne.dss.routing.engine.ClusterManager;
/*     */ import it.cyberdyne.dss.routing.io.InputManager;
/*     */ import it.cyberdyne.dss.routing.io.OutputManager;
/*     */ import it.cyberdyne.dss.routing.utils.Constants;
/*     */ import it.cyberdyne.dss.routing.utils.DAO;
/*     */ import java.io.File;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.PrintStream;
/*     */ 
/*     */ public class TourManager
/*     */ {
/*     */   public static final int RESULT_OK = 0;
/*     */   public static final int RESULT_FAULT = -1;
/*  25 */   private static final int DEP_ID = Constants.DEP_ID;
/*  26 */   private static final String WORK_DIR = Constants.WORK_DIR;
/*     */   private int m_callId;
/*     */ 
/*     */   public int start(int call_id, String inputDirPath)
/*     */   {
/*  41 */     this.m_callId = call_id;
/*  42 */     int esito = 0;
/*     */     try {
/*  44 */       InputManager iMan = new InputManager(call_id, inputDirPath);
/*  45 */       if (iMan.check())
/*  46 */         iMan.readFiles();
/*     */       else {
/*  48 */         throw new RuntimeException("Errore: check dei file di input fallito!");
/*     */       }
/*  50 */       ClusterManager clusterMan = new ClusterManager(iMan);
/*  51 */       int ncl = clusterMan.process();
/*  52 */       System.out.println("Debug: trovati " + ncl + " cluster");
/*  53 */       if (ncl > 0)
/*     */       {
/*  57 */         String clustersFilePath = WORK_DIR + File.separator + "clusters_" + call_id + ".txt";
/*     */ 
/*  60 */         PrintStream pw = new PrintStream(new FileOutputStream(clustersFilePath));
/*     */ 
/*  62 */         clusterMan.printClusters(pw);
/*  63 */         pw.close();
/*     */ 
/*  66 */         System.out.println("\nDEBUG: stampa dei routing:");
/*  67 */         clusterMan.printTours();
/*  68 */         String routesFilePath = WORK_DIR + File.separator + "routes_" + call_id + ".txt";
/*     */ 
/*  71 */         pw = new PrintStream(new FileOutputStream(routesFilePath));
/*  72 */         clusterMan.printTours(pw);
/*  73 */         pw.close();
/*     */ 
/*  78 */         OutputManager outMan = new OutputManager(clusterMan.getClusters(), call_id);
/*     */ 
/*  81 */         outMan.writeFiles();
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*  87 */       System.err.println("Errore. Esecuzione di TourFinder abortita");
/*  88 */       e.printStackTrace();
/*  89 */       esito = -1;
/*     */     }
/*     */ 
/*  92 */     return esito;
/*     */   }
/*     */ 
/*     */   public int start(int call_id)
/*     */   {
/* 100 */     return start(call_id, InputManager.INPUT_DIR);
/*     */   }
/*     */ 
/*     */   public int getCurrentCallId()
/*     */   {
/* 107 */     return this.m_callId;
/*     */   }
/*     */ 
/*     */   private void test_db(Double[][] matrix)
/*     */   {
/* 116 */     DAO.matrixToLinear(matrix, WORK_DIR, "lin_distMatrix_ncs", this.m_callId, false);
/* 117 */     DAO.matrixToLinear(matrix, WORK_DIR, "lin_distMatrix", this.m_callId, true);
/*     */ 
/* 120 */     double[][] m = { { 11.0D, 12.0D, 13.0D, 14.0D }, { 21.0D, 22.0D, 23.0D, 24.0D }, { 31.0D, 32.0D, 33.0D, 34.0D } };
/*     */ 
/* 125 */     Double[][] dm = new Double[m.length][m[0].length];
/* 126 */     for (int i = 0; i < m.length; i++) {
/* 127 */       for (int j = 0; j < m[i].length; j++)
/* 128 */         dm[i][j] = Double.valueOf(m[i][j]);
/*     */     }
/* 130 */     DAO.matrixToLinear(dm, WORK_DIR, "lin_testMatrix", 999, true);
/*     */ 
/* 132 */     DAO.linearToMatrix(WORK_DIR, "lin_distMatrix_ncs", this.m_callId);
/* 133 */     DAO.linearToMatrix(WORK_DIR, "lin_distMatrix", this.m_callId);
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.TourManager
 * JD-Core Version:    0.6.2
 */