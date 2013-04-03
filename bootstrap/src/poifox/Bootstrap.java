/**
 * Bootstrap. Quickly initialize a Processing sketch with a template.
 *
 * Copyright 2013, Jonathan Acosta. All rights reserved.
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
 * @author		JOnathan Acosta
 * @modified	April 2nd 2013
 * @version		1.0
 */

package poifox;

// processing imports
import processing.app.*;
import processing.app.tools.*;

// java imports
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
//import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import java.util.ArrayList;

/**
 * Bootstrap for Processing 2.0. Succeeds InitSketch 0.9
 * 
 * @author poifox
 * 
 */
public class Bootstrap implements Tool {

	private Editor editor;
	private String fs;
	private String defTemplate;
	private String templatesPath;
	private File skbkPath;
	private File templatesFolder;
	private File dataFolder;
	private ArrayList<File> allTemplates;

	public String getMenuTitle() {
		return "Bootstrap";
	}

	public void init(Editor theEditor) {
		editor = theEditor;
		fs = System.getProperty("file.separator");
		defTemplate = "default.txt";
		initTemplatesRoot();
		allTemplates = new ArrayList<File>();
	}

	public void run() {
		loadTemplates();
		if ( allTemplates.size() > 0 ) {
			if ( allTemplates.size() > 1 ) {
				offerMenu();
			} else {
				initialize(allTemplates.get(0) );
			}
		} else {
			JOptionPane
			.showMessageDialog(
					editor.getContentPane(),
					(Object) "Woops!"
							+ "\nLooks like you don't have any templates!\n"
							+ "Please put some .txt templates in your sketchbook:\n"
							+ skbkPath
							+ templatesPath,
					getMenuTitle(), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void offerMenu() {
		Object[] templateNames = new Object[ allTemplates.size() ];
		for ( int i = 0 ; i < allTemplates.size(); i++ ) {
			templateNames[i] = allTemplates.get( i ).getName();
		}
		Object selTemplate;
		selTemplate = JOptionPane.showInputDialog(
				editor.getContentPane(),
				(Object) "Select the template to use:",
				getMenuTitle(),
				JOptionPane.OK_CANCEL_OPTION,
				null,
				templateNames,
				templateNames[0]);
		System.err.println(selTemplate);
		if ( null == selTemplate ) {
			System.out.println("Canceled bootstrap action...");
			return;
		}
		for ( int j = 0 ; j < allTemplates.size() ; j++ ) {
			if ( allTemplates.get(j).getAbsolutePath().endsWith(selTemplate.toString()) ) {
				initialize( allTemplates.get(j) );
				return;
			}
		}
		JOptionPane
		.showMessageDialog(
				editor.getContentPane(),
				(Object) "Woops! Something went wrong while loading this template!\n",
				getMenuTitle(), JOptionPane.ERROR_MESSAGE);
	}
	
	private void initialize( File templateFile ) {
		String templateContent;
		templateContent = loadString( templateFile.getAbsolutePath() );
		
		editor.insertText( templateContent );
		editor.statusNotice("Sketch initialized with \""+ templateFile.getName() + "\". To work!" );
		if ( 1 == allTemplates.size() ) {
			System.out.println("You can have as many templates as you like in " + skbkPath );
		}
		allTemplates.clear();
	}
	
	private void loadTemplates() {
		String filePath;
		File[] templates = templatesFolder.listFiles();
		for ( int i = 0; i < templates.length; i ++ ) {
			filePath = templates[i].getAbsolutePath();
			if ( filePath.endsWith(".txt") || filePath.endsWith(".TXT") ) {
				allTemplates.add( new File(filePath) );
			}
		}
	}

	/**
	 * Figures out where the templates root is and stores it in templatesPath
	 * for later usage
	 */
	private void initTemplatesRoot() {
		skbkPath = new File(Preferences.get("sketchbook.path"));
		dataFolder = new File( skbkPath + fs + "tools" + fs + "Bootstrap" + fs + "data" );
		templatesPath = skbkPath.getAbsolutePath() + fs + "templates";
		templatesFolder = new File(templatesPath);
		if ( ! templatesFolder.exists()) {
			createTemplatesFolder();
		}
	}

	private void createTemplatesFolder() {
		JOptionPane
				.showMessageDialog(
						editor.getContentPane(),
						(Object) "The templates folder was not found"
								+ "\na new templates folder will be created for you in:\n"
								+ templatesPath,
						getMenuTitle(), JOptionPane.ERROR_MESSAGE);
		if (templatesFolder.mkdir()) {
			boolean executable = templatesFolder.setExecutable(true);
			boolean readable = templatesFolder.setReadable(true);
			boolean writable = templatesFolder.setWritable(true);
			if ( executable && readable && writable ) {
				System.out.println("The templates folder was created successfully in your sketchbook");
				System.out.println("You can add .txt files inside to grow your templates collection");
			}
			// TODO when question is answered turn this on
//			try {
//				installDefaultTemplate();
//				editor.statusNotice("Installed default.txt");
//			} catch (Exception e) {
//				System.err.println("Error while installing default.txt template!");
//			}
		} else {
			JOptionPane
					.showMessageDialog(
							editor,
							(Object) "Dang! An error prevented the templates folder to be created.\n"
									+ "This is probably caused by wrong permissions"
									+ "set for your sketchbook folder; I'm sorry!",
							"Error!", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void installDefaultTemplate() throws IOException {
		String dataTemplate = templatesFolder.getAbsolutePath() + fs + defTemplate;
		File installTemplate = new File(dataTemplate);
		if ( ! installTemplate.exists() ) {
			File defaultTemplate = new File( dataFolder.getAbsolutePath() + fs + defTemplate );
			System.out.println(defaultTemplate.getAbsolutePath());
			InputStream inStream = new FileInputStream( defaultTemplate.getAbsolutePath() );
			OutputStream outStream = new FileOutputStream( installTemplate.getAbsolutePath() );
			byte[] outBytes = new byte[1024];
			int nLength;
			BufferedInputStream buffer = new BufferedInputStream( inStream );
			while ( ( nLength = buffer.read(outBytes) ) > 0 ) {
				outStream.write( outBytes, 0, nLength );
			}
			inStream.close();
			outStream.close();
		}
	}

	/**
	 * load a text file from the data folder or an absolute path.
	 * 
	 * @param theFilename
	 * @return
	 */
	public String loadString(String theFilename) {
		InputStream is = null;
		if (theFilename.startsWith(File.separator)) {
			try {
				is = new FileInputStream(loadFile(theFilename));
			} catch (FileNotFoundException e) {
				System.err.println("ERROR @ loadString() " + e);
			}
		} else {
			is = getClass().getResourceAsStream(getPath(theFilename));
		}
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		int buffer;
		String result = "";
		try {
			while ((buffer = br.read()) != -1) {
				result += (char) buffer;
			}
		} catch (Exception e) {
			System.err.println("ERROR @ loadString() " + e);
		}
		return result;
	}

	/**
	 * getPath will return the path to a file or folder inside the data folder
	 * of the tool or an absolute path.
	 * 
	 * @param theFilename
	 * @return
	 */
	public String getPath(String theFilename) {
		if (theFilename.startsWith("/")) {
			return theFilename;
		}
		return File.separator + "data" + File.separator + theFilename;
	}

	/**
	 * load a file from the data folder or an absolute path.
	 * 
	 * @param theFilename
	 * @return
	 */
	public File loadFile(String theFilename) {
		if (theFilename.startsWith(File.separator)) {
			return new File(theFilename);
		}
		String path = getClass().getResource(getPath(theFilename)).getPath();
		return new File(path);
	}

}
