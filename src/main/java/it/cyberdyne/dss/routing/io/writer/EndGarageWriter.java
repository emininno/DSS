package it.cyberdyne.dss.routing.io.writer;

import it.cyberdyne.dss.routing.engine.Cluster;
import it.cyberdyne.dss.routing.model.Vehicle;
import it.cyberdyne.dss.routing.utils.Utilities;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class EndGarageWriter
{
  private final ArrayList<Cluster> m_clusters;
  private final int m_callId;
  private final ArrayList<Vehicle> m_vehs;
  
  public EndGarageWriter(ArrayList<Cluster> clusters, ArrayList<Vehicle> vehicles, int call_id)
  {
    this.m_clusters = clusters;
    this.m_callId = call_id;
    this.m_vehs = vehicles;
  }
  
  public void print() {
    HashMap<String, Integer> vehsMap = new HashMap();
    if ((this.m_vehs != null) && (!this.m_vehs.isEmpty()))
    {

      for (int i = 0; i < this.m_vehs.size(); i++) {
        Vehicle v = (Vehicle)this.m_vehs.get(i);
        if (!v.isAssigned()) {
          String key = "[free] " + v.getModel() + "@" + v.getStartHour();
          int count = 1;
          if (vehsMap.containsKey(key)) {
            count = ((Integer)vehsMap.get(key)).intValue() + 1;
            vehsMap.remove(key);
          }
          vehsMap.put(key, Integer.valueOf(count));
        }
        System.out.println(vehString((Vehicle)this.m_vehs.get(i)));
      }
      System.out.println("Mappa dei veicoli liberi");
      debugPrintMap(vehsMap);
    }
    

    if ((this.m_clusters != null) && (!this.m_clusters.isEmpty())) {
      for (int j = 0; j < this.m_clusters.size(); j++) {
        Cluster clst = (Cluster)this.m_clusters.get(j);
        Vehicle v = clst.getVehicle();
        String nextStartHour = getNewStartHour(clst);
        String key = "[used] " + v.getModel() + "@" + nextStartHour;
        int count = 1;
        if (vehsMap.containsKey(key)) {
          count = ((Integer)vehsMap.get(key)).intValue() + 1;
          vehsMap.remove(key);
        }
        vehsMap.put(key, Integer.valueOf(count));
      }
    }
    System.out.println("Mappa dei veicoli completa");
    debugPrintMap(vehsMap);
  }
  

  public void debug()
  {
    System.out.println("debug su EndGarageWriter: Stato elenco veicoli");
    if ((this.m_vehs != null) && (!this.m_vehs.isEmpty())) {
      for (int i = 0; i < this.m_vehs.size(); i++) {
        System.out.println(vehString((Vehicle)this.m_vehs.get(i)));
      }
    }
  }
  
  private void debugPrintMap(HashMap<String, Integer> map) {
    Iterator<String> keyIt = map.keySet().iterator();
    while (keyIt.hasNext()) {
      String key = (String)keyIt.next();
      System.out.println(key + " = " + map.get(key));
    }
  }
  
  private String vehString(Vehicle v)
  {
    return v.getModel() + "/start=" + v.getStartHour() + " used=" + v.isAssigned();
  }
  
  private String getNewStartHour(Cluster clst) {
    String hour = null;
    String origStartHour = clst.getVehicle().getStartHour();
    double totTime = clst.getTotTime();
    
    hour = Utilities.arrivalInstant(origStartHour, totTime);
    
    return hour;
  }
}