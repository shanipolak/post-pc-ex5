package il.ac.huji.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;

public class TodoListManagerDatabaseHelper extends SQLiteOpenHelper {

	public TodoListManagerDatabaseHelper(Context context) {
		super(context, "todo_db", null, 1);
	}


	public void onCreate(SQLiteDatabase db) {
		//todo items db
		db.execSQL("create table todo ( _id integer primary key autoincrement,"
				+ " title text, due INTEGER );");
		
		//tweets db (keeps the max tweet id that were already found and suggested to the user, for each tag)
		db.execSQL("create table tweets ( _id integer primary key autoincrement, tag text,"
				+ " maxId INTEGER );");
		
	}


	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//Nothing to do.
	}

}
