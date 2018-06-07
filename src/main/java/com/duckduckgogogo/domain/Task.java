package com.duckduckgogogo.domain;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "FACE_TASK")
public class Task {
	@Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
	
	@Column(name = "TASKID", length = 100)
    private String taskId;
	
	@Column(name = "RTSPURL", length = 200)
    private String rtspUrl;

    @Column(name = "RECEIVEURL", length = 200)
    private String receiveUrl;

    @Column(name = "DBID", length = 100)
    private String dbId;

    @Column(name = "SCORE", length = 100)
    private String score;
    
    @Column(name = "UPDATEDATE")
    private Date updateDate;
    
    @Column(name = "ENABLED")
    private boolean enabled;

    @Version
    @Column(name = "version")
    private int version;

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null) this.id = id;
    }
    
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public String getRtspUrl() {
        return rtspUrl;
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }
    
    public String getReceiveUrl() {
        return receiveUrl;
    }

    public void setReceiveUrl(String receiveUrl) {
        this.receiveUrl = receiveUrl;
    }
    
    public String getDbId() {
        return dbId;
    }

    public void setDbId(String dbId) {
        this.dbId = dbId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
    
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
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

    public void setVersion(Integer version) {
        if (version != null) this.version = version;
    }
}
