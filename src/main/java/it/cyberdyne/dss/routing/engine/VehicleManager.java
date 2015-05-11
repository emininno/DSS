package it.cyberdyne.dss.routing.engine;

import it.cyberdyne.dss.routing.model.Vehicle;
import it.cyberdyne.dss.routing.utils.Utilities;
import java.util.ArrayList;

public class VehicleManager
{
  public static void postFit(Cluster clst, ArrayList<Vehicle> vehs)
  {
    Vehicle fitV = null;
    Vehicle origV = clst.getVehicle();
    String origType = origV.getCode();
    double clstLoad = clst.getTotLoad();
    double origCapacityDiff = Utilities.squareDiff(origV.getMaxLoad(), clstLoad);
    

    Vehicle targetV = origV;
    double targetDiff = origCapacityDiff;
    for (int i = 0; i < vehs.size(); i++) {
      Vehicle v = (Vehicle)vehs.get(i);
      
      if (isElegibleForPostFit(v, clst))
      {
        double capDiff = Utilities.squareDiff(v.getMaxLoad(), clstLoad);
        if (capDiff < targetDiff) {
          targetV = v;
          targetDiff = capDiff;
        }
      }
    }
    
    if (targetV.getMaxLoad() < origV.getMaxLoad())
    {
      origV.setAssigned(false);
      clst.setVehicle(targetV);
      targetV.setAssigned(true);
    }
  }
  private static boolean isElegibleForPostFit(Vehicle v, Cluster clst)
  {
    boolean b = false;
    if (!v.isAssigned())
    {
      b = (clst.getTotLoad() <= v.getMaxLoad()) && (clst.getTotDistance() <= v.getMaxDistance()) && (clst.getTotTime() <= v.getMaxTime()) && (clst.getTimeToLast() <= v.getTimeLimitToLast());
    }
    


    return b;
  }
 
  public static void postFitWaived(Cluster clst, ArrayList<Vehicle> vehs)
  {
    System.out.println("Attenzione: editare in postFitWaived per implememtare il post fit in caso di deroga");
  }
}