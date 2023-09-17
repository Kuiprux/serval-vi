package com.ndc.servalvi.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.ndc.servalvi.dto.UserDTO;
import com.ndc.servalvi.dto.UserLoginDTO;

@Service
public class SignInService {
	
	private final String OAUTH2_GOOGLE_CLIENT_ID;
	private final GoogleIdTokenVerifier verifier;

	public SignInService(Environment env) {
		OAUTH2_GOOGLE_CLIENT_ID = env.getProperty("oauth2.google.client-id");

		this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
				// Specify the CLIENT_ID of the app that accesses the backend:
				.setAudience(Collections.singletonList(OAUTH2_GOOGLE_CLIENT_ID))
				// Or, if multiple clients access the backend:
				// .setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
				.build();
	}

	public UserLoginDTO verifyIdToken(String idTokenString) {

		try {
			// (Receive idTokenString by HTTPS POST)
			GoogleIdToken idToken = verifier.verify(idTokenString);
			if (idToken != null) {
				Payload payload = idToken.getPayload();
	
				// Print user identifier
				String userId = payload.getSubject();
				System.out.println("User ID: " + userId);
	
				// Get profile information from payload
				String email = payload.getEmail();
				/*boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
				String name = (String) payload.get("name");
				String pictureUrl = (String) payload.get("picture");
				String locale = (String) payload.get("locale");
				String familyName = (String) payload.get("family_name");
				String givenName = (String) payload.get("given_name");*/
	
				// Use or store profile information
				// ...
				
				return new UserLoginDTO(userId, email);
	
			} else {
				System.out.println("Invalid ID token.");
				return null;
			}
		} catch(GeneralSecurityException | IOException | IllegalArgumentException e) {
			return null;
		}
	}

	public UserDTO getDefaultUserDTO(String idTokenString) {

		try {
			// (Receive idTokenString by HTTPS POST)
			GoogleIdToken idToken = verifier.verify(idTokenString);
			if (idToken != null) {
				Payload payload = idToken.getPayload();
	
				String name = (String) payload.get("name");
				String pictureUrl = (String) payload.get("picture");
				
				return new UserDTO(name, pictureUrl, "offline", new ArrayList<>());
	
			} else {
				System.out.println("Invalid ID token.");
				return null;
			}
		} catch(GeneralSecurityException | IOException | IllegalArgumentException e) {
			return null;
		}
	}
}
