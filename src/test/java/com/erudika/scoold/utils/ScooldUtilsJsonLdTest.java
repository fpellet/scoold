/*
 * Copyright 2013-2026 Erudika. http://erudika.com
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import com.erudika.para.client.ParaClient;
import com.erudika.para.core.email.Emailer;
import com.erudika.scoold.utils.avatars.AvatarRepositoryProxy;
import com.erudika.scoold.utils.avatars.GravatarAvatarGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Before;
import org.junit.Test;

public class ScooldUtilsJsonLdTest {

	private static final String VALUE = "Faut-il déclarer l'ACRE ? \"cœur\" & <b>gras</b> </script><script>x()\\ \n\t";

	private ScooldUtils utils;

	@Before
	public void setUp() {
		utils = new ScooldUtils(mock(ParaClient.class), mock(LanguageUtils.class), mock(Emailer.class),
				mock(AvatarRepositoryProxy.class), mock(GravatarAvatarGenerator.class));
	}

	@Test
	public void producesValidJsonStringContent() throws Exception {
		String parsed = new ObjectMapper().readValue("\"" + utils.getJsonLdValue(VALUE) + "\"", String.class);
		assertEquals(VALUE, parsed);
	}

	@Test
	public void isNotAlteredByHtmlEscaping() {
		String json = utils.getJsonLdValue(VALUE);
		assertEquals(json, StringEscapeUtils.escapeHtml4(json));
		assertFalse(json.contains("<"));
	}

	@Test
	public void keepsUrlsReadable() {
		assertEquals("https://example.com/question/123/foo-bar?a=1#post-4",
				utils.getJsonLdValue("https://example.com/question/123/foo-bar?a=1#post-4"));
		assertEquals("-", utils.getJsonLdValue(null));
	}
}
