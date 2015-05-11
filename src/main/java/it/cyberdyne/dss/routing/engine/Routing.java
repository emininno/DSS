package it.cyberdyne.dss.routing.engine;

import it.cyberdyne.dss.routing.io.InputManager;
import it.cyberdyne.dss.routing.model.Node;
import it.cyberdyne.dss.routing.model.Vehicle;
import it.cyberdyne.dss.routing.utils.Constants;
import java.io.PrintStream;
import java.util.ArrayList;

public class Routing
{
  private Cluster m_cluster;
  private InputManager m_iMan;
  private Node m_dep;
  
  public Routing(Cluster clst, Node dep, InputManager iMan)
  {
    this.m_cluster = clst;
    this.m_iMan = iMan;
    
    this.m_dep = ((Node)dep.clone());
  }
  
  public Routing(Cluster clst, InputManager iMan) {
    this(clst, iMan.getNodeByID(Constants.DEP_ID), iMan);
  }
  
  public void process()
    throws Exception
  {
    if ((this.m_cluster != null) && (this.m_dep != null))
    {
      Node seed = this.m_cluster.getSeed();
      ArrayList<Node> route = new ArrayList();
      seed.setRouted(true);
      this.m_dep.setRouted(true);
      route.add(this.m_dep);
      route.add(seed);
      route.add((Node)this.m_dep.clone());
      for (int i = 1; i < this.m_cluster.getNodes().size(); i++) {
        ((Node)this.m_cluster.getNodes().get(i)).setRouted(false);
      }
     int toInsNodeId = 0;
      int k = 0;
      while ((toInsNodeId != -1) && (k++ < this.m_cluster.getNodes().size())) {
        toInsNodeId = whichNodeInsert();
        if (toInsNodeId != -1) {
          whereInsert(toInsNodeId, route);
          markNode(toInsNodeId);
        }
      }
      Float srvTimeCoeff = this.m_cluster.getVehicle().getSrvTimeCoeff();
      this.m_cluster.setTour(route, pathLength(route), pathDuration(route, true, srvTimeCoeff));
      setArrivalInstants(route, true, srvTimeCoeff);
    }
  }

  private int whichNodeInsert()
    throws Exception
  {
    int id = -1;
    ArrayList<Node> nodes = this.m_cluster.getNodes();
    double maxDistance = 0.0D;
    

    for (int i = 0; i < nodes.size(); i++) {
      Node nSrc = (Node)nodes.get(i);
      if (nSrc.isRouted())
      {

        for (int j = 0; j < nodes.size(); j++) {
          Node nDst = (Node)nodes.get(j);
          if (!nDst.isRouted())
          {

            double dst = this.m_iMan.getDistance(nSrc.getId(), nDst.getId());
            

            if (dst > maxDistance) {
              id = nDst.getId();
              maxDistance = dst;
            }
          }
        }
      }
    }
    
    return id;
  }
  
  private void whereInsert(int nodeId, ArrayList<Node> route)
  {
    int k = getNodePosition(this.m_cluster.getNodes(), nodeId);
    Node n = (Node)this.m_cluster.getNodes().get(k);
    double origLength = pathLength(route);
    int origSize = route.size();
    
    int pos = 1;
    if (origSize > 1) {
      double minLength = 9.999999999E9D;
      for (int i = 1; i < origSize; i++)
      {
        ArrayList<Node> attempt = new ArrayList(route);
        attempt.add(i, n);
        double length = pathLength(attempt);
        if (length < minLength) {
          minLength = length;
          pos = i;
        }
      }
      route.add(pos, n);
      n.setRouted(true);

    }
    else
    {
      System.out.println("DEBUG: path non inizializzato.");
    }
  }

  private void markNode(int nodeId)
  {
    this.m_iMan.markNodeAsRouted(nodeId);
  }

  private int getNodePosition(ArrayList<Node> nodes, int nodeId)
  {
    int pos = -1;
    if (nodeId > 0) {
      for (int i = 0; i < nodes.size(); i++) {
        if (((Node)nodes.get(i)).getId() == nodeId) {
          pos = i;
          break;
        }
      }
    }
    return pos;
  }

  
  private double pathLength(ArrayList<Node> route)
  {
    double len = 0.0D;
    for (int i = 0; i < route.size() - 1; i++) {
      try
      {
        Node src = (Node)route.get(i);
        Node dst = (Node)route.get(i + 1);
        len += this.m_iMan.getDistance(src.getId(), dst.getId());
      }
      catch (Exception e) {}
    }

    return len;
  }
  
  private double pathDuration(ArrayList<Node> route, boolean withService, Float srvTimeCoeff)
  {
    double d = 0.0D;
    if ((withService) && (route.size() > 0)) {
      d += ((Node)route.get(0)).getServiceTime(srvTimeCoeff);
    }
    for (int i = 0; i < route.size() - 1; i++) {
      try
      {
        Node src = (Node)route.get(i);
        Node dst = (Node)route.get(i + 1);
        d += this.m_iMan.getTime(src.getId(), dst.getId());
        
        if (withService) {
          d += dst.getServiceTime(srvTimeCoeff);
        }
      } catch (Exception e) {}
    }
    return d;
  }

  private void setArrivalInstants(ArrayList<Node> route, boolean withService, Float srvTimeCoeff)
  {
    double t = 0.0D;
    if (route.size() > 0)
      ((Node)route.get(0)).setArrivalTime(t);
    for (int i = 0; i < route.size() - 1; i++) {
      try {
        Node src = (Node)route.get(i);
        Node dst = (Node)route.get(i + 1);
        
        if (withService) {
          double srcSrvTime = src.getServiceTime(srvTimeCoeff);
          t += srcSrvTime;
        }
        
        double srcToDstTime = this.m_iMan.getTime(src.getId(), dst.getId());
        t += srcToDstTime;
        dst.setArrivalTime(t);
      }
      catch (Exception e) {
        System.err.println("Warning. Out of bound in setArrivalInstants: " + e);
      }
    }
  }
  

  public void print()
  {
    print(System.out);
  }
  

  public void print(PrintStream ps)
  {
    this.m_cluster.printTour(ps);
  }
}