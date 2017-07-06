package fr.malaaria.PDFDesigner.utils;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class PDFDesignerBasicDrawing {
	
	PDPageContentStream pdcs;
	
	public PDFDesignerBasicDrawing(PDPageContentStream pcontent) {
		if(pdcs == null){
			pdcs = pcontent;
		}
	}
	
	protected void setContentStream(PDPageContentStream pcontent) throws IOException{
		this.pdcs.close();
		this.pdcs = pcontent;
	}
	
	protected PDPageContentStream getContentStream(){
		return this.pdcs;
	}
	
	protected void drawLine(float xorig, float yorig, float xdest, float ydest) throws IOException{
		pdcs.moveTo(xorig, yorig);
		pdcs.lineTo(xdest, ydest);
		pdcs.stroke();
	}
	
	protected void setPageBackground(String bgFilePath, PDDocument doc) throws IOException{
		PDImageXObject pdImage = PDImageXObject.createFromFile(bgFilePath, doc);        
        
        // draw the image at full size at (x=20, y=20)
        pdcs.drawImage(pdImage, 20, 20);
	}
	
	
	
}
