package edu.gatech.cms.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

import edu.gatech.cms.InputFileHandler;
import edu.gatech.cms.course.Course;
import edu.gatech.cms.course.Record;
import edu.gatech.cms.logger.Log;
import edu.gatech.cms.logger.Logger;
import edu.gatech.cms.sql.RequestsTable;
import edu.gatech.cms.university.Student;
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

//		return getAprioriAssociationsWithSql(RecordsTable.SELECT_RECORDS);
		
		// Process records, create the right matrix for associations analysis.
		// The matrix has only courses on each line, for each student. If we 
		// consider each student's list of courses as "basket", we can find the 
		// associations between courses. Based on the associations, we can pick
		// instructors such that the courses with the stronger "rules" are being
		// covered. (or something like that)

		// 1. pick a file name
		String arffName = System.currentTimeMillis() + ".arff";
		Path path = Paths.get(arffName);
		System.out.println(path.getFileName());
		
		// 2. open file for writing
		try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
		    
		    // ARFF files have 3 sections: 
		    
		    // @relation
		    bw.write("@relation records\n\n");
		    
		    // @attributes
		    // for the attributes we pick ONLY the courses which are mentioned in records,
		    // otherwise we pollute the rules (too many "none")
		    HashMap<Integer,Course> recordCourses = new HashMap<>();
		    for (Record record: InputFileHandler.getRecords()) {
		        recordCourses.put(record.getCourse().getID(), record.getCourse());
		    }
		    for (Course course: recordCourses.values()) {
		        //bw.write("@attribute course" + course.getID() + " { T}\n");
                bw.write("@attribute course" + course.getID() + " {taken,none}\n");
		    }
		    bw.write("\n");
		    
		    // @data
            bw.write("@data\n");
		    for (Student student: InputFileHandler.getStudents().values()) {
		        // if no records, skip
		        if (student.getRecordHistory().isEmpty()) continue;
		        
		        // index in courses
		        int i = 0;
		        int numCourses = recordCourses.size();
		        
                // take the list of records and figure out which course it was
		        for (Course course: recordCourses.values()) {
		            
		            boolean found = false;
		            
		            for (Record record: student.getRecordHistory()) {
		                String grade = record.getGradeEarned();
		                if (record.getCourse().getID() == course.getID() && 
		                   ("A".equals(grade) || "B".equals(grade) || "C".equals(grade) || "D".equals(grade))) { 
		                    found = true;
		                    break;
		                }
		            }
		            
		            // actually write in the file
		            if (found) {
                        //bw.write("T");
                        bw.write("taken");
                        if (i == numCourses - 1) bw.write("\n");
                        else bw.write(",");
		            }
		            else {
                        //bw.write("?");
                        bw.write("none");
                        if (i == numCourses - 1) bw.write("\n");
                        else bw.write(",");
		            }
	                
	                i++;
	            }
		    }
		    
		} catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

		
		// 3. run analysis, if we're here we have a file

        try(BufferedReader reader = new BufferedReader(new FileReader(arffName))) {
            Instances data = new Instances(reader);
            data.setClassIndex(data.numAttributes() - 1);
            
            Apriori apriori = new Apriori();
//            apriori.setDelta(0.05);
//            apriori.setLowerBoundMinSupport(0.1);
//            apriori.setNumRules(120);
//            apriori.setUpperBoundMinSupport(1.0);
//            apriori.setMinMetric(0.5);
            
            apriori.buildAssociations(data);
            return apriori;
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        return null;
	}
}
