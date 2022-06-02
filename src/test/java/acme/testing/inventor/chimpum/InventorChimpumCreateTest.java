package acme.testing.inventor.chimpum;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import acme.testing.TestHarness;

public class InventorChimpumCreateTest extends TestHarness{
	
	@ParameterizedTest
	@CsvFileSource(resources = "/inventor/chimpum/create-positive.csv", encoding = "utf-8", numLinesToSkip = 1)
	@Order(10)
	public void positiveTest(final int recordIndex,final String tittle, final String description, final String startPeriod, 
		final String endPeriod,final String budget, final String link) {
		
		super.signIn("inventor2", "inventor2");
		super.clickOnMenu("Inventor", "List chimpums");
		super.checkListingExists();
		super.clickOnButton("Create");
		super.fillInputBoxIn("tittle", tittle);
		super.fillInputBoxIn("description", description);
		super.fillInputBoxIn("startPeriod", startPeriod);
		super.fillInputBoxIn("endPeriod", endPeriod);
		super.fillInputBoxIn("budget", budget);
		super.fillInputBoxIn("link", link);
		
		super.clickOnSubmit("Create");

		super.clickOnMenu("Inventor", "List chimpums");
		super.checkListingExists();
		super.sortListing(2, "desc");
		super.checkColumnHasValue(recordIndex, 2, tittle);
		super.checkColumnHasValue(recordIndex, 3, budget);
		
		
		super.signOut();
		
	}
	
	@ParameterizedTest
	@CsvFileSource(resources = "/inventor/chimpum/create-negative.csv", encoding = "utf-8", numLinesToSkip = 1)
	@Order(10)
	public void negativeTest(final int recordIndex,final String tittle, final String description, final String startPeriod, 
		final String endPeriod,final String budget, final String link) {
		
		super.signIn("inventor2", "inventor2");
		super.clickOnMenu("Inventor", "List chimpums");
		super.checkListingExists();
		super.clickOnButton("Create");
		super.fillInputBoxIn("tittle", tittle);
		super.fillInputBoxIn("description", description);
		super.fillInputBoxIn("startPeriod", startPeriod);
		super.fillInputBoxIn("endPeriod", endPeriod);
		super.fillInputBoxIn("budget", budget);
		super.fillInputBoxIn("link", link);
		
		super.clickOnSubmit("Create");
		super.checkErrorsExist();
		
		super.signOut();
		
	}
	@Test
	@Order(30)
	public void hackingTest() {
		super.navigate("/inventor/chimpum/create");
		super.checkPanicExists();
		
		super.signIn("patron1", "patron1");
		super.navigate("/inventor/chimpum/create");
		super.checkPanicExists();
		
		super.signOut(); 
	}

}
