/*    */ package it.cyberdyne.dss.routing.io;
/*    */ 
/*    */ import it.cyberdyne.dss.routing.engine.Cluster;
/*    */ import it.cyberdyne.dss.routing.io.writer.TourWriter;
/*    */ import it.cyberdyne.dss.routing.utils.Configuration;
/*    */ import it.cyberdyne.dss.routing.utils.Constants;
/*    */ import java.io.File;
/*    */ import java.io.FileNotFoundException;
/*    */ import java.io.FileOutputStream;
/*    */ import java.io.IOException;
/*    */ import java.io.InputStream;
/*    */ import java.net.URL;
/*    */ import java.util.ArrayList;
/*    */ 
/*    */ public class OutputManager
/*    */ {
/* 25 */   public static final String OUTPUT_DIR = Constants.OUTPUT_DIR;
/*    */   public static final String FILE_BASENAME_TOURS = "tours_";
/*    */   public static final String FILE_EXT_TOUR = ".xml";
/*    */   private ArrayList<Cluster> m_clusters;
/*    */   private int m_callId;
/*    */   private String m_outputDirPath;
/*    */ 
/*    */   public OutputManager(ArrayList<Cluster> clusters, int call_id)
/*    */   {
/* 42 */     this.m_callId = call_id;
/* 43 */     this.m_clusters = clusters;
/* 44 */     this.m_outputDirPath = OUTPUT_DIR;
/*    */   }
/*    */ 
/*    */   public void writeFiles()
/*    */     throws FileNotFoundException, IOException
/*    */   {
/*    */     try
/*    */     {
/* 57 */       String toursFilePath = this.m_outputDirPath + File.separator + "tours_" + this.m_callId + ".xml";
/*    */ 
/* 59 */       FileOutputStream toursFOS = new FileOutputStream(new File(toursFilePath));
/*    */ 
/* 63 */       URL styleUrl = getClass().getResource(Configuration.getXmlStylePath());
/*    */ 
/* 65 */       InputStream styleStream = styleUrl.openStream();
/*    */ 
/* 68 */       TourWriter tourWr = new TourWriter();
/* 69 */       tourWr.write(toursFOS, styleStream, this.m_clusters, this.m_callId);
/* 70 */       toursFOS.close();
/* 71 */       styleStream.close();
/*    */     }
/*    */     catch (FileNotFoundException fnfe) {
/* 74 */       throw fnfe;
/*    */     }
/*    */     catch (IOException ioe) {
/* 77 */       throw ioe;
/*    */     }
/*    */   }
/*    */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.io.OutputManager
 * JD-Core Version:    0.6.2
 */