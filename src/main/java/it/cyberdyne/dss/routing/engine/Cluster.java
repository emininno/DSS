/*     */ package it.cyberdyne.dss.routing.engine;
/*     */ 
/*     */ import it.cyberdyne.dss.routing.model.Node;
/*     */ import it.cyberdyne.dss.routing.model.Vehicle;
/*     */ import it.cyberdyne.dss.routing.utils.Utilities;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ 
/*     */ public class Cluster
/*     */ {
/*  36 */   public static final float GRANULARITY = Node.getDemandGranularity();
/*     */ 
/*  39 */   public static final boolean DEMAND_DIVISIBLE = Node.isDemandDivisible();
/*     */   private final Node m_seed;
/*     */   private final Vehicle m_vehicle;
/*     */   private ArrayList<Node> m_nodes;
/*     */   private ArrayList<Node> m_tour;
/*     */   private double m_duration;
/*     */   private double m_length;
/*     */   private double m_load;
/*     */   private boolean m_waived;
/*     */ 
/*     */   public Cluster(Node seed, Vehicle v)
/*     */   {
/*  65 */     this.m_seed = seed;
/*  66 */     this.m_nodes = new ArrayList();
/*     */ 
/*  68 */     this.m_vehicle = v;
/*     */ 
/*  70 */     this.m_load = 0.0D;
/*     */ 
/*  72 */     this.m_duration = 0.0D;
/*  73 */     this.m_length = 0.0D;
/*     */ 
/*  75 */     this.m_waived = false;
/*     */   }
/*     */ 
/*     */   public ArrayList<Node> fill(ArrayList<Node> allNodes, Double[] dists)
/*     */   {
/*  86 */     if ((this.m_nodes != null) && (this.m_nodes.size() > 0)) {
/*  87 */       this.m_nodes = new ArrayList();
/*     */     }
/*     */ 
/*  92 */     sortToSeed(dists, allNodes);
/*     */ 
/*  98 */     for (int i = 0; i < allNodes.size(); i++)
/*     */     {
/* 100 */       Node n = (Node)allNodes.get(i);
/*     */ 
/* 107 */       if (i == 0) {
/* 108 */         n = this.m_seed;
/* 109 */         n.setDistance(((Node)allNodes.get(i)).getDistance());
/*     */       }
/*     */ 
/* 114 */       if ((n.getRemainingDemand() > 0.0F) || (n.getId() == this.m_seed.getId()))
/*     */       {
/* 116 */         boolean b = addNodeIfCan(n);
/* 117 */         if (!b)
/*     */         {
/* 119 */           System.out.println("DEBUG. Chiusura Cluster " + this.m_seed.getId() + ": impossibile aggiungere il nodo " + n.getId());
/*     */ 
/* 121 */           break;
/*     */         }
/*     */ 
/* 134 */         if (i == 0)
/*     */         {
/* 138 */           ((Node)allNodes.get(0)).alignsToClone(n);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 143 */     return this.m_nodes;
/*     */   }
/*     */ 
/*     */   private void sortToSeed(Double[] distsToSeed, ArrayList<Node> toBeSortedNodes)
/*     */   {
/* 156 */     for (int i = 0; i < toBeSortedNodes.size(); i++) {
/* 157 */       ((Node)toBeSortedNodes.get(i)).setDistance(distsToSeed[i]);
/*     */     }
/* 159 */     Collections.sort(toBeSortedNodes);
/*     */   }
/*     */ 
/*     */   /** @deprecated */
/*     */   private void addNode(Node n)
/*     */   {
/* 167 */     n.setSeedId(this.m_seed.getId());
/* 168 */     this.m_load += n.getDemand();
/* 169 */     this.m_nodes.add(n);
/*     */   }
/*     */ 
/*     */   public boolean addNodeIfCan(Node n)
/*     */   {
/* 187 */     boolean result = false;
/* 188 */     if (DEMAND_DIVISIBLE) {
/* 189 */       result = addPartialNode(n, GRANULARITY);
/*     */     }
/*     */     else
/*     */     {
/* 194 */       float quantum = n.getRemainingDemand();
/* 195 */       if (GRANULARITY > 0.0F)
/* 196 */         quantum = (float)quantizeLoad(quantum, GRANULARITY);
/* 197 */       result = addPartialNode(n, quantum);
/*     */     }
/* 199 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean smallQuantumAddPartialNode(Node n, float quantum)
/*     */   {
/* 211 */     if (Utilities.isZero(quantum)) {
/* 212 */       return addPartialNode(n);
/*     */     }
/* 214 */     boolean result = false;
/* 215 */     float demandOnQuantum = n.getRemainingDemand() / quantum;
/* 216 */     float decimalPart = demandOnQuantum - (int)demandOnQuantum;
/* 217 */     int slots = (int)Math.ceil(n.getRemainingDemand() / quantum);
/* 218 */     double load = this.m_load;
/* 219 */     int i = 0;
/* 220 */     for (i = 0; i < slots; i++) {
/* 221 */       load += quantum;
/* 222 */       if (load > this.m_vehicle.getMaxLoad()) {
/* 223 */         load -= quantum;
/* 224 */         break;
/*     */       }
/*     */     }
/* 227 */     if (i > 0)
/*     */     {
/* 230 */       this.m_load = load;
/*     */ 
/* 232 */       if (Utilities.doubleEqual(decimalPart, 0.0D)) {
/* 233 */         n.addServedDemand(i * quantum, this.m_seed.getId());
/*     */       }
/*     */       else
/*     */       {
/* 237 */         float increment = (i - 1 + decimalPart) * quantum;
/* 238 */         n.addServedDemand(increment, this.m_seed.getId());
/*     */       }
/*     */ 
/* 242 */       n.setSeedId(this.m_seed.getId());
/* 243 */       this.m_nodes.add(n);
/* 244 */       result = true;
/*     */     }
/* 246 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean addPartialNode(Node n, float quantum)
/*     */   {
/* 274 */     if (Utilities.isZero(quantum)) {
/* 275 */       return addPartialNode(n);
/*     */     }
/* 277 */     if (quantum < 0.5D) {
/* 278 */       return smallQuantumAddPartialNode(n, quantum);
/*     */     }
/* 280 */     boolean result = false;
/* 281 */     float demandOnQuantum = n.getRemainingDemand() / quantum;
/* 282 */     float decimalPart = demandOnQuantum - (int)demandOnQuantum;
/* 283 */     int slots = (int)Math.ceil(n.getRemainingDemand() / quantum);
/*     */ 
/* 285 */     double load = this.m_load;
/* 286 */     float partialDemand = 0.0F;
/*     */ 
/* 289 */     int i = 1;
/* 290 */     for (i = 1; i <= slots; i++) {
/* 291 */       load += quantum;
/* 292 */       if (load > this.m_vehicle.getMaxLoad()) {
/* 293 */         load -= quantum;
/* 294 */         break;
/*     */       }
/*     */ 
/* 297 */       if ((i == slots) && (!Utilities.doubleEqual(decimalPart, 0.0D)))
/*     */       {
/* 299 */         partialDemand += decimalPart * quantum;
/*     */       }
/*     */       else {
/* 302 */         partialDemand += quantum;
/*     */       }
/*     */     }
/*     */ 
/* 306 */     if ((load > this.m_load) && (slots > 0)) {
/* 307 */       this.m_load = load;
/*     */ 
/* 310 */       n.addServedDemand(partialDemand, this.m_seed.getId());
/* 311 */       n.setSeedId(this.m_seed.getId());
/* 312 */       this.m_nodes.add(n);
/* 313 */       result = true;
/*     */     }
/*     */ 
/* 316 */     System.out.println("addPartialNode. seed=" + this.m_seed.getId() + " nodo=" + n.getId() + " load=" + this.m_load);
/*     */ 
/* 318 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean addPartialNode(Node n)
/*     */   {
/* 332 */     boolean added = false;
/* 333 */     float chargebleDemand = 0.0F;
/* 334 */     chargebleDemand = (float)(this.m_vehicle.getMaxLoad() - this.m_load);
/* 335 */     if (chargebleDemand > 0.0D) {
/* 336 */       if (chargebleDemand > n.getRemainingDemand())
/* 337 */         chargebleDemand = n.getRemainingDemand();
/* 338 */       n.addServedDemand(chargebleDemand, this.m_seed.getId());
/*     */ 
/* 341 */       this.m_load += chargebleDemand;
/*     */ 
/* 344 */       n.setSeedId(this.m_seed.getId());
/* 345 */       this.m_nodes.add(n);
/*     */ 
/* 347 */       added = true;
/*     */     }
/* 349 */     return added;
/*     */   }
/*     */ 
/*     */   private double quantizeLoad(double load, float granularity)
/*     */   {
/* 356 */     return Utilities.quantizeLoad(load, granularity);
/*     */   }
/*     */ 
/*     */   public void waivedAddNode(Node n)
/*     */   {
/* 372 */     if (DEMAND_DIVISIBLE) {
/* 373 */       addNodeIfCan(n);
/*     */     }
/*     */     else
/*     */     {
/* 377 */       n.setSeedId(this.m_seed.getId());
/* 378 */       n.addServedDemand(n.getDemand(), this.m_seed.getId());
/*     */ 
/* 382 */       if (GRANULARITY > 0.0F)
/* 383 */         this.m_load += (float)Utilities.quantizeLoad(n.getDemand(), GRANULARITY);
/*     */       else {
/* 385 */         this.m_load += n.getDemand();
/*     */       }
/* 387 */       this.m_nodes.add(n);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeFarthestNode()
/*     */   {
/* 399 */     if ((this.m_nodes != null) && (this.m_nodes.size() > 0)) {
/* 400 */       Node n = (Node)this.m_nodes.get(this.m_nodes.size() - 1);
/* 401 */       n.release();
/*     */ 
/* 403 */       this.m_nodes.remove(n);
/*     */ 
/* 405 */       double tmp = this.m_load;
/* 406 */       if (GRANULARITY > 0.0F)
/* 407 */         this.m_load -= Utilities.quantizeLoad(n.removeDemandOnCluster(this.m_seed.getId()), GRANULARITY);
/*     */       else
/* 409 */         this.m_load -= n.removeDemandOnCluster(this.m_seed.getId());
/* 410 */       System.out.println("removeFarthestNode. m_load prima e dopo = " + tmp + "; " + this.m_load);
/* 411 */       clearTour();
/*     */     }
/*     */     else {
/* 414 */       System.err.println(" WARNING. Nessun nodo da rimuovere: cluster vuoto!");
/*     */     }
/*     */   }
/*     */ 
/*     */   public void setWaived(boolean waiv)
/*     */   {
/* 420 */     this.m_waived = waiv;
/*     */   }
/*     */ 
/*     */   public boolean isWaived() {
/* 424 */     return this.m_waived;
/*     */   }
/*     */ 
/*     */   public ArrayList<Node> getNodes()
/*     */   {
/* 430 */     return this.m_nodes;
/*     */   }
/*     */ 
/*     */   public ArrayList<Node> getTour()
/*     */   {
/* 435 */     return this.m_tour;
/*     */   }
/*     */ 
/*     */   public Node getSeed()
/*     */   {
/* 440 */     return this.m_seed;
/*     */   }
/*     */ 
/*     */   public int getSeedId()
/*     */   {
/* 447 */     return this.m_seed.getId();
/*     */   }
/*     */ 
/*     */   public boolean isEmpty()
/*     */   {
/* 452 */     if ((this.m_nodes != null) && (this.m_nodes.size() > 0)) {
/* 453 */       return false;
/*     */     }
/* 455 */     return true;
/*     */   }
/*     */ 
/*     */   public void setTour(ArrayList<Node> tour)
/*     */   {
/* 464 */     setTour(tour, 0.0D, 0.0D);
/*     */   }
/*     */ 
/*     */   public void setTour(ArrayList<Node> tour, double length, double duration)
/*     */   {
/* 476 */     this.m_tour = null;
/* 477 */     this.m_tour = new ArrayList(tour);
/* 478 */     this.m_length = length;
/* 479 */     this.m_duration = duration;
/*     */   }
/*     */ 
/*     */   public void clearTour()
/*     */   {
/* 487 */     this.m_tour = null;
/* 488 */     if ((this.m_nodes != null) && (this.m_nodes.size() > 0))
/* 489 */       for (int i = 0; i < this.m_nodes.size(); i++)
/* 490 */         ((Node)this.m_nodes.get(i)).setRouted(false);
/*     */   }
/*     */ 
/*     */   public Vehicle getVehicle()
/*     */   {
/* 505 */     return this.m_vehicle;
/*     */   }
/*     */ 
/*     */   public double getTotLoad()
/*     */   {
/* 511 */     return this.m_load;
/*     */   }
/*     */ 
/*     */   public double getTotTime()
/*     */   {
/* 518 */     return this.m_duration;
/*     */   }
/*     */ 
/*     */   public double getTotDistance()
/*     */   {
/* 525 */     return this.m_length;
/*     */   }
/*     */ 
/*     */   public void print(PrintStream out)
/*     */   {
/* 537 */     out.print("Cluster (seed=" + this.m_seed.getId() + "): {");
/* 538 */     if ((this.m_nodes != null) && (!this.m_nodes.isEmpty())) {
/* 539 */       for (int i = 0; i < this.m_nodes.size(); i++)
/*     */       {
/* 541 */         Node n = (Node)this.m_nodes.get(i);
/* 542 */         out.print(n.getId() + "(" + n.getDemandOnCluster(this.m_seed.getId()) + "/" + n.getDemand() + ")");
/* 543 */         if (i < this.m_nodes.size() - 1)
/* 544 */           out.print(", ");
/*     */       }
/* 546 */       out.print(" [LOAD=" + Utilities.format3fract(this.m_load) + " notQtzLoad=" + Utilities.format3fract(notQuantizedLoad()) + "]");
/*     */ 
/* 548 */       if (this.m_waived)
/* 549 */         out.print(" deroga");
/*     */     }
/*     */     else {
/* 552 */       out.print("empty or null");
/*     */     }
/* 554 */     out.print("}");
/*     */   }
/*     */ 
/*     */   private double notQuantizedLoad()
/*     */   {
/* 560 */     double nqload = 0.0D;
/* 561 */     if ((this.m_nodes != null) && (!this.m_nodes.isEmpty())) {
/* 562 */       for (int i = 0; i < this.m_nodes.size(); i++) {
/* 563 */         nqload += ((Node)this.m_nodes.get(i)).getDemandOnCluster(this.m_seed.getId());
/*     */       }
/*     */     }
/* 566 */     return nqload;
/*     */   }
/*     */ 
/*     */   public void printTour(PrintStream ps)
/*     */   {
/* 573 */     ps.print("Route per cluster (" + this.m_seed.getId() + "): {");
/* 574 */     if ((this.m_tour != null) && (!this.m_tour.isEmpty())) {
/* 575 */       for (int i = 0; i < this.m_tour.size(); i++)
/*     */       {
/* 579 */         ps.print(((Node)this.m_tour.get(i)).debugString(this.m_seed.getId()));
/*     */ 
/* 581 */         if (i < this.m_tour.size() - 1)
/* 582 */           ps.print(", ");
/*     */       }
/*     */     }
/*     */     else {
/* 586 */       ps.print("empty or null");
/*     */     }
/* 588 */     ps.print("}");
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 593 */     String str = "Cluster (" + this.m_seed.getId() + "): {";
/* 594 */     if ((this.m_nodes != null) && (!this.m_nodes.isEmpty())) {
/* 595 */       for (int i = 0; i < this.m_nodes.size(); i++) {
/* 596 */         str = str + "" + ((Node)this.m_nodes.get(i)).getId();
/* 597 */         if (i < this.m_nodes.size() - 1)
/* 598 */           str = str + ", ";
/*     */       }
/*     */     }
/*     */     else {
/* 602 */       str = str + "empty or null";
/*     */     }
/* 604 */     str = str + "}";
/*     */ 
/* 606 */     return str;
/*     */   }
/*     */ 
/*     */   public String tourToString()
/*     */   {
/* 611 */     String str = "Route in cluster (" + this.m_seed.getId() + "): {";
/* 612 */     if ((this.m_tour != null) && (!this.m_tour.isEmpty())) {
/* 613 */       for (int i = 0; i < this.m_tour.size(); i++) {
/* 614 */         str = str + "" + ((Node)this.m_tour.get(i)).getId();
/* 615 */         if (i < this.m_tour.size() - 1)
/* 616 */           str = str + ", ";
/*     */       }
/*     */     }
/*     */     else {
/* 620 */       str = str + "empty or null";
/*     */     }
/* 622 */     str = str + "}";
/*     */ 
/* 624 */     return str;
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.engine.Cluster
 * JD-Core Version:    0.6.2
 */