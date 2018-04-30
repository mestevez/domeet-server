package model;

public enum MeetingType {
	UNDETERMINED((short)0),
	INFORM((short)1),
	CONSULT((short)2),
	ISSUE_SOLVING((short)3),
	DECISION_MAKING((short)4);

	private short enumvar;

	MeetingType (short val) {
		enumvar = val;
	}
}
