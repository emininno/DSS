/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.beans;

import it.cyberdyne.dss.places.Distance;
import it.cyberdyne.dss.places.ManagePlaces;
import it.cyberdyne.dss.places.Place;
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
import org.apache.commons.io.FilenameUtils;
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
    private ArrayList<Distance> distanceList;
    private ArrayList<Place> deletedPlaces;
    private PlaceBean service;
    private Place selectedPlace;

    private UploadedFile file;
    private UploadedFile fileMatrix;

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

            FacesMessage message = new FacesMessage("Succesful: ", file2.getFileName() + " has been uploaded.");
            FacesContext.getCurrentInstance().addMessage(null, message); //TODO dove va questo messaggio?
            String theString;
            theString = IOUtils.toString(file2.getInputstream(), "UTF-8");
            //System.out.println("theString:"+theString);
            ArrayList<String> list = new ArrayList<>(Arrays.asList(theString.split("\n")));
            //System.out.println("List size:"+ list.size());
            updateMatrix(list);
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

    public String saveAction() {
        SessionFactory s = HibernateUtil.getSessionFactory();
        for (Place placeList1 : placeList) {
            int id = placeList1.getId();
            if (id < 0) {
                Session session = s.openSession();
                Transaction tx = null;
                try {
                    tx = session.beginTransaction();
                    session.save(placeList1);
                    tx.commit();
                } catch (HibernateException e) {
                    if (tx != null) {
                        tx.rollback();
                    }
                    e.printStackTrace();
                } finally {
                    session.close();
                }
            } else {
                if (placeList1.isEdit()) {
                    Session session = s.openSession();
                    Transaction tx = null;
                    try {
                        tx = session.beginTransaction();
                        Place p = (Place) session.get(Place.class, id);

                        p.copy(placeList1);
                        session.update(p);
                        tx.commit();
                    } catch (HibernateException e) {
                        if (tx != null) {
                            tx.rollback();
                        }
                        e.printStackTrace();
                    } finally {
                        session.close();
                    }

                }
            }
        }
        for (Place v : deletedPlaces) {
            int id = v.getId();
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
                e.printStackTrace();
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

    @PostConstruct
    public void init() {
        this.placeList = (ArrayList<Place>) getPlaceListFromDB();
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
        System.out.println("xxx:" + ((Place) event.getObject()).getLabel());
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
        System.out.println("Modified place id:" + id);
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
        placeList.add(new Place("None", 0.0, 0, topen, tclose, "nowhere", loginBean.getLoggedId()));
    }

    public void addDistance(int id1, int id2, double dist) {
        distanceList.add(new Distance(id1, id2, dist));
    }

    public void addPlace(List<String> row) {
        String label = row.get(0);
        Double demand = Double.parseDouble(row.get(1).replaceAll(",", "."));
        Integer serviceTime = Integer.parseInt(row.get(2));
        String[] tOpenH = row.get(3).split(":");
        String[] tCloseH = row.get(4).split(":");
        String place = row.get(5);
        Time tOpen = new Time(Integer.parseInt(tOpenH[0]), Integer.parseInt(tOpenH[1]), 0);
        Time tClose = new Time(Integer.parseInt(tCloseH[0]), Integer.parseInt(tCloseH[1]), 0);
        placeList.add(new Place(label, demand, serviceTime, tOpen, tClose, place, loginBean.getLoggedId()));
    }

    public void updateMatrix(List<String> list) {

        ArrayList<String> row0 = new ArrayList<>(Arrays.asList(list.get(0).split(";")));
        ArrayList<Integer> indices = new ArrayList<>();
        Iterator<String> it = row0.iterator();
        while (it.hasNext()) {
            String currentLabel = it.next();
            int index = searchInPlaceList(currentLabel, placeList);
            if (index > 0) {
                indices.add(index);
            }
        }
        for (int i = 1; i < list.size(); i++) {
            String matrixString = list.get(i);
            List<String> row = new ArrayList<>(Arrays.asList(matrixString.split(";")));
            for (int j = i; j < row.size(); j++) {
                if (searchInDistanceList(indices.get(i), indices.get(j)) == -1) {
                    double dist=Double.parseDouble(row.get(j));
                    addDistance(indices.get(i), indices.get(j), dist);
                    System.out.println("Id1:"+indices.get(i)+" Id2:"+indices.get(j)+" d:"+dist);
                } else {
                    System.out.println("Distance found...");
                }

            }
        }
    }

    public void updatePlaces(List<String> list) {
        int i = 0;
        for (String placeString : list) {

            List<String> row = new ArrayList<String>(Arrays.asList(placeString.split(";")));
            if (searchInPlaceList(row.get(0), placeList) == -1) {
                System.out.println(i++ + ":");
                addPlace(row);
            } else {
                System.out.println("Trovato.");
            }
        }
    }

    private int searchInDistanceList(int id1, int id2) {
        for (int i = 0; i < distanceList.size(); i++) {
            if ((distanceList.get(i).getPlaceId1() == id1)
                    && (distanceList.get(i).getPlaceId2() == id2)) {
                return i;
            }
        }
        return -1;
    }

    private int searchInPlaceList(String label, List<Place> list) {

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getLabel().equalsIgnoreCase(label)) {
                return i;
            }
        }
        return -1;
    }
}
