package z9.cloud;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by dshue1 on 6/19/16.
 */
public interface RevivalRepository extends MongoRepository<Revival, String> {
	List<Revival> findByZ9SessionId(String z9SessionId);

	Long deleteByZ9SessionId(String z9SessionId);
}