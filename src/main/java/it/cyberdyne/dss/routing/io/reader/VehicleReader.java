package it.cyberdyne.dss.routing.io.reader;

import it.cyberdyne.dss.routing.model.Vehicle;
import it.cyberdyne.dss.routing.model.VehicleType;
import it.cyberdyne.dss.routing.utils.Constants;
import java.util.ArrayList;
import java.util.Collections;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class VehicleReader
  
{
  public  final String FILE_BASENAME = "vehicles_";
  public  final String SCHEMA_FILENAME = null;

  private ArrayList<Vehicle> m_vehicles;
  private final ArrayList<VehicleType> m_list;


  public VehicleReader(ArrayList<VehicleType> types)
  {
      
      this.m_list = types;
     
  }


  private void addToTypeArray(Node vehicleNode)
    throws Exception
  {
    String code = vehicleNode.getAttributes().getNamedItem("code").getNodeValue();
    

    NodeList children = vehicleNode.getChildNodes();
    
    Integer qty = Integer.valueOf(0);
    String model = null;
    
    Double maxLoad = new Double(9.999999999E9D);
    Double maxDistance = Double.valueOf(9.999999999E9D);
    Double maxTime = new Double(9.999999999E9D);
    
    String startHour = "0:00";
    
    Double srvTimeCoeff = new Double(Constants.SERVICE_TIME_COEFF);
    

    for (int i = 0; i < children.getLength(); i++)
    {
      Node child = children.item(i);
      if (child.getNodeType() == 1) {
        String name = child.getNodeName();
        String value = child.getTextContent();
        
        if (value != null) {
          if (name.equals("qty")) {
            qty = Integer.valueOf(Integer.parseInt(value));
          }
          else if (name.equals("model")) {
            model = value.trim();
          }
          else if ((name.equals("max_load")) && (!value.trim().equals("INF")))
          {
            maxLoad = Double.valueOf(Double.parseDouble(value));
          }
          else if ((name.equals("max_distance")) && (!value.trim().equals("INF")))
          {

            maxDistance = Double.valueOf(Double.parseDouble(value));
          }
          else if ((name.equals("max_time")) && (!value.trim().equals("INF")))
          {

            maxTime = Double.valueOf(Double.parseDouble(value));
          }
          else if (name.equals("start_hour")) {
            startHour = value.trim();
          }
          else if ((name.equals("service_time_coeff")) && (!value.trim().equals("NaN")))
          {
            srvTimeCoeff = Double.valueOf(Float.parseFloat(value.trim()));
            System.out.println("DEBUG: service_time_coeff = " + srvTimeCoeff);
          }
        }
      }
    }
    





    System.out.println("DEBUG. VehicleReader. code = " + code + ". service_time_coeff = " + srvTimeCoeff);
    

    VehicleType vehType = new VehicleType(Constants.DEP_ID, code, model, qty.intValue(), maxLoad.doubleValue(), maxDistance.doubleValue(), maxTime.doubleValue(), startHour, new Float(srvTimeCoeff));
    

    this.m_list.add(vehType);
  }
  



  public ArrayList<VehicleType> getTypeArray()
  {
    ArrayList<VehicleType> vt = new ArrayList();
    
    for (int i = 0; i < this.m_list.size(); i++) {
      vt.add((VehicleType)this.m_list.get(i));
    }
    
    return vt;
  }
  
  public ArrayList<Vehicle> getVehicles()
  {
    if ((this.m_vehicles == null) && 
      (this.m_list != null) && (this.m_list.size() > 0))
    {
      Collections.sort(this.m_list);
      this.m_vehicles = new ArrayList();
      
      for (int i = 0; i < this.m_list.size(); i++)
      {
        VehicleType vt = (VehicleType)this.m_list.get(i);
        for (int j = 0; j < vt.getTotQty(); j++) {
          Vehicle v = new Vehicle(vt);
          this.m_vehicles.add(v);
        }
      }
    }
   
    return this.m_vehicles;
  }
  

  
  public ArrayList<VehicleType> getArray() {
    ArrayList<VehicleType> vt = new ArrayList();
    
    for (int i = 0; i < this.m_list.size(); i++) {
      vt.add((VehicleType)this.m_list.get(i));
    }
    
    return vt;
  }
}