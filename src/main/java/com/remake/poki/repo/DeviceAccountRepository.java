package com.remake.poki.repo;

import com.remake.poki.model.DeviceAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceAccountRepository extends JpaRepository<DeviceAccount, Long> {

    List<DeviceAccount> findByDeviceId(String deviceId);

    long countByDeviceId(String deviceId);

    boolean existsByDeviceIdAndUsername(String deviceId, String username);
}
