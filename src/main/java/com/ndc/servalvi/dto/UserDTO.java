package com.ndc.servalvi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {

	String name;
	String icon;
	String state;
	List<String> guilds;
	
}
