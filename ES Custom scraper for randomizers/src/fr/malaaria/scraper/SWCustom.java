package fr.malaaria.scraper;

import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

public class SWCustom extends SwingWorker<Object, String>{
	
	private JTextArea logtext;
	
	SWCustom(JTextArea pta){		
		logtext = pta;
	}
	
	@Override
	protected Object doInBackground() throws Exception {
		logtext.setText("");
		this.publish("Beginning...");
		Zelda1Scraper z1s = new Zelda1Scraper(logtext);
		z1s.doScrapeSAX();
		Zelda2Scraper z2s = new Zelda2Scraper(logtext);
		z2s.doScrapeSAX();
		Zelda3Scraper z3s = new Zelda3Scraper(logtext);
		z3s.doScrapeSAX();
		SMItemScraper smis = new SMItemScraper(logtext);
		smis.doScrapeSAX();
		this.publish("Done.");
		return null;
	}
	
	 @Override
	    public void process(List<String> chunks) {
	        for (String s : chunks) {
	            logtext.append(s + "\n");;
	        }
	    }
	 
	 public void publishData(String text){
		 this.publish(text);
	 }
	
}
