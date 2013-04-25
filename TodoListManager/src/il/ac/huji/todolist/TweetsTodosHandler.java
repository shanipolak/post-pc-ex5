package il.ac.huji.todolist;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

public class TweetsTodosHandler {

	private Context _context;
	private TodoDAL _todoDal;
	ArrayList<String> _tweetsTexts;
	
	public TweetsTodosHandler(Context context, TodoDAL tododal)
	{
		_context = context;
		_todoDal = tododal;
		_tweetsTexts = new ArrayList<String>();
	}
	
	//find first 100 tweets, not yet inserted to db, todos (should be called from an Asynctask)
	public ArrayList<String> findTweetsTodos(){	

		String response = "";
		String searchTag = getTweetHashtagFromPrefs();
		
		try{
			//get twittter hashtag
			
			URL url = new URL("http://search.twitter.com/search.json?q=%23"+ searchTag +"&rpp=100");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();

			InputStream in = new BufferedInputStream(conn.getInputStream());

			response = readStream(in);

			in.close();


		} catch (IOException e) {

			e.printStackTrace();
			return null;
		}

		//parse tweets and filter old results (to results that the user wasn't asked about yet)
		return ParseAndfilterNewTweets(response, searchTag); 
	}
		
		//parse tweets and filter old results (to results that the user wasn't asked about yet)
		private ArrayList<String> ParseAndfilterNewTweets(String response, String searchTag) 
		{
			
			//parse tweets - add ids to db (not to ask about again), and keep text.

			JSONObject jsonobj = null;
			JSONArray tweetsArr = null;
			ArrayList<String> tweetsTexts = null;

			try {

				jsonobj = new JSONObject(response);
				tweetsArr = (JSONArray)(jsonobj.get("results"));
				tweetsTexts = new ArrayList<String>();
				long tweetId;
				
				boolean tagIsNew = true;
				
				long curMaxId= _todoDal.getMaxTweetId(searchTag);
				long newMaxId = curMaxId;
			
				for(int i =0; i< tweetsArr.length(); i++)
				{

					//get text and tweet id from the json object
					JSONObject tweet = (JSONObject)tweetsArr.get(i);

					//insert id to tweets db (max id for each tag) if tweet is new
					tweetId = tweet.getLong("id");
					
					if(tweetId <= curMaxId)
					{	
						continue; // tweet is old
					} 
					//otherwise, it's a new tweet 
					if(tweetId > newMaxId)
					{
						newMaxId = tweetId;
					}
					tweetsTexts.add(tweet.get("text").toString());
				}
				
				if(newMaxId != curMaxId) // there's at least one new tweet
				{
					tagIsNew = (curMaxId == -1) ? true : false;
					_todoDal.insertNewTweet(searchTag, newMaxId, tagIsNew);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return tweetsTexts;
		}
			
		
		private static String readStream(InputStream in) throws IOException {
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			StringBuffer buffer = new StringBuffer();
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				buffer.append(line);
				buffer.append('\n');
			}
			return buffer.toString();

		}
		
		// ask user whether to insert the new tweets to list, and saves tweets(ids to db. texts in class array list for later use)
		public void askUserToAddTweetsTasks(ArrayList<String> tweetsTexts){
			
			if(tweetsTexts.size() == 0){

				return;// no new tweets
			}
			
			_tweetsTexts = tweetsTexts;
			
			Intent intent = new Intent(_context, SuggestTweetsTodos.class);
			intent.putExtra("tweetsNum", _tweetsTexts.size());
			((Activity)_context).startActivityForResult(intent, 2448);
		}

		// insert todo tweets to list (async). should be called only if the user approves.
		public void addTweetsToList(Date dueDate){

			InsertGroupAsync async = new InsertGroupAsync(_context, _todoDal, dueDate, _tweetsTexts);
			async.execute();

		}
		
		//gets new tweet hashtag, and sets it.
		public String getTweetHashtagFromPrefs()
		{
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(_context);
			String searchTag = prefs.getString("tag", "todoapp"); 
		
			if(searchTag.startsWith("#")) //we don't know if the user inserted a hashtag with # or not. if yes, we're cutting it. we add %23 anyway later.
			{
				searchTag = searchTag.substring(1);
			}
			return searchTag;
		}
}
