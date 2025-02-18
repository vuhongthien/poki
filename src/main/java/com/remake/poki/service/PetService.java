package com.remake.poki.service;

import com.remake.poki.dto.GroupDTO;
import com.remake.poki.dto.PetDTO;
import com.remake.poki.dto.PetEnemyDTO;
import com.remake.poki.enums.ElementType;
import com.remake.poki.model.Pet;
import com.remake.poki.repo.GroupPetRepository;
import com.remake.poki.repo.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PetService {
    private final PetRepository petRepository;

    final
    GroupPetRepository groupPetRepository;

    public PetService(PetRepository petRepository, GroupPetRepository groupPetRepository) {
        this.petRepository = petRepository;
        this.groupPetRepository = groupPetRepository;
    }

    @Transactional
    public void createPetsFromImages(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Thư mục không tồn tại hoặc không hợp lệ: " + folderPath);
        }

        // Lọc chỉ lấy file ảnh (jpg, png, jpeg)
        File[] imageFiles = folder.listFiles(file ->
                file.isFile() && file.getName().matches(".*\\.(jpg|jpeg|png)$"));

        if (imageFiles == null || imageFiles.length == 0) {
            throw new IllegalStateException("Không tìm thấy ảnh nào trong thư mục: " + folderPath);
        }

        Arrays.stream(imageFiles).forEach(file -> {
            try {
                Long petId = Long.parseLong(file.getName().replaceAll("\\..*$", ""));
                Pet pet = new Pet();
                pet.setId(petId);
                pet.setMaxLevel(12);
//                Pet pet = new Pet(petId, null, null, ElementType.EARTH, 12);
                petRepository.save(pet);
                System.out.println("Saved Pet ID: " + petId);
            } catch (NumberFormatException e) {
                System.err.println("Bỏ qua file không hợp lệ: " + file.getName());
            }
        });
    }
    @Transactional
    public void updatePetNamesFromFile() throws IOException {
        // Read all pets from the database
        List<Pet> pets = petRepository.findAll();

        // Read file content
        List<String> lines = Files.readAllLines(Path.of("C:\\Users\\ASUS\\Downloads\\test.txt"));

        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length == 2) {
                String petName = parts[0].trim();
                String description = parts[1].trim();

                // Skip if petName is null or empty in the file
                if (petName == null || petName.isEmpty()) {
                    continue; // Skip this iteration if petName is null or empty
                }

                // Iterate through the pets to find the one matching the name
                for (Pet pet : pets) {
                    // Skip if pet's name is null in the database
                    if (pet.getName() == null) {
                        continue; // Skip this pet if its name is null
                    }

                    if (pet.getName().equals(petName)) {
                        pet.setDes(description); // Update description if name matches
                        break; // Exit the loop once the pet is found and updated
                    }
                }
            }
        }

        // Save all updated pets in batch
        petRepository.saveAll(pets);
    }

    public List<PetDTO> getPets() {
        return petRepository.findAllPet();
    }

    public List<GroupDTO> getEnemyPets(Long userId) {
        List<GroupDTO> groupDTOS = groupPetRepository.getGroupPet(userId);
        for (GroupDTO groupDTO : groupDTOS) {
            List<PetEnemyDTO> petEnemyDTOS = petRepository.getEnemyPets(userId, groupDTO.getId());
            groupDTO.setListPetEnemy(petEnemyDTOS);
        }

        return groupDTOS;
    }
}

