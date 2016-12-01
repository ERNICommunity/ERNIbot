package de.erni.confluenceapi;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class HTTPClient {

	
	public static final String REST_SEARCH_CHILDREN = "search?cql=parent=%s&";
	public static final String REST_READ_CONTENT = "%s?";
	private static final String BASE_URL = "https://wiki.erninet.ch";
	private static final String ENCODING = "utf-8";

	private static String getContentRestUrl(final Long contentId, String method, final String[] expansions, String username, String password)
			throws UnsupportedEncodingException {
		final String expand = URLEncoder.encode(StringUtils.join(expansions, ","), ENCODING);
		String load = String.format("%s/rest/api/content/" + method + "expand=%s&os_authType=basic&os_username=%s&os_password=%s",
				BASE_URL, contentId, expand, URLEncoder.encode(username, ENCODING),
				URLEncoder.encode(password, ENCODING));

		return load;
	}

	public JSONObject getPage(long pageId, String method, String username, String password) throws ClientProtocolException, IOException, ParseException {

		HttpClient client = new DefaultHttpClient();

		// Get current page version
		String pageObj = null;
		HttpEntity pageEntity = null;
		try {
			HttpGet getPageRequest = new HttpGet(getContentRestUrl(pageId, method, new String[] { "body.storage", "version" }, username, password));
			HttpResponse getPageResponse = client.execute(getPageRequest);
			pageEntity = getPageResponse.getEntity();

			pageObj = IOUtils.toString(pageEntity.getContent());

		} finally {
			if (pageEntity != null) {
				EntityUtils.consume(pageEntity);
			}
		}

		// Parse response into JSON
		return new JSONObject(pageObj);
	}
	
//	
// Update page
// The updated value must be Confluence Storage Format (https://confluence.atlassian.com/display/DOC/Confluence+Storage+Format), NOT HTML.
//page.getJSONObject("body").getJSONObject("storage").put("value", "hello, neue world");
//
//int currentVersion = page.getJSONObject("version").getInt("number");
//page.getJSONObject("version").put("number", currentVersion + 1);

//// Send update request
//HttpEntity putPageEntity = null;
//
//try
//{
//    HttpPut putPageRequest = new HttpPut(getContentRestUrl(pageId, new String[]{}));
//
//    StringEntity entity = new StringEntity(page.toString(), ContentType.APPLICATION_JSON);
//    putPageRequest.setEntity(entity);
//
//    HttpResponse putPageResponse = client.execute(putPageRequest);
//    putPageEntity = putPageResponse.getEntity();
//
//    System.out.println("Put Page Request returned " + putPageResponse.getStatusLine().toString());
//    System.out.println("");
//    System.out.println(IOUtils.toString(putPageEntity.getContent()));
//}
//finally
//{
//    EntityUtils.consume(putPageEntity);
//}

}
