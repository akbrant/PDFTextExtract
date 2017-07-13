package panowow.pdfscaner;

import java.io.IOException;
import java.text.SimpleDateFormat;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;


public class TagMetaData {

	private PDDocumentInformation newMetadata;
	private PDDocument pdoc;
	private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	
	
	public TagMetaData(PDDocument pdoc) {
		super();
		this.pdoc = pdoc;
		this.newMetadata = pdoc.getDocumentInformation();
	}

	public static void main(String[] args) {

	}

	public void setmetadata(PDDocument doc) {
		pdoc = doc;
		newMetadata = doc.getDocumentInformation();
	}

	public String toStringHeadersCSV() {
		StringBuffer outs = new StringBuffer();
		outs.append("Page Count," );
		outs.append( "Title,");
		outs.append( "Author,");
		outs.append( "Subject,");
		outs.append( "Keywords,");
		outs.append( "Creator,");
		outs.append( "Producer," );
		outs.append( "Creation Date,");
		outs.append( "Modification Date," );
		outs.append( "Trapped" );
		return outs.toString();   
	}
	

	public String toStringDataCSV() {
		StringBuffer outs = new StringBuffer();
		outs.append(pdoc.getNumberOfPages() );
		outs.append( "," + newMetadata.getTitle() );
		outs.append( "," + newMetadata.getAuthor() );
		outs.append( "," + newMetadata.getSubject() );
		outs.append( "," + newMetadata.getKeywords() );
		outs.append( "," + newMetadata.getCreator() );
		outs.append( "," + newMetadata.getProducer() );
		try {
			outs.append( "," + newMetadata.getCreationDate() );
			outs.append( "," + newMetadata.getModificationDate());
		} catch (IOException e) {
			outs.append( ",,");
			e.printStackTrace();
		}
		outs.append( "," + newMetadata.getTrapped() );
		return outs.toString();   
	}
	
	
	@Override
	public String toString() {
		StringBuffer outs = new StringBuffer();
		outs.append("\nPage Count=" + pdoc.getNumberOfPages() );
		outs.append( "\nTitle=" + newMetadata.getTitle() );
		outs.append( "\nAuthor=" + newMetadata.getAuthor() );
		outs.append( "\nSubject=" + newMetadata.getSubject() );
		outs.append( "\nKeywords=" + newMetadata.getKeywords() );
		outs.append( "\nCreator=" + newMetadata.getCreator() );
		outs.append( "\nProducer=" + newMetadata.getProducer() );
		try {
			outs.append( "\nCreation Date=" + sdf.format(newMetadata.getCreationDate().getTime()));
			outs.append( "\nModification Date=" + sdf.format(newMetadata.getModificationDate().getTime()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		outs.append( "\nTrapped=" + newMetadata.getTrapped() );
		return outs.toString();   
	}
	
	

}
