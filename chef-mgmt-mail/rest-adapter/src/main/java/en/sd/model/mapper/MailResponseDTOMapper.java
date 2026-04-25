package en.sd.model.mapper;

import en.sd.model.mail.MailResponse;
import en.sd.model.mail.MailResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MailResponseDTOMapper extends DtoMapper<MailResponse, MailResponseDTO> {
}
