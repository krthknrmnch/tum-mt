package com.tum.in.cm.platformservice.repository.primary;

import com.tum.in.cm.platformservice.model.user.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Data repository class used for database access on users collection
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(@Param("email") String email);

    boolean existsByEmail(@Param("email") String email);

    void deleteByEmail(@Param("email") String email);
}
