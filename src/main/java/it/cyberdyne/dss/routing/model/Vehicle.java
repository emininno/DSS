package it.cyberdyne.dss.routing.model;

import it.cyberdyne.dss.routing.utils.Constants;

public class Vehicle
{
  private VehicleType m_type;
  private int m_depId;
  private boolean m_assigned;
  
  public Vehicle()
  {
    this.m_type = null;
    this.m_depId = Constants.DEP_ID;
  }

  public Vehicle(VehicleType type)
  {
    this();
    this.m_type = type;
    this.m_depId = type.getDepId();
  }

  public void setAssigned(boolean b)
  {
    this.m_assigned = b;
  }
  

  public boolean isAssigned()
  {
    return this.m_assigned;
  }

  public String getCode()
  {
    return this.m_type.getCode();
  }
  
  public String getModel()
  {
    return this.m_type.getModel();
  }
  
  public double getMaxLoad()
  {
    return this.m_type.getMaxLoad();
  }

  public double getMaxLoad(float percent)
  {
    return this.m_type.getMaxLoad() * (percent / 100.0F);
  }
  
  public double getMaxDistance()
  {
    return this.m_type.getMaxDistance();
  }
  
  public double getMaxTime()
  {
    return this.m_type.getMaxTime();
  }
  
  public String getStartHour()
  {
    return this.m_type.getStartHour();
  }

  public Float getSrvTimeCoeff()
  {
    return this.m_type.getSrvTimeCoeff();
  }

  public float getTimeLimitToLast()
  {
    return this.m_type.getTimeLimitToLast();
  }
}
