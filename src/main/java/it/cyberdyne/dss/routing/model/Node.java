package it.cyberdyne.dss.routing.model;

import it.cyberdyne.dss.routing.utils.Constants;
import it.cyberdyne.dss.routing.utils.Utilities;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Node
  implements Comparable, Cloneable
{
  private int m_id;
  private String m_label;
  private float m_demand;
  private float m_srvTime;
  private float m_servedDemand;
  private HashMap<Integer, Float> m_dmnPerCluster;
  private double m_arrivalTime;
  private int m_seedId;
  private Double m_distance;
  private boolean m_routed;
  private static Float ms_granularity;
  private static boolean ms_demandDivisible = false;
  private double startTime;
  
  public Node(int id, String label, float demand, float serviceTime)
  {
    this.m_id = id;
    this.m_label = label;
    this.m_demand = demand;
    this.m_srvTime = serviceTime;
    
    this.m_seedId = Constants.DEP_ID;
  }
  

  public Object clone()
  {
    try
    {
      super.clone();
    }
    catch (CloneNotSupportedException e) {}
    

    Node n = new Node(this.m_id, this.m_label, this.m_demand, this.m_srvTime);
    
    n.setServedDemand(this.m_servedDemand);
    
    n.setRouted(this.m_routed);
    n.m_seedId = this.m_seedId;
    
    if ((this.m_dmnPerCluster != null) && (!this.m_dmnPerCluster.isEmpty()))
    {
      Set list = this.m_dmnPerCluster.keySet();
      Iterator<Integer> iter = list.iterator();
      while (iter.hasNext()) {
        Integer seedId = (Integer)iter.next();
        Float partialDemand = (Float)this.m_dmnPerCluster.get(seedId);
        
        n.setDemandOnCluster(partialDemand.floatValue(), seedId.intValue());
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
    if ((coeff != null) && (coeff.floatValue() > 0.0F)) {
      srvTime += coeff.floatValue() * this.m_demand;
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
    if (this.m_seedId != Constants.DEP_ID) {
      return true;
    }
    return false;
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
  
  public String toString()
  {
    return "Node [(" + this.m_id + ") " + this.m_label + " " + this.m_servedDemand + " / " + this.m_demand + " cluster(" + this.m_seedId + ", " + this.m_distance + ")]";
  }
  
  public String debugString(int seedId)
  {
    float demandFraction = -0.1F;
    if ((this.m_dmnPerCluster != null) && (this.m_dmnPerCluster.get(Integer.valueOf(seedId)) != null)) {
      demandFraction = ((Float)this.m_dmnPerCluster.get(Integer.valueOf(seedId))).floatValue();
    }
    String str = this.m_id + "(" + Utilities.format3fract(this.m_arrivalTime) + "; " + Utilities.format3fract(demandFraction) + "/" + this.m_demand + "; served=" + this.m_servedDemand + ")";
    
    return str;
  }
  
  public String debugString()
  {
    String str = this.m_id + "(" + Utilities.format3fract(this.m_arrivalTime) + "; " + Utilities.format3fract(this.m_servedDemand) + "/" + this.m_demand + ")";
    

    return str;
  }
  
  public int compareTo(Object obj)
  {
    int result = 1;
    if ((obj != null) && ((obj instanceof Node))) {
      Node n = (Node)obj;
      if (this.m_distance.doubleValue() < n.m_distance.doubleValue()) {
        result = -1;
      } else if (this.m_distance.doubleValue() > n.m_distance.doubleValue()) {
        result = 1;
      } else if ((this.m_distance.doubleValue() > n.m_distance.doubleValue() - 1.0E-5D) && (this.m_distance.doubleValue() < n.m_distance.doubleValue() + 1.0E-5D))
      {
        result = 0; }
    }
    return result;
  }
  
  public void release()
  {
    setSeedId(Constants.DEP_ID);
    setRouted(false);
    setDistance(Double.valueOf(0.0D));
  }
  
  public boolean setServedDemand(float demand)
  {
    boolean result = true;
    if (demand > this.m_demand) {
      result = false;
      System.out.println("WARNING: il sw ha cercato di impostare domanda servita maggiore di quella totale.");
    }
    else {
      this.m_servedDemand = demand; }
    return result;
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
      this.m_dmnPerCluster.put(Integer.valueOf(currentSeedId), Float.valueOf(increment));
    }
    return result;
  }
  
  private void setDemandOnCluster(float partialDemand, int seedId)
  {
    if (this.m_dmnPerCluster == null)
      this.m_dmnPerCluster = new HashMap();
    this.m_dmnPerCluster.put(Integer.valueOf(seedId), Float.valueOf(partialDemand));
  }
  
  public float getServedDemand()
  {
    return this.m_servedDemand;
  }
  
  public float getDemandOnCluster(int seedId)
  {
    float dmn = 0.0F;
    if ((this.m_dmnPerCluster != null) && (this.m_dmnPerCluster.get(Integer.valueOf(seedId)) != null)) {
      dmn = ((Float)this.m_dmnPerCluster.get(Integer.valueOf(seedId))).floatValue();
    }
    return dmn;
  }
  
  public float removeDemandOnCluster(int seedId)
  {
    float dmn = 0.0F;
    if ((this.m_dmnPerCluster != null) && (this.m_dmnPerCluster.get(Integer.valueOf(seedId)) != null)) {
      dmn = ((Float)this.m_dmnPerCluster.get(Integer.valueOf(seedId))).floatValue();
      this.m_servedDemand -= dmn;
      this.m_dmnPerCluster.remove(Integer.valueOf(seedId));
    }
    return dmn;
  }
  
  public float getRemainingDemand()
  {
    return this.m_demand - this.m_servedDemand;
  }
  
  public boolean isFullyServed()
  {
    if (Utilities.doubleEqual(this.m_demand, this.m_servedDemand)) {
      return true;
    }
    return false;
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
      return Float.valueOf(0.0F);
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