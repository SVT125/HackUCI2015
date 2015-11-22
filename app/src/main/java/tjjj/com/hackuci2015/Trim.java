package tjjj.com.hackuci2015;

import java.util.ArrayList;

import com.musicg.wave.Wave;
import com.musicg.wave.WaveFileManager;


public class Trim {
	
	private static final int NO_OF_SAMPLES = 1000;
	private static final double THRESHOLD_LOW_HIGH = 0.015;
	private static final double THRESHOLD_DELAY_LOW_HIGH = 5000;
	private static final double THRESHOLD_HIGH_LOW = 0.015;
	private static final double THRESHOLD_DELAY_HIGH_LOW = 15000;
	
	public static void trimAudioClip(String filename, String outFolder) {
		// Create a wave object
		Wave wave = new Wave(filename);
		System.out.println(wave);
		
		// Get the array of normalized amplitudes		
		double normAmpArray[] = wave.getNormalizedAmplitudes();
		
		// Idea is to run through all of the amplitude values
		// and check to see when it exceeds the threshold and when it does not.
		// Declare temp variables
		RunningQueue rq = new RunningQueue(NO_OF_SAMPLES);
		ArrayList<TimePosition> timePeriods = new ArrayList<TimePosition>();
		boolean isActive = false;
		int timeAfterLowThreshold = 0;
		int startFrame = 0;
		
		System.out.println("Length of normAmpArray[]: " + normAmpArray.length);
		
		// Add the first round of elements to the queue
		// Start calculating the avg and checking if it exceeds thresholds.
		for (int i = 0; i < normAmpArray.length; i++) {
			rq.insertAndRemoveElement(Math.abs(normAmpArray[i]));
			double avg = rq.getAvg();
			if (!isActive) {
				if (avg > THRESHOLD_LOW_HIGH) {
					// Start new time period from (i - the sample period)
					startFrame = (i - Math.min(i, (int)THRESHOLD_DELAY_LOW_HIGH));
					isActive = true;
				}
			}
			else {
				if (avg < THRESHOLD_HIGH_LOW) {
					if (timeAfterLowThreshold > THRESHOLD_DELAY_HIGH_LOW) {
						// End current time period
						timePeriods.add(new TimePosition(startFrame, i - NO_OF_SAMPLES));
						startFrame = 0;
						isActive = false;
						timeAfterLowThreshold = 0;
					}
					else {
						timeAfterLowThreshold++;
					}
				}
				else {
					// Keep maintaining current time period
					timeAfterLowThreshold = 0;
				}
			}
			
		}
		if (isActive) {
			timePeriods.add(new TimePosition(startFrame, normAmpArray.length - 1));
		}
		for (int i = 0; i < timePeriods.size(); i++) {
			TimePosition tp = timePeriods.get(i);
			System.out.println(tp.printTime());
			Wave waveToCopy = new Wave(filename);
			// waveToCopy trims the amount of seconds/frames specified.
			// It does not trim with a start time/end time.
			// To get the TimePosition end to work, subtract it from the total amount
			// of frames in the wav file.
			waveToCopy.trim(tp.getStart() * 2, (normAmpArray.length - tp.getEnd()) * 2);
			// Save the wav as a separate file
			WaveFileManager waveFileManager = new WaveFileManager(waveToCopy);
			waveFileManager.saveWaveAsFile(outFolder + "/out"+i+".wav");
		}
	}
	
	public static void main(String[] args) {
		String filename = "audio_work/voicemail.wav";
		String outFolder = "out";
		trimAudioClip(filename, outFolder);
	}
}
