package com.urlshortner.urlshortner.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class ShortCodeGenerator {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SHORT_KEY_LENGTH = 6;
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generates a random 6-character Base62 code.
     * @return A random 6-character string using Base62 encoding (0-9, a-z, A-Z)
     */
    public String generate() {
        StringBuilder code = new StringBuilder(SHORT_KEY_LENGTH);
        for (int i = 0; i < SHORT_KEY_LENGTH; i++) {
            int randomIndex = secureRandom.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(randomIndex));
        }

        return code.toString();
    }
}
