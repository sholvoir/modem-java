/**
 * 
 */
package com.sthiec.communication;

/**
 * @author Lorhur
 * 
 */
public class PDU_Address {
	private String phoneNum;
	private byte lenth, type;
	private byte[] addr;
	private int addr_offset = 0, addr_count;
	
	public byte getTON() {
		return (byte)(type & 0x70);
	}
	
	public void setTON(byte ton) {
		type = (byte)((type & ~0x70) | (ton & 0x70));
	}
	
	public byte getNPI() {
		return (byte)(type & 0x08);
	}
	
	public void setNPI(byte npi) {
		type = (byte)((type & ~0x08) | (npi & 0x08));
	}
	
	public String getPhoneNumber() {
		return phoneNum;
	}

	public PDU_Address(String phoneNumber) {
		phoneNum = phoneNumber;
		if (phoneNumber.length() == 0) {
			lenth = 0;
			type = (byte)0x80;
			return;
		}
		boolean international;
		if (international = phoneNumber.startsWith("+"))
			phoneNumber = phoneNumber.substring(1);
		else if (international = phoneNumber.startsWith("00"))
			phoneNumber = phoneNumber.substring(2);
		lenth = (byte)phoneNumber.length();
		type = (byte)(international ? 0x91 : 0xA1);
		if ((lenth & 0x01) != 0)
			phoneNumber += "F";
		addr = new byte[addr_count = phoneNumber.length() / 2];
		for (int i = addr_offset; i < addr_offset + addr_count; i++) {
			String s1 = phoneNumber.substring(2 * i, 2 * i + 1);
			String s2 = phoneNumber.substring(2 * i + 1, 2 * i + 2);
			int x1 = Byte.parseByte(s1, 16);
			int x2 = Byte.parseByte(s2, 16) << 4;
			addr[i] = (byte)(x1|x2);
		}
	}
	
	public PDU_Address(byte[] sms, int offset, int count) {
		lenth = sms[offset++];
		type = sms[offset++];
		addr = sms;
		addr_offset = offset + 2;
		addr_count = count - 2;
		StringBuilder phoneNumber = new StringBuilder(lenth + 1);
		for (int i = addr_offset; i < addr_offset + addr_count; i++) {
			phoneNumber.append(String.format("%1$1X", addr[i] & 0x0F))
				.append(String.format("%1$1X", addr[i] & 0xF0));
		}
		phoneNum = phoneNumber.substring(0, lenth);
		if (getTON() == 0x10)
			phoneNum = "+" + phoneNum;
	}
	
	public int lenth() {
		return addr_count + 2;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder((addr.length + 2) * 2);
		sb.append(String.format("%1$02X%2$02X", lenth, type));
		for (int i = addr_offset; i < addr_offset + addr_count; i++)
			sb.append(String.format("%1$02X", addr[i]));
		return sb.toString();
	}

}
