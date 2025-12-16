package com.remake.poki.service;

import com.remake.poki.dto.*;
import com.remake.poki.model.*;
import com.remake.poki.repo.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ShopPurchaseHistoryRepository purchaseHistoryRepository;
    private final StoneRepository stoneRepository;
    private final StoneUserRepository stoneUserRepository;
    private final PetRepository petRepository;
    private final UserPetRepository userPetRepository;
    private final AvatarRepository avatarRepository;
    private final UserAvatarRepository userAvatarRepository;
    private final CardRepository cardRepository;
    private final UserCardRepository userCardRepository;

    /**
     * Lấy toàn bộ dữ liệu shop từ database
     */
    public ShopDataResponse getShopData(Long userId) {
        ShopDataResponse response = new ShopDataResponse();

        // Lấy tất cả items active trong shop
        List<Shop> allShopItems = shopRepository.findByIsActiveTrueOrderBySortOrderAsc();

        // Phân loại theo category
        List<ShopItemDTO> items = new ArrayList<>();
        List<ShopPetDTO> pets = new ArrayList<>();
        List<ShopAvatarDTO> avatars = new ArrayList<>();

        for (Shop shop : allShopItems) {
            switch (shop.getItemType()) {
                case "STONE":
                case "ENERGY":
                case "WHEEL":
                case "STAR_WHITE":
                case "STAR_BLUE":
                case "STAR_RED":
                case "CARD":
                    items.add(convertToItemDTO(shop, userId));
                    break;

                case "PET":
                    pets.add(convertToPetDTO(shop, userId));
                    break;

                case "AVATAR":
                    avatars.add(convertToAvatarDTO(shop, userId));
                    break;
            }
        }

        response.setItems(items);
        response.setPets(pets);
        response.setAvatars(avatars);

        return response;
    }

    /**
     * Convert Shop entity sang ShopItemDTO
     */
    private ShopItemDTO convertToItemDTO(Shop shop, Long userId) {
        ShopItemDTO dto = new ShopItemDTO();
        dto.setId(shop.getId().intValue());
        dto.setItemType(shop.getItemType());
        dto.setName(shop.getItemName());
        dto.setPrice(shop.getPrice());
        dto.setCurrencyType(shop.getCurrencyType());
        dto.setValue(shop.getValue() != 0 ? shop.getValue() : 1);

        // For stones
        if ("STONE".equals(shop.getItemType())) {
            dto.setElementType(shop.getElementType() != null ? shop.getElementType().name() : null);
            dto.setLevel(shop.getLevel() != null ? shop.getLevel() : 0);
        }

        // For cards
        if ("CARD".equals(shop.getItemType())) {
            dto.setCardId(shop.getItemId());
        }

        // Check owned - Items không check owned vì có thể mua nhiều lần
        dto.setOwned(false);

        return dto;
    }

    /**
     * Convert Shop entity sang ShopPetDTO
     */
    private ShopPetDTO convertToPetDTO(Shop shop, Long userId) {
        ShopPetDTO dto = new ShopPetDTO();
        dto.setShopId(shop.getId());
        dto.setId(shop.getItemId());
        dto.setName(shop.getItemName());
        dto.setPrice(shop.getPrice());
        dto.setCurrencyType(shop.getCurrencyType());

        // Load pet details
        Pet pet = petRepository.findById(shop.getItemId()).orElse(null);
        if (pet != null) {
            dto.setElementType(pet.getElementType().name());
            // Calculate stats - có thể custom logic này
            dto.setAttack(100);
            dto.setHp(100);
            dto.setMana(100);
        }

        // SỬA: Check trong UserPet thay vì ShopPurchaseHistory
        boolean alreadyPurchased = userPetRepository.existsByUserIdAndPetId(userId, shop.getItemId());

        dto.setPurchaseCount(alreadyPurchased ? 1 : 0);
        dto.setMaxPurchasePerDay(1); // Chỉ mua 1 lần
        dto.setCanPurchase(!alreadyPurchased);

        return dto;
    }

    /**
     * Convert Shop entity sang ShopAvatarDTO
     */
    private ShopAvatarDTO convertToAvatarDTO(Shop shop, Long userId) {
        ShopAvatarDTO dto = new ShopAvatarDTO();
        dto.setId(shop.getItemId());
        dto.setShopId(shop.getId());
        dto.setName(shop.getItemName());
        dto.setPrice(shop.getPrice());
        dto.setCurrencyType(shop.getCurrencyType());

        // Load avatar details
        Avatar avatar = avatarRepository.findById(shop.getItemId()).orElse(null);
        if (avatar != null) {
            dto.setHp(avatar.getHp());
            dto.setAttack(avatar.getAttack());
            dto.setMana(avatar.getMana());
        }

        // SỬA: Check trong UserAvatar thay vì ShopPurchaseHistory
        boolean alreadyPurchased = userAvatarRepository.existsByUserIdAndAvatarId(userId, shop.getItemId());

        dto.setOwned(alreadyPurchased);

        return dto;
    }

    /**
     * Xử lý mua hàng
     */
    @Transactional
    public PurchaseResponse purchaseItem(PurchaseRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Shop shop = shopRepository.findById(request.getShopId())
                .orElseThrow(() -> new RuntimeException("Shop item not found"));

        if (!shop.isActive()) {
            return new PurchaseResponse(false, "Vật phẩm này hiện không có sẵn!", user.getGold(), 0);
        }

        // SỬA: Check if already owned (for pets/avatars) - check trong UserPet/UserAvatar
        if ("PET".equals(shop.getItemType())) {
            boolean alreadyOwned = userPetRepository.existsByUserIdAndPetId(user.getId(), shop.getItemId());
            if (alreadyOwned) {
                return new PurchaseResponse(false, "Bạn đã sở hữu pet này!", user.getGold(), 0);
            }
        } else if ("AVATAR".equals(shop.getItemType())) {
            boolean alreadyOwned = userAvatarRepository.existsByUserIdAndAvatarId(user.getId(), shop.getItemId());
            if (alreadyOwned) {
                return new PurchaseResponse(false, "Bạn đã sở hữu avatar này!", user.getGold(), 0);
            }
        }

        // Check purchase limit (nếu cần cho các item khác)
        if (shop.getMaxPurchasePerDay() != null) {
            LocalDate today = LocalDate.now();
            int purchaseCount = purchaseHistoryRepository.countByUserIdAndShopIdAndPurchaseDateAfter(
                    user.getId(),
                    shop.getId(),
                    today.atStartOfDay()
            );

            if (purchaseCount >= shop.getMaxPurchasePerDay()) {
                return new PurchaseResponse(
                        false,
                        String.format("Bạn chỉ có thể mua tối đa %d lần/ngày!", shop.getMaxPurchasePerDay()),
                        user.getGold(),
                        0
                );
            }
        }

        // Check balance
        int price = shop.getPrice();
        if ("GOLD".equals(shop.getCurrencyType())) {
            if (user.getGold() < price) {
                return new PurchaseResponse(false, "Không đủ vàng!", user.getGold(), 0);
            }
        } else if ("RUBY".equals(shop.getCurrencyType())) {
            if (user.getRuby() < price) {
                return new PurchaseResponse(false, "Không đủ ruby!", user.getRuby(), 0);
            }
        }

        // Deduct currency
        if ("GOLD".equals(shop.getCurrencyType())) {
            user.setGold(user.getGold() - price);
        }
        if ("RUBY".equals(shop.getCurrencyType())) {
            user.setRuby(user.getRuby() - price);
        }

        // Add item to user inventory
        boolean success = addItemToUserInventory(user, shop);

        if (!success) {
            return new PurchaseResponse(false, "Có lỗi khi thêm vật phẩm vào kho!", user.getGold(), 0);
        }

        // Save user
        userRepository.save(user);

        // Record purchase history
        ShopPurchaseHistory history = new ShopPurchaseHistory();
        history.setUserId(user.getId());
        history.setShopId(shop.getId());
        history.setItemType(shop.getItemType());
        history.setItemId(shop.getItemId());
        history.setPricePaid(price);
        history.setCurrencyType(shop.getCurrencyType());
        history.setPurchaseDate(LocalDateTime.now());
        purchaseHistoryRepository.save(history);

        return new PurchaseResponse(true, "Mua thành công!", user.getGold(), 0);
    }

    /**
     * Thêm item vào inventory của user
     */
    private boolean addItemToUserInventory(User user, Shop shop) {
        try {
            switch (shop.getItemType()) {
                case "STONE":
                    return addStoneToUser(user, shop);

                case "ENERGY":
                    user.setEnergy(user.getEnergy() + shop.getValue());
                    return true;

                case "WHEEL":
                    user.setWheel(user.getWheel() + shop.getValue());
                    return true;

                case "STAR_WHITE":
                    user.setStarWhite(user.getStarWhite() + shop.getValue());
                    return true;

                case "STAR_BLUE":
                    user.setStarBlue(user.getStarBlue() + shop.getValue());
                    return true;

                case "STAR_RED":
                    user.setStarRed(user.getStarRed() + shop.getValue());
                    return true;

                case "CARD":
                    return addCardToUser(user, shop);

                case "PET":
                    return addPetToUser(user, shop);

                case "AVATAR":
                    return addAvatarToUser(user, shop);

                default:
                    return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean addStoneToUser(User user, Shop shop) {
        StoneUser existingStone = stoneUserRepository
                .findByIdUserAndIdStone(user.getId(), shop.getItemId())
                .orElse(null);

        if (existingStone != null) {
            existingStone.setCount(existingStone.getCount() + shop.getValue());
            stoneUserRepository.save(existingStone);
        } else {
            StoneUser stoneUser = new StoneUser();
            stoneUser.setIdUser(user.getId());
            stoneUser.setIdStone(shop.getItemId());
            stoneUser.setCount(shop.getValue());
            stoneUserRepository.save(stoneUser);
        }

        return true;
    }

    private boolean addCardToUser(User user, Shop shop) {
        UserCard userCard = userCardRepository.findByUserIdAndCardId(user.getId(), shop.getItemId()).orElse(null);
        if (userCard != null) {
            userCard.setCount(userCard.getCount() + shop.getValue());
            userCardRepository.save(userCard);
            return true;
        }

        userCard = new UserCard();
        userCard.setUserId(user.getId());
        userCard.setCardId(shop.getItemId());
        userCard.setLevel(1);
        userCard.setCount(shop.getValue());
        userCardRepository.save(userCard);

        return true;
    }

    private boolean addPetToUser(User user, Shop shop) {
        UserPet userPet = new UserPet();
        userPet.setUserId(user.getId());
        userPet.setPetId(shop.getItemId());
        userPet.setLevel(1);
        userPetRepository.save(userPet);

        return true;
    }

    private boolean addAvatarToUser(User user, Shop shop) {
        UserAvatar userAvatar = new UserAvatar();
        userAvatar.setUserId(user.getId());
        userAvatar.setAvatarId(shop.getItemId());
        userAvatar.setCreated(LocalDateTime.now());
        userAvatarRepository.save(userAvatar);

        return true;
    }
}