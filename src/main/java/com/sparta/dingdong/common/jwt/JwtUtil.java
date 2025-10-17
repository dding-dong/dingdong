package com.sparta.dingdong.common.jwt;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sparta.dingdong.domain.user.entity.enums.UserRole;
import com.sparta.dingdong.domain.user.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secretKey;

	private final UserRepository userRepository;
	private static final long EXPIRATION = 1000L * 60 * 30; // 30분
	private static final long REFRESH_EXPIRATION = 1000L * 60 * 60 * 24 * 14; // 14일

	public String createAccessToken(Long id, UserRole userRole, long tokenVersion) {
		return Jwts.builder()
			.setSubject(String.valueOf(id))
			.claim("userRole", userRole.name())
			.claim("version", tokenVersion)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
			.compact();
	}

	public UserAuth extractUserAuth(String token) {
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
			.build()
			.parseClaimsJws(token)
			.getBody();
		Long version = ((Number)claims.get("version")).longValue();
		return new UserAuth(
			Long.parseLong(claims.getSubject()),
			UserRole.valueOf(claims.get("userRole", String.class)),
			version
		);
	}

	public boolean validateToken(String token) {
		try {
			extractUserAuth(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String extractToken(HttpServletRequest request) {
		String bearer = request.getHeader("Authorization");
		if (bearer != null && bearer.startsWith("Bearer ")) {
			return bearer.substring(7);
		}
		return null;
	}

	public long getExpiration(String token) {
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(secretKey.getBytes())
			.build()
			.parseClaimsJws(token)
			.getBody();

		return claims.getExpiration().getTime() - System.currentTimeMillis();
	}

	public String createRefreshToken(Long id, UserRole role, long tokenVersion) {
		return Jwts.builder()
			.setSubject(String.valueOf(id))
			.claim("userRole", role.name())
			.claim("version", tokenVersion)
			.claim("jti", UUID.randomUUID().toString())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
			.signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
			.compact();
	}

	public long getRefreshExpiration(String refreshToken) {
		return getExpiration(refreshToken);
	}

	public String getJti(String refreshToken) {
		Claims claims = Jwts.parserBuilder()
			.setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
			.build()
			.parseClaimsJws(refreshToken)
			.getBody();
		return claims.get("jti", String.class);
	}
}