package acme.features.inventor.chimpum;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.entities.chimpum.Pelfo;
import acme.entities.item.Item;
import acme.entities.systemConfiguration.SystemConfiguration;
import acme.framework.repositories.AbstractRepository;

@Repository
public interface InventorPelfoRepository extends AbstractRepository{
	
	
	@Query("SELECT i FROM Item i WHERE i.inventor.id = :id and i.itemType = acme.entities.item.ItemType.TOOL")
	Collection<Item> findItemByInventorId(int id);
	
	@Query("SELECT c FROM Pelfo c")
	Collection<Pelfo> findAllPelfo();
	
	@Query("SELECT c.item FROM Pelfo c")
	Collection<Item> findItemByPelfo();
	
	@Query("SELECT c.item FROM Pelfo c where c.item.code = :code")
	Item findItemPelfoByItemCode(String code);
	
	@Query("SELECT c FROM Pelfo c WHERE c.item.id = :id")
	Pelfo findPelfoByItemId(int id);
	
	@Query("SELECT c FROM Pelfo c WHERE c.id = :id")
	Pelfo findPelfoById(int id);
	
	@Query("SELECT ac.acceptedCurrencies from SystemConfiguration ac")
	String findAcceptedCurrencies();
	
	@Query("select systemConfiguration from SystemConfiguration systemConfiguration")
	SystemConfiguration systemConfiguration();
	
	@Query("select i from Item i where i.code = :code")
	Item findItemByCode(String code);
	
	@Query("select i from Item i where i.id = :id")
	Item findItemById(int id);
	
	
}
