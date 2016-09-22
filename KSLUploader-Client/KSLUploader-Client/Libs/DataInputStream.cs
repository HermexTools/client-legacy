using System;
using System.IO;

public class DataInputStream 
{
	private byte[] bytearr = new byte[80];
	private char[] chararr = new char[80];
	private char[] LineBuffer;
	private byte[] ReadBuffer = new byte[8];

	private BinaryReader ClientInput;

	public DataInputStream(BinaryReader _clientInput)
	{
		ClientInput = _clientInput;
	}

	public int Read(byte[] b)
	{
		return ClientInput.Read(b, 0, b.Length);
	}

	public int Read(byte[] b, int off, int len)
	{
		return ClientInput.Read (b, off, len);
	}

	public void ReadFully(byte[] b)
	{
		ReadFully (b, 0, b.Length);
	}

	public void ReadFully(byte[] b, int off, int len)
	{
		if(len < 0)
		{
			throw new IndexOutOfRangeException();
		}

		int n = 0;
		while(n < len) 
		{
			int count = ClientInput.Read(b, off + n, len - n);
			if(count < 0)
			{
				throw new EndOfStreamException();
			}
			n += count;
		}
	}

	public byte ReadByte()
	{
		int ch = ClientInput.Read();
		if(ch < 0)
		{
			throw new EndOfStreamException();
		}
		return (byte)(ch);
	}

	public bool ReadBoolean()
	{
		int ch = ClientInput.Read ();
		if(ch < 0)
			throw new EndOfStreamException();
		return (ch != 0);
	}

	public String ReadUTF()
	{
		int utflen = this.ReadUnsignedShort ();

		if(bytearr.Length < utflen)
		{
			bytearr = new byte[utflen * 2];
			chararr = new char[utflen * 2];
		} 

		int c, char2, char3;
		int count = 0;
		int chararr_count=0;

		this.ReadFully(bytearr, 0, utflen);

		while (count < utflen) {
			c = (int) bytearr[count] & 0xff;
			if (c > 127) break;
			count++;
			chararr[chararr_count++]=(char)c;
		}

		while (count < utflen) {
			c = (int) bytearr[count] & 0xff;
			switch (c >> 4) {
			case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
				/* 0xxxxxxx*/
				count++;
				chararr[chararr_count++]=(char)c;
				break;
			case 12: case 13:
				/* 110x xxxx   10xx xxxx*/
				count += 2;
				if (count > utflen)
					throw new Exception(
						"malformed input: partial character at end");

				char2 = (int) bytearr[count-1];
				if ((char2 & 0xC0) != 0x80)
					throw new Exception(
						"malformed input around byte " + count);

				chararr[chararr_count++]=(char)(((c & 0x1F) << 6) |
				                                (char2 & 0x3F));
				break;
			case 14:
				/* 1110 xxxx  10xx xxxx  10xx xxxx */
				count += 3;
				if (count > utflen)
					throw new Exception(
						"malformed input: partial character at end");

				char2 = (int) bytearr[count-2];
				char3 = (int) bytearr[count-1];
				if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
					throw new Exception(
						"malformed input around byte " + (count-1));

				chararr[chararr_count++]=(char)(((c     & 0x0F) << 12) |
				                                ((char2 & 0x3F) << 6)  |
				                                ((char3 & 0x3F) << 0));
				break;
			default:
				/* 10xx xxxx,  1111 xxxx */
				throw new Exception(
					"malformed input around byte " + count);
			}
		}
		// The number of chars produced may be less than utflen
		return new String(chararr, 0, chararr_count);
	}

	public char ReadChar()
	{
		int ch1 = ClientInput.Read();
		int ch2 = ClientInput.Read();
		if ((ch1 | ch2) < 0) 
		{
			throw new EndOfStreamException();
		}
		return (char)((ch1 << 8) + (ch2 << 0));
	}

	public double ReadDouble()
	{
		return BitConverter.Int64BitsToDouble(ReadLong());
	}

	public float ReadFloat()
	{
		return (float)ReadDouble ();
	}

	public int ReadInt()
	{
		int ch1 = ClientInput.Read ();
		int ch2 = ClientInput.Read ();
		int ch3 = ClientInput.Read ();
		int ch4 = ClientInput.Read ();

		if ((ch1 | ch2 | ch3 | ch4) < 0)
		{
			throw new EndOfStreamException();
		}
		return ((ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0));
	}

	public long ReadLong()
	{
		ReadFully (ReadBuffer, 0, 8);
		return (((long)ReadBuffer[0] << 56) +
		        ((long)(ReadBuffer[1] & 255) << 48) +
		        ((long)(ReadBuffer[2] & 255) << 40) +
		        ((long)(ReadBuffer[3] & 255) << 32) +
		        ((long)(ReadBuffer[4] & 255) << 24) +
		        ((ReadBuffer[5] & 255) << 16) +
		        ((ReadBuffer[6] & 255) <<  8) +
		        ((ReadBuffer[7] & 255) <<  0));
	}

	public short ReadShort()
	{
		int ch1 = ClientInput.Read ();
		int ch2 = ClientInput.Read ();
		if ((ch1 | ch2) < 0)
		{
			throw new EndOfStreamException();
		}
		return (short)((ch1 << 8) + (ch2 << 0));
	}

	public int ReadUnsignedShort()
	{
		int ch1 = ClientInput.Read();
		int ch2 = ClientInput.Read();
		if ((ch1 | ch2) < 0)
		{
			throw new EndOfStreamException();
		}
		return (ch1 << 8) + (ch2 << 0);	
	}
	
}