package il.ac.huji.todolist;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class TwitterHashtagPreference extends PreferenceActivity{

	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
	}  
}
