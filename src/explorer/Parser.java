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

public class Parser 
{
	private String className;
	private String methodName;
	private String methodBody;
	private CSVWriter csvWriter;
	private static String CSV_FILE;
	
	public Parser() 
	{

	}

	public void extractData(File projectDir) throws ParseException 
	{
		CSV_FILE = projectDir.getName().replaceAll("[^A-Za-z]", "");
		System.out.println(CSV_FILE);
		System.out.println(Strings.repeat("*", CSV_FILE.length()));
		setUpCSV();
		new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> 
		{
			System.out.println(path);
			System.out.println(Strings.repeat("-", path.length()));
			try 
			{
				new VoidVisitorAdapter<Object>() 
				{
					@Override
					public void visit(ClassOrInterfaceDeclaration n, Object arg) 
					{
						n.removeComment();
						n.removeJavaDocComment();
						boolean isTestClass = false;
						if (n.getExtendedTypes().isNonEmpty()) 
						{
							System.out.println(n.getExtendedTypes().getParentNode());
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
						System.out.println(n.getNameAsString());
						System.out.println(Strings.repeat("=", n.getNameAsString().length()));
						className = n.getNameAsString();
						List<String> classNames = new ArrayList<String>();
						classNames.add(className);
						if (n.getMethods().isEmpty()) 
						{
							System.out.println(
									file.getName() + " | " + classNames.toString().replaceAll("(^\\[|\\]$)", "") + " | "
											+ "" + " | " + "" + " | " + isTestClass);
							writeToCSV(file.getName(), classNames.toString().replaceAll("(^\\[|\\]$)", ""), "", "",
									String.valueOf(isTestClass));
						}
						for (MethodDeclaration m : n.getMethods()) 
						{
							methodName = m.getNameAsString();
							m.removeJavaDocComment();
							m.removeComment();
							methodBody = m.toString();
							System.out.println(
									file.getName() + " | " + classNames.toString().replaceAll("(^\\[|\\]$)", "") + " | "
											+ methodName + " | " + methodBody + " | " + isTestClass);
							writeToCSV(file.getName(), classNames.toString().replaceAll("(^\\[|\\]$)", ""), methodName,
									methodBody, String.valueOf(isTestClass));
						}
					}
				}.visit(StaticJavaParser.parse(file), null);
				System.out.println();
			} 
			catch (IOException e) 
			{
				new RuntimeException(e);
			}
		}).explore(projectDir);
		try 
		{
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
					"C:\\Users\\user\\Desktop\\Uni\\2DV50E_Thesis\\CSVData\\" + CSV_FILE + ".csv", true));
			String[] entries = { "file", "class", "method", "methodBody", "isTestClass" };
			csvWriter.writeNext(entries);
			csvWriter.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	private void writeToCSV(String fileName, String className, String methodName, String methodBody,
			String isTestClass) 
	{
		String[] entries = { fileName, className, methodName, methodBody, isTestClass };
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
