/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.pojo2;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author trieu
 */
@Entity
@Table(name = "maintenance")
@NamedQueries({
    @NamedQuery(name = "Maintenance.findAll", query = "SELECT m FROM Maintenance m"),
    @NamedQuery(name = "Maintenance.findById", query = "SELECT m FROM Maintenance m WHERE m.id = :id"),
    @NamedQuery(name = "Maintenance.findByTitle", query = "SELECT m FROM Maintenance m WHERE m.title = :title"),
    @NamedQuery(name = "Maintenance.findByStartDatetime", query = "SELECT m FROM Maintenance m WHERE m.startDatetime = :startDatetime"),
    @NamedQuery(name = "Maintenance.findByEndDatetime", query = "SELECT m FROM Maintenance m WHERE m.endDatetime = :endDatetime"),
    @NamedQuery(name = "Maintenance.findByQuantity", query = "SELECT m FROM Maintenance m WHERE m.quantity = :quantity")})
public class Maintenance implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "title")
    private String title;
    @Basic(optional = false)
    @Lob
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @Column(name = "start_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDatetime;
    @Basic(optional = false)
    @Column(name = "end_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDatetime;
    @Basic(optional = false)
    @Column(name = "quantity")
    private int quantity;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceId")
    private Set<UserMaintenance> userMaintenanceSet;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "maintenanceId")
    private Set<EquipmentMaintenance> equipmentMaintenanceSet;

    public Maintenance() {
    }

    public Maintenance(Integer id) {
        this.id = id;
    }

    public Maintenance(Integer id, String title, String description, Date startDatetime, Date endDatetime, int quantity) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDatetime = startDatetime;
        this.endDatetime = endDatetime;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartDatetime() {
        return startDatetime;
    }

    public void setStartDatetime(Date startDatetime) {
        this.startDatetime = startDatetime;
    }

    public Date getEndDatetime() {
        return endDatetime;
    }

    public void setEndDatetime(Date endDatetime) {
        this.endDatetime = endDatetime;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Set<UserMaintenance> getUserMaintenanceSet() {
        return userMaintenanceSet;
    }

    public void setUserMaintenanceSet(Set<UserMaintenance> userMaintenanceSet) {
        this.userMaintenanceSet = userMaintenanceSet;
    }

    public Set<EquipmentMaintenance> getEquipmentMaintenanceSet() {
        return equipmentMaintenanceSet;
    }

    public void setEquipmentMaintenanceSet(Set<EquipmentMaintenance> equipmentMaintenanceSet) {
        this.equipmentMaintenanceSet = equipmentMaintenanceSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Maintenance)) {
            return false;
        }
        Maintenance other = (Maintenance) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hatecode.pojo2.Maintenance[ id=" + id + " ]";
    }
    
}
