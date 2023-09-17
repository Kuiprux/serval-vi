package com.ndc.servalvi.service;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.ndc.servalvi.dto.ChannelDTO;
import com.ndc.servalvi.dto.GuildBasicDTO;
import com.ndc.servalvi.dto.GuildDTO;
import com.ndc.servalvi.dto.UserDTO;

import jakarta.annotation.PostConstruct;

@Service
public class FirestoreService {

	private Firestore fireStore;
	
	private CollectionReference userCollection;
	private CollectionReference guildCollection;
	private CollectionReference channelCollection;
	private CollectionReference settingsCollection;
	
	private DocumentReference whitelistDocument;

	@PostConstruct
	public void initialize() {
		try {
			FileInputStream serviceAccount = new FileInputStream("./src/main/resources/serviceAccountKey.json");
			FirestoreOptions firestoreOptions = FirestoreOptions.getDefaultInstance().toBuilder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
			
			fireStore = firestoreOptions.getService();
			
			userCollection = fireStore.collection("users");
			guildCollection = fireStore.collection("guilds");
			channelCollection = fireStore.collection("channels");
			settingsCollection = fireStore.collection("settings");
			
			whitelistDocument = settingsCollection.document("whitelist");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@SuppressWarnings("unchecked")
	public UserDTO getUserData(String userId) throws InterruptedException, ExecutionException {
		DocumentSnapshot snap = userCollection.document(userId).get().get();
		if(!snap.exists()) {
			return null;
		}
		return new UserDTO(snap.getString("name"),
							snap.getString("icon"),
							snap.getString("state"),
							(List<String>) snap.get("guilds"));
	}
	
	public void setUserData(String userId, UserDTO userDto) {
		userCollection.document(userId).set(userDto);
	}
	
	public String createGuild(String userId, GuildBasicDTO guild) {
		String guildId = UUID.randomUUID().toString();
		guildCollection.document(guildId).set(new GuildDTO(guild));
		return guildId;
	}
	
	public boolean removeGuild(String userId, String guildId) {
		guildCollection.document(guildId).delete();

		return true;
	}

	@SuppressWarnings("unchecked")
	public List<GuildBasicDTO> getGuilds(String userId) throws InterruptedException, ExecutionException {
		DocumentSnapshot snap = userCollection.document(userId).get().get();
		List<String> guildIds = (List<String>) snap.get("guilds");
		
		List<GuildBasicDTO> guilds = new ArrayList<>(guildIds.size());
		for(String guildId : guildIds) {
			guilds.add(getGuildBasic(guildId));
		}
		return guilds;
	}

	public GuildBasicDTO getGuildBasic(String guildId) throws InterruptedException, ExecutionException {
		DocumentSnapshot snap = guildCollection.document(guildId).get().get();
		return new GuildBasicDTO(snap.getString("name"),
								 snap.getString("icon"));
	}
	

	public String createChannel(String userId, String guildId, String name) {
		String channelId = UUID.randomUUID().toString();
		channelCollection.document(channelId).set(new ChannelDTO(name, guildId));
		return channelId;
	}
	
	public boolean removeChannel(String channelId) {
		channelCollection.document(channelId).delete();
		
		return true;
	}

	@SuppressWarnings("unchecked")
	public List<String> getChannels(String guildId) throws InterruptedException, ExecutionException {
		DocumentSnapshot snap = guildCollection.document(guildId).get().get();
		return (List<String>) snap.get("channels");
	}

	@SuppressWarnings("unchecked")
	public boolean isAdmin(String userId, String guildId) throws InterruptedException, ExecutionException {
		List<String> adminUserIds = (List<String>) guildCollection.document(guildId).get().get().get("admins");
		for(String adminUserId : adminUserIds) {
			if(adminUserId == userId)
				return true;
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public boolean isMember(String userId, String guildId) throws InterruptedException, ExecutionException {
		List<String> memGuildIds = (List<String>) userCollection.document(userId).get().get().get("guilds");
		for(String memGuildId : memGuildIds) {
			if(memGuildId == guildId)
				return true;
		}
		return false;
	}

	public String getVoiceSession(String userId, String channelId) {
		return null;
	}

	public String getGuildByChannel(String channelId) throws InterruptedException, ExecutionException {
		return channelCollection.document(channelId).get().get().getString("guild");
	}


	public boolean isWhitelisted(String email) throws InterruptedException, ExecutionException {
		return whitelistDocument.get().get().getData().containsKey(email);
	}
	
}
