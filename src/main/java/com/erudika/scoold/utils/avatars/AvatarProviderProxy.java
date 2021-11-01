package com.erudika.scoold.utils.avatars;

import com.erudika.scoold.core.Profile;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;

@Component
@Singleton
public class AvatarProviderProxy implements AvatarProvider {
	private final AvatarProvider repository;

	public AvatarProviderProxy(GravatarAvatarGenerator gravatarAvatarGenerator, AvatarConfig config) {
		this.repository = addGravatarIfEnabled(addCustomLinkIfEnabled(getDefault(config), gravatarAvatarGenerator, config), gravatarAvatarGenerator, config);
	}

	private AvatarProvider addGravatarIfEnabled(AvatarProvider repo, GravatarAvatarGenerator gravatarAvatarGenerator, AvatarConfig config) {
		return config.isGravatarEnabled()
			? new GravatarAvatarProvider(gravatarAvatarGenerator, repo)
			: repo;
	}

	private AvatarProvider addCustomLinkIfEnabled(AvatarProvider repo, GravatarAvatarGenerator gravatarAvatarGenerator, AvatarConfig config) {
		return config.isCustomLinkEnabled()
			? new CustomLinkAvatarProvider(gravatarAvatarGenerator, config, repo)
			: repo;
	}

	private AvatarProvider getDefault(AvatarConfig config) {
		return new DefaultAvatarProvider(config);
	}

	@Override
	public String getLink(Profile profile, AvatarFormat format) {
		return repository.getLink(profile, format);
	}

	@Override
	public String getAnonymizedLink(String data) {
		return repository.getAnonymizedLink(data);
	}
}
