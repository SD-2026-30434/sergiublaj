package en.sd.chefmgmt.mapper;

import en.sd.chefmgmt.model.mail.ChefWelcomeMailRequestDTO;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface ChefWelcomeMailRequestMapper {

    ChefWelcomeMailRequestDTO toRequest(UUID chefId);
}
