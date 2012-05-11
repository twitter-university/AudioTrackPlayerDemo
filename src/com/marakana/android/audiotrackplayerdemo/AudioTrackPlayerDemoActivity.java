package com.marakana.android.audiotrackplayerdemo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class AudioTrackPlayerDemoActivity extends Activity implements
		OnClickListener {

	private static final String TAG = "AudioTrackPlayerDemoActivity";
	private Button button;
	private byte[] audioData;
	private AudioTrack audioTrack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.main);
		this.button = (Button) super.findViewById(R.id.play);
		this.button.setOnClickListener(this);
		this.button.setEnabled(false);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					InputStream in = getResources().openRawResource(R.raw.ding);
					try {
						ByteArrayOutputStream out = new ByteArrayOutputStream(
								264848);
						for (int b; (b = in.read()) != -1;) {
							out.write(b);
						}
						Log.d(TAG, "Got the data");
						audioData = out.toByteArray();
					} finally {
						in.close();
					}
				} catch (IOException e) {
					Log.wtf(TAG, "Failed to read", e);
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void v) {
				Log.d(TAG, "Creating track...");
				button.setEnabled(true);
				Log.d(TAG, "Enabled button");
			}
		}.execute();
	}

	public void onClick(View view) {
		this.button.setEnabled(false);
		this.releaseAudioTrack();
		this.audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
				AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
				audioData.length, AudioTrack.MODE_STATIC);
		Log.d(TAG, "Writing audio data...");
		this.audioTrack.write(audioData, 0, audioData.length);
		Log.d(TAG, "Starting playback");
		audioTrack.play();
		Log.d(TAG, "Playing");
		this.button.setEnabled(true);
	}

	private void releaseAudioTrack() {
		if (this.audioTrack != null) {
			Log.d(TAG, "Stopping");
			audioTrack.stop();
			Log.d(TAG, "Releasing");
			audioTrack.release();
			Log.d(TAG, "Nulling");
		}
	}

	public void onPause() {
		super.onPause();
		this.releaseAudioTrack();
	}
}