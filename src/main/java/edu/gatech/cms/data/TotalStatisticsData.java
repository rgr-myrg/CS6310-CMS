package edu.gatech.cms.data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;
import edu.gatech.cms.sql.TotalStatisticsTable;
import edu.gatech.cms.util.DbHelper;

public class TotalStatisticsData {
	public static final Map<String, Integer> getTotalStatistics() {
		final Map<String, Integer> totalStatistics = new TreeMap<>();
		ResultSet resultSet = DbHelper.doSql(TotalStatisticsTable.SELECT_ALL);

		try {
			while (resultSet.next()) {
				String statName = resultSet.getString(TotalStatisticsTable.STATISTIC_NAME_COLUMN);
				int statTotal = resultSet.getInt(TotalStatisticsTable.STATISTIC_TOTAL_COLUMN);
				Logger.debug("STATS", statName + ":" + statTotal);
				totalStatistics.put(statName, statTotal);
			}

			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		// We've arrived here for the first time during an 'initial' cycle since no db records exist.
		if (totalStatistics.isEmpty()) {
			totalStatistics.put(TotalStatisticsTable.EXAMINED, 0);
			totalStatistics.put(TotalStatisticsTable.FAILED, 0);
			totalStatistics.put(TotalStatisticsTable.GRANTED, 0);
			totalStatistics.put(TotalStatisticsTable.WAITLISTED, 0);
		}

		return totalStatistics;
	}

	public static final void saveTotalStats(final Map<String, Integer> totalStatistics) {
		for (Map.Entry<String,Integer> item : totalStatistics.entrySet()) {
			updateTotalWithKeyValue(item.getKey(), item.getValue());
		}
	}

	public static final void updateTotalWithKeyValue(final String key, final int total) {
		String updateSql = TotalStatisticsTable.UDATE_STATISTIC_TOTAL
				.replaceFirst("\\?", String.valueOf(key))
				.replaceFirst("\\?", String.valueOf(total));

		boolean success = DbHelper.doUpdateSql(updateSql);

		if (Log.isDebug()) {
			Logger.debug("TotalStatisticsData", updateSql + " => " + success);
		}
	}
}