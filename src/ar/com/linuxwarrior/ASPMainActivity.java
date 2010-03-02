package ar.com.linuxwarrior;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.view.View;



public class ASPMainActivity extends Activity {

	private SharedPreferences mPrefs;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		mPrefs = getSharedPreferences("game_config", MODE_PRIVATE);

		Spinner s = (Spinner) findViewById(R.id.SpinnerSize);
        s.setSelection(mPrefs.getInt("puzzle_size", 0));

        final Button startButton = (Button) findViewById(R.id.StartButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	Intent myIntent = new Intent();
            	myIntent.setClassName("ar.com.linuxwarrior", "ar.com.linuxwarrior.ASPGameActivity");
            	
                startActivity(myIntent);
            }
        });
        
    }
    
    @Override
    protected void onPause() {
        super.onPause();

        Spinner s = (Spinner) findViewById(R.id.SpinnerSize);

        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("puzzle_size", s.getSelectedItemPosition());
        ed.commit();
    }
    
}