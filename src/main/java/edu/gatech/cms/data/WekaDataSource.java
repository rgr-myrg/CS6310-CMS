package edu.gatech.cms.data;

import java.io.IOException;

import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;
import edu.gatech.cms.sql.RecordsTable;
import edu.gatech.cms.sql.RequestsTable;
import edu.gatech.cms.util.DbHelper;
import edu.gatech.cms.util.FileUtil;
import weka.associations.Apriori;
import weka.core.Instances;
import weka.experiment.InstanceQuery;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.NumericToNominal;

public class WekaDataSource {
	public static final String TAG = WekaDataSource.class.getSimpleName();
	public static final String FILE_NAME = "DatabaseUtils.props";

	// Settings from https://weka.wikispaces.com/weka_experiment_DatabaseUtils.props
	public static final String SETTINGS = String.format(
			"jdbcDriver=%s%s" + 
			"jdbcURL=%s%s" + 
			"TEXT=0%s" +  
			"INTEGER=5%s", 
			DbHelper.SQLITE_JDBC_DRIVER, 
			System.lineSeparator(),
			DbHelper.DB_CONNECTION_URL, 
			System.lineSeparator(),
			System.lineSeparator(),
			System.lineSeparator());

	public WekaDataSource() {
		try {
			FileUtil.writeToFile(SETTINGS, FILE_NAME);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Apriori getAprioriAssociationsWithSql(final String sql) {
		final Apriori apriori = new Apriori();

		try {
			final InstanceQuery query = new InstanceQuery();

			query.setUsername(DbHelper.DB_USER_NAME);
			query.setPassword(DbHelper.DB_PASSWORD);
			query.setQuery(sql);

			Instances dataSet = query.retrieveInstances();
			dataSet.setClassIndex(dataSet.numAttributes() - 1);

			final NumericToNominal filter = new NumericToNominal();

			filter.setInputFormat(dataSet);
			dataSet = Filter.useFilter(dataSet, filter);

			apriori.setClassIndex(dataSet.classIndex());
			apriori.buildAssociations(dataSet);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return apriori;
	}

	public Apriori analyzeCourseRequests() {
		if (Log.isDebug()) {
			Logger.debug(TAG, "Analyzing Requests Data");
		}

		return getAprioriAssociationsWithSql(RequestsTable.SELECT_REQUESTS);
	}

	public Apriori analyzeStudentRecords() {
		if (Log.isDebug()) {
			Logger.debug(TAG, "Analyzing Records Data");
		}

		return getAprioriAssociationsWithSql(RecordsTable.SELECT_RECORDS);
	}
}
