package com.remake.poki.service;

import com.remake.poki.dto.CardDTO;
import com.remake.poki.dto.UserPetDTO;
import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import com.remake.poki.util.Calculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserPetService {
    final
    UserPetRepository userPetRepository;

    @Autowired
    PetRepository petRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    SkillCardRepository skillCardRepository;
    @Autowired
    private AvatarRepository avatarRepository;

    public UserPetService(UserPetRepository userPetRepository) {
        this.userPetRepository = userPetRepository;
    }

    public List<UserPetDTO> getUserPets(Long userId) {
        List<UserPetDTO> pets = userPetRepository.getListUserPets(userId);
        return pets.stream()
                .map(Calculator::calculateStats)
                .collect(Collectors.toList());
    }

    public UserPetDTO getInfoMatch(Long userId, Long petId, Long ePetId) {
        Pet pet = petRepository.findById(ePetId).get();
        UserPetDTO userPetDTO = Calculator.calculateStats(userPetRepository.getInfoMatch(userId, petId));
        User user = userRepository.findById(userId).get();
        Avatar avatar = avatarRepository.findById(user.getAvtId()).orElse(null);
        assert avatar != null;
        userPetDTO.setMana(userPetDTO.getMana()+avatar.getMana());
        userPetDTO.setAttack(userPetDTO.getAttack()+avatar.getAttack());
        userPetDTO.setHp(userPetDTO.getHp()+avatar.getHp());
        user.setPetId(petId);
        userRepository.save(user);
        if(!pet.getElementType().equals(userPetDTO.getElementOther())){
            userPetDTO.setWeaknessValue(BigDecimal.valueOf(1));
        }
        if(userPetDTO.getSkillCardId() == null){
            return userPetDTO;
        }
        Optional<SkillCard> skillCard = skillCardRepository.findById(userPetDTO.getSkillCardId());
        CardDTO cardDTO = new CardDTO();
        cardDTO.setCardId(skillCard.get().getId());
        cardDTO.setName(skillCard.get().getName());
        cardDTO.setConditionUse(Long.parseLong(String.valueOf(skillCard.get().getMana())));
        cardDTO.setValue(skillCard.get().getDame());
        cardDTO.setElementTypeCard(skillCard.get().getElementTypeCard());
        cardDTO.setLevel(userPetDTO.getLevel());
        cardDTO.setPower(skillCard.get().getPower());
        cardDTO.setBlue(skillCard.get().getBlue());
        cardDTO.setGreen(skillCard.get().getGreen());
        cardDTO.setRed(skillCard.get().getRed());
        cardDTO.setWhite(skillCard.get().getWhite());
        cardDTO.setYellow(skillCard.get().getYellow());
        userPetDTO.setCardDTO(cardDTO);
        return userPetDTO;
    }

    public UserPetDTO updatePets(Long userId, Long petId) {
        UserPet userPet = userPetRepository.findByUserIdAndPetId(userId, petId)
                .orElseThrow(() -> new RuntimeException("UserPet not found"));

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Pet not found"));

        if (userPet.getLevel() >= pet.getMaxLevel()) {
            throw new RuntimeException("Pet has reached maximum level");
        }

        userPet.setLevel(userPet.getLevel() + 1);
        userPetRepository.save(userPet);
        return Calculator.calculateStats(userPetRepository.getUserPet(userId,petId));
    }

    public UserPetDTO getUserPetsHT(Long userId, Long petId) {
        return Calculator.calculateStats(userPetRepository.getUserPet(userId, petId));
    }
}
