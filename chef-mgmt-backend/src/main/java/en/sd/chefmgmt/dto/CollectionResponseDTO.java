package en.sd.chefmgmt.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record CollectionResponseDTO<T>(
        int pageNumber,
        int pageSize,
        long totalPages,
        long totalElements,
        List<T> elements
) { }