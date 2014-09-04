package com.hemelix.audiotest;

/*
 *  interface InterfaceAudioEvents, the class who want to get notify about audio event must implement this interface.
 *  Like MainActivity class (our UI class) want to be notified  and MainActivity has implemented InterfaceAudioEvents interface and it's methods
 *  (c) Hemelix, more tutorial at hemelix.com
 */
public interface InterfaceAudioEvents {
	/*
	 * Called when recording just started.
	 */
	void audioRecordStarted();

	/*
	 * Called when recording done.
	 */
	void audioRecordDone();

	/*
	 * Called when the recorded audio has just started.
	 */
	void audioPlayeStarted();

	/*
	 * Called when the recorded audio has been played .
	 */
	void audioPlayeDone();

}
