
package acme.features.inventor.chimpum;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.chimpum.Pelfo;
import acme.entities.item.Item;
import acme.entities.moneyExchange.MoneyExchange;
import acme.features.authenticated.moneyExchange.AuthenticatedMoneyExchangePerformService;
import acme.framework.components.models.Model;
import acme.framework.controllers.Request;
import acme.framework.datatypes.Money;
import acme.framework.services.AbstractListService;
import acme.roles.Inventor;

@Service
public class InventorPelfoListService implements AbstractListService<Inventor, Pelfo> {

	@Autowired
	protected InventorPelfoRepository pelfoRepository;


	@Override
	public boolean authorise(final Request<Pelfo> request) {
		assert request != null;
		return true;
	}

	@Override
	public Collection<Pelfo> findMany(final Request<Pelfo> request) {

		assert request != null;

		final int inventorId = request.getPrincipal().getActiveRoleId();
		final Collection<Item> items = this.pelfoRepository.findItemByInventorId(inventorId);
		final Collection<Pelfo> pelfos = new HashSet<Pelfo>();
		for (final Item i : items) {
			final Pelfo pelfo = this.pelfoRepository.findPelfoById(i.getId());
			if (pelfo != null) {
				pelfos.add(pelfo);
			}

		}
		return pelfos;

	}

	@Override
	public void unbind(final Request<Pelfo> request, final Pelfo entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		
		
		final Money newBudget = this.moneyExchangePatronages(entity);
		model.setAttribute("newBudget", newBudget);
		request.unbind(entity, model, "code", "creationMoment", "name", "ration", "item.code");

	}

	public Money moneyExchangePatronages(final Pelfo p) {
		final String itemCurrency = p.getRation().getCurrency();

		final AuthenticatedMoneyExchangePerformService moneyExchange = new AuthenticatedMoneyExchangePerformService();
		final String systemCurrency = this.pelfoRepository.systemConfiguration().getSystemCurrency();
		final Double conversionAmount;

		if (!systemCurrency.equals(itemCurrency)) {
			MoneyExchange conversion;
			conversion = moneyExchange.computeMoneyExchange(p.getRation(), systemCurrency);
			conversionAmount = conversion.getTarget().getAmount();
		} else {
			conversionAmount = p.getRation().getAmount();
		}

		final Money newBudget = new Money();
		newBudget.setAmount(conversionAmount);
		newBudget.setCurrency(systemCurrency);

		return newBudget;
	}

}
