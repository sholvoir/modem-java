/**
 * sthiec.com通信包
 */
package com.sthiec.communication;

import gnu.io.*;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * @author Lorhur Modem基类，提供一些通用的方法和接口
 */
public abstract class Modem implements Closeable, Runnable {

	protected final int MAX_MESSAGE_LENTH = 280;

	private final int BUFFER_SIZE = 2048;
	private String portName;
	private int baudRate = 19200;
	private int dataBits = SerialPort.DATABITS_8;
	private int stopBits = SerialPort.STOPBITS_1;
	private int parity = SerialPort.PARITY_NONE;

	private boolean isOpen = false;
	private SerialPort serialPort;
	private InputStream inputStream;
	private OutputStream outputStream;
	private Thread readThread;
	private byte[] buffer = new byte[BUFFER_SIZE];
	private int bufferIndex = 0;
	private M_Task currentTask;

	public String getPortName() {
		return portName;
	}

	public void setPortName(String portName) {
		this.portName = portName;
	}

	public int getBaudRate() {
		return baudRate;
	}

	public void setBaudRate(int baudRate) {
		this.baudRate = baudRate;
	}

	public int getDataBits() {
		return dataBits;
	}

	public void setDataBits(int dataBits) {
		this.dataBits = dataBits;
	}

	public int getStopBits() {
		return stopBits;
	}

	public void setStopBits(int stopBits) {
		this.stopBits = stopBits;
	}

	public int getParity() {
		return parity;
	}

	public void setParity(int parity) {
		this.parity = parity;
	}

	public void open() throws NoSuchPortException, PortInUseException,
			UnsupportedCommOperationException, IOException, IllegalArgumentException,
			TooManyListenersException {
		if (isOpen)
			return;
		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		if (portIdentifier.isCurrentlyOwned())
			throw new PortUnreachableException("Port is currently in use.");
		CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);
		if (!(commPort instanceof SerialPort))
			throw new IllegalArgumentException("This port is not a serial port.");
		serialPort = (SerialPort)commPort;
		serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
		inputStream = serialPort.getInputStream();
		outputStream = serialPort.getOutputStream();
		isOpen = true;
		(readThread = new Thread(this)).start();
	}

	@Override
	public void close() throws IOException {
		if (!isOpen)
			return;
		isOpen = false;
		readThread.interrupt();
		outputStream.flush();
		outputStream.close();
		inputStream.close();
		serialPort.close();
	}

	@Override
	public void run() {
		int data = -1;
		while (isOpen) {
			try {
				if ((data = inputStream.read()) < 0) {
					break;
				}
			} catch (IOException e) {
				break;
			}
			buffer[bufferIndex++] = (byte)data;
			if (bufferIndex > 1) {
				if ('\r' == buffer[bufferIndex - 2] && '\n' == buffer[bufferIndex - 1]) {
					if (bufferIndex > 2) {
						dealLine(L_MLine.parse(buffer, bufferIndex - 2));
					}
					bufferIndex = 0;
				} else if ('>' == buffer[bufferIndex - 2] && ' ' == buffer[bufferIndex - 1]) {
					dealLine(new L_SmallMessageInputPrompt());
					bufferIndex = 0;
				}
			}
		}
	}

	public void dealLine(L_MLine mline) {
		if (mline instanceof L_ATCommand || mline instanceof L_UnSupportMLineType) {
			return;
		}
		if (null == currentTask)
			dealURCLine(mline);
		else {
			currentTask.dealLine(mline);
		}
		// L_Mline x = L_MLine.parse(line, length);
	}

	private void dealURCLine(L_MLine mline) {
		// TODO 自动生成的方法存根
		
	}

	protected synchronized void sendCommand(String instruction) throws IOException {
		byte[] b = instruction.getBytes("US-ASCII");
		outputStream.write(b, 0, b.length);
	}

}
