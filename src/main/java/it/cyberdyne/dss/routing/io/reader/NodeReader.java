/*     */ package it.cyberdyne.dss.routing.io.reader;
/*     */ 
/*     */ import it.cyberdyne.dss.routing.utils.Constants;
/*     */ import java.io.File;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class NodeReader extends XMLReader
/*     */ {
/*     */   public static final String FILE_BASENAME = "nodes_";
/*  31 */   public static final String SCHEMA_FILENAME = null;
/*     */   public String m_filepath;

/*     */   public NodeReader(String directory, int call_id)
/*     */   {
/*  47 */     super(call_id);
/*  48 */     this.m_filepath = (directory + File.separator + "nodes_" + call_id + ".xml");
/*     */   }
/*     */ 
/*     */   public NodeReader(ArrayList<it.cyberdyne.dss.routing.model.Node> nodes)
/*     */   {
/*  55 */     super(nodes);
/*     */   }
/*     */ 
/*     */   public void read()
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/*  64 */       File file = new File(this.m_filepath);
/*     */ 
/*  66 */       URL confUrl = this.getClass().getResource(Constants.XML_SCHEMA_NODES);
/*     */ 
/*  68 */       if (confUrl != null)
/*  69 */         parse(file, confUrl.openStream());
/*     */       else {
/*  71 */         parse(file, null);
/*     */       }
/*  73 */       if (this.m_document != null)
/*     */       {
/*  75 */         createNodeArray();
/*     */       }
/*     */     }
/*     */     catch (Exception ioe) {
/*  79 */       throw ioe;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void createNodeArray()
/*     */     throws Exception
/*     */   {
/*  93 */     this.m_list = new ArrayList();
/*     */ 
/*  95 */     org.w3c.dom.Node root = this.m_document.getFirstChild();
/*     */ 
/*  97 */     Integer readCallId = Integer.valueOf(Integer.parseInt(root.getAttributes().getNamedItem("call_id").getNodeValue()));
/*     */ 
/* 102 */     if (readCallId.intValue() != this.m_callId) {
/* 103 */       this.m_document = null;
/* 104 */       throw new Exception("Errore: Incongruenza tra nome del file e attributo call_id");
/*     */     }
/*     */ 
/* 108 */     NodeList elems = root.getChildNodes();
/* 109 */     for (int i = 0; i < elems.getLength(); i++)
/*     */     {
/* 111 */       org.w3c.dom.Node directChild = elems.item(i);
/* 112 */       if (directChild.getNodeType() == 1)
/*     */       {
/* 114 */         if (directChild.getNodeName().equals("nodes"))
/*     */         {
/* 117 */           NodeList children = directChild.getChildNodes();
/* 118 */           for (int j = 0; j < children.getLength(); j++)
/*     */           {
/* 120 */             if (children.item(j).getNodeName().equals("node"))
/*     */             {
/* 122 */               addToArray(children.item(j));
/*     */             }
/*     */           }
/*     */         }
/* 126 */         else if (directChild.getNodeName().equals("granularity"))
/*     */         {
/* 128 */           String value = directChild.getTextContent();
/* 129 */           float granularity = 0.0F;
/* 130 */           if (value != null) {
/* 131 */             granularity = Float.parseFloat(value);
/*     */           }
/* 133 */           it.cyberdyne.dss.routing.model.Node.setDemandGranularity(Float.valueOf(granularity));
/*     */         }
/* 135 */         else if (directChild.getNodeName().equals("demand_divisible"))
/*     */         {
/* 137 */           String value = directChild.getTextContent();
/* 138 */           boolean divisible = true;
/* 139 */           if (value != null) {
/* 140 */             divisible = Boolean.parseBoolean(value);
/*     */           }
/* 142 */           it.cyberdyne.dss.routing.model.Node.setDemandDivisible(divisible);
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 147 */     System.out.println("NodeReader: granularity = " + it.cyberdyne.dss.routing.model.Node.getDemandGranularity());
/* 148 */     System.out.println("NodeReader: demand_divisible = " + it.cyberdyne.dss.routing.model.Node.isDemandDivisible());
/*     */   }
/*     */ 
/*     */   private void addToArray(org.w3c.dom.Node node)
/*     */     throws Exception
/*     */   {
/* 156 */     Integer id = Integer.valueOf(Integer.parseInt(node.getAttributes().getNamedItem("id").getNodeValue()));
/*     */ 
/* 159 */     NodeList children = node.getChildNodes();
/*     */ 
/* 161 */     String label = null;
/* 162 */     Float demand = Float.valueOf(0.0F);
/* 163 */     Float serviceTime = new Float(Constants.SERVICE_TIME_CONSTANT);
/*     */ 
/* 165 */     String openHour = null;
/* 166 */     String closeHour = null;
/*     */ 
/* 169 */     for (int i = 0; i < children.getLength(); i++)
/*     */     {
/* 171 */       org.w3c.dom.Node child = children.item(i);
/* 172 */       if (child.getNodeType() == 1) {
/* 173 */         String name = child.getNodeName();
/* 174 */         String value = child.getTextContent();
/*     */ 
/* 176 */         if (value != null) {
/* 177 */           if (name.equals("label")) {
/* 178 */             label = value.trim();
/*     */           }
/* 180 */           else if (name.equals("demand")) {
/* 181 */             demand = Float.valueOf(Float.parseFloat(value));
/*     */           }
/* 183 */           else if ((name.equals("service_time")) && (!value.trim().equals("NaN")))
/*     */           {
/* 185 */             serviceTime = Float.valueOf(Float.parseFloat(value));
/*     */           }
/* 187 */           else if ((name.equals("open_hour")) && (!value.trim().equals("")))
/*     */           {
/* 190 */             openHour = value.trim();
/*     */           }
/* 192 */           else if ((name.equals("close_hour")) && (!value.trim().equals("")))
/*     */           {
/* 194 */             closeHour = value.trim();
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 207 */     System.out.println("DEBUG. NodeReader. label/service_time = " + label + " / " + serviceTime + "\n");
/*     */ 
/* 210 */     it.cyberdyne.dss.routing.model.Node clientNode = new it.cyberdyne.dss.routing.model.Node(id.intValue(), label, demand.floatValue(), serviceTime.floatValue());
/* 211 */     this.m_list.add(clientNode);
/*     */   }
/*     */ 
/*     */   public ArrayList<it.cyberdyne.dss.routing.model.Node> getArray()
/*     */   {
/* 219 */     ArrayList nodes = new ArrayList();
/*     */ 
/* 221 */     for (int i = 0; i < this.m_list.size(); i++) {
/* 222 */       nodes.add((it.cyberdyne.dss.routing.model.Node)this.m_list.get(i));
/*     */     }
/*     */ 
/* 225 */     return nodes;
/*     */   }
/*     */ 
/*     */   public it.cyberdyne.dss.routing.model.Node getNodeByID(int id)
/*     */   {
/* 233 */     it.cyberdyne.dss.routing.model.Node node = null;
/* 234 */     for (int i = 0; i < this.m_list.size(); i++) {
/* 235 */       it.cyberdyne.dss.routing.model.Node n = (it.cyberdyne.dss.routing.model.Node)this.m_list.get(i);
/* 236 */       if (n.getId() == id) {
/* 237 */         node = n;
/* 238 */         break;
/*     */       }
/*     */     }
/*     */ 
/* 242 */     return node;
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.io.reader.NodeReader
 * JD-Core Version:    0.6.2
 */