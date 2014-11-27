/*     */ package it.cyberdyne.dss.routing.engine;
/*     */ 
/*     */ import it.cyberdyne.dss.routing.io.InputManager;
/*     */ import it.cyberdyne.dss.routing.model.Node;
/*     */ import it.cyberdyne.dss.routing.model.Vehicle;
/*     */ import it.cyberdyne.dss.routing.utils.Constants;
/*     */ import it.cyberdyne.dss.routing.utils.Utilities;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class ClusterManager
/*     */ {
/*  24 */   private static final String WORK_DIR = Constants.WORK_DIR;
/*  25 */   private static final int DEP_ID = Constants.DEP_ID;
/*     */   private InputManager m_iMan;
/*     */   private ArrayList<Cluster> m_clusters;
/*     */ 
/*     */   public ClusterManager(InputManager iman)
/*     */   {
/*  35 */     this.m_iMan = iman;
/*  36 */     this.m_clusters = new ArrayList();
/*     */   }
/*     */ 
/*     */   public int process()
/*     */   {
/*  72 */     System.out.println("DEBUG. Inizio ClusterManager.process()");
/*  73 */     boolean fractionability = Node.isDemandDivisible();
/*  74 */     float granularity = Node.getDemandGranularity().floatValue();
/*     */ 
/*  77 */     if ((this.m_clusters != null) && (this.m_clusters.size() > 0)) {
/*  78 */       this.m_clusters = new ArrayList();
/*     */     }
/*     */ 
/*  82 */     ArrayList candidate_seeds = getSortedNodes(Constants.DEP_ID);
/*     */ 
/*  86 */     PrintStream psUnservible = getTempLogStream("unseribles");
/*  87 */     PrintStream psWaived = getTempLogStream("waiveds");
/*     */ 
/*  89 */     ArrayList vehics = this.m_iMan.getVehicles();
/*  90 */     for (int i = candidate_seeds.size() - 1; i > 0; i--) {
/*  91 */       Node seed = (Node)candidate_seeds.get(i);
/*     */ 
/*  94 */       System.out.println("DEBUG: candidato seed " + seed.debugString(DEP_ID) + " demand = " + seed.getDemand());
/*     */ 
/*  96 */       if (seed.getDemand() <= 0.0F) {
/*  97 */         System.out.println("Candidato saltato per domanda nulla.");
/*     */       }
/*     */       else
/*     */       {
/* 101 */         int k = 0;
/* 102 */         while (!seed.isFullyClustered())
/*     */         {
/* 105 */           seed = (Node)seed.clone();
/*     */ 
/* 108 */           Vehicle v = nextAvailableVehicle(vehics, seed, fractionability, granularity);
/* 109 */           if (v != null)
/*     */           {
/* 111 */             v.setAssigned(true);
/* 112 */             seed.setSeedId(seed.getId());
/*     */ 
/* 116 */             Cluster clst = new Cluster(seed, v);
/* 117 */             clst.fill(this.m_iMan.getNodes(), this.m_iMan.getDistRow(seed.getId()));
/* 118 */             System.out.println("process(). k=" + k + " clone seedid" + seed.getId() + " " + seed.getServedDemand() + "/" + seed.getDemand());
/*     */ 
/* 120 */             System.out.println("Cluster con solo vincolo portata (" + v.getMaxLoad() + "): ");
/* 121 */             clst.print(System.out); System.out.println();
/*     */ 
/* 125 */             Routing router = new Routing(clst, this.m_iMan.getNodeByID(DEP_ID), this.m_iMan);
/*     */             try
/*     */             {
/* 128 */               router.process();
/*     */ 
/* 132 */               while (((clst.getTotDistance() > v.getMaxDistance()) || (clst.getTotTime() > v.getMaxTime())) && (clst.getNodes().size() > 1))
/*     */               {
/* 134 */                 System.out.println("Riduzione cluster " + seed.getId());
/* 135 */                 System.out.println("distanza corrente: " + clst.getTotDistance());
/* 136 */                 System.out.println("tempo corrente: " + clst.getTotTime() + " / " + v.getMaxTime());
/* 137 */                 System.out.println("Devo rimuovere un nodo dal cluster " + seed.getId());
/* 138 */                 clst.removeFarthestNode();
/* 139 */                 router.process();
/*     */               }
/*     */             }
/*     */             catch (Exception e) {
/* 143 */               System.err.println("ERROR: errore in routing");
/*     */             }
/*     */ 
/* 147 */             markNodes(clst.getNodes(), seed.getId());
/* 148 */             System.out.println("DEBUG: seed=" + seed.getId() + ":");
/* 149 */             Utilities.printArrayList(clst.getNodes());
/* 150 */             this.m_clusters.add(clst);
/*     */           }
/*     */           else {
/* 153 */             System.out.println("Prima createWaivedCluster. candidato seed " + seed.debugString(seed.getId()));
/* 154 */             Vehicle locV = createWaivedCluster(vehics, seed);
/* 155 */             System.out.println("Dopo createWaivedCluster. candidato seed " + seed.debugString(seed.getId()));
/* 156 */             if (locV != null)
/*     */             {
/* 158 */               printWaived(seed.getId(), locV.getCode(), fractionability, psWaived);
/* 159 */               printWaived(seed.getId(), locV.getCode(), fractionability, System.out);
/* 160 */               ((Node)candidate_seeds.get(i)).alignsToClone(seed);
/*     */             }
/*     */             else
/*     */             {
/* 166 */               printUnservible(seed.getId(), psUnservible);
/* 167 */               printUnservible(seed.getId(), System.out);
/* 168 */               break;
/*     */             }
/*     */ 
/*     */           }
/*     */ 
/* 173 */           k++;
/* 174 */           boolean bloccoSicurezza = true;
/* 175 */           if ((k > 5) && (bloccoSicurezza)) {
/* 176 */             System.out.println("Blocco di sicurezza a k=" + k + " cicli");
/* 177 */             break;
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 183 */     closeTmpLog(psUnservible);
/* 184 */     closeTmpLog(psWaived);
/* 185 */     System.out.println("Stampa dei cluster/route a fine process():");
/* 186 */     printClusters();
/*     */ 
/* 188 */     return this.m_clusters.size();
/*     */   }
/*     */ 
/*     */   private void markNodes(ArrayList<Node> nodes, int seedId)
/*     */   {
/* 196 */     for (int i = 0; i < nodes.size(); i++)
/* 197 */       this.m_iMan.markNodeAsClustered(((Node)nodes.get(i)).getId(), seedId);
/*     */   }
/*     */ 
/*     */   private ArrayList<Node> getSortedNodes(int nodeId)
/*     */   {
/* 207 */     return Utilities.getSortedNodes(nodeId, this.m_iMan);
/*     */   }
/*     */ 
/*     */   private Vehicle nextAvailableVehicle(ArrayList<Vehicle> vs, Node seed, boolean fractionability, float granularity)
/*     */   {
/* 233 */     Vehicle v = null;
/*     */     try
/*     */     {
/* 236 */       double dist = this.m_iMan.getDistanceRT(DEP_ID, seed.getId());
/* 237 */       double time = this.m_iMan.getTimeRT(DEP_ID, seed.getId()) + seed.getServiceTime();
/*     */ 
/* 239 */       if (seed.getId() == 50) {
/* 240 */         System.out.println("timeRT = " + time);
/*     */       }
/*     */ 
/* 247 */       float demand = seed.getDemand();
/* 248 */       if (granularity > 0.0F) {
/* 249 */         demand = (float)Utilities.quantizeLoad(demand, granularity);
/*     */       }
/*     */ 
/* 252 */       for (int i = 0; i < vs.size(); i++) {
/* 253 */         Vehicle tmpV = (Vehicle)vs.get(i);
/* 254 */         if ((!tmpV.isAssigned()) && ((tmpV.getMaxLoad() >= demand) || (fractionability)) && (tmpV.getMaxDistance() >= dist) && (tmpV.getMaxTime() >= time))
/*     */         {
/* 259 */           v = (Vehicle)vs.get(i);
/*     */         }
/*     */       }
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 265 */       System.err.println("ERRORE in assegnazione veicolo " + e);
/*     */     }
/*     */ 
/* 268 */     return v;
/*     */   }
/*     */ 
/*     */   private Vehicle createWaivedCluster(ArrayList<Vehicle> vs, Node seed)
/*     */   {
/* 288 */     Vehicle v = null;
/*     */ 
/* 290 */     for (int i = 0; i < vs.size(); i++) {
/* 291 */       if (!((Vehicle)vs.get(i)).isAssigned()) {
/* 292 */         v = (Vehicle)vs.get(i);
/*     */       }
/*     */     }
/*     */ 
/* 296 */     if (v != null) {
/* 297 */       Cluster clst = new Cluster(seed, v);
/* 298 */       clst.setWaived(true);
/* 299 */       v.setAssigned(true);
/* 300 */       clst.waivedAddNode(seed);
/* 301 */       Routing router = new Routing(clst, this.m_iMan);
/*     */       try {
/* 303 */         router.process();
/*     */       }
/*     */       catch (Exception e) {
/* 306 */         System.err.println("ERROR: errore in routing per il cluster in deroga con seed = " + seed.getId());
/*     */       }
/* 308 */       this.m_clusters.add(clst);
/*     */     }
/* 310 */     return v;
/*     */   }
/*     */ 
/*     */   public ArrayList<Cluster> getClusters()
/*     */   {
/* 319 */     return this.m_clusters;
/*     */   }
/*     */ 
/*     */   public void printClusters()
/*     */   {
/* 328 */     printClusters(System.out);
/*     */   }
/*     */ 
/*     */   public void printClusters(String outPath)
/*     */   {
/*     */     try
/*     */     {
/* 337 */       FileOutputStream ostream = new FileOutputStream(outPath);
/* 338 */       printClusters(new PrintStream(ostream));
/*     */     }
/*     */     catch (FileNotFoundException fnfe) {
/* 341 */       System.err.println("Errore in printClusters(): " + fnfe);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void printClusters(PrintStream out)
/*     */   {
/* 350 */     if ((this.m_clusters != null) && (!this.m_clusters.isEmpty()))
/* 351 */       for (int i = 0; i < this.m_clusters.size(); i++) {
/* 352 */         ((Cluster)this.m_clusters.get(i)).print(out);
/* 353 */         out.println();
/*     */       }
/*     */   }
/*     */ 
/*     */   public void printTours()
/*     */   {
/* 361 */     printTours(System.out);
/*     */   }
/*     */ 
/*     */   public void printTours(PrintStream out)
/*     */   {
/* 369 */     if ((this.m_clusters != null) && (!this.m_clusters.isEmpty()))
/* 370 */       for (int i = 0; i < this.m_clusters.size(); i++) {
/* 371 */         ((Cluster)this.m_clusters.get(i)).printTour(out);
/* 372 */         out.println();
/*     */       }
/*     */   }
/*     */ 
/*     */   private PrintStream getTempLogStream(String fileBaseName)
/*     */   {
/* 383 */     PrintStream ps = null;
/*     */     try {
/* 385 */       String filePath = WORK_DIR + File.separator + fileBaseName + "_" + this.m_iMan.getCallId() + ".txt";
/*     */ 
/* 387 */       ps = new PrintStream(new FileOutputStream(filePath));
/*     */     }
/*     */     catch (IOException ioe)
/*     */     {
/* 391 */       ioe.printStackTrace();
/*     */     }
/* 393 */     return ps;
/*     */   }
/*     */ 
/*     */   private void printUnservible(int seedId, PrintStream ps)
/*     */   {
/* 405 */     Node seed = this.m_iMan.getNodeByID(seedId);
/* 406 */     String warningStr = "ATTENZIONE. Insufficienza di veicoli. Nodo " + seed.getLabel() + " (" + seedId + ") ";
/*     */ 
/* 409 */     if (seed.getServedDemand() > 0.0F) {
/* 410 */       warningStr = warningStr + "servito solo PARZIALMENTE (" + seed.getServedDemand() + "/" + seed.getDemand() + ")";
/*     */     }
/*     */     else
/*     */     {
/* 414 */       warningStr = warningStr + "NON servito";
/*     */     }
/*     */ 
/* 417 */     ps.println(warningStr);
/*     */   }
/*     */ 
/*     */   private void printWaived(int seedId, String vehCode, boolean fractionability, PrintStream ps)
/*     */   {
/* 429 */     Node seed = this.m_iMan.getNodeByID(seedId);
/* 430 */     String warningStr = "ATTENZIONE: il candidato seed " + seed.getLabel() + " (" + seedId + ") viola i vincoli di ";
/*     */ 
/* 432 */     if (!fractionability)
/* 433 */       warningStr = warningStr + "capacita' o ";
/* 434 */     warningStr = warningStr + "di distanza o di tempo di percorrenza. Si crea un cluster in deroga";
/*     */     try {
/* 436 */       warningStr = warningStr + " [";
/* 437 */       if (!fractionability)
/* 438 */         warningStr = warningStr + "demand=" + seed.getRemainingDemand() + "/" + seed.getDemand() + ";";
/* 439 */       warningStr = warningStr + " distanza=" + this.m_iMan.getDistanceRT(DEP_ID, seedId) + "; tempo=" + Math.round(this.m_iMan.getTimeRT(DEP_ID, seedId) + seed.getServiceTime()) + " codice veicolo=" + vehCode + "]";
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/*     */ 
/* 445 */     ps.println(warningStr);
/*     */   }
/*     */ 
/*     */   private void closeTmpLog(PrintStream ps)
/*     */   {
/* 450 */     if (ps != null)
/* 451 */       ps.close();
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.engine.ClusterManager
 * JD-Core Version:    0.6.2
 */