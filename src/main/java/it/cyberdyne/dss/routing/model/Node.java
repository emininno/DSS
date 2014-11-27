/*     */ package it.cyberdyne.dss.routing.model;
/*     */ 
/*     */ import it.cyberdyne.dss.routing.utils.Constants;
/*     */ import it.cyberdyne.dss.routing.utils.Utilities;
/*     */ import java.io.PrintStream;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Set;
/*     */ 
/*     */ public class Node
/*     */   implements Comparable, Cloneable
/*     */ {
/*     */   private int m_id;
/*     */   private String m_label;
/*     */   private float m_demand;
/*     */   private float m_srvTime;
/*     */   private float m_servedDemand;
/*     */   private HashMap<Integer, Float> m_dmnPerCluster;
/*     */   private double m_arrivalTime;
/*     */   private int m_seedId;
/*     */   private Double m_distance;
/*     */   private boolean m_routed;
/*     */   private static Float ms_granularity;
/*  66 */   private static boolean ms_demandDivisible = true;
/*     */ 
/*     */   public Node(int id, String label, float demand, float serviceTime)
/*     */   {
/*  77 */     this.m_id = id;
/*  78 */     this.m_label = label;
/*  79 */     this.m_demand = demand;
/*  80 */     this.m_srvTime = serviceTime;
/*     */ 
/*  82 */     this.m_seedId = Constants.DEP_ID;
/*     */   }
/*     */ 
/*     */   public Object clone()
/*     */   {
/*     */     try
/*     */     {
/*  90 */       super.clone();
/*     */     }
/*     */     catch (CloneNotSupportedException e)
/*     */     {
/*     */     }
/*  95 */     Node n = new Node(this.m_id, this.m_label, this.m_demand, this.m_srvTime);
/*     */ 
/*  97 */     n.setServedDemand(this.m_servedDemand);
/*     */ 
/*  99 */     n.setRouted(this.m_routed);
/* 100 */     n.m_seedId = this.m_seedId;
/*     */ 
/* 102 */     if ((this.m_dmnPerCluster != null) && (!this.m_dmnPerCluster.isEmpty()))
/*     */     {
/* 104 */       Set list = this.m_dmnPerCluster.keySet();
/* 105 */       Iterator iter = list.iterator();
/* 106 */       while (iter.hasNext()) {
/* 107 */         Integer seedId = (Integer)iter.next();
/* 108 */         Float partialDemand = (Float)this.m_dmnPerCluster.get(seedId);
/*     */ 
/* 110 */         n.setDemandOnCluster(partialDemand.floatValue(), seedId.intValue());
/*     */       }
/*     */     }
/*     */ 
/* 114 */     return n;
/*     */   }
/*     */ 
/*     */   public void alignsToClone(Node other)
/*     */   {
/* 119 */     setServedDemand(other.m_servedDemand);
/* 120 */     setRouted(other.m_routed);
/* 121 */     this.m_seedId = other.m_seedId;
/*     */   }
/*     */ 
/*     */   public int getId()
/*     */   {
/* 127 */     return this.m_id;
/*     */   }
/*     */ 
/*     */   public String getLabel()
/*     */   {
/* 134 */     return this.m_label;
/*     */   }
/*     */ 
/*     */   public float getDemand()
/*     */   {
/* 139 */     return this.m_demand;
/*     */   }
/*     */ 
/*     */   public float getServiceTime()
/*     */   {
/* 146 */     return this.m_srvTime;
/*     */   }
/*     */ 
/*     */   public float getServiceTime(Float coeff)
/*     */   {
/* 155 */     float srvTime = getServiceTime();
/* 156 */     if ((coeff != null) && (coeff.floatValue() > 0.0F)) {
/* 157 */       srvTime += coeff.floatValue() * this.m_demand;
/*     */     }
/*     */ 
/* 160 */     return srvTime;
/*     */   }
/*     */ 
/*     */   public void setDistance(Double d)
/*     */   {
/* 169 */     this.m_distance = d;
/*     */   }
/*     */ 
/*     */   public Double getDistance() {
/* 173 */     return this.m_distance;
/*     */   }
/*     */ 
/*     */   public void setSeedId(int id)
/*     */   {
/* 180 */     this.m_seedId = id;
/*     */   }
/*     */ 
/*     */   public int getSeedId()
/*     */   {
/* 187 */     return this.m_seedId;
/*     */   }
/*     */ 
/*     */   public boolean isClustered()
/*     */   {
/* 193 */     if (this.m_seedId != Constants.DEP_ID) {
/* 194 */       return true;
/*     */     }
/* 196 */     return false;
/*     */   }
/*     */ 
/*     */   public void setRouted(boolean b)
/*     */   {
/* 208 */     this.m_routed = b;
/*     */   }
/*     */ 
/*     */   public boolean isRouted()
/*     */   {
/* 215 */     return this.m_routed;
/*     */   }
/*     */ 
/*     */   public void setArrivalTime(double time)
/*     */   {
/* 224 */     this.m_arrivalTime = time;
/*     */   }
/*     */ 
/*     */   public double getArrivalTime()
/*     */   {
/* 229 */     return this.m_arrivalTime;
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/* 238 */     return "Node [(" + this.m_id + ") " + this.m_label + " " + this.m_servedDemand + " / " + this.m_demand + " cluster(" + this.m_seedId + ", " + this.m_distance + ")]";
/*     */   }
/*     */ 
/*     */   public String debugString(int seedId)
/*     */   {
/* 255 */     float demandFraction = -0.1F;
/* 256 */     if ((this.m_dmnPerCluster != null) && (this.m_dmnPerCluster.get(Integer.valueOf(seedId)) != null)) {
/* 257 */       demandFraction = ((Float)this.m_dmnPerCluster.get(Integer.valueOf(seedId))).floatValue();
/*     */     }
/* 259 */     String str = this.m_id + "(" + Utilities.format3fract(this.m_arrivalTime) + "; " + Utilities.format3fract(demandFraction) + "/" + this.m_demand + "; served=" + this.m_servedDemand + ")";
/*     */ 
/* 264 */     return str;
/*     */   }
/*     */ 
/*     */   public String debugString()
/*     */   {
/* 278 */     String str = this.m_id + "(" + Utilities.format3fract(this.m_arrivalTime) + "; " + Utilities.format3fract(this.m_servedDemand) + "/" + this.m_demand + ")";
/*     */ 
/* 281 */     return str;
/*     */   }
/*     */ 
/*     */   public int compareTo(Object obj)
/*     */   {
/* 293 */     int result = 1;
/* 294 */     if ((obj != null) && ((obj instanceof Node))) {
/* 295 */       Node n = (Node)obj;
/* 296 */       if (this.m_distance.doubleValue() < n.m_distance.doubleValue())
/* 297 */         result = -1;
/* 298 */       else if (this.m_distance.doubleValue() > n.m_distance.doubleValue())
/* 299 */         result = 1;
/* 300 */       else if ((this.m_distance.doubleValue() > n.m_distance.doubleValue() - 1.E-05D) && (this.m_distance.doubleValue() < n.m_distance.doubleValue() + 1.E-05D))
/*     */       {
/* 302 */         result = 0;
/*     */       }
/*     */     }
/* 304 */     return result;
/*     */   }
/*     */ 
/*     */   public void release()
/*     */   {
/* 314 */     setSeedId(Constants.DEP_ID);
/* 315 */     setRouted(false);
/* 316 */     setDistance(Double.valueOf(0.0D));
/*     */   }
/*     */ 
/*     */   public boolean setServedDemand(float demand)
/*     */   {
/* 330 */     boolean result = true;
/* 331 */     if (demand > this.m_demand) {
/* 332 */       result = false;
/* 333 */       System.out.println("WARNING: il sw ha cercato di impostare domanda servita maggiore di quella totale.");
/*     */     }
/*     */     else {
/* 336 */       this.m_servedDemand = demand;
/* 337 */     }return result;
/*     */   }
/*     */ 
/*     */   public boolean addServedDemand(float increment, int currentSeedId)
/*     */   {
/* 347 */     if (this.m_dmnPerCluster == null) {
/* 348 */       this.m_dmnPerCluster = new HashMap();
/*     */     }
/* 350 */     boolean result = true;
/* 351 */     double tmpDemand = this.m_servedDemand + increment;
/* 352 */     if (tmpDemand > this.m_demand) {
/* 353 */       result = false;
/* 354 */       System.out.println("WARNING: il sw ha cercato di incrementare la domanda servita oltre quella totale.");
/*     */     }
/*     */     else {
/* 357 */       this.m_servedDemand += increment;
/* 358 */       this.m_dmnPerCluster.put(Integer.valueOf(currentSeedId), Float.valueOf(increment));
/*     */     }
/* 360 */     return result;
/*     */   }
/*     */ 
/*     */   private void setDemandOnCluster(float partialDemand, int seedId)
/*     */   {
/* 365 */     if (this.m_dmnPerCluster == null)
/* 366 */       this.m_dmnPerCluster = new HashMap();
/* 367 */     this.m_dmnPerCluster.put(Integer.valueOf(seedId), Float.valueOf(partialDemand));
/*     */   }
/*     */ 
/*     */   public float getServedDemand()
/*     */   {
/* 372 */     return this.m_servedDemand;
/*     */   }
/*     */ 
/*     */   public float getDemandOnCluster(int seedId)
/*     */   {
/* 380 */     float dmn = 0.0F;
/* 381 */     if ((this.m_dmnPerCluster != null) && (this.m_dmnPerCluster.get(Integer.valueOf(seedId)) != null)) {
/* 382 */       dmn = ((Float)this.m_dmnPerCluster.get(Integer.valueOf(seedId))).floatValue();
/*     */     }
/* 384 */     return dmn;
/*     */   }
/*     */ 
/*     */   public float removeDemandOnCluster(int seedId)
/*     */   {
/* 397 */     float dmn = 0.0F;
/* 398 */     if ((this.m_dmnPerCluster != null) && (this.m_dmnPerCluster.get(Integer.valueOf(seedId)) != null)) {
/* 399 */       dmn = ((Float)this.m_dmnPerCluster.get(Integer.valueOf(seedId))).floatValue();
/* 400 */       this.m_servedDemand -= dmn;
/* 401 */       this.m_dmnPerCluster.remove(Integer.valueOf(seedId));
/*     */     }
/* 403 */     return dmn;
/*     */   }
/*     */ 
/*     */   public float getRemainingDemand()
/*     */   {
/* 408 */     return this.m_demand - this.m_servedDemand;
/*     */   }
/*     */ 
/*     */   public boolean isFullyServed()
/*     */   {
/* 413 */     if (Utilities.doubleEqual(this.m_demand, this.m_servedDemand)) {
/* 414 */       return true;
/*     */     }
/* 416 */     return false;
/*     */   }
/*     */ 
/*     */   public boolean isFullyClustered()
/*     */   {
/* 422 */     return (isFullyServed()) && (isClustered());
/*     */   }
/*     */ 
/*     */   public static void setDemandGranularity(Float g)
/*     */   {
/* 435 */     ms_granularity = g;
/*     */   }
/*     */ 
/*     */   public static Float getDemandGranularity() {
/* 439 */     if (ms_granularity == null) {
/* 440 */       return Float.valueOf(0.0F);
/*     */     }
/* 442 */     return ms_granularity;
/*     */   }
/*     */ 
/*     */   public static void setDemandDivisible(boolean divisible)
/*     */   {
/* 452 */     ms_demandDivisible = divisible;
/*     */   }
/*     */ 
/*     */   public static boolean isDemandDivisible() {
/* 456 */     return ms_demandDivisible;
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.model.Node
 * JD-Core Version:    0.6.2
 */