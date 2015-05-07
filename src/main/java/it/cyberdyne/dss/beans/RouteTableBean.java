package it.cyberdyne.dss.beans;

import it.cyberdyne.dss.places.Distance;
import it.cyberdyne.dss.places.ManageDistances;
import it.cyberdyne.dss.places.ManagePlaces;
import it.cyberdyne.dss.places.Place;
import it.cyberdyne.dss.routing.engine.Cluster;
import it.cyberdyne.dss.routing.engine.ClusterManager;
import it.cyberdyne.dss.routing.io.InputManager;
import it.cyberdyne.dss.routing.io.reader.MatrixReader;
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
    private ArrayList<Cluster> clusters;
    private ArrayList<Node> nodeList;
    private int clusterNumber = 0;
    private String tour;
    private Map<String, String> toursNames;
    private String timeDisplay;
    private String distanceDisplay;
    private String vehicleCode;
    private String vehicleMaxLoad;
    private String vehicleLoad;
    private static final String td = "Total Time: ";
    private static final String dd = "Total distance: ";
    private static final String vehicleString = "Vehicle code: ";
    private static final String maxLoad = "Vehicle Max Load: ";
    private static final String vLoad = "Vehicle Load: ";

    @ManagedProperty(value = "#{navigationBean}")
    private NavigationBean navigationBean;
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    private int loggedId;

    @PostConstruct
    public void init() {
        toursNames = new LinkedHashMap<>();

        for (int i = 0; i < clusterNumber; i++) {
            System.out.println("XXX:" + Integer.toString(i));
            toursNames.put(Integer.toString(i), Integer.toString(i));
        }
        timeDisplay = td;
        distanceDisplay = dd;
        vehicleCode = vehicleString;
        vehicleMaxLoad = maxLoad;
        vehicleLoad = vLoad;
        this.nodeList = new ArrayList<>();
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
        int thisTour = Integer.parseInt(tour);

        Locale locale = new Locale("en", "IT");
        String pattern = "###.##";

        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(locale);
        decimalFormat.applyPattern(pattern);

        timeDisplay = td + decimalFormat.format(clusters.get(thisTour).getTotTime());
        distanceDisplay = dd + decimalFormat.format(clusters.get(thisTour).getTotDistance());
        vehicleCode = vehicleString + clusters.get(thisTour).getVehicle().getCode();
        vehicleMaxLoad = maxLoad + decimalFormat.format(clusters.get(thisTour).getVehicle().getMaxLoad());
        vehicleLoad = vLoad + decimalFormat.format(clusters.get(thisTour).getTotLoad());
        nodeList = clusters.get(thisTour).getTour();
    }

    public void updateData() {
        nodeList = new ArrayList<>();
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

    private Double[][] removeDistances(Double[][] M, ArrayList<Integer> toBeRemoved) {
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
        System.out.println("Get data from DB...");
        List<Vehicle> vehicleList = vehicleManager.listVehicles();

        List<Place> placeList = placeManager.listPlaces();
        ArrayList<Integer> placeToBeRemoved = new ArrayList<>();
        int c = 0;
        for (Place place : placeList) {
            
            if (!place.isEnabled()) {
                Integer removeId = place.getId()-1;
                placeToBeRemoved.add(removeId);
                System.out.println("To be removed:"+ removeId);
            }
            
        }
        Double[][] distMatrix = new Double[placeList.size()][placeList.size()];
        System.out.println("PlaceList size before:"+placeList.size());
        for (int j=placeToBeRemoved.size()-1;j>=0;j--)
        {
            int toBeremoved = placeToBeRemoved.get(j);
            System.out.println("Removing:"+placeList.get(toBeremoved).getLabel());
            placeList.remove(toBeremoved);
        }
        System.out.println("PlaceList size after:"+placeList.size());
        List<Distance> distanceList = distanceManager.listDistances();
        ArrayList<VehicleType> vTypeList = new ArrayList<>();
        ArrayList<Node> localNodeList = new ArrayList<>();
        
        Iterator<Vehicle> it = vehicleList.iterator();
        int i = 0;
        while (it.hasNext()) {
            Vehicle v = it.next();
            VehicleType vehicle = new VehicleType(i++, v.getCode(), v.getModel(), v.getQuantity(), v.getCapacity(), v.getDistance(), v.getTime(), v.getTime().toString());
            vTypeList.add(vehicle);
        }
        Iterator<Place> it2 = placeList.iterator();
        i = 0;
        while (it2.hasNext()) {
            Place p = it2.next();
            Node node = new Node(i++, p.getLabel(), p.getDemand().floatValue(), p.getServiceTime());
            //System.out.println(i+":"+node);
            localNodeList.add(node);
        }
        Iterator<Distance> it3 = distanceList.iterator();
        /* i = 0;*/
        while (it3.hasNext()) {
            Distance p = it3.next();
            distMatrix[p.getPlaceId1() - 1][p.getPlaceId2() - 1] = p.getDistance();
        }
        System.out.println("Dist Matrix size before:"+distMatrix.length+","+distMatrix[0].length);
        if (placeToBeRemoved.size() > 0) {
            distMatrix = removeDistances(distMatrix, placeToBeRemoved);
        }
        System.out.println("Dist Matrix size after:"+distMatrix.length+","+distMatrix[0].length);
        InputManager iMan = new InputManager(1, distMatrix, localNodeList, vTypeList);
        ClusterManager clusterMan = new ClusterManager(iMan);
        clusterNumber = clusterMan.process();
        System.out.println("Trovati " + clusterNumber + " cluster");
        if (clusterNumber > 0) {
            System.out.println("Clusters:");
            clusterMan.printClusters();
            System.out.println("´Routing:");
            clusterMan.printTours();
            clusters = clusterMan.getClusters();
            init();
        }
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

}
