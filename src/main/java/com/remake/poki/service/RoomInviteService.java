package com.remake.poki.service;

import com.remake.poki.dto.RoomInviteDTO;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class RoomInviteService {

    private final Map<Long, RoomInviteDTO> invites = new ConcurrentHashMap<>();
    private final AtomicLong inviteIdGenerator = new AtomicLong(1);

    /**
     * ✅ Tạo invite với Long roomId
     */
    public RoomInviteDTO createInvite(Long roomId, Long fromUserId, String fromUsername, Long toUserId) {
        RoomInviteDTO invite = new RoomInviteDTO();
        invite.setInviteId(inviteIdGenerator.getAndIncrement());
        invite.setRoomId(roomId);  // ✅ Long roomId
        invite.setFromUserId(fromUserId);
        invite.setFromUsername(fromUsername);
        invite.setToUserId(toUserId);
        invite.setMessage(fromUsername + " đã mời bạn vào phòng " + roomId + "!");
        invite.setTimestamp(System.currentTimeMillis());
        invite.setStatus("PENDING");

        invites.put(invite.getInviteId(), invite);

        System.out.println("✅ Invite created: ID=" + invite.getInviteId() + ", Room=" + roomId);

        return invite;
    }

    public void acceptInvite(Long inviteId) {
        RoomInviteDTO invite = invites.get(inviteId);
        if (invite != null) {
            invite.setStatus("ACCEPTED");
            System.out.println("✅ Invite accepted: ID=" + inviteId + ", Room=" + invite.getRoomId());
        }
    }

    public void declineInvite(Long inviteId) {
        RoomInviteDTO invite = invites.get(inviteId);
        if (invite != null) {
            invite.setStatus("DECLINED");
            System.out.println("❌ Invite declined: ID=" + inviteId);
        }
    }

    public RoomInviteDTO getInvite(Long inviteId) {
        return invites.get(inviteId);
    }

    /**
     * ✅ Lấy tất cả invites của user
     */
    public List<RoomInviteDTO> getUserInvites(Long userId) {
        List<RoomInviteDTO> userInvites = new ArrayList<>();

        for (RoomInviteDTO invite : invites.values()) {
            if (invite.getToUserId().equals(userId) && "PENDING".equals(invite.getStatus())) {
                userInvites.add(invite);
            }
        }

        return userInvites;
    }

    /**
     * ✅ Xóa invites cũ (có thể chạy định kỳ)
     */
    public void cleanupOldInvites() {
        long currentTime = System.currentTimeMillis();
        long expireTime = 5 * 60 * 1000; // 5 phút

        invites.entrySet().removeIf(entry -> {
            RoomInviteDTO invite = entry.getValue();
            return currentTime - invite.getTimestamp() > expireTime;
        });
    }
}