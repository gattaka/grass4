package cz.gattserver.grass.core.services.impl;

import java.util.Properties;

import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import cz.gattserver.grass.core.exception.GrassException;
import cz.gattserver.grass.core.services.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailServiceImpl implements MailService {

	private static Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

	private String grassMailAddress;
	private String grassMailPassword;
	private String grassNotificationAddress;

	@Override
	public void sendToAdmin(String subject, String body) {
		sendEmail(grassNotificationAddress, subject, body);
	}

	@Override
	public void sendEmail(String toEmail, String subject, String body) {
		logger.info("Příprava emailu");

		body += "\n\n--\nZasláno systémem GRASS4";

		final String fromEmail = grassMailAddress;
		final String password = grassMailPassword;

		Properties props = new Properties();
		props.put("mail.smtp.timeout", "10000");
		props.put("mail.smtp.connectiontimeout", "10000");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.ssl.enable", "true"); // !!!
		props.put("mail.smtp.ssl.trust", "*");
		props.put("mail.smtp.ssl.protocols", "TLSv1.2");

		Session session = Session.getDefaultInstance(props, new jakarta.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromEmail));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
			message.setSubject(subject);
			message.setText(body);
			Transport.send(message);
			logger.info("Email odeslán");
		} catch (Exception e) {
			String msg = "Nezdařilo se odeslat email";
			logger.error(msg, e);
			throw new GrassException(msg, e);
		}
	}

	public String getGrassMailAddress() {
		return grassMailAddress;
	}

	public void setGrassMailAddress(String grassMailAddress) {
		this.grassMailAddress = grassMailAddress;
	}

	public String getGrassMailPassword() {
		return grassMailPassword;
	}

	public void setGrassMailPassword(String grassMailPassword) {
		this.grassMailPassword = grassMailPassword;
	}

	public String getGrassNotificationAddress() {
		return grassNotificationAddress;
	}

	public void setGrassNotificationAddress(String grassNotificationAddress) {
		this.grassNotificationAddress = grassNotificationAddress;
	}

}
