/**
 * 
 */
package com.sthiec.communication;

import java.io.*;
import java.util.*;

/**
 * @author Lorhur Modem任务类
 */
class M_Task {
	private Modem modem;
	private Class<?> T;
	private boolean error;
	private List<L_MLine> result;

	boolean isError() {
		return error;
	}
	
	List<L_MLine> getResult() {
		return result;
	}

	M_Task(Modem modem, Class<?> T) throws InterruptedException {
		this.modem = modem;
		this.T = T;
		this.result = new ArrayList<L_MLine>();
	}

	synchronized void sendCommand(String cmd) throws IOException, InterruptedException {
		modem.sendCommand(cmd);
		this.wait(10000);
	}

	synchronized void dealLine(L_MLine mline) {
		if (mline instanceof L_OK){
			this.notify();
		} else if (mline instanceof L_ERROR) {
			error = true;
			this.notify();
		} else if (T.isInstance(mline)) {
			result.add(mline);
		}
	}
}
