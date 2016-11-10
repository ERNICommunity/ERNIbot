package de.erni.trongbot.bot.helper;

import android.util.Log;

public class ChatUtils {

	public static final String TAG = "ChatUtils";
	
	public static String preparePattern(String pattern){
		pattern = pattern.trim();
		pattern = pattern.replaceAll("[\\?\\!\\.\\n,]", "");
		pattern = pattern.toUpperCase();
		
		return pattern;
	}
	
	public static String prepareAnswer(String answer){
		if 	(answer!=null){
		
			answer = answer.trim();
			Log.d(TAG, "answer: " + answer);
			answer = answer.replaceAll("[\\n]", "");
		}
		return answer;
	}
	
}
