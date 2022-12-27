package ca.skylinedata.model;


import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import lombok.Data;

@Entity
@Data
public class Note {

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String author;
    private String category;
    private String name;
    private String text;
    private String html;
    private String htmlSnippet;
    private LocalDateTime dtModified;
    private Boolean shared; 
    @Transient private Boolean checked; 

    public Note() {}
    
    public Note(String author, String category, Boolean shared, String name, String text, String htmlSnippet, String html) {
        this.author = author;
        this.category = category;
        this.name = name;
        this.text = text;
        this.htmlSnippet = htmlSnippet;
        this.html = html;
        this.shared = shared;
        this.dtModified = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format(
                "Note[id=%d, author='%s', shared='%s', category='%s', name='%s', text='%s', htmlSnippet='%s', html='%s', dtModified='%s']",
                id, author, shared, category, name, text, htmlSnippet, html, dtModified);
    }
}
