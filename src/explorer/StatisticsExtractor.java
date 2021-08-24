package explorer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.github.javaparser.ParseException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Strings;
import com.opencsv.CSVWriter;

public class StatisticsExtractor 
{
	private String className;
	private String methodName;
	private CSVWriter csvWriter;
	private static String CSV_FILE;
	private String numberOfClasses;
	private String numberOfMethods;
	private String numberOfTestClasses;
	private String numberOfTestMethods;
	
	public StatisticsExtractor() 
	{

	}

	public void extractTotalStatistics(File projectDir) throws ParseException 
	{
		CSV_FILE = projectDir.getName().replaceAll("[^A-Za-z]", "");
		System.out.println(CSV_FILE);
		System.out.println(Strings.repeat("*", CSV_FILE.length()));
		setUpCSV();
		List<String> classNames = new ArrayList<String>();
		List<String> methodNames = new ArrayList<String>();
		new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> 
		{
			try 
			{
				new VoidVisitorAdapter<Object>() 
				{
					@Override
					public void visit(ClassOrInterfaceDeclaration n, Object arg) 
					{
						super.visit(n, arg);
						className = n.getNameAsString();
						classNames.add(className);
						numberOfClasses = String.valueOf(classNames.size());
						for (MethodDeclaration m : n.getMethods()) 
						{
							methodName = m.getNameAsString();
							methodNames.add(methodName);
						}
						numberOfMethods = String.valueOf(methodNames.size());
					}
				}.visit(StaticJavaParser.parse(file), null);
			} 
			catch (IOException e) 
			{
				new RuntimeException(e);
			}
		}).explore(projectDir);
		try 
		{
			System.out.println("Number of classes: " + numberOfClasses + ", Number of methods: " + numberOfMethods);
			writeToCSV(CSV_FILE, numberOfClasses, numberOfMethods);
			csvWriter.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public void extractTestStatistics(File projectDir) throws ParseException 
	{
		CSV_FILE = projectDir.getName().replaceAll("[^A-Za-z]", "");
		System.out.println(CSV_FILE);
		System.out.println(Strings.repeat("*", CSV_FILE.length()));
		setUpTestCSV();
		List<String> classNames = new ArrayList<String>();
		List<String> methodNames = new ArrayList<String>();
		new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> 
		{
			try 
			{
				new VoidVisitorAdapter<Object>() 
				{
					@Override
					public void visit(ClassOrInterfaceDeclaration n, Object arg) 
					{
						boolean isTestClass = false;
						if (n.getExtendedTypes().isNonEmpty()) 
						{
							for (ClassOrInterfaceType a : n.getExtendedTypes()) 
							{
								if (a.getNameAsString().toLowerCase().contains("test")) 
								{
									isTestClass = true;
								}
							}
						}
						for (ImportDeclaration p : n.findCompilationUnit().get().getImports()) 
						{
							if (p.getNameAsString().toLowerCase().contains("junit")) 
							{
								isTestClass = true;
							}
						}
						super.visit(n, arg);
						className = n.getNameAsString();
						if(isTestClass)
						{
							classNames.add(className);
						}
						numberOfTestClasses = String.valueOf(classNames.size());
						for (MethodDeclaration m : n.getMethods()) 
						{
							methodName = m.getNameAsString();
							if(isTestClass)
							{
								methodNames.add(methodName);
							}
						}
						numberOfTestMethods = String.valueOf(methodNames.size());
					}
				}.visit(StaticJavaParser.parse(file), null);
			} 
			catch (IOException e) 
			{
				new RuntimeException(e);
			}
		}).explore(projectDir);
		try {
			System.out.println("Number of test classes: " + numberOfTestClasses + ", Number of test methods: " + numberOfTestMethods);
			writeToTestCSV(CSV_FILE, numberOfTestClasses, numberOfTestMethods);
			csvWriter.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private void setUpCSV() 
	{
		try 
		{
			csvWriter = new CSVWriter(new FileWriter(
					"C:\\Users\\user\\Desktop\\Uni\\2DV50E_Thesis\\CSVData\\Statistics\\ProjectStats.csv", true));
			String[] entries = { "project", "numberOfClasses", "numberOfMethods" };
			csvWriter.writeNext(entries);
			csvWriter.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private void writeToCSV(String fileName, String numberOfClasses, String numberOfMethods) 
	{
		String[] entries = { fileName, numberOfClasses, numberOfMethods };
		csvWriter.writeNext(entries);
		try 
		{
			csvWriter.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	private void setUpTestCSV() 
	{
		try 
		{
			csvWriter = new CSVWriter(new FileWriter(
					"C:\\Users\\user\\Desktop\\Uni\\2DV50E_Thesis\\CSVData\\Statistics\\ProjectTestStats.csv", true));
			String[] entries = { "project", "numberOfTestClasses", "numberOfTestMethods" };
			csvWriter.writeNext(entries);
			csvWriter.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private void writeToTestCSV(String fileName, String numberOfTestClasses, String numberOfTestMethods) 
	{
		String[] entries = { fileName, numberOfTestClasses, numberOfTestMethods };
		csvWriter.writeNext(entries);
		try 
		{
			csvWriter.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}