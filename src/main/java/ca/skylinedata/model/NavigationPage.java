package ca.skylinedata.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class NavigationPage {

	int id;
	String text;
	boolean withHref;
	
	public NavigationPage(int id, boolean hasHref) {
		this.id=id;
		this.text = ""+(id+1);
		this.withHref=hasHref;
	}

	public NavigationPage(String text, boolean hasHref) {
		this.text = text;
		this.withHref=hasHref;
	}
}
