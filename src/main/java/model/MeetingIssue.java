package model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.annotations.Expose;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonNaming(PropertyNamingStrategy.LowerCaseWithUnderscoresStrategy.class)
public class MeetingIssue {

	@Expose
	private MeetingIssueCode issue_code;
	@Expose
	private MeetingIssueType issue_type;
	@Expose
	private String issue_message;

	MeetingIssue(MeetingIssueCode issue_code, MeetingIssueType issue_type, String issue_message) {
		this.issue_code = issue_code;
		this.issue_type = issue_type;
		this.issue_message = issue_message;
	}

	public MeetingIssueCode getIssueCode() {
		return issue_code;
	}
}
