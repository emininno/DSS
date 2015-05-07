package it.cyberdyne.dss.routing.utils;

import it.cyberdyne.dss.routing.engine.Cluster;
import it.cyberdyne.dss.routing.io.InputManager;
import it.cyberdyne.dss.routing.model.Node;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class Utilities {

    public static DecimalFormat s_3fract = new DecimalFormat();

    public static String format3fract(double d) {
        return s_3fract.format(d);
    }

    public static ArrayList<Node> getSortedNodes(int nodeId, InputManager iman) {
        Double[] dists = iman.getDistRow(nodeId);
        
        ArrayList nodes = iman.getNodes();
        System.out.println("Dists size="+dists.length);
        for (int i = 0; i < nodes.size(); i++) {
            
            ((Node) nodes.get(i)).setDistance(dists[i]);
        }
        Collections.sort(nodes);
        return nodes;
    }

    public static void printArrayList(ArrayList objs, boolean inLine) {
        System.out.println("Debug printArrayList");
        if (inLine) {
            System.out.println(objs);
        } else {
            printArrayList(objs);
        }
    }

    public static void printArrayList(ArrayList objs) {
        for (int i = 0; i < objs.size(); i++) {
            System.out.println(objs.get(i));
        }
    }

    public static String getDateTime() {
        Calendar now = Calendar.getInstance(Locale.ITALIAN);
        int day = now.get(5);
        int month = now.get(2) + 1;
        int year = now.get(1);
        String date = twoDgtsFormat(day) + "/" + twoDgtsFormat(month) + "/" + year;

        int hour = now.get(11);
        int min = now.get(12);
        int sec = now.get(13);
        String time = twoDgtsFormat(hour) + ":" + twoDgtsFormat(min) + ":" + twoDgtsFormat(sec);
        return date + " " + time;
    }

    private static String twoDgtsFormat(int n) {
        if (n < 10) {
            return "0" + n;
        }
        return "" + n;
    }

    public static void printTours(ArrayList<Cluster> clusters, PrintStream out) {
        if ((clusters != null) && (out != null)) {
            for (int i = 0; i < clusters.size(); i++) {
                Cluster cls = (Cluster) clusters.get(i);
                cls.printTour(out);
                out.println();
            }
        }
    }

    public static void printTours(ArrayList<Cluster> clusters) {
        printTours(clusters, System.out);
    }

    public static String arrivalInstant(String startHour, double time) {
        String strTime = null;
        if (startHour != null) {
            String[] hhmi = startHour.split(":");
            if (hhmi.length >= 2) {
                int h = Integer.parseInt(hhmi[0]);
                int m = Integer.parseInt(hhmi[1]);
                Calendar cal = Calendar.getInstance();
                cal.set(11, h);
                cal.set(12, m + Math.round((float) time));
                cal.set(13, 0);

                h = cal.get(11);
                m = cal.get(12);

                strTime = twoDgtsFormat(h) + ":" + twoDgtsFormat(m);
            } else {
                System.err.println("Formato di startHour non corretto. Atteso hh:mi Non si procede al calcolo e si restituiesce null");
            }
        } else {
            System.err.println("Orario iniziale nullo!.");
        }
        return strTime;
    }

    public static int getDaysAfter(String startHour, double timeInMins) {
        int d = -1;
        if (startHour != null) {
            String[] hhmi = startHour.split(":");
            if (hhmi.length >= 2) {
                int h = Integer.parseInt(hhmi[0]);
                int m = Integer.parseInt(hhmi[1]);
                Calendar todayZero = Calendar.getInstance();
                todayZero.set(11, 0);
                todayZero.set(12, 0);
                todayZero.set(13, 0);
                todayZero.set(14, 0);

                Calendar cal = Calendar.getInstance();
                cal.set(11, h);
                cal.set(12, m + Math.round((float) timeInMins));
                cal.set(13, 0);
                cal.set(14, 0);

                long diff = cal.getTimeInMillis() - todayZero.getTimeInMillis();
                d = (int) diff / 86400000;
            } else {
                System.err.println("Formato di startHour non corretto. Atteso hh:mi Non si procede al calcolo e si restituiesce -1");
            }
        } else {
            System.err.println("Orario iniziale nullo!.");
        }
        return d;
    }

    public static String dateHourString(Calendar cal) {
        String str = cal.get(1) + "/" + twoDgtsFormat(cal.get(2) + 1) + "/" + twoDgtsFormat(cal.get(5)) + "_";

        str = str + twoDgtsFormat(cal.get(11)) + ":" + twoDgtsFormat(cal.get(12)) + ":" + twoDgtsFormat(cal.get(13));

        return str;
    }

    public static boolean isInfinity(double d) {
        return doubleEqual(d, 9999999999.0D);
    }

    public static boolean doubleEqual(double d1, double d2) {
        if (Math.abs(d2 - d1) < 1.0E-06D) {
            return true;
        }
        return false;
    }

    public static boolean doubleGreaterEqual(double d1, double d2) {
        return (doubleEqual(d1, d2)) || (d1 > d2);
    }

    public static boolean isZero(double d) {
        return doubleEqual(d, 0.0D);
    }

    public static double round2dgt(double d) {
        double tmp = Math.round(d * 100.0D);
        return tmp / 100.0D;
    }

    public static double quantizeLoad(double load, float granularity) {
        if (isZero(granularity)) {
            return load;
        }
        double quot = load / granularity;
        int slots = (int) Math.ceil(quot);
        return granularity * slots;
    }

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');

        String format = "#.###";
    }
}

