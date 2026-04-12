package en.sd.chefmgmt.controller.user;

import en.sd.chefmgmt.dto.user.UserResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@Tag(name = "User Management", description = "Operations for managing users")
public interface UserController {

    @GetMapping("/me")
    @Operation(summary = "Get current user", description = "Retrieve the currently authenticated user's information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User info retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @ResponseStatus(HttpStatus.OK)
    UserResponseDTO getCurrentUser();
}
