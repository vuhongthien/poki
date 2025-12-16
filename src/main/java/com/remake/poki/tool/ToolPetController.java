package com.remake.poki.tool;

import com.remake.poki.enums.ElementType;
import com.remake.poki.model.Pet;
import com.remake.poki.repo.PetRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pet")
@RequiredArgsConstructor
public class ToolPetController {

    private final PetRepository petRepository;

    @PostMapping("/create-evolution")
    public CreatePetResponse createPetEvolution(@RequestBody CreatePetRequest request) {
        List<Pet> savedPets = new ArrayList<>();

        int idCount = request.getIds().size();

        if (idCount < 1 || idCount > 3) {
            throw new IllegalArgumentException("Phải nhập từ 1 đến 3 ID");
        }

        ElementType elementType = ElementType.valueOf(request.getElementType().toUpperCase());

        // Trường hợp nhập 3 ID
        if (idCount == 3) {
            Long id1 = request.getIds().get(0);
            Long id2 = request.getIds().get(1);
            Long id3 = request.getIds().get(2);

            // ID1: maxLevel=4, parent=ID2, child=NULL
            Pet pet1 = createPet(id1, 4, request.getName(), request.getDescription(),
                    elementType, id2.intValue(), null);

            // ID2: maxLevel=7, parent=ID3, child=ID1
            Pet pet2 = createPet(id2, 7, request.getName(), request.getDescription(),
                    elementType, id3.intValue(), id1);

            // ID3: maxLevel=12, parent=ID3 (chính nó), child=ID2
            Pet pet3 = createPet(id3, 12, request.getName(), request.getDescription(),
                    elementType, id3.intValue(), id2);

            savedPets.add(petRepository.save(pet1));
            savedPets.add(petRepository.save(pet2));
            savedPets.add(petRepository.save(pet3));
        }
        // Trường hợp nhập 2 ID
        else if (idCount == 2) {
            Long id1 = request.getIds().get(0);
            Long id2 = request.getIds().get(1);

            // ID1: maxLevel=7, parent=ID2, child=NULL
            Pet pet1 = createPet(id1, 7, request.getName(), request.getDescription(),
                    elementType, id2.intValue(), null);

            // ID2: maxLevel=12, parent=ID2 (chính nó), child=ID1
            Pet pet2 = createPet(id2, 12, request.getName(), request.getDescription(),
                    elementType, id2.intValue(), id1);

            savedPets.add(petRepository.save(pet1));
            savedPets.add(petRepository.save(pet2));
        }
        // Trường hợp nhập 1 ID
        else {
            Long id1 = request.getIds().get(0);

            // ID1: maxLevel=12, parent=ID1 (chính nó), child=NULL
            Pet pet1 = createPet(id1, 12, request.getName(), request.getDescription(),
                    elementType, id1.intValue(), null);

            savedPets.add(petRepository.save(pet1));
        }

        CreatePetResponse response = new CreatePetResponse();
        response.setPets(savedPets);
        response.setCount(savedPets.size());
        response.setMessage("Đã tạo thành công " + savedPets.size() + " pet!");

        return response;
    }

    private Pet createPet(Long id, int maxLevel, String name, String description,
                          ElementType elementType, int parentId, Long childId) {
        Pet pet = new Pet();
        pet.setId(id);
        pet.setName(name);
        pet.setDes(description);
        pet.setElementType(elementType);
        pet.setMaxLevel(maxLevel);
        pet.setParentId(parentId);
        pet.setChildId(childId);
        pet.setSkillCardId(0L);
        pet.setNo(0);
        pet.setFlagLegend(false);

        return pet;
    }

    @Data
    public static class CreatePetRequest {
        private List<Long> ids;
        private String name;
        private String description;
        private String elementType;
    }

    @Data
    public static class CreatePetResponse {
        private List<Pet> pets;
        private int count;
        private String message;
    }
}