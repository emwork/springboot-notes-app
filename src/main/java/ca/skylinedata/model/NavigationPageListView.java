package ca.skylinedata.model;

import java.util.ArrayList;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
public class NavigationPageListView {
	@Valid
	private ArrayList<NavigationPage> pageList = new ArrayList<NavigationPage>();
	private Note searchCriteria;
	
	private NavigationPage prevPage;
	private NavigationPage nextPage;
	private int totalPages;
	private long totalItems;
		
}
