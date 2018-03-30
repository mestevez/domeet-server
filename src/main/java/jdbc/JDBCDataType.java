package jdbc;

public class JDBCDataType {

	static int TYPE_UNKNOWN					= 0;

	static int TYPE_CHAR 					= 10;
	static int TYPE_CHARACTER				= 11;
	static int TYPE_LONG 					= 12;
	static int TYPE_STRING 					= 13;
	static int TYPE_VARCHAR 				= 14;
	static int TYPE_VARCHAR2 				= 15;

	static int TYPE_NCHAR					= 20;
	static int TYPE_NVARCHAR2				= 21;

	static int TYPE_RAW						= 30;
	static int TYPE_LONG_RAW				= 31;

	static int TYPE_BINARY_INTEGER			= 40;
	static int TYPE_NATURAL					= 41;
	static int TYPE_NATURALN				= 42;
	static int TYPE_PLS_INTEGER				= 43;
	static int TYPE_POSITIVE				= 44;
	static int TYPE_POSITIVEN				= 45;
	static int TYPE_SIGNTYPE				= 46;
	static int TYPE_INT						= 47;
	static int TYPE_INTEGER					= 48;

	static int TYPE_DEC						= 60;
	static int TYPE_DECIMAL					= 61;
	static int TYPE_NUMBER					= 62;
	static int TYPE_NUMERIC					= 63;

	static int TYPE_DOUBLE_PRECISION		= 70;
	static int TYPE_FLOAT					= 71;

	static int TYPE_SMALLINT				= 80;

	static int TYPE_REAL					= 90;

	static int TYPE_DATE					= 100;

	static int TYPE_TIMESTAMP				= 101;

	static int TYPE_TIMESTAMP_WITH_TZ		= 102;
	static int TYPE_TIMESTAMP_WITH_LOCAL_TZ	= 103;

	static int TYPE_INTERVAL_YEAR_TO_MONTH	= 150;
	static int TYPE_INTERVAL_DAY_TO_SECOND	= 151;

	static int TYPE_ROWID					= 160;
	static int TYPE_UROWID					= 160;

	static int TYPE_BOOLEAN					= 160;

	static int TYPE_CLOB					= 170;
	static int TYPE_BLOB					= 171;
	static int TYPE_BFILE					= 172;

	public static boolean isNumeric(int sqltype) {
		return
				sqltype == TYPE_BINARY_INTEGER ||
				sqltype == TYPE_NATURAL ||
				sqltype == TYPE_NATURALN ||
				sqltype == TYPE_PLS_INTEGER ||
				sqltype == TYPE_POSITIVE ||
				sqltype == TYPE_POSITIVEN ||
				sqltype == TYPE_SIGNTYPE ||
				sqltype == TYPE_INT ||
				sqltype == TYPE_INTEGER ||
				sqltype == TYPE_DEC ||
				sqltype == TYPE_DECIMAL ||
				sqltype == TYPE_NUMBER ||
				sqltype == TYPE_NUMERIC ||
				sqltype == TYPE_DOUBLE_PRECISION ||
				sqltype == TYPE_FLOAT ||
				sqltype == TYPE_SMALLINT ||
				sqltype == TYPE_REAL;
	}
}
