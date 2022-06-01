
package acme.features.inventor.chimpum;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.chimpum.Chimpum;
import acme.entities.item.Item;
import acme.entities.moneyExchange.MoneyExchange;
import acme.features.authenticated.moneyExchange.AuthenticatedMoneyExchangePerformService;
import acme.framework.components.models.Model;
import acme.framework.controllers.Request;
import acme.framework.datatypes.Money;
import acme.framework.services.AbstractListService;
import acme.roles.Inventor;

@Service
public class InventorChimpumListService implements AbstractListService<Inventor, Chimpum> {

	@Autowired
	protected InventorChimpumRepository chimpumRepository;


	@Override
	public boolean authorise(final Request<Chimpum> request) {
		assert request != null;
		return true;
	}

	@Override
	public Collection<Chimpum> findMany(final Request<Chimpum> request) {

		assert request != null;

		final int inventorId = request.getPrincipal().getActiveRoleId();
		final Collection<Item> items = this.chimpumRepository.findItemByInventorId(inventorId);
		final Collection<Chimpum> chimpums = new HashSet<Chimpum>();
		for (final Item i : items) {
			final Chimpum chimpum = this.chimpumRepository.findChimpumByItemId(i.getId());
			if (chimpum != null) {
				chimpums.add(chimpum);
			}

		}
		return chimpums;

	}

	@Override
	public void unbind(final Request<Chimpum> request, final Chimpum entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		
		
		final Money newBudget = this.moneyExchangePatronages(entity);
		model.setAttribute("newBudget", newBudget);
		request.unbind(entity, model, "code", "creationMoment", "tittle", "budget", "item.code");

	}

	public Money moneyExchangePatronages(final Chimpum p) {
		final String itemCurrency = p.getBudget().getCurrency();

		final AuthenticatedMoneyExchangePerformService moneyExchange = new AuthenticatedMoneyExchangePerformService();
		final String systemCurrency = this.chimpumRepository.systemConfiguration().getSystemCurrency();
		final Double conversionAmount;

		if (!systemCurrency.equals(itemCurrency)) {
			MoneyExchange conversion;
			conversion = moneyExchange.computeMoneyExchange(p.getBudget(), systemCurrency);
			conversionAmount = conversion.getTarget().getAmount();
		} else {
			conversionAmount = p.getBudget().getAmount();
		}

		final Money newBudget = new Money();
		newBudget.setAmount(conversionAmount);
		newBudget.setCurrency(systemCurrency);

		return newBudget;
	}

}
