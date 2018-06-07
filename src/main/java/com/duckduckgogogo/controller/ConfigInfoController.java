package com.duckduckgogogo.controller;

import com.duckduckgogogo.domain.ConfigInfo;
import com.duckduckgogogo.services.ConfigInfoService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/console/faceconfig_management")
public class ConfigInfoController {
    @Autowired
    private ConfigInfoService configInfoService;

    @PostMapping("/save")
    @ResponseBody
    public Map<String, Object> save(@Valid ConfigInfo configInfo) throws Exception {
        Map<String, Object> r = new HashMap<>();

        Map<String, String> message = new HashMap<>();
        configInfo.setServerIP(configInfo.getServerIP().trim());
        configInfo.setImageDBName(configInfo.getImageDBName().trim());
        configInfo.setUserquality(configInfo.getUserquality().trim());
        configInfo.setVisitorquality(configInfo.getVisitorquality().trim());
        configInfo.setSimilarscore(configInfo.getSimilarscore().trim());
        configInfo.setTime1(configInfo.getTime1().trim());
        configInfo.setTime2(configInfo.getTime2().trim());
        configInfo.setTime3(configInfo.getTime3().trim());
        
        // "FAILED" "SUCCEED"

        if (message.isEmpty()) {
        	configInfo.setUpdateDate(new Date());
        	configInfoService.save(configInfo);

            r.put("status", "SUCCEED");
        } else {
            r.put("status", "FAILED");
            r.put("message", message);
        }

        return r;
    }
    
    @RequestMapping("/get/{id}")
    @ResponseBody
    public ConfigInfo get (@PathVariable Integer id) {
    	ConfigInfo configInfo = configInfoService.findById(id.longValue());
    	
        return configInfo;
    }
}
