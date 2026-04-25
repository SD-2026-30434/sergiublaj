package en.sd.model.mail;

import jakarta.validation.constraints.NotBlank;

public record MailRequestDTO(
        @NotBlank(message = "Sender is required and cannot be empty.")
        String from,

        @NotBlank(message = "Receiver is required and cannot be empty.")
        String to,

        @NotBlank(message = "Subject is required and cannot be empty.")
        String subject,

        @NotBlank(message = "Body is required and cannot be empty.")
        String body
) { }
