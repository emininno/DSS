package it.cyberdyne.dss.beans;

import it.cyberdyne.dss.feeds.Feed;
import it.cyberdyne.dss.feeds.ManageFeeds;
import it.cyberdyne.dss.places.Distance;
import it.cyberdyne.dss.places.ManageDistances;
import it.cyberdyne.dss.places.ManagePlaces;
import it.cyberdyne.dss.places.ManageTravelTimes;
import it.cyberdyne.dss.places.Place;
import it.cyberdyne.dss.places.TravelTime;
import it.cyberdyne.dss.routing.engine.Cluster;
import it.cyberdyne.dss.routing.engine.ClusterManager;
import it.cyberdyne.dss.routing.io.InputManager;
import it.cyberdyne.dss.routing.model.Node;
import it.cyberdyne.dss.routing.model.VehicleType;
import it.cyberdyne.dss.utils.MatrixRemover;
import it.cyberdyne.dss.vehicles.ManageVehicles;
import it.cyberdyne.dss.vehicles.Vehicle;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;

/**
 * Simple login bean.
 *
 * @author itcuties
 */
@ManagedBean(name = "routeBean")
@SessionScoped
public class RouteTableBean implements Serializable {

    private static final long serialVersionUID = 7765876811740798583L;

    private String code;
    private Integer quantity;
    private String model;
    private Double capacity;
    private Double distance;
    private Double start;
    private Integer userId;
    private boolean enabled;
    private Double time;
    private ArrayList<Cluster> clustersA;
    private ArrayList<Cluster> clustersB;
    private ArrayList<Node> nodeList;
    private ArrayList<Node> nodeListB;
    private int clusterNumberA = 0;
    private int clusterNumberB =0;
    private String tour;
    private String tourB;

   
    private Map<String, String> toursNames;
    private Map<String, String> toursNamesB;
    private String timeDisplay;
    private String distanceDisplay;
    private String vehicleCode;
    private String vehicleMaxLoad;
    private String vehicleLoad;
    private String vehicleStartTime;
    
    private String timeDisplayB;
    private String distanceDisplayB;
    private String vehicleCodeB;
    private String vehicleMaxLoadB;
    private String vehicleLoadB;
    private String vehicleStartTimeB;


    private static final String td = "Total Time [min]: ";
    private static final String dd = "Total distance [Km]: ";
    private static final String vehicleString = "Vehicle Model: ";
    private static final String maxLoad = "Vehicle Max Load [HU]: ";
    private static final String vLoad = "Vehicle Load: ";
    private static final String vStart = "Start Time [hh:mm]:";

    @ManagedProperty(value = "#{navigationBean}")
    private NavigationBean navigationBean;
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    private int loggedId;

    @PostConstruct
    public void init() {
        toursNames = new LinkedHashMap<>();

        for (int i = 0; i < clusterNumberA; i++) {
           toursNames.put(Integer.toString(i), Integer.toString(i));
        }
        timeDisplay = td;
        distanceDisplay = dd;
        vehicleCode = vehicleString;
        vehicleMaxLoad = maxLoad;
        vehicleLoad = vLoad;
        vehicleStartTime = vStart;
        this.nodeList = new ArrayList<>();
        if (clusterNumberB>0)
        {
            toursNamesB = new LinkedHashMap<>();

            for (int i = 0; i < clusterNumberB; i++) {
                toursNamesB.put(Integer.toString(i), Integer.toString(i));
            }
            timeDisplayB = td;
            distanceDisplayB = dd;
            vehicleCodeB = vehicleString;
            vehicleMaxLoadB = maxLoad;
            vehicleLoadB = vLoad;
            vehicleStartTimeB = vStart;
            this.nodeListB = new ArrayList<>();
        }
    }
    
    public String getVehicleStartTime() {
        return vehicleStartTime;
    }

    public void setVehicleStartTime(String vehicleStartTime) {
        this.vehicleStartTime = vehicleStartTime;
    }
    
    public void onCountryChange() {
        if (tour != null && !tour.equals("")) {
        }
    }

    public ArrayList<Node> getNodeList() {
        return nodeList;
    }

    public void setNodeList(ArrayList<Node> nodeList) {
        this.nodeList = nodeList;
    }

    public String getDistanceDisplay() {
        return distanceDisplay;
    }

    public void setDistanceDisplay(String distanceDisplay) {
        this.distanceDisplay = distanceDisplay;
    }

    public String getVehicleCode() {
        return vehicleCode;
    }

    public void setVehicleCode(String vehicleCode) {
        this.vehicleCode = vehicleCode;
    }

    public String getVehicleMaxLoad() {
        return vehicleMaxLoad;
    }

    public void setVehicleMaxLoad(String vehicleMaxLoad) {
        this.vehicleMaxLoad = vehicleMaxLoad;
    }

    public String getVehicleLoad() {
        return vehicleLoad;
    }

    public void setVehicleLoad(String vehicleLoad) {
        this.vehicleLoad = vehicleLoad;
    }

    public void tourDisplay() {
        int thisTour = -1;
        int thisTourB= -1;
        
        if(!tour.isEmpty())
            thisTour = Integer.parseInt(tour);
        if(!tourB.isEmpty())
            thisTourB = Integer.parseInt(tourB);
        
        Locale locale = new Locale("en", "IT");
        String pattern = "###.##";

        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        decimalFormat.applyPattern(pattern);

        if (thisTour>-1 && clusterNumberA >0)
        {
            timeDisplay = td + decimalFormat.format(clustersA.get(thisTour).getTotTime());
            distanceDisplay = dd + decimalFormat.format(clustersA.get(thisTour).getTotDistance());
            vehicleCode = vehicleString + clustersA.get(thisTour).getVehicle().getModel();
            vehicleMaxLoad = maxLoad + decimalFormat.format(clustersA.get(thisTour).getVehicle().getMaxLoad());
            vehicleLoad = vLoad + decimalFormat.format(clustersA.get(thisTour).getTotLoad());
            vehicleStartTime = vStart + clustersA.get(thisTour).getVehicle().getStartHour();
            nodeList = clustersA.get(thisTour).getTour();
        }
        if(thisTourB>-1 && clusterNumberB>0)
        {
            timeDisplayB = td + decimalFormat.format(clustersB.get(thisTourB).getTotTime());
            distanceDisplayB = dd + decimalFormat.format(clustersB.get(thisTourB).getTotDistance());
            vehicleCodeB = vehicleString + clustersB.get(thisTourB).getVehicle().getModel();
            vehicleMaxLoadB = maxLoad + decimalFormat.format(clustersB.get(thisTourB).getVehicle().getMaxLoad());
            vehicleLoadB = vLoad + decimalFormat.format(clustersB.get(thisTourB).getTotLoad());
            vehicleStartTimeB = vStart + clustersB.get(thisTourB).getVehicle().getStartHour();
            nodeListB = clustersB.get(thisTourB).getTour();
        }
        
    }

    public void updateData() {
        nodeList = new ArrayList<>();
        nodeListB = new ArrayList<>();
        tourDisplay();
    }

    public String getTimeDisplay() {
        return timeDisplay;
    }

    public void setTimeDisplay(String timeDisplay) {
        this.timeDisplay = timeDisplay;
    }

    public String getTour() {
        return tour;
    }

    public void setTour(String tour) {
        this.tour = tour;
    }

    public Map<String, String> getToursNames() {
        return toursNames;
    }

    public void setToursNames(Map<String, String> toursNames) {
        this.toursNames = toursNames;
    }

    private Double[][] removeUnused(Double[][] M, ArrayList<Integer> toBeRemoved) {
        Double[][] newM = new Double[M.length][M[0].length];
        for (int i = 0; i < M.length; i++) {
            System.arraycopy(M[i], 0, newM[i], 0, M[0].length);
        }
        Collections.sort(toBeRemoved);
        for (int i = toBeRemoved.size() - 1; i >= 0; i--) {
            newM = MatrixRemover.columnRemover(newM, toBeRemoved.get(i));
            newM = MatrixRemover.rowRemover(newM, toBeRemoved.get(i));
        }
        return newM;
    }
    

    public void route() throws CloneNotSupportedException {
        System.out.println("Initialize DB managers...");
        ManageVehicles vehicleManager = new ManageVehicles(loginBean.getLoggedId());
        ManagePlaces placeManager = new ManagePlaces(loginBean.getLoggedId());
        ManageDistances distanceManager = new ManageDistances(loginBean.getLoggedId());
        ManageFeeds feedManager = new ManageFeeds(loginBean.getLoggedId());
        ManageTravelTimes timeManager = new ManageTravelTimes((loginBean.getLoggedId()));
        
        System.out.println("Get data from DB...");
        List<Vehicle> vehicleList = vehicleManager.listVehicles();
        List<Place> placeList = placeManager.listPlaces();
        List<Feed> feedList = feedManager.listFeeds();
        
        ArrayList<Integer> placeToBeRemoved = new ArrayList<>();
        for (Place place : placeList) {
            if (!place.isEnabled()) {
                Integer removeId = place.getId()-1;
                placeToBeRemoved.add(removeId);
                System.out.println("To be removed:"+ removeId);
            }
            
        }
        Double[][] distMatrix = new Double[placeList.size()][placeList.size()];
        Double[][] timeMatrix = new Double[placeList.size()][placeList.size()];
        System.out.println("PlaceList size before:"+placeList.size());
        for (int j=placeToBeRemoved.size()-1;j>=0;j--)
        {
            int toBeremoved = placeToBeRemoved.get(j);
            System.out.println("Removing:"+placeList.get(toBeremoved).getLabel());
            placeList.remove(toBeremoved);
        }
        System.out.println("PlaceList size after:"+placeList.size());
        List<Distance> distanceList = distanceManager.listDistances();
        System.out.println("DistanceList size="+distanceList.size());
        
        List<TravelTime> timeList = timeManager.listTimes();
        System.out.println("timeList size="+timeList.size());
        
        ArrayList<VehicleType> vTypeList = new ArrayList<>();
        
        ArrayList<Node> localNodeList = new ArrayList<>();
        
        Iterator<Vehicle> it = vehicleList.iterator();
        int i = 0;
        Feed feed = feedList.get(0);//Tier A
        while (it.hasNext()) {
            Vehicle v = it.next();
            VehicleType vehicle = new VehicleType(i++, v.getCode(), v.getModel(), v.getQuantity(), v.getCapacity(), v.getDistance(), v.getTime(), v.getStart());
            vehicle.setFeedSessionAttributes(new Float(feed.getDuration()), feed.getStart(), feed.isUniformed());
            vTypeList.add(vehicle);
            //System.out.println(i+". "+vehicle);
        }

        Iterator<Place> it2 = placeList.iterator();
        i = 0;
        while (it2.hasNext()) {
            Place p = it2.next();
            Node node = new Node(i++, p.getLabel(), p.getDemandA().floatValue(), p.getServiceTime());
            System.out.println(i+":"+node);
            localNodeList.add(node);
        }
        
        Iterator<Distance> it3 = distanceList.iterator();
        /* i = 0;*/
        
        while (it3.hasNext()) {
            Distance p = it3.next();
            //System.out.println("Distance:"+p);
           
            int ix = placeListSearchId(placeList, p.getPlaceId1());
            int iy = placeListSearchId(placeList, p.getPlaceId2());
            //System.out.println("Place of "+p.getPlaceId1()+","+p.getPlaceId2()+":"+ix+","+iy);
            distMatrix[ix][iy] = p.getDistance();
        }
        System.out.println("Dist Matrix size before:"+distMatrix.length+","+distMatrix[0].length);
        if (placeToBeRemoved.size() > 0) {
            distMatrix = removeUnused(distMatrix, placeToBeRemoved);
        }
        System.out.println("Dist Matrix size after:"+distMatrix.length+","+distMatrix[0].length);
        System.out.println("Initializing input manager...");
        String s="";
        
        Iterator<TravelTime> it4 = timeList.iterator();
        /* i = 0;*/
        
        while (it4.hasNext()) {
            TravelTime p = it4.next();
            //System.out.println("Distance:"+p);
           
            int ix = placeListSearchId(placeList, p.getPlaceId1());
            int iy = placeListSearchId(placeList, p.getPlaceId2());
            //System.out.println("Place of "+p.getPlaceId1()+","+p.getPlaceId2()+":"+ix+","+iy);
            timeMatrix[ix][iy] = p.getTime();
        }
        System.out.println("Time Matrix size before:"+timeMatrix.length+","+timeMatrix[0].length);
        if (placeToBeRemoved.size() > 0) {
            timeMatrix = removeUnused(timeMatrix, placeToBeRemoved);
        }
        System.out.println("Dist Matrix size after:"+timeMatrix.length+","+timeMatrix[0].length);
        System.out.println("Initializing input manager...");
        s="";

        InputManager iMan = new InputManager(1, distMatrix, timeMatrix, localNodeList, vTypeList);
        ClusterManager clusterMan = new ClusterManager(iMan);
        clusterNumberA = clusterMan.process();
        System.out.println("Trovati " + clusterNumberA + " cluster");
        if (clusterNumberA > 0) {
            System.out.println("Clusters:");
            clusterMan.printClusters();
            System.out.println("´Routing:");
            clusterMan.printTours();
            clustersA = clusterMan.getClusters();
            init();
        }
        
        System.out.println("get Places in Tier B");
        it2 = placeList.iterator();
        i = 0;
        localNodeList.clear();
        while (it2.hasNext()) {
            Place p = it2.next();
            Node node = new Node(i++, p.getLabel(), p.getDemandB().floatValue(), p.getServiceTime());
            System.out.println(i+"of Tier B:"+node);
            localNodeList.add(node);
        }

        System.out.println("Get Feed in Tier B");
        Feed feedB = feedList.get(1);//Tier B
        
        //Remove used Vehicles in Tour A
        i=0;
        Iterator<Cluster> clusterIterator = this.clustersA.iterator();
        while (clusterIterator.hasNext()) {
            
            Cluster c = clusterIterator.next();
            String ccode = c.getVehicle().getCode();
            int vehicleIndex = searchVtype(vTypeList, ccode);
            System.out.println("Cluster "+i++ +" VCode:"+c.getVehicle().getCode()+" index:"+vehicleIndex + " vTypeCode:"+vTypeList.get(vehicleIndex).getCode());
            if (vehicleIndex>-1)
            {
                int currentQuantity = vTypeList.get(vehicleIndex).getTotQty();
                System.out.println("Decrese quantity of vehicle i:"+vehicleIndex+" code:"+ vTypeList.get(vehicleIndex).getCode()+". Quantity:"+currentQuantity);
                
                if (currentQuantity>0){
                    vTypeList.get(vehicleIndex).setTotQty(currentQuantity-1);
                }
                else{
                    vTypeList.get(vehicleIndex).setTotQty(0);
                }
            }
        }
        //Tier B vehicles
        Iterator<VehicleType> vit = vTypeList.iterator();
        i = 0;
        System.out.println("Setting new feeds to vehicles...");
        while (it.hasNext()) {
            VehicleType v = vit.next();
            v.setFeedSessionAttributes(new Float(feedB.getDuration()), feedB.getStart(), feedB.isUniformed());
        }
        System.out.println("Getting re-entry vehicle from tier A...");
        clusterIterator = this.clustersA.iterator();
        while (clusterIterator.hasNext()) {
            Cluster c = clusterIterator.next();
            String ccode = c.getVehicle().getCode();
            double maxTime = stringToDoubleH(c.getVehicle().getStartHour())+c.getTotTime();
            System.out.println("Current v Time:"+c.getVehicle().getCode()+". Time:"+maxTime+90+". Limit:"+feedB.getStart()+"+"+feed.getDuration());
            if (maxTime+90 < stringToDoubleH(feedB.getStart())+feed.getDuration())
            {
                it.cyberdyne.dss.routing.model.Vehicle oldVehicle = c.getVehicle();
                System.out.println("Re-enter vehicle "+oldVehicle.getCode() + " at "+DoubleToString(maxTime));
                VehicleType newVehicle= new VehicleType(1, oldVehicle.getCode(), 
                        oldVehicle.getModel(), 
                        1, //new Quantity 
                        oldVehicle.getMaxLoad(), 
                        oldVehicle.getMaxDistance(),
                        1000000,
                        DoubleToString(maxTime));
                vTypeList.add(newVehicle);
            }
        }
        iMan = new InputManager(1, distMatrix, timeMatrix, localNodeList, vTypeList);
        clusterMan = new ClusterManager(iMan);
        clusterNumberB = clusterMan.process();
        System.out.println("Trovati " + clusterNumberB + " cluster");
        if (clusterNumberB > 0) {
            System.out.println("Clusters:");
            clusterMan.printClusters();
            System.out.println("´Routing:");
            clusterMan.printTours();
            clustersB = clusterMan.getClusters();
            init();
        }
    }
    
    private String DoubleToString(double m)
    {
        int T=(int)m;
        String h  = Integer.toString(T / 60);
        String mm = Integer.toString(T % 60);
        String sh=  h+":"+mm;
        return sh;
    }
    private double stringToDoubleH(String s){
        double ret;
        String[] ctime = s.split(":");
        double h = new Double(ctime[0])*60;
        double m = new Double(ctime[1]);
        ret=h+m;
        return ret;
    }

    private int searchVtype(ArrayList<VehicleType> v, String code){
        for (int i=0; i<v.size(); i++)
        {
            String s = v.get(i).getCode();
            //System.out.println("VType["+i+"]:"+s);
            if(s.equalsIgnoreCase(code))
                return i;
        }
        return -1;
    }
    
    private int placeListSearchId(List<Place> placeList, int id) {
        
        for (int i=0; i<placeList.size(); i++)
        {
            if (placeList.get(i).getId() == id)
                return i;
        }
        return -1;
        
    }
    

    
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getStart() {
        return start;
    }

    public void setStart(Double start) {
        this.start = start;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public NavigationBean getNavigationBean() {
        return navigationBean;
    }

    public void setNavigationBean(NavigationBean navigationBean) {
        this.navigationBean = navigationBean;
    }

    public LoginBean getLoginBean() {
        return loginBean;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public int getLoggedId() {
        return loggedId;
    }

    public void setLoggedId(int loggedId) {
        this.loggedId = loggedId;
    }

     public ArrayList<Node> getNodeListB() {
        return nodeListB;
    }

    public void setNodeListB(ArrayList<Node> nodeListB) {
        this.nodeListB = nodeListB;
    }

    public String getTourB() {
        return tourB;
    }

    public void setTourB(String tourB) {
        this.tourB = tourB;
    }

    public Map<String, String> getToursNamesB() {
        return toursNamesB;
    }

    public void setToursNamesB(Map<String, String> toursNamesB) {
        this.toursNamesB = toursNamesB;
    }

    public String getTimeDisplayB() {
        return timeDisplayB;
    }

    public void setTimeDisplayB(String timeDisplayB) {
        this.timeDisplayB = timeDisplayB;
    }

    public String getDistanceDisplayB() {
        return distanceDisplayB;
    }

    public void setDistanceDisplayB(String distanceDisplayB) {
        this.distanceDisplayB = distanceDisplayB;
    }

    public String getVehicleCodeB() {
        return vehicleCodeB;
    }

    public void setVehicleCodeB(String vehicleCodeB) {
        this.vehicleCodeB = vehicleCodeB;
    }

    public String getVehicleMaxLoadB() {
        return vehicleMaxLoadB;
    }

    public void setVehicleMaxLoadB(String vehicleMaxLoadB) {
        this.vehicleMaxLoadB = vehicleMaxLoadB;
    }

    public String getVehicleLoadB() {
        return vehicleLoadB;
    }

    public void setVehicleLoadB(String vehicleLoadB) {
        this.vehicleLoadB = vehicleLoadB;
    }

    public String getVehicleStartTimeB() {
        return vehicleStartTimeB;
    }

    public void setVehicleStartTimeB(String vehicleStartTimeB) {
        this.vehicleStartTimeB = vehicleStartTimeB;
    }
}
