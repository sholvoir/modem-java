/**
 * 
 */
package com.sthiec.communication;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * 简单AT命令响应词法分析器
 * 
 * @author Lorhur
 */
class M_Node {

	static byte[] buffer;
	static int index;
	static int tailIndex;

	static M_Node parse(byte[] buffer, int index, int tailIndex) {
		if (index < 0 || tailIndex < 0 || index > buffer.length || tailIndex > buffer.length
				|| index > tailIndex) {
			return null;
		}
		M_Node.buffer = buffer;
		M_Node.index = index;
		M_Node.tailIndex = tailIndex;
		M_Node node = new M_Node();
		new M_Node(node);
		return node;
	}
	
	M_Node parents;
	ArrayList<M_Node> childs;
	boolean range = false;
	int beginIndex;
	int endIndex;

	boolean isComposite() {
		return null != childs;
	}
	
	boolean isRange() {
		return range;
	}
	
	String getData() {
		try {
			return new String(buffer, beginIndex, endIndex - beginIndex + 1, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	String getData(String charsetName) {
		try {
			return new String(buffer, beginIndex, endIndex - beginIndex + 1, charsetName);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	private M_Node() {
		parents = null;
		childs = new ArrayList<M_Node>();
		beginIndex = index;
		endIndex = tailIndex;
	}
	
	private M_Node(M_Node parents) {
		if (null != (this.parents = parents)) {
			parents.childs.add(this);
		}
		beginIndex = index;
		for (; index <= tailIndex;) {
			switch (buffer[index++]) {
				case '(':
					childs = new ArrayList<M_Node>();
					new M_Node(this);
					endIndex = index - 1;
					break;
				case ')':
					endIndex = index - 2;
					return;
				case ',':
					endIndex = index - 2;
					new M_Node(parents);
					return;
				case '-':
					endIndex = index - 2;
					parents.range = true;
					new M_Node(parents);
					return;
			}
		}
		endIndex = index - 1;
	}
	
	@Override
	public String toString() {
		if (isComposite()) {
			StringBuilder strb = new StringBuilder();
			strb.append('(');
			for (M_Node node : childs) {
				strb.append(node.toString()).append(range ? '-':',');
			}
			strb.deleteCharAt(strb.length() - 1).append(')');
			return strb.toString();
		} else {
			return new String(buffer, beginIndex, endIndex - beginIndex + 1);
		}
	}
	
	public static void main(String[] args) {
		try {
			byte[] bs = "(3-5),((a,v),())".getBytes("US-ASCII");
			M_Node x = M_Node.parse(bs, 0, bs.length - 1);
			System.out.print(x.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
