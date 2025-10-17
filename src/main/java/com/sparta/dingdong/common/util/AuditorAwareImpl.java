package com.sparta.dingdong.common.util;

import java.util.Optional;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.sparta.dingdong.common.jwt.UserAuth;

@Component
public class AuditorAwareImpl implements AuditorAware<Long> {

	@Override
	public Optional<Long> getCurrentAuditor() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()
			|| authentication.getPrincipal().equals("anonymousUser")) {
			return Optional.empty();
		}

		Object principal = authentication.getPrincipal();

		if (principal instanceof UserAuth userAuth) {
			return Optional.of(userAuth.getId());
		}

		return Optional.empty();
	}
}

