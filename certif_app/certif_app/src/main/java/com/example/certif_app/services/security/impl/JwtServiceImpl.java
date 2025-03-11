package com.example.certif_app.services.security.impl;

import com.example.certif_app.services.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${token.signing.key}")
    private String jwtSigningKey;
    @Override
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
        // Claims::getSubject) =>shorthand for (claims) -> claims.getSubject()
    }

    public Date extractExpiration(String token)
    {
        return extractClaim(token, Claims::getExpiration);
    }


    private <T>T extractClaim(String token, Function<Claims,T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        // les claims:
        //iss (issuer : Origine du token),
        //exp (heure d'expiration),
        //sub (sujet),
        //aud (public cible),
        //nbf (Not Before : A ne pas utiliser avant cette
        //date) ,
        //iat ( issued at : date de création du token),
        //jti ( JWT ID identifiant unique du JWT).
        return claimsResolvers.apply(claims);

    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 20))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token)
                .getBody();
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(),userDetails);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName=extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSigningKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date())  ;
    }
}
