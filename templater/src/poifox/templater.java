/*
 * SaveTemplate [Processing Tool].
 * Save your current sketch as a .txt template
 *
 * Copyright (C) 2011 Jonathan Acosta (aka 0p0).
 *
 * SaveTemplate is free software, you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SaveTemplate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package poifox;

import processing.app.Editor;
import processing.app.Preferences;
import processing.app.tools.Tool;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import javax.swing.JOptionPane;

/**
 * This little Processing tool lets you save a sketch you are working currently
 * on as a plain text template to be reused in later pieces/sketches.
 * SaveTemplate is the perfect companion for the InitSketch tool.
 *
 * TESTED IN PROCESSING 1.5.1
 *
 * @author Jonathan Acosta
 * @version 0.2
 * @see #getMenuTitle()
 * @see #init(Editor ed)
 * @see #run()
 *
 */
public class templater implements Tool {

	Editor editor;
	File templateRoot;
	String fs;
	String toolName = "SaveTemplate", toolVersion = "0.2";

	@Override
	public String getMenuTitle() {
		return "Save sketch as template";
	}

	@Override
	public void init(Editor editor) {
		this.editor = editor;
		// cross-platform compatible paths
		fs = System.getProperty("file.separator");
		templateRoot = new File(Preferences.get("sketchbook.path") + fs
				+ "templates");
	}

	@Override
	public void run() {
		// Create the templates folder if not found
		// No installing default template. that's for InitSketch
		if (!templateRoot.exists()) {
			templateRoot.mkdir();
			templateRoot.setExecutable(true);
			templateRoot.setReadable(true);
			templateRoot.setWritable(true);
			System.err.println("templates folder was created");
		}

		// Create and show save file dialog
		FileDialog saveTemplate = new FileDialog(editor,
				"Save Sketch as Template", FileDialog.SAVE);
		saveTemplate
				.setDirectory(templateRoot.getAbsolutePath() + "editor.txt");
		// saveTemplate.setFile("new_template.txt"); // the default file name
		saveTemplate.setVisible(true);
		String templateName = saveTemplate.getFile();

		// Exit on cancel
		if (null == templateName) {
			this.editor.statusNotice("Saving template file cancelled...");
			return;
		}

		// append .txt extension if it's not present
		if (!templateName.endsWith("txt") && !templateName.endsWith("TXT")) {
			templateName += ".txt";
		}
		System.out.println(templateName);

		// File handler
		File theTemplate = new File(templateRoot.getAbsolutePath() + fs
				+ templateName);
		int response = 0;
		if (theTemplate.exists()) {
			response = JOptionPane
					.showConfirmDialog(
							editor.getContentPane(),
							(Object) "The file " + theTemplate.getName()
									+ " already exixts in this folder, do you want to overwrite it?",
							toolName + toolVersion, JOptionPane.OK_CANCEL_OPTION);
		}

		System.out.println(response);

		if (2 != response) { // break if filename exists
			String templateContents = this.editor.getText();
			try {
				PrintWriter templateFile = new PrintWriter(theTemplate);
				templateFile.print(templateContents);
				templateFile.flush();
				templateFile.close();
				this.editor.statusNotice("Template saved as \""
						+ theTemplate.getName() + "\".");
				System.out
						.println("Success! - You can find your new template in:\n"
								+ templateRoot.getAbsolutePath());
				System.out.println(String
						.format("%s %s", toolName, toolVersion));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			this.editor.statusNotice("Saving template file cancelled...");
		}
	}
}
