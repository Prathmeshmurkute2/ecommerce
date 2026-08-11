package com.ecommerce.service;


import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.ecommerce.model.User;
import com.ecommerce.repository.UserRepository;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class UserService {
	
	private final UserRepository userRepository;
	
//	private List<User> userList =new ArrayList<>();
//	private Long nextId = 1L;
	
	public List<User> fetchAllUsers(){
		return userRepository.findAll();
	}
	
	public void addUser(User user){
//		user.setId(nextId++);
		userRepository.save(user);
	}
	
	
	public Optional<User> fetchUserById(Long id) {
		return userRepository.findById(id);
	}
	
	public boolean updateUser(Long id, User updatedUser) {
	    return userRepository.findById(id)
	    		.map(existingUser -> {
	    			existingUser.setFirstName(updatedUser.getFirstName());
	    			existingUser.setLastName(updatedUser.getLastName());
	    			userRepository.save(existingUser);
	    			return true;
	    		}).orElse(false);
	}
}
