package z9.cloud;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by dshue1 on 6/19/16.
 */
public interface SessionRepository extends MongoRepository<Session, String> {
	List<Session> findByNodeId(String nodeId);

	Long deleteByNodeIdAndZid(String nodeId, String zid);
}