package ch.erni.ernibot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ch.erni.ernibot.R;
import de.erni.trongbot.bot.entity.Message;


public class ChatAdapter extends ArrayAdapter<Message> {
  private final Context context;
  private final List<Message> values;

  public ChatAdapter(Context context, List<Message> values) {
    super(context, -1, values);
    this.context = context;
    this.values = values;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    
    Message message = values.get(position);
    
    View rowView;
    if (Message.MessageType.OTHER.equals(message.type)) {
    	rowView = inflater.inflate(R.layout.row_other, parent, false);
    } else {
    	rowView = inflater.inflate(R.layout.row_self, parent, false);
    }
    
    TextView textView = (TextView) rowView.findViewById(R.id.row_text);
    textView.setText(message.text);

    return rowView;
  }
} 


