/**
 * 
 */
package com.sthiec.communication;

/**
 * @author lorhur
 *
 */
public class CDMA_PDU {
	private byte smt = 0x00;
	
	protected CDMA_PDU_Parameter[] parameters;
	
	public byte getSmsMsgType() {
		return smt;
	}
	
	public void setSmsMsgType(byte smsMsgType) {
		smt = smsMsgType;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%1$02X", smt));
		if (parameters != null)
			for (CDMA_PDU_Parameter prm : parameters)
				sb.append(prm.toString());
		return sb.toString();
	}
	
	public static int bytesLeftShift(byte[] bytes, int shift) {
		shift &= 0x07;
		int mask = 1;
		for (int i = 1; i < shift; i++)
			mask = (mask << 1) + 1;
		int shiftb = 8 - shift;
		int result = bytes[0] >> shiftb;
		for (int i = 0; i < bytes.length - 1; i++)
			bytes[i] = (byte)((bytes[i] << shift) | ((bytes[i+1] >> shiftb) & mask));
		bytes[bytes.length - 1] <<= shift;
		return result;
	}
}

class CDMA_PDU_Parameter {
	private byte[] pm = new byte[2];
	
	public int getParameterID() {
		return pm[0];
	}
	
	public void setParameterID(byte parameterID) {
		pm[0] = parameterID;
	}
	
	public int getParameterLen() {
		return pm[1];
	}
	
	public void setParameterLen(byte subparamLen) {
		pm[1] = subparamLen;
	}
	
	@Override
	public String toString() {
		return String.format("%1$02X%2$02X", pm[0], pm[1]);
	}
}

class CDMA_PDU_Parameter_TeleserviceIdentifier extends CDMA_PDU_Parameter {
	private short ti = 0x1002;
	
	public int getIdentifier() {
		return ti;
	}
	
	public void setIdentifier(short identifier) {
		ti = identifier;
	}
	
	public CDMA_PDU_Parameter_TeleserviceIdentifier() {
		setParameterID((byte)0x00);
		setParameterLen((byte)0x02);
	}
	
	@Override
	public String toString() {
		return super.toString() + String.format("%1$04X", ti);
	}
}

class CDMA_PDU_Parameter_Address extends CDMA_PDU_Parameter {
	private byte num;
	private byte[] addr;
	
	public CDMA_PDU_Parameter_Address(String address) {
		setParameterID((byte)0x04);
		setParameterLen((byte)0x07);
		num = (byte)address.length();
		address = address.replace('0', 'A');
		Byte.parseByte(s, radix)
		addr = Long.parseLong(address, 16) << 2;
	}
	
	@Override
	public String toString() {
		return super.toString() + String.format("%1$014X", addr);
	}
}

class CDMA_PDU_Parameter_BearerReplyOption extends CDMA_PDU_Parameter {
	private int seq = 0;
	
	public int getReplySEQ() {
		return (seq >> 2) & 0x3F;
	}
	
	public void setReplySEQ(int replySEQ) {
		seq = (replySEQ << 2) & ~0xFC;
	}
	
	public CDMA_PDU_Parameter_BearerReplyOption() {
		setParameterID(0x06);
		setParameterLen(0x01);
	}
	
	@Override
	public String toString() {
		return super.toString() + String.format("%1$02X", seq);
	}
}

class CDMA_PDU_Subparameter {
	private int sp;
	
	public int getSubparameterID() {
		return (sp >> 8) & 0xFF;
	}
	
	public void setSubparameterID(int subparameterID) {
		sp = (sp & ~0xFF00) | ((subparameterID << 8) & 0xFF00);
	}
	
	public int getSubparamLen() {
		return sp & 0xFF;
	}
	
	public void setSubparamLen(int subparamLen) {
		sp = (sp & ~0xFF) | (subparamLen & 0xFF);
	}
	
	@Override
	public String toString() {
		return String.format("%1$04X", sp);
	}
}

class CDMA_PDU_Subparameter_MessageIdentifier extends CDMA_PDU_Subparameter {
	private int mi;
	
	public boolean isHeaderInd() {
		return (mi & 0x80) != 0; 
	}
	
	public void setHeaderInd(boolean headerInd) {
		if (headerInd) mi |= 0x80;
		else mi &= ~0x80;
	}
	
	public int getMessageID() {
		return (mi >> 4) & 0xFFFF; 
	}
	
	public void setMessageID(int messageID) {
		mi = (mi & ~0x000FFFF0) | ((messageID << 4) & 0x000FFFF0);
	}
	
	public int getMessageType() {
		return (mi >> 20) & 0x0F;
	}
	
	public void setMessageType(int messageType) {
		mi = (mi & ~0x00F00000) | ((messageType << 20) & 0x00F00000);
	}
}