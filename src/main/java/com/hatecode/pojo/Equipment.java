/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hatecode.pojo;

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
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
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
@Table(name = "equipment")
@NamedQueries({
    @NamedQuery(name = "Equipment.findAll", query = "SELECT e FROM Equipment e"),
    @NamedQuery(name = "Equipment.findById", query = "SELECT e FROM Equipment e WHERE e.id = :id"),
    @NamedQuery(name = "Equipment.findByCode", query = "SELECT e FROM Equipment e WHERE e.code = :code"),
    @NamedQuery(name = "Equipment.findByName", query = "SELECT e FROM Equipment e WHERE e.name = :name"),
    @NamedQuery(name = "Equipment.findByImportDate", query = "SELECT e FROM Equipment e WHERE e.importDate = :importDate"),
    @NamedQuery(name = "Equipment.findByRegularMaintenanceTime", query = "SELECT e FROM Equipment e WHERE e.regularMaintenanceTime = :regularMaintenanceTime"),
    @NamedQuery(name = "Equipment.findByLastMaintenanceTime", query = "SELECT e FROM Equipment e WHERE e.lastMaintenanceTime = :lastMaintenanceTime")})
public class Equipment implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @Column(name = "code")
    private String code;
    @Basic(optional = false)
    @Column(name = "name")
    private String name;
    @Basic(optional = false)
    @Column(name = "import_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date importDate;
    @Basic(optional = false)
    @Column(name = "regular_maintenance_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date regularMaintenanceTime;
    @Basic(optional = false)
    @Column(name = "last_maintenance_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastMaintenanceTime;
    @Lob
    @Column(name = "description")
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "equipmentId")
    private Set<EquipmentMaintenance> equipmentMaintenanceSet;
    @JoinColumn(name = "category", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Category category;
    @JoinColumn(name = "status", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Status status;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "equipmentId")
    private Set<EquipmentImage> equipmentImageSet;

    public Equipment() {
    }

    public Equipment(Integer id) {
        this.id = id;
    }

    public Equipment(Integer id, String code, String name, Date importDate, Date regularMaintenanceTime, Date lastMaintenanceTime) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.importDate = importDate;
        this.regularMaintenanceTime = regularMaintenanceTime;
        this.lastMaintenanceTime = lastMaintenanceTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getImportDate() {
        return importDate;
    }

    public void setImportDate(Date importDate) {
        this.importDate = importDate;
    }

    public Date getRegularMaintenanceTime() {
        return regularMaintenanceTime;
    }

    public void setRegularMaintenanceTime(Date regularMaintenanceTime) {
        this.regularMaintenanceTime = regularMaintenanceTime;
    }

    public Date getLastMaintenanceTime() {
        return lastMaintenanceTime;
    }

    public void setLastMaintenanceTime(Date lastMaintenanceTime) {
        this.lastMaintenanceTime = lastMaintenanceTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<EquipmentMaintenance> getEquipmentMaintenanceSet() {
        return equipmentMaintenanceSet;
    }

    public void setEquipmentMaintenanceSet(Set<EquipmentMaintenance> equipmentMaintenanceSet) {
        this.equipmentMaintenanceSet = equipmentMaintenanceSet;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<EquipmentImage> getEquipmentImageSet() {
        return equipmentImageSet;
    }

    public void setEquipmentImageSet(Set<EquipmentImage> equipmentImageSet) {
        this.equipmentImageSet = equipmentImageSet;
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
        if (!(object instanceof Equipment)) {
            return false;
        }
        Equipment other = (Equipment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hatecode.pojo.Equipment[ id=" + id + " ]";
    }
    
}
