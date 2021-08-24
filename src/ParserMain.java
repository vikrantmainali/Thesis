import java.io.File;

import explorer.Parser;
import explorer.StatisticsExtractor;
import com.github.javaparser.ParseException;

public class ParserMain 
{
	public static void main(String[] args) throws ParseException 
	{
		/*
		 * Location of the source code for each project
		 */
		File projectDir = new File("C:\\Users\\user\\Desktop\\Uni\\2DV50E_Thesis\\Data\\zookeeper");
		/*
		 * Parser class used to parse the data from each project using the extractData method which takes the project directory as the argument
		 */
		Parser parser = new Parser();
		parser.extractData(projectDir);
		/*
		 * StatisticsExtractor class used to get statistics of a project such as number of production classes and methods 
		 * and number of test classes and methods
		 */
	//	StatisticsExtractor ext = new StatisticsExtractor();
	//	ext.extractTotalStatistics(projectDir); // This method is for total statistics i.e., number of all classes and methods in a project
	//	ext.extractTestStatistics(projectDir); // This method is for test statistics i.e., number of all test classes and methods in a project
	}
}
