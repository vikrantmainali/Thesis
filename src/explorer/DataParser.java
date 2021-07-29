package explorer;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.github.javaparser.ParseException;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.google.common.base.Strings;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class DataParser 
{	
	private UUID id;
	private String className;
	private String methodName;
	private static String TABLE_NAME;    
	private static Session session;

	public DataParser(Session session) 
	{
		DataParser.session = session;
	}
	    
	public DataParser() 
	{

	}
	
    public void extractData(File projectDir) throws ParseException 
    {
    	
    	TABLE_NAME = projectDir.getName().replaceAll("[^A-Za-z]", "");
    	
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> 
        {
        	System.out.println(path);
        	System.out.println(Strings.repeat("-", path.length()));
        	System.out.println(TABLE_NAME);
        	System.out.println(Strings.repeat("*", TABLE_NAME.length()));
            try 
            {	
                new VoidVisitorAdapter<Object>() 
                {
                    @Override
                    public void visit(ClassOrInterfaceDeclaration n, Object arg)
                    {
                       super.visit(n, arg);
                       System.out.println(n.getNameAsString());
                       System.out.println(Strings.repeat("=", n.getNameAsString().length()));
               		   createTable();

                       for(MethodDeclaration m : n.getMethods())
                       {
                    	  id = UUIDs.random();
                    	  className = n.getNameAsString();
                    	  methodName = m.getNameAsString(); 
                
                    	  System.out.println(id + " | " + file.getName() + " | " + className + " | " + methodName);
                    	  insertData(id, file.getName(), className.toString(), methodName.toString());
                       }
                    }       
                }
                .visit(StaticJavaParser.parse(file), null);

                System.out.println();
            } 
            catch (IOException e) 
            { 
                new RuntimeException(e);
            }
        }).
        explore(projectDir);
    }
    
    private static void createTable() 
    {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
        		.append(TABLE_NAME)
        		.append(" (")
        		.append("id UUID PRIMARY KEY, ")
        		.append("file text, ")
        		.append("class text, ")
        		.append("method text);");

        final String query = sb.toString();
        System.out.println(query);
        session.execute(query);
        System.out.println("---Table created---");
    }
    
    private static void insertData(UUID id, String file, String className, String methodName) 
    {
        StringBuilder sb = new StringBuilder("INSERT INTO ")
        		.append(TABLE_NAME).append(" (id, file, class, method) ")
        		.append("VALUES (")
        		.append(id)
        		.append(", '")
        		.append(file)
        		.append("', '")
        		.append(className)
        		.append("', '")
        		.append(methodName)
        		.append("');");
        final String query = sb.toString();
        System.out.println(query);
        session.execute(query);
        System.out.println("---Data inserted---");
    }
}