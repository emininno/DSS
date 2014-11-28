 package it.cyberdyne.dss.routing.model;
 
 import it.cyberdyne.dss.routing.utils.Constants;
 import it.cyberdyne.dss.routing.utils.Utilities;
 import java.util.HashMap;
 import java.util.Iterator;
 import java.util.Set;
 
 public class Node
   implements Comparable, Cloneable
 {
   private final int m_id;
   private final String m_label;
   private final float m_demand;
   private final float m_srvTime;
   private float m_servedDemand;
   private HashMap<Integer, Float> m_dmnPerCluster;
   private double m_arrivalTime;
   private int m_seedId;
   private Double m_distance;
   private boolean m_routed;
   private static Float ms_granularity;
   private static boolean ms_demandDivisible = true;
 
   public Node(int id, String label, float demand, float serviceTime)
   {
     this.m_id = id;
     this.m_label = label;
     this.m_demand = demand;
     this.m_srvTime = serviceTime;
 
     this.m_seedId = Constants.DEP_ID;
   }
   
   @Override
   public Object clone() throws CloneNotSupportedException
   {
     try
     {
       super.clone();
     }
     catch (CloneNotSupportedException e)
     {
     }
     Node n = new Node(this.m_id, this.m_label, this.m_demand, this.m_srvTime);
 
     n.setServedDemand(this.m_servedDemand);
 
     n.setRouted(this.m_routed);
     n.m_seedId = this.m_seedId;
 
     if ((this.m_dmnPerCluster != null) && (!this.m_dmnPerCluster.isEmpty()))
     {
       Set list = this.m_dmnPerCluster.keySet();
       Iterator iter = list.iterator();
       while (iter.hasNext()) {
         Integer seedId = (Integer)iter.next();
         Float partialDemand = this.m_dmnPerCluster.get(seedId);
 
         n.setDemandOnCluster(partialDemand, seedId);
       }
     }
 
     return n;
   }
 
   public void alignsToClone(Node other)
   {
     setServedDemand(other.m_servedDemand);
     setRouted(other.m_routed);
     this.m_seedId = other.m_seedId;
   }
 
   public int getId()
   {
     return this.m_id;
   }
 
   public String getLabel()
   {
     return this.m_label;
   }
 
   public float getDemand()
   {
     return this.m_demand;
   }
 
   public float getServiceTime()
   {
     return this.m_srvTime;
   }
 
   public float getServiceTime(Float coeff)
   {
     float srvTime = getServiceTime();
     if ((coeff != null) && (coeff > 0.0F)) {
       srvTime += coeff * this.m_demand;
     }
 
     return srvTime;
   }
 
   public void setDistance(Double d)
   {
     this.m_distance = d;
   }
 
   public Double getDistance() {
     return this.m_distance;
   }
 
   public void setSeedId(int id)
   {
     this.m_seedId = id;
   }
 
   public int getSeedId()
   {
     return this.m_seedId;
   }
 
   public boolean isClustered()
   {
     return this.m_seedId != Constants.DEP_ID;
   }
 
   public void setRouted(boolean b)
   {
     this.m_routed = b;
   }
 
   public boolean isRouted()
   {
     return this.m_routed;
   }
 
   public void setArrivalTime(double time)
   {
     this.m_arrivalTime = time;
   }
 
   public double getArrivalTime()
   {
     return this.m_arrivalTime;
   }
 
   @Override
   public String toString()
   {
     return "Node [(" + this.m_id + ") " + this.m_label + " " + this.m_servedDemand + " / " + this.m_demand + " cluster(" + this.m_seedId + ", " + this.m_distance + ")]";
   }
 
   public String debugString(int seedId)
   {
     float demandFraction = -0.1F;
     if ((this.m_dmnPerCluster != null) && (this.m_dmnPerCluster.get(seedId) != null)) {
       demandFraction = (this.m_dmnPerCluster.get(seedId));
     }
     String str = this.m_id + "(" + Utilities.format3fract(this.m_arrivalTime) + "; " + Utilities.format3fract(demandFraction) + "/" + this.m_demand + "; served=" + this.m_servedDemand + ")";
 
     return str;
   }
 
   public String debugString()
   {
     String str = this.m_id + "(" + Utilities.format3fract(this.m_arrivalTime) + "; " + Utilities.format3fract(this.m_servedDemand) + "/" + this.m_demand + ")";
 
     return str;
   }
 
   @Override
   public int compareTo(Object obj)
   {
     int result = 1;
     if ((obj != null) && ((obj instanceof Node))) {
       Node n = (Node)obj;
       if (this.m_distance < n.m_distance)
         result = -1;
       else if (this.m_distance > n.m_distance)
         result = 1;
       else if ((this.m_distance > n.m_distance - 1.E-05D) && (this.m_distance < n.m_distance + 1.E-05D))
       {
         result = 0;
       }
     }
     return result;
   }
 
   public void release()
   {
     setSeedId(Constants.DEP_ID);
     setRouted(false);
     setDistance(0.0D);
   }
 
   public boolean setServedDemand(float demand)
   {
     boolean result = true;
     if (demand > this.m_demand) {
       result = false;
       System.out.println("WARNING: il sw ha cercato di impostare domanda servita maggiore di quella totale.");
     }
     else {
       this.m_servedDemand = demand;
     }return result;
   }
 
   public boolean addServedDemand(float increment, int currentSeedId)
   {
     if (this.m_dmnPerCluster == null) {
       this.m_dmnPerCluster = new HashMap();
     }
     boolean result = true;
     double tmpDemand = this.m_servedDemand + increment;
     if (tmpDemand > this.m_demand) {
       result = false;
       System.out.println("WARNING: il sw ha cercato di incrementare la domanda servita oltre quella totale.");
     }
     else {
       this.m_servedDemand += increment;
         Float put = this.m_dmnPerCluster.put(currentSeedId, Float.valueOf(increment));
     }
     return result;
   }
 
   private void setDemandOnCluster(float partialDemand, int seedId)
   {
     if (this.m_dmnPerCluster == null)
       this.m_dmnPerCluster = new HashMap();
     this.m_dmnPerCluster.put(seedId, partialDemand);
   }
 
   public float getServedDemand()
   {
     return this.m_servedDemand;
   }
 
   public float getDemandOnCluster(int seedId)
   {
     float dmn = 0.0F;
     if ((this.m_dmnPerCluster != null) && (this.m_dmnPerCluster.get(seedId) != null)) {
       dmn = (this.m_dmnPerCluster.get(seedId));
     }
     return dmn;
   }
 
   public float removeDemandOnCluster(int seedId)
   {
     float dmn = 0.0F;
     if ((this.m_dmnPerCluster != null) && (this.m_dmnPerCluster.get(seedId) != null)) {
       dmn = (this.m_dmnPerCluster.get(seedId));
       this.m_servedDemand -= dmn;
       this.m_dmnPerCluster.remove(seedId);
     }
     return dmn;
   }
 
   public float getRemainingDemand()
   {
     return this.m_demand - this.m_servedDemand;
   }
 
   public boolean isFullyServed()
   {
     return Utilities.doubleEqual(this.m_demand, this.m_servedDemand);
   }
 
   public boolean isFullyClustered()
   {
     return (isFullyServed()) && (isClustered());
   }
 
   public static void setDemandGranularity(Float g)
   {
     ms_granularity = g;
   }
 
   public static Float getDemandGranularity() {
     if (ms_granularity == null) {
       return 0.0F;
     }
     return ms_granularity;
   }
 
   public static void setDemandDivisible(boolean divisible)
   {
     ms_demandDivisible = divisible;
   }
 
   public static boolean isDemandDivisible() {
     return ms_demandDivisible;
   }
 }
