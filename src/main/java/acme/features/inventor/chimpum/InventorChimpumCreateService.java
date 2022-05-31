package acme.features.inventor.chimpum;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.chimpum.Chimpum;
import acme.entities.item.Item;
import acme.entities.systemConfiguration.SystemConfiguration;
import acme.framework.components.models.Model;
import acme.framework.controllers.Errors;
import acme.framework.controllers.Request;
import acme.framework.services.AbstractCreateService;
import acme.roles.Inventor;
import spamDetector.SpamDetector;

@Service
public class InventorChimpumCreateService implements AbstractCreateService<Inventor, Chimpum>{

	@Autowired
	protected InventorChimpumRepository chimpumRepository;

	@Override
	public boolean authorise(final Request<Chimpum> request) {
		assert request != null;
		return true;
	}

	@Override
	public void bind(final Request<Chimpum> request, final Chimpum entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		
		final String code;
		
		String itemCode;
		Item item;
		
		itemCode = request.getModel().getString("itemCode");
		item = this.chimpumRepository.findItemByCode(itemCode);
		entity.setItem(item);
		
		
		final Date creationMoment = request.getModel().getDate("creationMoment");
		final SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd");
		code = format.format(creationMoment.getTime());
		entity.setCode(code);		
		
		request.bind(entity, errors,"creationMoment","tittle","description","startPeriod","endPeriod","budget","link");
		
	}

	@Override
	public void unbind(final Request<Chimpum> request, final Chimpum entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		
		final Collection<Item> itemsByInventor = this.chimpumRepository.findItemByInventorId(request.getPrincipal().getActiveRoleId());
		final Collection<Item> itemsByInventorPublished = new HashSet<Item>();
		
		for(final Item i: itemsByInventor) {
			final Item item = this.chimpumRepository.findItemChimpumByItemCode(i.getCode());
			if(i.isPublished() && item == null) {
				itemsByInventorPublished.add(i);
			}
		}
		model.setAttribute("itemsByInventorPublished", itemsByInventorPublished);
		request.unbind(entity, model,"creationMoment","tittle","description","startPeriod","endPeriod","budget","link","item.code");
		
		
		
	}

	@Override
	public Chimpum instantiate(final Request<Chimpum> request) {
		assert request != null;
		
		Chimpum res;
		Item item;
		
		item = new Item();
		
		Date creationMoment;
		creationMoment = Calendar.getInstance().getTime();
		
		
		
		res = new Chimpum();
		res.setCreationMoment(creationMoment);
		res.setItem(item);
		
		return res;
		
		
	}

	@Override
	public void validate(final Request<Chimpum> request, final Chimpum entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		
		if(!errors.hasErrors("link") && (!entity.getLink().isEmpty())) {
			boolean isUrl;
			isUrl = (entity.getLink().startsWith("http") || entity.getLink().startsWith("www")) && entity.getLink().contains(".");
			errors.state(request, isUrl, "link", "inventor.chimpum.form.error.link");
		
		}
		if(!errors.hasErrors("budget")) {
			final Double amount = entity.getBudget().getAmount();
			
			final String[] acceptedCurrencies = this.chimpumRepository.findAcceptedCurrencies().split(",");
			final List<String> aceptedCurrenciesList = new ArrayList<String>();
			for(final String i: acceptedCurrencies) {
				aceptedCurrenciesList.add(i);
			}
			
			final boolean validCurrency = aceptedCurrenciesList.contains(entity.getBudget().getCurrency());
			
			errors.state(request, amount>0. , "budget", "inventor.chimpum.form.error.budget-amount-negative-or-zero");
			errors.state(request, validCurrency, "budget", "inventor.chimpum.form.error.budget-currency-invalid");
		}
		if(!errors.hasErrors("startPeriod")) {
			
			final Date startPeriod = entity.getStartPeriod();
			final Date creationMoment = entity.getCreationMoment();
			
			final Date startPeriodMin = DateUtils.addMonths(creationMoment, 1);
			
			errors.state(request, startPeriod.after(startPeriodMin), "startPeriod", "inventor.chimpum.form.error.startPeriod-must-be-a-month-after-creationMoment");

		}
		if(!errors.hasErrors("endPeriod")) {
			
			final Date starDate = entity.getStartPeriod();
			
			final Date endPeriodMin = DateUtils.addWeeks(starDate, 1);
			final Date endPeriodMinDay = DateUtils.addDays(endPeriodMin, -1);
			final Date endPeriod = entity.getEndPeriod();

			errors.state(request, endPeriod.after(endPeriodMinDay), "endPeriod", "inventor.chimpum.form.error.endPeriod-must-be-a-week-after-startPeriod");

		}
		if(!errors.hasErrors("tittle")) {
			final boolean res;
			final SystemConfiguration systemConfiguration = this.chimpumRepository.systemConfiguration();
			final String StrongES = systemConfiguration.getStrongSpamTermsEn();
			final String StrongEN = systemConfiguration.getStrongSpamTermsEn();
			final String WeakES = systemConfiguration.getWeakSpamTermsEs();
			final String WeakEN = systemConfiguration.getWeakSpamTermsEn();

			final double StrongT = systemConfiguration.getStrongThreshold();
			final double WeakT = systemConfiguration.getWeakThreshold();

			res = SpamDetector.spamDetector(entity.getTittle(),StrongES,StrongEN,WeakES,WeakEN,StrongT,WeakT);

			errors.state(request, res, "tittle", "alert-message.form.spam");
		}
		if(!errors.hasErrors("description")) {
			final boolean res;
			final SystemConfiguration systemConfiguration = this.chimpumRepository.systemConfiguration();
			final String StrongES = systemConfiguration.getStrongSpamTermsEn();
			final String StrongEN = systemConfiguration.getStrongSpamTermsEn();
			final String WeakES = systemConfiguration.getWeakSpamTermsEs();
			final String WeakEN = systemConfiguration.getWeakSpamTermsEn();

			final double StrongT = systemConfiguration.getStrongThreshold();
			final double WeakT = systemConfiguration.getWeakThreshold();

			res = SpamDetector.spamDetector(entity.getDescription(),StrongES,StrongEN,WeakES,WeakEN,StrongT,WeakT);

			errors.state(request, res, "description", "alert-message.form.spam");
		}
		
	}

	@Override
	public void create(final Request<Chimpum> request, final Chimpum entity) {
		assert request != null;
		assert entity != null;
		
		this.chimpumRepository.save(entity);
		
	}
	
}
