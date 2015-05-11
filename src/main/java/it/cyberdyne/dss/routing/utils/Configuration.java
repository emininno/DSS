 package it.cyberdyne.dss.routing.utils;
 
 import java.io.File;
 import java.io.FileReader;
 import java.io.IOException;
 import java.io.PrintStream;
 import java.net.URL;
 import java.util.Properties;
 
 public class Configuration
 {
   private static File s_extConfFile = null;
   
   private static String s_inputDir = null;
   private static String s_outputDir = null;
   private static String s_workDir = null;
   private static String s_logDir = null;
   
   private static Integer s_depotId = null;
   private static Double s_avgSpeed = null;
   
   private static String s_xmlStylePath = null;
   private static String s_xmlSchemaForNodes = null;
   private static String s_xmlSchemaForVehicles = null;
   private static Float s_serviceTime = Float.valueOf(0.0F);
   private static Float s_serviceTimeCoeff = Float.valueOf(0.0F);
   
 
   public static void loadConfig()
   {
     boolean result = loadInternalConfig();
     if (result) {
       loadExternalConfig();
     }
   }
  
   private static boolean loadInternalConfig()
   {
     URL confUrl = Configuration.class.getResource("/config/config.properties");
     
     try
     {
       Properties props = new Properties();
       props.load(confUrl.openStream());
       
       s_extConfFile = searchExtConfigFile(props);
       if (s_extConfFile == null) {
         String err = "ATTENZIONE: file di configurazione esterno non trovato o non definito!";
         
         System.err.println(err);
         throw new RuntimeException(err);
       }
       
 
       s_depotId = Integer.valueOf(Integer.parseInt(props.getProperty("depot.node_id")));
       
       s_xmlStylePath = props.getProperty("xmlstyle.tours");
       s_xmlSchemaForNodes = props.getProperty("xmlschema.nodes");
       s_xmlSchemaForVehicles = props.getProperty("xmlschema.vehicle_types");
     }
     catch (IOException ioe) {
       ioe.printStackTrace();
     }
     catch (NumberFormatException nfe) {
       nfe.printStackTrace();
     }
     
     if (s_extConfFile != null) {
       return true;
     }
     return false;
   }
   
   private static File searchExtConfigFile(Properties pr)
   {
     File f = null;
     String fname = pr.getProperty("ext_config.filename");
     if ((fname != null) && (!fname.isEmpty())) {
       int k = 0;
       boolean again = true;
       do {
         String dirPath = pr.getProperty("ext_config.dir." + ++k);
         if ((dirPath != null) && (!dirPath.trim().isEmpty()))
         {
           File dir = new File(dirPath);
           if (dir.exists()) {
             File extPropFile = new File(dir, fname);
             if (extPropFile.exists()) {
               f = extPropFile;
               again = false;
             }
           }
         }
         else {
           again = false;
         }
         
       } while (again);
     }
     return f;
   }
   
   private static boolean loadExternalConfig()
   {
     boolean result = true;
     try {
       Properties props = new Properties();
       props.load(new FileReader(s_extConfFile));
       
       s_inputDir = props.getProperty("dir.input");
       s_outputDir = props.getProperty("dir.output");
       s_workDir = props.getProperty("dir.work");
       s_logDir = props.getProperty("dir.log");
       
       s_avgSpeed = Double.valueOf(Double.parseDouble(props.getProperty("avg_speed")));
       
       s_serviceTime = Float.valueOf(Float.parseFloat(props.getProperty("service_time.constant")));
       
       s_serviceTimeCoeff = Float.valueOf(Float.parseFloat(props.getProperty("service_time.coeff")));
 
     }
     catch (IOException ioe)
     {
       ioe.printStackTrace();
       result = false;
     }
     return result;
   }
   
   public static String getDefaultInputDir()
   {
     if (s_inputDir == null) {
       loadConfig();
     }
     return s_inputDir;
   }
   
 
   public static String getOutputDir()
   {
     if (s_outputDir == null) {
       loadConfig();
     }
     return s_outputDir;
   }
   
   public static String getWorkDir()
   {
     if (s_workDir == null) {
       loadConfig();
     }
     return s_workDir;
   }
   
   public static String getLogDir()
   {
     if (s_logDir == null) {
       loadConfig();
     }
     return s_logDir;
   }
   
   public static int getDepotId()
   {
     if (s_depotId == null)
       loadConfig();
     return s_depotId.intValue();
   }
    
   public static double getAvgSpeed()
   {
     if (s_avgSpeed == null)
       loadConfig();
     return s_avgSpeed.doubleValue();
   }

   public static String getXmlStylePath()
   {
     if (s_xmlStylePath == null)
       loadConfig();
     return s_xmlStylePath;
   }

   public static String getXmlSchemaForNodes()
   {
     if (s_xmlSchemaForNodes == null)
       loadConfig();
     return s_xmlSchemaForNodes;
   }
   
   public static String getXmlSchemaForVehicles()
   {
     if (s_xmlSchemaForVehicles == null)
       loadConfig();
     return s_xmlSchemaForVehicles;
   }
   
   public static Float getServiceTimeConstant()
   {
     if (s_serviceTime == null)
       loadConfig();
     return s_serviceTime;
   }

   public static Float getServiceTimeCoeff()
   {
     if (s_serviceTimeCoeff == null)
       loadConfig();
     return s_serviceTimeCoeff;
   }
 }