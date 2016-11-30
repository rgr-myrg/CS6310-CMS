package edu.gatech.cms.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;
import edu.gatech.cms.sql.AssignmentsTable;
import edu.gatech.cms.sql.PrerequisitesTable;
import edu.gatech.cms.sql.RequestsTable;
import edu.gatech.cms.sql.CoursesTable;
import edu.gatech.cms.sql.RecordsTable;
import edu.gatech.cms.sql.UniversityPersonTable;
import edu.gatech.cms.sql.WaitingTable;

public class DbHelper {
	public static final String SQLITE_JDBC_DRIVER = "org.sqlite.JDBC";
	public static final String DB_CONNECTION_URL  = "jdbc:sqlite:cs6310.db";
	public static final String DB_USER_NAME = "nobody";
	public static final String DB_PASSWORD = "";

	public static Connection connection = null;

	public static final Connection getConnection() {
		if (connection == null) {
			try {
				Class.forName(SQLITE_JDBC_DRIVER);
				connection = DriverManager.getConnection(DB_CONNECTION_URL);
			} catch (ClassNotFoundException e) {
				if (Log.isDebug()) {
					Logger.debug(SQLITE_JDBC_DRIVER, "ClassNotFoundException: " + e.getMessage());
				}
			} catch (SQLException e) {
				logSqlException(e);
			}
		}

		return connection;
	}

	public static final void closeConnection() {
		if (connection == null) {
			logConnectionError();
			return;
		}

		try {
			connection.close();
		} catch (SQLException e) {
			logSqlException(e);
		}
	}

	public static final void createTables() {
		final Connection conn = getConnection();

		if (conn == null) {
			logConnectionError();
			return;
		}

		try {
			PreparedStatement preparedStatement = conn.prepareStatement(CoursesTable.CREATE_TABLE);
			preparedStatement.execute();

			preparedStatement = conn.prepareStatement(UniversityPersonTable.CREATE_TABLE);
			preparedStatement.execute();

			preparedStatement = conn.prepareStatement(RecordsTable.CREATE_TABLE);
			preparedStatement.execute();

			preparedStatement = conn.prepareStatement(AssignmentsTable.CREATE_TABLE);
			preparedStatement.execute();

			preparedStatement = conn.prepareStatement(PrerequisitesTable.CREATE_TABLE);
			preparedStatement.execute();

			preparedStatement = conn.prepareStatement(RequestsTable.CREATE_TABLE);
			preparedStatement.execute();
			
			preparedStatement = conn.prepareStatement(WaitingTable.CREATE_TABLE);
			preparedStatement.execute();
		} catch (SQLException e) {
			logSqlException(e);
		}
	}

	public static final void dropTables() {
		final Connection conn = getConnection();

		if (conn == null) {
			logConnectionError();
			return;
		}

		try {
			PreparedStatement preparedStatement = conn.prepareStatement(CoursesTable.DROP_TABLE);
			preparedStatement.execute();

			preparedStatement = conn.prepareStatement(UniversityPersonTable.DROP_TABLE);
			preparedStatement.execute();

			preparedStatement = conn.prepareStatement(RecordsTable.DROP_TABLE);
			preparedStatement.execute();

			preparedStatement = conn.prepareStatement(AssignmentsTable.DROP_TABLE);
			preparedStatement.execute();

			preparedStatement = conn.prepareStatement(PrerequisitesTable.DROP_TABLE);
			preparedStatement.execute();

			preparedStatement = conn.prepareStatement(RequestsTable.DROP_TABLE);
			preparedStatement.execute();
			
			preparedStatement = conn.prepareStatement(WaitingTable.DROP_TABLE);
			preparedStatement.execute();

		} catch (SQLException e) {
			logSqlException(e);
		}
	}

	public static final ResultSet doSql(final String sqlStr) {
		final Connection conn = getConnection();

		if (conn == null || sqlStr == null) {
			logConnectionError();
			return null;
		}

		PreparedStatement preparedStatement;
		ResultSet resultSet = null;

		try {
			preparedStatement = conn.prepareStatement(sqlStr);

			if (preparedStatement == null) {
				return null;
			}

			resultSet = preparedStatement.executeQuery();

		} catch (SQLException e) {
			logSqlException(e);
		}

		return resultSet;
	}

	public static final boolean doUpdateSql(final String sqlStr) {
		final Connection conn = getConnection();
		int result = 0;

		if (conn == null || sqlStr == null) {
			logConnectionError();
			return false;
		}

		try {
			final Statement statement = conn.createStatement();
			result = statement.executeUpdate(sqlStr);
		} catch (SQLException e) {
			logSqlException(e);
		}

		return result > 0;
	}

	public static final int getCountOf(final String sqlStr) {
		final Connection conn = getConnection();
		int total = -1;

		if (conn == null) {
			logConnectionError();
			return total;
		}

		ResultSet resultSet = DbHelper.doSql(sqlStr);

		try {
			total = resultSet.getInt("total");
		} catch (SQLException e) {
			logSqlException(e);
		}

		return total;
	}

	public static final void logSqlException(final SQLException e) {
		if (Log.isDebug()) {
			Logger.debug(SQLITE_JDBC_DRIVER, e.getMessage());
		}
	}

	public static final void logConnectionError() {
		if (Log.isDebug()) {
			Logger.debug(SQLITE_JDBC_DRIVER, "Error: Connection is NULL.");
		}
	}
}
