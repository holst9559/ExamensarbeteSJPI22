package com.example.examensarbete.utils;

import com.example.examensarbete.dto.UserDto;

public record AuthenticationResponse(
        String token,
        UserDto userDto
) {
}
