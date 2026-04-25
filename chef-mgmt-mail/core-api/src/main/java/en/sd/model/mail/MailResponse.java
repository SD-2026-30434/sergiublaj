package en.sd.model.mail;

public record MailResponse(String from, String to, SendingStatus status) { }
