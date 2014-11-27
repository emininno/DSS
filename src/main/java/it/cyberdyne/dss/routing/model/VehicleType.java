/*     */ package it.cyberdyne.dss.routing.model;
/*     */ 
/*     */ import it.cyberdyne.dss.routing.utils.Constants;
/*     */ 
/*     */ public class VehicleType
/*     */   implements Comparable
/*     */ {
/*     */   private String m_code;
/*     */   private String m_model;
/*     */   private int m_depId;
/*     */   private double m_maxLoad;
/*     */   private double m_maxDistance;
/*     */   private double m_maxTime;
/*     */   private int m_totQty;
/*     */   private String m_startHour;
/*     */   private Float m_srvTimeCoeff;
/*     */   private int m_inUse;
/*     */ 
/*     */   public VehicleType()
/*     */   {
/*     */   }
/*     */ 
/*     */   public VehicleType(int dep_id, String code, String model, int qty, double max_load, double max_dist, double max_time, String start_hour, Float srvTimeCoeff)
/*     */   {
/*  55 */     this.m_depId = dep_id;
/*  56 */     this.m_code = code;
/*  57 */     this.m_model = model;
/*  58 */     this.m_totQty = qty;
/*  59 */     this.m_maxLoad = max_load;
/*  60 */     this.m_maxDistance = max_dist;
/*  61 */     this.m_maxTime = max_time;
/*  62 */     this.m_startHour = start_hour;
/*  63 */     this.m_srvTimeCoeff = srvTimeCoeff;
/*     */   }
/*     */ 
/*     */   public VehicleType(int dep_id, String code, String model, int qty, double max_load, double max_dist, double max_time, String start_hour)
/*     */   {
/*  75 */     this(dep_id, code, model, qty, max_load, max_dist, max_time, start_hour, Constants.SERVICE_TIME_COEFF);
/*     */   }
/*     */ 
/*     */   public String toString()
/*     */   {
/*  88 */     return "VehicleType [" + this.m_code + "] " + this.m_totQty;
/*     */   }
/*     */ 
/*     */   public int getDepId()
/*     */   {
/*  94 */     return this.m_depId;
/*     */   }
/*     */ 
/*     */   public String getCode() {
/*  98 */     return this.m_code;
/*     */   }
/*     */ 
/*     */   public String getModel() {
/* 102 */     return this.m_model;
/*     */   }
/*     */ 
/*     */   public int getTotQty() {
/* 106 */     return this.m_totQty;
/*     */   }
/*     */ 
/*     */   public double getMaxLoad() {
/* 110 */     return this.m_maxLoad;
/*     */   }
/*     */ 
/*     */   public double getMaxDistance() {
/* 114 */     return this.m_maxDistance;
/*     */   }
/*     */ 
/*     */   public double getMaxTime() {
/* 118 */     return this.m_maxTime;
/*     */   }
/*     */ 
/*     */   public String getStartHour()
/*     */   {
/* 123 */     return this.m_startHour;
/*     */   }
/*     */ 
/*     */   public Float getSrvTimeCoeff()
/*     */   {
/* 133 */     return this.m_srvTimeCoeff;
/*     */   }
/*     */ 
/*     */   public int compareTo(Object obj)
/*     */   {
/* 144 */     int result = 1;
/* 145 */     if ((obj != null) && ((obj instanceof VehicleType))) {
/* 146 */       VehicleType vt = (VehicleType)obj;
/* 147 */       if (this.m_maxLoad < vt.m_maxLoad)
/* 148 */         result = -1;
/* 149 */       else if (this.m_maxLoad > vt.m_maxLoad)
/* 150 */         result = 1;
/* 151 */       else if ((this.m_maxLoad > vt.m_maxLoad - 1.E-05D) && (this.m_maxLoad < vt.m_maxLoad + 1.E-05D))
/*     */       {
/* 153 */         result = 0;
/*     */       }
/*     */     }
/* 155 */     return result;
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.model.VehicleType
 * JD-Core Version:    0.6.2
 */