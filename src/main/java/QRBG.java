/**
 * QRBG.java
 * (c) 2007 Brendan Burns, 
 * Portions (c) 2007 Radomir Stevanovic and Rudjer Boskovic Institute.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * The QRBG class access the Quantum Random Bit Generator at the 
 * Rudjer Boskovic Institute
 * @author Brendan Burns 
 * @version 0.1
 **/
public class QRBG 
{
    protected byte[] buffer;
    protected byte[] msg;
    protected byte[] temp;

    protected int bufferPointer;
    protected int bytesAvailable;

    protected Socket s;
    protected InputStream in;
    protected OutputStream out;
    protected String host;
    protected int port;
    protected String user;
    protected String pass;

    protected static final int DEFAULT_CACHE_SIZE = 4096;

    protected static final byte GET_DATA_AUTH_PLAIN = 0;
    protected static final byte GET_DATA_AUTH_CERT = 1;
    protected static final byte GET_INFO_AUTH_PLAIN = 2;
    protected static final byte GET_INFO_AUTH_CERT = 3;

    protected static final byte OK = 0;
    protected static final byte SERVER_STOPPING = 1;
    protected static final byte SERVER_ERROR = 2;
    protected static final byte UNKNOWN_OP = 3;
    protected static final byte ILL_FORMED_REQUEST = 4;
    protected static final byte TIMEOUT = 5;
    protected static final byte AUTH_FAILED = 6;
    protected static final byte QUOTA_EXCEEDED = 7;

    /**
     * Constructor, uses default host ("random.irb.hr") and port (1227)
     * @param user Your user name
     * @param pass Your password
     **/
    public QRBG(String user, String pass)
    {
	this("random.irb.hr", 1227, user, pass);
    }

    /**
     * Constructor
     * @param host The host of the server
     * @param port The port of the server
     * @param user Your user name
     * @param user Your password
     **/
    public QRBG(String host, int port, String user, String pass) {
	this.host = host;
	this.port = port;
	this.user = user;
	this.pass = pass;
	this.bytesAvailable = 0;
	this.bufferPointer = 0;
	this.buffer = new byte[1000];
	this.msg = new byte[512];
	this.temp = new byte[256];
    }
    

    public byte getByte()
	throws IOException, ServiceDeniedException
    {
	acquireBytes(temp, 1);
	return temp[0];
    }

    public long getLong()
	throws IOException, ServiceDeniedException
    {
	acquireBytes(temp, 8);
	return readLong(temp, 0);
    }

    public int getInt() 
	throws IOException, ServiceDeniedException
    {
	acquireBytes(temp, 4);
	return readInt(temp, 0);
    }

    public float getFloat() 
	throws IOException, ServiceDeniedException
    {
	int data = 0x3F800000 | (getInt() & 0x00FFFFFF);
	return Float.intBitsToFloat(data) - 1.0f;
    }

    public double getDouble()
	throws IOException, ServiceDeniedException
    {
	long data = 0x3FF0000000000000l | (getLong() & 0x000FFFFFFFFFFFFFl);
	return Double.longBitsToDouble(data) - 1.0;
    }

    public void getBytes(byte[] data, int count)
	throws IOException, ServiceDeniedException
    {
	acquireBytes(data, count);
    }

    public void getFloats(float[] data, int count)
	throws IOException, ServiceDeniedException
    {
	for (int i=0;i<count;i++)
	    data[i] = getFloat();
    }

    public void getDoubles(double[] data, int count)
	throws IOException, ServiceDeniedException
    {
	for (int i=0;i<count;i++)
	    data[i] = getDouble();
    }	

    protected void connect() 
	throws IOException
    {
	if (in == null) {
	    s = new Socket(host, port);
	    in = s.getInputStream();
	    out = s.getOutputStream();
	}
    }

    protected void close() 
	throws IOException
    {
	in.close();
	out.close();
	s.close();
	in = null;
	out = null;
	s = null;
    }

    protected static void writeShort(byte[] buff, int start, int val)
    {
	buff[start] = (byte)((val >> 8) & 0xFF);
	buff[start+1] = (byte)(val & 0xFF);
    }

    protected static void writeInt(byte[] buff, int start, int val)
    {
	buff[start] = (byte)((val >> 24) & 0xFF);
	buff[start+1] = (byte)((val >> 16) & 0xFF);
	buff[start+2] = (byte)((val >> 8) & 0xFF);
	buff[start+3] = (byte)(val & 0xFF);
    }
    
    protected static long readLong(byte[] buff, int start) {
	long hi = readInt(buff, start);
	long lo = readInt(buff, start+4);
	return hi<<32 + lo;
    } 

    protected static int readInt(byte[] buff, int start)
    {
	int sum = 0;
	int x = buff[start];
	if (x < 0)
	    x += 256;
	sum += x<<24;
	x = buff[start+1];
	if (x < 0)
	    x += 256;
	sum += x<<16;
	x = buff[start+2];
	if (x < 0)
	    x += 256;
	sum += x<<8;
	x = buff[start+3];
	if (x < 0)
	    x += 256;
	sum += x;
	return sum;
    }

    protected static void writeString(byte[] buff, int start, String str)
    {
	byte[] chars = str.getBytes();
	for (int i=0;i<chars.length;i++)
	    buff[start+i] = chars[i];
    }

    protected void acquireBytes(byte[] dest, int size)
	throws IOException, ServiceDeniedException
    {
	if (bytesAvailable < size) {
	    acquireBytesFromService(DEFAULT_CACHE_SIZE);
	}
	for (int i=0;i<size;i++) {
	    dest[i] = buffer[bufferPointer++];
	    bytesAvailable--;
	}
    }
    
    protected void acquireBytesFromService(int size)
	throws IOException, ServiceDeniedException
    {
	connect();

	int contentSize = 1 + user.length() + 1 + pass.length() + 4;
	int ix = 0;
	msg[ix++] = GET_DATA_AUTH_PLAIN;
	writeShort(msg, ix, contentSize); ix += 2;
	msg[ix++] = (byte)(user.length() & 0xFF);
	writeString(msg, ix, user); ix += user.length();
	msg[ix++] = (byte)(pass.length() & 0xFF);
	writeString(msg, ix, pass); ix += pass.length();
	writeInt(msg, ix, size);
	
	out.write(msg, 0, ix+4);
	out.flush();
	
	int ret = in.read(msg, 0, 6);
	if (ret != -1) {
	    byte response = msg[0];
	    byte reason = msg[1];
	    
	    int dataLength = readInt(msg, 2);
	    
	    if (response != OK) {
		close();
		throw new ServiceDeniedException(response, reason);
	    }
	    
	    ///////////////////////EDITED CODE
	    ///////////////////////////////
	    if (buffer.length < dataLength) {
		buffer = new byte[dataLength];
            }
	    bytesAvailable = in.read(buffer, 0, dataLength);
            bufferPointer = 0;
            ///////////////////////////////
	}
	close();
    }
	
    public static void main(String[] args) 
	throws IOException, ServiceDeniedException
    {
       QRBG random = new QRBG("username", "password");
       //print the alphabetical characters contained in ten thousand random bytes 
       int i = 10000;
       byte[] b = new byte[1];
       while (i > 0) {
           b[0] = random.getByte();
           if ((b[0] <= 'Z' && b[0] >= 'A')||(b[0] <= 'z' && b[0] >= 'a')) {
               System.out.print(new String(b));
           }
           i--;
       }
    }
}