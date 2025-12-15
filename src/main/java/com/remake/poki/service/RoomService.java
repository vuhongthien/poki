package com.remake.poki.service;

import com.remake.poki.dto.*;
import com.remake.poki.model.Card;
import com.remake.poki.model.Pet;
import com.remake.poki.model.UserCard;
import com.remake.poki.repo.*;
import com.remake.poki.util.Calculator;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.security.SecureRandom;
import java.util.stream.Collectors;

@Service
public class RoomService {

    private final Map<Long, RoomDTO> rooms = new ConcurrentHashMap<>();
    private final SecureRandom random = new SecureRandom();

    private static final int ROOM_ID_LENGTH = 5;
    private static final long MIN_ROOM_ID = 10000L;
    private static final long MAX_ROOM_ID = 99999L;
    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final UserCardRepository userCardRepository;
    private final CardRepository cardRepository;
    private final UserPetRepository userPetRepository;

    public RoomService(UserRepository userRepository, PetRepository petRepository, UserCardRepository userCardRepository, CardRepository cardRepository, UserPetRepository userPetRepository) {
        this.userRepository = userRepository;
        this.petRepository = petRepository;
        this.userCardRepository = userCardRepository;
        this.cardRepository = cardRepository;
        this.userPetRepository = userPetRepository;
    }

    private Long generateRoomId() {
        long roomId = MIN_ROOM_ID + (long)(random.nextDouble() * (MAX_ROOM_ID - MIN_ROOM_ID + 1));
        return roomId;
    }

    private Long generateUniqueRoomId() {
        Long roomId;
        int maxAttempts = 100;
        int attempts = 0;

        do {
            roomId = generateRoomId();
            attempts++;

            if (attempts >= maxAttempts) {
                throw new RuntimeException("Cannot generate unique room ID after " + maxAttempts + " attempts");
            }
        } while (rooms.containsKey(roomId));

        return roomId;
    }

    public RoomDTO createRoom(RoomDTO existingRoom, Long hostUserId, String hostUsername,
                              String avatarId, int level) {

        Long roomId = generateUniqueRoomId();
        RoomDTO room = new RoomDTO();

        room.setId(existingRoom.getId());
        room.setEnergy(existingRoom.getEnergy());
        room.setEnergyFull(existingRoom.getEnergyFull());
        room.setCount(existingRoom.getCount());
        room.setRequestPass(existingRoom.getRequestPass());
        room.setRequestAttack(existingRoom.getRequestAttack());
        room.setName(existingRoom.getName());
        room.setLever(existingRoom.getLever());
        room.setPetId(existingRoom.getPetId());
        room.setEnemyPetId(existingRoom.getEnemyPetId());
        room.setNameEnemyPetId(existingRoom.getNameEnemyPetId());
        room.setElementType(existingRoom.getElementType());
        room.setRoomId(roomId);
        room.setHostUserId(hostUserId);
        room.setHostUsername(hostUsername);
        room.setStatus("WAITING");
        room.setMaxPlayers(2);

        List<UserCard> userCards = userCardRepository.findByUserId(hostUserId);
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
        List<UserPetDTO> pets = userPetRepository.getListUserPets(hostUserId).stream()
                .map(Calculator::calculateStats)
                .toList();
        RoomMemberDTO host = new RoomMemberDTO(
                hostUserId,
                hostUsername,
                avatarId,
                level,
                true,
                existingRoom.getEnergy(),
                existingRoom.getEnergyFull(),
                existingRoom.getCount(),
                existingRoom.getRequestPass(),
                existingRoom.getRequestAttack(),
                existingRoom.getPetId(),
                existingRoom.getEnemyPetId(),
                existingRoom.getNameEnemyPetId(),
                existingRoom.getElementType(),
                cardDTOs, pets
        );

        List<RoomMemberDTO> members = new ArrayList<>();
        members.add(host);
        room.setMembers(members);
        rooms.put(roomId, room);

        System.out.println("✅ Room created with ID: " + roomId);
        return room;
    }

    /**
     * ✅ FIX: LOAD CARDS CHO MEMBER MỚI KHI JOIN
     */
    public RoomDTO addMember(Long roomId, RoomMemberDTO newMember) {
        RoomDTO room = rooms.get(roomId);
        if (room == null) {
            throw new RuntimeException("Room not found: " + roomId);
        }

        if (room.isFull()) {
            throw new RuntimeException("Room is full");
        }

        if (room.getMembers() == null) {
            room.setMembers(new ArrayList<>());
        }

        boolean alreadyInRoom = room.getMembers().stream()
                .anyMatch(m -> m.getUserId().equals(newMember.getUserId()));

        if (alreadyInRoom) {
            return room;
        }

        UserRoomDTO userRoomDTO = userRepository.findInfoRoom(newMember.getUserId(), (long) room.getEnemyPetId());
        newMember.setEnergy(userRoomDTO.getEnergy());
        newMember.setEnergyFull(userRoomDTO.getEnergyFull());
        newMember.setCount(userRoomDTO.getCount());
        newMember.setRequestPass(userRoomDTO.getRequestPass());
        newMember.setElementType(userRoomDTO.getElementType());
        newMember.setPetId(Math.toIntExact(userRoomDTO.getPetId()));
        newMember.setUsername(userRoomDTO.getName());
        newMember.setAvatarId(String.valueOf(userRoomDTO.getAvtId()));
        newMember.setLevel(userRoomDTO.getLever());
        newMember.setEnemyPetId(Math.toIntExact(userRoomDTO.getEnemyPetId()));
        newMember.setNameEnemyPetId(userRoomDTO.getNameEnemyPetId());
        List<UserPetDTO> pets = userPetRepository.getListUserPets(newMember.getUserId()).stream()
                .map(Calculator::calculateStats)
                .toList();
        // ✅ **KEY FIX**: LOAD CARDS FOR NEW MEMBER
        List<UserCard> userCards = userCardRepository.findByUserId(newMember.getUserId());
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

        newMember.setCards(cardDTOs);
        newMember.setUserPets(pets);
        System.out.println("✅ Loaded " + cardDTOs.size() + " cards for member userId=" + newMember.getUserId());

        room.getMembers().add(newMember);
        return room;
    }

    public RoomDTO addMember(Long roomId, Long userId, String username,
                             String avatarId, int level) {
        RoomMemberDTO member = new RoomMemberDTO(userId, username, avatarId, level, false);
        return addMember(roomId, member);
    }

    /**
     * ✅ LẤY TẤT CẢ MEMBERS TRƯỚC KHI XOÁ PHÒNG
     */
    public List<RoomMemberDTO> getAllMembersBeforeDelete(Long roomId) {
        RoomDTO room = rooms.get(roomId);
        if (room == null || room.getMembers() == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(room.getMembers());
    }

    /**
     * ✅ XOÁ PHÒNG HOÀN TOÀN
     */
    public void deleteRoom(Long roomId) {
        rooms.remove(roomId);
        System.out.println("❌ Room " + roomId + " has been deleted");
    }

    public RoomDTO removeMember(Long roomId, Long userId) {
        RoomDTO room = rooms.get(roomId);
        if (room == null) return null;

        if (room.getMembers() != null) {
            room.getMembers().removeIf(m -> m.getUserId().equals(userId));
        }

        // ✅ NẾU HOST RỜI → XOÁ PHÒNG
        if (room.getHostUserId().equals(userId)) {
            rooms.remove(roomId);
            return null; // ← Trả về null = phòng đã bị xoá
        }

        return room;
    }

    public RoomDTO setMemberReady(Long roomId, Long userId, boolean ready) {
        RoomDTO room = rooms.get(roomId);
        if (room == null) return null;

        if (room.getMembers() != null) {
            room.getMembers().stream()
                    .filter(m -> m.getUserId().equals(userId))
                    .findFirst()
                    .ifPresent(m -> m.setReady(ready));
        }

        return room;
    }

    public boolean isAllReady(Long roomId) {
        RoomDTO room = rooms.get(roomId);
        if (room == null || room.getMembers() == null || room.getMembers().isEmpty()) {
            return false;
        }

        // ✅ NẾU CHỈ CÓ 1 NGƯỜI (HOST SOLO) → LUÔN CHO PHÉP START
        if (room.getMembers().size() == 1) {
            return true;
        }

        // ✅ NẾU 2+ NGƯỜI → CHỈ KIỂM TRA MEMBERS (KHÔNG PHẢI HOST)
        // Host luôn sẵn sàng, chỉ cần tất cả members khác ready
        long nonHostReadyCount = room.getMembers().stream()
                .filter(m -> !m.isHost())  // Chỉ đếm members
                .filter(RoomMemberDTO::isReady)
                .count();

        long totalNonHostMembers = room.getMembers().stream()
                .filter(m -> !m.isHost())
                .count();

        // Nếu không có member nào (chỉ có host) → true
        if (totalNonHostMembers == 0) {
            return true;
        }

        // Tất cả members đã ready
        return nonHostReadyCount == totalNonHostMembers;
    }

    public RoomDTO startMatch(Long roomId) {
        RoomDTO room = rooms.get(roomId);
        if (room == null) return null;

        room.setStatus("IN_MATCH");
        return room;
    }

    public RoomDTO getRoom(Long roomId) {
        return rooms.get(roomId);
    }

    public Long getUserRoomId(Long userId) {
        return rooms.values().stream()
                .filter(room -> room.getMembers() != null && room.getMembers().stream()
                        .anyMatch(m -> m.getUserId().equals(userId)))
                .map(RoomDTO::getRoomId)
                .findFirst()
                .orElse(null);
    }

    public List<RoomDTO> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public RoomDTO getInfoPlayer(RoomDTO room, RoomMemberDTO member) {
        UserRoomDTO userRoomDTO = userRepository.findInfoRoom(member.getUserId(), (long) room.getEnemyPetId());
        member.setEnergy(userRoomDTO.getEnergy());
        member.setEnergyFull(userRoomDTO.getEnergyFull());
        member.setCount(userRoomDTO.getCount());
        member.setRequestPass(userRoomDTO.getRequestPass());
        member.setElementType(userRoomDTO.getElementType());
        member.setPetId(Math.toIntExact(userRoomDTO.getPetId()));
        return room;
    }

    public void removeRoom(Long roomId) {
        rooms.remove(roomId);
    }

    public RoomDTO updateMemberPet(Long roomId, Long userId, int petId) {
        RoomDTO room = rooms.get(roomId);
        if (room == null) {
            System.err.println("[RoomService] Room not found: " + roomId);
            return null;
        }

        if (room.getMembers() == null) {
            System.err.println("[RoomService] Room has no members");
            return null;
        }

        boolean found = false;
        for (RoomMemberDTO member : room.getMembers()) {
            if (member.getUserId().equals(userId)) {
                member.setPetId(petId);
                found = true;
                System.out.println("[RoomService] Updated pet for user " + userId + " to " + petId);
                break;
            }
        }

        if (!found) {
            System.err.println("[RoomService] Member not found in room: " + userId);
            return null;
        }

        return room;
    }

    public RoomDTO updateMemberCards(Long roomId, Long userId, List<CardDTO> cards) {
        RoomDTO room = rooms.get(roomId);
        if (room == null) {
            System.err.println("[RoomService] Room not found: " + roomId);
            return null;
        }

        if (room.getMembers() == null) {
            System.err.println("[RoomService] Room has no members");
            return null;
        }

        boolean found = false;
        for (RoomMemberDTO member : room.getMembers()) {
            if (member.getUserId().equals(userId)) {
                member.setCards(cards);
                found = true;
                System.out.println("[RoomService] Updated cards for user " + userId + ", count=" + cards.size());
                break;
            }
        }

        if (!found) {
            System.err.println("[RoomService] Member not found in room: " + userId);
            return null;
        }

        return room;
    }

    public RoomMemberDTO getMember(Long roomId, Long userId) {
        RoomDTO room = rooms.get(roomId);
        if (room == null || room.getMembers() == null) {
            return null;
        }

        return room.getMembers().stream()
                .filter(m -> m.getUserId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    public boolean isValidRoomId(Long roomId) {
        if (roomId == null) {
            return false;
        }
        return roomId >= MIN_ROOM_ID && roomId <= MAX_ROOM_ID;
    }
}