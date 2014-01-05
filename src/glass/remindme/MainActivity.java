package glass.remindme;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity {

	public String data, time;
	public String indicator, timeType;
	public ArrayList<Integer> timeValueList;
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
	        setReminder(spokenText);
	        // Do something with spokenText.
	    }
	    super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void setReminder(String input){
		
		TextView tv1 = (TextView)findViewById(R.id.fetchtext);
        
		//get current date and time to calculate time for reminder
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss");
		Date date = new Date();
		
		int year,month,day,hour,minute,second;
		ArrayList<Integer> timeParameters = this.getIntegerArray(new ArrayList<String>(Arrays.asList(dateFormat.format(date).toString().split("/"))));
		
		if(timeParameters.size() == 6){
			year = timeParameters.get(0);
			month = timeParameters.get(1);
			day = timeParameters.get(2);
			hour = timeParameters.get(3);
			minute = timeParameters.get(4);
			second = timeParameters.get(5);
		}else{
			tv1.setText("Error in getting time from system");
			return;
		}
		
		if(input.matches(".*\\d.*") && input.length() > 9){
			
			if(input.substring(0,9).equalsIgnoreCase("Remind me")){
				input = input.substring(9,input.length());
			}

//			tv1.setText(dateFormat.format(date).toString());
			
			int posIndicator = this.lastIndexOfIndicator(input);
			timeValueList = this.getIntsFromString(input.substring(posIndicator));
			this.setupTimeType(posIndicator, input);
			
			if(this.getIndicator().equals("in") || this.getIndicator().equals("after")){
				if(!timeValueList.isEmpty()){
					tv1.append(this.getIndicator() + " " + timeValueList.get(0).toString() + this.getTimeType());
					
					
				//	addAlarm(year,month,day,hour,minute,second,message);
				}
			}else if(this.getIndicator().equals("at") || this.getIndicator().equals("around")){
				if(!timeValueList.isEmpty()){

					switch(timeValueList.size()){
					case 1: tv1.append(this.getIndicator() + " " + timeValueList.get(0).toString() + " " + this.getTimeType());
							break;
					case 2: tv1.append(this.getIndicator() + " " + timeValueList.get(0).toString() + " " + timeValueList.get(1).toString() + " " + this.getTimeType());
							break;
					default: tv1.append("Seems like time interpreted is wrong");
							break;
					}

				}
			}else if(this.getIndicator().equals("on")){
				
			}else{
		        tv1.setText("Speak like, Remind me to study in 5 minutes");
			}
		}else{
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
		
		int[] pos = new int[5];

		pos[0] = input.lastIndexOf("in");
		pos[1] = input.lastIndexOf("after");
		pos[2] = input.lastIndexOf("at");
		pos[3] = input.lastIndexOf("around");
		pos[4] = input.lastIndexOf("on");
		
		int max = pos[0], maxPos = 0;
		// which of the above occures at last and set it as indicator
		for(int i=1; i<5;i++){
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
	

	public void addReminder(Reminder r){
		ReminderDB rdb= new ReminderDB(getApplicationContext());
		rdb.add(r);
	}

	public void addAlarm(int year,int month){
		AlarmManager am=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
       // intent.putExtra(ONE_TIME, Boolean.TRUE);
        
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),0/*id here*/, intent, 0);
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 30);
        
        
        
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
		
		
	}
	//setupTimeType mentioned in substring of input string starting from startIndex
	public void setupTimeType(int startIndex, String input){
		String text = input.substring(startIndex);
		
		if(text.contains("second") || text.contains("seconds")){
			this.setTimeType("second");
		}else if(text.contains("minute") || text.contains("minutes")){
			this.setTimeType("minute");
		}else if (text.contains("hour") || text.contains("hours")){
			this.setTimeType("hour");
		}else if (text.contains("a m")){
			this.setTimeType("a m");
		}else if (text.contains("p m")){
			this.setTimeType("p m");
		}else{
			this.setTimeType("");
		}
	}
	
	private ArrayList<Integer> getIntegerArray(ArrayList<String> stringArray) {
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(String stringValue : stringArray) {
            try {
                //Convert String to Integer, and store it into integer array list.
                result.add(Integer.parseInt(stringValue));
            } catch(NumberFormatException nfe) {
               System.out.println("Could not parse " + nfe);
            }
        }   
        return result;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.confirm, menu);
	        return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
         case R.id.ok:
             startActivity(new Intent(this, Reminder.class));
             return true;
         case R.id.cancel:
        	 displaySpeechRecognizer();
             return true;
         default:
             return super.onOptionsItemSelected(item);
     }
	}
	
	
	
}
