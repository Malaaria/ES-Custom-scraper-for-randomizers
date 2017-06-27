package fr.malaaria.scraper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Zelda3Scraper extends SwingWorker<Object, String>{
	String romsPath;	
	String gameListPath;
	String imagePath;
	String nameES;
	JTextArea logtext;
	int numberOfGames;

	Zelda3Scraper(JTextArea plog){
		try {
			logtext = plog;
			loadConfigFile();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	protected void doScrapeSAX(){
		SAXBuilder sxb = new SAXBuilder();
		Document gameListDocument;
		Element racine;
		File gameListFileOld = new File(this.gameListPath);		
		try {
			gameListDocument = sxb.build(gameListFileOld);
			racine = gameListDocument.getRootElement();
			File folder = new File(this.romsPath);
			String[] splitname = new String[4];
			String[][] splitoptions = new String[4][3];
			if(folder.exists()){
				if(folder.isDirectory()){
					File[] listOfFiles = folder.listFiles();
					Date dateOfFile;
					SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
					SimpleDateFormat formatHeure = new SimpleDateFormat("HHmmss");
					String mode = "";
					String logic = "";
					String goal = "";
					String difficulty = "";
					String path = "";
					String version = "";
					String seed = "";
					for(int i=0;i<listOfFiles.length;i++){
						if(listOfFiles[i].isFile()){
							splitname = listOfFiles[i].getName().split("_");						
							// On ne créé pas le jeu original
							if(splitname.length!=1){
								for(int j=0;j<splitname.length;j++){
									splitoptions[j] = splitname[j].split("-");
								}
								dateOfFile = Date.from(Instant.ofEpochMilli(listOfFiles[i].lastModified()));
								mode = splitoptions[2][1];
								logic = splitoptions[1][0] + " " + splitoptions[1][1];
								version = splitoptions[1][2];
								difficulty = splitoptions[2][0];
								goal = splitoptions[2][2];
								seed = splitname[3].substring(0, splitname[3].length()-4);
								path = "./" + listOfFiles[i].getName();
								if(!isGameInList(path)){
									this.publish("Adding " + listOfFiles[i].getName());
									Element gameElement = new Element("game");
									Element pathElement = new Element("path");
									pathElement.addContent(path);
									Element nameElement = new Element("name");
									nameElement.addContent("Seed: " + seed + ", v" + version + ", l:" + logic + ", g:" + goal + ", m:" + mode + ", d:" + difficulty);
									Element descElement = new Element("desc");
									descElement.addContent(
											"Seed: " + seed + "\n" +
													"Version: " + version + "\n" + 
													"Logic: " + logic + "\n" +
													"Goal: " + goal + "\n" +
													"Mode: " + mode + "\n" +
													"Difficulty: " + difficulty);
									Element imageElement = new Element("image");
									imageElement.addContent("~/.emulationstation/downloaded_images/" + this.nameES + "/" + copyImage(listOfFiles[i].getName()));
									Element ratingElement = new Element("rating");
									ratingElement.addContent("1.0");
									Element releaseElement = new Element("releasedate");
									releaseElement.addContent(formatDate.format(dateOfFile) + "T" + formatHeure.format(dateOfFile));
									Element developerElement = new Element("developer");
									developerElement.addContent("http://vt.alttp.run/randomizer");
									Element publisherElement = new Element("publisher");
									publisherElement.addContent("Nintendo");
									Element genreElement = new Element("genre");
									genreElement.addContent("Action");
									gameElement.addContent(pathElement);
									gameElement.addContent(nameElement);
									gameElement.addContent(descElement);
									gameElement.addContent(imageElement);
									gameElement.addContent(ratingElement);
									gameElement.addContent(releaseElement);
									gameElement.addContent(developerElement);
									gameElement.addContent(publisherElement);
									gameElement.addContent(genreElement);
									racine.addContent(gameElement);
									this.numberOfGames++;
								}
							}
						}						
					}
					XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
					FileOutputStream fosxml = new FileOutputStream(gameListPath);
					sortie.output(gameListDocument, fosxml);
					fosxml.close();
					this.publish(numberOfGames + " games were added to " + gameListPath);
					cleanGameList();
				}					
			}			
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	private boolean isGameInList(String pathOfGame){
		boolean result = false;
		SAXBuilder sxb = new SAXBuilder();
		Document gameListDocument;
		Element racine;
		File gameListFileOld = new File(gameListPath);
		try {
			gameListDocument = sxb.build(gameListFileOld);
			racine = gameListDocument.getRootElement();
			List<Element> gameListList = racine.getChildren();
			Iterator<Element> iterElement = gameListList.iterator();
			while(iterElement.hasNext()){
				Element gameElement = iterElement.next();				
				String path = gameElement.getChildText("path");
				if(pathOfGame.equals(path)){
					result = true;
				}
			}
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return result;
	}

	private String copyImage(String nameofGame) throws IOException{
		String imageOriginale = "zelda3.jpg";
		String imageOfGame = nameofGame.substring(0, nameofGame.length() - 4) + ".jpg";		
		File finalImage = new File(imagePath + imageOfGame);
		if(!finalImage.exists()){
			FileInputStream fis = new FileInputStream(imageOriginale);
			FileOutputStream fos = new FileOutputStream(imagePath + imageOfGame);
			int bytelu = 0;
			while((bytelu = fis.read()) != -1){
				fos.write(bytelu);
			}
			fis.close();
			fos.close();
		}
		return imageOfGame;
	}

	private void loadConfigFile() throws JDOMException, IOException{
		SAXBuilder sxb = new SAXBuilder();
		Document configDocument;
		Element racine;
		File configFile = new File("config.xml");		
		configDocument = sxb.build(configFile);
		racine = configDocument.getRootElement();
		List<Element> configList = racine.getChildren();
		Iterator<Element> i = configList.iterator();
		while(i.hasNext()){
			Element gameElement = i.next();
			if(gameElement.getAttributeValue("config").equals("z3rand")){	
				this.publish("Loading config z3rand");
				this.nameES = gameElement.getChildText("name");
				this.gameListPath = gameElement.getChildText("pathEmulationStation") + "/gamelists/" + this.nameES + "/gamelist.xml";
				this.imagePath = gameElement.getChildText("pathEmulationStation") + "/downloaded_images/" + this.nameES + "/";
				this.romsPath = gameElement.getChildText("pathRoms");
				this.numberOfGames = 0;
			}
		}
	}

	private void cleanGameList(){
		SAXBuilder sxb = new SAXBuilder();
		Document gameListDocument;
		Element racine;
		File gameListFile = new File(gameListPath);
		try {
			gameListDocument = sxb.build(gameListFile);
			racine = gameListDocument.getRootElement();
			List<Element> gameListList = racine.getChildren();
			Iterator<Element> iterElement = gameListList.iterator();			
			while(iterElement.hasNext()){
				Element gameElement = iterElement.next();				
				String path = gameElement.getChildText("path");
				// On enlève ./ dans path
				File gameFile = new File(this.romsPath + "/" + path.substring(2, path.length()));				
				if(!gameFile.exists()){
					this.publish("File cleaning: " + this.romsPath + "/" + path.substring(2, path.length()));
					iterElement.remove();
				}
			}			
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			FileOutputStream fosxml = new FileOutputStream(gameListPath);
			sortie.output(gameListDocument, fosxml);
			fosxml.close();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected Object doInBackground() throws Exception {
		return null;
	}

	protected void process(List<String> chunks){
		for (String s : chunks) {
			logtext.append(s + "\n");;
		}
	}
}
