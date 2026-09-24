package com.example.guardian.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
	@NotBlank(message = "email required")
	@Email(message = "Invalid email format found")
	private String email;
	@NotBlank(message = "password required")
	@Size(min = 5, max = 100, message = "Password must be between 5 and 100 characters")
	private String password;
	@NotNull(message = "role required")
	@Pattern(regexp = "ADMIN|USER", message = "role must be ADMIN or USER")
	private String role;
}
