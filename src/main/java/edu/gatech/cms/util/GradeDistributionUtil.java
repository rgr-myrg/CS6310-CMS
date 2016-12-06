package edu.gatech.cms.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * The class creates random vars in a given distribution. 
 * The distribution is 
 */
public class GradeDistributionUtil {
    
    // Use this class to describe in which intervals should the grades be. 
    // For example, an A-30%, B-40%, C-10%, D-10%, F-10% would become
    // A in [0,0.3], B in [0.3,0,7], C in [0.7,0.8], D in [0.8,0.9], F in [0.9,1]
    // This way we can random pick a number in [0,1] interval (Math.random()) and
    // see which interval falls in. That will be the letter grade. 
    private static class GradeInterval {
        public Double start;
        public Double end;
    }
    
    // actual distribution
    private static Map<String, GradeInterval> distribution = new HashMap<>();
    
    // statically initialize the distribution
    static {
        
        GradeInterval gi = new GradeInterval();
        gi.start = (double) 0;
        gi.end = 0.3;
        distribution.put("A", gi);

        gi = new GradeInterval();
        gi.start = 0.3;
        gi.end = 0.7;
        distribution.put("B", gi);

        gi = new GradeInterval();
        gi.start = 0.7;
        gi.end = 0.8;
        distribution.put("C", gi);

        gi = new GradeInterval();
        gi.start = 0.8;
        gi.end = 0.9;
        distribution.put("D", gi);

        gi = new GradeInterval();
        gi.start = 0.9;
        gi.end = (double) 1;
        distribution.put("F", gi);
    }

    // just for a simulation, checked with 10000, calculated the frequencies, got the right distribution
    public static void main(String[] args) {
        
        Path path = Paths.get("grades.csv");
        try (BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.CREATE)) {
        
        for (int i=0; i<10000; i++)
            bw.write(createRandomGrade() + "\n");
        }
        catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /** 
     * Create a value based on the given distribution. 
     */
    public static String createRandomGrade() {
        double rand = Math.random();
        // check where it falls on the distribution
        for (String letter: distribution.keySet()) {
            if (rand >= distribution.get(letter).start && rand <= distribution.get(letter).end)
                // yes, I know, includes both ends, will "sin" by picking the lower interval
                // in case the rand falls exactly on the upper boundary of that lower interval
                return letter;
        }
        
        // if it's not found, then something is really wrong with the distribution definition
        return "X";
    }

    
}
