package glass.remindme;

import glass.remindme.R.string;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract.Reminders;
import android.speech.RecognizerIntent;
import android.support.v4.util.ArrayMap;
import android.widget.TextView;

public class MainActivity extends Activity {

	public String data, time;
	public String indicator, timeType;
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
	
	public void setReminder(String input){
		
		if(input.matches(".*\\d.*") && input.length() > 9){
			
			if(input.substring(0,9).equalsIgnoreCase("Remind me")){
				input = input.substring(9,input.length());
			}
			
			int posIndicator = this.lastIndexOfIndicator(input);
			ArrayList<Integer> timeValueList = this.getIntsFromString(input.substring(posIndicator));
			this.setupTimeType(posIndicator, input);
			
			if(this.getIndicator().equals("in") || this.getIndicator().equals("after")){
				
			}else if(this.getIndicator().equals("at") || this.getIndicator().equals("around")){
				
			}else if(this.getIndicator().equals("on")){
				
			}else{
				TextView tv1 = (TextView)findViewById(R.id.fetchtext);
		        tv1.setText("Speak like, Remind me to study in 5 minutes");
			}
		}else{
			TextView tv1 = (TextView)findViewById(R.id.fetchtext);
	        tv1.setText("You did not specified time");
		}
	}
	
	//returns arraylist of all integers present in input string
	public ArrayList<Integer> getIntsFromString(String input){
		
		input = input.replaceAll("[^0-9]", " ");
		
		ArrayList<String> stringArrayList = new ArrayList<String>(Arrays.asList(input.split(" ")));
		ArrayList<Integer> intArrayList = new ArrayList<Integer>();
		
		for (String string : stringArrayList) {
			if(!string.equals("")){
				intArrayList.add(Integer.parseInt(string));
			}
		}
		
		return intArrayList;
	}	

	//returns index of last indicator present in input string and also sets value of indicator
	public int lastIndexOfIndicator(String input){
		
		int[] pos = new int[4];

		pos[0] = input.lastIndexOf("in");
		pos[1] = input.lastIndexOf("after");
		pos[2] = input.lastIndexOf("at");
		pos[3] = input.lastIndexOf("around");
		pos[4] = input.lastIndexOf("on");
		
		int max = pos[0], maxPos = 0;
		// which of the above occures at last and set it as indicator
		for(int i=1; i<4;i++){
			if(max < pos[i]){
				max = pos[i];
				maxPos = i;
			}
		}
		
		switch(maxPos){
			case 0: this.setIndicator("in");
					break;
			case 1: this.setIndicator("after");
					break;
			case 2: this.setIndicator("at");
					break;
			case 3: this.setIndicator("around");
					break;
			case 4: this.setIndicator("on");
					break;
			default: this.setIndicator("");
					 break;
			}
		
		return maxPos;
	}
	
	//returns sensible text of reminder from substring of input string starting from startIndex 
	public String getReminderText(int startIndex,String input){
		
		String text = input.substring(startIndex, this.lastIndexOfIndicator(input));
		// remove to/about from begining for saw off of AI
		if(text.indexOf("to") == 0){
			return text.substring(2);
		}else if(text.indexOf("about") == 0){
			return text.substring(6);
		}else{
			return input.substring(10, this.lastIndexOfIndicator(input));
		}
		
	}
	
	//setupTimeType mentioned in substring of input string starting from startIndex
	public void setupTimeType(int startIndex, String input){
		String text = input.substring(startIndex);
		
		if(text.contains("minute") || text.contains("minutes")){
			this.setTimeType("minute");
		}else if (text.contains("hour") || text.contains("hours")){
			this.setTimeType("hour");
		}else if (text.contains("AM")){
			this.setTimeType("AM");
		}else if (text.contains("PM")){
			this.setTimeType("PM");
		}else{
			this.setTimeType("");
		}
	}
	
	public void setIndicator(String indicator){
		this.indicator = indicator;
	}
	
	
	public String getIndicator(){
		return this.indicator;
	}
	
	
	public void setTimeType(String timeType){
		this.timeType = timeType;
	}
	
	
	public String getTimeType(){
		return this.timeType;
	}
}
