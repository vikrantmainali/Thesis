import java.io.File;
import com.datastax.driver.core.Session;
import com.github.javaparser.ParseException;
import explorer.DataParser;
import repository.KeyspaceRepository;
import repository.CassandraConnection;

public class ParserMain {
	public static void main(String[] args) {
		File projectDir = new File("C:\\Users\\user\\Downloads\\cocoon-2.2.0");
		 CassandraConnection cass = new CassandraConnection();
		 cass.connect("127.0.0.1", 9042); 
		 Session session = cass.getSession();
		  
		 KeyspaceRepository sr = new KeyspaceRepository(session);
		 sr.createKeyspace("ParsedData", "SimpleStrategy", 1); 
		 sr.useKeyspace("ParsedData");
		 
		DataParser parser = new DataParser(session);

		try 
		{
			parser.extractData(projectDir);
			cass.close();
		} 
		catch (ParseException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
