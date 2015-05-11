package it.cyberdyne.dss.routing.model;

import it.cyberdyne.dss.routing.utils.Constants;
import it.cyberdyne.dss.routing.utils.Utilities;

public class VehicleType
  implements Comparable
{
  private String m_code;
  private String m_model;
  private int m_depId;
  private double m_maxLoad;
  private double m_maxDistance;
  private double m_maxTime;
  private int m_totQty;
  private String m_startHour;
  private Float m_srvTimeCoeff;
  private int m_inUse;
  private Float ms_feedSessionDuration = null;
  private String ms_feedSessionStart = null;
  private boolean ms_uniformedStart = false;
  private boolean ms_sessionConstraintActive = false;
  
  private float m_timeLimitToLast = -1.0F;
  
  public VehicleType() {}
  
  public VehicleType(int dep_id, String code, String model, int qty, double max_load, double max_dist, double max_time, String start_hour, Float srvTimeCoeff)
  {
    this.m_depId = dep_id;
    this.m_code = code;
    this.m_model = model;
    this.m_totQty = qty;
    this.m_maxLoad = max_load;
    this.m_maxDistance = max_dist;
    this.m_maxTime = max_time;
    this.m_startHour = start_hour;
    this.m_srvTimeCoeff = srvTimeCoeff;
  }
  
  public VehicleType(int dep_id, String code, String model, int qty, double max_load, double max_dist, double max_time, String start_hour)
  {
    this(dep_id, code, model, qty, max_load, max_dist, max_time, start_hour, (float)Constants.SERVICE_TIME_COEFF);
  }
  
  public void setFeedSessionAttributes(Float duration, String startH, boolean uniformedStart)
  {
    ms_feedSessionDuration = duration;
    ms_feedSessionStart = startH;
    ms_uniformedStart = uniformedStart;
    

    if ((duration != null) && (startH != null)) {
      ms_sessionConstraintActive = true;
    }
  }
  
  public float getTimeLimitToLast()
  {
    if (this.m_timeLimitToLast < 0.0F) {
      float time = 1.0E10F;
      if (ms_sessionConstraintActive) {
        if (ms_uniformedStart) {
          time = ms_feedSessionDuration.floatValue();
        } else {
          time = ms_feedSessionDuration.floatValue() - Utilities.hourDiff(this.m_startHour, ms_feedSessionStart);
        }
      }
      
      this.m_timeLimitToLast = time;
    }
    return this.m_timeLimitToLast;
  }
  

  public String toString()
  {
    return "VehicleType [" + this.m_code + "] " + this.m_totQty;
  }
  

  public int getDepId()
  {
    return this.m_depId;
  }
  
  public String getCode() {
    return this.m_code;
  }
  
  public String getModel() {
    return this.m_model;
  }
  
  public int getTotQty() {
    return this.m_totQty;
  }
  
  public double getMaxLoad() {
    return this.m_maxLoad;
  }
  
  public double getMaxDistance() {
    return this.m_maxDistance;
  }
  
  public double getMaxTime() {
    return this.m_maxTime;
  }
  





  public String getStartHour()
  {
    if ((ms_sessionConstraintActive) && (ms_uniformedStart)) {
      this.m_startHour = ms_feedSessionStart;
    }
    return this.m_startHour;
  }
  
  public Float getSrvTimeCoeff()
  {
    return this.m_srvTimeCoeff;
  }
  
  public int compareTo(Object obj)
  {
    int result = 1;
    if ((obj != null) && ((obj instanceof VehicleType))) {
      VehicleType vt = (VehicleType)obj;
      
      if ((this.m_maxLoad > vt.m_maxLoad - 1.0E-5D) && (this.m_maxLoad < vt.m_maxLoad + 1.0E-5D))
      {

        if (getTimeLimitToLast() > vt.getTimeLimitToLast()) {
          result = 1;
        } else {
          result = -1;
        }
      } else if (this.m_maxLoad < vt.m_maxLoad) {
        result = -1;
      } else if (this.m_maxLoad > vt.m_maxLoad) {
        result = 1;
      }
    }
    return result;
  }
}