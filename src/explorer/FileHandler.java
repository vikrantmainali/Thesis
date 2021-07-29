package explorer;

import java.io.File;

public interface FileHandler 
{
	void handle(int level, String path, File file);
}
