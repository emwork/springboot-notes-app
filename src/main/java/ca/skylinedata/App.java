package ca.skylinedata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import ca.skylinedata.repository.NoteRepository;
import ca.skylinedata.util.Util;

@SpringBootApplication
public class App implements CommandLineRunner {

	@Autowired
	private NoteRepository noteRepository;

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Util.populateSampleNotes(noteRepository);
	}

}
