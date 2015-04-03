package it.ksuploader.utils;
import it.ksuploader.main.Main;
import java.net.URL;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

public class Sound extends Thread {

	private static final URL clickUrl = Sound.class.getResource("/res/complete.wav");

	public Sound() {
	}

	public void run() {
		AudioInputStream audioInputStream = null;

		try {
			audioInputStream = AudioSystem.getAudioInputStream(clickUrl);

			AudioFormat format = audioInputStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			Clip clip = null;
			clip = (Clip) AudioSystem.getLine(info);
			if (clip.isRunning()) {
				clip.close();
			}
			clip.open(audioInputStream);
			clip.start();
		} catch (Exception e) {
			e.printStackTrace();
            Main.myErr(Arrays.toString(e.getStackTrace()).replace(",", "\n"));
		}
	}
}
