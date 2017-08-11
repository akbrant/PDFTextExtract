
package panowow.pdfscaner;

import java.awt.Desktop;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfString;
import com.itextpdf.text.xml.xmp.XmpWriter;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

public class PdfTextchecker extends Application {
	
	private static Logger logger = Logger.getLogger(PdfTextchecker.class.getName());
    private Desktop desktop = Desktop.getDesktop();
	private String defaultfolder;
	private Stage primaryStage;
	
	ArrayList<String> NotaPDF = new ArrayList<String>();
	ArrayList<String> aPDFNonOCR = new ArrayList<String>();
	ArrayList<String> aPDFOCR = new ArrayList<String>();
	ArrayList<String> aPDFPass = new ArrayList<String>();
	ArrayList<String> htmlreportsCSV = new ArrayList<String>();
	ArrayList<String> pdfMetaDataCSV = new ArrayList<String>();
	
	@FXML private TextField Folderselected;
	@FXML private TextArea pdftextTextArea;
	@FXML private CheckBox extractCheckbox;
	@FXML private CheckBox pasrseHtmlCheckbox;
	@FXML private CheckBox removeUATaggsCheckbox;
	@FXML private CheckBox pdfMetaCheckBox;
	@FXML private CheckBox scanORCCheckbox;

	//UA tag support
	@FXML private Pane Uatagspane;
	@FXML private GridPane UaGridPane;
	
	@FXML public CheckBox uatable;
	@FXML public CheckBox uatablerow;
	@FXML public CheckBox uath;
	@FXML public CheckBox ua7headers;
	
	@FXML public CheckBox uaH2H6;
	@FXML public CheckBox uashape;
	@FXML public CheckBox uatoc;
	@FXML public CheckBox uatoci;	
	@FXML public CheckBox ualink;
	@FXML public CheckBox ual;
	@FXML public CheckBox uali;
	@FXML public CheckBox uafigure;
	@FXML public CheckBox uachart;
	@FXML public CheckBox uainlineshape;
	
	
	
	private PdfDictionary structTreeRoot;
	private TaggedPdfParser tagtool;
	
	
	
	
    @Override
    public void start(final Stage stage) throws IOException {
    
    	Parent root = FXMLLoader.load(getClass().getResource("Pdftext.fxml"));
		Scene scene = new Scene(root);

		stage.setTitle("Pick PDF directory");
		stage.setScene(scene);
		stage.setResizable(false);
		
		stage.show();

		this.primaryStage = stage;
 
        final FileChooser fileChooser = new FileChooser();
 
        final Button openButton = new Button("Open a Picture...");
        final Button openMultipleButton = new Button("Open Pictures...");

        
        openButton.setOnAction(
            new EventHandler<ActionEvent>() {
                public void handle(final ActionEvent e) {
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null) {
                        openFile(file);
                    }
                }
            });
 
        openMultipleButton.setOnAction(
            new EventHandler<ActionEvent>() {
                public void handle(final ActionEvent e) {
                    List<File> list =
                        fileChooser.showOpenMultipleDialog(stage);
                    if (list != null) {
                        for (File file : list) {
                            openFile(file);
                        }
                    }
                }
            });      
    }
 
    
    public static void main(String[] args) {
        Application.launch(args);
       
    }
 
    private void openFile(File file) {
        try {
            desktop.open(file);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }
    
    @FXML
    void enableUAtags(ActionEvent event) {
    	logger.debug("hit Remove UA Tags check box...");
    	if (Uatagspane.isDisabled()) {
    		Uatagspane.setDisable(false);
    	} else {
    		Uatagspane.setDisable(true);
    	}
    	
    }
    
    @FXML
    void PDFScanStart(ActionEvent event) {

    	logger.debug("start scanning....");
    	Logger logpdfengine = Logger.getLogger("org.apache.pdfbox.util.PDFStreamEngine");
    	logpdfengine.setLevel(org.apache.log4j.Level.OFF);
    	
    	if (defaultfolder != null) {
    		File[] files = new File(defaultfolder).listFiles();
    		tagtool = new TaggedPdfParser(this);
    		showFiles(files);

    		try {
    			logger.debug(System.getProperties());
    			logger.debug(System.getProperty("file.separator"));
    			String filesep = System.getProperty("file.separator");
    			if (scanORCCheckbox.isSelected()) {
	    			writeSmallTextFile(NotaPDF, defaultfolder + filesep +"Notapdf.txt");
	    			writeSmallTextFile(aPDFNonOCR, defaultfolder + filesep + "pdfNotOcred.txt");
	    			writeSmallTextFile(aPDFOCR, defaultfolder + filesep +"pdfocred.txt");
	    			NotaPDF.clear();
	    			aPDFNonOCR.clear();
	    			aPDFOCR.clear();
    			}
    			if (pasrseHtmlCheckbox.isSelected()) {
	    			writeSmallTextFile(htmlreportsCSV, defaultfolder + filesep +"pdfaccessibility.csv");
	    			htmlreportsCSV.clear();
    			}
    			if (pdfMetaCheckBox.isSelected()) {
	    			writeSmallTextFile(pdfMetaDataCSV, defaultfolder + filesep +"pdfMetaData.csv");
	    			pdfMetaDataCSV.clear();
    			}
    		} catch (IOException e) {
    			logger.debug(e);
    		}

    	}

    }
    
    public void showFiles(File[] files)
    {

    	StringBuffer strbuff = new StringBuffer();
		htmlreportsCSV.add("Filepath" + "," + "FileName" + "," + "SummPassed" + "," + "Summfailed"
				+ ",Accessibility permission flag" + ",Image-only PDF" + ",Tagged PDF" + ",Primary language" + ",Title"
				+ ",Bookmarks"
				+ ",Tagged content" + ",annotations" + ",Tab order" + ",Character encoding" + ",Tagged multimedia"
				+ ",Screen flicker" + ",Scripts" + ",Timed responses"
				+ ",Tagged form fields" + ",Field descriptions"
				+ ",Figures alternate text" + ",Nested alternate text" + ",Associated with content"
				+ ",Hides annotation" + ",Other elements alternate text"
				+ ",Rows" + ",TH and TD" + ",Headers" + ",Regularity" + ",Summary"
				+ ",List items" + ",Lbl and LBody" + ",Appropriate nesting");
		
		pdfMetaDataCSV.add(TagMetaData.toStringHeadersCSV());  //add headers for metaCSV
		
    	for (File file : files) {
    		if (file.isDirectory()) {
    			logger.debug("Directory: " + file.getName());
    			strbuff.append("Directory: " + file.getName() + "\n");
    			pdftextTextArea.setText(strbuff.toString());
    			showFiles(file.listFiles()); // Calls same method again.
    		} else {
    			logger.debug("File: " + file.getName());

    			if (removeUATaggsCheckbox.isSelected()  && (file.getName().toLowerCase().contains("pdf"))) {
    				removeUAtaggs(file);
    			} else if (pasrseHtmlCheckbox.isSelected() && (file.getName().toLowerCase().contains("html"))){
    				parseHtmlReport(file);
    			} else if (extractCheckbox.isSelected()  && (file.getName().toLowerCase().contains("pdf"))){
    				parsePDFdoc(file);
    			} else if (pdfMetaCheckBox.isSelected()  && (file.getName().toLowerCase().contains("pdf"))) {
	    			this.getPDFmetaData(file);
    			}
				
    			strbuff.append(file.getName() + "\n");
    			pdftextTextArea.setText(strbuff.toString());
    		}
    	} 
    }

    void removeUAtaggs(File file) {
    	try {
    		Map<String, String> infoitext;
			File dir = new File(file.getParent() + "/removedtags" );
			dir.mkdirs();
	    	logger.debug("Going to remove tags from: " + file.getName());
			PdfReader reader = new PdfReader(file.getAbsolutePath());
			logger.debug("Checking for anontations..");
			//removeannots(reader);
			tagtool.convertToXml(reader,  new FileOutputStream(new File(dir.getAbsolutePath() +"/" + file.getName() + ".xml")));	
			PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(dir.getAbsolutePath() +"/" + file.getName()));			
			logger.debug("Checking for title..and setting if blank");
			infoitext = this.checkNsettitle(reader, file);
			stamper.setMoreInfo(infoitext);
			stamper.close();
			reader.close();
		} catch (IOException e) {
    		logger.error(e);
    		logger.debug("cant read file or metadata: " + file.getAbsolutePath());   
		} catch (DocumentException e) {
    		logger.error(e);
    		logger.debug("cant read file or metadata: " + file.getAbsolutePath());   
		}    	
    }
    
    void removeannots(PdfReader reader){
    	for (int i = 1; i <= reader.getNumberOfPages(); i++) {
    	    PdfArray array = reader.getPageN(i).getAsArray(PdfName.ANNOTS);
    	    if (array == null) continue;
    	    for (int j = 0; j < array.size(); j++) {
    	        PdfDictionary annot = array.getAsDict(j);
    	        PdfString text = annot.getAsString(PdfName.CONTENTS);
    	        logger.debug(text);
    	        annot.clear();    	        
    	    }
    	}
    	
    }
    
    Map<String, String> checkNsettitle(PdfReader reader, File file){
    	String regex = "\r\n|[\r\n]|[,;]";
    	String title = String.valueOf(reader.getInfo().get("Title")).replaceAll(regex, "");
    	Map<String, String> l_infoitext = null;
    	if (title.equals("null") || title == null || title.length()<1) {  //no title set 
    		 l_infoitext= reader.getInfo();
    	     String fileNameWithOutExt = file.getName().replaceFirst("[.][^.]+$", "");
    	     l_infoitext.put("Title", fileNameWithOutExt);
    	} else {
    		 l_infoitext = reader.getInfo();
    	}
		return l_infoitext;
    }
    
	void getPDFmetaData(File pdffile) {    	
    	logger.debug("light parseing for metata PDF doc.... of " + pdffile.getName());
    	TagMetaData meatadata = null;
    	try {
    		//use itext to get metatdata
    		File dir = new File(pdffile.getParent() + "/xmltxtfiles" );
			dir.mkdirs();
    		PdfReader reader = new PdfReader(pdffile.getAbsolutePath());
    		meatadata = new TagMetaData(reader);
    		meatadata.setFilename(pdffile.getName());
    		meatadata.setFilepath(pdffile.getAbsolutePath());
    		logger.debug(meatadata.toString());    		
			tagtool.convertToXml(reader,  new FileOutputStream(new File(dir.getAbsolutePath() +"/" + pdffile.getName() + ".xml")), meatadata);
			//add the println to cheep csv writer strings.
			pdfMetaDataCSV.add(meatadata.toStringDataCSV());
    	} catch (IOException e) {
    		if (meatadata != null ) {  //hate to do this, but we want to recored pdf meta data in csv even if pdf is not tagged. 
    	 		pdfMetaDataCSV.add(meatadata.toStringDataCSV());
    		}
    		logger.error(e);
    		logger.debug("cant read file or metadata: " + pdffile.getAbsolutePath());   		
    	}
    	
	}

    

    
    
	void parseHtmlReport(File htmlfile) {
		/// Users/brant/git/PDFTextExtract/PDFTextExtract/docs/html
		logger.debug("parsing html report file " + htmlfile.getName());
		logger.debug("length" + htmlfile.getTotalSpace());
		String filenamePath ="";
		String filename="";
		String pass="";
		String failed="";
			
		try {
			
			Document doc = Jsoup.parse(htmlfile, "UTF-8");

		    for(Element e : doc.getAllElements()){
		        for(Node n: e.childNodes()){
		            if(n instanceof Comment){
		            	if(n.toString().contains("pdf-source")){
		            		filenamePath = n.toString().split("\"")[1];
		            		System.out.println(filenamePath);
		            	}
		                
		            }
		        }
		    }
			
			Elements dlfile = doc.select("dd:contains(pdf)");
			
			for (Element dl2 : dlfile) {
			    System.out.println(dl2.html());
			    filename = dl2.html();
			}	
			
			Elements lipass = doc.select("li:contains(Passed:)");
			
			for (Element li : lipass) {
			    System.out.println(li.html());
			    pass = li.html();
			}	

			Elements lifailded = doc.select("li:contains(Failed:)");
			
			for (Element li : lifailded) {
			    System.out.println(li.html());
			    failed = li.html();
			}	


			/* Document Rules 6 of them with Failed or Passed*/			
			String doc1 = this.checkstatus(doc, "Accessibility permission flag");
			String doc2 = this.checkstatus(doc, "Image-only PDF");
			String doc3 = this.checkstatus(doc, "Tagged PDF");
			String doc4 = this.checkstatus(doc, "Primary language");
			String doc5 = this.checkstatus(doc, "Title");
			String doc6 = this.checkstatus(doc, "Bookmarks");
			
			/* Page Rules 8 of them with Failed or Passed*/			
			String page1 = this.checkstatus(doc, "Tagged content");
			String page2 = this.checkstatus(doc, "Tagged annotations");
			String page3 = this.checkstatus(doc, "Tab order");
			String page4 = this.checkstatus(doc, "Character encoding");
			String page5 = this.checkstatus(doc, "Tagged multimedia");
			String page6 = this.checkstatus(doc, "Screen flicker");
			String page7 = this.checkstatus(doc, "Scripts");
			String page8 = this.checkstatus(doc, "Timed responses");
			
			/* Form Rules 2 of them */
			String form1 = this.checkstatus(doc, "Tagged form fields");
			String form2 = this.checkstatus(doc, "Field descriptions");
			
			/* Alt text 5 of them */
			String alttext1 = this.checkstatus(doc, "Figures alternate text");
			String alttext2 = this.checkstatus(doc, "Nested alternate text");
			String alttext3 = this.checkstatus(doc, "Associated with content");
			String alttext4 = this.checkstatus(doc, "Hides annotation");
			String alttext5 = this.checkstatus(doc, "Other elements alternate text");
			
			/* Tabels 5 of them  */
			String tables1 = this.checkstatus(doc, "Rows");
			String tables2 = this.checkstatus(doc, "TH and TD");
			String tables3 = this.checkstatus(doc, "Headers");
			String tables4 = this.checkstatus(doc, "Regularity");
			String tables5 = this.checkstatus(doc, "Summary");
			
			/* List 2 of them */
			String list1 = this.checkstatus(doc, "List items");
			String list2 = this.checkstatus(doc, "Lbl and LBody");
			
			/*  Headings 1 of them*/
			String headings1 = this.checkstatus(doc, "Appropriate nesting");
			
			if(filename.length() > 1) {
				htmlreportsCSV.add(filenamePath + "," + filename + "," 
						+ pass + "," + failed + ","  
						+doc1+doc2+doc3+doc4+doc5+doc6 +
						page1+page2+page3+page4+page5+page6+page7+page8+
						form1 + form2+ 
						alttext1+alttext2+alttext3+alttext4+alttext5+
						tables1+tables2+tables3+tables4+tables5+
						list1+list2+headings1
						);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private String checkstatus(Document doc, String findRule) {		
		String searchstr = "tr:contains(" + findRule + ")";
		Elements trappnest = doc.select(searchstr);
		String statusStr = "";
		for (Element li : trappnest) {				
			Elements cols2status = li.select("td");
		    System.out.println(cols2status.html());
		    statusStr = (cols2status.html().contains("Failed")) ? "Failed" : "Pass";
		}	
		return statusStr + ",";		
	}
	



    void parsePDFdoc(File pdffile){
    	PDFParser parser = null;
    	PDDocument pdDoc = null;
    	COSDocument cosDoc = null;
    	PDDocumentInformation info = null;
    	PDFTextStripper pdfStripper = null;
    	
    	logger.debug("Parsing PDF doc....");
    	
    	try {
    		parser = new PDFParser(new FileInputStream(pdffile));
    		parser.parse();
    		cosDoc = parser.getDocument();
    		pdfStripper = new PDFTextStripper();
    		pdDoc = new PDDocument(cosDoc);
    		info = pdDoc.getDocumentInformation();
    		logger.debug(info.getKeywords());
    		logger.debug(info.getTitle());
    		logger.debug("Found a PDF doc.  ");
    		String parsedText = pdfStripper.getText(pdDoc);
    		logger.debug("Done Parsing length: " + parsedText.length() +  " Num pages: " + pdDoc.getNumberOfPages());
    		if(parsedText.trim().length() > 0) {
    			aPDFOCR.add(pdffile.getAbsolutePath() + ", Text Length: " + parsedText.length() + ", Pages: " +  pdDoc.getNumberOfPages());
    			if (extractCheckbox.isSelected()) {
    				String filesep = System.getProperty("file.separator");
        			writeSmallTextFile(Arrays.asList(parsedText), defaultfolder + filesep +pdffile.getName()+".txt" );
    			}
    			
    		} else {
    			aPDFNonOCR.add(pdffile.getAbsolutePath()+ ", Text Length: " + parsedText.length() + ", Pages: " +  pdDoc.getNumberOfPages());
    		}
    		if (cosDoc != null)
				cosDoc.close();
			if (pdDoc != null)
				pdDoc.close();
    	} catch (IOException e) {
    		logger.error(e);
    		logger.debug("Not a pdf or cant read it: " + pdffile.getAbsolutePath());
    		NotaPDF.add(pdffile.getAbsolutePath());
    	}
    }

    
    
    @FXML
	void openfoldercho(ActionEvent event) {
		DirectoryChooser directoryChooser = new DirectoryChooser();
		
		//Set to user directory or go to default if cannot access
		String userDirectoryString = System.getProperty("user.home");
		File userDirectory = new File(userDirectoryString);
		directoryChooser.setInitialDirectory(userDirectory);
		
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
         
		
        if(selectedDirectory == null){
        	logger.debug("No Directory selected");
        }else{
        	logger.debug(selectedDirectory.getAbsolutePath());
        	defaultfolder = selectedDirectory.getAbsolutePath();
        	Folderselected.setText(defaultfolder);
        }
        
     
	}

    
    private static void writeSmallTextFile(List<String> aLines, String aFileName) throws IOException {
        Path path = Paths.get(aFileName);
        Files.write(path, aLines, StandardCharsets.UTF_8);
    }
    
}
	
	
	
	


