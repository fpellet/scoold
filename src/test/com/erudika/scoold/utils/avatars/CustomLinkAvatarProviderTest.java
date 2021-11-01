package com.erudika.scoold.utils.avatars;

import com.erudika.para.core.User;
import com.erudika.scoold.core.Profile;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class CustomLinkAvatarProviderTest {
	private CustomLinkAvatarProvider repository;
	private AvatarProvider defaultRepository;
	private Profile profile;
	private GravatarAvatarGenerator gravatarGenerator;
	private AvatarConfig config;

	@Before
	public void setUp(){
		this.config = mock(AvatarConfig.class);
		this.profile = new Profile();
		this.profile.setUser(new User());
		this.defaultRepository = new DefaultAvatarProvider(this.config);
		this.gravatarGenerator = new GravatarAvatarGenerator(config);
		this.repository = new CustomLinkAvatarProvider(gravatarGenerator, config, defaultRepository);
	}

	@Test
	public void getLink_should_return_default_if_no_profile() {
		String avatar = repository.getLink(null, AvatarFormat.Profile);

		assertEquals(defaultRepository.getLink(null, AvatarFormat.Profile), avatar);
	}

	@Test
	public void getLink_should_return_default_if_no_avatar() {
		profile.setPicture("");

		String avatar = repository.getLink(profile, AvatarFormat.Profile);

		assertEquals(defaultRepository.getLink(null, AvatarFormat.Profile), avatar);
	}

	@Test
	public void getLink_should_return_avatar_if_picture_with_url_https() {
		profile.setPicture("https://avatar");

		String avatar = repository.getLink(profile, AvatarFormat.Profile);

		assertEquals("https://avatar", avatar);
	}

	@Test
	public void getLink_should_return_avatar_endpoint_if_picture_with_url_and_validation_enabled() {
		profile.setPicture("https://avatar");
		when(config.isAvatarValidationEnabled()).thenReturn(true);

		String avatar = repository.getLink(profile, AvatarFormat.Profile);

		assertEquals("/people/avatar?url=https%3A%2F%2Favatar", avatar);
	}

	@Test
	public void getLink_should_return_avatar_if_picture_with_url_http() {
		profile.setPicture("http://avatar");

		String avatar = repository.getLink(profile, AvatarFormat.Profile);

		assertEquals("http://avatar", avatar);
	}

	@Test
	public void getLink_should_return_avatar_if_picture_with_data() {
		profile.setPicture("data:AvAtar");

		String avatar = repository.getLink(profile, AvatarFormat.Profile);

		assertEquals("data:AvAtar", avatar);
	}

	@Test
	public void getLink_should_return_default_if_picture_has_unknown_format() {
		profile.setPicture("bad:AvAtar");

		String avatar = repository.getLink(profile, AvatarFormat.Profile);

		assertEquals(defaultRepository.getLink(null, AvatarFormat.Profile), avatar);
	}

	@Test
	public void getLink_should_return_original_picture_if_picture_is_gravatar() {
		profile.setOriginalPicture("https://avatar");
		profile.setPicture(gravatarGenerator.getRawLink("toto@example.com"));

		String avatar = repository.getLink(profile, AvatarFormat.Profile);

		assertEquals("https://avatar", avatar);
	}

	@Test
	public void getLink_should_return_default_if_picture_and_original_picture_are_gravatar() {
		profile.setOriginalPicture(gravatarGenerator.getRawLink("toto@example.com"));
		profile.setPicture(gravatarGenerator.getRawLink("toto@example.com"));

		String avatar = repository.getLink(profile, AvatarFormat.Profile);

		assertEquals(defaultRepository.getLink(null, AvatarFormat.Profile), avatar);
	}

	@Test
	public void getAnonymizedLink_should_use_default_repository() {
		AvatarProvider defaultRepository = mock(AvatarProvider.class);
		AvatarProvider repository = new CustomLinkAvatarProvider(gravatarGenerator, config, defaultRepository);
		when(defaultRepository.getAnonymizedLink("A")).thenReturn("https://avatar");

		String avatar = repository.getAnonymizedLink("A");

		assertEquals("https://avatar", avatar);
	}
}
