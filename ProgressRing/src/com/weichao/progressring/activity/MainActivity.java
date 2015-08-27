package com.weichao.progressring.activity;

import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;

import com.weichao.progressring.R;
import com.weichao.progressring.view.ProgressRing;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final ProgressRing pr = (ProgressRing) findViewById(R.id.pr);
		final Random random = new Random();

		pr.increaseTotalValue(1024);
		
		SystemClock.sleep(1000);
		new Thread() {
			@Override
			public void run() {
				while (true) {
					SystemClock.sleep(100);
					runOnUiThread(new Runnable() {
						public void run() {
							pr.increaseConsumedValue(random.nextInt(10));
						}
					});
				}
			}
		}.start();
		
	}
}
