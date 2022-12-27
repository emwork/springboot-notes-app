package ca.skylinedata;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import ca.skylinedata.controller.NoteController;
import ca.skylinedata.repository.NoteRepository;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NoteController.class)
class NotesMvcTests {

	@Autowired
	MockMvc mvc;
	
	@MockBean
	NoteRepository noteRepository;
	
	
	@Test
	void testLoginRedirect() throws Exception {
		mvc.perform(get("/"))
		.andExpect(status().isFound())
		.andExpect(redirectedUrl("http://localhost/login"));
	}

	@Test
	void testUnprotectedUrl() throws Exception {
		mvc.perform(get("/about"))
		.andExpect(status().isOk())
		.andExpect(view().name("about"))
		.andExpect(content().string(containsString("<title>About</title>")));
	}

}
