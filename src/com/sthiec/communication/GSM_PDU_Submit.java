/**
 * 
 */
package com.sthiec.communication;

import java.io.*;

/**
 * @author lorhur
 *
 */
public class GSM_PDU_Submit extends GSM_PDU {
	private byte tp_mr = 00;
	private PDU_Address tp_da;
	private byte[] tp_vp = new byte[] { (byte)169 };
	private byte[] tp_udh = null;
	
	public boolean getTP_RD() {
		return (tp_title & 0x04) != 0;
	}
	
	public void setTP_RD(boolean rd) {
		if (rd) tp_title |= 0x04;
		else tp_title &= ~0x04;
	}
	
	public byte getTP_VPF() {
		return (byte)(tp_title & 0x18);
	}
	
	public void setTP_VPF(byte vpf) {
		tp_title = (byte)((tp_title & ~0x18) | (vpf & 0x18));
	}
	
	public boolean getTP_SRR() {
		return (tp_title & 0x20) != 0;
	}
	
	public void setTP_SRR(boolean srr) {
		if (srr) tp_title |= 0x20;
		else tp_title &= ~0x20;
	}
	
	public void setTP_UDHI(boolean udhi) {
		if (udhi) {
			tp_title |= 0x40;
			tp_udh = new byte[] { 05, 00, 03, 00, 02, 01 };
			tp_udl = (byte)(6 + tp_ud.length);
		} else {
			tp_title &= ~0x40;
			tp_udh = null;
			tp_udl = (byte)tp_ud.length;
		}
	}
	
	public byte getTP_UDH_RN() {
		return tp_udh[3];
	}
	
	public void setTP_UDH_RN(byte udh_rn) {
		tp_udh[3] = udh_rn;
	}
	
	public byte getTP_UDH_PT() {
		return tp_udh[4];
	}
	
	public void setTP_UDH_PT(byte udh_pt) {
		tp_udh[4] = udh_pt;
	}
	
	public byte getTP_UDH_PN() {
		return tp_udh[5];
	}
	
	public void setTP_UDH_PN(byte udh_pn) {
		tp_udh[5] = udh_pn;
	}
	
	public byte getTP_MR() {
		return tp_mr;
	}
	
	public void setTP_MR(byte mr) {
		tp_mr = mr;
	}
	
	public PDU_Address getTP_DA() {
		return tp_da;
	}
	
	public void setTP_DA(PDU_Address da) {
		tp_da = da;
	}

	public byte[] getTP_VP() {
		return tp_vp;
	}

	public void setTP_VP(byte[] vp) {
		this.tp_vp = vp;
	}
	
	public int lenth() {
		int l = 5 + tp_da.lenth() + tp_vp.length + tp_ud.length;
		return tp_udh == null ? l : l + tp_udh.length;
	}
	
	public GSM_PDU_Submit(String phoneNumber, String message) {
		tp_da = new PDU_Address(phoneNumber);
		tp_udl = (byte)(message.length() << 1);
		try {
			tp_ud = message.getBytes("UTF-16BE");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(164);
		sb.append(String.format("%1$02X%2$02X", tp_title, tp_mr));
		sb.append(tp_da);
		sb.append(String.format("%1$02X%2$02X", tp_pid, tp_dcs));
		for (byte b : tp_vp)
			sb.append(String.format("%1$02X", b));
		sb.append(String.format("%1$02X", tp_udl));
		if (tp_udh != null)
			for (byte b : tp_udh)
				sb.append(String.format("%1$02X", b));
		for (byte b : tp_ud)
			sb.append(String.format("%1$02X", b));
		return sb.toString();
	}
}
