package com.squad13.apimonolito.services;

import com.squad13.apimonolito.models.TipoItem;
import com.squad13.apimonolito.repository.TipoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoService {

    private TipoRepository tipoRepository;

    public List<TipoItem> findAll(){
        return tipoRepository.findAll();
    }

    public Optional<TipoItem> findById(Long id){
        return tipoRepository.findById(id);
    }
}
