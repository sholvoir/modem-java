/**
 * 
 */
package com.sthiec.communication;

/**
 * @author lorhur
 *
 */

public class GSM_PDU {
	protected byte tp_title = 0x11;
	protected byte tp_pid = 0, tp_dcs = 8;
	protected byte tp_udl;
	protected byte[] tp_ud;
	
	public byte getTP_MTI() {
		return (byte)(tp_title & 0x03);
	}
	
	public void setTP_MTI(byte mit) {
		tp_title = (byte)((tp_title & ~0x03) | (mit & 0x03));
	}
	
	public boolean isTP_UDHI() {
		return (tp_title & 0x40) != 0;
	}
	
	public boolean getTP_RP() {
		return (tp_title & 0x80) != 0;
	}
	
	public void setTP_RP(boolean rp) {
		if (rp) tp_title |= 0x80;
		else tp_title &= ~0x80;
	}
	
	public byte getTP_PID() {
		return tp_pid;
	}
	
	public void setTP_PID(byte pid) {
		tp_pid = pid;
	}
	
	public byte getTP_DCS() {
		return tp_dcs;
	}
	
	public void setTP_DCS(byte dcs) {
		tp_dcs = dcs;
	}
	
	public byte getTP_UDL() {
		return tp_udl;
	}

	public void setTP_UDL(byte udl) {
		this.tp_udl = udl;
	}

	public byte[] getTP_UD() {
		return tp_ud;
	}

	public void setTP_UD(byte[] ud) {
		this.tp_ud = ud;
	}
}
