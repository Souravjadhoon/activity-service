package com.activity_service.activity_service.repository;

import com.activity_service.activity_service.model.ActivitiesStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivitiesStatusRepo extends JpaRepository<ActivitiesStatus,Long> {
}
