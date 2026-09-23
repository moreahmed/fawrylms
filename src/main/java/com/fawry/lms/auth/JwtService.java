package com.fawry.lms.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service 
public class JwtService {
    private final JwtEncoder jwtEncoder;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateToken (Authentication authentication) {
        Instant now = Instant.now();
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("fawry")
            .subject(authentication.getName())
            .issuedAt(now)
            .expiresAt(now.plus(1, ChronoUnit.HOURS))
            .claim("role", role)
            .build();

        JwsHeader header = JwsHeader.with(SignatureAlgorithm.RS256).build();
        JwtEncoderParameters params = JwtEncoderParameters.from(header, claims);

        return jwtEncoder.encode(params).getTokenValue();
    }
}
