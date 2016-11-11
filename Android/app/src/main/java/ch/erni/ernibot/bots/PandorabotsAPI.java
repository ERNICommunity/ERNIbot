package ch.erni.ernibot.bots;

import android.util.JsonReader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import de.erni.trongbot.bot.entity.Message;

/**
 * Created by doa on 10.11.2016.
 */

public class PandorabotsAPI {


    public void sendMessageToBot(Message userMessage, Message botMessage){
        String status = null;

        try {
            URL url = new URL("https://aiaas.pandorabots.com/talk/1409613240932/dohren?user_key=acc7dd336d2efdde3242f40e4c6affd7&input=air%20force%20blue");
            HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
            System.out.println(url.toString());
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
