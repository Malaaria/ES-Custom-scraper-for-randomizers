package fr.malaaria.PDFDesigner.editors;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/**
 * An example showing how to create a multi-page editor.
 * This example has 3 pages:
 * <ul>
 * <li>page 0 contains a nested text editor.
 * <li>page 1 allows you to change the font used in page 2
 * <li>page 2 shows the words in page 0 in sorted order
 * </ul>
 */
public class PDFDesignerMultiPageEditor extends MultiPageEditorPart implements IResourceChangeListener{

	/** The text editor used in page 0. */
	private TextEditor editor;

	/** This is for the styler in page 1 */
	private PDDesignerMaquetteurLabel lblTitre;	
	private Composite composite;	
	private Canvas pageMaquetteur;
	int width;
	int height;
	String pageFormat;
	ScrollBar hBar;
	ScrollBar vBar;
	ArrayList<PDDesignerMaquetteurLabel> labelTab;
	Button addLabelButton;
	Point mouseCursorDown;

	/** The text widget used in page 2. */
	private StyledText text;
	/**
	 * Creates a multi-page editor example.
	 */
	public PDFDesignerMultiPageEditor() {
		super();		
		pageFormat = "A4";
		labelTab = new ArrayList<PDDesignerMaquetteurLabel>();
	}
	/**
	 * Creates page 0 of the multi-page editor,
	 * which contains a text editor.
	 */
	void createPageEditor() {
		try {
			editor = new TextEditor();
			int index = addPage(editor, getEditorInput());
			setPageText(index, "Code");
		} catch (PartInitException e) {
			ErrorDialog.openError(
					getSite().getShell(),
					"Error creating nested text editor",
					null,
					e.getStatus());
		}
	}
	/**
	 * Creates page 1 of the multi-page editor,
	 * which allows you to change the font used in page 2.
	 */
	void createPageMaquetteur() {

		composite = new Composite(getContainer(), SWT.NONE);
		FormLayout layout = new FormLayout();
		composite.setLayout(layout);		

		//Canvas canvas = new Canvas(composite, SWT.NONE);		

		int index = addPage(composite);
		setPageText(index, "Maquetteur");
	}
	/**
	 * Creates page 2 of the multi-page editor,
	 * which shows the sorted text.
	 */
	void createPagePreview() {
		Composite composite = new Composite(getContainer(), SWT.NONE);
		FillLayout layout = new FillLayout();
		composite.setLayout(layout);
		text = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL);
		text.setEditable(false);

		int index = addPage(composite);
		setPageText(index, "Pr�visualisation");
	}
	/**
	 * Creates the pages of the multi-page editor.
	 */
	protected void createPages() {
		createPageEditor();
		createPageMaquetteur();
		createPagePreview();
		this.setPartName(editor.getTitle());
	}
	/**
	 * The <code>MultiPageEditorPart</code> implementation of this 
	 * <code>IWorkbenchPart</code> method disposes all nested editors.
	 * Subclasses may extend.
	 */
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		super.dispose();
	}
	/**
	 * Saves the multi-page editor's document.
	 */
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}
	/**
	 * Saves the multi-page editor's document as another file.
	 * Also updates the text for page 0's tab, and updates this multi-page editor's input
	 * to correspond to the nested editor's.
	 */
	public void doSaveAs() {
		IEditorPart editor = getEditor(0);
		editor.doSaveAs();
		setPageText(0, editor.getTitle());
		setInput(editor.getEditorInput());
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart
	 */
	public void gotoMarker(IMarker marker) {
		setActivePage(0);
		IDE.gotoMarker(getEditor(0), marker);
	}
	/**
	 * The <code>MultiPageEditorExample</code> implementation of this method
	 * checks that the input is an instance of <code>IFileEditorInput</code>.
	 */
	public void init(IEditorSite site, IEditorInput editorInput)
			throws PartInitException {
		if (!(editorInput instanceof IFileEditorInput))
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		super.init(site, editorInput);
	}
	/* (non-Javadoc)
	 * Method declared on IEditorPart.
	 */
	public boolean isSaveAsAllowed() {
		return true;
	}
	/**
	 * Calculates the contents of page 2 when the it is activated.
	 */
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if (newPageIndex == 1) {
			try {
				try {
					buildMaquetteur();
				} catch (JDOMException | IOException e) {
					e.printStackTrace();
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Closes all project files on project close.
	 */
	public void resourceChanged(final IResourceChangeEvent event){
		if(event.getType() == IResourceChangeEvent.PRE_CLOSE){
			Display.getDefault().asyncExec(new Runnable(){
				public void run(){
					IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
					for (int i = 0; i<pages.length; i++){
						if(((FileEditorInput)editor.getEditorInput()).getFile().getProject().equals(event.getResource())){
							IEditorPart editorPart = pages[i].findEditor(editor.getEditorInput());
							pages[i].closeEditor(editorPart,true);
						}
					}
				}            
			});
		}
	}

	private boolean sizeCanvas(String sSize, boolean portrait){	
		boolean page_FormatChange = !pageFormat.equals(sSize);
		int temp;
		pageFormat = sSize;
		//Composite composite = (Composite) super.getControl(1);		
		if(sSize.equals("A0")){										
			width = 2348;
			height = 3370;
		}else if(sSize.equals("A1")){
			width = 2348;
			height = 1648;			
		}else if(sSize.equals("A2")){
			width = 1191;
			height = 1648;
		}else if(sSize.equals("A3")){
			width = 1191;
			height = 842;
		}else if(sSize.equals("A4")){
			width = 595;
			height = 842;
		}else if(sSize.equals("A5")){
			width = 595;
			height = 420;
		}else if(sSize.equals("A6")){
			width = 298;
			height = 420;
		}else{
			// A4 par d�faut
			width = 595;
			height = 842;
		}
		if(portrait){
			if(width>height){
				temp = width;
				width = height;
				height = temp;
			}
		}else{
			if(width<height){
				temp = width;
				width = height;
				height = temp;
			}
		}		
		return page_FormatChange; 
	}

	private void buildMaquetteur() throws BadLocationException, JDOMException, IOException{
		IEditorPart part = this.getEditor(0);		   
		ITextEditor editor = (ITextEditor)part;
		IDocumentProvider dp = editor.getDocumentProvider();
		IDocument doc = dp.getDocument(editor.getEditorInput());		
		String textOfCode = doc.get(0, doc.getLength());
		SAXBuilder sxb = new SAXBuilder();		   
		InputStream stream = new ByteArrayInputStream(textOfCode.getBytes("UTF-8"));
		Document document = sxb.build(stream);
		Element racine = document.getRootElement();		
		Composite composite = (Composite)getControl(1);		
		Element orientationElement = racine.getChild("orientation");
		boolean portrait = true;
		if(orientationElement != null){
			portrait = !orientationElement.getValue().equals("landscape");
		}		

		boolean pageFormatChange = false;
		Element sizeElement = racine.getChild("size");				
		if(sizeElement != null){
			pageFormatChange = sizeCanvas(sizeElement.getValue(), portrait);		
		}
		final Point origin = new Point (0, 0);	
		Point sizeOfContainer = getContainer().getSize();
		FormData pageMaquetteurData = new FormData();
		// On enl�ve 20 pour voir le bord du canvas qui peut �tre masqu� par des onglets
		pageMaquetteurData.height = (height)<=sizeOfContainer.y-20?height:sizeOfContainer.y-20;
		pageMaquetteurData.width = (width)<=sizeOfContainer.x-20?width:sizeOfContainer.x-20;
		pageMaquetteurData.top = new FormAttachment(pageMaquetteur, 40);
		if(pageMaquetteur == null || pageFormatChange){
			if(pageMaquetteur != null){
				pageMaquetteur.dispose();
				hBar.dispose();
				vBar.dispose();
			}
			pageMaquetteur = new Canvas(composite, SWT.H_SCROLL | SWT.V_SCROLL);
			pageMaquetteur.setLayout(new FormLayout());
			hBar = pageMaquetteur.getHorizontalBar();
			vBar = pageMaquetteur.getVerticalBar();										

			pageMaquetteur.setLayoutData(pageMaquetteurData);			
			hBar.addListener(SWT.Selection, e -> {
				int hSelection = hBar.getSelection ();
				int destX = -hSelection - origin.x;						
				pageMaquetteur.scroll (destX, 0, 0, 0, pageMaquetteur.getClientArea().width, pageMaquetteur.getClientArea().height, false);
				origin.x = -hSelection;				
				for(int i=0;i<labelTab.size();i++){
					labelTab.get(i).setOrigin(new Point(-hSelection, labelTab.get(i).getOrigin().y));
				}				
			});
			vBar.addListener (SWT.Selection, e -> {
				int vSelection = vBar.getSelection ();
				int destY = -vSelection - origin.y;							
				pageMaquetteur.scroll (0, destY, 0, 0, pageMaquetteur.getClientArea().width, pageMaquetteur.getClientArea().height, false);
				origin.y = -vSelection;			
				for(int i=0;i<labelTab.size();i++){
					labelTab.get(i).setOrigin(new Point(labelTab.get(i).getOrigin().x, -vSelection));					
				}				
			});			
		}
		
		labelTab.clear();
		Element labelsElement = racine.getChild("labels");
		List<Element> listElement = labelsElement.getChildren();
		Iterator<Element> iterElement = listElement.iterator();		
		while(iterElement.hasNext()){
			Element labelElement = iterElement.next();	
			PDDesignerMaquetteurLabel labelAStocker = new PDDesignerMaquetteurLabel(composite, SWT.NONE);
			Element posXElement = labelElement.getChild("posx");
			Element posYElement = labelElement.getChild("posy");
			Element texteElement = labelElement.getChild("texte");
			labelAStocker.setText(texteElement.getValue());
			labelAStocker.setLeftOffset(Integer.parseInt(posXElement.getValue()));
			labelAStocker.setTopOffset(Integer.parseInt(posYElement.getValue()));
			labelTab.add(labelAStocker);
		}		
		
		pageMaquetteur.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				// Repeindre le canvas
				Color white = new Color (getContainer().getDisplay(), 0xFF, 0xFF, 0xFF);
				Color red = new Color (getContainer().getDisplay(), 0, 0, 0);
				e.gc.setBackground(white);
				e.gc.setForeground(red);								
				e.gc.fillRectangle(0, 0, width, height);		
				/*for(int i=0;i<labelTab.size();i++){
					labelTab.get(i).
				}*/
				white.dispose();		
				red.dispose();											
			}
		});
		Element nameElement = racine.getChild("name");
		if(nameElement != null){
			if(lblTitre == null){								
				lblTitre = new PDDesignerMaquetteurLabel(composite, SWT.NONE);
				lblTitre.setText(nameElement.getValue());				
			}
		}
		addLabelButton = new Button(composite, SWT.NONE);
		FormData addLabelButtonData = new FormData();
		addLabelButtonData.left = new FormAttachment(composite, 130);
		addLabelButton.setLayoutData(addLabelButtonData);
		addLabelButton.setText("Ajouter Texte");
		addLabelButton.addListener(SWT.MouseUp, e -> {
			spawnNewLabel();
		});

		
		pageMaquetteur.redraw();
		composite.layout();		
	}

	private String printPointLocation(Point p){
		String result = "";
		result = "x:" + p.x + ", y:" + p.y;
		return result;
	}
	
	private void mdText(String text){
		MessageDialog.openInformation(getContainer().getShell(), "Debug", text);
	}

	private void saveLabelsInEditor(){
		IEditorPart part = this.getEditor(0);		   
		ITextEditor editor = (ITextEditor)part;
		IDocumentProvider dp = editor.getDocumentProvider();
		IDocument doc = dp.getDocument(editor.getEditorInput());		
		String textOfCode;
		try {
			textOfCode = doc.get(0, doc.getLength());		 
			SAXBuilder sxb = new SAXBuilder();		
			InputStream stream = new ByteArrayInputStream(textOfCode.getBytes("UTF-8"));
			Document document = sxb.build(stream);
			Element racine = document.getRootElement();
			Element elementLabels = racine.getChild("labels");			
			elementLabels.removeChildren("label");					
			for(int i=0;i<labelTab.size();i++){
				PDDesignerMaquetteurLabel labelEnCours = labelTab.get(i);				
				Element elementLabel = new Element("label");
				Element elementPosX = new Element("posx");
				elementPosX.addContent(Integer.toString(labelEnCours.getFormData().left.offset));
				Element elementPosY = new Element("posy");
				elementPosY.addContent(Integer.toString(labelEnCours.getFormData().top.offset));
				Element elementTexte = new Element("texte");
				elementTexte.addContent(labelEnCours.getText());
				elementLabel.addContent(elementPosX);
				elementLabel.addContent(elementPosY);
				elementLabel.addContent(elementTexte);
				elementLabels.addContent(elementLabel);				
			}			
			doc.set(new XMLOutputter(Format.getPrettyFormat()).outputString(document));		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}catch (BadLocationException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void spawnNewLabel(){
		PDDesignerMaquetteurLabel newLabel = new PDDesignerMaquetteurLabel(pageMaquetteur, SWT.NONE, 100, 20);			
		newLabel.setText("Texte");						
		newLabel.addListener(SWT.MouseUp, e1 -> {
			Point cursorLocation = Display.getCurrent().getCursorLocation();
			int varx = cursorLocation.x - mouseCursorDown.x;
			int vary = cursorLocation.y - mouseCursorDown.y;
			newLabel.setLeftOffset(newLabel.getFormData().left.offset + varx);								
			newLabel.setTopOffset(newLabel.getFormData().top.offset + vary);
			pageMaquetteur.layout();			
			mouseCursorDown = null;
			saveLabelsInEditor();				
		});
		newLabel.addListener(SWT.MouseDown, e2 -> {
			if(mouseCursorDown == null){
				mouseCursorDown = Display.getCurrent().getCursorLocation();
			}
		});
		newLabel.addListener(SWT.MouseMove, e3 -> {
			if(mouseCursorDown != null){
				Point cursorLocation = Display.getCurrent().getCursorLocation();
				int varx = cursorLocation.x - mouseCursorDown.x;
				int vary = cursorLocation.y - mouseCursorDown.y;
				newLabel.setLeftOffset(newLabel.getFormData().left.offset + varx);								
				newLabel.setTopOffset(newLabel.getFormData().top.offset + vary);
				mouseCursorDown = cursorLocation;
				pageMaquetteur.layout();
			}
		});
		labelTab.add(newLabel);
		pageMaquetteur.layout();
	}
}
