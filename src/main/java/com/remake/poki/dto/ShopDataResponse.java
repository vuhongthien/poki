package com.remake.poki.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShopDataResponse {
    private List<ShopItemDTO> items;
    private List<ShopPetDTO> pets;
    private List<ShopAvatarDTO> avatars;
}
