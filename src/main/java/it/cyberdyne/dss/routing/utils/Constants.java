/*    */ package it.cyberdyne.dss.routing.utils;
/*    */ 
/*    */ public class Constants
/*    */ {
/*    */   public static final long INFINITY = 9999999999L;
/*    */   public static final String XML_INFINITY_STR = "INF";
/*    */   public static final double ACCURACY = 1.0E-06D;
/* 26 */   public static final String NL = System.getProperty("line.separator");
/*    */ 
/* 29 */   public static final String INPUT_DIR = Configuration.getDefaultInputDir();
/*    */ 
/* 32 */   public static final String OUTPUT_DIR = Configuration.getOutputDir();
/*    */ 
/* 35 */   public static final String WORK_DIR = Configuration.getWorkDir();
/*    */ 
/* 38 */   public static final String LOG_DIR = Configuration.getLogDir();
/*    */ 
/* 41 */   public static final int DEP_ID = Configuration.getDepotId();
/*    */ 
/* 44 */   public static final double AVG_SPEED = Configuration.getAvgSpeed();
/*    */ 
/* 48 */   public static final String XML_SCHEMA_NODES = Configuration.getXmlSchemaForNodes();
/*    */ 
/* 50 */   public static final String XML_SCHEMA_VEHICLES = Configuration.getXmlSchemaForVehicles();
/*    */   public static final String DEFAULT_START_HOUR = "0:00";
/* 60 */   public static final Float SERVICE_TIME_CONSTANT = Configuration.getServiceTimeConstant();
/* 61 */   public static final Float SERVICE_TIME_COEFF = Configuration.getServiceTimeCoeff();
/*    */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.poliba.lca.routing.utils.Constants
 * JD-Core Version:    0.6.2
 */