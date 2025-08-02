/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.dbisolation;

import com.spring5.entity.User;
import com.spring5.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class Level1ReadUncommitted {
    
    private final UserRepository userRepository;
    
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public User getUserWithUncommittedRead(Long id) {
        // This may see uncommitted data from other transactions
        return userRepository.findById(id).orElse(null);
    }
    
    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public void updateUserWithUncommittedRead(User user) {
        // Changes here may be visible to other transactions before commit
        userRepository.save(user);
    }    
}
