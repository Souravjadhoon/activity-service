package com.activity_service.activity_service.repository;

import com.activity_service.activity_service.model.ActivitiesRequestMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivitiesRepo extends JpaRepository<ActivitiesRequestMessage,Long> {
    List<ActivitiesRequestMessage> findAllByEndToEndReferenceId(String id);
}
