package panowow.pdfscaner;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;

import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfReader;


public class TagMetaData {


	private PdfReader preader;
	private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	
	private int numtable = 0;
	private int numfigures = 0;
	
	
	public TagMetaData(PdfReader pdoc) {
		super();
		this.preader = pdoc;
		
	}

	public void setmetadata(PdfReader doc) {
		preader = doc;
	}

	public static String toStringHeadersCSV() {
		StringBuffer outs = new StringBuffer();
		outs.append("Page Count," );
		outs.append( "Title,");
		outs.append( "Author,");
		outs.append( "Subject,");
		outs.append( "Keywords,");
		outs.append( "Creator,");
		outs.append( "Producer," );
		outs.append( "Creation Date,");
		outs.append( "Modification Date" );
		return outs.toString();   
	}
	

	public String toStringDataCSV() {
		StringBuffer outs = new StringBuffer();
		outs.append(preader.getNumberOfPages());
		outs.append( "," + String.valueOf(preader.getInfo().get("Title")).replaceAll(",", ""));
		outs.append( "," + String.valueOf(preader.getInfo().get("Author")).replaceAll(",", ""));
		outs.append( "," + String.valueOf(preader.getInfo().get("Subject")).replaceAll(",", ""));
		outs.append( "," + String.valueOf(preader.getInfo().get("Keywords")).replaceAll(",", ""));
		outs.append( "," + String.valueOf(preader.getInfo().get("Creator")).replaceAll(",", ""));
		outs.append( "," + String.valueOf(preader.getInfo().get("Producer")).replaceAll(",", ""));	
		outs.append( "," + String.valueOf(preader.getInfo().get("CreationDate")).replaceAll(",", ""));
		outs.append( "," + String.valueOf(preader.getInfo().get("ModificationDate")).replaceAll(",", ""));
		return outs.toString();   
	}
	
	
	@Override
	public String toString() {
		return this.toStringDataCSV();  
	}

	public int getNumtable() {
		return numtable;
	}

	public void setNumtableP1() {
		this.numtable++;
	}

	public int getNumfigures() {
		return numfigures;
	}

	public void setNumfiguresP1() {
		this.numfigures++;
	}
	
	
	

}
