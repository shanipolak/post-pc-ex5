package il.ac.huji.todolist;

import java.util.ArrayList;

import org.json.JSONArray;

import android.R.integer;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class TweetAsyncTask extends AsyncTask<Void, Void, ArrayList<String>>{

	private Context _context;
	private ProgressDialog _progressDialog;
	private TweetsTodosHandler _tweetsTodosHandler;
	
	public TweetAsyncTask(Context context, TweetsTodosHandler tweetsTodosHandler)
	{
		_context = context;
		_tweetsTodosHandler = tweetsTodosHandler;
		_progressDialog = null;
	}
	
	//gets tweets from tweeter and put them in the handler.
	protected ArrayList<String> doInBackground(Void... params) {
		
	
			return _tweetsTodosHandler.findTweetsTodos();
	}
	
	
	protected void onPreExecute() {
		_progressDialog = new ProgressDialog(_context);
		_progressDialog.setTitle("Looking for tweets");
		_progressDialog.setMessage("Retrieving results from Twitter search...");
		_progressDialog.setCancelable(true);
		_progressDialog.show();
		super.onPreExecute();
	}
	
	// dismisses progress dialog and asks user whether to insert new todo tweets to list.
	protected void onPostExecute(ArrayList<String> result) {
		
		_progressDialog.dismiss();

		if(result != null)
		{
			_tweetsTodosHandler.askUserToAddTweetsTasks(result);
		}
		super.onPostExecute(result);
	}


}
