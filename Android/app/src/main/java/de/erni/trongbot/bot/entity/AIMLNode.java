package de.erni.trongbot.bot.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AIMLNode {
	
	private String pattern;
	private String template;
	private List<String> random;
	private Map<String, AIMLNode> that;
	private Map<String, AIMLNode> elements;
	private int matchRank;
	
	public AIMLNode() {
		elements = new HashMap<String, AIMLNode>();
		that = new HashMap<String, AIMLNode>();
	}
	
	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public List<String> getRandom() {
		return random;
	}

	public void setRandom(List<String> random) {
		this.random = random;
	}

	public void addRandomTemplate(String template) {
		random.add(template);
	}

	public Map<String, AIMLNode> getElements() {
		return elements;
	}
	
	public void addChild(String key, AIMLNode value) {
		elements.put(key, value);
	}

	public AIMLNode getChild(String string) {
		return elements.get(string);
	}

	public boolean hasPattern() {	
		return (random!=null && random.size() > 0) || template != null;
	}

	public Map<String, AIMLNode> getThat() {
		return that;
	}

	public void setThat(Map<String, AIMLNode> that) {
		this.that = that;
	}
	
	public void addThat(String thatPattern, AIMLNode element){
		this.that.put(thatPattern, element);
	}

	public int getMatchRank() {
		return matchRank;
	}

	public void setMatchRank(int matchRank) {
		this.matchRank = matchRank;
	}
	
	public void addMatchRank(int matchRank) {
		this.matchRank = this.matchRank + matchRank;
	}

}
