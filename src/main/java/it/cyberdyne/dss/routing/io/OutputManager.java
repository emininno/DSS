package it.cyberdyne.dss.routing.io;

import it.cyberdyne.dss.routing.engine.Cluster;
import it.cyberdyne.dss.routing.io.writer.TourWriter;
import it.cyberdyne.dss.routing.utils.Configuration;
import it.cyberdyne.dss.routing.utils.Constants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;









public class OutputManager
{
  public static final String OUTPUT_DIR = Constants.OUTPUT_DIR;
  

  public static final String FILE_BASENAME_TOURS = "tours_";
  

  public static final String FILE_EXT_TOUR = ".xml";
  
  private ArrayList<Cluster> m_clusters;
  
  private int m_callId;
  
  private String m_outputDirPath;
  

  public OutputManager(ArrayList<Cluster> clusters, int call_id)
  {
    this.m_callId = call_id;
    this.m_clusters = clusters;
    this.m_outputDirPath = OUTPUT_DIR;
  }
  





  public void writeFiles()
    throws FileNotFoundException, IOException
  {
    try
    {
      String toursFilePath = this.m_outputDirPath + File.separator + "tours_" + this.m_callId + ".xml";
      
      FileOutputStream toursFOS = new FileOutputStream(new File(toursFilePath));
      


      URL styleUrl = getClass().getResource(Configuration.getXmlStylePath());
      
      InputStream styleStream = styleUrl.openStream();
      

      TourWriter tourWr = new TourWriter();
      tourWr.write(toursFOS, styleStream, this.m_clusters, this.m_callId);
      toursFOS.close();
      styleStream.close();
    }
    catch (FileNotFoundException fnfe) {
      throw fnfe;
    }
    catch (IOException ioe) {
      throw ioe;
    }
  }
}