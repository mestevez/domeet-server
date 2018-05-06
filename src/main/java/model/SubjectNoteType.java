package model;

import util.SerializableOrdinalEnumeration;

public enum SubjectNoteType  implements SerializableOrdinalEnumeration {
	COMMENT((short)0),
	DECISION((short)1),
	AGREEMENT((short)2),
	UNSETTLED((short)3);

	private short enumvar;

	SubjectNoteType(short val) {
		enumvar = val;
	}

	@Override
	public short getKey() {
		return enumvar;
	}

	public static SubjectNoteType fromKey(short key) {
		for(SubjectNoteType type : values()) {
			if(type.getKey() == key) {
				return type;
			}
		}
		return null;
	}
}