package en.sd.model.mapper;

import en.sd.model.mail.ChefMailResult;
import en.sd.model.mail.ChefWelcomeMailResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChefMailResultMapper {

    ChefWelcomeMailResponseDTO toResponse(ChefMailResult result);
}
