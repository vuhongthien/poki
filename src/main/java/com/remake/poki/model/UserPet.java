package com.remake.poki.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_pet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserPet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long petId;

    private int level = 1;

}

