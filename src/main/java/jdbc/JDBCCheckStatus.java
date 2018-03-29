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
		logger.info("[ERROR] - %s", errormsg);
		errorsList.add(errormsg);
	}

	public void addInfoMessage(String infomsg) {
		logger.info("[INFO] - %s", infomsg);
		infoList.add(infomsg);
	}

	public void addWarningMessage(String warningmsg) {
		logger.info("[WARN] - %s", warningmsg);
		warningList.add(warningmsg);
	}

	public String getFatalError() {
		return fatalError;
	}

	public void setFatalError(String fatalError) {
		logger.info("[FATAL] - %s", fatalError);
		this.fatalError = fatalError;
	}
}
