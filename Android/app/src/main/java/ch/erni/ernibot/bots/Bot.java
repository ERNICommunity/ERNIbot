package ch.erni.ernibot.bots;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.erni.trongbot.bot.AIMLInterpreter;
import de.erni.trongbot.bot.entity.Message;

/**
 * Created by doa on 10.11.2016.
 */

public class Bot {

    private PandorabotsAPI pandorabotdAPI;
    private AIMLInterpreter trongbot;
    private Context context;

    public Bot(Context context){
        this.context = context;
        pandorabotdAPI = new PandorabotsAPI();
        trongbot = AIMLInterpreter.getInstance(context);
    }

    public void sendMessageToBot(Message userMessage, Message botMessage){

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean enablePandorabots = prefs.getBoolean("enable_pandorabots", true);

        if (enablePandorabots) {
            pandorabotdAPI.sendMessageToBot(userMessage, botMessage);
        }
        else {
            trongbot.sendMessageToBot(userMessage, botMessage);
        }
    }

}
