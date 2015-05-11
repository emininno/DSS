package it.cyberdyne.dss.routing.engine;

import it.cyberdyne.dss.routing.model.Node;
import it.cyberdyne.dss.routing.model.Vehicle;
import it.cyberdyne.dss.routing.utils.Utilities;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;


public class Cluster
{
  public static final float GRANULARITY = Node.getDemandGranularity().floatValue();
  

  public static final boolean DEMAND_DIVISIBLE = Node.isDemandDivisible();
  

  private Node m_seed;
  

  private Vehicle m_vehicle;
  

  private ArrayList<Node> m_nodes;
  

  private ArrayList<Node> m_tour;
  

  private double m_duration;
  
  private double m_length;
  
  private double m_load;
  
  private boolean m_waived;
  

  public Cluster(Node seed, Vehicle v)
  {
    this.m_seed = seed;
    this.m_nodes = new ArrayList();
    
    this.m_vehicle = v;
    
    this.m_load = 0.0D;
    
    this.m_duration = 0.0D;
    this.m_length = 0.0D;
    
    this.m_waived = false;
  }
  






  public ArrayList<Node> fill(ArrayList<Node> allNodes, Double[] dists)
  {
    if ((this.m_nodes != null) && (this.m_nodes.size() > 0)) {
      this.m_nodes = new ArrayList();
    }
    


    sortToSeed(dists, allNodes);
    




    for (int i = 0; i < allNodes.size(); i++)
    {
      Node n = (Node)allNodes.get(i);
      





      if (i == 0) {
        n = this.m_seed;
        n.setDistance(((Node)allNodes.get(i)).getDistance());
      }
      


      if ((n.getRemainingDemand() > 0.0F) || (n.getId() == this.m_seed.getId()))
      {
        boolean b = addNodeIfCan(n);
        if (!b)
        {
          System.out.println("DEBUG. Chiusura Cluster " + this.m_seed.getId() + ": impossibile aggiungere il nodo " + n.getId());
          
          break;
        }
        










        if (i == 0)
        {


          ((Node)allNodes.get(0)).alignsToClone(n);
        }
      }
    }
    
    return this.m_nodes;
  }
  








  private void sortToSeed(Double[] distsToSeed, ArrayList<Node> toBeSortedNodes)
  {
    for (int i = 0; i < toBeSortedNodes.size(); i++) {
      ((Node)toBeSortedNodes.get(i)).setDistance(distsToSeed[i]);
    }
    Collections.sort(toBeSortedNodes);
  }
  
  /**
   * @deprecated
   */
  private void addNode(Node n)
  {
    n.setSeedId(this.m_seed.getId());
    this.m_load += n.getDemand();
    this.m_nodes.add(n);
  }
  













  public boolean addNodeIfCan(Node n)
  {
    boolean result = false;
    if (DEMAND_DIVISIBLE) {
      result = addPartialNode(n, GRANULARITY);

    }
    else
    {
      float quantum = n.getRemainingDemand();
      if (GRANULARITY > 0.0F)
        quantum = (float)quantizeLoad(quantum, GRANULARITY);
      result = addPartialNode(n, quantum);
    }
    return result;
  }
  







  private boolean smallQuantumAddPartialNode(Node n, float quantum)
  {
    if (Utilities.isZero(quantum)) {
      return addPartialNode(n);
    }
    boolean result = false;
    float demandOnQuantum = n.getRemainingDemand() / quantum;
    float decimalPart = demandOnQuantum - (int)demandOnQuantum;
    int slots = (int)Math.ceil(n.getRemainingDemand() / quantum);
    double load = this.m_load;
    int i = 0;
    for (i = 0; i < slots; i++) {
      load += quantum;
      if (load > this.m_vehicle.getMaxLoad()) {
        load -= quantum;
        break;
      }
    }
    if (i > 0)
    {

      this.m_load = load;
      
      if (Utilities.doubleEqual(decimalPart, 0.0D)) {
        n.addServedDemand(i * quantum, this.m_seed.getId());
      }
      else
      {
        float increment = (i - 1 + decimalPart) * quantum;
        n.addServedDemand(increment, this.m_seed.getId());
      }
      

      n.setSeedId(this.m_seed.getId());
      this.m_nodes.add(n);
      result = true;
    }
    return result;
  }
  























  private boolean addPartialNode(Node n, float quantum)
  {
    if (Utilities.isZero(quantum)) {
      return addPartialNode(n);
    }
    if (quantum < 0.5D) {
      return smallQuantumAddPartialNode(n, quantum);
    }
    boolean result = false;
    float demandOnQuantum = n.getRemainingDemand() / quantum;
    float decimalPart = demandOnQuantum - (int)demandOnQuantum;
    int slots = (int)Math.ceil(n.getRemainingDemand() / quantum);
    
    double load = this.m_load;
    float partialDemand = 0.0F;
    

    int i = 1;
    for (i = 1; i <= slots; i++) {
      load += quantum;
      if (load > this.m_vehicle.getMaxLoad()) {
        load -= quantum;
        break;
      }
      
      if ((i == slots) && (!Utilities.doubleEqual(decimalPart, 0.0D)))
      {
        partialDemand += decimalPart * quantum;
      }
      else {
        partialDemand += quantum;
      }
    }
    
    if ((load > this.m_load) && (slots > 0)) {
      this.m_load = load;
      

      n.addServedDemand(partialDemand, this.m_seed.getId());
      n.setSeedId(this.m_seed.getId());
      this.m_nodes.add(n);
      result = true;
    }
    
    System.out.println("addPartialNode. seed=" + this.m_seed.getId() + " nodo=" + n.getId() + " load=" + this.m_load);
    
    return result;
  }
  









  private boolean addPartialNode(Node n)
  {
    boolean added = false;
    float chargebleDemand = 0.0F;
    chargebleDemand = (float)(this.m_vehicle.getMaxLoad() - this.m_load);
    if (chargebleDemand > 0.0D) {
      if (chargebleDemand > n.getRemainingDemand())
        chargebleDemand = n.getRemainingDemand();
      n.addServedDemand(chargebleDemand, this.m_seed.getId());
      

      this.m_load += chargebleDemand;
      

      n.setSeedId(this.m_seed.getId());
      this.m_nodes.add(n);
      
      added = true;
    }
    return added;
  }
  


  private double quantizeLoad(double load, float granularity)
  {
    return Utilities.quantizeLoad(load, granularity);
  }
  











  public void waivedAddNode(Node n)
  {
    if (DEMAND_DIVISIBLE) {
      addNodeIfCan(n);
    }
    else
    {
      n.setSeedId(this.m_seed.getId());
      n.addServedDemand(n.getDemand(), this.m_seed.getId());
      


      if (GRANULARITY > 0.0F) {
        this.m_load += (float)Utilities.quantizeLoad(n.getDemand(), GRANULARITY);
      } else {
        this.m_load += n.getDemand();
      }
      this.m_nodes.add(n);
    }
  }
  









  public void removeFarthestNode()
  {
    if ((this.m_nodes != null) && (this.m_nodes.size() > 0)) {
      Node n = (Node)this.m_nodes.get(this.m_nodes.size() - 1);
      n.release();
      
      this.m_nodes.remove(n);
      
      double tmp = this.m_load;
      if (GRANULARITY > 0.0F) {
        this.m_load -= Utilities.quantizeLoad(n.removeDemandOnCluster(this.m_seed.getId()), GRANULARITY);
      } else
        this.m_load -= n.removeDemandOnCluster(this.m_seed.getId());
      System.out.println("removeFarthestNode. m_load prima e dopo = " + tmp + "; " + this.m_load);
      clearTour();
    }
    else {
      System.err.println(" WARNING. Nessun nodo da rimuovere: cluster vuoto!");
    }
  }
  
  public void setWaived(boolean waiv)
  {
    this.m_waived = waiv;
  }
  
  public boolean isWaived() {
    return this.m_waived;
  }
  

  public ArrayList<Node> getNodes()
  {
    return this.m_nodes;
  }
  
  public ArrayList<Node> getTour()
  {
    return this.m_tour;
  }
  
  public Node getSeed()
  {
    return this.m_seed;
  }
  


  public int getSeedId()
  {
    return this.m_seed.getId();
  }
  
  public boolean isEmpty()
  {
    if ((this.m_nodes != null) && (this.m_nodes.size() > 0)) {
      return false;
    }
    return true;
  }
  




  public void setTour(ArrayList<Node> tour)
  {
    setTour(tour, 0.0D, 0.0D);
  }
  







  public void setTour(ArrayList<Node> tour, double length, double duration)
  {
    this.m_tour = null;
    this.m_tour = new ArrayList(tour);
    this.m_length = length;
    this.m_duration = duration;
  }
  



  public void clearTour()
  {
    this.m_tour = null;
    if ((this.m_nodes != null) && (this.m_nodes.size() > 0)) {
      for (int i = 0; i < this.m_nodes.size(); i++) {
        ((Node)this.m_nodes.get(i)).setRouted(false);
      }
    }
  }
  








  public Vehicle getVehicle()
  {
    return this.m_vehicle;
  }
  


  public void setVehicle(Vehicle v)
  {
    this.m_vehicle = v;
  }
  

  public double getTotLoad()
  {
    return this.m_load;
  }
  


  public double getTotTime()
  {
    return this.m_duration;
  }
  


  public double getTotDistance()
  {
    return this.m_length;
  }
  


  public float getTimeToLast()
  {
    float last = (float)this.m_duration;
    if ((this.m_tour != null) && (this.m_tour.size() > 2))
    {


      last = (float)((Node)this.m_tour.get(this.m_tour.size() - 2)).getArrivalTime();
    }
    return last;
  }
  







  public void print(PrintStream out)
  {
    out.print("Cluster (seed=" + this.m_seed.getId() + "): {");
    if ((this.m_nodes != null) && (!this.m_nodes.isEmpty())) {
      for (int i = 0; i < this.m_nodes.size(); i++)
      {
        Node n = (Node)this.m_nodes.get(i);
        out.print(n.getId() + "(" + n.getDemandOnCluster(this.m_seed.getId()) + "/" + n.getDemand() + ")");
        if (i < this.m_nodes.size() - 1)
          out.print(", ");
      }
      out.print(" [LOAD=" + Utilities.format3fract(this.m_load) + " notQtzLoad=" + Utilities.format3fract(notQuantizedLoad()) + "]");
      
      if (this.m_waived) {
        out.print(" deroga");
      }
    } else {
      out.print("empty or null");
    }
    out.print("}");
  }
  

  private double notQuantizedLoad()
  {
    double nqload = 0.0D;
    if ((this.m_nodes != null) && (!this.m_nodes.isEmpty())) {
      for (int i = 0; i < this.m_nodes.size(); i++) {
        nqload += ((Node)this.m_nodes.get(i)).getDemandOnCluster(this.m_seed.getId());
      }
    }
    return nqload;
  }
  


  public void printTour(PrintStream ps)
  {
    ps.print("Route per cluster (" + this.m_seed.getId() + "): {");
    if ((this.m_tour != null) && (!this.m_tour.isEmpty())) {
      for (int i = 0; i < this.m_tour.size(); i++)
      {


        ps.print(((Node)this.m_tour.get(i)).debugString(this.m_seed.getId()));
        
        if (i < this.m_tour.size() - 1) {
          ps.print(", ");
        }
      }
    } else {
      ps.print("empty or null");
    }
    ps.print("}");
  }
  
  public String toString()
  {
    String str = "Cluster (" + this.m_seed.getId() + "): {";
    if ((this.m_nodes != null) && (!this.m_nodes.isEmpty())) {
      for (int i = 0; i < this.m_nodes.size(); i++) {
        str = str + "" + ((Node)this.m_nodes.get(i)).getId();
        if (i < this.m_nodes.size() - 1) {
          str = str + ", ";
        }
      }
    } else {
      str = str + "empty or null";
    }
    str = str + "}";
    
    return str;
  }
  
  public String tourToString()
  {
    String str = "Route in cluster (" + this.m_seed.getId() + "): {";
    if ((this.m_tour != null) && (!this.m_tour.isEmpty())) {
      for (int i = 0; i < this.m_tour.size(); i++) {
        str = str + "" + ((Node)this.m_tour.get(i)).getId();
        if (i < this.m_tour.size() - 1) {
          str = str + ", ";
        }
      }
    } else {
      str = str + "empty or null";
    }
    str = str + "}";
    
    return str;
  }
}
