/**
 * 
 */
package com.sthiec.communication;

/**
 * 行对象基类，行类命名规则：</br> 以L_开头，特殊字符按下面的规则替换：</br> + -> P_ Plus</br> ^ -> C_
 * Caret</br> SPACE -> _S_</br> 特殊的几种类型:</br> L_MLine:所有行类型的公共基类</br>
 * L_ATCommand:AT命令行基类</br> L_SmallMessageInputPrompt:短信内容输入行
 * 
 * @author Lorhur
 */
class L_MLine {
	void initialize(M_Node node){
	}
	
	static L_MLine parse(byte[] buffer, int length) {
		int i = 0;
		if (length > 2) {
			if (('a' == buffer[0] || 'A' == buffer[0]) && ('t' == buffer[1] || 'T' == buffer[1])) {
				return new L_ATCommand();
			}
		}
		for (; i < length; i++)
			if (':' == buffer[i])
				break;
		try {
			String head = new String(buffer, 0, i, "US-ASCII");//.toUpperCase();
			head = "L_" + head.replace(" ", "_S_").replace("+", "P_").replace("^", "C_");
			M_Node body = M_Node.parse(buffer, i + 1, length - 1);
			Class<?> c = Class.forName(head);
			L_MLine mline = (L_MLine)c.newInstance();
			mline.initialize(body);
			return mline;
		} catch (Exception e) {
			return new L_UnSupportMLineType();
		}
	}
}

class L_ATCommand extends L_MLine {
}

class L_SmallMessageInputPrompt extends L_MLine {
}

class L_URCInformation {
}

class L_UnSupportMLineType extends L_MLine {
}

class L_OK extends L_MLine {
}

class L_ERROR extends L_MLine {
	int err;
}

class L_P_CME_S_ERROR extends L_ERROR {
	@Override
	void initialize(M_Node node){
		err = Integer.parseInt(node.childs.get(0).getData());
	}
}

class L_P_CMS_S_ERROR extends L_ERROR {
	@Override
	void initialize(M_Node node){
		err = Integer.parseInt(node.childs.get(0).getData());
	}
}