package acme.features.inventor.chimpum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.chimpum.Pelfo;
import acme.entities.moneyExchange.MoneyExchange;
import acme.features.authenticated.moneyExchange.AuthenticatedMoneyExchangePerformService;
import acme.framework.components.models.Model;
import acme.framework.controllers.Request;
import acme.framework.datatypes.Money;
import acme.framework.services.AbstractShowService;
import acme.roles.Inventor;

@Service
public class InventorPelfoShowService implements AbstractShowService<Inventor, Pelfo> {
	
	@Autowired
	protected InventorPelfoRepository pelfoRepository;

	@Override
	public boolean authorise(final Request<Pelfo> request) {
		assert request != null;

		boolean res;
		int id;
		Pelfo pelfo;
		
		id = request.getModel().getInteger("id");
		pelfo = this.pelfoRepository.findPelfoById(id);
		res = pelfo != null && pelfo.getItem().getInventor().getId() == request.getPrincipal().getActiveRoleId();
		
		return res;
	}

	@Override
	public Pelfo findOne(final Request<Pelfo> request) {
		assert request != null;
		
		Pelfo res;
		int id;

		id = request.getModel().getInteger("id");
		res = this.pelfoRepository.findPelfoById(id);

		return res;
		
	}

	@Override
	public void unbind(final Request<Pelfo> request, final Pelfo entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		
		final Money newBudget = this.moneyExchangePatronages(entity);
		model.setAttribute("newBudget", newBudget);
		request.unbind(entity, model, "code","creationMoment","name","summary","startPeriod","endPeriod","ration","additionalInfo");
	}
	
	
	public Money moneyExchangePatronages(final Pelfo p) {
		final String itemCurrency = p.getRation().getCurrency();
	
		final AuthenticatedMoneyExchangePerformService moneyExchange = new AuthenticatedMoneyExchangePerformService();
		final String systemCurrency = this.pelfoRepository.systemConfiguration().getSystemCurrency();
		final Double conversionAmount;
		
		if(!systemCurrency.equals(itemCurrency)) {
			MoneyExchange conversion;
			conversion = moneyExchange.computeMoneyExchange(p.getRation(), systemCurrency);
			conversionAmount = conversion.getTarget().getAmount();	
		}
		else {
			conversionAmount = p.getRation().getAmount();
		}
		
		final Money newBudget = new Money();
		newBudget.setAmount(conversionAmount);
		newBudget.setCurrency(systemCurrency);
		
		return newBudget;
	}

}
