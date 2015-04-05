package it.ksuploader.utils;

import it.ksuploader.main.SystemTrayMenu;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;

import org.jnativehook.keyboard.NativeKeyEvent;

public class Constants {

	public static HashMap<Integer, String> fromKeyToName = new HashMap<Integer, String>();
	public static Environment so = new Environment();
	public static LoadConfig config = new LoadConfig();
	public static SystemTrayMenu st = new SystemTrayMenu();
	public static PrintWriter log;
	public static HashSet<Integer> keyHashScreen = new HashSet<>();
	public static HashSet<Integer> keyHashCScreen = new HashSet<>();
	public static HashSet<Integer> keyHashFile = new HashSet<>();
	public static HashSet<Integer> keyHashClipboard = new HashSet<>();

	public Constants() {

		fromKeyToName.put(NativeKeyEvent.VC_ALT_L, "ALT-L");
		fromKeyToName.put(NativeKeyEvent.VC_ALT_R, "ALT-R");
		fromKeyToName.put(NativeKeyEvent.VC_SHIFT_L, "SHIFT-L");
		fromKeyToName.put(NativeKeyEvent.VC_SHIFT_R, "SHIFT-R");
		fromKeyToName.put(NativeKeyEvent.VC_CONTROL_L, "CTRL-L");
		fromKeyToName.put(NativeKeyEvent.VC_CONTROL_R, "CTRL-R");
		fromKeyToName.put(NativeKeyEvent.VC_0, "0");
		fromKeyToName.put(NativeKeyEvent.VC_1, "1");
		fromKeyToName.put(NativeKeyEvent.VC_2, "2");
		fromKeyToName.put(NativeKeyEvent.VC_3, "3");
		fromKeyToName.put(NativeKeyEvent.VC_4, "4");
		fromKeyToName.put(NativeKeyEvent.VC_5, "5");
		fromKeyToName.put(NativeKeyEvent.VC_6, "6");
		fromKeyToName.put(NativeKeyEvent.VC_7, "7");
		fromKeyToName.put(NativeKeyEvent.VC_8, "8");
		fromKeyToName.put(NativeKeyEvent.VC_9, "9");
		fromKeyToName.put(NativeKeyEvent.VC_A, "A");
		fromKeyToName.put(NativeKeyEvent.VC_B, "B");
		fromKeyToName.put(NativeKeyEvent.VC_C, "C");
		fromKeyToName.put(NativeKeyEvent.VC_D, "D");
		fromKeyToName.put(NativeKeyEvent.VC_E, "E");
		fromKeyToName.put(NativeKeyEvent.VC_F, "F");
		fromKeyToName.put(NativeKeyEvent.VC_G, "G");
		fromKeyToName.put(NativeKeyEvent.VC_H, "H");
		fromKeyToName.put(NativeKeyEvent.VC_I, "I");
		fromKeyToName.put(NativeKeyEvent.VC_J, "J");
		fromKeyToName.put(NativeKeyEvent.VC_K, "K");
		fromKeyToName.put(NativeKeyEvent.VC_L, "L");
		fromKeyToName.put(NativeKeyEvent.VC_M, "M");
		fromKeyToName.put(NativeKeyEvent.VC_N, "N");
		fromKeyToName.put(NativeKeyEvent.VC_O, "O");
		fromKeyToName.put(NativeKeyEvent.VC_P, "P");
		fromKeyToName.put(NativeKeyEvent.VC_Q, "Q");
		fromKeyToName.put(NativeKeyEvent.VC_R, "R");
		fromKeyToName.put(NativeKeyEvent.VC_S, "S");
		fromKeyToName.put(NativeKeyEvent.VC_T, "T");
		fromKeyToName.put(NativeKeyEvent.VC_U, "U");
		fromKeyToName.put(NativeKeyEvent.VC_V, "V");
		fromKeyToName.put(NativeKeyEvent.VC_W, "W");
		fromKeyToName.put(NativeKeyEvent.VC_X, "X");
		fromKeyToName.put(NativeKeyEvent.VC_Y, "Y");
		fromKeyToName.put(NativeKeyEvent.VC_Z, "Z");

	}

}
