package ca.skylinedata;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import ca.skylinedata.model.NavigationPage;
import ca.skylinedata.util.Util;

class TestUtils {
	
	@Test
	void testNavigationPage() throws Exception {
		System.out.println("Start pagination test");

		List<NavigationPage> pageList = new ArrayList<>();
		Util.generatePageLinks(pageList, 3, 7, 5, 2);
		// 5 links: 2,3,<-4->,5,6
		assertEquals(5, pageList.size());
		// first link is page 2
		assertEquals("2", pageList.get(0).getText());
		assertEquals(true, pageList.get(0).isWithHref());
		// current page 4 has no link 
		assertEquals("4", pageList.get(2).getText());
		assertEquals(false, pageList.get(2).isWithHref());
		// last link is page 6
		assertEquals("6", pageList.get(4).getText());
		assertEquals(true, pageList.get(4).isWithHref());
	}


}
