package model;

import util.SerializableOrdinalEnumeration;

public enum MeetingType implements SerializableOrdinalEnumeration {
	UNDETERMINED((short)0),
	INFORM((short)1),
	CONSULT((short)2),
	ISSUE_SOLVING((short)3),
	DECISION_MAKING((short)4);

	private short enumvar;

	MeetingType (short val) {
		enumvar = val;
	}

	@Override
	public short getKey() {
		return enumvar;
	}

	static MeetingType fromKey(short key) {
		for(MeetingType type : values()) {
			if(type.getKey() == key) {
				return type;
			}
		}
		return null;
	}
}
