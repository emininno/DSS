package it.cyberdyne.dss.beans;

import it.cyberdyne.dss.places.Distance;
import it.cyberdyne.dss.places.ManageDistances;
import it.cyberdyne.dss.places.ManagePlaces;
import it.cyberdyne.dss.places.Place;
import it.cyberdyne.dss.routing.engine.ClusterManager;
import it.cyberdyne.dss.routing.io.InputManager;
import it.cyberdyne.dss.routing.model.Node;
import it.cyberdyne.dss.routing.model.VehicleType;
import it.cyberdyne.dss.vehicles.ManageVehicles;
import it.cyberdyne.dss.vehicles.Vehicle;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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

    @ManagedProperty(value = "#{navigationBean}")
    private NavigationBean navigationBean;
    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    private int loggedId;

    public void route() throws CloneNotSupportedException {
        System.out.println("Initialize DB managers...");
        ManageVehicles vehicleManager = new ManageVehicles(loginBean.getLoggedId());
        ManagePlaces placeManager = new ManagePlaces(loginBean.getLoggedId());
        ManageDistances distanceManager = new ManageDistances(loginBean.getLoggedId());
        System.out.println("Get data from DB...");
        List<Vehicle> vehicleList = vehicleManager.listVehicles();
        List<Distance> distanceList = distanceManager.listDistances();
        List<Place> placeList = placeManager.listPlaces();

        ArrayList<VehicleType> vTypeList = new ArrayList<>();
        ArrayList<Node> nodeList = new ArrayList<>();
        Double[][] distMatrix = new Double[placeList.size()][placeList.size()];
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
            nodeList.add(node);
        }
        Iterator<Distance> it3 = distanceList.iterator();
        i = 0;
        while (it3.hasNext()) {
            Distance p = it3.next();
            distMatrix[p.getPlaceId1() - 1][p.getPlaceId2() - 1] = p.getDistance();
        }
        InputManager iMan = new InputManager(1, distMatrix, nodeList, vTypeList);
        ClusterManager clusterMan = new ClusterManager(iMan);
        int ncl = clusterMan.process();
        System.out.println("Trovati " + ncl + " cluster");
        if (ncl > 0) {

            //clusterMan.printClusters();
            System.out.println("´Routing:");
            //clusterMan.printTours();
        
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
