package panowow.pdfscaner;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.itextpdf.text.pdf.PdfDate;
import com.itextpdf.text.pdf.PdfReader;


public class TagMetaData {


	private PdfReader preader;
	private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	
	private int numtable = 0;
	private int numtablerow = 0;
	private int numtableTH = 0;
	

	private int numH2 = 0;
	private int numH3 = 0;
	private int numH4 = 0;
	private int numH5 = 0;

	private int numL = 0;
	private int numLI = 0;

	
	
	private int numfigures = 0;
	
	private String filename ="";
	private String filepath ="";	
	
	public TagMetaData(PdfReader pdoc) {
		super();
		this.preader = pdoc;
		
	}

	public void setmetadata(PdfReader doc) {
		preader = doc;
	}

	public static String toStringHeadersCSV() {
		StringBuffer outs = new StringBuffer();
		outs.append( "FileName,");
		outs.append( "FilePath,");		
		outs.append( "Page Count," );
		outs.append( "Title,");
		outs.append( "Author,");
		outs.append( "Subject,");
		outs.append( "Keywords,");
		outs.append( "Creator,");
		outs.append( "Producer," );
		outs.append( "Creation Date,");
		outs.append( "Creation Date2,");		
		outs.append( "Modification Date," );
		outs.append( "tag Tables," );
		outs.append( "tag Tablerow," );
		outs.append( "tag TableTH," );
		outs.append( "tag H2," );
		outs.append( "tag H3," );
		outs.append( "tag H4," );
		outs.append( "tag H5," );
		outs.append( "tag L," );
		outs.append( "tag LI," );		
		outs.append( "tag Figures" );
		return outs.toString();   
	}
	

	public String toStringDataCSV() {
		StringBuffer outs = new StringBuffer();
		String regex = "\r\n|[\r\n]|[,;]";
		outs.append(this.getFilename());
		outs.append( "," + this.getFilepath());
		outs.append( "," + preader.getNumberOfPages());
		outs.append( "," + String.valueOf(preader.getInfo().get("Title")).replaceAll(regex, ""));
		outs.append( "," + String.valueOf(preader.getInfo().get("Author")).replaceAll(regex, ""));
		outs.append( "," + String.valueOf(preader.getInfo().get("Subject")).replaceAll(regex, ""));
		outs.append( "," + String.valueOf(preader.getInfo().get("Keywords")).replaceAll(regex, ""));
		outs.append( "," + String.valueOf(preader.getInfo().get("Creator")).replaceAll(regex, ""));
		outs.append( "," + String.valueOf(preader.getInfo().get("Producer")).replaceAll(regex, ""));
		outs.append( "," + String.valueOf(preader.getInfo().get("CreationDate")).replaceAll(regex, ""));
		
		SimpleDateFormat format1 = new SimpleDateFormat("MM-dd-yyyy");
		Calendar cal = PdfDate.decode(preader.getInfo().get("CreationDate"));
		outs.append( "," + format1.format(cal.getTime())); //date only no time
		
		outs.append( "," + String.valueOf(preader.getInfo().get("Modified")).replaceAll(regex, ""));
		outs.append( "," + this.getNumtable());
		outs.append( "," + this.getNumtablerow());
		outs.append( "," + this.getNumtableTH());
		outs.append( "," + this.getNumH2());
		outs.append( "," + this.getNumH3());
		outs.append( "," + this.getNumH4());
		outs.append( "," + this.getNumH5());
		outs.append( "," + this.getNumL());
		outs.append( "," + this.getNumLI());
		outs.append( "," + this.getNumfigures());
		return outs.toString();   
	}
	
	
	@Override
	public String toString() {
		return this.toStringDataCSV();  
	}
	
	public String getFilename() {
		return filename;
	}	

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	public void setFilename(String filename) {
		this.filename = filename;
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

	public int getNumtablerow() {
		return numtablerow;
	}

	public void setNumtablerowP1() {
		this.numtablerow++;
	}

	public int getNumtableTH() {
		return numtableTH;
	}

	public void setNumtableTHP1() {
		this.numtableTH++;
	}

	public int getNumH2() {
		return numH2;
	}

	public void setNumH2P1() {
		this.numH2++;
	}

	public int getNumH3() {
		return numH3;
	}

	public void setNumH3P1() {
		this.numH3++;
	}

	public int getNumH4() {
		return numH4;
	}

	public void setNumH4P1() {
		this.numH4++;
	}

	public int getNumH5() {
		return numH5;
	}

	public void setNumH5P1() {
		this.numH5++;
	}

	public int getNumL() {
		return numL;
	}

	public void setNumLP1() {
		this.numL++;
	}

	public int getNumLI() {
		return numLI;
	}

	public void setNumLIP1() {
		this.numLI++;
	}
	
	
	

}
