package model;

import util.SerializableOrdinalEnumeration;

public enum AttendantState implements SerializableOrdinalEnumeration {
	PENDING((short)0),
	NOTIFIED((short)1),
	REJECTED((short)2),
	CONFIRMED((short)3),
	ATTEND((short)4);

	private short enumvar;

	AttendantState(short val) {
		enumvar = val;
	}

	@Override
	public short getKey() {
		return enumvar;
	}

	static AttendantState fromKey(short key) {
		for(AttendantState type : values()) {
			if(type.getKey() == key) {
				return type;
			}
		}
		return null;
	}
}
