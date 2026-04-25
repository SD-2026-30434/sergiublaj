package en.sd.chefmgmt.mapper;

import en.sd.chefmgmt.service.mailing.client.SendOrderMailRequestDTO;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface SendOrderMailRequestMapper {

    SendOrderMailRequestDTO toRequest(UUID chefId, UUID orderId);
}
