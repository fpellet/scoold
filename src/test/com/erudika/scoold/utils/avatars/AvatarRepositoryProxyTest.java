package com.erudika.scoold.utils.avatars;

import com.erudika.para.core.User;
import com.erudika.scoold.core.Profile;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AvatarRepositoryProxyTest {
	private GravatarAvatarGenerator gravatarAvatarGeneratorFake;
	private Profile profile;

	@Before
	public void setUp() {
		this.gravatarAvatarGeneratorFake = mock(GravatarAvatarGenerator.class);
		this.profile = new Profile();
		this.profile.setUser(new User());
	}

	@Test
	public void should_use_gravatar_then_custom_link_then_default_if_gravatar_enable_and_custom_link_enable() {
		AvatarConfig config = mock(AvatarConfig.class);
		when(config.getDefaultAvatar()).thenReturn("/default_avatar");
		when(config.isGravatarEnabled()).thenReturn(true);
		when(config.isCustomLinkEnabled()).thenReturn(true);
		AvatarRepository repository = new AvatarRepositoryProxy(gravatarAvatarGeneratorFake, config);

		when(gravatarAvatarGeneratorFake.getRawLink("A")).thenReturn("https://gravatarA");
		String result = repository.getAnonymizedLink("A");
		assertEquals("https://gravatarA", result);

		when(gravatarAvatarGeneratorFake.isLink("https://gravatarA")).thenReturn(true);
		when(gravatarAvatarGeneratorFake.configureLink("https://gravatarA", AvatarFormat.Profile)).thenReturn("https://gravatarA?profile");
		profile.setPicture("https://gravatarA");
		String gravatarLink = repository.getLink(profile, AvatarFormat.Profile);
		assertEquals(gravatarLink, "https://gravatarA?profile");

		profile.setPicture("https://avatar");
		String customLinkLink = repository.getLink(profile, AvatarFormat.Profile);
		assertEquals(customLinkLink, profile.getPicture());

		profile.setPicture("bad:avatar");
		String defaultLink = repository.getLink(profile, AvatarFormat.Profile);
		assertEquals(new DefaultAvatarRepository(config).getLink(profile, AvatarFormat.Profile), defaultLink);
	}

	@Test
	public void should_not_use_gravatar_if_gravatar_disable() {
		AvatarConfig config = mock(AvatarConfig.class);
		when(config.isGravatarEnabled()).thenReturn(false);
		when(config.isCustomLinkEnabled()).thenReturn(true);
		AvatarRepository repository = new AvatarRepositoryProxy(gravatarAvatarGeneratorFake, config);

		when(gravatarAvatarGeneratorFake.getRawLink("A")).thenReturn("https://gravatarA");
		String result = repository.getAnonymizedLink("A");
		assertNotEquals("https://gravatarA", result);
	}

	@Test
	public void should_not_use_custom_link_if_disable() {
		AvatarConfig config = mock(AvatarConfig.class);
		when(config.getDefaultAvatar()).thenReturn("/default_avatar");
		when(config.isGravatarEnabled()).thenReturn(true);
		when(config.isCustomLinkEnabled()).thenReturn(false);
		AvatarRepository repository = new AvatarRepositoryProxy(gravatarAvatarGeneratorFake, config);

		when(gravatarAvatarGeneratorFake.getRawLink("A")).thenReturn("https://gravatarA");
		String result = repository.getAnonymizedLink("A");
		assertEquals("https://gravatarA", result);

		when(gravatarAvatarGeneratorFake.isLink("https://gravatarA")).thenReturn(true);
		when(gravatarAvatarGeneratorFake.configureLink("https://gravatarA", AvatarFormat.Profile)).thenReturn("https://gravatarA?profile");
		profile.setPicture("https://gravatarA");
		String gravatarLink = repository.getLink(profile, AvatarFormat.Profile);
		assertEquals(gravatarLink, "https://gravatarA?profile");

		profile.setPicture("https://avatar");
		String defaultLink = repository.getLink(profile, AvatarFormat.Profile);
		assertEquals(new DefaultAvatarRepository(config).getLink(profile, AvatarFormat.Profile), defaultLink);
	}
}
