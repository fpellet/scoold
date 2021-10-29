package com.erudika.scoold.utils.avatars;

import com.erudika.scoold.core.Profile;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;

@Component
@Singleton
public class AvatarRepositoryProxy implements AvatarRepository {
	private final AvatarRepository repository;

	public AvatarRepositoryProxy(GravatarAvatarGenerator gravatarAvatarGenerator, AvatarConfig config) {
		this.repository = addGravatarIfEnabled(addCustomLinkIfEnabled(getDefault(), gravatarAvatarGenerator, config), gravatarAvatarGenerator, config);
	}

	private AvatarRepository addGravatarIfEnabled(AvatarRepository repo, GravatarAvatarGenerator gravatarAvatarGenerator, AvatarConfig config) {
		return config.isGravatarEnabled()
			? new GravatarAvatarRepository(gravatarAvatarGenerator, repo)
			: repo;
	}

	private AvatarRepository addCustomLinkIfEnabled(AvatarRepository repo, GravatarAvatarGenerator gravatarAvatarGenerator, AvatarConfig config) {
		return new CustomLinkAvatarRepository(gravatarAvatarGenerator, config, repo);
	}

	private AvatarRepository getDefault() {
		return new DefaultAvatarRepository();
	}

	@Override
	public String getLink(Profile profile, AvatarFormat format) {
		return repository.getLink(profile, format);
	}

	@Override
	public String getAnonymizedLink(String data) {
		return repository.getAnonymizedLink(data);
	}

	@Override
	public AvatarStorageResult store(Profile profile, String url) {
		return repository.store(profile, url);
	}
}
