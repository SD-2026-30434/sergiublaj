package en.sd.model.mail;

public record MailResponseDTO(String from, String to, SendingStatusDTO status) { }