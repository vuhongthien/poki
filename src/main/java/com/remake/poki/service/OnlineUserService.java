package com.remake.poki.service;

import com.remake.poki.dto.OnlineUserDTO;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OnlineUserService {

    // Map: userId -> OnlineUserDTO
    private final Map<Long, OnlineUserDTO> onlineUsers = new ConcurrentHashMap<>();

    /**
     * Thêm user online
     */
    public void addOnlineUser(Long userId, String username, String avatarId, int level) {
        OnlineUserDTO user = new OnlineUserDTO();
        user.setUserId(userId);
        user.setUsername(username);
        user.setAvatarId(avatarId);
        user.setLevel(level);
        user.setRoomId(null);  // Chưa vào phòng
        user.setInMatch(false); // Chưa vào match

        onlineUsers.put(userId, user);
    }

    /**
     * Xóa user khỏi danh sách online
     */
    public void removeOnlineUser(Long userId) {
        onlineUsers.remove(userId);
    }

    /**
     * Set user vào phòng
     */
    public void setUserRoomId(Long userId, Long roomId) {
        OnlineUserDTO user = onlineUsers.get(userId);
        if (user != null) {
            user.setRoomId(roomId);
        }
    }

    /**
     * Set user vào match
     */
    public void setUserInMatch(Long userId, boolean inMatch) {
        OnlineUserDTO user = onlineUsers.get(userId);
        if (user != null) {
            user.setInMatch(inMatch);
        }
    }

    /**
     * Lấy danh sách user có thể mời (available)
     * - Không trong phòng (roomId == null)
     * - Không trong match (inMatch == false)
     * - Không phải chính user hiện tại
     */
    public List<OnlineUserDTO> getAvailableUsers(Long excludeUserId) {
        return onlineUsers.values().stream()
                .filter(user -> !user.getUserId().equals(excludeUserId)) // Loại bỏ chính mình
                .filter(user -> user.getRoomId() == null)                 // Chưa vào phòng
                .filter(user -> !user.isInMatch())                        // Chưa vào match
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả user online (để broadcast)
     */
    public List<OnlineUserDTO> getAllOnlineUsers() {
        return new ArrayList<>(onlineUsers.values());
    }

    /**
     * Kiểm tra user có online không
     */
    public boolean isUserOnline(Long userId) {
        return onlineUsers.containsKey(userId);
    }

    /**
     * Lấy thông tin user
     */
    public OnlineUserDTO getUser(Long userId) {
        return onlineUsers.get(userId);
    }
}