package com.erudika.scoold.utils.avatars;

import com.erudika.scoold.core.Profile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GravatarAvatarRepository implements AvatarRepository {
	private final GravatarAvatarGenerator gravatarAvatarGenerator;
	private final AvatarConfig config;
	private final AvatarRepository nextRepository;

	public GravatarAvatarRepository(GravatarAvatarGenerator gravatarAvatarGenerator, AvatarConfig config, AvatarRepository nextRepository) {
		this.gravatarAvatarGenerator = gravatarAvatarGenerator;
		this.config = config;
		this.nextRepository = nextRepository;
	}
	private static final Logger logger = LoggerFactory.getLogger(GravatarAvatarRepository.class);

	@Override
	public String getLink(Profile profile, AvatarFormat format) {
		if (profile == null) {
			logger.info("no profile");
			return nextRepository.getLink(profile, format);
		}

		String picture = profile.getPicture();
		logger.info("profile picture: " + picture);
		if (StringUtils.isBlank(picture)) {
			logger.info("profile picture (blonk): " + picture);
			return gravatarAvatarGenerator.getLink(profile, format);
		}

		if (!gravatarAvatarGenerator.isLink(picture)) {
			logger.info("profile picture (no gravatar): " + picture);
			return nextRepository.getLink(profile, format);
		}

		logger.info("profile picture (gravatar): " + picture);
		return gravatarAvatarGenerator.configureLink(picture, format);
	}

	@Override
	public String getAnonymizedLink(String data) {
		return gravatarAvatarGenerator.getRawLink(data);
	}

	@Override
	public AvatarStorageResult store(Profile profile, String url) {
		if (StringUtils.isBlank(url) || StringUtils.equals(url, config.getDefaultAvatar())) {
			String gravatarUrl = gravatarAvatarGenerator.getRawLink(profile);
			return applyChange(profile, gravatarUrl);
		}

		if (!gravatarAvatarGenerator.isLink(url)) {
			return nextRepository.store(profile, url);
		}

		return applyChange(profile, url);
	}

	private AvatarStorageResult applyChange(Profile profile, String url) {
		profile.setPicture(url);
		return AvatarStorageResult.profileChanged();
	}
}
