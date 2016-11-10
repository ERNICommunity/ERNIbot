package de.erni.trongbot.bot.xml;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;

import de.erni.trongbot.bot.entity.AIMLNode;
import de.erni.trongbot.bot.helper.ChatUtils;

/**
 * 
 * @author doa
 *
 * http://www.alicebot.org/documentation/aiml-reference.html
 *
 */
public class AIMLHandler extends DefaultHandler {

	private static final String TAG_CATEGORY = "category";
	private static final String TAG_PATTERN = "pattern";
	private static final String TAG_TEMPLATE = "template";
	private static final String TAG_RANDOM = "random";
	private static final String TAG_LIST = "li";
	public static final String TAG_THAT = "that";
	
	public static final String TAG_SRAI = "srai";
	public static final String TAG_STAR = "star";
	public static final String TAG_SR = "sr";
	
	private AIMLNode element;
	
	private static final String TAG = "AIMHandler";
	private AIMLNode root;
	private String that;
	private boolean isTemplate;

	private StringBuilder currentText;
	
	public AIMLHandler(AIMLNode aimlMemory){
		currentText = new StringBuilder();
		root = aimlMemory;
		that = null;
	}

    public void startDocument() throws SAXException {
//    	Log.d(TAG, "start document   : ");
    }

    public void endDocument() throws SAXException {
//    	Log.d(TAG, "end document     : ");
    }

    public void startElement(String uri, String localName,
        String qName, Attributes attributes){
        
        if (TAG_CATEGORY.equals(qName)){
        	element = new AIMLNode();
        } 
        else if (TAG_RANDOM.equals(qName) && element != null){
        	element.setRandom(new ArrayList<String>());
        }
        else if (TAG_SRAI.equals(qName) && element != null) {
        	currentText.append("<" + TAG_SRAI + ">");
        }
        else if (TAG_STAR.equals(qName) && element != null) {
        	currentText.append("<" + TAG_STAR + "/>");
        }
        else if (TAG_TEMPLATE.equals(qName) && element != null) {
        	isTemplate = true;
        }
//    	Log.d(TAG, "start element    : " + qName);
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
    	
    	if(TAG_CATEGORY.equals(qName) && element != null) {
    		String[] pattern = element.getPattern().split(" ");
    		AIMLNode currentNode = root;
    		
    		currentNode = findOrCreateBrunch(pattern, currentNode);
    		
    		if (that==null){
    			addPatternToBrunch(pattern, currentNode);
    		}
    		else {
    			addThatToBrunch(pattern, currentNode);
    		}
    		
    		that=null;
        	element=null;
        }
    	else if(TAG_PATTERN.equals(qName) && element != null) {
    		String pattern = ChatUtils.preparePattern(getCurrentText());
        	element.setPattern(pattern);
        }
    	else if(TAG_TEMPLATE.equals(qName) && element != null) {
    		element.setTemplate(getCurrentText());
    		isTemplate = false;
    	}
    	else if(TAG_LIST.equals(qName) && element != null && element.getRandom() != null){
    		element.addRandomTemplate(getCurrentText());
    	} 
    	else if (TAG_SRAI.equals(qName) && element != null) {
        	currentText.append("</"+ TAG_SRAI + ">");
        }
    	else if(TAG_SR.equals(qName) && element != null){
    		currentText.append("<" + TAG_SR + "/>");
    	}
    	else if(TAG_THAT.equals(qName) && element != null && isTemplate){
    		currentText.append("<" + TAG_THAT + "/>");
    	}
    	else if(TAG_THAT.equals(qName) && element != null){
    		that = getCurrentText();
    	}   
//    	Log.d(TAG, "end element      : " + qName);   	
    }

    
	private AIMLNode findOrCreateBrunch(String[] pattern, AIMLNode currentNode) {
    	for (int i = 0; i < pattern.length-1; i++){
    		
			AIMLNode childNode = currentNode.getChild(pattern[i]);
			if (childNode == null) {
				childNode = new AIMLNode();
				currentNode.addChild(pattern[i], childNode);
			}	
			currentNode = childNode;
		}
		return currentNode;
	}

    private void addPatternToBrunch(String[] pattern, AIMLNode currentNode) {
		AIMLNode matchingNode = currentNode.getChild(pattern[pattern.length-1]);
		
		if (matchingNode == null){
			currentNode.addChild(pattern[pattern.length-1], element);
		} 
		else {
			matchingNode.setPattern(element.getPattern());
			Log.d(TAG, "doppeltes Element: " + matchingNode.getPattern());  
			if (element.getTemplate()!=null){
				matchingNode.setTemplate(element.getTemplate());
			}
			if (element.getRandom()!=null){
				matchingNode.setRandom(element.getRandom());
			}
		}	
	}
	
    private void addThatToBrunch(String[] pattern, AIMLNode currentNode) {
		AIMLNode matchingNode = currentNode.getChild(pattern[pattern.length-1]);
		if (matchingNode == null){
			AIMLNode thatNode = new AIMLNode();
			thatNode.setPattern(element.getPattern());
			currentNode.addChild(pattern[pattern.length-1], new AIMLNode());
			matchingNode = thatNode;
		} 
		matchingNode.addThat(that, element);	
	}
    
    public void characters(char ch[], int start, int length) throws SAXException {
    	currentText.append(ch, start, length);
    }
	
	private String getCurrentText(){
		String text = currentText.toString();
		currentText = new StringBuilder();
		return text;
	}
    
}
