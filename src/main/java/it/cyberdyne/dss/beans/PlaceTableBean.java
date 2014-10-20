/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.cyberdyne.dss.beans;

import it.cyberdyne.dss.places.ManagePlaces;
import it.cyberdyne.dss.places.Place;
import it.cyberdyne.dss.utils.HibernateUtil;
import it.cyberdyne.dss.vehicles.Vehicle;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.primefaces.context.RequestContext;
import org.primefaces.event.CellEditEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author ern
 */
@ManagedBean
@SessionScoped
public class PlaceTableBean {

    @ManagedProperty(value = "#{loginBean}")
    private LoginBean loginBean;
    
    private ArrayList<Place> placeList;
    private ArrayList<Place> deletedPlaces;
    private PlaceBean  service;
    private Place selectedPlace;
    
    private UploadedFile file;
 
    public UploadedFile getFile() {
        return file;
    }
 
    public void setFile(UploadedFile file) {
        this.file = file;
    }
     
    public void upload() {
        System.out.println("UPLOAD");
        RequestContext.getCurrentInstance().closeDialog(file);
        if(file != null) {
            System.out.println("NotNULL");
            FacesMessage message = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
            
            FacesContext.getCurrentInstance().addMessage(null, message);
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
        ManagePlaces manager = new ManagePlaces(loginBean.getLoggedId());
        List<Place> list = manager.listPlaces();
        return list;
    }
    
    public String saveAction() {
        SessionFactory s = HibernateUtil.getSessionFactory();
        for (Place placeList1 : placeList){
            int id = placeList1.getId();
            if (id<0){
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
        Time tclose = new Time(18,0,0);
        placeList.add(new Place("None",0.0, 0, topen, tclose, "nowhere", loginBean.getLoggedId()));
    }
}
