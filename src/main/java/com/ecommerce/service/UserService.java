package com.ecommerce.service;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.ecommerce.dto.AddressDTO;
import com.ecommerce.dto.UserRequest;
import com.ecommerce.dto.UserResponse;
import com.ecommerce.model.Address;
import org.springframework.http.ResponseEntity;
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
	
	public List<UserResponse> fetchAllUsers(){
		return userRepository.findAll().stream()
				.map(this::mapToUseResponse)
				.collect(Collectors.toList());
	}
	
	public void addUser(UserRequest userRequest){
//		user.setId(nextId++);
		User user = new User();
		updateUserFromRequest(user, userRequest);
		userRepository.save(user);
	}

	private void updateUserFromRequest(User user, UserRequest userRequest) {

		user.setFirstName(userRequest.getFirstName());
		user.setLastName(userRequest.getLastName());
		user.setEmail(userRequest.getEmail());
		user.setPhoneNo(user.getPhoneNo());

		if(userRequest.getAddress() != null){
			Address address = new Address();

			address.setStreet(userRequest.getAddress().getStreet());
			address.setCity(userRequest.getAddress().getCity());
			address.setState(userRequest.getAddress().getCity());
			address.setCountry(userRequest.getAddress().getCountry());
			address.setZipcode(userRequest.getAddress().getZipcode());
		}

	}


	public Optional<UserResponse> fetchUserById(Long id) {

		return userRepository.findById(id)
				.map(this::mapToUseResponse);

	}
	
	public boolean updateUser(Long id, UserRequest updatedUserRequest) {
	    return userRepository.findById(id)
	    		.map(existingUser -> {
	    			updateUserFromRequest(existingUser, updatedUserRequest);
	    			userRepository.save(existingUser);
	    			return true;
	    		}).orElse(false);
	}

	private UserResponse mapToUseResponse(User user){
		UserResponse response = new UserResponse();
		response.setId(String.valueOf(user.getId()));
		response.setFirstName(user.getFirstName());
		response.setLastName(user.getLastName());
		response.setEmail(user.getEmail());
		response.setPhone(user.getPhoneNo());
		response.setRole(user.getRole());

		if(user.getAddress()!= null){
			AddressDTO addressDTO = new AddressDTO();
			addressDTO.setStreet(user.getAddress().getStreet());
			addressDTO.setCity(user.getAddress().getCity());
			addressDTO.setState(user.getAddress().getState());
			addressDTO.setCountry(user.getAddress().getCountry());
			addressDTO.setZipcode(user.getAddress().getZipcode());
		}
		return response;
	}
}
