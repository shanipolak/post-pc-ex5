package il.ac.huji.todolist;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;

import android.widget.DatePicker;
import android.widget.TextView;

public class SuggestTweetsTodos extends Activity {

	final public static String suggestion = "Would you like to add them to your todo list?\n" 
											+ "if your answer is yes, don't forget to pick a due date.";
	
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggest_tweets_todos);
		
		//get new todo tweets num
		Intent intent = getIntent(); 
		int tweetsNum = intent.getIntExtra("tweetsNum", -1);
		if(tweetsNum <= 0)
		{
			setResult(RESULT_CANCELED);
			finish();
		}
		
		//ask user whether to add todos to list
		TextView suggestionView = (TextView) findViewById(R.id.suggestTweets);

		//check if there are more than 1 result to make suggestion sentence correct
		String suggestionStart = (tweetsNum > 1) ? String.format("There are %s new results.\n", tweetsNum) : ("There is 1 new result.\n");
		
		suggestionView.setText(suggestionStart + suggestion);
		
		//check user choice
		findViewById(R.id.noButten).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		
		// if the user wants to add the tweet tasks, get the due date from the date picker.
		findViewById(R.id.yesButten).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				
				DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker1);
				int day = datePicker.getDayOfMonth();
				int month = datePicker.getMonth();
				int year = datePicker.getYear();
				
			    Calendar cal = Calendar.getInstance();
			    cal.set(year, month, day);
			    Date date = cal.getTime();
			
			    //set results in intent
			    Intent resultIntent = new Intent();
			    resultIntent.putExtra("dueDate", date);
			    
				setResult(RESULT_OK, resultIntent);
				finish();
			}
		});
	}
}
