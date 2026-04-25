package en.sd.model.mapper;

import en.sd.model.mail.MailRequest;
import en.sd.model.mail.MailRequestDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MailRequestDTOMapper extends DtoMapper<MailRequest, MailRequestDTO> {
}
