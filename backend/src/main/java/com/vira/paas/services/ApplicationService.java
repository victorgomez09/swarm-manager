package com.vira.paas.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.vira.paas.models.ApplicationModel;
import com.vira.paas.repositories.ApplicationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public List<ApplicationModel> findAll() {
        return applicationRepository.findAll();
    }

    public ApplicationModel create(ApplicationModel payload) {
        return applicationRepository.save(payload);
    }
}
