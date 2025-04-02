/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.pojo;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Basic;
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

/**
 *
 * @author trieu
 */
@Entity
@Table(name = "maintenance_type")
@NamedQueries({
    @NamedQuery(name = "MaintenanceType.findAll", query = "SELECT m FROM MaintenanceType m"),
    @NamedQuery(name = "MaintenanceType.findById", query = "SELECT m FROM MaintenanceType m WHERE m.id = :id"),
    @NamedQuery(name = "MaintenanceType.findByName", query = "SELECT m FROM MaintenanceType m WHERE m.name = :name"),
    @NamedQuery(name = "MaintenanceType.findBySuggestPrice", query = "SELECT m FROM MaintenanceType m WHERE m.suggestPrice = :suggestPrice")})
public class MaintenanceType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Lob
    @Column(name = "description")
    private String description;
    @Basic(optional = false)
    @Column(name = "suggest_price")
    private float suggestPrice;
    @OneToMany(mappedBy = "maintenanceTypeId")
    private Set<EquipmentMaintenance> equipmentMaintenanceSet;

    public MaintenanceType() {
    }

    public MaintenanceType(Integer id) {
        this.id = id;
    }

    public MaintenanceType(Integer id, String name, String description, float suggestPrice) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.suggestPrice = suggestPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getSuggestPrice() {
        return suggestPrice;
    }

    public void setSuggestPrice(float suggestPrice) {
        this.suggestPrice = suggestPrice;
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
        if (!(object instanceof MaintenanceType)) {
            return false;
        }
        MaintenanceType other = (MaintenanceType) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hatecode.pojo.MaintenanceType[ id=" + id + " ]";
    }
    
}
