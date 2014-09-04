package com.hemelix.audiotest;

/*
 *  class HandleAudioPlayerRecorder
 *  Handle audio related instructions from other classes.
 *  It can record/play audio to/from internal private folder
 *  To create an instance of this class, you need Context and InterfaceAudioEvents
 *  (c) Hemelix, more tutorial at hemelix.com
 */
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

public class HandleAudioPlayerRecorder {
	private static final String TAG = "HandleAudioPlayerRecorder";
	private static final boolean D = true;
	private Context mCtx;
	private MediaRecorder myRecorder;
	private MediaPlayer myPlayer;
	private InterfaceAudioEvents mReceiver;
	private String mAudioFilePath = null;
	private long mRecodingTime = -1; // Time is in seconds
	private Timer mAudioRecordTimer;
	private TimerTask mAudioTimerTask;
	private Handler mThreadhandler = null;

	/*
	 * HandleAudioPlayerRecorder, Constructor, during construction time it sets
	 * the private file where we can record the audio
	 * 
	 * @param aCtx, Context of the application, need to find the private path of
	 * the application
	 * 
	 * @param aReceiver, need to call back when audio related events happen
	 */
	public HandleAudioPlayerRecorder(Context aCtx,
			InterfaceAudioEvents aReceiver) {
		this.mReceiver = aReceiver;
		this.mCtx = aCtx;

		mAudioFilePath = mCtx.getFilesDir().getAbsolutePath();
		if (D)
			Log.d(TAG, "><  HandleAudioPlayerRecorder dir = " + mAudioFilePath);
		String lastChar = mAudioFilePath.substring(mAudioFilePath.length() - 1);
		if (lastChar.contains("//")) {
			mAudioFilePath = mAudioFilePath + "hemelixRecording.3gpp";
		} else {
			mAudioFilePath = mAudioFilePath + "/hemelixRecording.3gpp";
		}
	}

	/*
	 * startAudioRecording, this method is called when we need to record audio
	 * 
	 * @param aRecordingTime, Time is seconds, how many seconds we want to
	 * record. If the value is wrong then 10 is set
	 */

	void startAudioRecording(long aRecordingTime) {
		try {
			myRecorder = new MediaRecorder();
			myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
			File file = new File(mAudioFilePath);
			if (file.exists()) {
				file.delete();
			}
			myRecorder.setOutputFile(mAudioFilePath);
			myRecorder.prepare();
			myRecorder.start();
			if (mReceiver != null) {
				mReceiver.audioRecordStarted();
			}

			// Convert the seconds time to milliseconds times
			// also initialize the timer
			if (aRecordingTime > 0) {
				mRecodingTime = aRecordingTime * 1000;
				mThreadhandler = new Handler();
				mAudioRecordTimer = new Timer();
				mAudioTimerTask = new AudioTimerTask();
				mAudioRecordTimer.schedule(mAudioTimerTask, mRecodingTime);
				if (D)
					Log.d(TAG,
							"Recordding start at = "
									+ SystemClock.elapsedRealtime());
			}
		} catch (IllegalStateException e) {
			if (D)
				Log.d("IllegalStateException ",
						"Error message = " + e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			if (D)
				Log.d("IOException ", "Error message = " + e.getMessage());
			e.printStackTrace();
		}
	}

	/*
	 * stopAudioRecording, this method is called when we need to stop recording
	 * audio
	 * 
	 * @param none
	 */
	void stopAudioRecording() {
		if (mAudioRecordTimer != null) {
			mAudioRecordTimer.cancel();
			mAudioRecordTimer = null;
		}
		if (myRecorder != null) {
			myRecorder.stop();
			myRecorder.reset();
			myRecorder.release();
			myRecorder = null;
		}

		if (mReceiver != null) {
			mReceiver.audioRecordDone();
		}
	}

	/*
	 * startAudioPlaying, this method is called when we need to start playing
	 * the audio. If nothing has been recored yet, it does not do anything. It
	 * will inform via interface when audio play has been completed
	 * 
	 * @param none
	 */
	void startAudioPlaying() {

		// If there is no file no need to continue
		File file = new File(mAudioFilePath);
		if (!file.exists()) {
			return;
		}

		if ((mAudioFilePath == null) || mAudioFilePath.length() == 0) {
			return;
		}
		try {
			myPlayer = new MediaPlayer();
			myPlayer.setDataSource(mAudioFilePath);
			myPlayer.prepare();
			myPlayer.start();
			myPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					if (mReceiver != null) {
						mReceiver.audioPlayeDone();
					}
				}
			});

			if (mReceiver != null) {
				mReceiver.audioPlayeStarted();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * stopPlay, this method is called when we need to stop playing the audio.
	 * 
	 * @param none
	 */
	public void stopPlay() {
		try {
			if (myPlayer != null) {
				myPlayer.stop();
				myPlayer.release();
				myPlayer = null;
				if (mReceiver != null) {
					mReceiver.audioPlayeDone();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * Local class that extends TimerTask. When we schedule Timer then we pass a
	 * object of this to schedule methods. After that specific time the run
	 * method is called (time has elapsed). But problem here is the Timertask
	 * works on different thread and by our call back method we can't update the
	 * UI. We introduce Handler object. When we post via handler then we can
	 * update our UI like in our case we are calling stopAudioRecording() and in
	 * this method we can update our UI as usual.
	 */
	class AudioTimerTask extends TimerTask {
		public void run() {
			if (D)
				Log.d(TAG,
						"Recordding stop at = " + SystemClock.elapsedRealtime());
			mThreadhandler.post(new Runnable() {
				@Override
				public void run() {
					if (D)
						Log.d(TAG, ">< Recodting time elapsed informing UI");
					stopAudioRecording();
				}
			});
		}
	}
}
