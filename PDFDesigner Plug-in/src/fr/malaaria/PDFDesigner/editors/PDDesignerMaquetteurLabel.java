package fr.malaaria.PDFDesigner.editors;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class PDDesignerMaquetteurLabel{
	
	private FormData formdata;
	private Control parent;
	private Label label;
	private Point origin;
	
	public PDDesignerMaquetteurLabel(Composite composite, int style) {
		this(composite, style, 20, 20);		
	}		
	
	public PDDesignerMaquetteurLabel(Composite composite, int style, int top, int left) {
		label = new Label(composite, style);
		formdata = new FormData();
		parent = composite;
		label.setVisible(true);
		label.setEnabled(true);
		this.setTopOffset(top);
		this.setLeftOffset(left);
		this.origin = new Point(0, 0);
	}
	
	protected FormData getFormData(){
		return this.formdata;
	}
	
	protected void setLeftOffset(int offset){
		formdata.left = new FormAttachment(parent, offset);
		label.setLayoutData(formdata);
	}
	
	protected void setRightOffset(int offset){
		formdata.right = new FormAttachment(parent, offset);
		label.setLayoutData(formdata);
	}
	
	protected void setTopOffset(int offset){
		formdata.top = new FormAttachment(parent, offset);
		label.setLayoutData(formdata);
	}
	
	protected void setBottomOffset(int offset){
		formdata.bottom = new FormAttachment(parent, offset);
		label.setLayoutData(formdata);
	}
	
	protected void addListener(int eventType, Listener listener){
		label.addListener(eventType, listener);
	}
	
	protected void setText(String text){
		label.setText(text);
	}
	
	protected String getText(){
		return label.getText();
	}
	
	protected Point getOrigin(){
		return this.origin;
	}
	
	protected void setOrigin(Point newOrigin){
		this.origin = newOrigin;
	}
}
