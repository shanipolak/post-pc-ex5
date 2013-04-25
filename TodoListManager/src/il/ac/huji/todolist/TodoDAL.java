
package il.ac.huji.todolist;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



public class TodoDAL {
	
	private SQLiteDatabase _db;
	
	public TodoDAL(Context context) {
		
		//db
		TodoListManagerDatabaseHelper helper = new TodoListManagerDatabaseHelper(context);
		_db = helper.getWritableDatabase();
		
		//parse
		Parse.initialize(context, context.getResources().getString(R.string.parseApplication), context.getResources().getString(R.string.clientKey));
		PushService.subscribe(context, "", TodoListManagerActivity.class);
		PushService.setDefaultPushCallback(context, TodoListManagerActivity.class);
		ParseUser.enableAutomaticUser();
		
	}
	
	public SQLiteDatabase getDB()
	{
		return _db;
	}
	
	//inserts a task to the todo table in the sqlite and parse dbs.
	public boolean insert(ITodoItem todoItem) {

		
		if(todoItem == null || todoItem.getTitle() == null)
		{
			return false;
		} 
		// sqlite
		ContentValues task = new ContentValues();
		task.put("title", todoItem.getTitle());
		if(todoItem.getDueDate() != null)
		{
			task.put("due", todoItem.getDueDate().getTime());
		}
		else
		{
			task.putNull("due");
		}
		if(_db.insert("todo", null, task) == -1)
		{
			return false;
		}
	
		// parse
		ParseObject todoObg = new ParseObject("todo");
		todoObg.put("title", todoItem.getTitle());
		if(todoItem.getDueDate() != null)
		{
			todoObg.put("due", todoItem.getDueDate().getTime());
		}
		else
		{
			todoObg.put("due", JSONObject.NULL);
		}

		todoObg.saveInBackground();
		
		return true;
	}
	
	public boolean update(ITodoItem todoItem) { 
		
		final ITodoItem finalItem = todoItem;
		
		if(todoItem == null || todoItem.getTitle() == null)
		{
			return false;
		}
		
		
		//sqlite
		ContentValues task = new ContentValues();
		
		if(todoItem.getDueDate() != null)
		{
			task.put("due", todoItem.getDueDate().getTime());
		}
		else
		{
			task.putNull("due");
		}
		
		
		if(_db.update("todo", task, "title=?", new String[] {todoItem.getTitle()}) == 0)
		{
			return false; //can't update an item that doesn't exist
		}
		
		// parse
		//get parse obj	
		ParseQuery query = new ParseQuery("todo");
		query.whereEqualTo("title", todoItem.getTitle());
		query.findInBackground(new FindCallback() {
			public void done(List<ParseObject> todoList, ParseException e) {
				if (e == null) {
					for(int i=0; i< todoList.size(); i++)
					{
						ParseObject parseObj = todoList.get(i);
						Object dateParse = (finalItem.getDueDate() == null) ? JSONObject.NULL : finalItem.getDueDate().getTime();
						parseObj.put("due", dateParse);
						parseObj.saveInBackground();
					}
				} 
			
			}
		});
		
		return true;
	}
	
	public boolean delete(ITodoItem todoItem) {
		
		if(todoItem == null || todoItem.getTitle() == null)
		{
			return false;
		}
				
		// sqlite
		if(_db.delete("todo", "title=?", new String[] {todoItem.getTitle()}) == 0)
		{
			return false;//can't delete an item that doesn't exist
		}
	
		//get parse obj	
		ParseQuery query = new ParseQuery("todo");
		query.whereEqualTo("title", todoItem.getTitle());
		
		query.findInBackground(new FindCallback() {
		    public void done(List<ParseObject> todoList, ParseException e) {
		        if (e == null) {
		            for(int i=0; i< todoList.size(); i++)
		            {
		            	(todoList.get(i)).get("title").toString();
		            	(todoList.get(i)).deleteInBackground();
		            }
		        } 
		    }
		});
		return true;
	}
	
	public List<ITodoItem> all() {
		
		List<ITodoItem> allTodos = new ArrayList<ITodoItem>();
		Cursor cursor =_db.query("todo", new String[] {"title", "due" }, null, null, null, null, null);
		
		if(cursor.moveToFirst()){
			do {

							String title = cursor.getString(0);
							Date due;
							if(!cursor.isNull(1))
							{
								due = new Date(cursor.getLong(1));
							}
							else
							{
								due = null;
							}
							allTodos.add((ITodoItem)new Task(title, due));

			} while (cursor.moveToNext());
		}
		return allTodos;

	}
	
	//gets from db max id for the tag. if tag doesn't exist, return -1
	public long getMaxTweetId(String tag)
	{
		Cursor cursor =_db.query("tweets", null, "tag=?", new String[]{tag}, null, null, null);
		if(cursor.moveToFirst())
		{
			return cursor.getLong(2);
		}
		return -1;
	}
	
	
	//insert tweet id to db if the tag doesn't exist or it has a higher id then the maximum so far for an existing tag. (a new tweet)
	//should be called only if it's a new tweet
	//newTag arg is true if the tag wasn't seen before (doesn't exist in the list). false otherwise.
	public void insertNewTweet(String tag, long tweetId, boolean newTag)
	{
		
		if(!newTag) // the tag exist and the tweet is newer
		{
			ContentValues tweet = new ContentValues();
			tweet.put("tag", tag);
			tweet.put("maxId", tweetId);
			_db.update("tweets", tweet, "tag=?", new String[] {tag});
		}
		
		else
		{
			ContentValues tweet = new ContentValues();
			tweet.put("tag", tag);
			tweet.put("maxId", tweetId);
			_db.insert("tweets",null, tweet);
		}
		

	}
}