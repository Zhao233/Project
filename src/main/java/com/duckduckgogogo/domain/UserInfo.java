package com.duckduckgogogo.domain;

import java.util.Date;

import javax.persistence.*;

import com.duckduckgogogo.controller.ProjectRestController;

@Entity
@Table(name = "FACE_INHOUSE")
public class UserInfo {
    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "NAME", length = 100)
    private String name;

    @Column(name = "IMAGEID", length = 100)
    private String imageid;

    @Column(name = "FEATURE", length = 3000)
    private String feature;

    @Column(name = "UPDATESTATUS")
    private boolean updatestatus;
    
    @Column(name = "ENABLED")
    private boolean enabled;
    
    @Column(name = "UPDATEDATE")
    private Date updateDate;

    @Version
    @Column(name = "version")
    private int version;
    
    public long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null) this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }

    public String getFeature() {
        return "http://" + ProjectRestController.configInfo.getServerIP() + ":"+feature;
    }
    
    public String getFeatureUrl() {
        return feature;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public boolean isUpdatestatus() {
        return updatestatus;
    }

    public void setUpdatestatus(boolean updatestatus) {
        this.updatestatus = updatestatus;
    }

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public int getVersion() {
        return version;
    }

	public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
	
    public void setVersion(Integer version) {
        if (version != null) this.version = version;
    }
	
	public long getUserid() {
		return id;
    }
}
