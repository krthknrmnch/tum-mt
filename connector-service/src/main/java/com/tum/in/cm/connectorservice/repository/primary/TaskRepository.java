package com.tum.in.cm.connectorservice.repository.primary;

import com.tum.in.cm.connectorservice.model.task.Task;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data repository class used for database access on tasks collection
 */
@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    Optional<Task> findFirstByProbeIdOrderByScheduledStartTimestampAsc(@Param("probeId") String probeId);

    List<Task> findAllByProbeId(@Param("probeId") String probeId);
}
