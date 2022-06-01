package acme.features.inventor.chimpum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.chimpum.Chimpum;
import acme.entities.moneyExchange.MoneyExchange;
import acme.features.authenticated.moneyExchange.AuthenticatedMoneyExchangePerformService;
import acme.framework.components.models.Model;
import acme.framework.controllers.Request;
import acme.framework.datatypes.Money;
import acme.framework.services.AbstractShowService;
import acme.roles.Inventor;

@Service
public class InventorChimpumShowService implements AbstractShowService<Inventor, Chimpum> {
	
	@Autowired
	protected InventorChimpumRepository chimpumRepository;

	@Override
	public boolean authorise(final Request<Chimpum> request) {
		assert request != null;

		boolean res;
		int id;
		Chimpum chimpum;
		
		id = request.getModel().getInteger("id");
		chimpum = this.chimpumRepository.findChimpumById(id);
		res = chimpum != null && chimpum.getItem().getInventor().getId() == request.getPrincipal().getActiveRoleId();
		
		return res;
	}

	@Override
	public Chimpum findOne(final Request<Chimpum> request) {
		assert request != null;
		
		Chimpum res;
		int id;

		id = request.getModel().getInteger("id");
		res = this.chimpumRepository.findChimpumById(id);

		return res;
		
	}

	@Override
	public void unbind(final Request<Chimpum> request, final Chimpum entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		
		final Money newBudget = this.moneyExchangePatronages(entity);
		model.setAttribute("newBudget", newBudget);
		request.unbind(entity, model, "code","creationMoment","tittle","description","startPeriod","endPeriod","budget","link");
	}
	
	
	public Money moneyExchangePatronages(final Chimpum p) {
		final String itemCurrency = p.getBudget().getCurrency();
	
		final AuthenticatedMoneyExchangePerformService moneyExchange = new AuthenticatedMoneyExchangePerformService();
		final String systemCurrency = this.chimpumRepository.systemConfiguration().getSystemCurrency();
		final Double conversionAmount;
		
		if(!systemCurrency.equals(itemCurrency)) {
			MoneyExchange conversion;
			conversion = moneyExchange.computeMoneyExchange(p.getBudget(), systemCurrency);
			conversionAmount = conversion.getTarget().getAmount();	
		}
		else {
			conversionAmount = p.getBudget().getAmount();
		}
		
		final Money newBudget = new Money();
		newBudget.setAmount(conversionAmount);
		newBudget.setCurrency(systemCurrency);
		
		return newBudget;
	}

}
