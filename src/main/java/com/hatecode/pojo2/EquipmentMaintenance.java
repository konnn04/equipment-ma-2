/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.pojo2;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author trieu
 */
@Entity
@Table(name = "equipment_maintenance")
@NamedQueries({
    @NamedQuery(name = "EquipmentMaintenance.findAll", query = "SELECT e FROM EquipmentMaintenance e"),
    @NamedQuery(name = "EquipmentMaintenance.findById", query = "SELECT e FROM EquipmentMaintenance e WHERE e.id = :id"),
    @NamedQuery(name = "EquipmentMaintenance.findByPrice", query = "SELECT e FROM EquipmentMaintenance e WHERE e.price = :price")})
public class EquipmentMaintenance implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Lob
    @Column(name = "description")
    private String description;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "price")
    private Float price;
    @JoinColumn(name = "equipment_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Equipment equipmentId;
    @JoinColumn(name = "maintenance_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Maintenance maintenanceId;
    @JoinColumn(name = "maintenance_type_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private MaintenanceType maintenanceTypeId;

    public EquipmentMaintenance() {
    }

    public EquipmentMaintenance(Integer id) {
        this.id = id;
    }

    public EquipmentMaintenance(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Equipment getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Equipment equipmentId) {
        this.equipmentId = equipmentId;
    }

    public Maintenance getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(Maintenance maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public MaintenanceType getMaintenanceTypeId() {
        return maintenanceTypeId;
    }

    public void setMaintenanceTypeId(MaintenanceType maintenanceTypeId) {
        this.maintenanceTypeId = maintenanceTypeId;
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
        if (!(object instanceof EquipmentMaintenance)) {
            return false;
        }
        EquipmentMaintenance other = (EquipmentMaintenance) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hatecode.pojo2.EquipmentMaintenance[ id=" + id + " ]";
    }
    
}
