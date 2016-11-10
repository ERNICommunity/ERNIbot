package de.erni.trongbot.bot.entity;

public class Message {

	public static enum MessageType {
		   OTHER(1), SELF(2);
		  
		   int id;
		   MessageType(int p) {
		      id = p;
		   }
		   int getId() {
		      return id;
		   } 
		}
	
	public String text;
	public MessageType type;
	
}
