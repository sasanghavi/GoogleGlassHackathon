package glass.remindme;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.glass.media.Sounds;

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
		Date date = new Date();
		time.setText(dateFormat.format(date));
		id = getIntent().getIntExtra("ID", 0);
		db = new ReminderDB(getApplicationContext());
		data.setText("You need to "+db.get(id));
		AudioManager audio = (AudioManager) getApplicationContext()
				.getSystemService(Context.AUDIO_SERVICE);
		audio.playSoundEffect(Sounds.ERROR);
		openOptionsMenu();
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
