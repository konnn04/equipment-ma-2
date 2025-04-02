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
@Table(name = "equipment_maintenance_status")
@NamedQueries({
    @NamedQuery(name = "EquipmentMaintenanceStatus.findAll", query = "SELECT e FROM EquipmentMaintenanceStatus e"),
    @NamedQuery(name = "EquipmentMaintenanceStatus.findById", query = "SELECT e FROM EquipmentMaintenanceStatus e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentMaintenanceStatus.findByName", query = "SELECT e FROM EquipmentMaintenanceStatus e WHERE e.name = :name")})
public class EquipmentMaintenanceStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Lob
    @Column(name = "desciption")
    private String desciption;
    @OneToMany(mappedBy = "statusId")
    private Set<EquipmentMaintenance> equipmentMaintenanceSet;

    public EquipmentMaintenanceStatus() {
    }

    public EquipmentMaintenanceStatus(Integer id) {
        this.id = id;
    }

    public EquipmentMaintenanceStatus(Integer id, String name) {
        this.id = id;
        this.name = name;
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

    public String getDesciption() {
        return desciption;
    }

    public void setDesciption(String desciption) {
        this.desciption = desciption;
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
        if (!(object instanceof EquipmentMaintenanceStatus)) {
            return false;
        }
        EquipmentMaintenanceStatus other = (EquipmentMaintenanceStatus) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hatecode.pojo.EquipmentMaintenanceStatus[ id=" + id + " ]";
    }
    
}
