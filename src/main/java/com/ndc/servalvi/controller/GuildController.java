package com.ndc.servalvi.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.ndc.servalvi.dto.GuildBasicDTO;
import com.ndc.servalvi.service.FirestoreService;
import com.ndc.servalvi.service.SignInService;

@RestController
@RequestMapping("/api/v1/guilds")
public class GuildController {
	
	SignInService signInService;
	FirestoreService firestoreService;
	
	public GuildController(SignInService signInService, FirestoreService firestoreService) {
		this.signInService = signInService;
		this.firestoreService = firestoreService;
	}

	@GetMapping
	@ResponseBody
	public ResponseEntity<List<GuildBasicDTO>> getGuilds(@RequestParam String idToken) {
		String userId = signInService.verifyIdToken(idToken).getUserID();
		if(userId == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			
		try {
			return new ResponseEntity<>(firestoreService.getGuilds(idToken), HttpStatus.OK);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping
	@ResponseBody
	public ResponseEntity<String> createGuild(@RequestParam String idToken, @RequestParam GuildBasicDTO guild) {
		String userId = signInService.verifyIdToken(idToken).getUserID();
		if(userId == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		
		String guildId = firestoreService.createGuild(userId, guild);
			
		return new ResponseEntity<>(guildId, HttpStatus.OK);
	}
	
	@DeleteMapping
	@ResponseBody
	public ResponseEntity<Void> removeGuild(@RequestParam String idToken, @RequestParam String guildId) {
		try {
			String userId = signInService.verifyIdToken(idToken).getUserID();
			if(userId == null)
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			
			boolean isAdmin = firestoreService.isAdmin(userId, guildId);
			if(!isAdmin)
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			
			boolean succeed = firestoreService.removeGuild(userId, guildId);
			if(succeed)	
				return new ResponseEntity<>(HttpStatus.OK);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
