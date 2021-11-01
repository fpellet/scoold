package com.erudika.scoold.utils.avatars;

import com.erudika.scoold.core.Profile;

public class DefaultAvatarRepository implements AvatarRepository {
	private final AvatarConfig config;

	public DefaultAvatarRepository(AvatarConfig config) {
		this.config = config;
	}

	@Override
	public String getLink(Profile profile, AvatarFormat format) {
		return config.getDefaultAvatar();
	}

	@Override
	public String getAnonymizedLink(String data) {
		return config.getDefaultAvatar();
	}
}
