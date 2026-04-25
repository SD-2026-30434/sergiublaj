package en.sd.service.persistence;

import en.sd.model.domain.Chef;

import java.util.UUID;

public interface ChefService {

    Chef getById(UUID id);
}
