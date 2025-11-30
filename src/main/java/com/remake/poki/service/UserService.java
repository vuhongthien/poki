package com.remake.poki.service;

import com.remake.poki.dto.*;
import com.remake.poki.model.Card;
import com.remake.poki.model.Pet;
import com.remake.poki.model.User;
import com.remake.poki.model.UserCard;
import com.remake.poki.repo.CardRepository;
import com.remake.poki.repo.PetRepository;
import com.remake.poki.repo.UserCardRepository;
import com.remake.poki.repo.UserRepository;
import com.remake.poki.request.UpdateStarRequest;
import com.remake.poki.response.UpdateStarResponse;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final int ENERGY_REGEN_MINUTES = 8;
    private final UserRepository userRepository;

    @Autowired
    PetRepository petRepository;

    @Autowired
    UserCardRepository userCardRepository;

    @Autowired
    CardRepository cardRepository;

    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public UserDTO getMoney(Long userId) {
        if (userId == 0) {
            userId = 1L;
        }
        Long finalUserId = userId;
        return userRepository.findById(userId)
                .map(user -> modelMapper.map(user, UserDTO.class))
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + finalUserId));
    }

    public UserRoomDTO getInfoRoom(Long userId, Long enemyPetId) {
        UserRoomDTO userRoomDTO = userRepository.findInfoRoom(userId, enemyPetId);
        if (userRoomDTO == null) {
            userRoomDTO = userRepository.findInfoRoomHT(userId, enemyPetId);
        }
        Pet pet = petRepository.findById(enemyPetId).orElseThrow();
        userRoomDTO.setElementType(pet.getElementType().name());
        userRoomDTO.setNameEnemyPetId(pet.getName());

        // Load danh sách thẻ của user
        List<CardDTO> userCards = getUserCards(userId);
        userRoomDTO.setCards(userCards);

        return userRoomDTO;
    }

    /**
     * Lấy danh sách thẻ của user
     */
    public List<CardDTO> getUserCards(Long userId) {
        List<UserCard> userCards = userCardRepository.findByUserId(userId);
        List<CardDTO> cardDTOs = new ArrayList<>();

        for (UserCard userCard : userCards) {
            Optional<Card> cardOpt = cardRepository.findById(userCard.getCardId());
            if (cardOpt.isPresent()) {
                Card card = cardOpt.get();
                CardDTO cardDTO = new CardDTO();
                cardDTO.setId(userCard.getId());
                cardDTO.setCardId(card.getId());
                cardDTO.setName(card.getName());
                cardDTO.setDescription(card.getDescription());
                cardDTO.setElementTypeCard(card.getElementTypeCard().name());
                cardDTO.setValue(card.getValue());
                cardDTO.setMaxLever(card.getMaxLever());
                cardDTO.setCount(userCard.getCount());
                cardDTO.setLevel(userCard.getLevel());
                cardDTO.setConditionUse(card.getConditionUse());

                cardDTOs.add(cardDTO);
            }
        }

        return cardDTOs;
    }

    public UserDTO login(LoginDTO request) {
        User user = userRepository.findByUserAndPassword(request.getUser(), request.getPassword())
                .orElseThrow(() -> new NoSuchElementException("fail info login: " + request.getUser()));
        ;
        return getMoney(user.getId());
    }

    public UserDTO getUser(String userName) {
        if (userName == null) {
            userName = "";
        }
        String finalUserName = userName;
        return userRepository.findByUser(finalUserName)
                .map(user -> modelMapper.map(user, UserDTO.class))
                .orElseThrow(() -> new NoSuchElementException("User not found with user name: " + finalUserName));
    }

    public String downEnergy(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        user.setEnergy(user.getEnergy() - 1);
        userRepository.save(user);
        return "down energy successfully";
    }

    public String upCT(Long userId, int requestAttack) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found with id: " + userId));
        user.setRequestAttack(user.getRequestAttack() + requestAttack);
        userRepository.save(user);
        return "down energy successfully";
    }

    @Transactional
    public DeductGoldResponseDTO deductGold(DeductGoldRequestDTO request) {
        try {
            validateDeductGoldRequest(request);
            User user = findUserById(request.getUserId());
            if (user.getGold() < request.getAmount()) {
                return new DeductGoldResponseDTO(
                        false,
                        "Không đủ gold để thực hiện thao tác này",
                        user.getGold()
                );
            }
            int newGold = user.getGold() - request.getAmount();
            user.setGold(newGold);
            userRepository.save(user);
            return new DeductGoldResponseDTO(
                    true,
                    "Đã trừ " + request.getAmount() + " gold thành công",
                    newGold
            );

        } catch (Exception e) {
            return new DeductGoldResponseDTO(false, "User không tồn tại", 0);
        }
    }

    private void validateDeductGoldRequest(DeductGoldRequestDTO request) {
        if (request.getUserId() <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }

        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Số vàng phải lớn hơn 0");
        }

        if (request.getReason() == null || request.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Lý do trừ vàng không được để trống");
        }
    }

    private User findUserById(int userId) {
        return userRepository.findById((long) userId).orElseThrow();
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User updateWheel(Long userId, int wheelAmount) {
        User user = findById(userId);
        if (user != null) {
            user.setWheel(wheelAmount);
            return save(user);
        }
        return null;
    }

    @Transactional
    public UpdateStarResponse updateStar(UpdateStarRequest request) {
        UpdateStarResponse response = new UpdateStarResponse();

        try {
            // Tìm user
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Cập nhật sao theo loại
            switch (request.getStarType().toLowerCase()) {
                case "white":
                    user.setStarWhite(user.getStarWhite() + request.getAmount());
                    break;
                case "blue":
                    user.setStarBlue(user.getStarBlue() + request.getAmount());
                    break;
                case "red":
                    user.setStarRed(user.getStarRed() + request.getAmount());
                    break;
                default:
                    response.setSuccess(false);
                    response.setMessage("Invalid star type: " + request.getStarType());
                    return response;
            }

            // Lưu user
            user = userRepository.save(user);

            // Trả về response thành công
            response.setSuccess(true);
            response.setMessage("Star updated successfully");
            response.setStarWhite(user.getStarWhite());
            response.setStarBlue(user.getStarBlue());
            response.setStarRed(user.getStarRed());

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("Error updating star: " + e.getMessage());
        }

        return response;
    }

    /**
     * Tính toán và cập nhật năng lượng dựa trên thời gian
     */
    @Transactional
    public User updateEnergy(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Nếu đã đạt max energy thì không tính toán
        if (user.getEnergy() >= user.getEnergyFull()) {
//            user.setEnergy(user.getEnergyFull());
            user.setLastEnergyUpdate(LocalDateTime.now());
            return userRepository.save(user);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastUpdate = user.getLastEnergyUpdate();

        // Nếu chưa có lastUpdate thì set = now
        if (lastUpdate == null) {
            user.setLastEnergyUpdate(now);
            return userRepository.save(user);
        }

        // Tính số phút đã trôi qua
        long minutesPassed = ChronoUnit.MINUTES.between(lastUpdate, now);

        // Tính số năng lượng được hồi
        int energyToAdd = (int) (minutesPassed / ENERGY_REGEN_MINUTES);

        if (energyToAdd > 0) {
            int newEnergy = user.getEnergy() + energyToAdd;

            // Không vượt quá max
//            if (newEnergy > user.getEnergyFull()) {
//                newEnergy = user.getEnergyFull();
//            }

            user.setEnergy(newEnergy);

            // Cập nhật lastUpdate: thời gian thực tế đã hồi (bội số của 8 phút)
            LocalDateTime newLastUpdate = lastUpdate.plusMinutes(energyToAdd * ENERGY_REGEN_MINUTES);
            user.setLastEnergyUpdate(newLastUpdate);

            userRepository.save(user);
        }

        return user;
    }

    /**
     * Reset wheelDay về 2 mỗi ngày lúc 00:00:00
     * Chỉ reset những user có wheelDay < 2
     */
    @Scheduled(cron = "0 0 0 * * ?") // Chạy lúc 00:00:00 mỗi ngày
    @Transactional
    public void resetWheelDay() {
        int updatedCount = userRepository.resetWheelDayForUsersBelow2();
        System.out.printf("updatedCount: %d\n ", updatedCount);
    }

    public long getSecondsUntilNextRegen(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Nếu đã đạt max thì không cần hồi
        if (user.getEnergy() >= user.getEnergyFull()) {
            return 0;
        }

        LocalDateTime lastUpdate = user.getLastEnergyUpdate();
        if (lastUpdate == null) {
            lastUpdate = LocalDateTime.now();
            user.setLastEnergyUpdate(lastUpdate);
            userRepository.save(user);
        }

        LocalDateTime now = LocalDateTime.now();
        long secondsPassed = ChronoUnit.SECONDS.between(lastUpdate, now);
        long secondsPerRegen = ENERGY_REGEN_MINUTES * 60;

        // Tính số giây còn lại trong chu kỳ hiện tại
        long secondsRemaining = secondsPerRegen - (secondsPassed % secondsPerRegen);

        return secondsRemaining;
    }
}