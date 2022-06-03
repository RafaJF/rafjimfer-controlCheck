package acme.features.inventor.chimpum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import acme.entities.chimpum.Pelfo;
import acme.framework.components.models.Model;
import acme.framework.controllers.Errors;
import acme.framework.controllers.Request;
import acme.framework.services.AbstractDeleteService;
import acme.roles.Inventor;

@Service
public class InventorPelfoDeleteService implements AbstractDeleteService<Inventor, Pelfo>{

	@Autowired
	protected InventorPelfoRepository pelfoRepository;
	
	@Override
	public boolean authorise(final Request<Pelfo> request) {
		assert request != null;
		
		int pelfoId;
		Pelfo pelfo;
		Inventor inventor;
		
		pelfoId = request.getModel().getInteger("id");
		pelfo = this.pelfoRepository.findPelfoById(pelfoId);
		inventor = pelfo.getItem().getInventor();
		return request.isPrincipal(inventor); 
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
		
	}

	@Override
	public void delete(final Request<Pelfo> request, final Pelfo entity) {
		assert request != null;
		assert entity != null;
		
		this.pelfoRepository.delete(entity);
		
	}

}
