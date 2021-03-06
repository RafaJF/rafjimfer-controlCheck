package acme.features.inventor.chimpum;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.chimpum.Pelfo;
import acme.entities.systemConfiguration.SystemConfiguration;
import acme.framework.components.models.Model;
import acme.framework.controllers.Errors;
import acme.framework.controllers.Request;
import acme.framework.services.AbstractUpdateService;
import acme.roles.Inventor;
import spamDetector.SpamDetector;

@Service
public class InventorPelfoUpdateService implements AbstractUpdateService<Inventor, Pelfo>{

	@Autowired
	protected InventorPelfoRepository pelfoRepository;
	

	
	
	@Override
	public boolean authorise(final Request<Pelfo> request) {
		boolean res;
		int id;
		Pelfo pelfo;
		
		id = request.getModel().getInteger("id");
		pelfo = this.pelfoRepository.findPelfoById(id);
		res = pelfo != null && pelfo.getItem().getInventor().getId() == request.getPrincipal().getActiveRoleId();
		
		return res;
	}

	@Override
	public void bind(final Request<Pelfo> request, final Pelfo entity, final Errors errors) {
		assert request != null;
		assert entity != null;
		assert errors != null;
		
		request.bind(entity, errors, "code","creationMoment","name","summary","startPeriod","endPeriod","ration","additionalInfo");
		
	}

	@Override
	public void unbind(final Request<Pelfo> request, final Pelfo entity, final Model model) {
		assert request != null;
		assert entity != null;
		assert model != null;
		
		request.unbind(entity, model, "code","creationMoment","name","summary","startPeriod","endPeriod","ration","additionalInfo");
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
			
			final Date creationMoment = entity.getCreationMoment();
			
			final Date startPeriodMin = DateUtils.addMonths(creationMoment, 1);

			final Date startPeriod = entity.getStartPeriod();
			
			errors.state(request, startPeriod.after(startPeriodMin), "startPeriod", "inventor.pelfo.form.error.startPeriod-must-be-a-month-after-creationMoment");

		}
		if(!errors.hasErrors("endPeriod")) {
			if(entity.getStartPeriod() != null) {
				final Date starDate = entity.getStartPeriod();
				
				final Date endPeriodMin = DateUtils.addWeeks(starDate, 1);
				final Date endPeriodMinDay = DateUtils.addDays(endPeriodMin, -1);
				final Date endPeriod = entity.getEndPeriod();

				errors.state(request, endPeriod.after(endPeriodMinDay), "endPeriod", "inventor.pelfo.form.error.endPeriod-must-be-a-week-after-startPeriod");

			}
			
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
	public void update(final Request<Pelfo> request, final Pelfo entity) {
		assert request != null;
		assert entity != null;
		
		this.pelfoRepository.save(entity);
		
		
	}

}
