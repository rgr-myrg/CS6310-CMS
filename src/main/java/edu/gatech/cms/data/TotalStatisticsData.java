package edu.gatech.cms.data;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import edu.gatech.cms.sql.TotalStatisticsTable;
import edu.gatech.cms.util.DbHelper;

public class TotalStatisticsData {
	public static final void load() {
		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = DbHelper.getConnection().prepareStatement(TotalStatisticsTable.INSERT_EXAMINED);
			preparedStatement.execute();

			preparedStatement = DbHelper.getConnection().prepareStatement(TotalStatisticsTable.INSERT_GRANTED);
			preparedStatement.execute();

			preparedStatement = DbHelper.getConnection().prepareStatement(TotalStatisticsTable.INSERT_FAILED);
			preparedStatement.execute();

			preparedStatement = DbHelper.getConnection().prepareStatement(TotalStatisticsTable.INSERT_WAITLISTED);
			preparedStatement.execute();
		} catch (SQLException e) {
			//e.printStackTrace();
		}
	}

	public static final Map<String, Integer> loadFromDB() {
		final Map<String, Integer> totalStatistics = new TreeMap<>();
		ResultSet resultSet = DbHelper.doSql(TotalStatisticsTable.SELECT_ALL);

		if (resultSet != null) {
			try {
				while (resultSet.next()) {
					String statName = resultSet.getString(TotalStatisticsTable.STATISTIC_NAME_COLUMN);
					int statTotal = resultSet.getInt(TotalStatisticsTable.STATISTIC_TOTAL_COLUMN);
					totalStatistics.put(statName, statTotal);
				}
			} catch (SQLException e) {
				//e.printStackTrace();
			}
		}

		return totalStatistics;
	}

	public static final void updateWithNewTotal(final String column, final int total) {
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = DbHelper.getConnection().prepareStatement(TotalStatisticsTable.UDATE_STATISTIC_TOTAL);

			preparedStatement.setInt(1, total);
			preparedStatement.setString(2, column);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	public static final void updateExaminedTotal(final int total) {
		updateWithNewTotal(TotalStatisticsTable.EXAMINED, total);
	}

	public static final void updateGrantedTotal(final int total) {
		updateWithNewTotal(TotalStatisticsTable.GRANTED, total);
	}

	public static final void updateFailedTotal(final int total) {
		updateWithNewTotal(TotalStatisticsTable.FAILED, total);
	}

	public static final void updateWaitlistedTotal(final int total) {
		updateWithNewTotal(TotalStatisticsTable.WAITLISTED, total);
	}
}