/*
 * Friends in Global Health - FGH © 2016
 */
package org.openmrs.module.pharmacyapi.api.templates;

import org.openmrs.Concept;
import org.openmrs.DrugOrder;
import org.openmrs.Encounter;
import org.openmrs.Provider;
import org.openmrs.module.pharmacyapi.api.util.BaseTemplateLoader;

import br.com.six2six.fixturefactory.Fixture;
import br.com.six2six.fixturefactory.Rule;

/**
 * @author Stélio Moiane
 */
public class OrderTemplate implements BaseTemplateLoader {
	
	public static final String DRUG_ORDER = "DRUG_ORDER";
	
	public static final String REVISED = "REVISED";
	
	public static final String HAS_PREVIOUS = "HAS_PREVIOUS";
	
	@Override
	public void load() {
		
		Fixture.of(DrugOrder.class).addTemplate(DRUG_ORDER, new Rule() {
			
			{
				this.add("dosingInstructions", "9d7408af-10e8-11e5-9009-0242ac110012");
				this.add("orderer", this.one(Provider.class, ProviderTemplate.VALID));
				this.add("encounter", this.one(Encounter.class, EncounterTemplate.VALID));
				this.add("dose", 1.0);
				this.add("duration", 1);
				this.add("durationUnits", this.one(Concept.class, ConceptTemplate.WEEKS));
			}
		});
		
		Fixture.of(DrugOrder.class).addTemplate(HAS_PREVIOUS).inherits(DRUG_ORDER, new Rule() {
			
			{
				this.add("previousOrder", this.one(DrugOrder.class, DRUG_ORDER));
				this.add("encounter", this.one(Encounter.class, EncounterTemplate.DISPENSATION));
			}
		});
		
		Fixture.of(DrugOrder.class).addTemplate(REVISED).inherits(DRUG_ORDER, new Rule() {
			
			{
				this.add("previousOrder", this.one(DrugOrder.class, HAS_PREVIOUS));
				this.add("encounter", this.one(Encounter.class, EncounterTemplate.DISPENSATION));
				this.add("concept", this.one(Concept.class, ConceptTemplate.AZT_3TC_NVP));
			}
		});
	}
}
