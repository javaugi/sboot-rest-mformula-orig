package com.spring5.entity;

import jakarta.persistence.Cacheable;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "USERS")
@Cacheable
public class User {
    
    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    
    public User(Long id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    private String email;
    private String name;
    private String lastName;
    private String firstName;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = Role.class)
    private Set<Role> roles;
    
    ////cannot simultaneously fetch multiple bags: [com.spring5.entity.UserAccount.trades, com.spring5.entity.User.roles
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, targetEntity = UserAccount.class)
    @BatchSize(size = 20)
    private List<UserAccount> userAccounts; 
    
    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)  // Makes sense here
    @JoinColumn(name = "preferences_id")
    private UserPreferences preferences;

    @PrePersist  // Hash before saving to DB
    @PreUpdate   // Re-hash if password changes
    public void hashPassword() {
        if (password != null && !password.startsWith("$2a$")) { // Skip if already hashed
            this.password = hash(password);
        }
    }

    // Helper method (or inject a Spring Bean)
    private static String hash(String plainText) {
        return BCrypt.hashpw(plainText, BCrypt.gensalt(12));
    }

    // Verify password (for login)
    public boolean verifyPassword(String rawPassword) {
        return BCrypt.checkpw(rawPassword, this.password);
    }
}
