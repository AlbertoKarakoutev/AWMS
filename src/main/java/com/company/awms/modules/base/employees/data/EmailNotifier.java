package com.company.awms.modules.base.employees.data;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailNotifier {

	public static void sendMail(String host, String username, String password, String toEmailAddress, String subject, String messageText) {
		Properties properties = new Properties();
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.port", "587");

		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(username));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress));
			msg.setSubject(subject);
			MimeBodyPart mbp = new MimeBodyPart();
			mbp.setText(messageText, "UTF-8", "html");
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mbp);
			msg.setContent(multipart);
			Transport.send(msg);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
