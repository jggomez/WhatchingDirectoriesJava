package co.dane.test.watching;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Properties;

import static java.nio.file.StandardWatchEventKinds.*;

public class TestWatching {

	public static final String properties = "config.properties";
	public static final String rootDirectory = "directorio";

	public static void main(String args[]) {

		TestWatching obj = new TestWatching();
		obj.init();
	}

	public void init() {

		Properties prop = new Properties();
		InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream(properties);

		File file = null;

		try {
			if (inputStream != null) {

				prop.load(inputStream);

				file = new File(prop.getProperty(rootDirectory));

			} else {
				throw new FileNotFoundException(
						"Archivo de properties no encontrado");
			}

			WatchService objWatchService = FileSystems.getDefault()
					.newWatchService();

			recursiveDirs(file, objWatchService);

			watchingDirectory(objWatchService);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void recursiveDirs(File file, WatchService objWatchService) {

		if (file.isDirectory()) {

			String[] directories = file.list();
			Path path = null;

			if (directories != null && directories.length > 0) {

				for (String dir : directories) {

					try {

						path = Paths.get(file.getAbsolutePath() + "\\" + dir);
						path.register(objWatchService, ENTRY_CREATE);

						recursiveDirs(new File(file.getAbsolutePath() + "\\"
								+ dir), objWatchService);

					} catch (IOException e) {
						e.printStackTrace();
					}

				}

			}

		}

	}

	private void watchingDirectory(WatchService inObjWatchService) {

		for (;;) {

			WatchKey objWatchKey = null;

			try {
				objWatchKey = inObjWatchService.take();
			} catch (InterruptedException e) {
				return;
			}

			for (WatchEvent<?> event : objWatchKey.pollEvents()) {

				WatchEvent.Kind<?> kind = event.kind();

				if (kind == ENTRY_CREATE) {

					Path dir = (Path) objWatchKey.watchable();

					@SuppressWarnings("unchecked")
					WatchEvent<Path> ev = (WatchEvent<Path>) event;
					Path fileName = ev.context();

					System.out
							.format("Se detecta un nuevo archivo en %s, con el nombre %s%n",
									dir.getFileName(), fileName.getFileName());
				}

			}

			boolean valid = objWatchKey.reset();
			if (!valid) {
				break;
			}

		}

	}

}
