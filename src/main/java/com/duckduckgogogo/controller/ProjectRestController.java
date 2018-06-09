package com.duckduckgogogo.controller;

import com.duckduckgogogo.domain.ConfigInfo;
import com.duckduckgogogo.domain.Trace;
import com.duckduckgogogo.domain.UserInfo;
import com.duckduckgogogo.services.ConfigInfoService;
import com.duckduckgogogo.services.TraceService;
import com.duckduckgogogo.services.UserInfoService;
import com.duckduckgogogo.utils.PubMsg;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

import javax.persistence.Column;

@RestController
public class ProjectRestController {

    @Autowired
    private TraceService traceService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private ConfigInfoService configInfoService;

    private static Date opendoordate1 = new Date();
    private static Date opendoordate2;

    public static int detectFlag = 0;

    public static ConfigInfo configInfo;

    @RequestMapping(value = "/api/receive", method = RequestMethod.POST)
    public void receiveResult(@RequestBody String receiveResult) {
        java.util.Date stage1 = new java.util.Date();
        System.out.println("stage 1: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(stage1));

        JSONObject obj = JSONObject.fromObject(receiveResult);

        String type = obj.getString("type");

        boolean isUser = false;

        String user_idx = "";

        if (type.equals("1")) {
            configInfo = configInfoService.findById(1);

            double score = 0;

            String resultIdx = obj.getString("resultIdx");

            JSONObject recogResult = obj.getJSONObject("recogResult");

            String feature = recogResult.getString("feature");

            Date thetime = new java.util.Date(recogResult.getLong("msTime"));

            String showtime = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(thetime);

            String imgUrl = recogResult.getString("imgUrl");

            double qualityScore = recogResult.getDouble("qualityScore");

            String taskIdx = recogResult.getString("taskIdx");

            if (recogResult.containsKey("similars")) {
                JSONObject similars = recogResult.getJSONArray("similars").getJSONObject(0);

                JSONArray userArray = similars.getJSONArray("users");

                UserInfo checkuser;

                for (int i = 0; i < userArray.size(); i++) {
                    if (userArray.getJSONObject(i).getDouble("score") >= Double.valueOf(configInfo.getSimilarscore()) / 100) {
                        //isUser = true;
                        checkuser = userInfoService.findByImageid(userArray.getJSONObject(i).getString("user_idx"));
                        if (checkuser.isUpdatestatus() == false) {
                            user_idx = userArray.getJSONObject(i).getString("user_idx");
                            score = userArray.getJSONObject(i).getDouble("score");
                            break;
                        }
                    } else {
                        break;
                    }
                }

                if (user_idx.equals("")) {
                    JSONObject users = similars.getJSONArray("users").getJSONObject(0);

                    user_idx = users.getString("user_idx");

                    score = users.getDouble("score");
                }


                if (score >= Double.valueOf(configInfo.getSimilarscore()) / 100) {
                    isUser = true;
                } else {
                    isUser = false;
                }

            } else {
                isUser = false;
            }

            if (isUser == true) {
                java.util.Date stagea1 = new java.util.Date();
                System.out.println("stage a1: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(stagea1));
                System.out.println("用户：" + user_idx + "  时间：" + showtime + "  任务ID：" + taskIdx + "  识别结果序号：" + resultIdx + "  人脸图像url地址：" + imgUrl);

                Trace trace = new Trace();

                UserInfo queryuser = userInfoService.findByImageid(user_idx);

                Object a = traceService.queryMaxdate(user_idx);

                long ms = Long.valueOf(configInfo.getTime2()) * 1000;

                if (a != null) {
                    Date c = (java.sql.Timestamp) a;

                    ms = thetime.getTime() - c.getTime();
                }

                if (ms >= Long.valueOf(configInfo.getTime2()) * 1000) {
                    trace.setUser(queryuser);
                    trace.setCameraid(taskIdx);
                    trace.setImgurl(imgUrl.split(":")[2]);
                    trace.setTracedate(thetime);
                    //trace.setResultidx(resultIdx+" "+String.valueOf(score));
                    trace.setResultidx(resultIdx);
                    trace.setScore("" + String.valueOf(score));
                    trace.setUpdatestatus(true);
		    		/*
		    		if(ms >= 15000)
		    		{
		    			trace.setUpdatestatus(true);
		    		}else {
		    			trace.setUpdatestatus(false);
		    		}
		    		*/
                    traceService.save(trace);
                }

                detectFlag = 1;


                if (queryuser.isUpdatestatus() == false) {

                    opendoordate2 = new Date();

                    if ((opendoordate2.getTime() - opendoordate1.getTime() >= Long.valueOf(configInfo.getTime1()) * 1000) && (ms >= Long.valueOf(configInfo.getTime2()) * 1000)) {
                        opendoordate1 = opendoordate2;
                        System.out.println(queryuser.getName() + " " + queryuser.getId() + ":Open the door." + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(opendoordate2));
                        try {
                            PubMsg.publish("message content", "client-id-0", "face/detection");
                            System.out.println(queryuser.getName() + " " + queryuser.getId() + ":The door is opened." + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(opendoordate2));
                        } catch (MqttException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                }


                java.util.Date stagea2 = new java.util.Date();
                System.out.println("stage a2: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(stagea2));
                System.out.println("stage a time: " + (stagea2.getTime() - stagea1.getTime()) + "ms");
            } else {

                java.util.Date stageb1 = new java.util.Date();
                System.out.println("stage b1: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(stageb1));
                System.out.println("未识别：" + user_idx + "  时间：" + showtime + "  任务ID：" + taskIdx + "  识别结果序号：" + resultIdx + "  人脸图像url地址：" + imgUrl);

                if (qualityScore >= Double.valueOf(configInfo.getVisitorquality()) / 100) {
                    //String url = "http://192.168.4.75:80/verify/feature/synAdd";
                    String url = "http://" + configInfo.getServerIP() + ":80/verify/feature/synAdd";

                    RestTemplate rest = new RestTemplate();
                    MultiValueMap<String, Object> param = new LinkedMultiValueMap<String, Object>();
                    param.add("feature", feature);
                    param.add("dbName", configInfo.getImageDBName());

                    String string = rest.postForObject(url, param, String.class);
                    System.out.println(string);

                    JSONObject resultobj = JSONObject.fromObject(string);

                    String result = resultobj.getString("result");

                    if (result.equals("success")) {
                        String featureId = resultobj.getString("featureId");
                        UserInfo userInfo = new UserInfo();
                        userInfo.setImageid(featureId);
                        userInfo.setName("Visitor");
                        //userInfo.setFeature(featureId);
                        userInfo.setFeature(imgUrl.split(":")[2]);
                        userInfo.setUpdatestatus(true);

                        userInfoService.save(userInfo);

                        Trace trace1 = new Trace();
                        trace1.setUser(userInfoService.findByImageid(featureId));
                        trace1.setCameraid(taskIdx);
                        trace1.setImgurl(imgUrl.split(":")[2]);
                        trace1.setTracedate(thetime);
                        //trace1.setResultidx(resultIdx+" "+String.valueOf(score));
                        trace1.setResultidx(resultIdx);
                        trace1.setScore("" + String.valueOf(score));
                        trace1.setUpdatestatus(true);
                        traceService.save(trace1);
                    }
                }

                java.util.Date stageb2 = new java.util.Date();
                System.out.println("stage b2: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(stageb2));
                System.out.println("stage b time: " + (stageb2.getTime() - stageb1.getTime()) + "ms");

            }
        }


        System.out.println("999999999999999999");
        System.out.println(receiveResult);

        java.util.Date stage2 = new java.util.Date();
        System.out.println("stage 2: " + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS").format(stage2));
        System.out.println("stage time: " + (stage2.getTime() - stage1.getTime()) + "ms");


    }
}
