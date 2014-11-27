/*     */ package it.cyberdyne.dss.routing.utils;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.IOException;
/*     */ import java.io.PrintStream;
/*     */ import java.net.URL;
/*     */ import java.util.Properties;
/*     */ 
/*     */ public class Configuration
/*     */ {
/*  30 */   private static File s_extConfFile = null;
/*     */ 
/*  32 */   private static String s_inputDir = null;
/*  33 */   private static String s_outputDir = null;
/*  34 */   private static String s_workDir = null;
/*  35 */   private static String s_logDir = null;
/*     */ 
/*  37 */   private static Integer s_depotId = null;
/*  38 */   private static Double s_avgSpeed = null;
/*     */ 
/*  40 */   private static String s_xmlStylePath = null;
/*  41 */   private static String s_xmlSchemaForNodes = null;
/*  42 */   private static String s_xmlSchemaForVehicles = null;
/*     */ 
/*  45 */   private static Float s_serviceTime = Float.valueOf(0.0F);
/*  46 */   private static Float s_serviceTimeCoeff = Float.valueOf(0.0F);
/*     */ 
/*     */   public static void loadConfig()
/*     */   {
/*  51 */     boolean result = loadInternalConfig();
/*  52 */     if (result)
/*  53 */       loadExternalConfig();
/*     */   }
/*     */ 
/*     */   private static boolean loadInternalConfig()
/*     */   {
/*  63 */     URL confUrl = Configuration.class.getResource("/config/config.properties");
/*     */     try
/*     */     {
/*  67 */       Properties props = new Properties();
/*  68 */       props.load(confUrl.openStream());
/*     */ 
/*  70 */       s_extConfFile = searchExtConfigFile(props);
/*  71 */       if (s_extConfFile == null) {
/*  72 */         String err = "ATTENZIONE: file di configurazione esterno non trovato o non definito!";
/*     */ 
/*  74 */         System.err.println(err);
/*  75 */         throw new RuntimeException(err);
/*     */       }
/*     */ 
/*  79 */       s_depotId = Integer.valueOf(Integer.parseInt(props.getProperty("depot.node_id")));
/*     */ 
/*  81 */       s_xmlStylePath = props.getProperty("xmlstyle.tours");
/*  82 */       s_xmlSchemaForNodes = props.getProperty("xmlschema.nodes");
/*  83 */       s_xmlSchemaForVehicles = props.getProperty("xmlschema.vehicle_types");
/*     */     }
/*     */     catch (IOException ioe) {
/*  86 */       ioe.printStackTrace();
/*     */     }
/*     */     catch (NumberFormatException nfe) {
/*  89 */       nfe.printStackTrace();
/*     */     }
/*     */ 
/*  92 */     if (s_extConfFile != null) {
/*  93 */       return true;
/*     */     }
/*  95 */     return false;
/*     */   }
/*     */ 
/*     */   private static File searchExtConfigFile(Properties pr)
/*     */   {
/* 111 */     File f = null;
/* 112 */     String fname = pr.getProperty("ext_config.filename");
/* 113 */     if ((fname != null) && (!fname.isEmpty())) {
/* 114 */       int k = 0;
/* 115 */       boolean again = true;
/*     */       do {
/* 117 */         String dirPath = pr.getProperty("ext_config.dir." + ++k);
/* 118 */         if ((dirPath != null) && (!dirPath.trim().isEmpty()))
/*     */         {
/* 120 */           File dir = new File(dirPath);
/* 121 */           if (dir.exists()) {
/* 122 */             File extPropFile = new File(dir, fname);
/* 123 */             if (extPropFile.exists()) {
/* 124 */               f = extPropFile;
/* 125 */               again = false;
/*     */             }
/*     */           }
/*     */         }
/*     */         else {
/* 130 */           again = false;
/*     */         }
/*     */       }
/* 133 */       while (again);
/*     */     }
/* 135 */     return f;
/*     */   }
/*     */ 
/*     */   private static boolean loadExternalConfig()
/*     */   {
/* 140 */     boolean result = true;
/*     */     try {
/* 142 */       Properties props = new Properties();
/* 143 */       props.load(new FileReader(s_extConfFile));
/*     */ 
/* 145 */       s_inputDir = props.getProperty("dir.input");
/* 146 */       s_outputDir = props.getProperty("dir.output");
/* 147 */       s_workDir = props.getProperty("dir.work");
/* 148 */       s_logDir = props.getProperty("dir.log");
/*     */ 
/* 150 */       s_avgSpeed = Double.valueOf(Double.parseDouble(props.getProperty("avg_speed")));
/*     */ 
/* 152 */       s_serviceTime = Float.valueOf(Float.parseFloat(props.getProperty("service_time.constant")));
/*     */ 
/* 154 */       s_serviceTimeCoeff = Float.valueOf(Float.parseFloat(props.getProperty("service_time.coeff")));
/*     */     }
/*     */     catch (IOException ioe)
/*     */     {
/* 159 */       ioe.printStackTrace();
/* 160 */       result = false;
/*     */     }
/* 162 */     return result;
/*     */   }
/*     */ 
/*     */   public static String getDefaultInputDir()
/*     */   {
/* 167 */     if (s_inputDir == null) {
/* 168 */       loadConfig();
/*     */     }
/* 170 */     return s_inputDir;
/*     */   }
/*     */ 
/*     */   public static String getOutputDir()
/*     */   {
/* 176 */     if (s_outputDir == null) {
/* 177 */       loadConfig();
/*     */     }
/* 179 */     return s_outputDir;
/*     */   }
/*     */ 
/*     */   public static String getWorkDir()
/*     */   {
/* 184 */     if (s_workDir == null) {
/* 185 */       loadConfig();
/*     */     }
/* 187 */     return s_workDir;
/*     */   }
/*     */ 
/*     */   public static String getLogDir()
/*     */   {
/* 192 */     if (s_logDir == null) {
/* 193 */       loadConfig();
/*     */     }
/* 195 */     return s_logDir;
/*     */   }
/*     */ 
/*     */   public static int getDepotId()
/*     */   {
/* 200 */     if (s_depotId == null)
/* 201 */       loadConfig();
/* 202 */     return s_depotId.intValue();
/*     */   }
/*     */ 
/*     */   public static double getAvgSpeed()
/*     */   {
/* 208 */     if (s_avgSpeed == null)
/* 209 */       loadConfig();
/* 210 */     return s_avgSpeed.doubleValue();
/*     */   }
/*     */ 
/*     */   public static String getXmlStylePath()
/*     */   {
/* 225 */     if (s_xmlStylePath == null)
/* 226 */       loadConfig();
/* 227 */     return s_xmlStylePath;
/*     */   }
/*     */ 
/*     */   public static String getXmlSchemaForNodes()
/*     */   {
/* 237 */     if (s_xmlSchemaForNodes == null)
/* 238 */       loadConfig();
/* 239 */     return s_xmlSchemaForNodes;
/*     */   }
/*     */ 
/*     */   public static String getXmlSchemaForVehicles()
/*     */   {
/* 248 */     if (s_xmlSchemaForVehicles == null)
/* 249 */       loadConfig();
/* 250 */     return s_xmlSchemaForVehicles;
/*     */   }
/*     */ 
/*     */   public static Float getServiceTimeConstant()
/*     */   {
/* 256 */     if (s_serviceTime == null)
/* 257 */       loadConfig();
/* 258 */     return s_serviceTime;
/*     */   }
/*     */ 
/*     */   public static Float getServiceTimeCoeff()
/*     */   {
/* 265 */     if (s_serviceTimeCoeff == null)
/* 266 */       loadConfig();
/* 267 */     return s_serviceTimeCoeff;
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.utils.Configuration
 * JD-Core Version:    0.6.2
 */