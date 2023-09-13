package com.tum.in.cm.platformservice.repository.primary;

import com.tum.in.cm.platformservice.model.probe.Probe;
import com.tum.in.cm.platformservice.util.Constants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Data repository class used for database access on probes collection
 */
@Repository
public interface ProbeRepository extends MongoRepository<Probe, String> {
    Page<Probe> findByCountryAndStatusOrderByCountryAsc(@Param("country") String country, @Param("status") Constants.ProbeStatus status, @Param("pageable") Pageable pageable);

    Page<Probe> findByCountryOrderByCountryAsc(@Param("country") String country, @Param("pageable") Pageable pageable);

    Page<Probe> findByStatusOrderByStatusAsc(@Param("status") Constants.ProbeStatus status, @Param("pageable") Pageable pageable);

    Page<Probe> findByUserEmail(@Param("userEmail") String userEmail, @Param("pageable") Pageable pageable);

    Page<Probe> findByUserEmailAndCountryAndStatusOrderByCountryAsc(@Param("userEmail") String userEmail, @Param("country") String country, @Param("status") Constants.ProbeStatus status, @Param("pageable") Pageable pageable);

    Page<Probe> findByUserEmailAndCountryOrderByCountryAsc(@Param("userEmail") String userEmail, @Param("country") String country, @Param("pageable") Pageable pageable);

    Page<Probe> findByUserEmailAndStatusOrderByStatusAsc(@Param("userEmail") String userEmail, @Param("status") Constants.ProbeStatus status, @Param("pageable") Pageable pageable);
}
