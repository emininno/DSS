package it.cyberdyne.dss.routing.io;

import it.cyberdyne.dss.routing.io.reader.MatrixReader;
import it.cyberdyne.dss.routing.io.reader.NodeReader;
import it.cyberdyne.dss.routing.io.reader.VehicleReader;
import it.cyberdyne.dss.routing.model.Node;
import it.cyberdyne.dss.routing.model.Vehicle;
import it.cyberdyne.dss.routing.model.VehicleType;
import it.cyberdyne.dss.routing.utils.Configuration;
import it.cyberdyne.dss.routing.utils.Constants;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;

public class InputManager
{
  private static final String NL = Constants.NL;
  private static final String FILE_SEP = File.separator;
  public static final String INPUT_DIR = "./input";
  public static final String SCHEMA_DIR = "./input/schema";
  public static final String FILE_BASENAME_DIST_MATRIX = "dist_matrix_";
  public static final String FILE_BASENAME_TIME_MATRIX = "time_matrix_";
  public static final String FILE_EXTENSION_MATRIX = ".csv";

  private int m_callId;
  private String m_inputDirParh;
  private MatrixReader m_distMatrixReader;
  private MatrixReader m_timeMatrixReader;
  private VehicleReader m_vehicleReader;
  private NodeReader m_nodeReader;
  private boolean m_timeFilePresent;
  

  public InputManager(int call_id, String inputDirPath) {
    this.m_callId = call_id;
    if ((inputDirPath != null) && (!inputDirPath.trim().isEmpty())) {
      this.m_inputDirParh = inputDirPath;
    } else {
      this.m_inputDirParh = INPUT_DIR;
    }
  }
  
  public InputManager(int call_id, Double[][] distMatr, Double[][] timeMatr, ArrayList<Node> nodes, ArrayList<VehicleType> vehTypes)
  {
    directInit(call_id, distMatr, timeMatr, nodes, vehTypes);
  }
  
  private void directInit(int call_id, Double[][] distMatr, Double[][] timeMatr, ArrayList<Node> nodes, ArrayList<VehicleType> vehTypes)
  {
    this.m_callId = call_id;
    this.m_distMatrixReader = new MatrixReader(distMatr);
    this.m_timeMatrixReader = new MatrixReader(timeMatr);
    this.m_nodeReader = new NodeReader(nodes);
  }
  
  public InputManager(int call_id, Double[][] distMatr, double avgSpeed, ArrayList<Node> nodes, ArrayList<VehicleType> vehTypes)
  {
    if ((distMatr != null) && (distMatr.length > 0) && (distMatr[0].length > 0)) {
      Double[][] timeMatr = new Double[distMatr.length][distMatr[0].length];
      
      for (int i = 0; i < distMatr.length; i++) {
        for (int j = 0; j < distMatr[0].length; j++)
          timeMatr[i][j] = Double.valueOf(distMatr[i][j].doubleValue() / avgSpeed);
      }
      directInit(call_id, distMatr, timeMatr, nodes, vehTypes);
    }
  }
  

  public InputManager(int call_id, Double[][] distMatr, ArrayList<Node> nodes, ArrayList<VehicleType> vehTypes)
  {
    this(call_id, distMatr, Constants.AVG_SPEED, nodes, vehTypes);
  }
  
  public void directCheck() {}
  
  public int getCallId()
  {
    return this.m_callId;
  }

  public boolean check()
    throws FileNotFoundException
  {
    boolean result = true;
    File dir = new File(this.m_inputDirParh);
    if (dir.exists()) {
      File vehiclesFile = new File(dir, "vehicles_" + this.m_callId + ".xml");
      
      File nodesFile = new File(dir, "nodes_" + this.m_callId + ".xml");
      
      File distMatrixFile = new File(dir, "dist_matrix_" + this.m_callId + ".csv");
      

      String resultString = "";
      if (!checkReadable(vehiclesFile))
        resultString = resultString + errorString(vehiclesFile) + NL;
      if (!checkReadable(nodesFile))
        resultString = resultString + errorString(nodesFile) + NL;
      if (!checkReadable(distMatrixFile)) {
        resultString = resultString + errorString(distMatrixFile) + NL;
      }
      

      File timeMatrixFile = new File(dir, "time_matrix_" + this.m_callId + ".csv");
      
      if (!checkReadable(timeMatrixFile))
      {
        System.out.println("\tLa matrice dei tempi sara' creata automaticamente con velocita' media " + Constants.AVG_SPEED);
        

        this.m_timeFilePresent = false;
      }
      else
      {
        this.m_timeFilePresent = true;
      }
      
      if (!resultString.equals("")) {
        result = false;
        System.err.println("Check error: \n" + resultString);
      }
    }
    else {
      System.err.println("Check error: Directory " + dir.getAbsolutePath() + " inesistente!");
      
      result = false;
    }
    return result;
  }
  
  private boolean checkReadable(File f)
  {
    if ((f.exists()) && (f.canRead()))
      return true;
    return false;
  }
  
  private String errorString(File f) {
    return "File " + f.getName() + " inesistente o non leggibile. ";
  }
  


  public void readFiles()
    throws Exception
  {
    try
    {
      VehicleReader vr = new VehicleReader(this.m_inputDirParh, this.m_callId);
      vr.read();
      
      this.m_vehicleReader = vr;

      NodeReader nr = new NodeReader(this.m_inputDirParh, this.m_callId);
      nr.read();
      
      System.out.println("DEBUG. Size del vettore dei nodi " + nr.getArray().size());
      this.m_nodeReader = nr;
      
      String distFilePath = this.m_inputDirParh + "/" + "dist_matrix_" + this.m_callId + ".csv";
      
      MatrixReader dists = new MatrixReader(distFilePath);
      dists.read();
      
      System.out.println("dists.getRowCount() = " + dists.getRowCount());
      
      String timeFileName = "time_matrix_" + this.m_callId + ".csv";
      
      String timeFilepath = "";
      if (this.m_timeFilePresent) {
        timeFilepath = this.m_inputDirParh + FILE_SEP + timeFileName;

      }
      else
      {

        timeFilepath = Constants.WORK_DIR + FILE_SEP + timeFileName;
        
        buildTimeMatrixFile(Constants.AVG_SPEED, dists, timeFilepath);
        System.out.println("DEBUG: al momento la matrice dei tempi e' generata in ./work considerando una velocita' media di " + Constants.AVG_SPEED + " km/h");
      }
      
      MatrixReader durations = new MatrixReader(timeFilepath);
      durations.read();
      
      this.m_distMatrixReader = dists;
      this.m_timeMatrixReader = durations;
      
      checkPostRead();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw e;
    }
  }
  
  private boolean checkPostRead()
    throws Exception
  {
    boolean check = true;
    
    if (this.m_distMatrixReader.getRowCount() != this.m_distMatrixReader.getColumnCount()) {
      check = false;
      String err = "ERRORE: la matrice delle distanze deve essere quadrata!";
      System.err.println(err);
      throw new RuntimeException(err);
    }
    
    if (this.m_timeMatrixReader.getRowCount() != this.m_timeMatrixReader.getColumnCount()) {
      check = false;
      String err = "ERRORE: la matrice delle dei tempi deve essere quadrata!";
      System.err.println(err);
      throw new RuntimeException(err);
    }
    
    if (this.m_distMatrixReader.getColumnCount() != this.m_timeMatrixReader.getColumnCount()) {
      check = false;
      String err = "ERRORE: la matrice delle distanze e la matrice dei tempi hanno dimensioni diverse.";
      System.err.println(err);
      throw new RuntimeException(err);
    }
    
    if (this.m_distMatrixReader.getColumnCount() != this.m_nodeReader.getArray().size()) {
      check = false;
      String err = "ERRORE: la matrice delle distanze e vettore dei nodi/domande hanno dimensioni incompatibili.";
      System.err.println(err);
      throw new RuntimeException(err);
    }
    
    if ((this.m_vehicleReader.getVehicles() == null) || (this.m_vehicleReader.getVehicles().isEmpty()))
    {
      check = false;
      String err = "ERRORE: parco veicoli vuoto.";
      System.err.println(err);
      throw new RuntimeException(err);
    }
    return check;
  }

  public Double[][] getDistMatrix()
  {
    return this.m_distMatrixReader.getMatrix();
  }

  public Double[] getDistRow(int index)
  {
    Double[] row = new Double[this.m_distMatrixReader.getRow(index).length];
    
    System.arraycopy(this.m_distMatrixReader.getRow(index), 0, row, 0, row.length);
    return row;
  }

  public double getDistance(int src, int dest)
    throws Exception
  {
    double dist = -1.0D;
    try {
      dist = this.m_distMatrixReader.at(src, dest).doubleValue();
    }
    catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    return dist;
  }
  
  public double getDistanceRT(int src, int dest)
    throws Exception
  {
    return getDistance(src, dest) + getDistance(dest, src);
  }
  
  public double getTime(int src, int dest)
    throws Exception
  {
    double time = -1.0D;
    try {
      time = this.m_timeMatrixReader.at(src, dest).doubleValue();
    }
    catch (Exception e) {
      e.printStackTrace();
      throw e;
    }
    return time;
  }
  
  public double getTimeRT(int src, int dest)
    throws Exception
  {
    return getTime(src, dest) + getTime(dest, src);
  }
  
  public ArrayList<Node> getNodes()
  {
    ArrayList<Node> nodes = new ArrayList(this.m_nodeReader.getArray());
    return nodes;
  }
  
  public ArrayList<Vehicle> getVehicles()
  {
    return this.m_vehicleReader.getVehicles();
  }

  public Node getNodeByID(int id)
  {
    return this.m_nodeReader.getNodeByID(id);
  }
  
  public void markNodeAsClustered(int nodeId, int seedId)
  {
    for (int i = 0; i < this.m_nodeReader.getArray().size(); i++) {
      if (((Node)this.m_nodeReader.getArray().get(i)).getId() == nodeId) {
        ((Node)this.m_nodeReader.getArray().get(i)).setSeedId(seedId);
        break;
      }
    }
  }
  
  public void markNodeAsRouted(int nodeId)
  {
    this.m_nodeReader.getNodeByID(nodeId).setRouted(true);
  }
  
  private void buildTimeMatrixFile(double avgSpeed, MatrixReader dists, String timeFilePath)
  {
    try
    {
      BufferedWriter bw = new BufferedWriter(new FileWriter(timeFilePath));
      
      for (int i = 0; i < dists.getRowCount(); i++) {
        String line = "";
        for (int j = 0; j < dists.getColumnCount(); j++)
        {
          double dist = dists.at(i, j).doubleValue();
          double t = 60.0D * dist / avgSpeed;
          line = line + t;
          if (j < dists.getColumnCount() - 1) {
            line = line + ", ";
          } else
            line = line + ";";
        }
        line = line + NL;
        
        bw.write(line);
      }
      
      bw.close();
    }
    catch (IOException ioe) {
      ioe.printStackTrace();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void whereWeAre()
  {
    System.out.println("Sono in InputManager.whereWeAre()");
    File currDir = new File(".");
    System.out.println("current dir = " + currDir.getAbsolutePath());
    

    URL url = getClass().getResource("/");
    System.out.println("URL. file=" + url.getFile());
    File rootDir = new File(url.getFile());
    System.out.println("rootDir = " + rootDir.getAbsolutePath());
    

    URL urlLoader = getClass().getClassLoader().getResource("");
    File loadDir = new File(urlLoader.getPath());
    System.out.println("loadDir = " + loadDir.getAbsolutePath());
    
    URL url2 = getClass().getResource("/config");
    
    File confDir = new File(url2.getPath());
    System.out.println("configDir = " + confDir.getAbsolutePath());
    
    URL confFileUrl = getClass().getResource("/config/config.properties");
    
    try
    {
      InputStream is = confFileUrl.openStream();
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String str = null;
      while ((str = br.readLine()) != null) {
        System.out.println("conf: " + str);
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}