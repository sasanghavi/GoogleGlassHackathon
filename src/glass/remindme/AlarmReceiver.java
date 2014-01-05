package glass.remindme;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import junit.framework.Test;
import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AlarmReceiver extends Activity {
	int id;
	ReminderDB db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_receiver);
		TextView data = (TextView) findViewById(R.id.alarmReceiverData);
		TextView time = (TextView) findViewById(R.id.alarmReceiverSysTime);
		DateFormat dateFormat = new SimpleDateFormat("dddd HH:mm");
		DateFormat dateFormat2 = new SimpleDateFormat("HH");
		Date date = new Date();
		time.setText(dateFormat.format(date));
		id = getIntent().getIntExtra("ID", 0);
		db = new ReminderDB(getApplicationContext());
		data.setText("You need to "+db.get(id));
		MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.remind_me_theme);   
        mp.start();
		openOptionsMenu();
		RelativeLayout layout=(RelativeLayout)findViewById(R.id.layout);
		int hr=Integer.parseInt(dateFormat2.format(date));
		if(hr>5 && hr <17)
			layout.setBackgroundResource(R.drawable.sunny_day_summer_wallpaper);
		if(hr>=17 && hr <=19)
			layout.setBackgroundResource(R.drawable.beautiful_sunset);
		if(hr<=5 && hr >19)
			layout.setBackgroundResource(R.drawable.night);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		 MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.alarm_receiver_menu, menu);
	        return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==R.id.ok){
			finish();
		}
		else if(item.getItemId()==R.id.later){
			Toast.makeText(getApplicationContext(), "Remind later", Toast.LENGTH_SHORT).show();
		}
		return super.onOptionsItemSelected(item);
	}
}
