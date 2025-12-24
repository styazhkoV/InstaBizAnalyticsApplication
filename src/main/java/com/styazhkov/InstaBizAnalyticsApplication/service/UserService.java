package com.styazhkov.InstaBizAnalyticsApplication.service;

//import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.styazhkov.InstaBizAnalyticsApplication.model.InstagramAccount;
import com.styazhkov.InstaBizAnalyticsApplication.model.User;
import com.styazhkov.InstaBizAnalyticsApplication.repository.InstagramAccountRepository;
import com.styazhkov.InstaBizAnalyticsApplication.repository.UserRepository;

import java.util.Optional;

@Service
//@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final InstagramAccountRepository accountRepository;

    public UserService(UserRepository userRepository, InstagramAccountRepository accountRepository) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
    }

    public User createUser(String username) {
        User user = new User();
        user.setUsername(username);
        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public InstagramAccount saveInstagramAccount(InstagramAccount account) {
        return accountRepository.save(account);
    }

    public Optional<InstagramAccount> findAccountByIgUserId(String igUserId) {
        return accountRepository.findById(igUserId);
    }
}
