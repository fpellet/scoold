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
package com.erudika.scoold.utils;

import static com.erudika.scoold.utils.HttpUtils.getDefaultPort;
import jakarta.servlet.http.HttpServletRequest;
import java.net.URI;
import static org.junit.Assert.*;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpUtilsTest {

	@Test
	public void testGetFullUrl_AsciiPathAndQuery() {
		HttpServletRequest req = request("/question/123/some-title", "page=2&sortby=newest");
		assertEquals("/question/123/some-title?page=2&sortby=newest", HttpUtils.getFullUrl(req, true));
		assertTrue(HttpUtils.getFullUrl(req, false).endsWith("/question/123/some-title?page=2&sortby=newest"));
	}

	@Test
	public void testGetFullUrl_AccentedPathIsEncoded() {
		HttpServletRequest req = request("/question/123/maider-à-déclarer-limpôt", null);
		assertEquals("/question/123/maider-%C3%A0-d%C3%A9clarer-limp%C3%B4t", HttpUtils.getFullUrl(req, true));
	}

	@Test
	public void testGetFullUrl_IllegalUriCharsDoNotThrow() {
		// UTF-8 bytes of "à" decoded as ISO-8859-1 -> "Ã" + U+00A0 (rejected by java.net.URI)
		HttpServletRequest req = request("/question/123/maider-Ã -dÃ©clarer", null);
		assertEquals("/question/123/maider-%C3%83%C2%A0-d%C3%83%C2%A9clarer", HttpUtils.getFullUrl(req, true));
	}

	private static HttpServletRequest request(String servletPath, String queryString) {
		HttpServletRequest req = mock(HttpServletRequest.class);
		when(req.getServletPath()).thenReturn(servletPath);
		when(req.getQueryString()).thenReturn(queryString);
		return req;
	}

	@Test
	public void testIsSameOrigin_SameServer() {
		assertTrue(isSameOrigin(
				"https://example.com/scoold/questions",
				"https://example.com"));
	}

	@Test
	public void testIsSameOrigin_SameServerDifferentPath() {
		assertTrue(isSameOrigin(
				"https://example.com/other/path",
				"https://example.com"));
	}

	@Test
	public void testIsSameOrigin_ExploitSubdomain() {
		assertFalse(isSameOrigin(
				"https://example.com.evil.com/steal",
				"https://example.com"));
	}

	@Test
	public void testIsSameOrigin_DifferentScheme() {
		assertFalse(isSameOrigin(
				"http://example.com/scoold",
				"https://example.com"));
	}

	@Test
	public void testIsSameOrigin_DifferentHost() {
		assertFalse(isSameOrigin(
				"https://evil.com/path",
				"https://example.com"));
	}

	@Test
	public void testIsSameOrigin_ExplicitPort() {
		assertTrue(isSameOrigin(
				"https://example.com:443/scoold",
				"https://example.com"));
	}

	@Test
	public void testIsSameOrigin_DifferentPort() {
		assertFalse(isSameOrigin(
				"https://example.com:8443/admin",
				"https://example.com"));
	}

	@Test
	public void testIsSameOrigin_CredentialsInUrl() {
		assertFalse(isSameOrigin(
				"https://evil.com@example.com/scoold",
				"https://example.com"));
	}

	@Test
	public void testIsSameOrigin_NullHost() {
		assertFalse(isSameOrigin("", "https://example.com"));
	}

	@Test
	public void testIsSameOrigin_InvalidUri() {
		assertFalse(isSameOrigin("///bad", "https://example.com"));
	}

	@Test
	public void testGetDefaultPort() {
		assertEquals(443, getDefaultPort("https"));
		assertEquals(80, getDefaultPort("http"));
		assertEquals(-1, getDefaultPort("ftp"));
	}

	@Test
	public void testIsSameOrigin_HttpDefaultPort() {
		assertTrue(isSameOrigin(
				"http://example.com/scoold",
				"http://example.com"));
		assertTrue(isSameOrigin(
				"http://example.com:80/scoold",
				"http://example.com"));
	}

	@Test
	public void testIsSameOrigin_StartsWithAttack() {
		assertFalse(isSameOrigin(
				"https://example.com@evil.com/steal",
				"https://example.com"));
	}

	private static boolean isSameOrigin(String uri1, String uri2) {
		return HttpUtils.isSameOrigin(URI.create(uri1), URI.create(uri2));
	}
}
