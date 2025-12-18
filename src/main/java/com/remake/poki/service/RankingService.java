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

import java.util.*;
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
    @Autowired
    private AvatarRepository avatarRepository;

    public List<TopRankingDTO> getTop9Ranking() {
        // 1. Lấy top 9 users có level cao nhất và nhiều pet nhất (1 query)
        List<Object[]> top9Users = userRepository.findTop9UsersByLevelAndPetCount();

        if (top9Users.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Lấy userIds của top 9
        List<Long> top9UserIds = top9Users.stream()
                .map(row -> (Long) row[0])
                .collect(Collectors.toList());

        // 3. Lấy TẤT CẢ pets của 9 users này (1 query thay vì 9)
        List<UserPet> userPets = userPetRepository.findByUserIdIn(top9UserIds);

        // 4. Lấy TẤT CẢ pet info cần thiết (1 query)
        List<Long> petIds = userPets.stream()
                .map(UserPet::getPetId)
                .distinct()
                .collect(Collectors.toList());
        List<Pet> pets = petRepository.findAllById(petIds);

        // 5. Tạo Map để tra cứu nhanh O(1)
        Map<Long, List<UserPet>> userPetsMap = userPets.stream()
                .collect(Collectors.groupingBy(UserPet::getUserId));

        Map<Long, Pet> petMap = pets.stream()
                .collect(Collectors.toMap(Pet::getId, pet -> pet));

        // 6. Tính combat power cho 9 users
        List<TopRankingDTO> rankings = new ArrayList<>();

        for (Object[] row : top9Users) {
            Long userId = (Long) row[0];
            String userName = (String) row[1];
            Long currentPetId = (Long) row[2];
            Long avtId = (Long) row[3];
            Integer level = (Integer) row[4];

            List<UserPet> userPetList = userPetsMap.getOrDefault(userId, Collections.emptyList());

            int totalCombatPower = 0;

            for (UserPet userPet : userPetList) {
                Pet pet = petMap.get(userPet.getPetId());
                if (pet == null) continue;

                PetDTO petDTO = createPetDTO(pet);
                UserPetDTO calculatedPet = Calculator.calculateFromPet(petDTO, userPet.getLevel());

                if (calculatedPet != null) {
                    totalCombatPower += (calculatedPet.getAttack() + calculatedPet.getHp());
                }
            }

            TopRankingDTO dto = new TopRankingDTO();
            dto.setUserId(userId);
            dto.setUserName(userName);
            dto.setCurrentPetId(currentPetId);
            dto.setAvtId(avtId);
            dto.setLevel(level);
            dto.setTotalCombatPower(totalCombatPower);

            rankings.add(dto);
        }

        // 7. Sort lại theo combat power (vì có thể thay đổi thứ tự)
        rankings.sort(Comparator.comparingInt(TopRankingDTO::getTotalCombatPower).reversed());

        // 8. Gán rank
        int rank = 1;
        for (TopRankingDTO dto : rankings) {
            dto.setRank(rank++);
        }

        return rankings;
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
                    Avatar avatar = avatarRepository.findById(user.getAvtId()).orElse(null);
                    if (calculatedPet != null) {
                        UserDetailDTO.PetDetailInfo petDetail = new UserDetailDTO.PetDetailInfo();
                        petDetail.setPetId(pet.getId());
                        petDetail.setPetName(pet.getName());
                        petDetail.setLevel(currentUserPet.getLevel());
                        assert avatar != null;
                        petDetail.setAttack(calculatedPet.getAttack()+ avatar.getAttack());
                        petDetail.setHp(calculatedPet.getHp() + avatar.getHp());
                        petDetail.setMana(calculatedPet.getMana()+ avatar.getMana());
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