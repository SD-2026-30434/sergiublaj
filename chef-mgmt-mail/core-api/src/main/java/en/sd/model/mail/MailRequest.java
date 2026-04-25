package en.sd.model.mail;

public record MailRequest(String from, String to, String subject, String body) { }
