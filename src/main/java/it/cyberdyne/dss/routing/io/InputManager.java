/*     */ package it.cyberdyne.dss.routing.io;
/*     */ 
/*     */ import it.cyberdyne.dss.routing.io.reader.MatrixReader;
/*     */ import it.cyberdyne.dss.routing.io.reader.NodeReader;
/*     */ import it.cyberdyne.dss.routing.io.reader.VehicleReader;
/*     */ import it.cyberdyne.dss.routing.model.Node;
/*     */ import it.cyberdyne.dss.routing.model.Vehicle;
/*     */ import it.cyberdyne.dss.routing.model.VehicleType;
/*     */ import it.cyberdyne.dss.routing.utils.Configuration;
/*     */ import it.cyberdyne.dss.routing.utils.Constants;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.File;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class InputManager
/*     */ {
/*  52 */   private static final String NL = Constants.NL;
/*  53 */   private static final String FILE_SEP = File.separator;
/*     */ 
/*  57 */   public static final String INPUT_DIR = Configuration.getDefaultInputDir();
/*     */   public static final String SCHEMA_DIR = "./input/schema";
/*     */   public static final String FILE_BASENAME_DIST_MATRIX = "dist_matrix_";
/*     */   public static final String FILE_BASENAME_TIME_MATRIX = "time_matrix_";
/*     */   public static final String FILE_EXTENSION_MATRIX = ".csv";
/*     */   private int m_callId;
/*     */   private String m_inputDirParh;
/*     */   private MatrixReader m_distMatrixReader;
/*     */   private MatrixReader m_timeMatrixReader;
/*     */   private VehicleReader m_vehicleReader;
/*     */   private NodeReader m_nodeReader;
/*     */   private boolean m_timeFilePresent;
/*     */ 
/*     */   public InputManager(int call_id, String inputDirPath)
/*     */   {
/*  93 */     this.m_callId = call_id;
/*  94 */     if ((inputDirPath != null) && (!inputDirPath.trim().isEmpty()))
/*  95 */       this.m_inputDirParh = inputDirPath;
/*     */     else
/*  97 */       this.m_inputDirParh = INPUT_DIR;
/*     */   }
/*     */ 
/*     */   public InputManager(int call_id, Double[][] distMatr, Double[][] timeMatr, ArrayList<Node> nodes, ArrayList<VehicleType> vehTypes)
/*     */   {
/* 112 */     directInit(call_id, distMatr, timeMatr, nodes, vehTypes);
/*     */   }
/*     */ 
/*     */   private void directInit(int call_id, Double[][] distMatr, Double[][] timeMatr, ArrayList<Node> nodes, ArrayList<VehicleType> vehTypes)
/*     */   {
/* 123 */     this.m_callId = call_id;
/* 124 */     this.m_distMatrixReader = new MatrixReader(distMatr);
/* 125 */     this.m_timeMatrixReader = new MatrixReader(timeMatr);
/* 126 */     this.m_nodeReader = new NodeReader(nodes);
/*     */   }
/*     */ 
/*     */   public InputManager(int call_id, Double[][] distMatr, double avgSpeed, ArrayList<Node> nodes, ArrayList<VehicleType> vehTypes)
/*     */   {
/* 142 */     if ((distMatr != null) && (distMatr.length > 0) && (distMatr[0].length > 0)) {
/* 143 */       Double[][] timeMatr = new Double[distMatr.length][distMatr[0].length];
/*     */ 
/* 145 */       for (int i = 0; i < distMatr.length; i++) {
/* 146 */         for (int j = 0; j < distMatr[0].length; j++)
/* 147 */           timeMatr[i][j] = Double.valueOf(distMatr[i][j].doubleValue() / avgSpeed);
/*     */       }
/* 149 */       directInit(call_id, distMatr, timeMatr, nodes, vehTypes);
/*     */     }
/*     */   }
/*     */ 
/*     */   public InputManager(int call_id, Double[][] distMatr, ArrayList<Node> nodes, ArrayList<VehicleType> vehTypes)
/*     */   {
/* 165 */     this(call_id, distMatr, Constants.AVG_SPEED, nodes, vehTypes);
/*     */   }
/*     */ 
/*     */   public void directCheck()
/*     */   {
/*     */   }
/*     */ 
/*     */   public int getCallId()
/*     */   {
/* 178 */     return this.m_callId;
/*     */   }
/*     */ 
/*     */   public boolean check()
/*     */     throws FileNotFoundException
/*     */   {
/* 190 */     boolean result = true;
/* 191 */     File dir = new File(this.m_inputDirParh);
/* 192 */     if (dir.exists()) {
/* 193 */       File vehiclesFile = new File(dir, "vehicles_" + this.m_callId + ".xml");
/*     */ 
/* 195 */       File nodesFile = new File(dir, "nodes_" + this.m_callId + ".xml");
/*     */ 
/* 197 */       File distMatrixFile = new File(dir, "dist_matrix_" + this.m_callId + ".csv");
/*     */ 
/* 200 */       String resultString = "";
/* 201 */       if (!checkReadable(vehiclesFile))
/* 202 */         resultString = resultString + errorString(vehiclesFile) + NL;
/* 203 */       if (!checkReadable(nodesFile))
/* 204 */         resultString = resultString + errorString(nodesFile) + NL;
/* 205 */       if (!checkReadable(distMatrixFile)) {
/* 206 */         resultString = resultString + errorString(distMatrixFile) + NL;
/*     */       }
/*     */ 
/* 210 */       File timeMatrixFile = new File(dir, "time_matrix_" + this.m_callId + ".csv");
/*     */ 
/* 212 */       if (!checkReadable(timeMatrixFile))
/*     */       {
/* 214 */         System.out.println("\tLa matrice dei tempi sara' creata automaticamente con velocita' media " + Constants.AVG_SPEED);
/*     */ 
/* 217 */         this.m_timeFilePresent = false;
/*     */       }
/*     */       else
/*     */       {
/* 221 */         this.m_timeFilePresent = true;
/*     */       }
/*     */ 
/* 224 */       if (!resultString.equals("")) {
/* 225 */         result = false;
/* 226 */         System.err.println("Check error: \n" + resultString);
/*     */       }
/*     */     }
/*     */     else {
/* 230 */       System.err.println("Check error: Directory " + dir.getAbsolutePath() + " inesistente!");
/*     */ 
/* 232 */       result = false;
/*     */     }
/* 234 */     return result;
/*     */   }
/*     */ 
/*     */   private boolean checkReadable(File f)
/*     */   {
/* 243 */     if ((f.exists()) && (f.canRead()))
/* 244 */       return true;
/* 245 */     return false;
/*     */   }
/*     */ 
/*     */   private String errorString(File f) {
/* 249 */     return "File " + f.getName() + " inesistente o non leggibile. ";
/*     */   }
/*     */ 
/*     */   public void readFiles()
/*     */     throws Exception
/*     */   {
/*     */     try
/*     */     {
/* 259 */       VehicleReader vr = new VehicleReader(this.m_inputDirParh, this.m_callId);
/* 260 */       vr.read();
/*     */ 
/* 262 */       this.m_vehicleReader = vr;
/*     */ 
/* 265 */       NodeReader nr = new NodeReader(this.m_inputDirParh, this.m_callId);
/* 266 */       nr.read();
/*     */ 
/* 268 */       System.out.println("DEBUG. Size del vettore dei nodi " + nr.getArray().size());
/* 269 */       this.m_nodeReader = nr;
/*     */ 
/* 272 */       String distFilePath = this.m_inputDirParh + "/" + "dist_matrix_" + this.m_callId + ".csv";
/*     */ 
/* 274 */       MatrixReader dists = new MatrixReader(distFilePath);
/* 275 */       dists.read();
/*     */ 
/* 279 */       System.out.println("dists.getRowCount() = " + dists.getRowCount());
/*     */ 
/* 284 */       String timeFileName = "time_matrix_" + this.m_callId + ".csv";
/*     */ 
/* 286 */       String timeFilepath = "";
/* 287 */       if (this.m_timeFilePresent) {
/* 288 */         timeFilepath = this.m_inputDirParh + FILE_SEP + timeFileName;
/*     */       }
/*     */       else
/*     */       {
/* 294 */         timeFilepath = Constants.WORK_DIR + FILE_SEP + timeFileName;
/*     */ 
/* 296 */         buildTimeMatrixFile(Constants.AVG_SPEED, dists, timeFilepath);
/* 297 */         System.out.println("DEBUG: al momento la matrice dei tempi e' generata in ./work considerando una velocita' media di " + Constants.AVG_SPEED + " km/h");
/*     */       }
/*     */ 
/* 302 */       MatrixReader durations = new MatrixReader(timeFilepath);
/* 303 */       durations.read();
/*     */ 
/* 307 */       this.m_distMatrixReader = dists;
/* 308 */       this.m_timeMatrixReader = durations;
/*     */ 
/* 310 */       checkPostRead();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/* 314 */       e.printStackTrace();
/* 315 */       throw e;
/*     */     }
/*     */   }
/*     */ 
/*     */   private boolean checkPostRead()
/*     */     throws Exception
/*     */   {
/* 331 */     boolean check = true;
/*     */ 
/* 333 */     if (this.m_distMatrixReader.getRowCount() != this.m_distMatrixReader.getColumnCount()) {
/* 334 */       check = false;
/* 335 */       String err = "ERRORE: la matrice delle distanze deve essere quadrata!";
/* 336 */       System.err.println(err);
/* 337 */       throw new RuntimeException(err);
/*     */     }
/*     */ 
/* 340 */     if (this.m_timeMatrixReader.getRowCount() != this.m_timeMatrixReader.getColumnCount()) {
/* 341 */       check = false;
/* 342 */       String err = "ERRORE: la matrice delle dei tempi deve essere quadrata!";
/* 343 */       System.err.println(err);
/* 344 */       throw new RuntimeException(err);
/*     */     }
/*     */ 
/* 347 */     if (this.m_distMatrixReader.getColumnCount() != this.m_timeMatrixReader.getColumnCount()) {
/* 348 */       check = false;
/* 349 */       String err = "ERRORE: la matrice delle distanze e la matrice dei tempi hanno dimensioni diverse.";
/* 350 */       System.err.println(err);
/* 351 */       throw new RuntimeException(err);
/*     */     }
/*     */ 
/* 354 */     if (this.m_distMatrixReader.getColumnCount() != this.m_nodeReader.getArray().size()) {
/* 355 */       check = false;
/* 356 */       String err = "ERRORE: la matrice delle distanze e vettore dei nodi/domande hanno dimensioni incompatibili.";
/* 357 */       System.err.println(err);
/* 358 */       throw new RuntimeException(err);
/*     */     }
/*     */ 
/* 361 */     if ((this.m_vehicleReader.getVehicles() == null) || (this.m_vehicleReader.getVehicles().isEmpty()))
/*     */     {
/* 363 */       check = false;
/* 364 */       String err = "ERRORE: parco veicoli vuoto.";
/* 365 */       System.err.println(err);
/* 366 */       throw new RuntimeException(err);
/*     */     }
/* 368 */     return check;
/*     */   }
/*     */ 
/*     */   public Double[][] getDistMatrix()
/*     */   {
/* 377 */     return this.m_distMatrixReader.getMatrix();
/*     */   }
/*     */ 
/*     */   public Double[] getDistRow(int index)
/*     */   {
/* 385 */     Double[] row = new Double[this.m_distMatrixReader.getRow(index).length];
/*     */ 
/* 387 */     System.arraycopy(this.m_distMatrixReader.getRow(index), 0, row, 0, row.length);
/* 388 */     return row;
/*     */   }
/*     */ 
/*     */   public double getDistance(int src, int dest)
/*     */     throws Exception
/*     */   {
/* 400 */     double dist = -1.0D;
/*     */     try {
/* 402 */       dist = this.m_distMatrixReader.at(src, dest).doubleValue();
/*     */     }
/*     */     catch (Exception e) {
/* 405 */       e.printStackTrace();
/* 406 */       throw e;
/*     */     }
/* 408 */     return dist;
/*     */   }
/*     */ 
/*     */   public double getDistanceRT(int src, int dest)
/*     */     throws Exception
/*     */   {
/* 418 */     return getDistance(src, dest) + getDistance(dest, src);
/*     */   }
/*     */ 
/*     */   public double getTime(int src, int dest)
/*     */     throws Exception
/*     */   {
/* 432 */     double time = -1.0D;
/*     */     try {
/* 434 */       time = this.m_timeMatrixReader.at(src, dest).doubleValue();
/*     */     }
/*     */     catch (Exception e) {
/* 437 */       e.printStackTrace();
/* 438 */       throw e;
/*     */     }
/* 440 */     return time;
/*     */   }
/*     */ 
/*     */   public double getTimeRT(int src, int dest)
/*     */     throws Exception
/*     */   {
/* 449 */     return getTime(src, dest) + getTime(dest, src);
/*     */   }
/*     */ 
/*     */   public ArrayList<Node> getNodes()
/*     */   {
/* 458 */     ArrayList nodes = new ArrayList(this.m_nodeReader.getArray());
/* 459 */     return nodes;
/*     */   }
/*     */ 
/*     */   public ArrayList<Vehicle> getVehicles()
/*     */   {
/* 467 */     return this.m_vehicleReader.getVehicles();
/*     */   }
/*     */ 
/*     */   public Node getNodeByID(int id)
/*     */   {
/* 477 */     return this.m_nodeReader.getNodeByID(id);
/*     */   }
/*     */ 
/*     */   public void markNodeAsClustered(int nodeId, int seedId)
/*     */   {
/* 489 */     for (int i = 0; i < this.m_nodeReader.getArray().size(); i++)
/* 490 */       if (((Node)this.m_nodeReader.getArray().get(i)).getId() == nodeId) {
/* 491 */         ((Node)this.m_nodeReader.getArray().get(i)).setSeedId(seedId);
/* 492 */         break;
/*     */       }
/*     */   }
/*     */ 
/*     */   public void markNodeAsRouted(int nodeId)
/*     */   {
/* 501 */     this.m_nodeReader.getNodeByID(nodeId).setRouted(true);
/*     */   }
/*     */ 
/*     */   private void buildTimeMatrixFile(double avgSpeed, MatrixReader dists, String timeFilePath)
/*     */   {
/*     */     try
/*     */     {
/* 511 */       BufferedWriter bw = new BufferedWriter(new FileWriter(timeFilePath));
/*     */ 
/* 513 */       for (int i = 0; i < dists.getRowCount(); i++) {
/* 514 */         String line = "";
/* 515 */         for (int j = 0; j < dists.getColumnCount(); j++)
/*     */         {
/* 520 */           double dist = dists.at(i, j).doubleValue();
/* 521 */           double t = 60.0D * dist / avgSpeed;
/* 522 */           line = line + t;
/* 523 */           if (j < dists.getColumnCount() - 1)
/* 524 */             line = line + ", ";
/*     */           else
/* 526 */             line = line + ";";
/*     */         }
/* 528 */         line = line + NL;
/*     */ 
/* 530 */         bw.write(line);
/*     */       }
/*     */ 
/* 533 */       bw.close();
/*     */     }
/*     */     catch (IOException ioe) {
/* 536 */       ioe.printStackTrace();
/*     */     }
/*     */     catch (Exception e) {
/* 539 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */ 
/*     */   private void whereWeAre()
/*     */   {
/* 548 */     System.out.println("Sono in InputManager.whereWeAre()");
/* 549 */     File currDir = new File(".");
/* 550 */     System.out.println("current dir = " + currDir.getAbsolutePath());
/*     */ 
/* 553 */     URL url = getClass().getResource("/");
/* 554 */     System.out.println("URL. file=" + url.getFile());
/* 555 */     File rootDir = new File(url.getFile());
/* 556 */     System.out.println("rootDir = " + rootDir.getAbsolutePath());
/*     */ 
/* 559 */     URL urlLoader = getClass().getClassLoader().getResource("");
/* 560 */     File loadDir = new File(urlLoader.getPath());
/* 561 */     System.out.println("loadDir = " + loadDir.getAbsolutePath());
/*     */ 
/* 563 */     URL url2 = getClass().getResource("/config");
/*     */ 
/* 565 */     File confDir = new File(url2.getPath());
/* 566 */     System.out.println("configDir = " + confDir.getAbsolutePath());
/*     */ 
/* 568 */     URL confFileUrl = getClass().getResource("/config/config.properties");
/*     */     try
/*     */     {
/* 572 */       InputStream is = confFileUrl.openStream();
/* 573 */       BufferedReader br = new BufferedReader(new InputStreamReader(is));
/* 574 */       String str = null;
/* 575 */       while ((str = br.readLine()) != null)
/* 576 */         System.out.println("conf: " + str);
/*     */     }
/*     */     catch (IOException ioe) {
/* 579 */       ioe.printStackTrace();
/*     */     }
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.io.InputManager
 * JD-Core Version:    0.6.2
 */