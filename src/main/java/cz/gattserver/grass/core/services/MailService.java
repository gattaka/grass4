package cz.gattserver.grass.core.services;

public interface MailService {

	void sendToAdmin(String subject, String body);

	void sendEmail(String toEmail, String subject, String body);
}
