package en.sd.controller.mail;

import en.sd.model.exception.ExceptionBody;
import en.sd.model.mail.SendOrderMailRequestDTO;
import en.sd.model.mail.SendOrderMailResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping("/v1/mails/orders")
@Tag(name = "Order Mail", description = "Send the order-placed email to the chef who received an order")
public interface OrderMailController {

    @PostMapping
    @Operation(summary = "Send order-placed mail", description = "Looks up the chef and order, renders a Thymeleaf email, and dispatches it via SMTP.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Mail successfully dispatched",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SendOrderMailResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionBody.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ExceptionBody.class)))
    })
    @ResponseStatus(HttpStatus.CREATED)
    SendOrderMailResponseDTO send(@Valid @RequestBody SendOrderMailRequestDTO request);
}
