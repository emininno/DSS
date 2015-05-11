package it.cyberdyne.dss.routing.io.reader;

import it.cyberdyne.dss.routing.model.Vehicle;
import it.cyberdyne.dss.routing.model.VehicleType;
import it.cyberdyne.dss.routing.utils.Constants;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class VehicleReader
  extends XMLReader
{
  public  final String FILE_BASENAME = "vehicles_";
  public  final String SCHEMA_FILENAME = null;

  private ArrayList<Vehicle> m_vehicles;

  public VehicleReader(String directory, int call_id)
  {
    super(call_id);
    this.m_filepath = (directory + File.separator + "vehicles_" + call_id + ".xml");
  }

  public VehicleReader(ArrayList<VehicleType> types)
  {
    super(types);
  }

  public void read()
    throws Exception
  {
    try
    {
      File file = new File(this.m_filepath);
      
      URL confUrl = getClass().getResource(Constants.XML_SCHEMA_VEHICLES);
      
      if (confUrl != null) {
        parse(file, confUrl.openStream());
      } else {
        parse(file, null);
      }
      if (this.m_document != null)
      {
        createVehicleTypeArray();
      }
    }
    catch (Exception ioe) {
      throw ioe;
    }
  }

  private void createVehicleTypeArray()
    throws Exception
  {
    this.m_list = new ArrayList();
    
    Node root = this.m_document.getFirstChild();
    
    Integer readCallId = Integer.valueOf(Integer.parseInt(root.getAttributes().getNamedItem("call_id").getNodeValue()));

    if (readCallId.intValue() != this.m_callId) {
      this.m_document = null;
      throw new Exception("Errore: Incongruenza tra nome del file e attributo call_id");
    }
  
    Float feedSessionDuration = Float.valueOf(1.0E10F);
    String feedSessionStart = null;
    boolean uniformedStart = false;
    
    NodeList elems = root.getChildNodes();
    for (int i = 0; i < elems.getLength(); i++)
    {
      String tmpText = null;
      Node directChild = elems.item(i);
      if (directChild.getNodeType() == 1) {
        Float wkTime;
        if (directChild.getNodeName().equals("working_time"))
        {
          wkTime = Float.valueOf(Float.parseFloat(directChild.getTextContent()));

        }
        else if (directChild.getNodeName().equals("feed_session_duration")) {
          tmpText = directChild.getTextContent();
          if ((tmpText != null) && (!tmpText.trim().equals("INF"))) {
            feedSessionDuration = Float.valueOf(Float.parseFloat(directChild.getTextContent()));
          }
        } else if (directChild.getNodeName().equals("feed_session_start")) {
          tmpText = directChild.getTextContent();
          if ((tmpText != null) && (!tmpText.trim().equals("x:xx"))) {
            feedSessionStart = tmpText.trim();
          }
        } else if (directChild.getNodeName().equals("uniformed_start")) {
          String value = directChild.getTextContent();
          if (value != null) {
            uniformedStart = Boolean.parseBoolean(value);
          }
        }
        else if (directChild.getNodeName().equals("vehicles"))
        {

          NodeList vsChildren = directChild.getChildNodes();
          for (int j = 0; j < vsChildren.getLength(); j++)
          {
            if (vsChildren.item(j).getNodeName().equals("vehicle"))
            {
              addToTypeArray(vsChildren.item(j));
            }
          }
        }
      }
    }
    


    System.out.println("DEBUG: feedSessionDuration = " + feedSessionDuration);
    System.out.println("DEBUG: feedSessionStart = " + feedSessionStart);
    System.out.println("DEBUG: uniformedStart = " + uniformedStart);
    
    //VehicleType.setFeedSessionAttributes(feedSessionDuration, feedSessionStart, uniformedStart);
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
  
  public void test_read()
    throws Exception
  {
    try
    {
      FileReader fr = new FileReader(this.m_filepath);
      BufferedReader br = new BufferedReader(fr);
      
      String line;
      while ((line = br.readLine()) != null) {
        System.out.println(line);
      }
    }
    catch (FileNotFoundException fnfe) {
      throw fnfe;
    }
    catch (IOException ioe) {
      throw ioe;
    }
  }
  
  public ArrayList<VehicleType> getArray() {
    ArrayList<VehicleType> vt = new ArrayList();
    
    for (int i = 0; i < this.m_list.size(); i++) {
      vt.add((VehicleType)this.m_list.get(i));
    }
    
    return vt;
  }
}