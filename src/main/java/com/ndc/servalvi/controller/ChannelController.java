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

import com.ndc.servalvi.service.FirestoreService;
import com.ndc.servalvi.service.SignInService;

@RestController
@RequestMapping("/api/v1/channels")
public class ChannelController {
	
	SignInService signInService;
	FirestoreService firestoreService;
	
	public ChannelController(SignInService signInService, FirestoreService firestoreService) {
		this.signInService = signInService;
		this.firestoreService = firestoreService;
		
		System.out.println(signInService);
	}

	@GetMapping
	@ResponseBody
	public ResponseEntity<List<String>> getChannels(@RequestParam String idToken, @RequestParam String guildId) {
		try {
			String userId = signInService.verifyIdToken(idToken).getUserID();
			if(userId == null)
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			
			boolean isMember = firestoreService.isMember(userId, guildId);
			if(!isMember)
				new ResponseEntity<>(HttpStatus.FORBIDDEN);
				
			return new ResponseEntity<>(firestoreService.getChannels(idToken), HttpStatus.OK);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping
	@ResponseBody
	public ResponseEntity<String> createChannel(@RequestParam String idToken, @RequestParam String guildId, @RequestParam String name) {
		try {
			String userId = signInService.verifyIdToken(idToken).getUserID();
			if(userId == null)
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			
			boolean isAdmin = firestoreService.isAdmin(userId, guildId);
			if(!isAdmin)
				new ResponseEntity<>(HttpStatus.FORBIDDEN);
			
			String channelId = firestoreService.createChannel(userId, guildId, name);
			return new ResponseEntity<>(channelId, HttpStatus.OK);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping
	@ResponseBody
	public ResponseEntity<Void> removeGuild(@RequestParam String idToken, @RequestParam String channelId) {
		try {
			String userId = signInService.verifyIdToken(idToken).getUserID();
			if(userId == null)
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			
			String guildId = firestoreService.getGuildByChannel(channelId);
			boolean isAdmin = firestoreService.isAdmin(userId, guildId);
			if(!isAdmin)
				return new ResponseEntity<>(HttpStatus.FORBIDDEN);
			
			boolean succeed = firestoreService.removeChannel(channelId);
			if(succeed)	
				return new ResponseEntity<>(HttpStatus.OK);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/voice")
	public ResponseEntity<String> getVoiceSession(@RequestParam String idToken, @RequestParam String channelId) {
		try {
			String userId = signInService.verifyIdToken(idToken).getUserID();
			if(userId == null)
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
	
			String guildId;
				guildId = firestoreService.getGuildByChannel(channelId);
			boolean isMember = firestoreService.isMember(userId, guildId);
			if(!isMember)
				new ResponseEntity<>(HttpStatus.FORBIDDEN);
			
			String sessionId =  firestoreService.getVoiceSession(userId, channelId);
			return new ResponseEntity<>(sessionId, HttpStatus.OK);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
