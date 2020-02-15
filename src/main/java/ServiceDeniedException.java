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

public class ServiceDeniedException extends Exception 
{
    protected static final String[] SERVER_RESPONSES = new String[] 
	{
	    "OK",
	    "Service was shutting down",
	    "Server was/is experiencing internal errors",
	    "Service said we have requested some unsupported operation",
	    "Service said we sent an ill-formed request packet",
	    "Service said we were sending our request too slow",
	    "Authentication failed",
	    "User quota exceeded"
	};
    
    protected static final String[] SERVER_REMEDY = new String[]
	{
	    "None",
	    "Try again later",
	    "Try again later",
	    "Upgrade your client software",
	    "Upgrade your client software",
	    "Check your network connection",
	    "Check your login credentials",
	    "Try again later, or contact Service admin to increase your quota(s)"
	};

    int response, reason;
    
    public ServiceDeniedException(int response, int reason)
    {
	this.response = response;
	this.reason = reason;
    }

    public String toString() {
	return SERVER_RESPONSES[response] + " : " + SERVER_REMEDY[response];
    }
}