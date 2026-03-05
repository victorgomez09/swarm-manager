package com.vira.paas.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vira.paas.models.ApplicationModel;

@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationModel, UUID> {

}
