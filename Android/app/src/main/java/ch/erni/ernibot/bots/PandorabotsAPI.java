package ch.erni.ernibot.bots;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.JsonReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

import de.erni.trongbot.bot.entity.Message;

/**
 * Created by doa on 10.11.2016.
 */

public class PandorabotsAPI {

    Context context;

    public PandorabotsAPI(Context context){
        this.context = context;
    }

    public void sendMessageToBot(Message userMessage, Message botMessage){
        String status = null;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String botName = prefs.getString("pandorabot_botname", "german");
        String userKey = prefs.getString("pandorabot_userkey", "cfb485db5f62981ea63aa4f9c5bfcea8");
        String appId = prefs.getString("pandorabot_appid", "1409613245650");
        String hostname = prefs.getString("pandorabot_hostname", "aiaas.pandorabots.com");

        try {

            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append("https://");
            urlBuilder.append(hostname);
            urlBuilder.append("/talk/");
            urlBuilder.append(appId);
            urlBuilder.append("/");
            urlBuilder.append(botName);
            urlBuilder.append("?user_key=");
            urlBuilder.append(userKey);
            urlBuilder.append("&input=");
            urlBuilder.append(URLEncoder.encode(userMessage.text, "UTF-8"));
            urlBuilder.append("&client_name=");
            urlBuilder.append("user");

            URL url = new URL(urlBuilder.toString());
            System.out.println(url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("POST");
            int statusCode = urlConnection.getResponseCode();

            if (statusCode ==  200) {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("status")) {
                        status = reader.nextString();
                    } else if (name.equals("responses")) {
                        reader.beginArray();
                        botMessage.text = reader.nextString();
                    } else {
                        reader.skipValue();
                    }
                }
            }

        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
