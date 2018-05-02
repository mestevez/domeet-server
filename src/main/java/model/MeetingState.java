package model;

import util.SerializableOrdinalEnumeration;

public enum MeetingState implements SerializableOrdinalEnumeration {
	EDIT((short)0),
	READY((short)1),
	CANCELLED((short)2),
	STARTED((short)3),
	ENDED((short)4),
	CONCLUDED((short)5),
	MAIL_SENT((short)6);

	private short enumvar;

	MeetingState(short val) {
		enumvar = val;
	}

	@Override
	public short getKey() {
		return enumvar;
	}

	static MeetingState fromKey(short key) {
		for(MeetingState type : values()) {
			if(type.getKey() == key) {
				return type;
			}
		}
		return null;
	}
}
