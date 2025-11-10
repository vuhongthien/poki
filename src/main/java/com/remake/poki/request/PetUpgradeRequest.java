package com.remake.poki.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

public class PetUpgradeRequest implements Serializable {
    private Long userPetId;
    private List<Long> stoneIds;
    private boolean success;
    private boolean preventDowngrade;

    public PetUpgradeRequest(Long userPetId, List<Long> stoneIds, boolean success) {
        this.userPetId = userPetId;
        this.stoneIds = stoneIds;
        this.success = success;
    }

    public PetUpgradeRequest() {
    }

    public Long getUserPetId() {
        return userPetId;
    }

    public void setUserPetId(Long userPetId) {
        this.userPetId = userPetId;
    }

    public List<Long> getStoneIds() {
        return stoneIds;
    }

    public void setStoneIds(List<Long> stoneIds) {
        this.stoneIds = stoneIds;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isPreventDowngrade() {
        return preventDowngrade;
    }

    public void setPreventDowngrade(boolean preventDowngrade) {
        this.preventDowngrade = preventDowngrade;
    }
}