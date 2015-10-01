package it.ksuploader.utils;

import it.ksuploader.main.Main;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import java.net.URL;
import java.util.Arrays;

public class Sound extends Thread {

	private static final URL clickUrl = Sound.class.getResource("/res/complete.wav");

	public Sound() {
	}

	@Override
	public void run() {
		AudioInputStream audioInputStream;

		try {
			audioInputStream = AudioSystem.getAudioInputStream(clickUrl);

			DataLine.Info info = new DataLine.Info(Clip.class, audioInputStream.getFormat());
			Clip clip = (Clip) AudioSystem.getLine(info);
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
