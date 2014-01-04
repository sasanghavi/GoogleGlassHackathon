package glass.remindme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
	
	public void addReminder(Reminder r){
		ReminderDB rdb= new ReminderDB(getApplicationContext());
		rdb.add(r); 
		
	}

	public void addAlarm(){
		AlarmManager am=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
       // intent.putExtra(ONE_TIME, Boolean.TRUE);
        
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),0/*id here*/, intent, 0);
        
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
		
		
	}
}
