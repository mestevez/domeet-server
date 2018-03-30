package jdbc;

public class JDBCColumn {

	private String	columnName;
	private int 	columnType;
	private int 	columnSize;
	private int 	columnDecimalPrecision;
	private boolean isNullable;

	JDBCColumn(String cname, int type, boolean nullable, int size, int decimalPrecision) {
		columnName = cname;
		columnType = type;
		isNullable = nullable;
		columnSize = size;
		columnDecimalPrecision = decimalPrecision;
	}

	public String getColumnName() {
		return columnName;
	}

	public int getColumnType() {
		return columnType;
	}

	public int getColumnSize() {
		return columnSize;
	}

	public int getColumnDecimalPrecision() {
		return columnDecimalPrecision;
	}

	public boolean isColumnNullable() {
		return isNullable;
	}
}
