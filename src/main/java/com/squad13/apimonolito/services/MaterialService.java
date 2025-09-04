package com.squad13.apimonolito.services;

import com.squad13.apimonolito.models.Material;
import com.squad13.apimonolito.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MaterialService {

    @Autowired
    private MaterialRepository materialRepository;

    public List<Material> findAll(){
        return materialRepository.findAll();
    }

    public Optional<Material> findById(Long id){
        return materialRepository.findById(id);
    }
}
