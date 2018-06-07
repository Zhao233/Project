package com.duckduckgogogo.controller;

import com.duckduckgogogo.domain.Task;
import com.duckduckgogogo.services.ConfigInfoService;
import com.duckduckgogogo.services.TaskService;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/console/facetask_management")
public class TaskManagementController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private ConfigInfoService configInfoService;

    @RequestMapping("/search")
    @ResponseBody
    public Map<String, Object> search(@RequestParam(value = "search", defaultValue = "") String search,
                                      @RequestParam(value = "offset", defaultValue = "0") Integer offset,
                                      @RequestParam(value = "limit", defaultValue = "10") Integer limit) {
        Map<String, Object> p = new HashMap<>();

        Pageable pageable = new PageRequest(offset, limit, new Sort(Sort.Direction.DESC, "id"));
        Page<Task> page;
        if ("".equals(search.trim())) {
            page = taskService.findAll(pageable);
        } else {
            page = taskService.findAll(search.trim(), pageable);
        }

        p.put("total", page != null ? page.getTotalElements() : 0);
        p.put("rows", page != null ? page.getContent() : "");

        return p;
    }

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> save(@Valid Task task) throws Exception {
        Map<String, Object> r = new HashMap<>();

        Map<String, String> message = new HashMap<>();
        task.setTaskId(task.getTaskId().trim());
        task.setDbId(task.getDbId().trim());
        task.setRtspUrl(task.getRtspUrl().trim());
        task.setReceiveUrl(task.getReceiveUrl().trim());
        task.setScore(task.getScore().trim());
        
        String url = "";
        String param = "";
        
        ProjectRestController.configInfo = configInfoService.findById(1);
        
        if(task.getTaskId()==null||task.getTaskId().equals(""))
        {
	        url = "http://" + ProjectRestController.configInfo.getServerIP() + ":80/Task/CreateTask?ProjectID=1000";
			
			param = "{\"taskType\":0,\"param\":{\"Source\":{\"ProtoType\":1,\"RtspUrl\":\"" + task.getRtspUrl() + "\",\"SourceType\":2},\"Result\":[{\"PORT\":\"\",\"Index\":0,\"ProtocolType\":10,\"URL\":\"" + task.getReceiveUrl() + "\",\"FilterNoImg\":1},{\"Index\":1,\"ProtocolType\":10,\"URL\":\"" + task.getReceiveUrl() + "\",\"FilterNoImg\":1}],\"Private\":{\"targets\":[{\"dbId\":\"" + task.getDbId() + "\",\"score\":"+ task.getScore() +"}]}}}";
	    }else {
            url = "http://" + ProjectRestController.configInfo.getServerIP() + ":80/Task/UpdateConfig?ProjectID=1000";
			
			param = "{\"taskType\":0,\"taskID\":\"" + task.getTaskId() + "\",\"isFullUpdate\":1,\"fullParam\":{\"Source\":{\"ProtoType\":1,\"RtspUrl\":\"" + task.getRtspUrl() + "\",\"SourceType\":2},\"Result\":[{\"PORT\":\"\",\"Index\":0,\"ProtocolType\":10,\"URL\":\"" + task.getReceiveUrl() + "\",\"FilterNoImg\":1},{\"Index\":1,\"ProtocolType\":10,\"URL\":\"" + task.getReceiveUrl() + "\",\"FilterNoImg\":1}],\"Private\":{\"targets\":[{\"dbId\":\"" + task.getDbId() + "\",\"score\":" + task.getScore() + "}]}}}";
	    }
	    	
		RestTemplate rest = new RestTemplate();

		String string = rest.postForObject(url, param, String.class);
		
		System.out.println(string);
		
		JSONObject obj = JSONObject.fromObject(string);
        
		String returnCode = obj.getString("returnCode");
		
		if(returnCode.equals("0"))
		{
			if(task.getTaskId()==null||task.getTaskId().equals(""))
	        {
				String taskID = obj.getString("taskID");
				task.setTaskId(taskID);
	        }
		}else {
			message.put("WARNING", "returnCode:"+returnCode);
		}
		
        // "FAILED" "SUCCEED"

        if (message.isEmpty()) {
        	task.setEnabled(true);
        	task.setUpdateDate(new Date());
        	taskService.save(task);

            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }

    @RequestMapping("/get/{id}")
    @ResponseBody
    public Task get (@PathVariable Integer id) {
        Task task = taskService.findById(id.longValue());
        return task;
    }
    
    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Object> reOpen(@RequestParam Integer id) throws Exception {
        Map<String, Object> r = new HashMap<>();
        Map<String, String> message = new HashMap<>();
        
        Task task = taskService.findById(id);
        
        ProjectRestController.configInfo = configInfoService.findById(1);
        
        String url = "http://" + ProjectRestController.configInfo.getServerIP() + ":80/Task/DeleteTask";
        String param = "{\"taskID\":\"" + task.getTaskId() + "\"}";
        
        RestTemplate rest = new RestTemplate();

		String string = rest.postForObject(url, param, String.class);
		
		System.out.println(string);
		
		JSONObject obj = JSONObject.fromObject(string);
        
		String returnCode = obj.getString("returnCode");
        
		if(!returnCode.equals("0"))
		{
			message.put("WARNING", "returnCode:"+returnCode);
		}
		
        if (message.isEmpty()) {
        	task.setEnabled(false);
        	task.setUpdateDate(new Date());
        	taskService.save(task);
           
            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }
}
