package jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JDBCCheckStatus {
	private final static Logger logger = LoggerFactory.getLogger(JDBCCheckStatus.class);

	private List<String>	errorsList;
	private List<String>	infoList;
	private List<String>	warningList;
	private String 			fatalError;

	JDBCCheckStatus() {
		errorsList = new ArrayList<>();
		infoList = new ArrayList<>();
		warningList = new ArrayList<>();
	}

	public void addErrorMessage(String errormsg) {
		logger.error(errormsg);
		errorsList.add(errormsg);
	}

	public void addInfoMessage(String infomsg) {
		logger.info(infomsg);
		infoList.add(infomsg);
	}

	public void addWarningMessage(String warningmsg) {
		logger.warn(warningmsg);
		warningList.add(warningmsg);
	}

	public String getFatalError() {
		return fatalError;
	}

	public List<String> getErrorsList() {
		return errorsList;
	}

	public List<String> getInfoList() {
		return infoList;
	}

	public List<String> getWarningsList() {
		return warningList;
	}

	public void setFatalError(String fatalError) {
		logger.error("FatalError: " + fatalError);
		this.fatalError = fatalError;
	}
}
