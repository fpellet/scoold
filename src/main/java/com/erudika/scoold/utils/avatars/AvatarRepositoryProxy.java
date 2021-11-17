package com.erudika.scoold.utils.avatars;

import com.erudika.scoold.core.Profile;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;

@Component
@Singleton
public class AvatarRepositoryProxy implements AvatarRepository {
	private final AvatarRepository repository;

	public AvatarRepositoryProxy(GravatarAvatarGenerator gravatarAvatarGenerator, AvatarConfig config) {
		this.repository =
			addGravatarIfEnabled(gravatarAvatarGenerator, config,
				addCloudinaryIfEnabled(config,
					addCustomLinkIfEnabled(gravatarAvatarGenerator, config,
						getDefault(config))));
	}

	private AvatarRepository addGravatarIfEnabled(GravatarAvatarGenerator gravatarAvatarGenerator, AvatarConfig config, AvatarRepository repo) {
		return config.isGravatarEnabled()
			? new GravatarAvatarRepository(gravatarAvatarGenerator, config, repo)
			: repo;
	}

	private AvatarRepository addCloudinaryIfEnabled(AvatarConfig config, AvatarRepository repo) {
		return config.isCloudinaryEnabled()
			? new CloudinaryAvatarRepository(repo)
			: repo;
	}

	private AvatarRepository addCustomLinkIfEnabled(GravatarAvatarGenerator gravatarAvatarGenerator, AvatarConfig config, AvatarRepository repo) {
		return config.isCustomLinkEnabled()
			? new CustomLinkAvatarRepository(gravatarAvatarGenerator, config, repo)
			: repo;
	}

	private AvatarRepository getDefault(AvatarConfig config) {
		return new DefaultAvatarRepository(config);
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
