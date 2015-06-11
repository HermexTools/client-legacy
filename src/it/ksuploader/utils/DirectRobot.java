package it.ksuploader.utils;

import sun.awt.ComponentFactory;

import java.awt.*;
import java.awt.image.*;
import java.awt.peer.MouseInfoPeer;
import java.awt.peer.RobotPeer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Hashtable;

public final class DirectRobot
{
	public DirectRobot() throws AWTException
	{
		this(null);
	}

	public DirectRobot(GraphicsDevice device) throws AWTException
	{
		if (device == null)
			device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

		this.device = device;
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		peer = ((ComponentFactory) toolkit).createRobot(null, device);
		Class<?> peerClass = peer.getClass();
		Method method = null;
		int methodType = -1;
		Object methodParam = null;
		try
		{
			method = peerClass.getDeclaredMethod("getRGBPixels", Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, int[].class);
			methodType = 0;
		}
		catch (Exception ex) { }
		if (methodType < 0)
			try
			{
				method = peerClass.getDeclaredMethod("getScreenPixels", Rectangle.class, int[].class);
				methodType = 1;
			}
		catch (Exception ex) { }

		if (methodType < 0)
			try
			{
				method = peerClass.getDeclaredMethod("getScreenPixels", Integer.TYPE, Rectangle.class, int[].class);
				methodType = 2;
				GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().
					getScreenDevices();
				int count = devices.length;
				for (int i = 0; i != count; ++i)
					if (device.equals(devices[i]))
					{
						methodParam = Integer.valueOf(i);
						break;
					}

			}
		catch (Exception ex) { }

		if (methodType < 0)
			try
			{
				method = peerClass.getDeclaredMethod("getRGBPixelsImpl", Class.forName("sun.awt.X11GraphicsConfig"), Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, int[].class);
				methodType = 3;
				Field field = peerClass.getDeclaredField("xgc");
				try
				{
					field.setAccessible(true);
					methodParam = field.get(peer);
				}
				finally
				{
					field.setAccessible(false);
				}
			}
		catch (Exception ex) { }

		if (methodType >= 0 && method != null && (methodType <= 1 || methodParam != null))
		{
			getRGBPixelsMethod = method;
			getRGBPixelsMethodType = methodType;
			getRGBPixelsMethodParam = methodParam;
		}
		else
		{
			System.out.println("WARNING: Failed to acquire direct method for grabbing pixels, please post this on the main thread!");
			System.out.println();
			System.out.println(peer.getClass().getName());
			System.out.println();
			try
			{
				Method[] methods = peer.getClass().getDeclaredMethods();
				for (Method method1 : methods)
					System.out.println(method1);

			}
			catch (Exception ex) { }
			System.out.println();
		}
	}

	public static GraphicsDevice getMouseInfo(Point point)
	{
		if (!hasMouseInfoPeer)
		{
			hasMouseInfoPeer = true;
			try
			{
				Toolkit toolkit = Toolkit.getDefaultToolkit();
				Method method = toolkit.getClass().getDeclaredMethod("getMouseInfoPeer");
				try
				{
					method.setAccessible(true);
					mouseInfoPeer = (MouseInfoPeer) method.invoke(toolkit);
				}
				finally
				{
					method.setAccessible(false);
				}
			}
			catch (Exception ex) { }
		}
		if (mouseInfoPeer != null)
		{
			int device = mouseInfoPeer.fillPointWithCoords(point != null ? point:new Point());
			GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().
				getScreenDevices();
			return devices[device];
		}
		PointerInfo info = MouseInfo.getPointerInfo();
		if (point != null)
		{
			Point location = info.getLocation();
			point.x = location.x;
			point.y = location.y;
		}
		return info.getDevice();
	}

	public static int getNumberOfMouseButtons()
	{
		return MouseInfo.getNumberOfButtons();
	}

	public static GraphicsDevice getDefaultScreenDevice()
	{
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	}

	public static GraphicsDevice getScreenDevice()
	{
		return getMouseInfo(null);
	}

	public void mouseMove(int x, int y)
	{
		peer.mouseMove(x, y);
	}

	public void mousePress(int buttons)
	{
		peer.mousePress(buttons);
	}

	public void mouseRelease(int buttons)
	{
		peer.mouseRelease(buttons);
	}

	public void mouseWheel(int wheelAmt)
	{
		peer.mouseWheel(wheelAmt);
	}

	public void keyPress(int keycode)
	{
		peer.keyPress(keycode);
	}

	public void keyRelease(int keycode)
	{
		peer.keyRelease(keycode);
	}

	public int getRGBPixel(int x, int y)
	{
		return peer.getRGBPixel(x, y);
	}

	public int[] getRGBPixels(Rectangle bounds)
	{
		return peer.getRGBPixels(bounds);
	}

	public boolean getRGBPixels(int x, int y, int width, int height, int[] pixels)
	{
		if (getRGBPixelsMethod != null)
			try
			{
				if (getRGBPixelsMethodType == 0)
					getRGBPixelsMethod.invoke(peer, Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(width), Integer.valueOf(height), pixels);
				else if (getRGBPixelsMethodType == 1)
					getRGBPixelsMethod.invoke(peer, new Rectangle(x, y, width, height), pixels);
				else if (getRGBPixelsMethodType == 2)
					getRGBPixelsMethod.invoke(peer, getRGBPixelsMethodParam, new Rectangle(x, y, width, height), pixels);
				else
					getRGBPixelsMethod.invoke(peer, getRGBPixelsMethodParam, Integer.valueOf(x), Integer.valueOf(y), Integer.valueOf(width), Integer.valueOf(height), pixels);

				return true;
			}
		catch (Exception ex) { }

		int[] tmp = getRGBPixels(new Rectangle(x, y, width, height));
		System.arraycopy(tmp, 0, pixels, 0, width * height);
		return false;
	}

	public BufferedImage createScreenCapture(Rectangle r) {

		int[] pixels = new int[r.width*r.height];
		getRGBPixels(0, 0, r.width, r.height,pixels);
		ColorModel model = new DirectColorModel(32, 0xff0000, 0xff00, 0xff, 0xff000000);
		BufferedImage image = new BufferedImage(model, Raster.createWritableRaster(model.createCompatibleSampleModel(r.width, r.height), new DataBufferInt(pixels, r.width * r.height), null), false, new Hashtable<Object, Object>());
		return image;

	}

	public int[] createIntArrayScreenCapture(Rectangle r){

		int[] pixels = new int[r.width*r.height];
		getRGBPixels(0,0,r.width,r.height,pixels);
		return pixels;
	}

	public byte[] getBytePixels(Rectangle re, int[] pix) {
		byte[] pixb = new byte[re.width*re.height];
		for (int i = 0; i < pix.length; i++) {
			int c = pix[i];
			int r = ((c >> 16) & 0xFF) / 36;
			int g = ((c >> 8) & 0xFF) / 36;
			int b = (c & 0xFF) / 85;
			pixb[i] = (byte) ((r << 5) + (g << 2) + b);
		}
		return pixb;
	}

	public void dispose()
	{
		getRGBPixelsMethodParam = null;
		Method method = getRGBPixelsMethod;
		if (method != null)
		{
			getRGBPixelsMethod = null;
			try
			{
				method.setAccessible(false);
			}
			catch (Exception ex) { }
		}
		//Using reflection now because of some peers not having ANY support at all (1.5)
		try
		{
			peer.getClass().getDeclaredMethod("dispose", new Class<?>[0]).invoke(peer, new Class<?>[0]);
		}
		catch (Exception ex) { }
	}

	protected void finalize() throws Throwable
	{
		try
		{
			dispose();
		}
		finally
		{
			super.finalize();
		}
	}

	private Object getRGBPixelsMethodParam;
	private int getRGBPixelsMethodType;
	public final GraphicsDevice device;
	private Method getRGBPixelsMethod;
	private final RobotPeer peer;
	private static boolean hasMouseInfoPeer;
	private static MouseInfoPeer mouseInfoPeer;
}
