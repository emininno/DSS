package it.cyberdyne.dss.routing.io.reader;

import it.cyberdyne.dss.routing.utils.Constants;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import org.w3c.dom.NodeList;

public class NodeReader
  extends XMLReader
{
  public static final String FILE_BASENAME = "nodes_";
  public static final String SCHEMA_FILENAME = null;
  public static final boolean DEFAULT_DEMAND_DIVISIBLE = false;

  public NodeReader(String directory, int call_id)
  {
    super(call_id);
    this.m_filepath = (directory + File.separator + "nodes_" + call_id + ".xml");
  }

  public NodeReader(ArrayList<it.cyberdyne.dss.routing.model.Node> nodes)
  {
    super(nodes);
  }

  public void read()
    throws Exception
  {
    try
    {
      File file = new File(this.m_filepath);
      
      URL confUrl = getClass().getResource(Constants.XML_SCHEMA_NODES);
      
      if (confUrl != null) {
        parse(file, confUrl.openStream());
      } else {
        parse(file, null);
      }
      if (this.m_document != null)
      {
        createNodeArray();
      }
    }
    catch (Exception ioe) {
      throw ioe;
    }
  }

  private void createNodeArray()
    throws Exception
  {
    this.m_list = new ArrayList();
    
    org.w3c.dom.Node root = this.m_document.getFirstChild();
    
    Integer readCallId = Integer.valueOf(Integer.parseInt(root.getAttributes().getNamedItem("call_id").getNodeValue()));

    if (readCallId.intValue() != this.m_callId) {
      this.m_document = null;
      throw new Exception("Errore: Incongruenza tra nome del file e attributo call_id");
    }
    

    NodeList elems = root.getChildNodes();
    for (int i = 0; i < elems.getLength(); i++)
    {
      org.w3c.dom.Node directChild = elems.item(i);
      if (directChild.getNodeType() == 1)
      {
        if (directChild.getNodeName().equals("nodes"))
        {

          NodeList children = directChild.getChildNodes();
          for (int j = 0; j < children.getLength(); j++)
          {
            if (children.item(j).getNodeName().equals("node"))
            {
              addToArray(children.item(j));
            }
          }
        }
        else if (directChild.getNodeName().equals("granularity"))
        {
          String value = directChild.getTextContent();
          float granularity = 0.0F;
          if (value != null) {
            granularity = Float.parseFloat(value);
          }
          it.cyberdyne.dss.routing.model.Node.setDemandGranularity(Float.valueOf(granularity));
        }
        else if (directChild.getNodeName().equals("demand_divisible"))
        {
          String value = directChild.getTextContent();
          boolean divisible = false;
          if (value != null) {
            divisible = Boolean.parseBoolean(value);
          }
          it.cyberdyne.dss.routing.model.Node.setDemandDivisible(divisible);
        }
      }
    }
    
    System.out.println("NodeReader: granularity = " + it.cyberdyne.dss.routing.model.Node.getDemandGranularity());
    System.out.println("NodeReader: demand_divisible = " + it.cyberdyne.dss.routing.model.Node.isDemandDivisible());
  }
  


  private void addToArray(org.w3c.dom.Node node)
    throws Exception
  {
    Integer id = Integer.valueOf(Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue()));
    

    NodeList children = node.getChildNodes();
    
    String label = null;
    Float demand = Float.valueOf(0.0F);
    Double serviceTime = new Double(Constants.SERVICE_TIME_CONSTANT);
    
    String openHour = null;
    String closeHour = null;
    

    for (int i = 0; i < children.getLength(); i++)
    {
      org.w3c.dom.Node child = children.item(i);
      if (child.getNodeType() == 1) {
        String name = child.getNodeName();
        String value = child.getTextContent();
        
        if (value != null) {
          if (name.equals("label")) {
            label = value.trim();
          }
          else if (name.equals("demand")) {
            demand = Float.valueOf(Float.parseFloat(value));
          }
          else if ((name.equals("service_time")) && (!value.trim().equals("NaN")))
          {
            serviceTime = Double.valueOf(Float.parseFloat(value));
          }
          else if ((name.equals("open_hour")) && (!value.trim().equals("")))
          {

            openHour = value.trim();
          }
          else if ((name.equals("close_hour")) && (!value.trim().equals("")))
          {
            closeHour = value.trim();
          }
        }
      }
    }

    System.out.println("DEBUG. NodeReader. label/service_time = " + label + " / " + serviceTime + "\n");
    
    it.cyberdyne.dss.routing.model.Node clientNode = new it.cyberdyne.dss.routing.model.Node(id.intValue(), label, demand.floatValue(), serviceTime.floatValue());
    this.m_list.add(clientNode);
  }
  
  public ArrayList<it.cyberdyne.dss.routing.model.Node> getArray()
  {
    ArrayList<it.cyberdyne.dss.routing.model.Node> nodes = new ArrayList();
    
    for (int i = 0; i < this.m_list.size(); i++) {
      nodes.add((it.cyberdyne.dss.routing.model.Node)this.m_list.get(i));
    }
    
    return nodes;
  }
  
  public it.cyberdyne.dss.routing.model.Node getNodeByID(int id)
  {
    it.cyberdyne.dss.routing.model.Node node = null;
    for (int i = 0; i < this.m_list.size(); i++) {
      it.cyberdyne.dss.routing.model.Node n = (it.cyberdyne.dss.routing.model.Node)this.m_list.get(i);
      if (n.getId() == id) {
        node = n;
        break;
      }
    }
    
    return node;
  }
}