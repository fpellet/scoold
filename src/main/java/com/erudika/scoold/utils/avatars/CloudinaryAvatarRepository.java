package com.erudika.scoold.utils.avatars;

import com.erudika.para.core.User;
import com.erudika.scoold.core.Profile;
import org.apache.commons.lang3.StringUtils;

public class CloudinaryAvatarRepository implements AvatarRepository {
	private static final String BASE_URL = "https://res.cloudinary.com/";

	private final AvatarRepository nextRepository;

	public CloudinaryAvatarRepository(AvatarRepository nextRepository) {
		this.nextRepository = nextRepository;
	}

	@Override
	public String getLink(Profile profile, AvatarFormat format) {
		if (profile == null) {
			return nextRepository.getLink(profile, format);
		}

		String picture = profile.getPicture();
		if (!isCloudinaryLink(picture)) {
			return nextRepository.getLink(profile, format);
		}

		return configureLink(picture, format);
	}

	@Override
	public String getAnonymizedLink(String data) {
		return nextRepository.getAnonymizedLink(data);
	}

	@Override
	public AvatarStorageResult store(Profile profile, String url) {
		if (!isCloudinaryLink(url)) {
			return nextRepository.store(profile, url);
		}

		return applyChange(profile, url);
	}

	private AvatarStorageResult applyChange(Profile profile, String url) {
		profile.setPicture(url);

		User user = profile.getUser();
		if (!user.getPicture().equals(url)) {
			user.setPicture(url);

			return AvatarStorageResult.userChanged();
		}

		return AvatarStorageResult.profileChanged();
	}

	private boolean isCloudinaryLink(String url) {
		return StringUtils.startsWith(url, BASE_URL);
	}

	private String configureLink(String url, AvatarFormat format) {
		return url.replace("/upload/", "/upload/t_" + getTransformName(format) + "/");
	}

	private String getTransformName(AvatarFormat format) {
		return format.name().toLowerCase();
	}
}
