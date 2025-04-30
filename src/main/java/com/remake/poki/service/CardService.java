package com.remake.poki.service;

import com.remake.poki.dto.CardDTO;
import com.remake.poki.model.Card;
import com.remake.poki.repo.CardRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardService {

    private final CardRepository cardRepository;
    final
    ModelMapper modelMapper;

    public CardService(CardRepository cardRepository, ModelMapper modelMapper) {
        this.cardRepository = cardRepository;
        this.modelMapper = modelMapper;
    }

    public List<CardDTO> getCards() {
        List<Card> card = cardRepository.findAll();
        return card.stream().map(c -> modelMapper.map(c, CardDTO.class)).toList();
    }
}
