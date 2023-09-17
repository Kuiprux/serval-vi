package com.ndc.servalvi.dto;

import lombok.Data;

@Data
public class ChannelDTO {
	
	private String name;
	private String guild;
	private String session = "";
	
	public ChannelDTO(String name, String guild) {
		this.name = name;
		this.guild = guild;
	}
	
}
