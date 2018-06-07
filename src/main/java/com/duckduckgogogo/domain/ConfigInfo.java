package com.duckduckgogogo.domain;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "FACE_CONFIG_INFO")
public class ConfigInfo {
	@Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
	
    @Column(name = "SERVERIP", length = 100)
    private String serverIP;
    
    @Column(name = "IMAGEDBNAME", length = 100)
    private String imageDBName;

	@Column(name = "USERQUALITY", length = 100)
    private String userquality;

    @Column(name = "VISITORQUALITY", length = 100)
    private String visitorquality;
    
    @Column(name = "SIMILARSCORE", length = 100)
    private String similarscore;
    
    @Column(name = "TIME1", length = 100)
    private String time1;
    
    @Column(name = "TIME2", length = 100)
    private String time2;
    
    @Column(name = "TIME3", length = 100)
    private String time3;

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
    
    public String getServerIP() {
		return serverIP;
	}

	public void setServerIP(String serverIP) {
		this.serverIP = serverIP;
	}

	public String getImageDBName() {
		return imageDBName;
	}

	public void setImageDBName(String imageDBName) {
		this.imageDBName = imageDBName;
	}
	
	public String getUserquality() {
		return userquality;
	}

	public void setUserquality(String userquality) {
		this.userquality = userquality;
	}

	public String getVisitorquality() {
		return visitorquality;
	}

	public void setVisitorquality(String visitorquality) {
		this.visitorquality = visitorquality;
	}

	public String getSimilarscore() {
		return similarscore;
	}

	public void setSimilarscore(String similarscore) {
		this.similarscore = similarscore;
	}

	public String getTime1() {
		return time1;
	}

	public void setTime1(String time1) {
		this.time1 = time1;
	}

	public String getTime2() {
		return time2;
	}

	public void setTime2(String time2) {
		this.time2 = time2;
	}

	public String getTime3() {
		return time3;
	}

	public void setTime3(String time3) {
		this.time3 = time3;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	
	public int getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        if (version != null) this.version = version;
    }
}
