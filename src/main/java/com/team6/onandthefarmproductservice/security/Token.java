package com.team6.onandthefarmproductservice.security;

import lombok.Getter;

@Getter
public class Token {

    private String token;
    private String refreshToken;

    public Token(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
