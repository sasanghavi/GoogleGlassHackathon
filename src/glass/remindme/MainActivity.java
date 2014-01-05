package glass.remindme;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.TimelineManager;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	public String data, time;
	public String indicator, timeType;
	public ArrayList<Integer> timeValueList;
	
	  // To pick an arbitrary images
    private final static Random sRandom = new Random(System.currentTimeMillis());
    private final static int IMAGE_COUNT = 4;
    private static int getRandomPuppyImageResourceId()
    {
        int i = sRandom.nextInt(IMAGE_COUNT);
        switch(i) {
        case 0:
        default:
            return R.drawable.imagecard;
        case 1:
            return R.drawable.imagecard;
        case 2:
            return R.drawable.imagecard;
        case 3:
            return R.drawable.imagecard;
        }
    }

    // Timeline manager instance.
    private TimelineManager timelineManager = null;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_timer);
		timelineManager = TimelineManager.from(this);
		
		
		
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
					if(this.getTimeType().equals("second")){
						addAlarm(year,month,day,hour,minute,second + timeValueList.get(0)%60,this.getReminderText(0, input));
					}else if(this.getTimeType().equals("minute")){
						addAlarm(year,month,day,hour,minute + timeValueList.get(0)%60,second,this.getReminderText(0, input));
					}else if(this.getTimeType().equals("hour")){
						addAlarm(year,month,day,hour + timeValueList.get(0)%12,minute,second,this.getReminderText(0, input));
					}else{
						tv1.setText("Failed to set alarm from in/after");
					}
				}
			}else if(this.getIndicator().equals("at") || this.getIndicator().equals("around")){
				if(!timeValueList.isEmpty()){
					if(this.getTimeType().equals("a m")){
						addAlarm(year,month,day,timeValueList.get(0)%12,minute,second,this.getReminderText(0, input));
					}else if(this.getTimeType().equals("p m")){
						addAlarm(year,month,day, 12 + timeValueList.get(0)%12 ,minute,second,this.getReminderText(0, input));
					}else{
						tv1.setText("Failed to set alarm from at/around");
					}
				}
			}else if(this.getIndicator().equals("on")){
				tv1.setText("Day feature will come soon :)");
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

		pos[0] = input.lastIndexOf(" in ");
		pos[1] = input.lastIndexOf(" after ");
		pos[2] = input.lastIndexOf(" at ");
		pos[3] = input.lastIndexOf(" around ");
		pos[4] = input.lastIndexOf(" on ");
		
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
			if(input.length() < this.lastIndexOfIndicator(input)){
				return input.substring(0, this.lastIndexOfIndicator(input));
			}else{
				return "Its reminder";
			}
		}
		
	}
	

	public void addReminder(Reminder r){
		ReminderDB rdb= new ReminderDB(getApplicationContext());
		rdb.add(r);
	}

	public String cardValue="";
	
	public void addAlarm(int year,int month, int day, int hour, int minute, int second, String str){
		AlarmManager am=(AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmBroadcast.class);
        Toast.makeText(getApplicationContext(), "Enjoy now ", Toast.LENGTH_SHORT).show();
       // intent.putExtra(ONE_TIME, Boolean.TRUE);
        Random i = new Random();
        int value = i.nextInt(200);
        
        intent.putExtra("ID", value);
        Reminder r =  new Reminder(value, str, "");
        ReminderDB db = new ReminderDB(getApplicationContext());
        db.add(r);
        db.close();
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		int check = settings.getInt("ID",0);
        if(check!=0){
        	timelineManager.delete(check);
        	
        }
        cardValue = str;
        insertNewCardImageFull();
        PendingIntent pi = PendingIntent.getBroadcast(getApplicationContext(),value/*id here*/, intent, 0);
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        
        
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pi);
        
        
		
		finish();
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
	
	 private void insertNewCardImageFull()
	    {
	        // Create a card.
	        Card staticCard = new Card(this);
	        staticCard.setImageLayout(Card.ImageLayout.FULL);
	        staticCard.addImage(getRandomPuppyImageResourceId());
	   //     staticCard.setFootnote(cardValue);
	        staticCard.setText(cardValue);
	        staticCard.setFootnote("Remind Me!");
	        long cardId = timelineManager.insert(staticCard);
	      //  if(Log.I) Log.i("Static Card (image - full) inserted: cardId = " + cardId);
	        
	        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			int check = settings.getInt("ID",0);
			Editor edit = settings.edit();
			edit.putInt("ID",(int) cardId);
			edit.apply(); 
	        // Update the card content with the card Id.
	        updateCardWithID(staticCard, cardId);
	    }
	 
	 private void updateCardWithID(Card staticCard, long id)
	    {
	        if(staticCard != null) {
	            String content = staticCard.getText();
	            if(content == null) {  // ????
	                content = "";
	            }
	          //  content += "\nID: " + id;
	         //   if(Log.D) Log.d("New Static Card content: cardId = " + id + "; content = " + content);
	        //    staticCard.setText(content);
	            boolean suc = timelineManager.update(id, staticCard);
	         //   if(Log.I) Log.i("Static Card updated: cardId = " + id + "; suc = " + suc);
	        } else {
	            // ???
	           // Log.w("Card not found for cardId = " + id);
	        }
	    }
	
}
