package acme.testing.inventor.chimpum;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import acme.testing.TestHarness;

public class InventorDeleteChimpumTest extends TestHarness {
	
	@Order(10)
	@CsvFileSource(resources = "/inventor/chimpum/delete-positive.csv", encoding = "utf-8", numLinesToSkip = 1)
	@ParameterizedTest
	public void positiveTest(final int recordIndex,final String tittle, final String description, final String startPeriod, 
		final String endPeriod,final String budget, final String link) {
		
		super.signIn("inventor2", "inventor2");
		super.clickOnMenu("Inventor", "List chimpums");
		
		super.checkListingExists();
		
		super.clickOnListingRecord(recordIndex);
		super.checkFormExists();
		super.checkInputBoxHasValue("tittle", tittle);
		super.checkInputBoxHasValue("description", description);
		super.checkInputBoxHasValue("startPeriod", startPeriod);
		super.checkInputBoxHasValue("endPeriod", endPeriod);
		super.checkInputBoxHasValue("budget", budget);
		super.checkInputBoxHasValue("link", link);
		super.clickOnSubmit("Delete");
		super.checkNotErrorsExist();
		
		super.signOut();
		
		
		
	}
	


}
