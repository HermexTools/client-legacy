using System;
using System.Collections;
using System.IO;

public class DataOutputStream
{
	protected int Written;
	protected byte[] WriteBuffer = new byte[8];

	BinaryWriter ClientOutput;

	public DataOutputStream(BinaryWriter _clientOutput)
	{
		ClientOutput = _clientOutput;
	}

	public void Flush()
	{
		ClientOutput.Flush ();
	}

	public void Write(int b)
	{
		ClientOutput.Write (b);
		IncCount (1);
	}

	public void Write(byte[] b, int off, int len)
	{
		ClientOutput.Write (b, off, len);
		IncCount (len);
	}

	public void WriteBoolean(Boolean v)
	{
		ClientOutput.Write (v ? 1 : 0);
		IncCount (1);
	}

    public void WriteByte(int v)
    {
        ClientOutput.Write(v);
        IncCount(1);
    }

	public void WriteBytes(String s)
	{
		int len = s.Length;
		for(int i = 0; i < len; i++)
		{
			ClientOutput.Write ((byte)s.ToCharArray()[i]);
		}
		IncCount (len);
	}

	public void WriteChar(int v)
	{
		ClientOutput.Write ((byte)(((uint)v >> 8) & 0xFF));
		ClientOutput.Write ((byte)(((uint)v >> 0) & 0xFF));
		IncCount (2);
	}

	public void WriteChars(String s)
	{
		int len = s.Length;
		for(int i = 0; i < len; i++)
		{
			int v = s.ToCharArray()[i];
			ClientOutput.Write (((byte)((uint)v >> 8) & 0xFF));
			ClientOutput.Write (((byte)((uint)v >> 0) & 0xFF));
		}
		IncCount (len * 2);
	}

	public void WriteDouble(double v)
	{
		WriteLong(BitConverter.DoubleToInt64Bits(v));
	}

	public void WriteFloat(float v)
	{
		WriteDouble (System.Convert.ToDouble (v));
	}

	public void WriteInt(int v)
	{
		ClientOutput.Write((byte)(((uint)v >> 24) & 0xFF));
		ClientOutput.Write((byte)(((uint)v >> 16) & 0xFF));
		ClientOutput.Write((byte)(((uint)v >> 8) & 0xFF));
		ClientOutput.Write((byte)(((uint)v >> 0) & 0xFF));


		IncCount (4);
	}

	public void WriteLong(long v) 
	{
		WriteBuffer[0] = (byte)((ulong)v >> 56);
		WriteBuffer[1] = (byte)((ulong)v >> 48);
		WriteBuffer[2] = (byte)((ulong)v >> 40);
		WriteBuffer[3] = (byte)((ulong)v >> 32);
		WriteBuffer[4] = (byte)((ulong)v >> 24);
		WriteBuffer[5] = (byte)((ulong)v >> 16);
		WriteBuffer[6] = (byte)((ulong)v >>  8);
		WriteBuffer[7] = (byte)((ulong)v >>  0);
		ClientOutput.Write(WriteBuffer, 0, 8);
		IncCount(8);
	}

	public void WriteShort(short v)
	{
		ClientOutput.Write ((byte)(((uint)v >> 8) & 0xFF));
		ClientOutput.Write ((byte)(((uint)v >> 0) & 0xFF));
		IncCount (2);
	}
		         
	public int WriteUTF(string str)
	{
		int strlen = str.Length;
		int utflen = 0;
		int c, count = 0;

		for(int i = 0; i < strlen; i++) 
		{
			c = str.ToCharArray()[i];
			if((c >= 0x0001) && (c <= 0x007F)) 
			{
				utflen++;
			} 
			else if(c > 0x07FF)
			{
				utflen += 3;
			}
			else
			{
				utflen += 2;
			}
		}

		if(utflen > 65535)
		{
			throw new Exception("Encoded string is too long: " + utflen + " bytes");
		}

		byte[] bytearr = null;
		bytearr = new byte[(utflen*2) + 2];

		bytearr[count++] = (byte) (((uint)utflen >> 8) & 0xFF);
		bytearr[count++] = (byte) (((uint)utflen >> 0) & 0xFF);

		int x = 0;
		for(x = 0; x < strlen; x++) 
		{
			c = str.ToCharArray()[x];
			if (!((c >= 0x0001) && (c <= 0x007F))) break;
			bytearr[count++] = (byte)c;
		}

		for(;x < strlen; x++)
		{
			c = str.ToCharArray()[x];
			if ((c >= 0x0001) && (c <= 0x007F)) 
			{
				bytearr[count++] = (byte)c;
			}
			else if (c > 0x07FF)
			{
				bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
				bytearr[count++] = (byte) (0x80 | ((c >>  6) & 0x3F));
				bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
			}
			else
			{
				bytearr[count++] = (byte) (0xC0 | ((c >>  6) & 0x1F));
				bytearr[count++] = (byte) (0x80 | ((c >>  0) & 0x3F));
			}
		}
		ClientOutput.Write (bytearr, 0, utflen+2);
		return utflen + 2;
	}
		
	private void IncCount(int value) 
	{
		int temp = Written + value;
		if(temp < 0)
		{
			temp = Int32.MaxValue;
		}
		Written = temp;
	}

	public int GetSize() 
	{
		return Written;
	}

	//public static unsafe uint FloatToUInt32Bits(float f) {
	//	return *((uint*)&f);
	//}
}