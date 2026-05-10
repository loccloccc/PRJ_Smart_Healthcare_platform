package com.example.smart_healthcare_platform.service.Impl;

import com.example.smart_healthcare_platform.model.Specialty;
import com.example.smart_healthcare_platform.repository.ISpecialtyRepository;
import com.example.smart_healthcare_platform.service.ISpecialtyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpecialtyServiceImpl implements ISpecialtyService {
    private final ISpecialtyRepository specialtyRepository;

    @Override
    public List<Specialty> getSpecialties() {
        return specialtyRepository.findAll();
    }
}
