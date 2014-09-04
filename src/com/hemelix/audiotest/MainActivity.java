package com.hemelix.audiotest;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*
 *  class MainActivity
 *  Main UI class that creates and object of HandleAudioPlayerRecorder and calls method as necessary
 *  It also implement InterfaceAudioEvents interface to get notified when some events are happened in HandleAudioPlayerRecorder class
 *  (c) Hemelix, more tutorial at hemelix.com
 */

public class MainActivity extends Activity implements InterfaceAudioEvents {

	private HandleAudioPlayerRecorder mAudioHandler = null;
	private Button startBtn;
	private Button stopBtn;
	private Button playBtn;
	private Button stopPlayBtn;
	private EditText mEdittext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mEdittext = (EditText) findViewById(R.id.recordingtime);
		mAudioHandler = new HandleAudioPlayerRecorder(this, this);
		startBtn = (Button) findViewById(R.id.start);
		startBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				start(v);
			}
		});

		stopBtn = (Button) findViewById(R.id.stop);
		stopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stop(v);
			}
		});

		playBtn = (Button) findViewById(R.id.play);
		playBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				play(v);
			}
		});

		stopPlayBtn = (Button) findViewById(R.id.stopPlay);
		stopPlayBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				stopPlay(v);
			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		startBtn.setEnabled(true);
		stopBtn.setEnabled(false);
		playBtn.setEnabled(true);
		stopPlayBtn.setEnabled(false);
	}

	/*
	 * Start recording audio. Amount of time is taken from the edit box. If for
	 * some reason value is not OK then it sets to 10 seconds
	 */
	public void start(View view) {
		long recordingtime = 10; // default 10 seconds
		String recordTime = mEdittext.getText().toString();
		if (recordTime != null && recordTime.length() > 0) {
			try {
				recordingtime = Long.parseLong(recordTime);
			} catch (Exception e) {

			}
		}
		mAudioHandler.startAudioRecording(recordingtime);
		Toast.makeText(getApplicationContext(), "Start recording...",
				Toast.LENGTH_SHORT).show();
		startBtn.setEnabled(false);
		stopBtn.setEnabled(true);
		playBtn.setEnabled(false);
		stopPlayBtn.setEnabled(false);
	}

	public void stop(View view) {
		Toast.makeText(getApplicationContext(), "Stop recording...",
				Toast.LENGTH_SHORT).show();
		mAudioHandler.stopAudioRecording();
		startBtn.setEnabled(true);
		stopBtn.setEnabled(false);
		playBtn.setEnabled(true);
		stopPlayBtn.setEnabled(false);
	}

	public void play(View view) {
		mAudioHandler.startAudioPlaying();
		Toast.makeText(getApplicationContext(), "Start play the recording...",
				Toast.LENGTH_SHORT).show();
		startBtn.setEnabled(false);
		stopBtn.setEnabled(false);
		playBtn.setEnabled(false);
		stopPlayBtn.setEnabled(true);
	}

	public void stopPlay(View view) {
		mAudioHandler.stopPlay();
		Toast.makeText(getApplicationContext(),
				"Stop playing the recording...", Toast.LENGTH_SHORT).show();
		startBtn.setEnabled(true);
		stopBtn.setEnabled(false);
		playBtn.setEnabled(true);
		stopPlayBtn.setEnabled(false);
	}

	@Override
	public void audioRecordDone() {
		startBtn.setEnabled(true);
		stopBtn.setEnabled(false);
		playBtn.setEnabled(true);
		stopPlayBtn.setEnabled(false);
	}

	@Override
	public void audioRecordStarted() {
		startBtn.setEnabled(false);
		stopBtn.setEnabled(true);
		playBtn.setEnabled(false);
		stopPlayBtn.setEnabled(false);
	}

	@Override
	public void audioPlayeDone() {
		startBtn.setEnabled(true);
		stopBtn.setEnabled(false);
		playBtn.setEnabled(true);
		stopPlayBtn.setEnabled(false);
	}

	@Override
	public void audioPlayeStarted() {
		startBtn.setEnabled(false);
		stopBtn.setEnabled(false);
		playBtn.setEnabled(false);
		stopPlayBtn.setEnabled(true);
	}
}
