
package panowow.pdfscaner;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

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
 
public class PdfTextchecker extends Application {
	
	private static Logger logger = Logger.getLogger(PdfTextchecker.class.getName());
    private Desktop desktop = Desktop.getDesktop();
	private String defaultfolder;
	private Stage primaryStage;
	
	ArrayList<String> NotaPDF = new ArrayList<String>();
	ArrayList<String> aPDFNonOCR = new ArrayList<String>();
	ArrayList<String> aPDFOCR = new ArrayList<String>();
	ArrayList<String> aPDFPass = new ArrayList<String>();
	ArrayList<String> htmlreports = new ArrayList<String>();
	
	@FXML private TextField Folderselected;
	@FXML private TextArea pdftextTextArea;
	@FXML private CheckBox extractCheckbox;

	
	
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
    void PDFScanStart(ActionEvent event) {

    	logger.debug("start scanning....");
    	Logger logpdfengine = Logger.getLogger("org.apache.pdfbox.util.PDFStreamEngine");
    	logpdfengine.setLevel(org.apache.log4j.Level.OFF);
    	
    	if (defaultfolder != null) {
    		File[] files = new File(defaultfolder).listFiles();
    		showFiles(files);

    		try {
    			logger.debug(System.getProperties());
    			logger.debug(System.getProperty("file.separator"));
    			String filesep = System.getProperty("file.separator");
    			writeSmallTextFile(NotaPDF, defaultfolder + filesep +"Notapdf.txt");
    			writeSmallTextFile(aPDFNonOCR, defaultfolder + filesep + "pdfNotOcred.txt");
    			writeSmallTextFile(aPDFOCR, defaultfolder + filesep +"pdfocred.txt");
    			writeSmallTextFile(htmlreports, defaultfolder + filesep +"pdfaccessibility.csv");
    			
    		} catch (IOException e) {
    			logger.debug(e);
    		}

    	}

    }
    
    public void showFiles(File[] files)
    {

    	StringBuffer strbuff = new StringBuffer();
		htmlreports.add("Filepath" + "," + "FileName" + "," + "SummPassed" + "," +
    	  "Summfailed" + "," + "Headings App Nesting" + "," + "Tagged annotations" +
				"," + "Tagged form fields" + "headers" + "Hides annotation");
			 
    	for (File file : files) {
    		if (file.isDirectory()) {
    			logger.debug("Directory: " + file.getName());
    			strbuff.append("Directory: " + file.getName() + "\n");
    			pdftextTextArea.setText(strbuff.toString());
    			showFiles(file.listFiles()); // Calls same method again.
    		} else {
    			logger.debug("File: " + file.getName());
    			//parsePDFdoc(file);
    			parseHtmlReport(file);
    			strbuff.append(file.getName() + "\n");
    			pdftextTextArea.setText(strbuff.toString());
    		}
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
		String appnest="";
		String taggann="";
		String taggform="";
		String headers="";
		String hideann="";
		
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

			Elements trtaggform = doc.select("tr:contains(Tagged form fields)");
			for (Element li : trtaggform) {				
				Elements cols2status = li.select("td");
			    System.out.println(cols2status.html());
			    taggform = (cols2status.html().contains("Failed")) ? "Failed" : "Pass";
			}
			
			Elements tr = doc.select("tr:contains(Hides annotation)");
			for (Element li : tr) {				
				Elements cols2status = li.select("td");
			    System.out.println(cols2status.html());
			    hideann = (cols2status.html().contains("Failed")) ? "Failed" : "Pass";
			}
			
			
			tr = doc.select("tr:contains(Headers)");
			for (Element li : tr) {				
				Elements cols2status = li.select("td");
			    System.out.println(cols2status.html());
			    headers = (cols2status.html().contains("Failed")) ? "Failed" : "Pass";
			}
			
			
			
			Elements trtaggann = doc.select("tr:contains(Tagged annotations)");
			for (Element li : trtaggann) {				
				Elements cols2status = li.select("td");
			    System.out.println(cols2status.html());
			    taggann = (cols2status.html().contains("Failed")) ? "Failed" : "Pass";
			}	
			
			
			Elements trappnest = doc.select("tr:contains(Appropriate nesting)");
			
			for (Element li : trappnest) {				
				Elements cols2status = li.select("td");
			    System.out.println(cols2status.html());
			    appnest = (cols2status.html().contains("Failed")) ? "Failed" : "Pass";
			}	
			
			if(filename.length() > 1) {
				htmlreports.add(filenamePath + "," + filename + "," 
						+ pass + "," + failed + "," + appnest 
						+ "," + taggann + "," + taggform + "," + headers + "," + hideann);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}

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
	
	
	
	


