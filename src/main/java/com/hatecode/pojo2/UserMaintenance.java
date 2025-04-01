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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author trieu
 */
@Entity
@Table(name = "user_maintenance")
@NamedQueries({
    @NamedQuery(name = "UserMaintenance.findAll", query = "SELECT u FROM UserMaintenance u"),
    @NamedQuery(name = "UserMaintenance.findById", query = "SELECT u FROM UserMaintenance u WHERE u.id = :id")})
public class UserMaintenance implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @JoinColumn(name = "maintenance_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private Maintenance maintenanceId;
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    @ManyToOne(optional = false)
    private User userId;

    public UserMaintenance() {
    }

    public UserMaintenance(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Maintenance getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(Maintenance maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
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
        if (!(object instanceof UserMaintenance)) {
            return false;
        }
        UserMaintenance other = (UserMaintenance) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.hatecode.pojo2.UserMaintenance[ id=" + id + " ]";
    }
    
}
