/*     */ package it.cyberdyne.dss.routing.utils;
/*     */ 
/*     */ import it.cyberdyne.dss.routing.engine.Cluster;
/*     */ import it.cyberdyne.dss.routing.io.InputManager;
/*     */ import it.cyberdyne.dss.routing.model.Node;
/*     */ import java.io.PrintStream;
/*     */ import java.text.DecimalFormat;
/*     */ import java.text.DecimalFormatSymbols;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Calendar;
/*     */ import java.util.Collections;
/*     */ import java.util.Locale;
/*     */ 
/*     */ public class Utilities
/*     */ {
/*  36 */   public static DecimalFormat s_3fract = new DecimalFormat();
/*     */ 
/*     */   public static String format3fract(double d)
/*     */   {
/*  40 */     return s_3fract.format(d);
/*     */   }
/*     */ 
/*     */   public static ArrayList<Node> getSortedNodes(int nodeId, InputManager iman)
/*     */   {
/*  51 */     Double[] dists = iman.getDistRow(nodeId);
/*  52 */     ArrayList nodes = iman.getNodes();
/*     */ 
/*  57 */     for (int i = 0; i < nodes.size(); i++) {
/*  58 */       ((Node)nodes.get(i)).setDistance(dists[i]);
/*     */     }
/*  60 */     Collections.sort(nodes);
/*  61 */     return nodes;
/*     */   }
/*     */ 
/*     */   public static void printArrayList(ArrayList objs, boolean inLine)
/*     */   {
/*  71 */     System.out.println("Debug printArrayList");
/*  72 */     if (inLine)
/*  73 */       System.out.println(objs);
/*     */     else
/*  75 */       printArrayList(objs);
/*     */   }
/*     */ 
/*     */   public static void printArrayList(ArrayList objs)
/*     */   {
/*  81 */     for (int i = 0; i < objs.size(); i++)
/*  82 */       System.out.println(objs.get(i));
/*     */   }
/*     */ 
/*     */   public static String getDateTime()
/*     */   {
/*  89 */     Calendar now = Calendar.getInstance(Locale.ITALIAN);
/*  90 */     int day = now.get(5);
/*  91 */     int month = now.get(2) + 1;
/*  92 */     int year = now.get(1);
/*  93 */     String date = twoDgtsFormat(day) + "/" + twoDgtsFormat(month) + "/" + year;
/*     */ 
/*  96 */     int hour = now.get(11);
/*  97 */     int min = now.get(12);
/*  98 */     int sec = now.get(13);
/*  99 */     String time = twoDgtsFormat(hour) + ":" + twoDgtsFormat(min) + ":" + twoDgtsFormat(sec);
/* 100 */     return date + " " + time;
/*     */   }
/*     */ 
/*     */   private static String twoDgtsFormat(int n) {
/* 104 */     if (n < 10)
/* 105 */       return "0" + n;
/* 106 */     return "" + n;
/*     */   }
/*     */ 
/*     */   public static void printTours(ArrayList<Cluster> clusters, PrintStream out)
/*     */   {
/* 115 */     if ((clusters != null) && (out != null))
/* 116 */       for (int i = 0; i < clusters.size(); i++) {
/* 117 */         Cluster cls = (Cluster)clusters.get(i);
/* 118 */         cls.printTour(out);
/* 119 */         out.println();
/*     */       }
/*     */   }
/*     */ 
/*     */   public static void printTours(ArrayList<Cluster> clusters)
/*     */   {
/* 128 */     printTours(clusters, System.out);
/*     */   }
/*     */ 
/*     */   public static String arrivalInstant(String startHour, double time)
/*     */   {
/* 140 */     String strTime = null;
/* 141 */     if (startHour != null)
/*     */     {
/* 143 */       String[] hhmi = startHour.split(":");
/* 144 */       if (hhmi.length >= 2) {
/* 145 */         int h = Integer.parseInt(hhmi[0]);
/* 146 */         int m = Integer.parseInt(hhmi[1]);
/* 147 */         Calendar cal = Calendar.getInstance();
/* 148 */         cal.set(11, h);
/* 149 */         cal.set(12, m + Math.round((float)time));
/* 150 */         cal.set(13, 0);
/*     */ 
/* 152 */         h = cal.get(11);
/* 153 */         m = cal.get(12);
/*     */ 
/* 155 */         strTime = twoDgtsFormat(h) + ":" + twoDgtsFormat(m);
/*     */       }
/*     */       else {
/* 158 */         System.err.println("Formato di startHour non corretto. Atteso hh:mi Non si procede al calcolo e si restituiesce null");
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 163 */       System.err.println("Orario iniziale nullo!.");
/*     */     }
/* 165 */     return strTime;
/*     */   }
/*     */ 
/*     */   public static int getDaysAfter(String startHour, double timeInMins)
/*     */   {
/* 179 */     int d = -1;
/* 180 */     if (startHour != null)
/*     */     {
/* 182 */       String[] hhmi = startHour.split(":");
/* 183 */       if (hhmi.length >= 2) {
/* 184 */         int h = Integer.parseInt(hhmi[0]);
/* 185 */         int m = Integer.parseInt(hhmi[1]);
/* 186 */         Calendar todayZero = Calendar.getInstance();
/* 187 */         todayZero.set(11, 0);
/* 188 */         todayZero.set(12, 0);
/* 189 */         todayZero.set(13, 0);
/* 190 */         todayZero.set(14, 0);
/*     */ 
/* 192 */         Calendar cal = Calendar.getInstance();
/* 193 */         cal.set(11, h);
/* 194 */         cal.set(12, m + Math.round((float)timeInMins));
/* 195 */         cal.set(13, 0);
/* 196 */         cal.set(14, 0);
/*     */ 
/* 201 */         long diff = cal.getTimeInMillis() - todayZero.getTimeInMillis();
/* 202 */         d = (int)diff / 86400000;
/*     */       }
/*     */       else
/*     */       {
/* 207 */         System.err.println("Formato di startHour non corretto. Atteso hh:mi Non si procede al calcolo e si restituiesce -1");
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/* 212 */       System.err.println("Orario iniziale nullo!.");
/*     */     }
/* 214 */     return d;
/*     */   }
/*     */ 
/*     */   public static String dateHourString(Calendar cal)
/*     */   {
/* 219 */     String str = cal.get(1) + "/" + twoDgtsFormat(cal.get(2) + 1) + "/" + twoDgtsFormat(cal.get(5)) + "_";
/*     */ 
/* 223 */     str = str + twoDgtsFormat(cal.get(11)) + ":" + twoDgtsFormat(cal.get(12)) + ":" + twoDgtsFormat(cal.get(13));
/*     */ 
/* 227 */     return str;
/*     */   }
/*     */ 
/*     */   public static boolean isInfinity(double d)
/*     */   {
/* 236 */     return doubleEqual(d, 9999999999.0D);
/*     */   }
/*     */ 
/*     */   public static boolean doubleEqual(double d1, double d2)
/*     */   {
/* 244 */     if (Math.abs(d2 - d1) < 1.0E-06D) {
/* 245 */       return true;
/*     */     }
/* 247 */     return false;
/*     */   }
/*     */ 
/*     */   public static boolean doubleGreaterEqual(double d1, double d2)
/*     */   {
/* 252 */     return (doubleEqual(d1, d2)) || (d1 > d2);
/*     */   }
/*     */ 
/*     */   public static boolean isZero(double d)
/*     */   {
/* 260 */     return doubleEqual(d, 0.0D);
/*     */   }
/*     */ 
/*     */   public static double round2dgt(double d)
/*     */   {
/* 265 */     double tmp = Math.round(d * 100.0D);
/* 266 */     return tmp / 100.0D;
/*     */   }
/*     */ 
/*     */   public static double quantizeLoad(double load, float granularity)
/*     */   {
/* 273 */     if (isZero(granularity)) {
/* 274 */       return load;
/*     */     }
/* 276 */     double quot = load / granularity;
/* 277 */     int slots = (int)Math.ceil(quot);
/* 278 */     return granularity * slots;
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  32 */     DecimalFormatSymbols symbols = new DecimalFormatSymbols();
/*  33 */     symbols.setDecimalSeparator('.');
/*     */ 
/*  35 */     String format = "#.###";
/*     */   }
/*     */ }

/* Location:           /home/ern/Desktop/installazione/lib/TourFinder_v1_2.jar
 * Qualified Name:     it.cyberdyne.dss.routing.utils.Utilities
 * JD-Core Version:    0.6.2
 */