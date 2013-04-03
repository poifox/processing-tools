/**
 * you can put a one sentence description of your tool here.
 *
 * ##copyright##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author		##author##
 * @modified	##date##
 * @version		##version##
 */

package poifox;

import processing.app.*;
import processing.app.tools.*;

import java.awt.FileDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.swing.JOptionPane;

public class Templater implements Tool {

	// when creating a tool, the name of the main class which implements Tool
	// must be the same as the value defined for project.name in your
	// build.properties

	Editor editor;
	File templatesFolder;
	String fs;
	String toolName = "SaveTemplate", toolVersion = "0.2";

	/**
	 * Returns the menu item string
	 * required by the Tool interface
	 */
	public String getMenuTitle() {
		return "Templater: save as template";
	}

	/**
	 * Inits the tool
	 * required by the Tool interface
	 */
	public void init(Editor theEditor) {
		editor = theEditor;
		// cross-platform compatible paths
		fs = System.getProperty("file.separator");
		templatesFolder = new File( Preferences.get("sketchbook.path") + fs + "templates" );
	}

	/**
	 * Does all the business logic
	 * required by the Tool interface
	 */
	public void run() {
		createTemplatesFolder();
		saveDialog();
	}
	
	/**
	 * Initializes templates folder if not present
	 */
	private void createTemplatesFolder() {
		// Create the templates folder if not found
		// No installing default template. that's for InitSketch
		if ( ! templatesFolder.exists() ) {
			templatesFolder.mkdir();
			templatesFolder.setExecutable(true);
			templatesFolder.setReadable(true);
			templatesFolder.setWritable(true);
			System.err.println("templates folder was created");
		}
	}
	
	/**
	 * Presents the save dialog to the user, set to be browsing already 
	 * on the user's sketchbook path.
	 */
	private void saveDialog() {
		// Create saving dialog, set save location and show the dialog
		FileDialog saveDialog = new FileDialog( editor, "Save Sketch as Template", FileDialog.SAVE );
		saveDialog.setDirectory( templatesFolder.getAbsolutePath() );
		saveDialog.setVisible(true);
		// get the file name entered
		String templateName = saveDialog.getFile();
		// Exit on cancel or no file given
		if ( null == templateName ) {
			editor.statusNotice("Abort: cancelled or no name was provided");
			return;
		}
		// append .txt extension if it's not present
		if (!templateName.endsWith("txt") && !templateName.endsWith("TXT")) {
			templateName += ".txt";
		}
		saveTemplate( templateName );
	}
	
	/**
	 * Saves the template do disk.
	 * But first check to see if the template exists and prompt fo rewrite confirmation
	 * Also let the user know of errors
	 * @param templateName
	 */
	private void saveTemplate( String templateName ) {
		// check to see if the template already exists
		File templateToSave = new File( templatesFolder.getAbsolutePath() + fs + templateName);
		int confirmCode = 0;
		// warning if
		if ( templateToSave.exists() ) {
			confirmCode = JOptionPane.showConfirmDialog(
				editor.getContentPane(),
				(Object) "The file " + templateToSave.getName() + " already exixts in this folder, do you want to overwrite it?",
				"Templater",
				JOptionPane.INFORMATION_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION
			);
		}
		// 0 means OK
		if ( 0 == confirmCode ) {
			String templateContents = editor.getText();
			try {
				PrintWriter templateFile = new PrintWriter( templateToSave );
				templateFile.print( templateContents );
				templateFile.flush();
				templateFile.close();
				editor.statusNotice("Template saved as \"" + templateToSave.getName() + "\".");
				System.out.println("You can use your template with Bootstrap now :)");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else if ( -1 == confirmCode ) {
			editor.statusError("Dialog force-closed, aborted operation.");
		} else {
			editor.statusNotice("User canceled saving template.");
		}
	}

}
