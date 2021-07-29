package explorer;

import java.io.File;

public interface Filter 
{
	boolean interested(int level, String path, File file);
}