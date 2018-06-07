package com.duckduckgogogo.controller;

import com.duckduckgogogo.domain.Task;
import com.duckduckgogogo.domain.Trace;
import com.duckduckgogogo.domain.TraceTimes;
import com.duckduckgogogo.domain.UserInfo;
import com.duckduckgogogo.services.ConfigInfoService;
import com.duckduckgogogo.services.TraceService;
import com.duckduckgogogo.services.UserInfoService;
import com.duckduckgogogo.utils.PasswordEncodeAssistant;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/console/project_management")
public class ProjectManagementContraller {
    
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private TraceService traceService;
    @Autowired
    private ConfigInfoService configInfoService;

    @RequestMapping("/search")
    @ResponseBody
    public Map<String, Object> search(@RequestParam(value = "search", defaultValue = "") String search,
                                      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                      @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
    	ProjectRestController.configInfo = configInfoService.findById(1);
    	
        Map<String, Object> p = new HashMap<>();

        Pageable pageable = new PageRequest(offset, limit, new Sort(Sort.Direction.DESC, "id"));
        Page<UserInfo> page;
        if ("".equals(search.trim())) {
            page = userInfoService.findAll(pageable);
        } else {
            page = userInfoService.findAll(search.trim(), pageable);
        }

        p.put("total", page != null ? page.getTotalElements() : 0);
        p.put("rows", page != null ? page.getContent() : "");

        return p;
    }
    
    @PostMapping("/upload2")
    @ResponseBody
    public Map<String, Object> upload2(@RequestParam String name, MultipartHttpServletRequest request) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();

        if (name != null && !name.equals("")) {

                MultipartFile mf = request.getFile("file");

                String filename = mf.getOriginalFilename();
                String suffix = filename.substring(filename.indexOf('.') + 1);
                // File.separator
                String folder = System.getProperty("java.io.tmpdir");
                String datetime = String.valueOf(new Date().getTime());
                String target = folder + PasswordEncodeAssistant.encode((datetime + filename).toCharArray()) + "." + suffix;
                File file = new File(target);
                try (FileInputStream fis = (FileInputStream) mf.getInputStream();
                     FileOutputStream fos = new FileOutputStream(target)) {
                    byte[] b = new byte[1024];
                    int i = fis.read(b);
                    while (i > -1) {
                        fos.write(b, 0, b.length);
                        fos.flush();
                        i = fis.read(b);
                    }
                } catch (Exception e) {
                    throw e;
                }
                
                ProjectRestController.configInfo = configInfoService.findById(1);
                
                String url = "http://" + ProjectRestController.configInfo.getServerIP() + ":80/verify/face/detectAndQuality";
        		//String filePath = "D:\\人脸识别\\4b90f603738da977215057e4bb51f8198718e386.jpg";

        		RestTemplate rest = new RestTemplate();
        		FileSystemResource resource = new FileSystemResource(file);
        		MultiValueMap<String, Object> param = new LinkedMultiValueMap<String, Object>();
        		param.add("imageData", resource);

        		String string = rest.postForObject(url, param, String.class);
        		System.out.println(string);
                
                JSONObject resultobj0 = JSONObject.fromObject(string);
	    		
	    		String result = resultobj0.getString("result");
	    		
                
	    		if(result.equals("success"))
	    		{
	    			
	    			double quality_score = resultobj0.getJSONArray("data").getJSONObject(0).getDouble("quality_score");
	    			
	    			if(quality_score >= Double.valueOf(ProjectRestController.configInfo.getUserquality()))
	    			{
	    				
	    				url = "http://" + ProjectRestController.configInfo.getServerIP() + ":80/verify/face/synAdd";
	            		//String filePath = "D:\\人脸识别\\4b90f603738da977215057e4bb51f8198718e386.jpg";

	            		MultiValueMap<String, Object> param1 = new LinkedMultiValueMap<String, Object>();
	            		param1.add("imageDatas", resource);
	            		param1.add("dbName", ProjectRestController.configInfo.getImageDBName());
	            		//param.add("qualityThreshold", 0.9);

	            		string = rest.postForObject(url, param1, String.class);
	            		System.out.println(string);
	            		
	                    JSONObject resultobj = JSONObject.fromObject(string);
	    	    		
	    	    		result = resultobj.getString("result");
    	    		           		
	    	    		if(result.equals("success"))
	    	    		{
	    	    			String featureId = resultobj.getJSONArray("success").getJSONObject(0).getString("imageId");
	    	    			
	    	    			UserInfo userInfo = new UserInfo();
	    	    			userInfo.setImageid(featureId);
	    	    			userInfo.setName(name);
	    	    			//userInfo.setFeature(featureId);
	    	    			//userInfo.setFeature("http://" + ProjectRestController.configInfo.getServerIP() + ":80/verify/face/gets?imageId="+featureId);
	    	    			userInfo.setFeature("80/verify/face/gets?imageId="+featureId);
	    	    			userInfo.setUpdatestatus(false);
	    	    			userInfo.setUpdateDate(new Date());
	    	    			userInfo.setEnabled(true);
	    	    			
	    	    			userInfoService.save(userInfo);
	    	    		}else {
	    	    			message.put("WARNING", "Please check your photo.");
	    	    		}
	    			}else {
	    				message.put("WARNING", "quality_score:"+ quality_score +"(The quality_score of your photo must higher than " + ProjectRestController.configInfo.getUserquality() + ".)");
	    			}
	    			
	    		}else {
	    			message.put("WARNING", "Please check your photo.");    			
	    		}
        		
                
                

        } else {
            message.put("WARNING", "Please enter your name.");
        }

        if (message.isEmpty()) {       	
            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }
    
    @RequestMapping("/search_photolist")
    @ResponseBody
    public List<Trace> searchPhotoList() {
    	ProjectRestController.configInfo = configInfoService.findById(1);
    	
        List<Trace> traces = new ArrayList<>();
        //User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //if (user != null) {
            //user = userService.findById(user.getId());
            //if (user != null && user.getRole().equals(User.ROLE_SUPPLIER)) {
        traces = traceService.findAllList();
               
            //}
        //}

        return traces;
    }
    
    @RequestMapping("/search_photonumber")
    @ResponseBody
    public Map<String, Object> searchPhotoNumber() {
    	Map<String, Object> r = new HashMap<>();
        //Map<String, String> message = new HashMap<>();
    	
        //List<Trace> traces = new ArrayList<>();
        //User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //if (user != null) {
            //user = userService.findById(user.getId());
            //if (user != null && user.getRole().equals(User.ROLE_SUPPLIER)) {
    	//BigDecimal tracenumber = (BigDecimal)traceService.queryCount();
    	BigInteger tracenumber = (BigInteger)traceService.queryCount();
    	
    	//int detectFlag = ProjectRestController.detectFlag;
    	
            //}
        //}
        r.put("status", tracenumber.intValue());
        
        return r;
    }
    
    @RequestMapping("/setdetectflag")
    @ResponseBody
    public Map<String, Object> setDetectFlag() {
    	Map<String, Object> r = new HashMap<>();
        //Map<String, String> message = new HashMap<>();
    	
        //List<Trace> traces = new ArrayList<>();
        //User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //if (user != null) {
            //user = userService.findById(user.getId());
            //if (user != null && user.getRole().equals(User.ROLE_SUPPLIER)) {
    	//BigDecimal tracenumber = (BigDecimal)traceService.queryCount();
    	
    	ProjectRestController.detectFlag = 0;
    	
            //}
        //}
        r.put("status", 0);
        
        return r;
    }
    
    @RequestMapping("/search_latest")
    @ResponseBody
    public Map<String, Object> searchLatest() {
    	ProjectRestController.configInfo = configInfoService.findById(1);
    	
    	Map<String, Object> r = new HashMap<>();
        //Map<String, String> message = new HashMap<>();
    	
        //List<Trace> traces = new ArrayList<>();
        //User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //if (user != null) {
            //user = userService.findById(user.getId());
            //if (user != null && user.getRole().equals(User.ROLE_SUPPLIER)) {
        Trace trace = traceService.findLatest();
               
        //System.out.println("stage 111111: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(new java.util.Date()));
        
            //}
        //}
        //r.put("status", traces.size());
        
        r.put("userid", trace.getUserid());
        r.put("userName", trace.getUserName());
        
        String ScoreString = String.format("%.2f", ((Double.valueOf(trace.getScore())).doubleValue())*100)+"%";
        
        r.put("score", ScoreString);
        r.put("imgurl", trace.getImgurl());
        r.put("imageid", trace.getImageid());
        r.put("visitorImgurl", trace.getVisitorImgurl());
        
        return r;
    }
    
    @RequestMapping("/search_times")
    @ResponseBody
    public List<TraceTimes> searchTimes() {
    	ProjectRestController.configInfo = configInfoService.findById(1);
    	
        List<TraceTimes> traceTimes = new ArrayList<>();
        //User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //if (user != null) {
            //user = userService.findById(user.getId());
            //if (user != null && user.getRole().equals(User.ROLE_SUPPLIER)) {
        
        List<Object[]> queryTimes = traceService.queryTimes();
        
        for(int i=0;i<queryTimes.size();i++)
        {
        	TraceTimes traceTime = new TraceTimes();
        	traceTime.setTimes(Long.valueOf(String.valueOf((queryTimes.get(i))[0])));
        	traceTime.setStrangerid(String.valueOf((queryTimes.get(i))[1]));
        	traceTime.setPersonname(String.valueOf((queryTimes.get(i))[2]));
        	traceTime.setImageid(String.valueOf((queryTimes.get(i))[3]));
        	traceTime.setImgurl("http://" + ProjectRestController.configInfo.getServerIP() + ":"+String.valueOf((queryTimes.get(i))[4]));
        	
        	traceTimes.add(traceTime);
        }
        
               
            //}
        //}

        return traceTimes;
    }
    
    @RequestMapping("/get/{id}")
    @ResponseBody
    public UserInfo get (@PathVariable Integer id) {
    	UserInfo userInfo = userInfoService.findById(id.longValue());
        return userInfo;
    }
    
    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> delete(@RequestParam Integer id) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        
        UserInfo userInfo = userInfoService.findById(id);
        
        ProjectRestController.configInfo = configInfoService.findById(1);
        
        String url = "http://" + ProjectRestController.configInfo.getServerIP() + ":80/verify/face/deletes?dbName="+ProjectRestController.configInfo.getImageDBName()+"&imageId="+userInfo.getImageid();
        
        String param = "{\"dbName\":\"" + ProjectRestController.configInfo.getImageDBName() + "\",\"imageId\":\"" + userInfo.getImageid() + "\"}";
        
        RestTemplate rest = new RestTemplate();

		String string = rest.postForObject(url, null, String.class);
		
		System.out.println(string);
		
		JSONObject obj = JSONObject.fromObject(string);
        
		String returnCode = obj.getString("result");
          
		if(!returnCode.equals("success"))
		{
			message.put("WARNING", "errorMessage:"+obj.getString("errorMessage"));
		}
		
        if (message.isEmpty()) {
        	userInfo.setEnabled(false);
        	userInfo.setUpdateDate(new Date());
        	userInfoService.save(userInfo);
           
            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }
    
    @PostMapping("/update")
    @ResponseBody
    public Map<String, Object> save(@Valid UserInfo userInfo) throws Exception {
        Map<String, Object> r = new HashMap<>();

        UserInfo userInfoQuery = userInfoService.findById(userInfo.getId());
        
        Map<String, String> message = new HashMap<>();
        
        userInfo.setName(userInfo.getName().trim());
        userInfo.setImageid(userInfoQuery.getImageid());
        userInfo.setFeature(userInfoQuery.getFeatureUrl());
        userInfo.setUpdatestatus(userInfoQuery.isUpdatestatus());
        
        // "FAILED" "SUCCEED"

        if (message.isEmpty()) {
        	userInfo.setEnabled(true);
        	userInfo.setUpdateDate(new Date());
        	userInfoService.save(userInfo);

            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }
    
    
    @RequestMapping("/getchart/{id}")
    @ResponseBody
    public Map<String, Object> searchChart(@PathVariable Integer id) {
    	
    	Map<String, Object> r = new HashMap<>();
        
        List<Object[]> queryTimes = traceService.queryTimesChart(id);
        
        for(int i=0;i<queryTimes.size();i++)
        {
        	r.put("h"+Integer.valueOf(String.valueOf((queryTimes.get(i))[1])),String.valueOf((queryTimes.get(i))[0]));
        
        	/*
        	traceTime.setStrangerid(String.valueOf((queryTimes.get(i))[1]));
        	traceTime.setPersonname(String.valueOf((queryTimes.get(i))[2]));
        	traceTime.setImageid(String.valueOf((queryTimes.get(i))[3]));
        	traceTime.setImgurl("http://" + ProjectRestController.configInfo.getServerIP() + ":"+String.valueOf((queryTimes.get(i))[4]));
        	
        	traceTimes.add(traceTime);
        	*/
        }
        
        String data = "[";
        
        for(int j=0;j<=23;j++)
        {
        	if(j==23)
        	{
        		if(r.get("h"+String.valueOf(Integer.valueOf(j))) != null)
            	{
            		data = data + r.get("h"+String.valueOf(Integer.valueOf(j))) + "]";
            	}else{
            		data = data + "0]";
            		r.put("h"+String.valueOf(Integer.valueOf(j)),0);
            	}
        	}else {
        		if(r.get("h"+String.valueOf(Integer.valueOf(j))) != null)
            	{
            		data = data + r.get("h"+String.valueOf(Integer.valueOf(j))) + ",";
            	}else{
            		data = data + "0,";
            		r.put("h"+String.valueOf(Integer.valueOf(j)),0);
            	}
        	}
        	
        }
        
               
String datas = "{" + 
		"	 labels : [\"0\",\"1\",\"2\",\"3\",\"4\",\"5\",\"6\",\"7\",\"8\",\"9\",\"10\",\"11\",\"12\",\"13\",\"14\",\"15\",\"16\",\"17\",\"18\",\"19\",\"20\",\"21\",\"22\",\"23\"]," + 
		"	 datasets : [" + 
		"	 	{" + 
		"	 		fillColor : \"rgba(151,187,205,0.5)\"," + 
		"	 		strokeColor : \"rgba(151,187,205,1)\"," + 
		"	 		pointColor : \"rgba(151,187,205,1)\"," + 
		"	 		pointStrokeColor : \"#fff\"," + 
		"	 		data : " + data +
		"	 	}" + 
		"	 ]" + 
		"" + 
		"	 }";
        

JSONObject obj = JSONObject.fromObject(datas);
            //}
        //}
        r.put("status", obj);
        
        return r;
    }
}
