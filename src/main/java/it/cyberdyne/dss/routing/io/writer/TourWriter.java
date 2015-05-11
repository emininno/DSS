package it.cyberdyne.dss.routing.io.writer;

import it.cyberdyne.dss.routing.engine.Cluster;
import it.cyberdyne.dss.routing.model.Node;
import it.cyberdyne.dss.routing.model.Vehicle;
import it.cyberdyne.dss.routing.utils.Utilities;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
















public class TourWriter
{
  private static DecimalFormat s_3fract;
  
  static
  {
    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
    symbols.setDecimalSeparator('.');
    
    String format = "#.###";
    s_3fract = new DecimalFormat(format, symbols);
  }
  









  public void write(OutputStream outStream, InputStream styleStream, ArrayList<Cluster> clusters, int call_id)
  {
    Document document = null;
    




    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try
    {
      DocumentBuilder builder = factory.newDocumentBuilder();
      document = builder.newDocument();
      
      Element root = document.createElement("document");
      root.setAttribute("call_id", "" + call_id);
      root.setAttribute("create_time", getDateTime());
      document.appendChild(root);
      
      Element tours = document.createElement("tours");
      root.appendChild(tours);
      

      for (int i = 0; i < clusters.size(); i++) {
        Element tour = document.createElement("tour");
        tour.setAttribute("nr", "" + i);
        



        Element totTimeElem = document.createElement("tot_time");
        totTimeElem.setTextContent(s_3fract.format(((Cluster)clusters.get(i)).getTotTime()));
        tour.appendChild(totTimeElem);
        

        Element totDistanceElem = document.createElement("tot_distance");
        totDistanceElem.setTextContent(s_3fract.format(((Cluster)clusters.get(i)).getTotDistance()));
        tour.appendChild(totDistanceElem);
        

        Element vehicleTag = document.createElement("vehicle");
        writeVehicle(document, (Cluster)clusters.get(i), vehicleTag);
        tour.appendChild(vehicleTag);
        

        Element clusterSeedElem = document.createElement("cluster_seed");
        clusterSeedElem.setTextContent("" + ((Cluster)clusters.get(i)).getSeedId());
        tour.appendChild(clusterSeedElem);
        


        if (((Cluster)clusters.get(i)).isWaived()) {
          Element waivedElem = document.createElement("waived");
          waivedElem.setTextContent("true");
          tour.appendChild(waivedElem);
        }
        

        Element nodes = document.createElement("nodes");
        writeNodes(document, (Cluster)clusters.get(i), nodes);
        tour.appendChild(nodes);
        

        tours.appendChild(tour);
      }
      
    }
    catch (ParserConfigurationException pce)
    {
      pce.printStackTrace();
    }
    


    try
    {
      TransformerFactory tFactory = TransformerFactory.newInstance();
      

      Transformer transformer = tFactory.newTransformer(new StreamSource(styleStream));
      DOMSource source = new DOMSource(document);
      

      StreamResult result = new StreamResult(outStream);
      
      transformer.setOutputProperty("indent", "yes");
      
      transformer.transform(source, result);
    }
    catch (TransformerConfigurationException e) {
      e.printStackTrace();
    }
    catch (TransformerException te) {
      te.printStackTrace();
    }
  }
  





  private void writeNodes(Document doc, Cluster cluster, Element nodes)
  {
    for (int i = 0; i < cluster.getTour().size(); i++) {
      Element tagNode = doc.createElement("node");
      Node node = (Node)cluster.getTour().get(i);
      
      Element idElem = doc.createElement("id");
      idElem.setTextContent("" + node.getId());
      Element label = doc.createElement("label");
      label.setTextContent(node.getLabel());
      Element demandElem = doc.createElement("demand");
      demandElem.setTextContent(s_3fract.format(node.getDemand()));
      


      Element partialDemandElem = null;
      if (!Utilities.doubleEqual(node.getDemand(), node.getDemandOnCluster(cluster.getSeedId()))) {
        partialDemandElem = doc.createElement("demand_on_cluster");
        partialDemandElem.setTextContent(s_3fract.format(node.getDemandOnCluster(cluster.getSeedId())));
      }
      

      double time = 0.0D;
      if (i > 0) {
        time = node.getArrivalTime();
      }
      String strTime = Utilities.arrivalInstant(cluster.getVehicle().getStartHour(), time);
      


      Element timeTag = doc.createElement("arrival_time");
      timeTag.setTextContent(strTime);
      

      Element daysTag = null;
      int daysAfter = Utilities.getDaysAfter(cluster.getVehicle().getStartHour(), time);
      if (daysAfter > 0) {
        daysTag = doc.createElement("days_after");
        daysTag.setTextContent("" + daysAfter);
      }
      

      tagNode.appendChild(idElem);
      tagNode.appendChild(label);
      tagNode.appendChild(demandElem);
      if (partialDemandElem != null)
        tagNode.appendChild(partialDemandElem);
      tagNode.appendChild(timeTag);
      if (daysTag != null) {
        tagNode.appendChild(daysTag);
      }
      
      nodes.appendChild(tagNode);
    }
  }
  





  private void writeVehicle(Document doc, Cluster clst, Element tagVehicle)
  {
    Vehicle vehicle = clst.getVehicle();
    Element code = doc.createElement("code");
    code.setTextContent(vehicle.getCode());
    

    Element maxLoad = doc.createElement("max_load");
    if (Utilities.isInfinity(vehicle.getMaxLoad())) {
      maxLoad.setTextContent("INF");
    } else {
      maxLoad.setTextContent(s_3fract.format(vehicle.getMaxLoad()));
    }
    Element load = doc.createElement("load");
    

    load.setTextContent(s_3fract.format(clst.getTotLoad()));
    
    tagVehicle.appendChild(code);
    tagVehicle.appendChild(maxLoad);
    tagVehicle.appendChild(load);
  }
  
  private String getDateTime()
  {
    return Utilities.getDateTime();
  }
}