package com.erudika.scoold.utils.avatars;

import com.erudika.para.utils.Config;
import com.erudika.para.utils.Utils;
import com.erudika.scoold.core.Profile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;

import static com.erudika.scoold.ScooldServer.*;
import static com.erudika.scoold.ScooldServer.CONTEXT_PATH;

@Component
@Singleton
public class LegacyAvatarRepository implements AvatarRepository {
	public LegacyAvatarRepository() {
	}

	@Override
	public String getLink(Profile user, AvatarFormat format) {
		if (user == null || user.getPicture() == null) {
			return IMAGESLINK + "/anon.sgv";
		}

		String avatar = user.getPicture();
		if (avatar.matches("^(http:|https:).*")) {
			return isAvatarValidationEnabled()
				? PEOPLELINK + "/avatar?url=" + Utils.urlEncode(avatar)
				: avatar;
		}
		if (avatar.matches("^(data:).*")) {
			return avatar;
		}
		return IMAGESLINK + "/anon.sgv";
	}

	private String getUserPicture(Profile user) {
		String picture = user.getPicture();
		if (StringUtils.contains(picture, "gravatar.com") && !gravatarAvatarGenerator.isEnabled()) {
			String originalPicture = user.getOriginalPicture();
			if (StringUtils.contains(originalPicture, "gravatar.com")) {
				return gravatarAvatarGenerator.getLink(user); // returns default image, not gravatar
			} else {
				return StringUtils.isBlank(originalPicture) ? gravatarAvatarGenerator.getLink(user) : originalPicture;
			}
		}
		return picture;
	}

	private String getGravatar(String email) {
		if (!isGravatarEnabled()) {
			return getServerURL() + CONTEXT_PATH +  PEOPLELINK + "/avatar";
		}
		if (StringUtils.isBlank(email)) {
			return "https://www.gravatar.com/avatar?d=retro&size=400";
		}
		return "https://www.gravatar.com/avatar/" + Utils.md5(email.toLowerCase()) + "?size=400&d=retro";
       }
	@Override
	public String getAnonymizedLink(String data) {
		return gravatarAvatarGenerator.getLink(data);
	}

	public boolean isAvatarValidationEnabled() {
		return Config.getConfigBoolean("avatar_validation_enabled", false); // this should be deleted in the future
	}

	public static boolean isGravatarEnabled() {
		return Config.getConfigBoolean("gravatars_enabled", true);
	}
}
