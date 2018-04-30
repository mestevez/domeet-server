package model;

public enum MeetingState {
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
}
