package com.erudika.scoold.utils.avatars;

import com.erudika.scoold.core.Profile;
import org.apache.commons.lang3.StringUtils;

public class GravatarAvatarProvider implements AvatarProvider {
	private final GravatarAvatarGenerator gravatarAvatarGenerator;
	private final AvatarProvider nextRepository;

	public GravatarAvatarProvider(GravatarAvatarGenerator gravatarAvatarGenerator, AvatarProvider nextRepository) {
		this.gravatarAvatarGenerator = gravatarAvatarGenerator;
		this.nextRepository = nextRepository;
	}

	@Override
	public String getLink(Profile profile, AvatarFormat format) {
		if (profile == null) {
			return nextRepository.getLink(profile, format);
		}

		String picture = profile.getPicture();
		if (StringUtils.isBlank(picture)) {
			return gravatarAvatarGenerator.getLink(profile, format);
		}

		if (!gravatarAvatarGenerator.isLink(picture)) {
			return nextRepository.getLink(profile, format);
		}

		return gravatarAvatarGenerator.configureLink(picture, format);
	}

	@Override
	public String getAnonymizedLink(String data) {
		return gravatarAvatarGenerator.getRawLink(data);
	}
}
