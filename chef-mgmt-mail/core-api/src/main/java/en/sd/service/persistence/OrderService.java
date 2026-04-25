package en.sd.service.persistence;

import en.sd.model.domain.Order;

import java.util.UUID;

public interface OrderService {

    Order getById(UUID id);
}
