package com.remake.poki.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.remake.poki.dto.*;
import com.remake.poki.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class RoomWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private OnlineUserService onlineUserService;

    @Autowired
    private RoomInviteService inviteService;

    @Autowired
    private RoomService roomService;

    /**
     * User connect
     */
    @MessageMapping("/room/connect")
    public void userConnect(@Payload OnlineUserDTO user) {
        onlineUserService.addOnlineUser(
                user.getUserId(),
                user.getUsername(),
                user.getAvatarId(),
                user.getLevel()
        );

        broadcastOnlineUsers();
    }

    /**
     * User disconnect
     */
    @MessageMapping("/room/disconnect")
    public void userDisconnect(@Payload Long userId) {
        // Xóa user khỏi phòng nếu có
        Long roomId = roomService.getUserRoomId(userId);
        if (roomId != null) {
            RoomDTO room = roomService.removeMember(roomId, userId);
            if (room != null) {
                broadcastRoomUpdate(room);
            } else {
                // Phòng đã bị xóa
                messagingTemplate.convertAndSend(
                        "/topic/room-closed/" + roomId,
                        roomId
                );
            }
        }

        onlineUserService.removeOnlineUser(userId);
        broadcastOnlineUsers();
    }

    /**
     * Lấy danh sách user online
     */
    @MessageMapping("/room/get-online-users")
    public void getOnlineUsers(@Payload Long userId) {
        List<OnlineUserDTO> users = onlineUserService.getAvailableUsers(userId);

        messagingTemplate.convertAndSend(
                "/queue/online-users/" + userId,
                users
        );
    }

    /**
     * ✅ Tạo phòng từ RoomDTO có sẵn
     */
    @MessageMapping("/room/create")
    public void createRoom(@Payload RoomDTO roomData) {
        // roomData đã có đầy đủ thông tin từ Unity (petId, cards, etc.)
        OnlineUserDTO user = onlineUserService.getUser(roomData.getHostUserId());

        if (user == null) {
            return;
        }

        RoomDTO room = roomService.createRoom(
                roomData,
                user.getUserId(),
                user.getUsername(),
                user.getAvatarId(),
                user.getLevel()
        );

        // Đánh dấu user đang trong phòng
        onlineUserService.setUserRoomId(user.getUserId(), room.getRoomId());

        // Gửi thông tin phòng cho host
        messagingTemplate.convertAndSend(
                "/queue/room-created/" + user.getUserId(),
                room
        );

        System.out.println("✅ Room created: " + room.getRoomId() + " for user " + user.getUsername());
        broadcastOnlineUsers();
    }

    /**
     * Gửi lời mời
     */
    @MessageMapping("/room/send-invite")
    public void sendInvite(@Payload RoomInviteDTO inviteRequest) {
        // ✅ VALIDATE ROOM ID
        if (!roomService.isValidRoomId(inviteRequest.getRoomId())) {
            messagingTemplate.convertAndSend(
                    "/queue/invite-error/" + inviteRequest.getFromUserId(),
                    "Invalid room ID format"
            );
            return;
        }

        // ✅ KIỂM TRA ROOM TỒN TẠI
        RoomDTO room = roomService.getRoom(inviteRequest.getRoomId());
        if (room == null) {
            messagingTemplate.convertAndSend(
                    "/queue/invite-error/" + inviteRequest.getFromUserId(),
                    "Room not found"
            );
            return;
        }

        RoomInviteDTO invite = inviteService.createInvite(
                inviteRequest.getRoomId(),
                inviteRequest.getFromUserId(),
                inviteRequest.getFromUsername(),
                inviteRequest.getToUserId()
        );

        messagingTemplate.convertAndSend(
                "/queue/invite/" + invite.getToUserId(),
                invite
        );

        System.out.println("✅ Invite sent: Room " + invite.getRoomId() + " from " + invite.getFromUsername());
    }

    /**
     * Chấp nhận lời mời
     */
    @MessageMapping("/room/accept-invite")
    public void acceptInvite(@Payload Long inviteId) {
        RoomInviteDTO invite = inviteService.getInvite(inviteId);

        if (invite == null) {
            System.err.println("❌ Invite not found: " + inviteId);
            return;
        }

        System.out.println("📨 Processing invite accept: inviteId=" + inviteId + ", roomId=" + invite.getRoomId());

        // Validate room ID
        if (!roomService.isValidRoomId(invite.getRoomId())) {
            messagingTemplate.convertAndSend(
                    "/queue/join-error/" + invite.getToUserId(),
                    "Invalid room ID format"
            );
            return;
        }

        // Kiểm tra room tồn tại
        RoomDTO room = roomService.getRoom(invite.getRoomId());
        if (room == null) {
            System.err.println("❌ Room not found: " + invite.getRoomId());
            messagingTemplate.convertAndSend(
                    "/queue/join-error/" + invite.getToUserId(),
                    "Phòng không tồn tại!"
            );
            return;
        }

        // Kiểm tra phòng đã đầy
        if (room.isFull()) {
            messagingTemplate.convertAndSend(
                    "/queue/join-error/" + invite.getToUserId(),
                    "Phòng đã đầy!"
            );
            return;
        }

        inviteService.acceptInvite(inviteId);

        // Lấy thông tin user
        OnlineUserDTO user = onlineUserService.getUser(invite.getToUserId());

        if (user == null) {
            System.err.println("❌ User not found: " + invite.getToUserId());
            return;
        }

        try {
            room = roomService.addMember(
                    invite.getRoomId(),
                    user.getUserId(),
                    user.getUsername(),
                    user.getAvatarId(),
                    user.getLevel()
            );

            onlineUserService.setUserRoomId(invite.getToUserId(), invite.getRoomId());

            messagingTemplate.convertAndSend(
                    "/queue/invite-response/" + invite.getFromUserId(),
                    invite
            );

            messagingTemplate.convertAndSend(
                    "/queue/room-joined/" + invite.getToUserId(),
                    room
            );

            broadcastRoomUpdate(room);

            // ✅ BROADCAST READY STATUS
            broadcastReadyStatus(invite.getRoomId());

            broadcastOnlineUsers();

            System.out.println("✅ User " + user.getUsername() + " joined room " + invite.getRoomId());

        } catch (Exception e) {
            System.err.println("❌ Error accepting invite: " + e.getMessage());
            messagingTemplate.convertAndSend(
                    "/queue/join-error/" + invite.getToUserId(),
                    e.getMessage()
            );
        }
    }

    /**
     * Từ chối lời mời
     */
    @MessageMapping("/room/decline-invite")
    public void declineInvite(@Payload Long inviteId) {
        RoomInviteDTO invite = inviteService.getInvite(inviteId);

        if (invite != null) {
            inviteService.declineInvite(inviteId);

            messagingTemplate.convertAndSend(
                    "/queue/invite-response/" + invite.getFromUserId(),
                    invite
            );
        }
    }

    /**
     * ✅ RỜI PHÒNG - NẾU HOST THÌ ĐÓNG PHÒNG VÀ KICK TẤT CẢ
     */
    @MessageMapping("/room/leave")
    public void leaveRoom(@Payload Long userId) {
        Long roomId = roomService.getUserRoomId(userId);
        if (roomId == null) {
            System.out.println("⚠️ User " + userId + " not in any room");
            return;
        }

        // ✅ LẤY THÔNG TIN PHÒNG TRƯỚC KHI XOÁ
        RoomDTO room = roomService.getRoom(roomId);
        if (room == null) {
            System.out.println("⚠️ Room " + roomId + " not found");
            return;
        }

        // ✅ KIỂM TRA XEM USER NÀY CÓ PHẢI HOST KHÔNG
        boolean isHost = room.getHostUserId().equals(userId);

        System.out.println("========================================");
        System.out.println("👋 LEAVE ROOM REQUEST");
        System.out.println("   Room ID: " + roomId);
        System.out.println("   User ID: " + userId);
        System.out.println("   Is Host: " + isHost);
        System.out.println("   Current members: " + (room.getMembers() != null ? room.getMembers().size() : 0));

        if (isHost) {
            // ✅ HOST RỜI → ĐÓNG PHÒNG VÀ KICK TẤT CẢ
            System.out.println("🚨 HOST LEFT - CLOSING ROOM AND KICKING ALL MEMBERS");

            // ✅ LẤY DANH SÁCH TẤT CẢ MEMBERS (trước khi xoá phòng)
            List<RoomMemberDTO> allMembers = roomService.getAllMembersBeforeDelete(roomId);

            // ✅ XOÁ PHÒNG NGAY LẬP TỨC
            roomService.deleteRoom(roomId);

            // ✅ THÔNG BÁO CHO TẤT CẢ MEMBERS (bao gồm cả host)
            for (RoomMemberDTO member : allMembers) {
                System.out.println("   → Notifying member: " + member.getUsername() + " (ID: " + member.getUserId() + ")");

                // ✅ ĐÁNH DẤU MEMBER KHÔNG CÒN TRONG PHÒNG
                onlineUserService.setUserRoomId(member.getUserId(), null);

                // ✅ GỬI THÔNG BÁO ROOM CLOSED
                Map<String, Object> notification = new HashMap<>();
                notification.put("roomId", roomId);
                notification.put("hostId", userId);

                if (member.getUserId().equals(userId)) {
                    // ✅ Thông báo cho chính host
                    notification.put("reason", "Bạn đã rời phòng");
                    notification.put("host", true);
                } else {
                    // ✅ Thông báo cho members khác
                    notification.put("reason", "Chủ phòng đã rời - Phòng bị đóng");
                    notification.put("host", false);
                }

                messagingTemplate.convertAndSend(
                        "/queue/room-closed/" + member.getUserId(),
                        notification
                );
            }

            // ✅ BROADCAST CHO TOPIC (backup cho những ai chưa nhận được)
            messagingTemplate.convertAndSend(
                    "/topic/room-closed/" + roomId,
                    Map.of(
                            "roomId", roomId,
                            "reason", "Host đã rời phòng",
                            "hostId", userId
                    )
            );

            System.out.println("✅ Room " + roomId + " closed and all members kicked");
            System.out.println("========================================");

        } else {
            // ✅ MEMBER THƯỜNG RỜI
            System.out.println("👤 REGULAR MEMBER LEFT");

            RoomDTO updatedRoom = roomService.removeMember(roomId, userId);
            onlineUserService.setUserRoomId(userId, null);

            // ✅ **FIX**: GỬI THÔNG BÁO CHO MEMBER ĐÃ RỜI
            Map<String, Object> leaveNotification = new HashMap<>();
            leaveNotification.put("roomId", roomId);
            leaveNotification.put("reason", "Bạn đã rời phòng thành công");
            leaveNotification.put("success", true);

            messagingTemplate.convertAndSend(
                    "/queue/room-left/" + userId,
                    leaveNotification
            );

            System.out.println("   → Sent leave confirmation to user " + userId);

            if (updatedRoom != null) {
                // ✅ Phòng vẫn còn → broadcast update đến members còn lại
                broadcastRoomUpdate(updatedRoom);
                System.out.println("✅ Member " + userId + " left room " + roomId);
                System.out.println("   Remaining members: " + updatedRoom.getMembers().size());
            } else {
                System.out.println("⚠️ Room was deleted after member left");
            }

            System.out.println("========================================");
        }

        // ✅ BROADCAST ONLINE USERS
        broadcastOnlineUsers();
    }

    /**
     * ✅ CHECK READY STATUS - TRẢ VỀ CHO CLIENT
     */
    @MessageMapping("/room/check-ready-status")
    public void checkReadyStatus(@Payload Long roomId) {
        RoomDTO room = roomService.getRoom(roomId);
        if (room == null) return;

        boolean allReady = roomService.isAllReady(roomId);
        int readyCount = 0;
        int totalMembers = room.getMembers() != null ? room.getMembers().size() : 0;

        if (room.getMembers() != null) {
            for (RoomMemberDTO member : room.getMembers()) {
                if (member.isReady()) readyCount++;
            }
        }

        Map<String, Object> status = new HashMap<>();
        status.put("roomId", roomId);
        status.put("allReady", allReady);
        status.put("readyCount", readyCount);
        status.put("totalMembers", totalMembers);

        // Broadcast cho tất cả members
        if (room.getMembers() != null) {
            for (RoomMemberDTO member : room.getMembers()) {
                messagingTemplate.convertAndSend(
                        "/queue/ready-status/" + member.getUserId(),
                        status
                );
            }
        }
    }

    /**
     * Set ready - CẬP NHẬT VÀ BROADCAST READY STATUS
     */
    @MessageMapping("/room/set-ready")
    public void setReady(@Payload Map<String, Object> payload) {
        Long roomId = Long.valueOf(payload.get("roomId").toString());
        Long userId = Long.valueOf(payload.get("userId").toString());
        Boolean ready = (Boolean) payload.get("ready");

        System.out.println("[Room] Set ready: roomId=" + roomId + ", userId=" + userId + ", ready=" + ready);

        RoomDTO room = roomService.setMemberReady(roomId, userId, ready);

        if (room != null) {
            // ❌ KHÔNG GỌI broadcastRoomUpdate() NỮA
            // broadcastRoomUpdate(room);  // ← XÓA DÒNG NÀY

            // ✅ CHỈ BROADCAST READY STATUS
            broadcastReadyStatus(roomId);

            // ✅ GỬI READY UPDATE ĐƠN GIẢN (không gọi getInfoPlayer)
            broadcastSimpleReadyUpdate(roomId, userId, ready);
        }
    }

    /**
     * ✅ BROADCAST READY UPDATE ĐỂ CẬP NHẬT UI (KHÔNG GHI ĐÈ DATA)
     */
    private void broadcastSimpleReadyUpdate(Long roomId, Long userId, boolean ready) {
        RoomDTO room = roomService.getRoom(roomId);
        if (room == null || room.getMembers() == null) return;

        Map<String, Object> readyUpdate = new HashMap<>();
        readyUpdate.put("userId", userId);
        readyUpdate.put("ready", ready);

        System.out.println("[Room] Broadcasting ready update: userId=" + userId + ", ready=" + ready);

        // Gửi cho tất cả members
        for (RoomMemberDTO member : room.getMembers()) {
            messagingTemplate.convertAndSend(
                    "/queue/ready-update/" + member.getUserId(),
                    readyUpdate
            );
        }
    }

    /**
     * ✅ KICK MEMBER
     */
    @MessageMapping("/room/kick-member")
    public void kickMember(@Payload Map<String, Object> payload) {
        Long roomId = Long.valueOf(payload.get("roomId").toString());
        Long hostUserId = Long.valueOf(payload.get("hostUserId").toString());
        Long kickedUserId = Long.valueOf(payload.get("kickedUserId").toString());

        System.out.println("[Room] Kick request: roomId=" + roomId + ", host=" + hostUserId + ", kicked=" + kickedUserId);

        RoomDTO room = roomService.getRoom(roomId);
        if (room == null) {
            System.err.println("❌ Room not found: " + roomId);
            return;
        }

        // ✅ KIỂM TRA XEM NGƯỜI GỬI CÓ PHẢI HOST KHÔNG
        if (!room.getHostUserId().equals(hostUserId)) {
            System.err.println("❌ User " + hostUserId + " is not the host!");
            messagingTemplate.convertAndSend(
                    "/queue/kick-error/" + hostUserId,
                    "Chỉ host mới có quyền kick!"
            );
            return;
        }

        // ✅ KIỂM TRA KHÔNG THỂ KICK CHÍNH MÌNH
        if (kickedUserId.equals(hostUserId)) {
            System.err.println("❌ Host cannot kick themselves!");
            messagingTemplate.convertAndSend(
                    "/queue/kick-error/" + hostUserId,
                    "Không thể kick chính mình!"
            );
            return;
        }

        // ✅ XÓA MEMBER KHỎI PHÒNG
        RoomDTO updatedRoom = roomService.removeMember(roomId, kickedUserId);
        onlineUserService.setUserRoomId(kickedUserId, null);

        if (updatedRoom != null) {
            System.out.println("✅ Member " + kickedUserId + " kicked from room " + roomId);

            // ✅ THÔNG BÁO CHO MEMBER BỊ KICK
            Map<String, Object> kickNotification = new HashMap<>();
            kickNotification.put("roomId", roomId);
            kickNotification.put("reason", "Bạn đã bị kick khỏi phòng");
            kickNotification.put("kicked", true);

            messagingTemplate.convertAndSend(
                    "/queue/room-kicked/" + kickedUserId,
                    kickNotification
            );

            // ✅ BROADCAST ROOM UPDATE CHO MEMBERS CÒN LẠI
            broadcastRoomUpdate(updatedRoom);

            // ✅ BROADCAST READY STATUS
            broadcastReadyStatus(roomId);

            // ✅ BROADCAST ONLINE USERS
            broadcastOnlineUsers();
        } else {
            System.err.println("❌ Failed to remove member from room");
        }
    }

    /**
     * ✅ BROADCAST READY STATUS CHO TẤT CẢ MEMBERS
     */
    private void broadcastReadyStatus(Long roomId) {
        RoomDTO room = roomService.getRoom(roomId);
        if (room == null || room.getMembers() == null) return;

        int totalMembers = room.getMembers().size();

        // ✅ ĐẾM SỐ MEMBERS (KHÔNG TÍNH HOST)
        long nonHostMembers = room.getMembers().stream()
                .filter(m -> !m.isHost())
                .count();

        long nonHostReadyCount = room.getMembers().stream()
                .filter(m -> !m.isHost())
                .filter(RoomMemberDTO::isReady)
                .count();

        // ✅ TÍNH allReady
        boolean allReady;
        if (totalMembers == 1) {
            // Chỉ có host solo
            allReady = true;
        } else if (nonHostMembers == 0) {
            // Không có member nào (chỉ host) → true
            allReady = true;
        } else {
            // Có members → kiểm tra tất cả members đã ready chưa
            allReady = (nonHostReadyCount == nonHostMembers);
        }

        Map<String, Object> status = new HashMap<>();
        status.put("roomId", roomId);
        status.put("allReady", allReady);
        status.put("readyCount", (int)nonHostReadyCount);  // Số members ready
        status.put("totalMembers", (int)nonHostMembers);   // Tổng số members (không tính host)

        System.out.println("[Room] Broadcasting ready status: " + nonHostReadyCount + "/" + nonHostMembers + " members ready, allReady=" + allReady);

        // Gửi cho tất cả members
        for (RoomMemberDTO member : room.getMembers()) {
            messagingTemplate.convertAndSend(
                    "/queue/ready-status/" + member.getUserId(),
                    status
            );
        }
    }

    /**
     * Bắt đầu match
     */
    @MessageMapping("/room/start-match")
    public void startMatch(@Payload Long roomId) {
        RoomDTO room = roomService.getRoom(roomId);

        if (room == null) {
            return;
        }

        // Kiểm tra tất cả đã ready chưa
        if (!roomService.isAllReady(roomId)) {
            messagingTemplate.convertAndSend(
                    "/queue/room-error/" + room.getHostUserId(),
                    "Not all players are ready"
            );
            return;
        }

        // Đánh dấu các user đang trong match
        if (room.getMembers() != null) {
            for (RoomMemberDTO member : room.getMembers()) {
                onlineUserService.setUserInMatch(member.getUserId(), true);
            }
        }

        room = roomService.startMatch(roomId);

        // Broadcast start match cho tất cả members
        broadcastRoomUpdate(room);
        broadcastOnlineUsers();
    }

    /**
     * Broadcast cập nhật phòng
     */
    private void broadcastRoomUpdate(RoomDTO room) {
        if (room == null || room.getMembers() == null) return;

        System.out.println("[Room] Broadcasting update to " + room.getMembers().size() + " members");

        for (RoomMemberDTO member : room.getMembers()) {
            messagingTemplate.convertAndSend(
                    "/queue/room-update/" + member.getUserId(),
                    roomService.getInfoPlayer(room, member)
            );
        }
    }

    /**
     * Broadcast danh sách user online
     */
    private void broadcastOnlineUsers() {
        messagingTemplate.convertAndSend(
                "/topic/online-users",
                onlineUserService.getAvailableUsers(null)
        );
    }

    /**
     * ✅ Join phòng bằng Room ID STRING
     */
    @MessageMapping("/room/join-by-id")
    public void joinRoomById(@Payload Map<String, Object> payload) {
        Long roomId = Long.valueOf(payload.get("roomId").toString());
        Long userId = Long.valueOf(payload.get("userId").toString());

        // ✅ VALIDATE
        if (!roomService.isValidRoomId(roomId)) {
            messagingTemplate.convertAndSend(
                    "/queue/join-error/" + userId,
                    "Room ID không hợp lệ!"
            );
            return;
        }

        // ✅ GET USER FROM ONLINE SERVICE
        OnlineUserDTO user = onlineUserService.getUser(userId);
        if (user == null) {
            messagingTemplate.convertAndSend(
                    "/queue/join-error/" + userId,
                    "User not found"
            );
            return;
        }

        // ✅ CHECK ROOM
        RoomDTO room = roomService.getRoom(roomId);
        if (room == null) {
            messagingTemplate.convertAndSend(
                    "/queue/join-error/" + userId,
                    "Phòng không tồn tại!"
            );
            return;
        }

        if (room.isFull()) {
            messagingTemplate.convertAndSend(
                    "/queue/join-error/" + userId,
                    "Phòng đã đầy!"
            );
            return;
        }

        try {
            room = roomService.addMember(
                    roomId,
                    user.getUserId(),
                    user.getUsername(),
                    user.getAvatarId(),
                    user.getLevel()
            );

            onlineUserService.setUserRoomId(userId, roomId);

            messagingTemplate.convertAndSend(
                    "/queue/room-joined/" + userId,
                    room
            );

            broadcastRoomUpdate(room);

            // ✅ BROADCAST READY STATUS
            broadcastReadyStatus(roomId);

            broadcastOnlineUsers();

            System.out.println("✅ User " + user.getUsername() + " joined room " + roomId);

        } catch (Exception e) {
            System.err.println("❌ Error joining room: " + e.getMessage());
            messagingTemplate.convertAndSend(
                    "/queue/join-error/" + userId,
                    e.getMessage()
            );
        }
    }

    /**
     * ✅ Join với RoomMemberDTO hoàn chỉnh
     */
    @MessageMapping("/room/join-with-full-info")
    public void joinRoomWithFullInfo(@Payload Map<String, Object> payload) {
        try {
            Long roomId = Long.valueOf(payload.get("roomId").toString());

            // ✅ VALIDATE ROOM ID
            if (!roomService.isValidRoomId(roomId)) {
                return;
            }

            // Parse RoomMemberDTO
            ObjectMapper mapper = new ObjectMapper();
            RoomMemberDTO memberInfo = mapper.convertValue(
                    payload.get("memberInfo"),
                    RoomMemberDTO.class
            );

            // Validation
            RoomDTO room = roomService.getRoom(roomId);
            if (room == null) {
                messagingTemplate.convertAndSend(
                        "/queue/join-error/" + memberInfo.getUserId(),
                        "Phòng không tồn tại!"
                );
                return;
            }

            if (room.isFull()) {
                messagingTemplate.convertAndSend(
                        "/queue/join-error/" + memberInfo.getUserId(),
                        "Phòng đã đầy!"
                );
                return;
            }

            // Add member
            room = roomService.addMember(roomId, memberInfo);
            onlineUserService.setUserRoomId(memberInfo.getUserId(), roomId);

            // Broadcast
            messagingTemplate.convertAndSend(
                    "/queue/room-joined/" + memberInfo.getUserId(),
                    room
            );
            broadcastRoomUpdate(room);
            broadcastOnlineUsers();

        } catch (Exception e) {
            System.err.println("❌ Error in joinRoomWithFullInfo: " + e.getMessage());
        }
    }

    /**
     * ✅ CẬP NHẬT PET
     */
    @MessageMapping("/room/update-pet")
    public void updatePet(@Payload Map<String, Object> payload) {
        try {
            Long roomId = Long.valueOf(payload.get("roomId").toString());
            Long userId = Long.valueOf(payload.get("userId").toString());
            Integer petId = Integer.valueOf(payload.get("petId").toString());

            System.out.println("[Room] Update pet request: roomId=" + roomId + ", userId=" + userId + ", petId=" + petId);

            // Cập nhật pet trong room
            RoomDTO room = roomService.updateMemberPet(roomId, userId, petId);

            if (room == null) {
                messagingTemplate.convertAndSend(
                        "/queue/room-error/" + userId,
                        "Room not found or member not in room"
                );
                return;
            }

            // ✅ CHỈ GỬI PET UPDATE - KHÔNG GỬI broadcastRoomUpdate()
            if (room.getMembers() != null) {
                for (RoomMemberDTO member : room.getMembers()) {
                    var updateData = Map.of(
                            "userId", userId,
                            "petId", petId
                    );

                    messagingTemplate.convertAndSend(
                            "/queue/pet-update/" + member.getUserId(),
                            updateData
                    );
                }
            }

            System.out.println("[Room] ✓ Pet update sent to all members");

        } catch (Exception e) {
            System.err.println("[Room] Error updating pet: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ✅ CẬP NHẬT CARDS
     */
    @MessageMapping("/room/update-cards")
    public void updateCards(@Payload Map<String, Object> payload) {
        try {
            Long roomId = Long.valueOf(payload.get("roomId").toString());
            Long userId = Long.valueOf(payload.get("userId").toString());

            // Parse cards
            ObjectMapper mapper = new ObjectMapper();
            String cardsJson = mapper.writeValueAsString(payload.get("cards"));
            List<CardDTO> cards = mapper.readValue(cardsJson,
                    mapper.getTypeFactory().constructCollectionType(List.class, CardDTO.class));

            System.out.println("[Room] Update cards request: roomId=" + roomId + ", userId=" + userId + ", count=" + cards.size());

            // Cập nhật cards trong room
            RoomDTO room = roomService.updateMemberCards(roomId, userId, cards);

            if (room == null) {
                messagingTemplate.convertAndSend(
                        "/queue/room-error/" + userId,
                        "Room not found or member not in room"
                );
                return;
            }

            // ✅ CHỈ GỬI CARDS UPDATE - KHÔNG GỬI broadcastRoomUpdate()
            if (room.getMembers() != null) {
                for (RoomMemberDTO member : room.getMembers()) {
                    var updateData = Map.of(
                            "userId", userId,
                            "cards", cards
                    );

                    messagingTemplate.convertAndSend(
                            "/queue/cards-update/" + member.getUserId(),
                            updateData
                    );
                }
            }

            System.out.println("[Room] ✓ Cards update sent to all members");

        } catch (Exception e) {
            System.err.println("[Room] Error updating cards: " + e.getMessage());
            e.printStackTrace();
        }
    }
}