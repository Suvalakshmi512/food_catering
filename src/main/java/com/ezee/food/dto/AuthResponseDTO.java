package com.ezee.food.dto;


import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor 
public class AuthResponseDTO {
 private String accessToken;
 private String username;
}
