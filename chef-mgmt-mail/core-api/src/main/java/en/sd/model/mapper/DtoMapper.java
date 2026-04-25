package en.sd.model.mapper;

public interface DtoMapper<Core, Dto> {

    Dto convertCoreToDto(Core core);

    Core convertDtoToCore(Dto dto);
}