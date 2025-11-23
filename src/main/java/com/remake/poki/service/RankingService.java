package com.remake.poki.service;

import com.remake.poki.dto.TopRankingDTO;
import com.remake.poki.dto.UserDetailDTO;
import com.remake.poki.dto.UserPetDTO;
import com.remake.poki.dto.PetDTO;
import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import com.remake.poki.util.Calculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RankingService {

    @Autowired
    private RankingRepository rankingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserPetRepository userPetRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetStatsRepository petStatsRepository;

    @Autowired
    private StoneUserRepository stoneUserRepository;

    @Autowired
    private StoneRepository stoneRepository;

    public List<TopRankingDTO> getTop9Ranking() {
        // Lấy tất cả users
        List<User> allUsers = userRepository.findAll();
        List<TopRankingDTO> rankings = new ArrayList<>();

        for (User user : allUsers) {
            // Lấy tất cả pet của user
            List<UserPet> userPets = userPetRepository.findByUserId(user.getId());

            if (userPets.isEmpty()) {
                continue; // Bỏ qua user không có pet
            }

            int totalCombatPower = 0;

            // Tính combat power cho từng pet
            for (UserPet userPet : userPets) {
                // Lấy thông tin Pet base
                Pet pet = petRepository.findById(userPet.getPetId()).orElse(null);

                if (pet == null) continue;

                // Tạo PetDTO từ Pet
                PetDTO petDTO = createPetDTO(pet);

                // Tính stats theo Calculator với level của user_pet
                UserPetDTO calculatedPet = Calculator.calculateFromPet(petDTO, userPet.getLevel());

                // Cộng attack + hp vào tổng lực chiến
                if (calculatedPet != null) {
                    totalCombatPower += (calculatedPet.getAttack() + calculatedPet.getHp());
                }
            }

            // Tạo DTO cho user
            TopRankingDTO dto = new TopRankingDTO();
            dto.setUserId(user.getId());
            dto.setUserName(user.getName());
            dto.setCurrentPetId(user.getPetId());
            dto.setAvtId(user.getAvtId());
            dto.setLevel(user.getLever());
            dto.setTotalCombatPower(totalCombatPower);

            rankings.add(dto);
        }

        // Sort theo totalCombatPower giảm dần
        rankings.sort(Comparator.comparingInt(TopRankingDTO::getTotalCombatPower).reversed());

        // Lấy top 9 và gán rank
        List<TopRankingDTO> top9 = rankings.stream()
                .limit(9)
                .collect(Collectors.toList());

        int rank = 1;
        for (TopRankingDTO dto : top9) {
            dto.setRank(rank++);
        }

        return top9;
    }

    public UserDetailDTO getUserDetail(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetailDTO detail = new UserDetailDTO();
        detail.setUserId(user.getId());
        detail.setUserName(user.getName());
        detail.setLevel(user.getLever());
        detail.setCurrentPetId(user.getPetId());
        detail.setAvtId(user.getAvtId());

        // Lấy thông tin pet đang dùng
        if (user.getPetId() != null) {
            UserPet currentUserPet = userPetRepository.findByUserIdAndPetId(userId, user.getPetId())
                    .orElse(null);

            if (currentUserPet != null) {
                Pet pet = petRepository.findById(user.getPetId()).orElse(null);

                if (pet != null) {
                    PetDTO petDTO = createPetDTO(pet);
                    UserPetDTO calculatedPet = Calculator.calculateFromPet(petDTO, currentUserPet.getLevel());

                    if (calculatedPet != null) {
                        UserDetailDTO.PetDetailInfo petDetail = new UserDetailDTO.PetDetailInfo();
                        petDetail.setPetId(pet.getId());
                        petDetail.setPetName(pet.getName());
                        petDetail.setLevel(currentUserPet.getLevel());
                        petDetail.setAttack(calculatedPet.getAttack());
                        petDetail.setHp(calculatedPet.getHp());
                        petDetail.setMana(calculatedPet.getMana());
                        detail.setCurrentPet(petDetail);
                    }
                }
            }
        }

        // Lấy danh sách tất cả pet
        List<UserPetDTO> getListUserPets = userPetRepository.getListUserPets(userId);
        List<UserDetailDTO.UserPetInfo> petInfos = getListUserPets.stream()
                .map(up -> new UserDetailDTO.UserPetInfo(up.getPetId(), up.getLevel(), up.getElementType()))
                .collect(Collectors.toList());
        detail.setAllPets(petInfos);

        // Lấy danh sách đá - Sắp xếp theo hệ (elementType) và level từ thấp đến cao
        List<StoneUser> stoneUsers = stoneUserRepository.findByIdUser(userId);
        List<UserDetailDTO.StoneInfo> stoneInfos = new ArrayList<>();

        for (StoneUser stoneUser : stoneUsers) {
            Stone stone = stoneRepository.findById(stoneUser.getIdStone()).orElse(null);
            if (stone != null && stoneUser.getCount() > 0) {
                UserDetailDTO.StoneInfo stoneInfo = new UserDetailDTO.StoneInfo();
                stoneInfo.setStoneId(stone.getId());
                stoneInfo.setStoneName(stone.getName());
                stoneInfo.setCount(stoneUser.getCount());
                stoneInfo.setLevel(stone.getLever());
                stoneInfo.setElementType(stone.getElementType()); // Thêm elementType
                if(stoneUser.getCount() > 0){
                    stoneInfos.add(stoneInfo);
                }
            }
        }

        // Sắp xếp: Theo elementType trước, sau đó theo level (thấp đến cao)
        stoneInfos.sort(Comparator
                .comparing(UserDetailDTO.StoneInfo::getElementType, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparingInt(UserDetailDTO.StoneInfo::getLevel));

        detail.setStones(stoneInfos);

        return detail;
    }

    /**
     * Helper: Convert Pet entity sang PetDTO để dùng với Calculator
     */
    private PetDTO createPetDTO(Pet pet) {
        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setElementType(pet.getElementType());
        dto.setMaxLevel(pet.getMaxLevel());
        dto.setDes(pet.getDes());

        // Lấy base stats từ pet_stats ở level 1
        PetStats baseStats = petStatsRepository.findByPetIdAndLevel(pet.getId(), 1).orElse(null);

        if (baseStats != null) {
            dto.setAttack(baseStats.getAttack());
            dto.setHp(baseStats.getHp());
            dto.setMana(baseStats.getMana());
            dto.setWeaknessValue(baseStats.getWeaknessValue());
        } else {
            // Fallback: nếu không có level 1, set default
            dto.setAttack(0);
            dto.setHp(0);
            dto.setMana(0);
            dto.setWeaknessValue(java.math.BigDecimal.ONE);
        }

        return dto;
    }
}