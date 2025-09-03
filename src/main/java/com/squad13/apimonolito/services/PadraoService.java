package com.squad13.apimonolito.services;

import com.squad13.apimonolito.models.Ambiente;
import com.squad13.apimonolito.models.Padrao;
import com.squad13.apimonolito.repository.PadraoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PadraoService {

    @Autowired
    private PadraoRepository padraoRepository;

    public List<Padrao> findAll() {
        return padraoRepository.findAll();
    }

    public Optional<Padrao> findById(Long id) {
        return padraoRepository.findById(id);
    }
}
