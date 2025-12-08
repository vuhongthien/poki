package com.remake.poki.service;

import com.remake.poki.dto.CardDTO;
import com.remake.poki.response.UseCardResponse;
import com.remake.poki.model.Card;
import com.remake.poki.model.UserCard;
import com.remake.poki.repo.CardRepository;
import com.remake.poki.repo.UserCardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCardService {

    private final UserCardRepository userCardRepository;
    private final CardRepository cardRepository;

    /**
     * Lấy danh sách card của user
     */
    public List<CardDTO> getUserCards(Long userId) {
        List<UserCard> userCards = userCardRepository.findByUserId(userId);
        List<CardDTO> result = new ArrayList<>();

        for (UserCard userCard : userCards) {
            if (userCard.getCount() <= 0) continue;

            Optional<Card> cardOpt = cardRepository.findById(userCard.getCardId());
            if (cardOpt.isPresent()) {
                Card card = cardOpt.get();
                
                CardDTO dto = new CardDTO();
                dto.setId(userCard.getId());
                dto.setCardId(card.getId());
                dto.setName(card.getName());
                dto.setDescription(card.getDescription());
                dto.setElementTypeCard(card.getElementTypeCard().name());
                dto.setValue(card.getValue());
//                dto.setMaxLever(card.getMaxLevel());
                dto.setCount(userCard.getCount());
                dto.setLevel(userCard.getLevel());
                dto.setConditionUse(card.getConditionUse());

                result.add(dto);
            }
        }

        return result;
    }

    /**
     * Sử dụng card - giảm số lượng trong DB
     */
    @Transactional
    public UseCardResponse useCard(Long userId, Long cardId, int quantity) {
        UseCardResponse response = new UseCardResponse();

        Optional<UserCard> userCardOpt = userCardRepository.findByUserIdAndCardId(userId, cardId);
        
        if (userCardOpt.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Card not found");
            response.setRemainingCount(0);
            return response;
        }

        UserCard userCard = userCardOpt.get();

        if (userCard.getCount() < quantity) {
            response.setSuccess(false);
            response.setMessage("Not enough cards");
            response.setRemainingCount(userCard.getCount());
            return response;
        }

        int newCount = userCard.getCount() - quantity;
        userCard.setCount(newCount);
        userCardRepository.save(userCard);

        log.info("User {} used card {}. Remaining: {}", userId, cardId, newCount);

        response.setSuccess(true);
        response.setMessage("OK");
        response.setRemainingCount(newCount);
        return response;
    }
}
