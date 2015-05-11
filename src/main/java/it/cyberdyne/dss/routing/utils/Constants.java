package it.cyberdyne.dss.routing.utils;

public class Constants
{
  public static final long INFINITY = 9999999999L;
  public static final String XML_INFINITY_STR = "INF";
  public static final double ACCURACY = 1.0E-6D;
  public static final String NL = System.getProperty("line.separator");
  public static final String INPUT_DIR = ".";//Configuration.getDefaultInputDir();
  public static final String OUTPUT_DIR = ".";//Configuration.getOutputDir();
  public static final String WORK_DIR = ".";//Configuration.getWorkDir();
  public static final String LOG_DIR = ".";//Configuration.getLogDir();
  public static final int DEP_ID = 0;//Configuration.getDepotId();
  public static final double AVG_SPEED = 70.0;
  public static final String XML_SCHEMA_NODES = ".";//Configuration.getXmlSchemaForNodes();
  public static final String XML_SCHEMA_VEHICLES = ".";//Configuration.getXmlSchemaForVehicles();
  public static final String DEFAULT_START_HOUR = "0:00";

  public static final Float SERVICE_TIME_CONSTANT = (float) 15;
  public static final Float SERVICE_TIME_COEFF = (float) 0;
}
