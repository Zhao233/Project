package com.duckduckgogogo.utils;

import java.util.Calendar;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

@Service("sendMail")
@EnableAsync 
public class SendMail {
	
	@Autowired
	private MailInfo mailInfo;
	
    @SuppressWarnings("static-access")
    @Async
    public void sendMessage(String smtpHost, String from,
            String fromUserPassword, String to, String subject,
            String messageText, String messageType) throws MessagingException {
    	
    	if(mailInfo.getSendswitch().equals("true"))
    	{	
    		smtpHost = mailInfo.getSmtpHost();
    		from = mailInfo.getFrom();
    		fromUserPassword = mailInfo.getFromUserPassword();
    		
	        // 第一步：配置javax.mail.Session对象
	        System.out.println("为" + smtpHost + "配置mail session对象");
	
	        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	        
	        Properties props = new Properties();
	        props.put("mail.smtp.host", smtpHost);
	        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
            props.setProperty("mail.smtp.socketFactory.fallback", "false");
	        
	        //props.put("mail.smtp.starttls.enable", "true");// 使用 STARTTLS安全连接
	        
	        props.put("mail.smtp.port", "465"); //google使用465或587端口
	        props.setProperty("mail.smtp.socketFactory.port", "465");
	        
	        props.put("mail.smtp.auth", "true"); // 使用验证
	        // props.put("mail.debug", "true");
	        Session mailSession = Session.getInstance(props, new MyAuthenticator(
	                from, fromUserPassword));
	
	        // 第二步：编写消息
	        System.out.println("编写消息from——to:" + from + "——" + to);
	
	        InternetAddress fromAddress = new InternetAddress(from);
	        InternetAddress toAddress = new InternetAddress(to);
	
	        MimeMessage message = new MimeMessage(mailSession);
	
	        message.setFrom(fromAddress);
	        message.addRecipient(RecipientType.TO, toAddress);
	
	        message.setSentDate(Calendar.getInstance().getTime());
	        message.setSubject(subject);
	        message.setContent(messageText, messageType);
	
	        // 第三步：发送消息
	        Transport transport = mailSession.getTransport("smtp");
	        transport.connect(smtpHost, from, fromUserPassword);
	        transport.send(message, message.getRecipients(RecipientType.TO));
	        System.out.println("message yes");
    	}
    }
    
 public static void main(String[] args) {
	 /*
        try {
//            SendMail.sendMessage("smtp.gmail.com", "karemjohn@gmail.com",
//                    "************", "karemjohn@gmail.com", "nihao",
//                    "---------------wrwe-----------",
//                    "text/html;charset=gb2312");
            
        	
        	
        	
            SendMail.sendMessage("smtp.qq.com", "84602016@qq.com",
                    "aiuxjfljisdbbjha", "jiangheng2512@126.com", "nihao",
                    "---------------wrwe-----------",
                    "text/html;charset=gb2312");
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/
    }  
}

class MyAuthenticator extends Authenticator {
    String userName = "";
    String password = "";

    public MyAuthenticator() {

    }

    public MyAuthenticator(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(userName, password);
    }
}