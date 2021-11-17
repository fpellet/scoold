package com.erudika.scoold.utils.avatars;

import com.erudika.para.core.User;
import com.erudika.scoold.core.Profile;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.mockito.Mockito.*;

public class CloudinaryAvatarRepositoryTest {
	private CloudinaryAvatarRepository repository;
	private AvatarRepository defaultRepository;
	private Profile profile;

	@Before
	public void setUp(){
		this.profile = new Profile();
		this.profile.setUser(new User());
		this.defaultRepository = new DefaultAvatarRepository(new AvatarConfig());
		this.repository = new CloudinaryAvatarRepository(defaultRepository);
	}

	@Test
	public void getLink_should_return_default_if_no_profile() {
		String avatar = repository.getLink(null, AvatarFormat.Profile);

		assertEquals(defaultRepository.getLink(null, AvatarFormat.Profile), avatar);
	}

	@Test
	public void getLink_should_return_default_if_no_cloudinary_link() {
		profile.setPicture("https://avatar");

		String avatar = repository.getLink(profile, AvatarFormat.Profile);

		assertEquals(defaultRepository.getLink(profile, AvatarFormat.Profile), avatar);
	}

	@Test
	public void getLink_should_return_formatted_link_if_cloudinary_link() {
		profile.setPicture("https://res.cloudinary.com/test/image/upload/avatar.jpg");

		String avatar = repository.getLink(profile, AvatarFormat.Profile);
		assertEquals("https://res.cloudinary.com/test/image/upload/t_profile/avatar.jpg", avatar);

		String avatar25 = repository.getLink(profile, AvatarFormat.Square25);
		assertEquals("https://res.cloudinary.com/test/image/upload/t_square25/avatar.jpg", avatar25);
	}

	@Test
	public void getAnonymizedLink_should_use_default_repository() {
		AvatarRepository defaultRepository = mock(AvatarRepository.class);
		AvatarRepository repository = new CloudinaryAvatarRepository(defaultRepository);
		when(defaultRepository.getAnonymizedLink("A")).thenReturn("https://avatar");

		String avatar = repository.getAnonymizedLink("A");

		assertEquals("https://avatar", avatar);
	}

	@Test
	public void store_should_change_profile_and_user_picture() {
		String avatar = "https://res.cloudinary.com/test/image/upload/avatar.jpg";

		AvatarStorageResult result = repository.store(profile, avatar);

		assertEquals(AvatarStorageResult.userChanged(), result);
		assertEquals(avatar, profile.getPicture());
		assertEquals(avatar, profile.getUser().getPicture());
	}

	@Test
	public void store_should_change_profile_picture_but_not_user_is_already_this_picture() {
		String avatar = "https://res.cloudinary.com/test/image/upload/avatar.jpg";
		profile.getUser().setPicture(avatar);

		AvatarStorageResult result = repository.store(profile, avatar);

		assertEquals(AvatarStorageResult.profileChanged(), result);
		assertEquals(avatar, profile.getPicture());
		assertEquals(avatar, profile.getUser().getPicture());
	}

	@Test
	public void store_should_call_next_repository_if_bad_url() {
		AvatarRepository defaultRepository = mock(AvatarRepository.class);
		AvatarRepository repository = new CloudinaryAvatarRepository(defaultRepository);
		String avatar = "https://avatar";
		when(defaultRepository.store(profile, avatar)).thenReturn(AvatarStorageResult.failed());

		AvatarStorageResult result = repository.store(profile, avatar);

		verify(defaultRepository).store(profile, avatar);
		assertEquals(AvatarStorageResult.failed(), result);
		assertNotEquals(avatar, profile.getPicture());
		assertNotEquals(avatar, profile.getUser().getPicture());
	}
}
