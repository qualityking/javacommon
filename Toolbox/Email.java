package com.automation.toolbox.messaging;

import java.io.File;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
//import javax.activation.*;
public class Email {


	public static boolean send(String to, String subject, String htmlBody) {
		return send(to, subject, htmlBody, null);
	}

	public static boolean send(String to, String subject, String htmlBody,
			String attachment) {

		HtmlEmail email = new HtmlEmail();
		
		email.setHostName("127.0.0.1");
		
		try {
			email.addTo(to);

			email.setFrom("no-reply@hostname.com", "Automation Execution Engine");
			email.setSubject(subject);
			if (attachment != null) {
				File f = new File(attachment);
				email.attach(f);
			}
			email.setHtmlMsg(htmlBody);
			email.send();
			return true;
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}
}
