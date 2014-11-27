/*    */ package it.cyberdyne.dss.routing.model;
/*    */ 
/*    */ import it.cyberdyne.dss.routing.utils.Constants;
/*    */ 
/*    */ public class Vehicle
/*    */ {
/*    */   private VehicleType m_type;
/*    */   private int m_depId;
/*    */   private boolean m_assigned;
/*    */ 
/*    */   public Vehicle()
/*    */   {
/* 23 */     this.m_type = null;
/* 24 */     this.m_depId = Constants.DEP_ID;
/*    */   }
/*    */ 
/*    */   public Vehicle(VehicleType type)
/*    */   {
/* 32 */     this();
/* 33 */     this.m_type = type;
/* 34 */     this.m_depId = type.getDepId();
/*    */   }
/*    */ 
/*    */   public void setAssigned(boolean b)
/*    */   {
/* 41 */     this.m_assigned = b;
/*    */   }
/*    */ 
/*    */   public boolean isAssigned()
/*    */   {
/* 47 */     return this.m_assigned;
/*    */   }
/*    */ 
/*    */   public String getCode()
/*    */   {
/* 56 */     return this.m_type.getCode();
/*    */   }
/*    */ 
/*    */   public String getModel()
/*    */   {
/* 61 */     return this.m_type.getModel();
/*    */   }
/*    */ 
/*    */   public double getMaxLoad()
/*    */   {
/* 66 */     return this.m_type.getMaxLoad();
/*    */   }
/*    */ 
/*    */   public double getMaxLoad(float percent)
/*    */   {
/* 73 */     return this.m_type.getMaxLoad() * (percent / 100.0F);
/*    */   }
/*    */ 
/*    */   public double getMaxDistance()
/*    */   {
/* 78 */     return this.m_type.getMaxDistance();
/*    */   }
/*    */ 
/*    */   public double getMaxTime()
/*    */   {
/* 83 */     return this.m_type.getMaxTime();
/*    */   }
/*    */ 
/*    */   public String getStartHour()
/*    */   {
/* 88 */     return this.m_type.getStartHour();
/*    */   }
/*    */ 
/*    */   public Float getSrvTimeCoeff()
/*    */   {
/* 99 */     return this.m_type.getSrvTimeCoeff();
/*    */   }
/*    */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.poliba.lca.routing.model.Vehicle
 * JD-Core Version:    0.6.2
 */