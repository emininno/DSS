/*     */ package it.cyberdyne.dss.routing.engine;
/*     */ 
/*     */ import it.cyberdyne.dss.routing.io.InputManager;
/*     */ import it.cyberdyne.dss.routing.model.Node;
/*     */ import it.cyberdyne.dss.routing.utils.Constants;
/*     */ import java.io.PrintStream;
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ public class Routing
/*     */ {
/*     */   private Cluster m_cluster;
/*     */   private InputManager m_iMan;
/*     */   private Node m_dep;
/*     */ 
/*     */   public Routing(Cluster clst, Node dep, InputManager iMan) throws CloneNotSupportedException
/*     */   {
/*  33 */     this.m_cluster = clst;
/*  34 */     this.m_iMan = iMan;
/*     */ 
/*  36 */     this.m_dep = ((Node)dep.clone());
/*     */   }
/*     */ 
/*     */   public Routing(Cluster clst, InputManager iMan) throws CloneNotSupportedException {
/*  40 */     this(clst, iMan.getNodeByID(Constants.DEP_ID), iMan);
/*     */   }
/*     */ 
/*     */   public void process()
/*     */     throws Exception
/*     */   {
/*  55 */     if ((this.m_cluster != null) && (this.m_dep != null))
/*     */     {
/*  57 */       Node seed = this.m_cluster.getSeed();
/*  58 */       ArrayList route = new ArrayList();
/*  59 */       seed.setRouted(true);
/*  60 */       this.m_dep.setRouted(true);
/*  61 */       route.add(this.m_dep);
/*  62 */       route.add(seed);
/*     */ 
/*  66 */       route.add((Node)this.m_dep.clone());
/*     */ 
/*  70 */       for (int i = 1; i < this.m_cluster.getNodes().size(); i++) {
/*  71 */         ((Node)this.m_cluster.getNodes().get(i)).setRouted(false);
/*     */       }
/*     */ 
/*  84 */       int toInsNodeId = 0;
/*  85 */       int k = 0;
/*  86 */       while ((toInsNodeId != -1) && (k++ < this.m_cluster.getNodes().size())) {
/*  87 */         toInsNodeId = whichNodeInsert();
/*     */ 
/*  91 */         if (toInsNodeId != -1) {
/*  92 */           whereInsert(toInsNodeId, route);
/*  93 */           markNode(toInsNodeId);
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/*  98 */       Float srvTimeCoeff = this.m_cluster.getVehicle().getSrvTimeCoeff();
/*  99 */       this.m_cluster.setTour(route, pathLength(route), pathDuration(route, true, srvTimeCoeff));
/*     */ 
/* 102 */       setArrivalInstants(route, true, srvTimeCoeff);
/*     */     }
/*     */   }
/*     */ 
/*     */   private int whichNodeInsert()
/*     */     throws Exception
/*     */   {
/* 117 */     int id = -1;
/* 118 */     ArrayList nodes = this.m_cluster.getNodes();
/* 119 */     double maxDistance = 0.0D;
/*     */ 
/* 122 */     for (int i = 0; i < nodes.size(); i++) {
/* 123 */       Node nSrc = (Node)nodes.get(i);
/* 124 */       if (nSrc.isRouted())
/*     */       {
/* 127 */         for (int j = 0; j < nodes.size(); j++) {
/* 128 */           Node nDst = (Node)nodes.get(j);
/* 129 */           if (!nDst.isRouted())
/*     */           {
/* 132 */             double dst = this.m_iMan.getDistance(nSrc.getId(), nDst.getId());
/*     */ 
/* 135 */             if (dst > maxDistance) {
/* 136 */               id = nDst.getId();
/* 137 */               maxDistance = dst;
/*     */             }
/*     */           }
/*     */         }
/*     */       }
/*     */     }
/*     */ 
/* 144 */     return id;
/*     */   }
/*     */ 
/*     */   private void whereInsert(int nodeId, ArrayList<Node> route)
/*     */   {
/* 154 */     int k = getNodePosition(this.m_cluster.getNodes(), nodeId);
/* 155 */     Node n = (Node)this.m_cluster.getNodes().get(k);
/*     */ 
/* 158 */     double origLength = pathLength(route);
/* 159 */     int origSize = route.size();
/*     */ 
/* 161 */     int pos = 1;
/*     */ 
/* 165 */     if (origSize > 1) {
/* 166 */       double minLength = 9999999999.0D;
/* 167 */       for (int i = 1; i < origSize; i++)
/*     */       {
/* 169 */         ArrayList attempt = new ArrayList(route);
/* 170 */         attempt.add(i, n);
/* 171 */         double length = pathLength(attempt);
/* 172 */         if (length < minLength) {
/* 173 */           minLength = length;
/* 174 */           pos = i;
/*     */         }
/*     */ 
/*     */       }
/*     */ 
/* 179 */       route.add(pos, n);
/* 180 */       n.setRouted(true);
/*     */     }
/*     */     else
/*     */     {
/* 185 */       System.out.println("DEBUG: path non inizializzato.");
/*     */     }
/*     */   }
/*     */ 
/*     */   private void markNode(int nodeId)
/*     */   {
/* 193 */     this.m_iMan.markNodeAsRouted(nodeId);
/*     */   }
/*     */ 
/*     */   private int getNodePosition(ArrayList<Node> nodes, int nodeId)
/*     */   {
/* 202 */     int pos = -1;
/* 203 */     if (nodeId > 0) {
/* 204 */       for (int i = 0; i < nodes.size(); i++) {
/* 205 */         if (((Node)nodes.get(i)).getId() == nodeId) {
/* 206 */           pos = i;
/* 207 */           break;
/*     */         }
/*     */       }
/*     */     }
/* 211 */     return pos;
/*     */   }
/*     */ 
/*     */   private double pathLength(ArrayList<Node> route)
/*     */   {
/* 218 */     double len = 0.0D;
/* 219 */     for (int i = 0; i < route.size() - 1; i++) {
/*     */       try
/*     */       {
/* 222 */         Node src = (Node)route.get(i);
/* 223 */         Node dst = (Node)route.get(i + 1);
/* 224 */         len += this.m_iMan.getDistance(src.getId(), dst.getId());
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/*     */     }
/*     */ 
/* 231 */     return len;
/*     */   }
/*     */ 
/*     */   private double pathDuration(ArrayList<Node> route, boolean withService, Float srvTimeCoeff)
/*     */   {
/* 246 */     double d = 0.0D;
/* 247 */     if ((withService) && (route.size() > 0)) {
/* 248 */       d += ((Node)route.get(0)).getServiceTime(srvTimeCoeff);
/*     */     }
/* 250 */     for (int i = 0; i < route.size() - 1; i++)
/*     */       try
/*     */       {
/* 253 */         Node src = (Node)route.get(i);
/* 254 */         Node dst = (Node)route.get(i + 1);
/* 255 */         d += this.m_iMan.getTime(src.getId(), dst.getId());
/*     */ 
/* 257 */         if (withService)
/* 258 */           d += dst.getServiceTime(srvTimeCoeff);
/*     */       }
/*     */       catch (Exception e) {
/*     */       }
/* 262 */     return d;
/*     */   }
/*     */ 
/*     */   private void setArrivalInstants(ArrayList<Node> route, boolean withService, Float srvTimeCoeff)
/*     */   {
/* 278 */     double t = 0.0D;
/* 279 */     if (route.size() > 0)
/* 280 */       ((Node)route.get(0)).setArrivalTime(t);
/* 281 */     for (int i = 0; i < route.size() - 1; i++)
/*     */       try {
/* 283 */         Node src = (Node)route.get(i);
/* 284 */         Node dst = (Node)route.get(i + 1);
/*     */ 
/* 286 */         if (withService) {
/* 287 */           double srcSrvTime = src.getServiceTime(srvTimeCoeff);
/* 288 */           t += srcSrvTime;
/*     */         }
/*     */ 
/* 291 */         double srcToDstTime = this.m_iMan.getTime(src.getId(), dst.getId());
/* 292 */         t += srcToDstTime;
/* 293 */         dst.setArrivalTime(t);
/*     */       }
/*     */       catch (Exception e) {
/* 296 */         System.err.println("Warning. Out of bound in setArrivalInstants: " + e);
/*     */       }
/*     */   }
/*     */ 
/*     */   public void print()
/*     */   {
/* 304 */     print(System.out);
/*     */   }
/*     */ 
/*     */   public void print(PrintStream ps)
/*     */   {
/* 310 */     this.m_cluster.printTour(ps);
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.engine.Routing
 * JD-Core Version:    0.6.2
 */