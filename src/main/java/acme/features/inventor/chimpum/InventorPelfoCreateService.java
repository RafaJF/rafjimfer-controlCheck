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

import acme.entities.chimpum.Pelfo;
import acme.entities.item.Item;
import acme.entities.systemConfiguration.SystemConfiguration;
import acme.framework.components.models.Model;
import acme.framework.controllers.Errors;
import acme.framework.controllers.Request;
import acme.framework.services.AbstractCreateService;
import acme.roles.Inventor;
import spamDetector.SpamDetector;

@Service
public class InventorPelfoCreateService implements AbstractCreateService<Inventor, Pelfo>{

	@Autowired
	protected InventorPelfoRepository pelfoRepository;

	@Override
	public boolean authorise(final Request<Pelfo> request) {
		assert request != null;
		return true;
	}

	@Override
	public void bind(final Request<Pelfo> request, final Pelfo entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		
		final String code;
		
		String itemCode;
		Item item;
		
		itemCode = request.getModel().getString("itemCode");
		item = this.pelfoRepository.findItemByCode(itemCode);
		entity.setItem(item);
		
		
		final Date creationMoment = request.getModel().getDate("creationMoment");
		final SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd");
		code = format.format(creationMoment.getTime());
		entity.setCode(code);		
		
		request.bind(entity, errors,"creationMoment","name","summary","startPeriod","endPeriod","ration","additionalInfo");
		
	}

	@Override
	public void unbind(final Request<Pelfo> request, final Pelfo entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		
		final Collection<Item> itemsByInventor = this.pelfoRepository.findItemByInventorId(request.getPrincipal().getActiveRoleId());
		final Collection<Item> itemsByInventorPublished = new HashSet<Item>();
		
		for(final Item i: itemsByInventor) {
			final Item item = this.pelfoRepository.findItemPelfoByItemCode(i.getCode());
			if(i.isPublished() && item == null) {
				itemsByInventorPublished.add(i);
			}
		}
		model.setAttribute("toolsByInventorPublished", itemsByInventorPublished);
		request.unbind(entity, model,"creationMoment","name","summary","startPeriod","endPeriod","ration","additionalInfo","item.code");
		
		
		
	}

	@Override
	public Pelfo instantiate(final Request<Pelfo> request) {
		assert request != null;
		
		Pelfo res;
		Item item;
		
		item = new Item();
		
		Date creationMoment;
		creationMoment = Calendar.getInstance().getTime();
		
		
		
		res = new Pelfo();
		res.setCreationMoment(creationMoment);
		res.setItem(item);
		
		return res;
		
		
	}

	@Override
	public void validate(final Request<Pelfo> request, final Pelfo entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		
		if(!errors.hasErrors("additionalInfo") && (!entity.getAdditionalInfo().isEmpty())) {
			boolean isUrl;
			isUrl = (entity.getAdditionalInfo().startsWith("http") || entity.getAdditionalInfo().startsWith("www")) && entity.getAdditionalInfo().contains(".");
			errors.state(request, isUrl, "link", "inventor.pelfo.form.error.link");
		
		}
		if(!errors.hasErrors("ration")) {
			final Double amount = entity.getRation().getAmount();
			
			final String[] acceptedCurrencies = this.pelfoRepository.findAcceptedCurrencies().split(",");
			final List<String> aceptedCurrenciesList = new ArrayList<String>();
			for(final String i: acceptedCurrencies) {
				aceptedCurrenciesList.add(i);
			}
			
			final boolean validCurrency = aceptedCurrenciesList.contains(entity.getRation().getCurrency());
			
			errors.state(request, amount>0. , "ration", "inventor.pelfo.form.error.budget-amount-negative-or-zero");
			errors.state(request, validCurrency, "ration", "inventor.pelfo.form.error.budget-currency-invalid");
		}
		if(!errors.hasErrors("startPeriod")) {
			
			final Date startPeriod = entity.getStartPeriod();
			final Date creationMoment = entity.getCreationMoment();
			
			final Date startPeriodMin = DateUtils.addMonths(creationMoment, 1);
			
			errors.state(request, startPeriod.after(startPeriodMin), "startPeriod", "inventor.pelfo.form.error.startPeriod-must-be-a-month-after-creationMoment");

		}
		if(!errors.hasErrors("endPeriod")) {
			
			final Date starDate = entity.getStartPeriod();
			
			final Date endPeriodMin = DateUtils.addWeeks(starDate, 1);
			final Date endPeriodMinDay = DateUtils.addDays(endPeriodMin, -1);
			final Date endPeriod = entity.getEndPeriod();

			errors.state(request, endPeriod.after(endPeriodMinDay), "endPeriod", "inventor.pelfo.form.error.endPeriod-must-be-a-week-after-startPeriod");

		}
		if(!errors.hasErrors("name")) {
			final boolean res;
			final SystemConfiguration systemConfiguration = this.pelfoRepository.systemConfiguration();
			final String StrongES = systemConfiguration.getStrongSpamTermsEn();
			final String StrongEN = systemConfiguration.getStrongSpamTermsEn();
			final String WeakES = systemConfiguration.getWeakSpamTermsEs();
			final String WeakEN = systemConfiguration.getWeakSpamTermsEn();

			final double StrongT = systemConfiguration.getStrongThreshold();
			final double WeakT = systemConfiguration.getWeakThreshold();

			res = SpamDetector.spamDetector(entity.getName(),StrongES,StrongEN,WeakES,WeakEN,StrongT,WeakT);

			errors.state(request, res, "name", "alert-message.form.spam");
		}
		if(!errors.hasErrors("summary")) {
			final boolean res;
			final SystemConfiguration systemConfiguration = this.pelfoRepository.systemConfiguration();
			final String StrongES = systemConfiguration.getStrongSpamTermsEn();
			final String StrongEN = systemConfiguration.getStrongSpamTermsEn();
			final String WeakES = systemConfiguration.getWeakSpamTermsEs();
			final String WeakEN = systemConfiguration.getWeakSpamTermsEn();

			final double StrongT = systemConfiguration.getStrongThreshold();
			final double WeakT = systemConfiguration.getWeakThreshold();

			res = SpamDetector.spamDetector(entity.getSummary(),StrongES,StrongEN,WeakES,WeakEN,StrongT,WeakT);

			errors.state(request, res, "summary", "alert-message.form.spam");
		}
		
	}

	@Override
	public void create(final Request<Pelfo> request, final Pelfo entity) {
		assert request != null;
		assert entity != null;
		
		this.pelfoRepository.save(entity);
		
	}
	
}
