package com.ndc.servalvi.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GuildDTO {

    String name;
    String icon;
    List<String> users;
    List<String> admins;
    List<String> channels;

    public GuildDTO(GuildBasicDTO basicDTO) {
        this(basicDTO.getName(), basicDTO.getIcon(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
    


}
