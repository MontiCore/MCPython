package de.monticore.sipython;

import de.monticore.sipython.generator.Generator;
import de.monticore.siunits.SIUnitsMill;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Path;

public class Main {

	private static String executeScript = null;

	public static void main(String[] args) {
		init();

		Path input = null;
		Path output = null;

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.startsWith("--")) {
				String setting = arg.replaceFirst("--", "");
				if (setting.equalsIgnoreCase("execute")) {
					if (i + 1 >= args.length) {
						Log.error("Missing argument at --execute");
						return;
					}

					executeScript = args[++i];
				}
			} else {
				final Path argPath = Path.of(arg);
				if (input == null) {
					input = argPath;
				} else if (output == null) {
					output = argPath;
				} else {
					Log.error("Unable to read " + arg);
					return;
				}
			}
		}

		if (input == null) {
			Log.error("Input file or directory missing");
			return;
		}

		if (output == null) {
			Log.error("Output file or directory missing");
			return;
		}

		if (output.toFile().isFile()) {
			Log.error("Output path is a file not a directory");
			return;
		}

		String[] fileNames;

		if (input.toFile().isFile()) {
			fileNames = new String[]{input.toFile().getName()};
			input = input.getParent();
		} else {
			fileNames = input.toFile().list((dir, name) -> name.endsWith(".sipy"));
		}

		if (fileNames == null) {
			Log.error("No files found");
			return;
		}

		if (input.toFile().isDirectory()) {
			Generator.generate(input.toAbsolutePath().toString(), output.toAbsolutePath().toString(), fileNames);
		}

		if (executeScript != null) {
			if (!output.resolve(executeScript).toFile().exists()) {
				Log.error("Unable to execute " + executeScript + ", file not found");
				return;
			}

			Log.println("Executing " + executeScript + ":\n");

			Process process;
			try {
				process = Runtime.getRuntime().exec("python3 " + output.toAbsolutePath() + File.separator + executeScript);
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

				String s;

				while ((s = stdInput.readLine()) != null) {
					Log.println(s);
				}

				while ((s = stdError.readLine()) != null) {
					Log.println(s);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

	}

	private static void init() {
		Log.init();
		Log.enableFailQuick(false);
		SIPythonMill.reset();
		SIPythonMill.init();
		SIUnitsMill.initializeSIUnits();
	}
}
