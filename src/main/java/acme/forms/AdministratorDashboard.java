package acme.forms;

import java.io.Serializable;
import java.util.Map;

import org.springframework.data.util.Pair;

import acme.entities.patronage.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministratorDashboard implements Serializable{
	
	// Serialisation identifier -----------------------------------------------

	protected static final long	serialVersionUID	= 1L;

	// Attributes -------------------------------------------------------------

    int totalNumberOfComponents;
    int totalNumberOfTools;
    Map<Status, Integer> totalNumberOfPatronagesByStatus;
    
    Map<Pair<String, String>, Double> averageRetailPriceOfComponentsByTechnologyAndCurrency;
    Map<Pair<String, String>, Double> deviationRetailPriceOfComponentsByTechnologyAndCurrency;
    Map<Pair<String, String>, Double> minimumRetailPriceOfComponentsByTechnologyAndCurrency;
    Map<Pair<String, String>, Double> maximumRetailPriceOfComponentsByTechnologyAndCurrency;
    
    Map<String, Double> averageRetailPriceOfToolsByCurrency;
    Map<String, Double> deviationRetailPriceOfToolsByCurrency;
    Map<String, Double> minimumRetailPriceOfToolsByCurrency;
    Map<String, Double> maximumRetailPriceOfToolsByCurrency;
    
    Map<Pair<Status, String>, Double> averageBudgetPatronagesByStatus;
    Map<Pair<Status, String>, Double> deviationBudgetPatronagesByStatus;
    Map<Pair<Status, String>, Double> minimumBudgetPatronagesByStatus;
    Map<Pair<Status, String>, Double> maximumBudgetPatronagesByStatus;
    
   
    
    //-----------CONTROL CHECK------------------------------------------------
    
    String ratioToolWithPelfo;
    
    Map<String,Double>  averageRationPelfoByCurrency;
    Map<String,Double> deviationRationPelfoByCurrency;
    Map<String,Double> minRationPelfoByCurrency;
    Map<String, Double> maxRationPelfoByCurrency;

	// Derived attributes -----------------------------------------------------

	// Relationships ----------------------------------------------------------

}
