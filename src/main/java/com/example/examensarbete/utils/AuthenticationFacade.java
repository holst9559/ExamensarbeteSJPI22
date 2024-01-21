package com.example.examensarbete.utils;

import java.util.Set;

public interface AuthenticationFacade {
    Set<String> getRoles();
    String getEmail();
}