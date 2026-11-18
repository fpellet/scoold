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
package com.erudika.scoold.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import com.erudika.para.client.ParaClient;
import com.erudika.para.core.email.Emailer;
import com.erudika.scoold.ScooldServer;
import com.erudika.scoold.core.Profile;
import com.erudika.scoold.core.Question;
import com.erudika.scoold.utils.LanguageUtils;
import com.erudika.scoold.utils.ScooldUtils;
import com.erudika.scoold.utils.avatars.AvatarRepositoryProxy;
import com.erudika.scoold.utils.avatars.GravatarAvatarGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Renders the real templates with the production Velocity configuration (global HTML escaping of references).
 */
public class TemplateEscapingTest {

	private static final String TITLE = "Faut-il déclarer l'ACRE ? \"cœur\" & <b>gras</b> </script>";

	private VelocityEngine engine;
	private ScooldUtils utils;

	@Before
	public void setUp() throws Exception {
		engine = new ScooldServer().velocityConfigBean().createVelocityEngine();
		utils = new ScooldUtils(mock(ParaClient.class), mock(LanguageUtils.class), mock(Emailer.class),
				mock(AvatarRepositoryProxy.class), mock(GravatarAvatarGenerator.class));
	}

	@Test
	public void questionJsonLdIsValidJson() throws Exception {
		Profile author = new Profile("u1", "Zoé d'Arc \"Œ\"");
		Question q = new Question();
		q.setId("123");
		q.setTitle(TITLE);
		q.setBody("Corps & <i>texte</i> l'été");
		q.setAuthor(author);
		q.setVotes(0);

		VelocityContext ctx = new VelocityContext();
		ctx.put("path", "question.vm");
		ctx.put("showPost", q);
		ctx.put("answerslist", List.of());
		ctx.put("cspNonce", "nonce");
		ctx.put("scooldUtils", utils);
		StringWriter out = new StringWriter();
		engine.evaluate(ctx, out, "test", "#renderJSONLD()");

		String html = out.toString();
		String json = html.substring(html.indexOf('>') + 1, html.lastIndexOf("</script>"));
		JsonNode question = new ObjectMapper().readTree(json).get("mainEntity");
		assertEquals(TITLE, question.get("name").asText());
		assertEquals(author.getName(), question.get("author").get("name").asText());
		assertEquals(ScooldUtils.getConfig().serverUrl() + q.getPostLinkForRedirect(), question.get("url").asText());
	}

	@Test
	public void feedIsValidXml() throws Exception {
		VelocityContext ctx = new VelocityContext();
		ctx.put("_xml_title", SearchController.xml("Communauté - Questions récentes"));
		ctx.put("_xml_description", SearchController.xml("Un résumé & plus"));
		ctx.put("_xml_baseurl", SearchController.xml("https://example.com/"));
		ctx.put("_xml_updated", SearchController.xml("Wed, 18 Nov 2026 10:00:00 +0000"));
		ctx.put("entries", List.of(Map.of(
				"xml_url", SearchController.xml("https://example.com/question/123"),
				"xml_id", SearchController.xml("https://example.com/question/123/faut-il-déclarer"),
				"xml_title", SearchController.xml(TITLE),
				"xml_body", SearchController.xml("<p>Corps &amp; texte l&#39;été</p>"),
				"xml_created", SearchController.xml("Wed, 18 Nov 2026 10:00:00 +0000"))));
		StringWriter out = new StringWriter();
		engine.getTemplate("feed.vm", "UTF-8").merge(ctx, out);

		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().
				parse(new ByteArrayInputStream(out.toString().getBytes(StandardCharsets.UTF_8)));
		assertEquals("Communauté - Questions récentes",
				doc.getElementsByTagName("title").item(0).getTextContent());
		assertEquals(TITLE, doc.getElementsByTagName("title").item(1).getTextContent());
		assertEquals("<p>Corps &amp; texte l&#39;été</p>",
				doc.getElementsByTagName("description").item(1).getTextContent());
	}
}
