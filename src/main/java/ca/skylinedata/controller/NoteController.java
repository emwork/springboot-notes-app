package ca.skylinedata.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.ModelAndView;

import ca.skylinedata.model.NavigationPage;
import ca.skylinedata.model.NavigationPageListView;
import ca.skylinedata.model.Note;
import ca.skylinedata.model.NotesListView;
import ca.skylinedata.model.SearchForm;
import ca.skylinedata.repository.NoteRepository;
import ca.skylinedata.util.Util;


@Controller
@Slf4j
public class NoteController {

	@Value("${notes.exportdir:/tmp}")
	private String exportDir;
	
	@Autowired
	private NoteRepository noteRepository;

	private final int itemsPerPage = 5;
	private final int pageLinksAside = 2;
	

	@GetMapping("/")
    public String index(@RequestParam(defaultValue="0") int page, Principal principal, Model model) {
		log.debug("/index, page {}", page);
		long countAll = noteRepository.countByAuthor(principal.getName());
		int pages = (int) (countAll % itemsPerPage == 0 ? countAll / itemsPerPage : countAll / itemsPerPage + 1);
		
        ArrayList<Note> noteList = (ArrayList<Note>) noteRepository.findByAuthorOrderByDtModifiedDesc(principal.getName(), PageRequest.of(page, itemsPerPage));
        model.addAttribute("items", new NotesListView(noteList));

        ArrayList<NavigationPage> pageList = new ArrayList();
        // generate 5 items, relative to currentPage: -2, -1, 0, +1, +2
        Util.generatePageLinks(pageList, page, pages, itemsPerPage, pageLinksAside);
        NavigationPage prev = page == 0 ? new NavigationPage(0, false) : new NavigationPage(page-1, true);
        NavigationPage next = page == pages-1 ? new NavigationPage(0, false) : new NavigationPage(page+1, true);
        
        model.addAttribute("pages", new NavigationPageListView(pageList, null, prev, next, pages, countAll));
        return "index";
    } 
	
	@GetMapping("/view")
    public String view(@RequestParam long id, Principal principal, Model model) {
		log.debug("/view for note #{} by user {}", id, principal.getName());
        return noteRepository.findByIdAndAuthor(id, principal.getName())
        	.map(item -> {
                model.addAttribute("edititem", item);
                return "view";
        	})
        	.orElse("redirect:/");
    }
	
	@GetMapping("/edit")
    public String edit(@RequestParam long id, Principal principal, Model model) {
		log.debug("/edit for note #" + id);
		return noteRepository.findByIdAndAuthor(id, principal.getName())
        	.map(item -> {
                model.addAttribute("edititem", item);
                //log.info(item.getHtml());
                return "edit";
        	})
        	.orElse("redirect:/");
    }

    @PostMapping("/add")
    public String addNote(@ModelAttribute Note requestItem, Principal principal) {
        Note item = new Note(principal.getName(), requestItem.getCategory().trim(), false, requestItem.getName(), requestItem.getText(), requestItem.getHtmlSnippet(), requestItem.getHtml());
        log.debug("/add:" + item.getName());
        noteRepository.save(item);
        return "redirect:/";
    }

    @GetMapping("/rm")
    public String rmNote(@RequestParam long id, Principal principal) {
        noteRepository.deleteByIdAndAuthor(id, principal.getName());
        return "redirect:/";
    }
    
    @PostMapping("/update")
    public String updateNote(@ModelAttribute Note requestItem, Principal principal) {
        Note item = new Note(principal.getName(), requestItem.getCategory().trim(), requestItem.getShared(), requestItem.getName(), requestItem.getText(), requestItem.getHtmlSnippet(), requestItem.getHtml());
        item.setId(requestItem.getId());
        log.debug("/update:  " + item.getId());
        noteRepository.save(item);
        return "redirect:/";
    }

    @GetMapping("/add")
    public String add(Model model) {
		log.debug("/add");
        model.addAttribute("newitem", new Note());
        return "add";
    }

    @GetMapping("/searchForm")
    public String searchForm(Model model) {
		log.debug("/searchForm");
        model.addAttribute("searchForm", new SearchForm());
        return "searchForm";
    }

    @GetMapping("/search")
    public String search(@ModelAttribute SearchForm searchForm, @RequestParam Map<String,String> allRequestParams, 
    		Principal principal, @PageableDefault(value = itemsPerPage) Pageable pageable0, Model model) {
		log.debug("/search form processing: {}", searchForm);
		//http://localhost:8081/search?keyword=&category=cat+abc&page=1
		log.info("search by KEYWORD:{}", allRequestParams.get("keyword"));
		log.info("search by CAT:{}", allRequestParams.get("category"));

        Note searchCriteria = new Note();
        searchCriteria.setText(allRequestParams.get("keyword"));
        searchCriteria.setCategory(allRequestParams.get("category"));

		// we ignore potential query parameter to override page size
		Pageable pageable = PageRequest.of(pageable0.getPageNumber(), itemsPerPage);
		Page<Note> notesPage = null;
		
		if (searchCriteria.getCategory() != null && ! "".equals(searchCriteria.getCategory().trim())
				&& searchCriteria.getText() != null && ! "".equals(searchCriteria.getText().trim())) {
        	// try search by keyword and category combined
        	notesPage = noteRepository.findByAuthorAndCategoryAndTextIgnoreCase(principal.getName(), String.format("%%%s%%", searchForm.getKeyword()), searchForm.getCategory().trim(), pageable);
		} else if (searchCriteria.getText() != null && ! "".equals(searchCriteria.getText().trim())) {
        	// try search by keyword
        	notesPage = noteRepository.findByAuthorAndTextIgnoreCase(principal.getName(), String.format("%%%s%%", searchForm.getKeyword()), pageable);
        } else if (searchCriteria.getCategory() != null && ! "".equals(searchCriteria.getCategory().trim())) {
        	// try search by category
        	notesPage = noteRepository.findByAuthorAndCategoryIgnoreCase(principal.getName(), searchForm.getCategory().trim(), pageable);
        }
		
        if (notesPage == null || notesPage.isEmpty()) {
    		model.addAttribute("pages", new NavigationPageListView(null, searchCriteria, null, null, 0, 0));
            return "search";
        }
		
		long countAll = notesPage.getTotalElements();
		int pages = notesPage.getTotalPages();
		
        model.addAttribute("items", new NotesListView(notesPage.toList()));

        ArrayList<NavigationPage> pageList = new ArrayList<>();
        int currentPage = pageable0.getPageNumber();
        // generate 5 items, relative to currentPage: -2, -1, 0, +1, +2
        Util.generatePageLinks(pageList, currentPage, pages, pageable.getPageSize(), pageLinksAside);
        NavigationPage prev = currentPage == 0 ? new NavigationPage(0, false) : new NavigationPage(currentPage-1, true);
        NavigationPage next = currentPage == pages-1 ? new NavigationPage(0, false) : new NavigationPage(currentPage+1, true);
        
		model.addAttribute("pages", new NavigationPageListView(pageList, searchCriteria, prev, next, pages, countAll));
        return "search";
    }
	
	@GetMapping("export")
    public String export(@RequestParam(defaultValue="0") int page, Principal principal, Model model) {
		log.debug("/export, page {}", page);
		long countAll = noteRepository.countByAuthor(principal.getName());
		int pages = (int) (countAll % itemsPerPage == 0 ? countAll / itemsPerPage : countAll / itemsPerPage + 1);
		
        ArrayList<Note> noteList = (ArrayList<Note>) noteRepository.findByAuthorOrderByDtModifiedDesc(principal.getName(), PageRequest.of(page, itemsPerPage));
        model.addAttribute("items", new NotesListView(noteList));

        ArrayList<NavigationPage> pageList = new ArrayList();
        // generate 5 items, relative to currentPage: -2, -1, 0, +1, +2
        Util.generatePageLinks(pageList, page, pages, itemsPerPage, pageLinksAside);
        NavigationPage prev = page == 0 ? new NavigationPage(0, false) : new NavigationPage(page-1, true);
        NavigationPage next = page == pages-1 ? new NavigationPage(0, false) : new NavigationPage(page+1, true);
        
        model.addAttribute("pages", new NavigationPageListView(pageList, null, prev, next, pages, countAll));
        return "export";
    } 
	
    @PostMapping("/export")
    public ResponseEntity export(@ModelAttribute NotesListView requestItems) {
		log.debug("/export post");

		List<String> pdfFilesPaths = new ArrayList<String>();
        for (Note n : requestItems.getNoteList() ) {
        	if (n.getChecked()) {
        		log.info("NOTE EXPORT: {}", n);
        		noteRepository.findById(n.getId()).ifPresent(n0 -> {
        			String fileName = Util.exportNote(n0, exportDir);
        			if (fileName != null) {
        				pdfFilesPaths.add(fileName);
        			}
        		});
        	}        	
        }
        
        if (pdfFilesPaths.isEmpty()) {        	
        	HttpHeaders headers = new HttpHeaders();
        	headers.add("Location", "/export");    
        	return new ResponseEntity<String>(headers, HttpStatus.FOUND);
        }
        
        String zipFilePath = Util.zipFiles(pdfFilesPaths, exportDir + "tmp_export.zip");
        
        // cleanup pdf files
        for (String ff: pdfFilesPaths) {
        	try {
				Files.delete(Paths.get(ff));
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        
        // return ZIP file with PDFs inside
		File zipFile = new File(zipFilePath);
		HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=notes.zip");
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        Path path = Paths.get(zipFile.getAbsolutePath());
		try {
			ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
	        ResponseEntity<Resource> re = ResponseEntity.ok()
	                .headers(header)
	                .contentLength(zipFile.length())
	                .contentType(MediaType.APPLICATION_OCTET_STREAM)
	                .body(resource);
	        return re;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
    	return ResponseEntity.status(501)
		.body("<html>Error</html");
    }

    @GetMapping("/categories")
    @ResponseBody
    public List<String> categories(@RequestParam String term, Principal principal, Model model) {
        log.debug("/categories like {} by user {}", term, principal.getName());
        if (term.length() > 10) {
            term = term.substring(0,10);
        }
        return noteRepository.findCategories(principal.getName(), "%"+term.toUpperCase()+"%");
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @ExceptionHandler({ Exception.class })
    public ModelAndView handleException(Exception e) {
        log.error("Error!", e);
        ModelAndView mav = new ModelAndView();
        // mav.addObject("exception", e.getMessage());
        // mav.addObject("url", httpServletRequest.getRequestURL());
        mav.setViewName("error");
        return mav;
    }

}
