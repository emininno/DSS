/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.beans;

import it.cyberdyne.dss.places.Distance;
import it.cyberdyne.dss.places.ManageDistances;
import it.cyberdyne.dss.places.ManagePlaces;
import it.cyberdyne.dss.places.ManageTravelTimes;
import it.cyberdyne.dss.places.Place;
import it.cyberdyne.dss.places.TravelTime;
import it.cyberdyne.dss.utils.HibernateUtil;
import it.cyberdyne.dss.vehicles.Vehicle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.io.IOUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author ern
 */
@ManagedBean
@SessionScoped
public class PlaceTableBean implements Serializable {

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    private static final File LOCATION = new File("/tmp");
    private ArrayList<Place> placeList;
    private ArrayList<Place> deletedPlaces;
    private ArrayList<Distance> distanceList;
    private ArrayList<TravelTime> timeList;
    private PlaceBean service;
    private Place selectedPlace;

    private UploadedFile file;
    private UploadedFile fileMatrix;
    private UploadedFile fileTimeMatrix;

    public UploadedFile getFileTimeMatrix() {
        return fileTimeMatrix;
    }

    public void setFileTimeMatrix(UploadedFile fileTimeMatrix) {
        this.fileTimeMatrix = fileTimeMatrix;
    }

    public UploadedFile getFileMatrix() {
        return fileMatrix;
    }

    public void setFileMatrix(UploadedFile fileMatrix) {
        this.fileMatrix = fileMatrix;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void dummy() {
    }

    public void copyFile(String fileName, InputStream in) throws FileNotFoundException, IOException {
        OutputStream out = new FileOutputStream(new File(LOCATION + File.separator + fileName));
        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = in.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
        in.close();
        out.flush();
        out.close();
        System.out.println("New file created!");
    }

    public void upload(FileUploadEvent event) throws IOException {
        System.out.println("UPLOAD");
        //RequestContext.getCurrentInstance().closeDialog(event);
        UploadedFile file2 = event.getFile();
        if (file2 != null) {

            FacesMessage message = new FacesMessage("Succesful", file2.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message); //TODO dove va questo messaggio?
            String theString;
            theString = IOUtils.toString(file2.getInputstream(), "UTF-8");
            //System.out.println("theString:"+theString);
            ArrayList<String> list = new ArrayList<>(Arrays.asList(theString.split("\n")));
            //System.out.println("List size:"+ list.size());
            updatePlaces(list);
        } else {
            System.out.println("NULL!");
        }
    }

    public void uploadMatrix(FileUploadEvent event) throws IOException {
        System.out.println("UPLOAD Matrix");
        //RequestContext.getCurrentInstance().closeDialog(event);
        UploadedFile file2 = event.getFile();
        if (file2 != null) {

            FacesMessage message = new FacesMessage("Succesful", file2.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message); //TODO dove va questo messaggio?
            String theString;
            theString = IOUtils.toString(file2.getInputstream(), "UTF-8");
            System.out.println("theString:"+theString);
            ArrayList<String> list = new ArrayList<>(Arrays.asList(theString.split("\n")));
            System.out.println("List size:"+ list.size());
            updateMatrix(list);
        } else {
            System.out.println("NULL!");
        }
    }
    
    public void uploadTimeMatrix(FileUploadEvent event) throws IOException {
        System.out.println("UPLOAD Time Matrix");
        //RequestContext.getCurrentInstance().closeDialog(event);
        UploadedFile file2 = event.getFile();
        if (file2 != null) {

            FacesMessage message = new FacesMessage("Succesful", file2.getFileName() + " is uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message); //TODO dove va questo messaggio?
            String theString;
            theString = IOUtils.toString(file2.getInputstream(), "UTF-8");
            System.out.println("theString:"+theString);
            ArrayList<String> list = new ArrayList<>(Arrays.asList(theString.split("\n")));
            System.out.println("List size:"+ list.size());
            updateTimeMatrix(list);
        } else {
            System.out.println("NULL!");
        }
    }

    public Place getSelectedPlace() {
        return selectedPlace;
    }

    public void setSelectedPlace(Place selectedPlace) {
        this.selectedPlace = selectedPlace;
    }

    public void toggleEnabled() {
        if (selectedPlace.isEnabled()) {
            selectedPlace.setEnabled(false);
        } else {
            selectedPlace.setEnabled(true);
        }
    }

    public ArrayList<Place> getPlaceList() {
        return placeList;
    }

    /**
     * Creates a new instance of PlaceTableBean
     */
    public PlaceTableBean() {

    }

    private Place[] getPlaceArrayFromDB() {
        ManagePlaces manager = new ManagePlaces(loginBean.getLoggedId());
        List<Place> list = manager.listPlaces();
        Place[] p = new Place[list.size()];
        return list.toArray(p);
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    private List<Place> getPlaceListFromDB() {
        System.out.println("Get From DB...");
        ManagePlaces manager = new ManagePlaces(loginBean.getLoggedId());
        List<Place> list = manager.listPlaces();
        return list;
    }

    private List<Distance> getDistanceListFromDB() {
        System.out.println("Get Distances From DB...");
        ManageDistances manager;
        manager = new ManageDistances(loginBean.getLoggedId());
        List<Distance> list = manager.listDistances();
        return list;
    }
    
    private List<TravelTime> getTimeListFromDB() {
        System.out.println("Get Time From DB...");
        ManageTravelTimes manager;
        manager = new ManageTravelTimes(loginBean.getLoggedId());
        List<TravelTime> list = manager.listTimes();
        return list;
    }

    public String saveAction() {
        SessionFactory s = HibernateUtil.getSessionFactory();
        for (Place place : placeList) {
            int id = place.getId();
            if (id < 0) {
                Session session = s.openSession();
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    session.save(place);
                    tx.commit();
                } catch (HibernateException e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                } finally {
                    session.close();
                }
            } else {
                if (place.isEdit()) {
                    Session session = s.openSession();
                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();
                        Place p = (Place) session.get(Place.class, id);

                        p.copy(place);
                        session.update(p);
                        tx.commit();
                    } catch (HibernateException e) {
                        if (tx != null) {
                            tx.rollback();
                        }
                    } finally {
                        session.close();
                    }

                }
            }
        }
        for (Place place2 : deletedPlaces) {
            int id = place2.getId();
            Session session = s.openSession();
            Transaction tx = null;
            try {
                tx = session.beginTransaction();
                Place tmpV = (Place) session.get(Place.class, id);
                session.delete(tmpV);
                tx.commit();
            } catch (HibernateException e) {
                if (tx != null) {
                    tx.rollback();
                }
            } finally {
                session.close();
            }
        }
        deletedPlaces.removeAll(deletedPlaces);
        //return to current page
        return null;
    }

    public String editAction(Place place) {
        place.setEdit(true);
        return null;
    }

    private void printCurrentDistanceMatrix() {
        String listString = "";
        for (Distance d : this.distanceList) {
            listString += d.toString() + "\n";
        }
        System.out.println(listString);
    }

    @PostConstruct
    public void init() {
        this.placeList = (ArrayList<Place>) getPlaceListFromDB();
        this.distanceList = (ArrayList<Distance>) getDistanceListFromDB();
        this.distanceList = new ArrayList<>();
        this.deletedPlaces = new ArrayList<>();

    }

    public void setService(PlaceBean service) {
        this.service = service;
    }

    public void onEdit(RowEditEvent event) {
        //System.out.println("xxx:");
    }

    public void onRowEdit(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Place Edited", ((Place) event.getObject()).getLabel());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        ((Vehicle) event.getObject()).setEdit(true);
        //System.out.println("xxx:" + ((Place) event.getObject()).getLabel());
    }

    public void onRowCancel(RowEditEvent event) {
        FacesMessage msg = new FacesMessage("Edit Cancelled", ((Place) event.getObject()).getLabel());
        FacesContext.getCurrentInstance().addMessage(null, msg);
        ((Vehicle) event.getObject()).setEdit(false);
    }

    public void onCellEdit(CellEditEvent event) {
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        int id = event.getRowIndex();
        //((Vehicle) event.getSource()).setEdit(true);
        placeList.get(id).setEdit(true);
        //System.out.println("Modified place id:" + id);
        //if (newValue != null && !newValue.equals(oldValue)) {
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Cell Changed", "Row:" + id);
        FacesContext.getCurrentInstance().addMessage(null, msg);
        //}
    }

    public void deletePlace() {
        deletedPlaces.add(selectedPlace);
        placeList.remove(selectedPlace);
        selectedPlace = null;
    }

    public void addPlace() {
        Time topen = new Time(8, 0, 0);
        Time tclose = new Time(18, 0, 0);
        placeList.add(new Place("None", 0.0,0.0, 0, topen, tclose, "nowhere", loginBean.getLoggedId()));
    }

    public void addPlace(List<String> row) {
        String label = row.get(0);
        Double demandA = Double.parseDouble(row.get(1).replaceAll(",", "."));
        Double demandB = Double.parseDouble(row.get(2).replaceAll(",", "."));
        Integer serviceTime = Integer.parseInt(row.get(3));
        String[] tOpenH = row.get(4).split(":");
        String[] tCloseH = row.get(5).split(":");
        String place = row.get(6);
        Time tOpen = new Time(Integer.parseInt(tOpenH[0]), Integer.parseInt(tOpenH[1]), 0);
        Time tClose = new Time(Integer.parseInt(tCloseH[0]), Integer.parseInt(tCloseH[1]), 0);
        placeList.add(new Place(label, demandA,demandB, serviceTime, tOpen, tClose, place, loginBean.getLoggedId()));
    }

    public void addDistance(int id1, int id2, double dist) {
        Distance d = new Distance(id1, id2, dist);
        //System.out.println("Distance added:"+d);
        distanceList.add(d);
    }
    
    public void addTime(int id1, int id2, double dist) {
        TravelTime d = new TravelTime(id1, id2, dist);
        //System.out.println("Distance added:"+d);
        timeList.add(d);
    }

    public void updateMatrix(List<String> list) {
        System.out.println("UpdateMatrix");
        this.distanceList = (ArrayList<Distance>) getDistanceListFromDB();
        ArrayList<String> row0 = new ArrayList<>(Arrays.asList(list.get(0).split(";")));
        ArrayList<Integer> indices = new ArrayList<>();
        Iterator<String> it = row0.iterator();
        int c = 0;
        while (it.hasNext()) {
            String currentLabel = it.next();
            
            int index = searchInPlaceList(currentLabel, placeList);
            if (index >= 0) {
                indices.add(index);
                
            }
        }
        //System.out.println("ListSize:"+list.size()+" IndicesSize:"+indices.size());
        ManagePlaces manager = new ManagePlaces(loginBean.getLoggedId());
        int rowNumber = 0;
        for (int i = 0; i < indices.size(); i++) {
            rowNumber++;
            String currentRow=list.get(rowNumber);
            List<String> rows = new ArrayList<>(Arrays.asList(currentRow.split(";")));
            for (int j = 0; j < indices.size(); j++) {
                int id1 = manager.getPlaceId(row0.get(i));
                int id2 = manager.getPlaceId(row0.get(j));

                int s=searchInDistanceList(id1, id2);
                if (s != -1) {
                    //System.out.println("Distance Found!");
                } else {
                    addDistance(id1, id2, Double.parseDouble(rows.get(j)));
                }
            }
        }
        //System.out.println(" ************ Current List: **********");
        //System.out.println("DistanceList size:"+distanceList.size());
        //printCurrentDistanceMatrix();
        SessionFactory s = HibernateUtil.getSessionFactory();
        for (Distance distance : distanceList) {
            int id;
            if (distance.getId()!=null)
                id = distance.getId();
            else
                id = -1;
           
            if (id < 0) {
                Session session = s.openSession();
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    session.save(distance);
                    tx.commit();
                } catch (HibernateException e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                } finally {
                    session.close();
                }
            }
        }
    }
    
    public void updateTimeMatrix(List<String> list) {
        System.out.println("UpdateTimeMatrix");
        this.timeList = (ArrayList<TravelTime>) getTimeListFromDB();
        this.placeList= (ArrayList<Place>)  getPlaceListFromDB();
        ArrayList<String> row0 = new ArrayList<>(Arrays.asList(list.get(0).split(";")));
        ArrayList<Integer> indices = new ArrayList<>();
        Iterator<String> it = row0.iterator();
        int c = 0;
        while (it.hasNext()) {
            String currentLabel = it.next();
            int index = searchInPlaceList(currentLabel, placeList);
            if (index >= 0) {
                indices.add(index);
                
            }
        }
        //System.out.println("ListSize:"+list.size()+" IndicesSize:"+indices.size());
        ManagePlaces manager = new ManagePlaces(loginBean.getLoggedId());
        int rowNumber = 0;
        for (int i = 0; i < indices.size(); i++) {
            rowNumber++;
            String currentRow=list.get(rowNumber);
            List<String> rows = new ArrayList<>(Arrays.asList(currentRow.split(";")));
            for (int j = 0; j < indices.size(); j++) {
                
                int id1 = manager.getPlaceId(row0.get(i));
                int id2 = manager.getPlaceId(row0.get(j));

                int s=searchInTimeList(id1, id2);
                if (s != -1) {
                    //System.out.println("Distance Found!");
                } else {
                    addTime(id1, id2, Double.parseDouble(rows.get(j)));
                }
            }
        }
        //System.out.println(" ************ Current List: **********");
        //System.out.println("DistanceList size:"+distanceList.size());
        //printCurrentDistanceMatrix();
        SessionFactory s = HibernateUtil.getSessionFactory();
        for (TravelTime time : timeList) {
            int id;
            if (time.getId()!=null)
                id = time.getId();
            else
                id = -1;
           
            if (id < 0) {
                Session session = s.openSession();
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    session.save(time);
                    tx.commit();
                } catch (HibernateException e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                } finally {
                    session.close();
                }
            }
        }
    }

    public void updatePlaces(List<String> list) {
        int i = 0;
        for (String placeString : list) {

            List<String> row = new ArrayList<>(Arrays.asList(placeString.split(";")));
            if (searchInPlaceList(row.get(0), placeList) == -1) {
                //System.out.println(i++ + ":");
                addPlace(row);
            } else {
                //System.out.println("Trovato.");
            }
        }
    }

    private int searchInPlaceList(String label, List<Place> list) {

        for (int i = 0; i < list.size(); i++) {
            //System.out.println(i+". "+label+"=="+list.get(i).getLabel());
            boolean b=list.get(i).getLabel().contains(label);
            //System.out.println("b="+b);
            if (list.get(i).getLabel().equalsIgnoreCase(label)) {
                //System.out.println("got it!");
                return i;
            }
        }
        return -1;
    }

    private int searchInTimeList(int id1, int id2) {

        for (TravelTime d : timeList) {
            //System.out.println("d:"+d);
            if ((d.getPlaceId1() == id1) && (d.getPlaceId2() == id2)) {
                if(d != null && d.getId()!=null)
                    return d.getId();
                else 
                    return -1;
            }
        }
        return -1;
    }
    
    private int searchInDistanceList(int id1, int id2) {

        for (Distance d : distanceList) {
            //System.out.println("d:"+d);
            if ((d.getPlaceId1() == id1) && (d.getPlaceId2() == id2)) {
                if(d != null && d.getId()!=null)
                    return d.getId();
                else 
                    return -1;
            }
        }
        return -1;
    }
}
