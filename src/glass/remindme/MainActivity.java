package glass.remindme;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract.Reminders;
import android.speech.RecognizerIntent;
import android.widget.TextView;

public class MainActivity extends Activity {

	public String data, time;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_timer);
		displaySpeechRecognizer();
	}
	
	private void displaySpeechRecognizer() {
	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    startActivityForResult(intent, 132);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	        Intent data) {
	    if (requestCode == 132 && resultCode == RESULT_OK) {
	        List<String> results = data.getStringArrayListExtra(
	                RecognizerIntent.EXTRA_RESULTS);
	        String spokenText = results.get(0);
	        TextView tv1 = (TextView)findViewById(R.id.fetchtext);
	        tv1.setText(spokenText);
	        // Do something with spokenText.
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}
	public void processString(String input){
		String temp=input;
		if(temp.substring(0,9).equalsIgnoreCase("Remind me")){
			input = input.substring(9,input.length());
		}
		
		if(input.matches(".*\\d.*")){
			ArrayList<String> inArrayList=new ArrayList<String>(Arrays.asList(input.split(" ")));
			if(inArrayList.get(0).equalsIgnoreCase("to")||inArrayList.get(0).equalsIgnoreCase("about")){
//				inArrayList.lastIndexOf(object)
//				inArrayList.indexOf("")
			}
		}else{
			System.out.println("You did not specified time");
		}
		
	}
	

}
