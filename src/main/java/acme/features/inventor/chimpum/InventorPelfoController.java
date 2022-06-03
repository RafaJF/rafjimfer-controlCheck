package acme.features.inventor.chimpum;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import acme.entities.chimpum.Pelfo;
import acme.framework.controllers.AbstractController;
import acme.roles.Inventor;

@Controller
public class InventorPelfoController  extends AbstractController<Inventor,Pelfo>{
	
	@Autowired
	protected InventorPelfoListService listService;
	
	@Autowired
	protected InventorPelfoShowService pelfoShowService;
	
	@Autowired
	protected InventorPelfoUpdateService pelfoUpdateService;
	
	@Autowired
	protected InventorPelfoDeleteService pelfoDeleteService;
	
	@Autowired
	protected InventorPelfoCreateService pelfoCreateService;
	
	
	@PostConstruct
	protected void initialise() {
		super.addCommand("list", this.listService);
		super.addCommand("show", this.pelfoShowService);
		super.addCommand("update", this.pelfoUpdateService);
		super.addCommand("delete", this.pelfoDeleteService);
		super.addCommand("create", this.pelfoCreateService);
	}

}
