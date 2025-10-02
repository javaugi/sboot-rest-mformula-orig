/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.controller;

import com.spring5.dao.LoginRequest;
import com.spring5.dao.LoginResponse;
import com.spring5.dto.UserDtoMapper;
import com.spring5.dto.auth.ApiKeyLoginRequest;
import com.spring5.dto.auth.AuthResponse;
import com.spring5.dto.auth.InternalLoginRequest;
import com.spring5.dto.auth.RefreshTokenRequest;
import com.spring5.dto.auth.TokenInfo;
import com.spring5.entity.auth.ApiClient;
import com.spring5.entity.auth.ExternalUser;
import com.spring5.entity.auth.InternalUser;
import com.spring5.service.auth.AuthService;
import com.spring5.utils.JwtTokenProvider;
import com.spring5.utils.JwtUtils;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/*
This architecture provides:
    Separate authentication flows for internal and external users
    JWT-based stateless authentication
    OAuth2 integration for social logins
    API key support for external integrations
    Role-based access control
    Redis-based token management
    Angular guards and interceptors for frontend security
    Spring Cloud Gateway for centralized security
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@Validated
@RequiredArgsConstructor
public class AuthController {

	JwtUtils jwtUtils;

	private ReactiveAuthenticationManager authenticationManager;

	private final AuthService authService;

	private final JwtTokenProvider tokenProvider;

	@GetMapping("/me")
	public Mono<Map<String, Object>> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
		log.info("AuthController getCurrentUser {}", userDetails);
		if (userDetails == null) {
			return Mono.empty(); // Or return 401 if no user is authenticated
		}

		return Mono.just(Map.of("username", userDetails.getUsername(), "roles",
				userDetails.getAuthorities()
					.stream()
					.map(grantedAuthority -> grantedAuthority.getAuthority())
					.collect(Collectors.toList())));
	}

	@PostMapping("/login")
	public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest request) {
		log.info("AuthController login {}", request);
		// return Mono.just(ResponseEntity.ok(new LoginResponse("login done")));
		// *
		return authenticationManager
			.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()))
			.flatMap(auth -> {
				String jwt = jwtUtils.generateToken(auth).block();
				return Mono.just(ResponseEntity.ok(new LoginResponse(jwt)));
			})
			.onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
		// */
	}

	@PostMapping("/logout")
	public Mono<ResponseEntity<String>> logout() {
		log.info("AuthController logout ");
		return ReactiveSecurityContextHolder.getContext().flatMap(securityContext -> {
			ReactiveSecurityContextHolder.clearContext();
			return Mono.just(ResponseEntity.ok("Logged out successfully"));
		}).switchIfEmpty(Mono.just(ResponseEntity.ok("Logged out (no active session)")));
	}

	// Internal user login
	@PostMapping("/internal/login")
	public ResponseEntity<AuthResponse> internalLogin(@Valid @RequestBody InternalLoginRequest request) {
		InternalUser user = authService.authenticateInternalUser(request.getUsername(), request.getPassword());
		String token = tokenProvider.generateToken(user.getEmail(), user.getRole().toString(), "INTERNAL");
		String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());

		return ResponseEntity.ok(AuthResponse.builder()
			.accessToken(token)
			.refreshToken(refreshToken)
			.tokenType("Bearer")
			.user(mapToUserDto(user))
			.build());
	}

	// External OAuth2 login initiation
	@GetMapping("/external/{provider}")
	public ResponseEntity<Void> initiateExternalLogin(@PathVariable String provider) {
		String redirectUrl = authService.getOAuth2RedirectUrl(provider);
		return ResponseEntity.status(HttpStatus.FOUND).header(HttpHeaders.LOCATION, redirectUrl).build();
	}

	// OAuth2 callback
	@GetMapping("/external/{provider}/callback")
	public ResponseEntity<AuthResponse> handleOAuth2Callback(@PathVariable String provider, @RequestParam String code) {

		ExternalUser user = authService.authenticateExternalUser(provider, code);
		String token = tokenProvider.generateToken(user.getEmail(), "USER", "EXTERNAL");
		String refreshToken = tokenProvider.generateRefreshToken(user.getEmail());

		return ResponseEntity.ok(AuthResponse.builder()
			.accessToken(token)
			.refreshToken(refreshToken)
			.tokenType("Bearer")
			.user(mapToUserDto(user))
			.build());
	}

	// API Key authentication
	@PostMapping("/external/apikey")
	public ResponseEntity<AuthResponse> apiKeyLogin(@Valid @RequestBody ApiKeyLoginRequest request) {
		ApiClient client = authService.authenticateApiClient(request.getApiKey());
		String token = tokenProvider.generateToken(client.getClientId(), "API_CLIENT", "EXTERNAL");

		return ResponseEntity
			.ok(AuthResponse.builder().accessToken(token).tokenType("Bearer").user(mapToUserDto(client)).build());
	}

	// Token refresh
	@PostMapping("/refresh")
	public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
		try {
			String email = tokenProvider.validateRefreshToken(request.getRefreshToken());
			TokenInfo tokenInfo = tokenProvider.getTokenInfoFromRefreshToken(request.getRefreshToken());

			String newAccessToken = tokenProvider.generateToken(email, tokenInfo.getRole(), tokenInfo.getUserType());
			String newRefreshToken = tokenProvider.generateRefreshToken(email);

			return ResponseEntity.ok(AuthResponse.builder()
				.accessToken(newAccessToken)
				.refreshToken(newRefreshToken)
				.tokenType("Bearer")
				.build());
		}
		catch (Exception ex) {

		}
		return ResponseEntity.noContent().build();
	}

	private UserDtoMapper mapToUserDto(Object user) {
		// Map different user types to common DTO
		UserDtoMapper userMapper = UserDtoMapper.builder().build();
		if (user instanceof InternalUser) {
			BeanUtils.copyProperties(((InternalUser) user), userMapper);
		}
		else if (user instanceof ExternalUser) {
			BeanUtils.copyProperties(((ExternalUser) user), userMapper);
		}
		else if (user instanceof ApiClient) {
			BeanUtils.copyProperties(((ApiClient) user), userMapper);
		}
		return userMapper;
	}

}

/*
 * 2. Enhanced Logout (Optional) If you want to invalidate tokens (requires token
 * blacklisting):
 * 
 * Option A: Simple In-Memory Blacklist import java.util.HashSet; import java.util.Set;
 * 
 * @RestController public class AuthController { private final Set<String>
 * blacklistedTokens = new HashSet<>(); private final JwtUtilsImplBkup jwtUtils; // Your
 * JWT utility class
 * 
 * @PostMapping("/api/logout") public ResponseEntity<String> logout(HttpServletRequest
 * request) { String token = extractToken(request); if (token != null) {
 * blacklistedTokens.add(token); // Add to blacklist }
 * SecurityContextHolder.clearContext(); return ResponseEntity.ok("Logout successful"); }
 * 
 * private String extractToken(HttpServletRequest request) { String header =
 * request.getHeader("Authorization"); if (header != null && header.startsWith("Bearer "))
 * { return header.substring(7); } return null; } } Option B: Database-Backed Blacklist
 * java
 * 
 * @Entity public class InvalidatedToken {
 * 
 * @Id private String token; private Date expiryDate; }
 * 
 * @Repository public interface InvalidatedTokenRepository extends
 * JpaRepository<InvalidatedToken, String> {}
 * 
 * @RestController public class AuthController {
 * 
 * @Autowired private InvalidatedTokenRepository tokenRepo;
 * 
 * @Autowired private JwtUtilsImplBkup jwtUtils;
 * 
 * @PostMapping("/api/logout") public ResponseEntity<String> logout(HttpServletRequest
 * request) { String token = extractToken(request); if (token != null) { InvalidatedToken
 * invalidToken = new InvalidatedToken(); invalidToken.setToken(token);
 * invalidToken.setExpiryDate(jwtUtils.extractExpiration(token));
 * tokenRepo.save(invalidToken); // Store in DB } SecurityContextHolder.clearContext();
 * return ResponseEntity.ok("Logout successful"); } }
 * 
 * 3. Update JWT Filter to Check Blacklist Modify your JwtAuthFilter:
 * 
 * java public class JwtAuthFilter extends OncePerRequestFilter { // ... existing code ...
 * 
 * @Override protected void doFilterInternal(HttpServletRequest request, ...) { String jwt
 * = extractToken(request); if (jwt != null && isTokenBlacklisted(jwt)) {
 * response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token invalidated"); return; }
 * // ... rest of the filter logic ... }
 * 
 * private boolean isTokenBlacklisted(String token) { // For in-memory blacklist: //
 * return blacklistedTokens.contains(token);
 * 
 * // For database blacklist: // return tokenRepo.existsById(token); return false; //
 * Implement your logic } } 4. React Integration (Client-Side) jsx const logout = async ()
 * => { try { await axios.post('/api/logout', {}, { headers: { Authorization: `Bearer
 * ${localStorage.getItem('token')}` } }); localStorage.removeItem('token'); // Clear JWT
 * // Redirect to login } catch (err) { console.error("Logout failed:", err); } }; Key
 * Considerations Stateless Nature of JWT:
 * 
 * Traditional session invalidation doesn't apply.
 * 
 * Clients must delete the token locally.
 * 
 * Token Expiration:
 * 
 * Short-lived tokens (e.g., 15-30 mins) reduce the need for blacklisting.
 * 
 * Blacklist Tradeoffs:
 * 
 * In-Memory: Fast but doesn't survive server restarts.
 * 
 * Database: Persistent but adds overhead.
 * 
 * Alternative: Use refresh tokens for better security.
 */
