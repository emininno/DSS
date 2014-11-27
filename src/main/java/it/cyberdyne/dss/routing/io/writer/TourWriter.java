/*     */ package it.cyberdyne.dss.routing.io.writer;
/*     */ 
/*     */ import it.cyberdyne.dss.routing.engine.Cluster;
/*     */ import it.cyberdyne.dss.routing.model.Node;
/*     */ import it.cyberdyne.dss.routing.model.Vehicle;
/*     */ import it.cyberdyne.dss.routing.utils.Utilities;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.DecimalFormatSymbols;
/*     */ import java.util.ArrayList;
/*     */ import javax.xml.parsers.DocumentBuilder;
/*     */ import javax.xml.parsers.DocumentBuilderFactory;
/*     */ import javax.xml.parsers.ParserConfigurationException;
/*     */ import javax.xml.transform.Transformer;
/*     */ import javax.xml.transform.TransformerConfigurationException;
/*     */ import javax.xml.transform.TransformerException;
/*     */ import javax.xml.transform.TransformerFactory;
/*     */ import javax.xml.transform.dom.DOMSource;
/*     */ import javax.xml.transform.stream.StreamResult;
/*     */ import javax.xml.transform.stream.StreamSource;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.Element;
/*     */ 
/*     */ public class TourWriter
/*     */ {
/*  50 */   private static final DecimalFormat s_3fract = new DecimalFormat();
/*     */ 
/*     */   public void write(OutputStream outStream, InputStream styleStream, ArrayList<Cluster> clusters, int call_id)
/*     */   {
/*  64 */     Document document = null;
/*     */ 
/*  70 */     DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
/*     */     try
/*     */     {
/*  73 */       DocumentBuilder builder = factory.newDocumentBuilder();
/*  74 */       document = builder.newDocument();
/*     */ 
/*  76 */       Element root = document.createElement("document");
/*  77 */       root.setAttribute("call_id", "" + call_id);
/*  78 */       root.setAttribute("create_time", getDateTime());
/*  79 */       document.appendChild(root);
/*     */ 
/*  81 */       Element tours = document.createElement("tours");
/*  82 */       root.appendChild(tours);
/*     */ 
/*  85 */       for (int i = 0; i < clusters.size(); i++) {
/*  86 */         Element tour = document.createElement("tour");
/*  87 */         tour.setAttribute("nr", "" + i);
/*     */ 
/*  92 */         Element totTimeElem = document.createElement("tot_time");
/*  93 */         totTimeElem.setTextContent(s_3fract.format(((Cluster)clusters.get(i)).getTotTime()));
/*  94 */         tour.appendChild(totTimeElem);
/*     */ 
/*  97 */         Element totDistanceElem = document.createElement("tot_distance");
/*  98 */         totDistanceElem.setTextContent(s_3fract.format(((Cluster)clusters.get(i)).getTotDistance()));
/*  99 */         tour.appendChild(totDistanceElem);
/*     */ 
/* 102 */         Element vehicleTag = document.createElement("vehicle");
/* 103 */         writeVehicle(document, (Cluster)clusters.get(i), vehicleTag);
/* 104 */         tour.appendChild(vehicleTag);
/*     */ 
/* 107 */         Element clusterSeedElem = document.createElement("cluster_seed");
/* 108 */         clusterSeedElem.setTextContent("" + ((Cluster)clusters.get(i)).getSeedId());
/* 109 */         tour.appendChild(clusterSeedElem);
/*     */ 
/* 113 */         if (((Cluster)clusters.get(i)).isWaived()) {
/* 114 */           Element waivedElem = document.createElement("waived");
/* 115 */           waivedElem.setTextContent("true");
/* 116 */           tour.appendChild(waivedElem);
/*     */         }
/*     */ 
/* 120 */         Element nodes = document.createElement("nodes");
/* 121 */         writeNodes(document, (Cluster)clusters.get(i), nodes);
/* 122 */         tour.appendChild(nodes);
/*     */ 
/* 125 */         tours.appendChild(tour);
/*     */       }
/*     */ 
/*     */     }
/*     */     catch (ParserConfigurationException pce)
/*     */     {
/* 131 */       pce.printStackTrace();
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 138 */       TransformerFactory tFactory = TransformerFactory.newInstance();
/*     */ 
/* 141 */       Transformer transformer = tFactory.newTransformer(new StreamSource(styleStream));
/* 142 */       DOMSource source = new DOMSource(document);
/*     */ 
/* 145 */       StreamResult result = new StreamResult(outStream);
/*     */ 
/* 147 */       transformer.setOutputProperty("indent", "yes");
/*     */ 
/* 149 */       transformer.transform(source, result);
/*     */     }
/*     */     catch (TransformerConfigurationException e) {
/* 152 */       e.printStackTrace();
/*     */     }
/*     */     catch (TransformerException te) {
/* 155 */       te.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void writeNodes(Document doc, Cluster cluster, Element nodes)
/*     */   {
/* 166 */     for (int i = 0; i < cluster.getTour().size(); i++) {
/* 167 */       Element tagNode = doc.createElement("node");
/* 168 */       Node node = (Node)cluster.getTour().get(i);
/*     */ 
/* 170 */       Element idElem = doc.createElement("id");
/* 171 */       idElem.setTextContent("" + node.getId());
/* 172 */       Element label = doc.createElement("label");
/* 173 */       label.setTextContent(node.getLabel());
/* 174 */       Element demandElem = doc.createElement("demand");
/* 175 */       demandElem.setTextContent(s_3fract.format(node.getDemand()));
/*     */ 
/* 179 */       Element partialDemandElem = null;
/* 180 */       if (!Utilities.doubleEqual(node.getDemand(), node.getDemandOnCluster(cluster.getSeedId()))) {
/* 181 */         partialDemandElem = doc.createElement("demand_on_cluster");
/* 182 */         partialDemandElem.setTextContent(s_3fract.format(node.getDemandOnCluster(cluster.getSeedId())));
/*     */       }
/*     */ 
/* 186 */       double time = 0.0D;
/* 187 */       if (i > 0) {
/* 188 */         time = node.getArrivalTime();
/*     */       }
/* 190 */       String strTime = Utilities.arrivalInstant(cluster.getVehicle().getStartHour(), time);
/*     */ 
/* 194 */       Element timeTag = doc.createElement("arrival_time");
/* 195 */       timeTag.setTextContent(strTime);
/*     */ 
/* 198 */       Element daysTag = null;
/* 199 */       int daysAfter = Utilities.getDaysAfter(cluster.getVehicle().getStartHour(), time);
/* 200 */       if (daysAfter > 0) {
/* 201 */         daysTag = doc.createElement("days_after");
/* 202 */         daysTag.setTextContent("" + daysAfter);
/*     */       }
/*     */ 
/* 206 */       tagNode.appendChild(idElem);
/* 207 */       tagNode.appendChild(label);
/* 208 */       tagNode.appendChild(demandElem);
/* 209 */       if (partialDemandElem != null)
/* 210 */         tagNode.appendChild(partialDemandElem);
/* 211 */       tagNode.appendChild(timeTag);
/* 212 */       if (daysTag != null) {
/* 213 */         tagNode.appendChild(daysTag);
/*     */       }
/*     */ 
/* 216 */       nodes.appendChild(tagNode);
/*     */     }
/*     */   }
/*     */ 
/*     */   private void writeVehicle(Document doc, Cluster clst, Element tagVehicle)
/*     */   {
/* 227 */     Vehicle vehicle = clst.getVehicle();
/* 228 */     Element code = doc.createElement("code");
/* 229 */     code.setTextContent(vehicle.getCode());
/*     */ 
/* 232 */     Element maxLoad = doc.createElement("max_load");
/* 233 */     if (Utilities.isInfinity(vehicle.getMaxLoad()))
/* 234 */       maxLoad.setTextContent("INF");
/*     */     else {
/* 236 */       maxLoad.setTextContent(s_3fract.format(vehicle.getMaxLoad()));
/*     */     }
/* 238 */     Element load = doc.createElement("load");
/*     */ 
/* 241 */     load.setTextContent(s_3fract.format(clst.getTotLoad()));
/*     */ 
/* 243 */     tagVehicle.appendChild(code);
/* 244 */     tagVehicle.appendChild(maxLoad);
/* 245 */     tagVehicle.appendChild(load);
/*     */   }
/*     */ 
/*     */   private String getDateTime()
/*     */   {
/* 250 */     return Utilities.getDateTime();
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  46 */     DecimalFormatSymbols symbols = new DecimalFormatSymbols();
/*  47 */     symbols.setDecimalSeparator('.');
/*     */ 
/*  49 */     String format = "#.###";
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.io.writer.TourWriter
 * JD-Core Version:    0.6.2
 */