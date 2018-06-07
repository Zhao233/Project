package com.duckduckgogogo.domain;

public class TraceTimes {

    private long times;

    private String strangerid;

    private String personname;
    
    private String imageid;
    
    private String imgurl;
    
    public long getTimes() {
        return times;
    }

    public void setTimes(Long times) {
        if (times != null) this.times = times;
    }
    
    public String getStrangerid() {
        return strangerid;
    }

    public void setStrangerid(String strangerid) {
        this.strangerid = strangerid;
    }

    public String getPersonname() {
        return personname;
    }

    public void setPersonname(String personname) {
        this.personname = personname;
    }
    
    public String getImageid() {
        return imageid;
    }

    public void setImageid(String imageid) {
        this.imageid = imageid;
    }
    
    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }
    
    public String getShowtimes() {
    	return "ID:"+this.strangerid+"<br/>"+"Name:"+this.personname+"<br/>"+"Time:"+this.times;
    }
}
