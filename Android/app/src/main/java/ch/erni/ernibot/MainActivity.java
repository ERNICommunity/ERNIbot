package ch.erni.ernibot;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import ch.erni.ernibot.adapter.ChatAdapter;
import ch.erni.ernibot.bots.Bot;
import de.erni.trongbot.bot.entity.Memory;
import de.erni.trongbot.bot.entity.Message;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TextToSpeech.OnInitListener {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final int REQUEST_CODE = 1234;
    private static final int RESULT_OK = -1;
    private static final String TAG = "MainActivity";

    private Button sendButton;
    private EditText input;
    private Memory memory;
    private Bot bot;
    private ChatAdapter adapter;
    private ListView chatList;
    private TextToSpeech tts;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        memory = Memory.getInstance();
        bot = new Bot(this);

        tts = new TextToSpeech(this, this);

        adapter = new ChatAdapter(this, memory.getChat());
        chatList = (ListView) findViewById(R.id.chat_table);
        chatList.setDivider(null);
        chatList.setDividerHeight(0);
        chatList.setAdapter(adapter);

        input = (EditText) findViewById(R.id.message_text);
		sendButton = (Button) findViewById(R.id.send_button);

		input.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = input.getText().toString();
				if (text.length() == 0) {
					sendButton.setBackgroundResource(R.drawable.ic_button_microphone);
				} else {
					sendButton.setBackgroundResource(R.drawable.ic_button_text);
				}
			}

		});

        sendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String messageText = input.getText().toString();
                if ("".equals(messageText)) {
                    startVoiceRecognitionActivity();

                }
                else {
                    addMessage(messageText);
                }
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        startActivityForResult(intent, REQUEST_CODE);
    }

    private void addMessage(String text){
        Message message = new Message();
        message.text = text;
        message.type = Message.MessageType.USER;
        memory.addMessage(message);

        input.setText("");

        input.onEditorAction(EditorInfo.IME_ACTION_DONE);

        adapter.notifyDataSetChanged();
        chatList.setSelection(adapter.getCount() - 1);

        Message userMessage = new Message();
        userMessage.type = Message.MessageType.USER;
        userMessage.text = text;

        Message botMessage = new Message();
        botMessage.type = Message.MessageType.BOT;

        SendMessageTask sendMessageTask = new SendMessageTask();
        sendMessageTask.execute(userMessage, botMessage);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            Log.d(TAG, "####################" + matches.get(0));
            addMessage(matches.get(0));

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_chat) {
            // Handle the camera action
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onInit(int status) {
        // TODO Auto-generated method stub

    }

    private class SendMessageTask extends AsyncTask<Message, Integer, Long> {

        private Message botMessage;
        private Message userMessage;

        protected Long doInBackground(Message... messages) {
            userMessage = messages[0];
            botMessage = messages[1];
            bot.sendMessageToBot(userMessage, botMessage);
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            sendButton.setEnabled(false);
        }

        protected void onPostExecute(Long result) {

            memory.addMessage(botMessage);
            HashMap<String, String> map = new HashMap<>();
            adapter.notifyDataSetChanged();
            chatList.setSelection(adapter.getCount() - 1);

            boolean isTTSEnabled = prefs.getBoolean("enable_tts", true);

            if (isTTSEnabled){
                map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
                tts.speak(botMessage.text, TextToSpeech.QUEUE_FLUSH, map);
                sendButton.setEnabled(true);
            }
        }
    }
}
