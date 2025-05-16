package org.demo.baoleme.entity;

import jakarta.persistence.Entity;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String gender;
    private String phone;
    private String avatar;
    private String password;

    // getters and setters
}
