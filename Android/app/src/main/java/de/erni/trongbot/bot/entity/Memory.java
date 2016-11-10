package de.erni.trongbot.bot.entity;

import java.util.ArrayList;
import java.util.List;

public class Memory {

	private List<Message> chat;
	private static Memory memory;
	
	private Memory(){
		chat = new ArrayList<Message>();
	}
	
	public static Memory getInstance(){
		if (memory==null) {
			memory = new Memory();
		}
		return memory;
	}

	public List<Message> getChat() {
		return chat;
	}

	public void setChat(List<Message> chat) {
		this.chat = chat;
	}
	
	public void addMessage(Message message) {
		chat.add(message);
	}

	public String getThat() {
		
		if (chat.size() > 1){
			Message message = chat.get(chat.size()-2);
			return message.text;
		}
		
		return "";
	}

}
