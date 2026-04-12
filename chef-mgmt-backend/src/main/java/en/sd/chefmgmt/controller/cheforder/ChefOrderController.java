package en.sd.chefmgmt.controller.cheforder;

import en.sd.chefmgmt.dto.CollectionResponseDTO;
import en.sd.chefmgmt.dto.order.OrderFilterDTO;
import en.sd.chefmgmt.dto.order.OrderRequestDTO;
import en.sd.chefmgmt.dto.order.OrderResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@Tag(name = "Chef Order Management", description = "Operations for managing orders under a chef")
public interface ChefOrderController {

    @GetMapping
    @Operation(summary = "Get all orders for a chef", description = "Retrieve all orders that belong to a specific chef.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders retrieved successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = CollectionResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Chef not found")
    })
    @ResponseStatus(HttpStatus.OK)
    CollectionResponseDTO<OrderResponseDTO> findAllByChefId(@PathVariable(name = "chefId") UUID chefId, @Valid OrderFilterDTO orderFilterDTO);

    @PostMapping
    @Operation(summary = "Create order for chef", description = "Add a new order for the specified chef.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Chef not found")
    })
    @ResponseStatus(HttpStatus.CREATED)
    OrderResponseDTO save(@PathVariable(name = "chefId") UUID chefId, @RequestBody @Valid OrderRequestDTO orderRequestDTO);

    @PutMapping("/{id}")
    @Operation(summary = "Update chef order", description = "Update an existing order that belongs to the specified chef.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated successfully",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = OrderResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request body"),
            @ApiResponse(responseCode = "404", description = "Order or chef not found")
    })
    @ResponseStatus(HttpStatus.OK)
    OrderResponseDTO update(@PathVariable(name = "chefId") UUID chefId, @PathVariable(name = "id") UUID id, @RequestBody @Valid OrderRequestDTO orderRequestDTO);

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete chef order", description = "Delete an order that belongs to the specified chef.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Order or chef not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable(name = "chefId") UUID chefId, @PathVariable(name = "id") UUID id);
}
