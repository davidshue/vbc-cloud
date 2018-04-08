package z9.cloud.journal;

import org.springframework.data.repository.CrudRepository;

public interface UserEventRepository extends CrudRepository<UserEvent, UserEventKey> {
}
