/**
 * 
 */
package com.sthiec.communication;

import gnu.io.*;

import java.io.*;

/**
 * @author Lorhur Vultrue
 * 
 */
public class Test {
	OutputStream out;

	/**
	 * @param args
	 * @throws NoSuchPortException
	 * @throws PortInUseException
	 * @throws UnsupportedCommOperationException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws NoSuchPortException, PortInUseException, UnsupportedCommOperationException, IOException, InterruptedException {
		//Test a = new Test();
		//a.connect();
//		GSM_PDU_Submit pdu1 = new GSM_PDU_Submit("13319235473", "这是一个超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超");
//		GSM_PDU_Submit pdu2 = new GSM_PDU_Submit("13319235473", "超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超超长JAVA中文短信！");
//		pdu1.setTP_UDHI(true); pdu2.setTP_UDHI(true);
//		pdu1.setTP_UDH_RN((byte)5); pdu2.setTP_UDH_RN((byte)5);
//		pdu1.setTP_UDH_PT((byte)2); pdu2.setTP_UDH_PT((byte)2);
//		pdu1.setTP_UDH_PN((byte)1); pdu2.setTP_UDH_PN((byte)2);
//		//System.out.println(pdu1);
//		//System.out.println(pdu2);
//		System.out.print("AT^HCMGS=");
//		System.out.println(pdu1.lenth());
//		System.out.print("00");
//		System.out.println(pdu1);
//		System.out.print("AT^HCMGS=");
//		System.out.println(pdu2.lenth());
//		System.out.print("00");
//		System.out.println(pdu2);
		CDMA_PDU_Parameter_Address x = new CDMA_PDU_Parameter_Address("18966921129");
		System.out.println(x);
	}

	public void connect() throws NoSuchPortException, PortInUseException,
			UnsupportedCommOperationException, IOException, InterruptedException {

		CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier("/dev/ttyUSB0");

		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open("", 2000);
			if (!(commPort instanceof SerialPort)) {
				System.out.println("Error: Need a SerialPort for this test");
				return;
			}
			SerialPort port = (SerialPort)commPort;
			port.setSerialPortParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			(new Thread(new SerialReader(port.getInputStream()))).start();
			out = port.getOutputStream();
			//(new Thread(new SerialWriter())).start();
		}
	}

	public class SerialReader implements Runnable {
		InputStream in;

		public SerialReader(InputStream in) {
			this.in = in;
		}

		@Override
		public void run() {
			byte[] buffer = new byte[1024];
			int len = -1;
			try {
				while ((len = this.in.read(buffer)) > -1) {
					System.out.print(new String(buffer, 0, len));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class SerialWriter implements Runnable {
		OutputStream out;

		public SerialWriter(OutputStream out) {
			this.out = out;
		}

		@Override
		public void run() {
			try {
				int c = 0;
				while ((c = System.in.read()) > -1) {
					if (c == '\n') {
						this.out.write('\r');
					}
					this.out.write(c);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
	}

	class SerialEventReader implements SerialPortEventListener {
		private InputStream in;
		private byte[] buffer = new byte[1024];

		public SerialEventReader(InputStream in) {
			this.in = in;
		}

		public void serialEvent(SerialPortEvent arg0) {
			int data;

			try {
				int len = 0;
				while ((data = in.read()) > -1) {
					if (data == '\n') {
						break;
					}
					buffer[len++] = (byte)data;
					System.out.print(new String(buffer, 0, len));
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}

	}
}
