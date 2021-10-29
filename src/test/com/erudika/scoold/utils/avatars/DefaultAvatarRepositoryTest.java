package com.erudika.scoold.utils.avatars;

import com.erudika.scoold.core.Profile;
import org.junit.Before;
import org.junit.Test;

import static com.erudika.scoold.ScooldServer.IMAGESLINK;
import static org.junit.Assert.*;

public class DefaultAvatarRepositoryTest {
	private DefaultAvatarRepository repository;

	@Before
	public void setUp(){
		this.repository = new DefaultAvatarRepository();
	}

	@Test
	public void getLink_should_return_always_default_avatar() {
		assertEquals(IMAGESLINK + "/anon.sgv", repository.getLink(new Profile(), AvatarFormat.Profile));
		assertEquals( repository.getLink(new Profile(), AvatarFormat.Square32), repository.getLink(new Profile(), AvatarFormat.Profile));
	}

	@Test
	public void getAnonymizedLink_should_always_return_default_avatar() {
		assertEquals(IMAGESLINK + "/anon.sgv", repository.getAnonymizedLink("A"));
		assertEquals( repository.getAnonymizedLink("A"), repository.getAnonymizedLink("B"));
	}

	@Test
	public void store_should_nothing() {
		Profile profile = new Profile();

		AvatarStorageResult result = repository.store(profile, "https://avatar");

		assertEquals(AvatarStorageResult.failed(), result);
	}
}
