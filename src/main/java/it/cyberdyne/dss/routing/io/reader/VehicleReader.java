/*     */ package it.cyberdyne.dss.routing.io.reader;
/*     */ 
/*     */ import it.cyberdyne.dss.routing.model.Vehicle;
/*     */ import it.cyberdyne.dss.routing.model.VehicleType;
/*     */ import it.cyberdyne.dss.routing.utils.Constants;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import org.w3c.dom.Document;
/*     */ import org.w3c.dom.NamedNodeMap;
/*     */ import org.w3c.dom.Node;
/*     */ import org.w3c.dom.NodeList;
/*     */ 
/*     */ public class VehicleReader extends XMLReader
/*     */ {
/*     */   public static final String FILE_BASENAME = "vehicles_";
/*  32 */   public static final String SCHEMA_FILENAME = null;
/*     */   private ArrayList<Vehicle> m_vehicles;
/*     */ 
/*     */   public VehicleReader(String directory, int call_id)
/*     */   {
/*  53 */     super(call_id);
/*  54 */     this.m_filepath = (directory + File.separator + "vehicles_" + call_id + ".xml");
/*     */   }
/*     */ 
/*     */   public VehicleReader(ArrayList<VehicleType> types)
/*     */   {
/*  63 */     super(types);
/*     */   }
/*     */ 
/*     */   public void read()
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/*  73 */       File file = new File(this.m_filepath);
/*     */ 
/*  75 */       URL confUrl = getClass().getResource(Constants.XML_SCHEMA_VEHICLES);
/*     */ 
/*  77 */       if (confUrl != null)
/*  78 */         parse(file, confUrl.openStream());
/*     */       else {
/*  80 */         parse(file, null);
/*     */       }
/*  82 */       if (this.m_document != null)
/*     */       {
/*  84 */         createVehicleTypeArray();
/*     */       }
/*     */     }
/*     */     catch (Exception ioe) {
/*  88 */       throw ioe;
/*     */     }
/*     */   }
/*     */ 
/*     */   private void createVehicleTypeArray()
/*     */     throws Exception
/*     */   {
/*  96 */     this.m_list = new ArrayList();
/*     */ 
/*  98 */     Node root = this.m_document.getFirstChild();
/*     */ 
/* 100 */     Integer readCallId = Integer.valueOf(Integer.parseInt(root.getAttributes().getNamedItem("call_id").getNodeValue()));
/*     */ 
/* 105 */     if (readCallId.intValue() != this.m_callId) {
/* 106 */       this.m_document = null;
/* 107 */       throw new Exception("Errore: Incongruenza tra nome del file e attributo call_id");
/*     */     }
/*     */ 
/* 111 */     NodeList elems = root.getChildNodes();
/* 112 */     for (int i = 0; i < elems.getLength(); i++)
/*     */     {
/* 114 */       Node directChild = elems.item(i);
/* 115 */       if (directChild.getNodeType() == 1)
/*     */       {
/*     */         Float wkTime;
/* 117 */         if (directChild.getNodeName().equals("working_time"))
/*     */         {
/* 119 */           wkTime = Float.valueOf(Float.parseFloat(directChild.getTextContent()));
/*     */         }
/* 126 */         else if (directChild.getNodeName().equals("vehicles"))
/*     */         {
/* 129 */           NodeList vsChildren = directChild.getChildNodes();
/* 130 */           for (int j = 0; j < vsChildren.getLength(); j++)
/*     */           {
/* 132 */             if (vsChildren.item(j).getNodeName().equals("vehicle"))
/*     */             {
/* 134 */               addToTypeArray(vsChildren.item(j));
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   private void addToTypeArray(Node vehicleNode)
/*     */     throws Exception
/*     */   {
/* 148 */     String code = vehicleNode.getAttributes().getNamedItem("code").getNodeValue();
/*     */ 
/* 151 */     NodeList children = vehicleNode.getChildNodes();
/*     */ 
/* 153 */     Integer qty = Integer.valueOf(0);
/* 154 */     String model = null;
/*     */ 
/* 159 */     Double maxLoad = new Double(9999999999.0D);
/* 160 */     Double maxDistance = Double.valueOf(9999999999.0D);
/* 161 */     Double maxTime = new Double(9999999999.0D);
/*     */ 
/* 163 */     String startHour = "0:00";
/*     */ 
/* 165 */     Float srvTimeCoeff = new Float(Constants.SERVICE_TIME_COEFF);
/*     */ 
/* 168 */     for (int i = 0; i < children.getLength(); i++)
/*     */     {
/* 170 */       Node child = children.item(i);
/* 171 */       if (child.getNodeType() == 1) {
/* 172 */         String name = child.getNodeName();
/* 173 */         String value = child.getTextContent();
/*     */ 
/* 175 */         if (value != null) {
/* 176 */           if (name.equals("qty")) {
/* 177 */             qty = Integer.valueOf(Integer.parseInt(value));
/*     */           }
/* 179 */           else if (name.equals("model")) {
/* 180 */             model = value.trim();
/*     */           }
/* 182 */           else if ((name.equals("max_load")) && (!value.trim().equals("INF")))
/*     */           {
/* 187 */             maxLoad = Double.valueOf(Double.parseDouble(value));
/*     */           }
/* 189 */           else if ((name.equals("max_distance")) && (!value.trim().equals("INF")))
/*     */           {
/* 192 */             maxDistance = Double.valueOf(Double.parseDouble(value));
/*     */           }
/* 194 */           else if ((name.equals("max_time")) && (!value.trim().equals("INF")))
/*     */           {
/* 197 */             maxTime = Double.valueOf(Double.parseDouble(value));
/*     */           }
/* 199 */           else if (name.equals("start_hour")) {
/* 200 */             startHour = value.trim();
/*     */           }
/* 202 */           else if ((name.equals("service_time_coeff")) && (!value.trim().equals("NaN")))
/*     */           {
/* 204 */             srvTimeCoeff = Float.valueOf(Float.parseFloat(value.trim()));
/* 205 */             System.out.println("DEBUG: service_time_coeff = " + srvTimeCoeff);
/*     */           }
/*     */ 
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 216 */     System.out.println("DEBUG. VehicleReader. code = " + code + ". service_time_coeff = " + srvTimeCoeff);
/*     */ 
/* 219 */     VehicleType vehType = new VehicleType(Constants.DEP_ID, code, model, qty.intValue(), maxLoad.doubleValue(), maxDistance.doubleValue(), maxTime.doubleValue(), startHour, srvTimeCoeff);
/*     */ 
/* 222 */     this.m_list.add(vehType);
/*     */   }
/*     */ 
/*     */   public ArrayList<VehicleType> getTypeArray()
/*     */   {
/* 230 */     ArrayList vt = new ArrayList();
/*     */ 
/* 232 */     for (int i = 0; i < this.m_list.size(); i++) {
/* 233 */       vt.add((VehicleType)this.m_list.get(i));
/*     */     }
/*     */ 
/* 236 */     return vt;
/*     */   }
/*     */ 
/*     */   public ArrayList<Vehicle> getVehicles()
/*     */   {
/* 246 */     if ((this.m_vehicles == null) && 
/* 247 */       (this.m_list != null) && (this.m_list.size() > 0))
/*     */     {
/* 249 */       Collections.sort(this.m_list);
/* 250 */       this.m_vehicles = new ArrayList();
/*     */ 
/* 252 */       for (int i = 0; i < this.m_list.size(); i++)
/*     */       {
/* 254 */         VehicleType vt = (VehicleType)this.m_list.get(i);
/* 255 */         for (int j = 0; j < vt.getTotQty(); j++) {
/* 256 */           Vehicle v = new Vehicle(vt);
/* 257 */           this.m_vehicles.add(v);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 264 */     return this.m_vehicles;
/*     */   }
/*     */ 
/*     */   public void test_read()
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 280 */       FileReader fr = new FileReader(this.m_filepath);
/* 281 */       BufferedReader br = new BufferedReader(fr);
/*     */       String line;
/* 284 */       while ((line = br.readLine()) != null)
/* 285 */         System.out.println(line);
/*     */     }
/*     */     catch (FileNotFoundException fnfe)
/*     */     {
/* 289 */       throw fnfe;
/*     */     }
/*     */     catch (IOException ioe) {
/* 292 */       throw ioe;
/*     */     }
/*     */   }
/*     */ 
/*     */   public ArrayList<VehicleType> getArray() {
/* 297 */     ArrayList vt = new ArrayList();
/*     */ 
/* 299 */     for (int i = 0; i < this.m_list.size(); i++) {
/* 300 */       vt.add((VehicleType)this.m_list.get(i));
/*     */     }
/*     */ 
/* 303 */     return vt;
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.io.reader.VehicleReader
 * JD-Core Version:    0.6.2
 */