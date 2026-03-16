package en.sd.chefmgmt.controller.chef;

import en.sd.chefmgmt.dto.CollectionResponseDTO;
import en.sd.chefmgmt.dto.chef.ChefFilterDTO;
import en.sd.chefmgmt.dto.chef.ChefRequestDTO;
import en.sd.chefmgmt.dto.chef.ChefResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@Tag(name = "Chef Management", description = "Operations for managing chefs")
public interface ChefController {

    @GetMapping
    @Operation(summary = "Get all chefs", description = "Retrieve a list of chefs based on optional filtering criteria.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of chefs retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CollectionResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid filter parameters")
    })
    @ResponseStatus(HttpStatus.OK)
    CollectionResponseDTO<ChefResponseDTO> findAll(@Validated ChefFilterDTO chefFilterDTO);

    @GetMapping("/{id}")
    @Operation(summary = "Get a chef by ID", description = "Retrieve a chef's details using their unique ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chef found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChefResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Chef not found")
    })
    @ResponseStatus(HttpStatus.OK)
    ChefResponseDTO findById(@PathVariable(name = "id") UUID id);

    @PostMapping
    @Operation(summary = "Create a new chef", description = "Add a new chef with the provided details.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Chef created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChefResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @ResponseStatus(HttpStatus.CREATED)
    ChefResponseDTO save(@RequestBody @Valid ChefRequestDTO chefRequestDTO);

    @PutMapping("/{id}")
    @Operation(summary = "Update a chef", description = "Modify an existing chef's details using their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Chef updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ChefResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Chef not found"),
            @ApiResponse(responseCode = "400", description = "Invalid request body")
    })
    @ResponseStatus(HttpStatus.OK)
    ChefResponseDTO update(@PathVariable(name = "id") UUID id, @RequestBody @Valid ChefRequestDTO chefRequestDTO);

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a chef", description = "Remove a chef from the system using their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Chef deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Chef not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable(name = "id") UUID id);
}
