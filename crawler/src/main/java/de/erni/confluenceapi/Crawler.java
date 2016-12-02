package de.erni.confluenceapi;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {

	
	private static final Map<String, String> HEADER_PATTERNS = new HashMap<String, String>();
	private Map<String, String> categories = new HashMap<String, String>();
	private String eol = System.getProperty("line.separator");
	private StringBuilder aiml = new StringBuilder();
	private String username;
	private String password;
	
	
    public Crawler(){
    	HEADER_PATTERNS.put("Benefit", "WHAT IS THE BENEFIT OF @serviceName");
		HEADER_PATTERNS.put("Problems &amp; goals", "WHAT IS @serviceName");
		HEADER_PATTERNS.put("Input", "WHAT DO YOU NEED FOR @serviceName");
		HEADER_PATTERNS.put("Output", "WHAT DO YOU GET FROM @serviceName");
		HEADER_PATTERNS.put("Roles", "WHAT ARE THE ROLES OF @serviceName");
		HEADER_PATTERNS.put("Workflow / Process", "WHAT DO YOU KNOW ABOUT @serviceName");
		HEADER_PATTERNS.put("Tools &amp; templates", "WHICH TOOLS DO YOU NEEDE FOR @serviceName");
		HEADER_PATTERNS.put("Lessons learnt", "WHAT HAVE YOU LEARNED FROM @serviceName");
    }
	
    
    public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public void crawl() throws Exception
    {
//        final long pageId = 45219938; // Eigene Testseite
//        final long pageId =  32966160; // Service: https://wiki.erninet.ch/display/SWi/Services
    	final long pageId =  45810095; // Service: https://wiki.erninet.ch/display/SWi/Services
    	
    	HTTPClient client = new HTTPClient();
        JSONObject page = client.getPage(pageId, HTTPClient.REST_SEARCH_CHILDREN, username, password);
        
        JSONArray pages = (JSONArray) page.get("results");
        
        
		aiml.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>").append(eol);
		aiml.append("<aiml>").append(eol);
        
       	for(int i = 0; i < pages.length(); i++){
       		JSONObject item = (JSONObject) pages.get(i);
       		page = client.getPage(item.getLong("id"), HTTPClient.REST_READ_CONTENT, username, password);
       		String html = page.getJSONObject("body").getJSONObject("storage").get("value").toString();
       		String title = item.get("title").toString();
       		System.out.println(html);
       		createAiml(Jsoup.parse(html), title);

       	}
       	
       	aiml.append("</aiml>").append(eol);
		System.out.println(aiml.toString());
		
		Tools.writeFile("C:\\project\\git\\pb-java\\bot\\erni_services.aiml", aiml.toString());

    }
    
    
    
    public  void createAiml(Document doc, String serviceName) {

		Elements headers = doc.getElementsByTag("h1");

		for (int i = 0; i < headers.size() - 1; i++) {
			Element currentElement = headers.get(i).nextElementSibling();
			String header = headers.get(i).text();
			System.out.println("########" + header);
			StringBuilder template = new StringBuilder();
			while (currentElement!=null && !currentElement.tagName().equals("h1")) {
				System.out.print(currentElement.text());
				template.append(currentElement.text());
				currentElement = currentElement.nextElementSibling();
			}

			String newTemplate = HEADER_PATTERNS.get(header);
			if (newTemplate != null && !"".equals(template.toString())) {
				categories.put(newTemplate.replace("@serviceName", serviceName.toUpperCase()), template.toString());
			}

			System.out.println("\n");
		}
		
		
		for (Map.Entry<String, String> entry : categories.entrySet()) {
			aiml.append("<category>").append(eol);
			aiml.append("<pattern>").append(entry.getKey()).append("</pattern>").append(eol);
			aiml.append("<template>").append(StringEscapeUtils.escapeXml(entry.getValue())).append("</template>").append(eol);
			aiml.append("</category>").append(eol);
			
		}
		
	}
    
}
