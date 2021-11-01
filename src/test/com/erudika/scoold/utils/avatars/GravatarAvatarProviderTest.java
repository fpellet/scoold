package com.erudika.scoold.utils.avatars;

import com.erudika.para.core.User;
import com.erudika.scoold.core.Profile;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class GravatarAvatarProviderTest {
	private GravatarAvatarProvider repository;
	private AvatarProvider defaultRepository;
	private Profile profile;
	private GravatarAvatarGenerator gravatarGenerator;
	private AvatarConfig config;

	@Before
	public void setUp(){
		this.profile = new Profile();
		this.profile.setUser(new User());
		this.config = new AvatarConfig();
		this.defaultRepository = new DefaultAvatarProvider(config);
		this.gravatarGenerator = new GravatarAvatarGenerator(config);
		this.repository = new GravatarAvatarProvider(gravatarGenerator, defaultRepository);
	}

	@Test
	public void getLink_should_return_default_if_no_profile() {
		String avatar = repository.getLink(null, AvatarFormat.Profile);

		assertEquals(defaultRepository.getLink(null, AvatarFormat.Profile), avatar);
	}

	@Test
	public void getLink_should_return_gravatar_link_if_no_picture() {
		profile.setPicture("");

		String avatar = repository.getLink(profile, AvatarFormat.Profile);

		assertEquals(gravatarGenerator.configureLink(gravatarGenerator.getRawLink(profile), AvatarFormat.Profile), avatar);
	}

	@Test
	public void getLink_should_return_gravatar_link_with_email() {
		profile.getUser().setEmail("toto@example.com");

		String avatar = repository.getLink(profile, AvatarFormat.Square32);

		assertEquals(gravatarGenerator.getLink(profile, AvatarFormat.Square32), avatar);
	}

	@Test
	public void getLink_should_return_default_if_picture_isnot_empty() {
		profile.getUser().setEmail("toto@example.com");
		profile.setPicture("https://avatar");

		String avatar = repository.getLink(profile, AvatarFormat.Profile);

		assertEquals(defaultRepository.getLink(null, AvatarFormat.Profile), avatar);
	}

	@Test
	public void getLink_should_configure_link_if_picture_is_a_gravatar() {
		profile.getUser().setEmail("toto@example.com");
		profile.setPicture(gravatarGenerator.getRawLink("titi@example.com"));

		String avatar = repository.getLink(profile, AvatarFormat.Square32);

		assertEquals(gravatarGenerator.configureLink(gravatarGenerator.getRawLink("titi@example.com"), AvatarFormat.Square32), avatar);
	}

	@Test
	public void getAnonymizedLink_should_return_gravatar_with_data() {
		String avatar = repository.getAnonymizedLink("A");

		assertEquals(gravatarGenerator.getRawLink("A"), avatar);
	}
}
