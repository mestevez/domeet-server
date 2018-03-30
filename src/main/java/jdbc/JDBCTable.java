package jdbc;

import java.util.ArrayList;
import java.util.List;

public class JDBCTable {
	private String 					tableName;
	private String 					tableScheme;
	private ArrayList<JDBCColumn>	columns;

	JDBCTable(String schema, String tname) {
		tableScheme = schema;
		tableName	= tname;
		columns		= new ArrayList<>();
	}

	void addColumn(JDBCColumn column) {
		columns.add(column);
	}

	public List<JDBCColumn> getColumns() {
		return (List<JDBCColumn>) columns.clone();
	}

	public String getTableName() {
		return tableName;
	}

	public String getTableScheme() {
		return tableScheme;
	}
}
