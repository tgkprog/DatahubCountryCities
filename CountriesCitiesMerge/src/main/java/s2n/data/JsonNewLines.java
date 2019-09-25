package s2n.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

public class JsonNewLines {

	public static void main(String[] args) {
		//File in = new File("/d/syn/blacksmith/data/cities/9/o1.jsons");
		//File out = new File("/d/syn/blacksmith/data/cities/9/o2.jsons");
		
		File in = new File("/u/apps/datahub_io/core/world-cities/data/world-cities_json.json");
		File out = new File("/u/apps/datahub_io/core/world-cities/data/world-cities_lines.json");
		
		new JsonNewLines(in, out);
	}

	public JsonNewLines(File in, File out) {
		Reader reader = null;
		Writer writer = null;
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(in));
			reader = new InputStreamReader(bis, "UTF-8");
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(out));
			writer = new OutputStreamWriter(bos, "UTF-8");
			boolean inquotes = false;
			char chr;
			char prv = 1;
			while (reader.ready()) {
				chr = (char) reader.read();
				if (chr > 0) {
					if (chr == '\"' && prv != '\\') {
						inquotes = !inquotes;
					} else if (chr == '{' && inquotes == false) {
						writer.write('\n');
					}
					writer.write(chr);
					prv = chr;

				}
			}

		} catch (Exception e) {
			//
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception e2) {

				}
			}

		}

	}
}
