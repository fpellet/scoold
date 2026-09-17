/*
 * Copyright 2013-2026 Erudika. https://erudika.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For issues and patches go to: https://github.com/erudika
 */
package com.erudika.scoold.controllers;

import com.erudika.para.client.ParaClient;
import com.erudika.para.core.email.Emailer;
import com.erudika.scoold.core.Post;
import com.erudika.scoold.core.Question;
import com.erudika.scoold.utils.CoreUtils;
import com.erudika.scoold.utils.LanguageUtils;
import com.erudika.scoold.utils.ScooldUtils;
import com.erudika.scoold.utils.avatars.AvatarRepositoryProxy;
import com.erudika.scoold.utils.avatars.GravatarAvatarGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.servlet.View;

public class QuestionControllerTest {

	private static final String ID = "1502039947555639296";
	private static final String SLUG = "je-suis-auto-entrepreneur-jai-besoin-dun-comptable-pour-maider-a-declarer-limpot-et-urssaf";

	private ParaClient pc;
	private QuestionController controller;

	@Before
	public void setUp() throws Exception {
		CoreUtils.registerCoreClasses();
		pc = mock(ParaClient.class);
		LanguageUtils langutils = mock(LanguageUtils.class);
		when(langutils.getProperLocale(anyString())).thenReturn(Locale.FRENCH);
		when(langutils.readLanguage(anyString())).thenReturn(Map.of("maxlength", "{0}"));
		ScooldUtils utils = new ScooldUtils(pc, langutils, mock(Emailer.class),
				mock(AvatarRepositoryProxy.class), mock(GravatarAvatarGenerator.class));

		Field instanceField = ScooldUtils.class.getDeclaredField("instance");
		instanceField.setAccessible(true);
		instanceField.set(null, utils);

		Question q = new Question();
		q.setId(ID);
		q.setTitle("Je suis auto entrepreneur, j'ai besoin d'un comptable pour m'aider à déclarer l'impôt et URSSAF");
		q.setSpace(Post.DEFAULT_SPACE);
		when(pc.read(ID)).thenReturn(q);

		controller = new QuestionController(utils);
	}

	@Test
	public void testGet_LegacyAccentedSlugIsPermanentlyRedirected() {
		HttpServletRequest req = request("je-suis-auto-entrepreneur-jai-besoin-dun-comptable-pour-maider-à-déclarer-limpôt-et-urssaf", null);
		String view = controller.get(ID, path(req), null, req, mock(HttpServletResponse.class), mock(Model.class));
		assertEquals("redirect:/question/" + ID + "/" + SLUG, view);
		verify(req).setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.MOVED_PERMANENTLY);
	}

	@Test
	public void testGet_MojibakeSlugIsPermanentlyRedirected() {
		HttpServletRequest req = request("maider-Ã -dÃ©clarer-limpÃ´t-et-urssaf", null);
		String view = controller.get(ID, path(req), null, req, mock(HttpServletResponse.class), mock(Model.class));
		assertEquals("redirect:/question/" + ID + "/" + SLUG, view);
		verify(req).setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.MOVED_PERMANENTLY);
	}

	@Test
	public void testGet_RedirectKeepsQueryString() {
		HttpServletRequest req = request("old-title", "sortby=newest&page=2");
		String view = controller.get(ID, path(req), "newest", req, mock(HttpServletResponse.class), mock(Model.class));
		assertEquals("redirect:/question/" + ID + "/" + SLUG + "?sortby=newest&page=2", view);
	}

	@Test
	public void testGet_CanonicalSlugIsNotRedirected() {
		HttpServletRequest req = request(SLUG, null);
		String view = controller.get(ID, path(req), null, req, mock(HttpServletResponse.class), mock(Model.class));
		assertEquals("base", view);
		verify(req, never()).setAttribute(eq(View.RESPONSE_STATUS_ATTRIBUTE), any());
	}

	@Test
	public void testGet_IdOnlyIsNotRedirected() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getServletPath()).thenReturn("/question/" + ID);
		when(req.getLocale()).thenReturn(Locale.FRENCH);
		String view = controller.get(ID, null, null, req, mock(HttpServletResponse.class), mock(Model.class));
		assertEquals("base", view);
		verify(req, never()).setAttribute(eq(View.RESPONSE_STATUS_ATTRIBUTE), any());
	}

	@Test
	public void testGet_SubPathIsNotRedirected() {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getServletPath()).thenReturn("/question/" + ID + "/old-title/edit-post-" + ID);
		when(req.getLocale()).thenReturn(Locale.FRENCH);
		String view = controller.get(ID, "old-title", null, req, mock(HttpServletResponse.class), mock(Model.class));
		assertEquals("base", view);
		verify(req, never()).setAttribute(eq(View.RESPONSE_STATUS_ATTRIBUTE), any());
	}

	private static HttpServletRequest request(String title, String queryString) {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getServletPath()).thenReturn("/question/" + ID + "/" + title);
		when(req.getQueryString()).thenReturn(queryString);
		when(req.getLocale()).thenReturn(Locale.FRENCH);
		return req;
	}

	private static String path(HttpServletRequest req) {
		// the decoded {title} path variable, as Spring would extract it
		return req.getServletPath().substring(("/question/" + ID + "/").length());
	}
}
