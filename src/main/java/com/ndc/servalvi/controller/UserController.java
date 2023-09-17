package com.ndc.servalvi.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ndc.servalvi.dto.UserDTO;
import com.ndc.servalvi.dto.UserLoginDTO;
import com.ndc.servalvi.service.FirestoreService;
import com.ndc.servalvi.service.SignInService;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

	SignInService signInService;
	FirestoreService firestoreService;
	
	public UserController(SignInService signInService, FirestoreService firestoreService) {
		this.signInService = signInService;
		this.firestoreService = firestoreService;
	}

	@PostMapping
	public ResponseEntity<UserDTO> signIn(@RequestParam String idToken) {
		try {
			UserLoginDTO userLogin = signInService.verifyIdToken(idToken);
			String userId = userLogin.getUserID();
			if(userId == null)
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			
			if(!firestoreService.isWhitelisted(userLogin.getEmail())) 
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		
			UserDTO userData = firestoreService.getUserData(userId);
			if(userData == null)
				firestoreService.setUserData(userId, signInService.getDefaultUserDTO(idToken));
			return new ResponseEntity<>(userData, HttpStatus.OK);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
