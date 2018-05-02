package model;

import util.SerializableOrdinalEnumeration;

public enum SubjectPriority implements SerializableOrdinalEnumeration {
	IRRELEVANT((short)0),
	NORMAL((short)1),
	ESSENTIAL((short)2);

	private short enumvar;

	SubjectPriority(short val) {
		enumvar = val;
	}

	@Override
	public short getKey() {
		return enumvar;
	}

	static SubjectPriority fromKey(short key) {
		for(SubjectPriority type : values()) {
			if(type.getKey() == key) {
				return type;
			}
		}
		return null;
	}
}
