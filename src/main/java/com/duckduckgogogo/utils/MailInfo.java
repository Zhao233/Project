package com.duckduckgogogo.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2017/5/28.
 */
@RestController
@Component("mailInfo")
public class MailInfo {

	public MailInfo() {

	}

	// 获取配置文件中的age
	@Value("${age}")
	private int age;

	// 获取配置文件中的name
	@Value("${name}")
	private String name;

	// 获取配置文件中的manInfo
	@Value("${manInfo}")
	private String manInfo;
	
	@Value("${mail.sendswitch}")
	private String sendswitch;
	
	@Value("${mail.smtpHost}")
	private String smtpHost;
	
	@Value("${mail.from}")
	private String from;
	
	@Value("${mail.fromUserPassword}")
	private String fromUserPassword;

	@RequestMapping(value = "/getAge", method = RequestMethod.GET)
	public int getAge() {
		return age;
	}

	@RequestMapping(value = "/getName", method = RequestMethod.GET)
	public String getName() {
		return name;
	}

	@RequestMapping(value = "/getManInfo", method = RequestMethod.GET)
	public String getManInfo() {
		return manInfo;
	}
	
	@RequestMapping(value = "/getSendswitch", method = RequestMethod.GET)
	public String getSendswitch() {
		return sendswitch;
	}
	
	@RequestMapping(value = "/getSmtpHost", method = RequestMethod.GET)
	public String getSmtpHost() {
		return smtpHost;
	}
	
	@RequestMapping(value = "/getFrom", method = RequestMethod.GET)
	public String getFrom() {
		return from;
	}
	
	@RequestMapping(value = "/getFromUserPassword", method = RequestMethod.GET)
	public String getFromUserPassword() {
		return fromUserPassword;
	}
}