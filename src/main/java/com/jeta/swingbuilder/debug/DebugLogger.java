/*
 * Copyright (C) 2005 Jeff Tassin
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.jeta.swingbuilder.debug;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import com.jeta.swingbuilder.store.LogRequest;

/**
 * This class implements a client that sends System.out and System.err messages
 * to a server running on the same maching. This is used for debugging webstart
 * apps.
 * 
 * @author Jeff Tassin
 */
public class DebugLogger {
	/** the socket used for sending/receiving data from the server */
	private Socket m_sock;

	/** the server port */
	private static final int SERVER_PORT = 7000;

	/**
	 * ctor
	 */
	public DebugLogger(String host) throws IOException, UnknownHostException {
		System.out.println("DebugLogger.starting...");
		InetAddress addr = InetAddress.getByName(host);
		// let's run the server on some user port, say 7000
		m_sock = new Socket(addr, SERVER_PORT);

		sendRequest(new LogRequest("Forms Client Starting..."));
	}

	/**
	 * Helper method that sends a message to the logger only when debugging
	 */
	public void logMessage(String msg) {
		sendRequest(msg);
	}

	/**
	 * Make a request of the server to do something
	 * 
	 * @param req
	 *            a request object that is sent to the server and determines
	 *            what the server should send back to the client
	 * @return the object returned by the server based on the request message
	 */
	public void sendRequest(String msg) {
		sendRequest(new LogRequest(msg));
	}

	/**
	 * Make a request of the server to do something
	 * 
	 * @param req
	 *            a request object that is sent to the server and determines
	 *            what the server should send back to the client
	 * @return the object returned by the server based on the request message
	 */
	public void sendRequest(LogRequest req) {
		try {
			// send the request to the server
			OutputStream os = m_sock.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(os);
			oos.writeObject(req);
			oos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
