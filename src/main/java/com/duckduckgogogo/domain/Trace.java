package com.duckduckgogogo.domain;

import java.util.Date;

import javax.persistence.*;

import com.duckduckgogogo.controller.ProjectRestController;

@Entity
@Table(name = "FACE_TRACE_INHOUSE")
public class Trace {
    @Id
    //@GeneratedValue(strategy = GenerationType.SEQUENCE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @OneToOne
    @JoinColumn(name = "STRANGERID")
    private UserInfo user;

    @Column(name = "CAMERAID", length = 100)
    private String cameraid;

    @Column(name = "IMGURL", length = 500)
    private String imgurl;

    @Column(name = "TRACEDATE")
    private Date tracedate;

    @Column(name = "RESULTIDX", length = 100)
    private String resultidx;

    @Column(name = "UPDATESTATUS")
    private boolean updatestatus;

    @Column(name = "SCORE", length = 100)
    private String score;

    public long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null) this.id = id;
    }

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public String getCameraid() {
        return cameraid;
    }

    public void setCameraid(String cameraid) {
        this.cameraid = cameraid;
    }

    public String getImgurl() {
        return "http://" + ProjectRestController.configInfo.getServerIP() + ":" + imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public Date getTracedate() {
        return tracedate;
    }

    public void setTracedate(Date tracedate) {
        this.tracedate = tracedate;
    }

    public String getResultidx() {
        return resultidx;
    }

    public void setResultidx(String resultidx) {
        this.resultidx = resultidx;
    }

    public boolean isUpdatestatus() {
        return updatestatus;
    }

    public void setUpdatestatus(boolean updatestatus) {
        this.updatestatus = updatestatus;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getUserName() {
        return this.user != null ? this.user.getName() : "";
    }

    public String getUserid() {
        return this.user != null ? String.valueOf(this.user.getId()) : "";
    }

    public String getImageid() {
        return this.user != null ? this.user.getImageid() : "";
    }

    public String getVisitorImgurl() {
        return this.user != null ? this.user.getFeature() : "";
    }
}
