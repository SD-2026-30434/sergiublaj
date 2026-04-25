package en.sd.model.mapper;

import en.sd.model.mail.OrderMailResult;
import en.sd.model.mail.SendOrderMailResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMailResultMapper {

    SendOrderMailResponseDTO toResponse(OrderMailResult result);
}
