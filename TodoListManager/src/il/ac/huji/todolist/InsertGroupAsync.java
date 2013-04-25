package il.ac.huji.todolist;

import java.util.ArrayList;
import java.util.Date;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

// insert a group of tweets to the task db
public class InsertGroupAsync extends AsyncTask<Void, Void, Void> {

	private TodoDAL _todoDal;
	private Date _chosenDate;
	private ArrayList<String> _tweets;
	private Context _context;
	private ProgressDialog _progressDialog;
	
	public InsertGroupAsync(Context context, TodoDAL todoDAL, Date date, ArrayList<String> tweetsTexts)
	{
		_todoDal = todoDAL;
		_chosenDate = date;
		_context = context;
		_progressDialog = null;
		_tweets = tweetsTexts;
	}

	protected Void doInBackground(Void... arg0) {
		
		
		for(int i =0; i< _tweets.size(); i++)
		{

			//inset tweet task to tasks db
			_todoDal.insert(new Task(_tweets.get(i), _chosenDate));
		}
		return null;
	}

	protected void onPreExecute() {
		_progressDialog = new ProgressDialog(_context);
		_progressDialog.setTitle("Inserting tweets to database");
		_progressDialog.setMessage("please wait\n (You can close this dialog,\n but results will be shown only at the end of the process)");
		_progressDialog.setCancelable(true);
		_progressDialog.show();
		super.onPreExecute();
	}
	
	// dismisses progress dialog 
	protected void onPostExecute(Void result) {
		
		_progressDialog.dismiss();
		super.onPostExecute(result);
	}

}
