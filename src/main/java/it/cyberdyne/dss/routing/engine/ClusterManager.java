package it.cyberdyne.dss.routing.engine;

import it.cyberdyne.dss.routing.io.InputManager;
import it.cyberdyne.dss.routing.model.Node;
import it.cyberdyne.dss.routing.model.Vehicle;
import it.cyberdyne.dss.routing.utils.Constants;
import it.cyberdyne.dss.routing.utils.Utilities;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

public class ClusterManager
{
  private static final String WORK_DIR = Constants.WORK_DIR;
  private static final int DEP_ID = Constants.DEP_ID;
  

  private InputManager m_iMan;
  
  private ArrayList<Cluster> m_clusters;
  

  public ClusterManager(InputManager iman)
  {
    System.out.println("Initializing Cluster Manager...");
    this.m_iMan = iman;
    this.m_clusters = new ArrayList();
  }
  
  public int process() throws CloneNotSupportedException
  {
    System.out.println("DEBUG. Inizio ClusterManager.process()");
    boolean fractionability = Node.isDemandDivisible();
    float granularity = Node.getDemandGranularity().floatValue();
    

    if ((this.m_clusters != null) && (this.m_clusters.size() > 0)) {
      this.m_clusters = new ArrayList();
    }
    

    ArrayList<Node> candidate_seeds = getSortedNodes(Constants.DEP_ID);
    
    PrintStream psUnservible = getTempLogStream("unseribles");
    PrintStream psWaived = getTempLogStream("waiveds");
    
    ArrayList<Vehicle> vehics = this.m_iMan.getVehicles();
    
    for (int i = candidate_seeds.size() - 1; i > 0; i--) {
      Node seed = (Node)candidate_seeds.get(i);
      

      System.out.println("DEBUG: candidato seed " + seed.debugString(DEP_ID) + " demand = " + seed.getDemand());
      
      if (seed.getDemand() <= 0.0F) {
        System.out.println("Candidato saltato per domanda nulla.");
      }
      else
      {
        int k = 0;
        while (!seed.isFullyClustered())
        {

          seed = (Node)seed.clone();
          

          Vehicle v = nextAvailableVehicle(vehics, seed, fractionability, granularity);
          if (v != null)
          {
            System.out.println("DEBUG:  veicolo iniziale " + v.getModel() + " start=" + v.getStartHour() + " vincolo_fascia=" + v.getTimeLimitToLast());
            
            v.setAssigned(true);
            seed.setSeedId(seed.getId());
            


            Cluster clst = new Cluster(seed, v);
            clst.fill(this.m_iMan.getNodes(), this.m_iMan.getDistRow(seed.getId()));
            System.out.println("process(). k=" + k + " clone seedid" + seed.getId() + " " + seed.getServedDemand() + "/" + seed.getDemand());
            
            System.out.println("Cluster con solo vincolo portata (" + v.getMaxLoad() + "): ");
            clst.print(System.out);System.out.println();
            


            Routing router = new Routing(clst, this.m_iMan.getNodeByID(DEP_ID), this.m_iMan);
            try
            {
              router.process();
              

              System.out.println("distanza corrente: " + clst.getTotDistance());
              System.out.println("tempo corrente: " + clst.getTotTime() + " / " + v.getMaxTime());
              System.out.println("tempo per last CR (v.1.2.1): " + clst.getTimeToLast() + " / " + v.getTimeLimitToLast());
              



              while (((clst.getTotDistance() > v.getMaxDistance()) || (clst.getTotTime() > v.getMaxTime()) || (clst.getTimeToLast() > v.getTimeLimitToLast())) && (clst.getNodes().size() > 1))
              {
                System.out.println("Riduzione cluster " + seed.getId());
                



                clst.removeFarthestNode();
                router.process();
              }
            }
            catch (Exception e) {
              System.err.println("ERROR: errore in routing");
            }
            

            markNodes(clst.getNodes(), seed.getId());
            System.out.println("DEBUG: seed=" + seed.getId() + ":");
            Utilities.printArrayList(clst.getNodes());
            this.m_clusters.add(clst);
            

            VehicleManager.postFit(clst, this.m_iMan.getVehicles());
          }
          else {
            System.out.println("Prima createWaivedCluster. candidato seed " + seed.debugString(seed.getId()));
            Vehicle locV = createWaivedCluster(vehics, seed);
            System.out.println("Dopo createWaivedCluster. candidato seed " + seed.debugString(seed.getId()));
            if (locV != null)
            {


              printWaived(seed, locV, fractionability, psWaived);
              printWaived(seed, locV, fractionability, System.out);
              ((Node)candidate_seeds.get(i)).alignsToClone(seed);

            }
            else
            {

              printUnservible(seed.getId(), psUnservible);
              printUnservible(seed.getId(), System.out);
              break;
            }
          }
          

          k++;
          boolean bloccoSicurezza = true;
          if ((k > 5) && (bloccoSicurezza)) {
            System.out.println("Blocco di sicurezza a k=" + k + " cicli");
            break;
          }
        }
      } }
    closeTmpLog(psUnservible);
    closeTmpLog(psWaived);
    System.out.println("Stampa dei cluster/route a fine process():");
    printClusters();
    
    return this.m_clusters.size();
  }
  



  private void markNodes(ArrayList<Node> nodes, int seedId)
  {
    for (int i = 0; i < nodes.size(); i++) {
      this.m_iMan.markNodeAsClustered(((Node)nodes.get(i)).getId(), seedId);
    }
  }

  private ArrayList<Node> getSortedNodes(int nodeId)
  {
    return Utilities.getSortedNodes(nodeId, this.m_iMan);
  }
  
  private Vehicle nextAvailableVehicle(ArrayList<Vehicle> vs, Node seed, boolean fractionability, float granularity)
  {
    Vehicle v = null;
    try
    {
      double dist = this.m_iMan.getDistanceRT(DEP_ID, seed.getId());
      double time = this.m_iMan.getTimeRT(DEP_ID, seed.getId()) + seed.getServiceTime();
      
      float timeToSeed = (float)this.m_iMan.getTime(DEP_ID, seed.getId());
      float demand = seed.getDemand();
      if (granularity > 0.0F) {
        demand = (float)Utilities.quantizeLoad(demand, granularity);
      }
      
      for (int i = 0; i < vs.size(); i++) {
        Vehicle tmpV = (Vehicle)vs.get(i);
        if ((!tmpV.isAssigned()) && ((tmpV.getMaxLoad() >= demand) || (fractionability)) && (tmpV.getMaxDistance() >= dist) && (tmpV.getMaxTime() >= time) && (tmpV.getTimeLimitToLast() >= timeToSeed))
        {
          v = (Vehicle)vs.get(i);
        }
      }
    }
    catch (Exception e)
    {
      System.err.println("ERRORE in assegnazione veicolo " + e);
    }
    
    return v;
  }
  
  private Vehicle createWaivedCluster(ArrayList<Vehicle> vs, Node seed) throws CloneNotSupportedException
  {
    Vehicle v = null;
    
    for (int i = 0; i < vs.size(); i++) {
      if (!((Vehicle)vs.get(i)).isAssigned()) {
        v = (Vehicle)vs.get(i);
      }
    }
    
    if (v != null) {
      Cluster clst = new Cluster(seed, v);
      clst.setWaived(true);
      v.setAssigned(true);
      clst.waivedAddNode(seed);
      Routing router = new Routing(clst, this.m_iMan);
      try {
        router.process();
      }
      catch (Exception e) {
        System.err.println("ERROR: errore in routing per il cluster in deroga con seed = " + seed.getId());
      }
      this.m_clusters.add(clst);
      

      VehicleManager.postFitWaived(clst, vs);
    }
    return v;
  }
  
  public ArrayList<Cluster> getClusters()
  {
    return this.m_clusters;
  }
  
  public void printClusters()
  {
    printClusters(System.out);
  }
  
  public void printClusters(String outPath)
  {
    try
    {
      FileOutputStream ostream = new FileOutputStream(outPath);
      printClusters(new PrintStream(ostream));
    }
    catch (FileNotFoundException fnfe) {
      System.err.println("Errore in printClusters(): " + fnfe);
    }
  }
  
  public void printClusters(PrintStream out)
  {
    if ((this.m_clusters != null) && (!this.m_clusters.isEmpty())) {
      for (int i = 0; i < this.m_clusters.size(); i++) {
        ((Cluster)this.m_clusters.get(i)).print(out);
        out.println();
      }
    }
  }
  

  public void printTours()
  {
    printTours(System.out);
  }
  
  public void printTours(PrintStream out)
  {
    if ((this.m_clusters != null) && (!this.m_clusters.isEmpty())) {
      for (int i = 0; i < this.m_clusters.size(); i++) {
        ((Cluster)this.m_clusters.get(i)).printTour(out);
        out.println();
      }
    }
  }
  
  private PrintStream getTempLogStream(String fileBaseName)
  {
    PrintStream ps = null;
    try {
      String filePath = WORK_DIR + File.separator + fileBaseName + "_" + this.m_iMan.getCallId() + ".txt";
      
      ps = new PrintStream(new FileOutputStream(filePath));
    }
    catch (IOException ioe)
    {
      ioe.printStackTrace();
    }
    return ps;
  }
  
  private void printUnservible(int seedId, PrintStream ps)
  {
    Node seed = this.m_iMan.getNodeByID(seedId);
    String warningStr = "ATTENZIONE. Insufficienza di veicoli. Nodo " + seed.getLabel() + " (" + seedId + ") ";
    

    if (seed.getServedDemand() > 0.0F) {
      warningStr = warningStr + "servito solo PARZIALMENTE (" + seed.getServedDemand() + "/" + seed.getDemand() + ")";
    }
    else
    {
      warningStr = warningStr + "NON servito";
    }
    
    ps.println(warningStr);
  }
  
  private void printWaived(int seedId, String vehCode, boolean fractionability, PrintStream ps)
  {
    Node seed = this.m_iMan.getNodeByID(seedId);
    String warningStr = "ATTENZIONE: il candidato seed " + seed.getLabel() + " (" + seedId + ") viola i vincoli di ";
    
    if (!fractionability)
      warningStr = warningStr + "capacita' o ";
    warningStr = warningStr + "di distanza o di tempo di percorrenza. Si crea un cluster in deroga";
    try {
      warningStr = warningStr + " [";
      if (!fractionability)
        warningStr = warningStr + "demand=" + seed.getRemainingDemand() + "/" + seed.getDemand() + ";";
      warningStr = warningStr + " distanza=" + this.m_iMan.getDistanceRT(DEP_ID, seedId) + "; tempo=" + Math.round(this.m_iMan.getTimeRT(DEP_ID, seedId) + seed.getServiceTime()) + " codice veicolo=" + vehCode + "]";
    }
    catch (Exception e) {}
    ps.println(warningStr);
  }
  
  private void printWaived(Node seed, Vehicle v, boolean fractionability, PrintStream ps)
  {
    String warningStr = "ATTENZIONE: violazione vincoli per seed/veicolo " + seed.getLabel() + "/" + v.getCode() + ": ";
    
    if ((!fractionability) && (seed.getDemand() > v.getMaxLoad())) {
      warningStr = warningStr + " Capacita' (" + seed.getDemand() + "/" + v.getMaxLoad() + ").";
    }
    
    try
    {
      double totDistance = this.m_iMan.getDistanceRT(DEP_ID, seed.getId());
      double maxDistance = v.getMaxDistance();
      if (totDistance > maxDistance) {
        warningStr = warningStr + " Distanza (" + totDistance + "/" + maxDistance + ").";
      }
      
      double totTime = this.m_iMan.getTimeRT(DEP_ID, seed.getId()) + seed.getServiceTime();
      
      double maxTime = v.getMaxTime();
      if (totTime > maxTime) {
        warningStr = warningStr + " Durata (" + totTime + "/" + maxTime + ").";
      }
      
      float timeToLast = (float)this.m_iMan.getTime(DEP_ID, seed.getId());
      float timeLimitToLast = v.getTimeLimitToLast();
      if (timeToLast > timeLimitToLast) {
        warningStr = warningStr + " Fascia (" + timeToLast + "/" + timeLimitToLast + ").";
      }
    } catch (Exception e) {}
    ps.println(warningStr);
  }
  


  private void closeTmpLog(PrintStream ps)
  {
    if (ps != null) {
      ps.close();
    }
  }
}