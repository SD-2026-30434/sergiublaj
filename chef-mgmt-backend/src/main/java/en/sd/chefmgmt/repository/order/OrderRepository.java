package en.sd.chefmgmt.repository.order;

import en.sd.chefmgmt.model.order.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface OrderRepository extends JpaRepository<OrderEntity, UUID>, JpaSpecificationExecutor<OrderEntity> {

    boolean existsByIdAndChefId(UUID id, UUID chefId);
}
