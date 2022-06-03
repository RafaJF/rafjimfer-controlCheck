
package acme.entities.chimpum;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import acme.entities.item.Item;
import acme.framework.datatypes.Money;
import acme.framework.entities.AbstractEntity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Pelfo extends AbstractEntity {

	protected static final long	serialVersionUID	= 1L;

	@NotBlank
	@Pattern(regexp = "^\\d{6}:yy:mm:dd$")
	protected String			code;

	@NotNull
	@Past
	@Temporal(TemporalType.TIMESTAMP)
	protected Date				creationMoment;

	@NotBlank
	@Length(min = 0, max = 100)
	protected String			name;

	@NotBlank
	@Length(min = 0, max = 255)
	protected String			summary;

	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	protected Date				startPeriod;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	protected Date				endPeriod;
	
	@NotNull
	@Valid
	protected Money				ration;

	@URL
	protected String			additionalInfo;

	//Control Check -----------------------------------------------------------

	@OneToOne(optional = false)
	@Valid
	protected Item				item;

}
