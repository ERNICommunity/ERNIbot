package de.erni.trongbot.bot;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ch.erni.ernibot.bots.Bot;
import de.erni.trongbot.bot.entity.AIMLNode;
import de.erni.trongbot.bot.entity.Memory;
import de.erni.trongbot.bot.entity.Message;
import de.erni.trongbot.bot.helper.ChatUtils;
import de.erni.trongbot.bot.xml.AIMLHandler;

public class AIMLInterpreter {
	
	
	private final String EMPTY_STAR = "";
	private static AIMLNode aimlMemory;
	private static AIMLInterpreter interpreter;
	private static final String TAG = "AIMLInterpereter";
	private Context context;
	private Memory memory;
	private static final int MATCH_RANK_THAT = 50;
	private static final int MATCH_RANK_FULL = 20;
	private static final int MATCH_RANK_STAR = 2;
	private static final int MATCH_RANK_STAR_AT_BEGIN = 5;
	private static final int MATCH_RANK_STAR_AT_END = 10;
	private static final int MATCH_RANK_STAR_IN_THE_MIDDLE = 7;
	
	private Pattern srai = Pattern.compile("<" + AIMLHandler.TAG_SRAI + ">(.+?)</" + AIMLHandler.TAG_SRAI + ">");
	
	public static AIMLInterpreter getInstance(Context context){
		if (interpreter==null){
			interpreter = new AIMLInterpreter(context);
		}
		return interpreter;
	}
	
	private AIMLInterpreter(Context context){
		this.context = context;
		aimlMemory = new AIMLNode();
		memory = Memory.getInstance();
		parse();
	}
	
	private void parse(){
		AIMLHandler handler = new AIMLHandler(aimlMemory);
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			
		    InputStream    xmlInput  = context.getAssets().open("German-standalone.aiml");
		    SAXParser      saxParser = factory.newSAXParser();
 
		    saxParser.parse(xmlInput, handler);

		} catch (Throwable err) {
		    err.printStackTrace ();
		}		
	}

	public void sendMessageToBot(Message userMessage, Message botMessage){
		botMessage.text = match(userMessage.text);
	}

	/**
	 * Core method to find the fitting pattern
	 * 
	 * @param query
	 * @return
	 */
	public String match(String pattern){
		String star = "";
		String answer = null;
		String query = ChatUtils.preparePattern(pattern);
		
		AIMLNode currentNode = aimlMemory;
		List<AIMLNode> matches = new ArrayList<AIMLNode>();
		
		String[] words = query.split(" ");
		for (int i = 0; i < words.length; i++){
			
			matches.addAll(findMatchWithStar(currentNode, i, words));
			
			currentNode = currentNode.getChild(words[i]);
				
			if (currentNode == null) {
				break;
			}
			
			if (i == words.length-1 && currentNode.hasPattern()){
				matches.add(currentNode);
			}
		}
		
		
		AIMLNode match = findBestMatchByRank(matches, pattern);
		
		if (pattern!=null) {
			star = getStar(match.getPattern(), pattern);		
		}
		
		answer = readAnswer(match, star);
		answer = answer.replace("<" + AIMLHandler.TAG_STAR + "/>", star);
		
		answer = answer.replace("<" + AIMLHandler.TAG_THAT + "/>", memory.getThat());
		
		if (answer.contains("<" + AIMLHandler.TAG_SR + "/>")){
			answer = answer.replace("<" + AIMLHandler.TAG_SR + "/>", match(star));
		}
		
		Matcher sraiMatcher = srai.matcher(answer);
		if (sraiMatcher.find()){
			answer = answer.replace(sraiMatcher.group(0), match(sraiMatcher.group(1)));	
		}

		return answer;
	}

	
	private AIMLNode findBestMatchByRank(List<AIMLNode> matches, String pattern){
		AIMLNode bestMatch = new AIMLNode();
		bestMatch.setMatchRank(-1);
		
		for (int i = 0; i < matches.size(); i++){
			AIMLNode match = matches.get(i);
			match.setMatchRank(i);
			
			if (match.getThat()!=null && match.getThat().size() > 0){
				String thatPattern = ChatUtils.preparePattern(memory.getThat());
				AIMLNode that = match.getThat().get(thatPattern);
				if (that!=null){
					that.addMatchRank(MATCH_RANK_THAT);
					match = that;	
				}
			}
			if (match.getPattern().equals("*")){
				match.addMatchRank(MATCH_RANK_STAR);
			}
			else if (!match.getPattern().contains("*")){
				match.addMatchRank(MATCH_RANK_FULL);
			} 
			else if (match.getPattern().startsWith("*")){
				match.addMatchRank(MATCH_RANK_STAR_AT_BEGIN);
			} 
			else if (match.getPattern().endsWith("*")){
				match.addMatchRank(MATCH_RANK_STAR_AT_END);
			} 
			else {
				match.addMatchRank(MATCH_RANK_STAR_IN_THE_MIDDLE);
			} 
			
			if (match.getMatchRank() > bestMatch.getMatchRank()){
				bestMatch = match;
			}	
		}
		return bestMatch;
	}

	private List<AIMLNode> findMatchWithStar(AIMLNode currentNode, int currentBranchIndex, String[] words ) {
		List<AIMLNode> matches = new ArrayList<AIMLNode>();
		AIMLNode petentiallyMatch = currentNode.getChild("*");
		
		if (petentiallyMatch != null){
			
			// current Node already is end Node with an answer
			if (readAnswer(petentiallyMatch, EMPTY_STAR) != null){
				matches.add(currentNode.getChild("*"));
			}
			
			// look down the branches, if the star is in the middle of the pattern
			for (int t = currentBranchIndex; t < words.length; t++) {
				
				if (petentiallyMatch.getChild(words[t]) == null) {
					continue;
				}
				petentiallyMatch = petentiallyMatch.getChild(words[t]);
				
				if (t == words.length-1 && petentiallyMatch.hasPattern()){
					matches.add(petentiallyMatch);
				}
			}	
		}
		
		return matches;
	}

	private String getStar(String pattern, String query) {

		Log.d(TAG, "############### pattern:" + pattern);
		int start = pattern.indexOf("*");
		int end = query.length()-pattern.length()+start+1;
		if (start >= 0){
			return query.subSequence(start, end).toString();
		}
		return "";
	}
	
	private String readAnswer(AIMLNode node, String star){

		String answer = null;
		if (node.getRandom() != null && node.getRandom().size() != 0) {
			List<String> templates = node.getRandom();
			int index = (int)(Math.random() * templates.size());
			answer =  templates.get(index);
		}
		else {
			answer = node.getTemplate();
		}
		
		return ChatUtils.prepareAnswer(answer);
	}

}
