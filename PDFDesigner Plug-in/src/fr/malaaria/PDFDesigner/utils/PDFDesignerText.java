package fr.malaaria.PDFDesigner.utils;

import java.io.IOException;

import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.util.Matrix;

public class PDFDesignerText {

	PDPageContentStream pdcs;
	
	public PDFDesignerText(PDPageContentStream pcontent) {
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
	
	protected void drawText(float xorig, float yorig, String ptext) throws IOException{
		pdcs.beginText();
		pdcs.setTextMatrix(Matrix.getTranslateInstance(xorig, yorig));
		pdcs.showText(ptext);
		pdcs.endText();
	}
}
