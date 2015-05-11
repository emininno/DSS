package it.cyberdyne.dss.routing;

import it.cyberdyne.dss.routing.engine.ClusterManager;
import it.cyberdyne.dss.routing.io.InputManager;
import it.cyberdyne.dss.routing.io.OutputManager;
import it.cyberdyne.dss.routing.io.writer.EndGarageWriter;
import it.cyberdyne.dss.routing.utils.Constants;
import it.cyberdyne.dss.routing.utils.DAO;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;


public class TourManager
{
  public static final int RESULT_OK = 0;
  public static final int RESULT_FAULT = -1;
  private static final int DEP_ID = Constants.DEP_ID;
  private static final String WORK_DIR = Constants.WORK_DIR;
  private int m_callId;
  
  public int start(int call_id, String inputDirPath)
  {
    this.m_callId = call_id;
    int esito = 0;
    try {
      InputManager iMan = new InputManager(call_id, inputDirPath);
      if (iMan.check()) {
        iMan.readFiles();
      } else {
        throw new RuntimeException("Errore: check dei file di input fallito!");
      }
      ClusterManager clusterMan = new ClusterManager(iMan);
      int ncl = clusterMan.process();
      System.out.println("Debug: trovati " + ncl + " cluster");
      if (ncl > 0)
      {
        String clustersFilePath = WORK_DIR + File.separator + "clusters_" + call_id + ".txt";
        PrintStream pw = new PrintStream(new FileOutputStream(clustersFilePath));
        
        clusterMan.printClusters(pw);
        pw.close();
        
        System.out.println("\nDEBUG: stampa dei routing:");
        clusterMan.printTours();
        String routesFilePath = WORK_DIR + File.separator + "routes_" + call_id + ".txt";
        
        pw = new PrintStream(new FileOutputStream(routesFilePath));
        clusterMan.printTours(pw);
        pw.close();
        
        OutputManager outMan = new OutputManager(clusterMan.getClusters(), call_id);
        outMan.writeFiles();
        
        EndGarageWriter garageWriter = new EndGarageWriter(clusterMan.getClusters(), iMan.getVehicles(), call_id);
        
        garageWriter.print();
      }
    }
    catch (Exception e) {
      System.err.println("Errore. Esecuzione di TourFinder abortita");
      e.printStackTrace();
      esito = -1;
    }
    
    return esito;
  }
  
  public int start(int call_id)
  {
    return start(call_id, InputManager.INPUT_DIR);
  }
  
  public int getCurrentCallId()
  {
    return this.m_callId;
  }

  private void test_db(Double[][] matrix)
  {
    DAO.matrixToLinear(matrix, WORK_DIR, "lin_distMatrix_ncs", this.m_callId, false);
    DAO.matrixToLinear(matrix, WORK_DIR, "lin_distMatrix", this.m_callId, true);
    

    double[][] m = { { 11.0D, 12.0D, 13.0D, 14.0D }, { 21.0D, 22.0D, 23.0D, 24.0D }, { 31.0D, 32.0D, 33.0D, 34.0D } };
    
    Double[][] dm = new Double[m.length][m[0].length];
    for (int i = 0; i < m.length; i++) {
      for (int j = 0; j < m[i].length; j++)
        dm[i][j] = Double.valueOf(m[i][j]);
    }
    DAO.matrixToLinear(dm, WORK_DIR, "lin_testMatrix", 999, true);
    
    DAO.linearToMatrix(WORK_DIR, "lin_distMatrix_ncs", this.m_callId);
    DAO.linearToMatrix(WORK_DIR, "lin_distMatrix", this.m_callId);
  }
}