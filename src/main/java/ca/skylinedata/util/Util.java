package ca.skylinedata.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;

import ca.skylinedata.model.NavigationPage;
import ca.skylinedata.model.Note;
import ca.skylinedata.repository.NoteRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Util {


	public static void generatePageLinks(List<NavigationPage> pageList, int currentPage, int pages, int itemsPerPage, int pageLinksAside) {
		int linksToShow = pageLinksAside*2+1;
		// case when we have fewer pages than links to show
		if (pages <= linksToShow) {
			for (int i=0; i<pages; i++) {
				boolean hasHref = i == currentPage ? false : true;
		        pageList.add(new NavigationPage(i, hasHref));
			}
			return;
		}
		int firstLink = currentPage - pageLinksAside;
		int lastLink = currentPage + pageLinksAside;
		if (firstLink < 0) {
			firstLink = 0;
		}
		if (lastLink > pages-1) {
			lastLink = pages-1;
		}
		for (int i=firstLink; i<=lastLink; i++) {
			boolean hasHref = i == currentPage ? false : true;
	        pageList.add(new NavigationPage(i, hasHref));
		}		
	}

    public static String exportNote(Note n, String exportDir) {

        try {
        	String noteName = n.getName();
        	if (noteName == null || n.getName().isBlank() ) {
        		noteName = "Untitled-" + n.getDtModified().format(DateTimeFormatter.ISO_DATE_TIME); 
        	}
        	
        	String filename = noteName.replace(" ", "_").replace(":", "").replace("\\", "").replace("/", "") + ".pdf";
        	final PdfWriter pdfWriter = new PdfWriter(exportDir + "/" + filename);
        	
        	Document doc = HtmlConverter.convertToDocument("<style>div > p { margin: 0px;}</style>"
        			+ "<p><strong>Title:</strong> " + n.getName() + "</p>" 
        			+ "<p><strong>Category:</strong> " + n.getCategory() + "</p>" 
                	+ "<p><strong>Edited:</strong> " + n.getDtModified().format(DateTimeFormatter.ISO_DATE_TIME) + "</p>" 
        			+ "<div>" + n.getHtml() + "</div>", pdfWriter);
        	PdfDocument pdfDoc = doc.getPdfDocument();
        	
            Rectangle pageSize;
            PdfCanvas canvas;
            int np = pdfDoc.getNumberOfPages();
            for (int i = 1; i <= np; i++) {
            	log.info("Processing note {}, page {}, add header/footer", n.getId(), i);
                PdfPage page = pdfDoc.getPage(i);
                pageSize = page.getPageSize();
                canvas = new PdfCanvas(page);
                addHeaderFooter(canvas, pageSize, 
                		noteName.substring(0, Math.min(100, noteName.length())) + " (" + n.getDtModified().format(DateTimeFormatter.ISO_DATE_TIME) + ")",
                		i, np);
            }
            doc.close();
            log.info("PDF Created: {}", exportDir + filename );
            return exportDir + filename;
		} catch (FileNotFoundException e) {
			log.error("ERROR", e);
		} catch (IOException e) {
			log.error("ERROR", e);
		} catch (NullPointerException e) {
			log.error("ERROR", e);
		}
        
        return null;
    }

	
	public static void addHeaderFooter(PdfCanvas canvas, Rectangle pageSize, String header, int pageNumber, int totalPages) throws IOException {
        //Draw header text
         canvas.beginText().setFontAndSize(
                 PdfFontFactory.createFont(StandardFonts.HELVETICA), 7)
                 .moveText(36, pageSize.getHeight() - 20)
                 .showText(header)
                 .endText();
         //Draw footer line
         canvas.setStrokeColor(ColorConstants.BLACK)
                 .setLineWidth(.2f)
                 .moveTo(pageSize.getWidth() / 2 - 30, 20)
                 .lineTo(pageSize.getWidth() / 2 + 30, 20).stroke();
         //Draw page number
         canvas.beginText().setFontAndSize(
                 PdfFontFactory.createFont(StandardFonts.HELVETICA), 7)
                 .moveText(pageSize.getWidth() / 2 - 7, 10)
                 .showText(String.valueOf(pageNumber))
                 .showText(" of ")
                 .showText(String.valueOf(totalPages))
                 .endText();
	}

    public static String zipFiles(List<String> filesPaths, String zipFilePath) {
    	log.info("NOTE EXPORT: {}", zipFilePath);
    	File zipFile = new File(zipFilePath);
		try {
			byte[] buffer = new byte[1024];
			ZipOutputStream zipout = new ZipOutputStream(new FileOutputStream(zipFile));
			for (String fp : filesPaths) {
				File pdfFile = new File(fp);
				FileInputStream fis = new FileInputStream(pdfFile);
                // begin writing a new ZIP entry, positions the stream to the start of the entry data
				zipout.putNextEntry(new ZipEntry(pdfFile.getName()));
                int length;
                while ((length = fis.read(buffer)) > 0) {
                	zipout.write(buffer, 0, length);
                }
                zipout.closeEntry();
                fis.close();
                
			}
			zipout.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return zipFile.getAbsolutePath();
    }

	public static void populateSampleNotes(NoteRepository noteRepository) {
		log.info("Notes in the DB: {}",noteRepository.countByAuthor("user"));
		if (noteRepository.count() == 0) {
			noteRepository.save(new Note("user","Sample Category", false, "Note A", "Sample note A", "<p>Sample note A</p>", "<p>Sample note A</p>"));
			noteRepository.save(new Note("user","Sample Category", false, "Note B", "Sample note B", "<p>Sample note B</p>", "<p>Sample note B</p>"));
			log.info("Created {} sample notes in the DB", noteRepository.count());
		}
	}
    
    
}
