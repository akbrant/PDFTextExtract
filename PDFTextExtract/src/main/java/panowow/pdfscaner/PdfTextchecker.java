
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
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.util.PDFTextStripper;

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
	
	@FXML private TextField Folderselected;
	@FXML private TextArea pdftextTextArea;
	@FXML private CheckBox extractCheckbox;

	
	
    @Override
    public void start(final Stage stage) throws IOException {
    
    	Parent root = FXMLLoader.load(getClass().getResource("/fxml/Pdftext.fxml"));
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

    		} catch (IOException e) {
    			logger.debug(e);
    		}

    	}

    }
    
    public void showFiles(File[] files)
    {

    	StringBuffer strbuff = new StringBuffer();
    	for (File file : files) {
    		if (file.isDirectory()) {
    			logger.debug("Directory: " + file.getName());
    			strbuff.append("Directory: " + file.getName() + "\n");
    			pdftextTextArea.setText(strbuff.toString());
    			showFiles(file.listFiles()); // Calls same method again.
    		} else {
    			logger.debug("File: " + file.getName());
    			parsePDFdoc(file);
    			strbuff.append(file.getName() + "\n");
    			pdftextTextArea.setText(strbuff.toString());
    		}
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
    		logger.debug("Found a PDF doc. ");
    		String parsedText = pdfStripper.getText(pdDoc);
    		logger.debug("Done Parsing length: " + parsedText.length() +  " Num pages: " + pdDoc.getNumberOfPages());
    		if(parsedText.length() !=  pdDoc.getNumberOfPages() ) {
    			aPDFOCR.add(pdffile.getAbsolutePath() + " Text Length: " + parsedText.length() + " Pages: " +  pdDoc.getNumberOfPages());
    			if (extractCheckbox.isSelected()) {
    				String filesep = System.getProperty("file.separator");
        			writeSmallTextFile(Arrays.asList(parsedText), defaultfolder + filesep +pdffile.getName()+".txt" );
    			}
    			
    		} else {
    			aPDFNonOCR.add(pdffile.getAbsolutePath()+ " Text Length: " + parsedText.length() + " Pages: " +  pdDoc.getNumberOfPages());
    		}
    		if (cosDoc != null)
				cosDoc.close();
			if (pdDoc != null)
				pdDoc.close();
    	} catch (IOException e) {
    		logger.error(e);
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
	
	
	
	


