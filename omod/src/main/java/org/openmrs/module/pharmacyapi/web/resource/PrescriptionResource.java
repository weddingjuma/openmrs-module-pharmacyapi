package org.openmrs.module.pharmacyapi.web.resource;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.openmrs.Patient;
import org.openmrs.api.APIException;
import org.openmrs.api.context.Context;
import org.openmrs.module.pharmacyapi.api.common.exception.PharmacyBusinessException;
import org.openmrs.module.pharmacyapi.api.prescription.model.Prescription;
import org.openmrs.module.pharmacyapi.api.prescription.model.PrescriptionItem;
import org.openmrs.module.pharmacyapi.api.prescription.service.PrescriptionService;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.PropertySetter;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.RefRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.api.PageableResult;
import org.openmrs.module.webservices.rest.web.resource.impl.DataDelegatingCrudResource;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.EmptySearchResult;
import org.openmrs.module.webservices.rest.web.resource.impl.NeedsPaging;
import org.openmrs.module.webservices.rest.web.response.ResourceDoesNotSupportOperationException;
import org.openmrs.module.webservices.rest.web.response.ResponseException;
import org.openmrs.module.webservices.rest.web.v1_0.resource.openmrs1_11.RestConstants1_11;

/**
 * @author Stélio Moiane
 */
@Resource(name = RestConstants.VERSION_1 + "/prescription", order = 1, supportedClass = Prescription.class, supportedOpenmrsVersions = {
        "1.8.*", "1.9.*", "1.10.*", "1.11.*", "1.12.*" })
public class PrescriptionResource extends DataDelegatingCrudResource<Prescription> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(final Representation rep) {
		
		if (rep instanceof RefRepresentation) {
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("provider");
			description.addProperty("patient", Representation.REF);
			description.addProperty("prescriptionDate");
			description.addProperty("prescriptionItems");
			description.addProperty("prescriptionEncounter", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("regime", Representation.REF);
			description.addProperty("arvPlan", Representation.REF);
			description.addProperty("therapeuticLine", Representation.REF);
			description.addProperty("changeReason");
			description.addProperty("interruptionReason");
			description.addProperty("prescriptionStatus");
			description.addSelfLink();
			return description;
		} else if (rep instanceof DefaultRepresentation) {
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("provider");
			description.addProperty("patient", Representation.REF);
			description.addProperty("prescriptionDate");
			description.addProperty("prescriptionItems");
			description.addProperty("prescriptionEncounter", Representation.REF);
			description.addProperty("location", Representation.REF);
			description.addProperty("regime", Representation.REF);
			description.addProperty("arvPlan", Representation.REF);
			description.addProperty("therapeuticLine", Representation.REF);
			description.addProperty("changeReason");
			description.addProperty("interruptionReason");
			description.addProperty("prescriptionStatus");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			final DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("uuid");
			description.addProperty("provider");
			description.addProperty("patient");
			description.addProperty("prescriptionDate");
			description.addProperty("prescriptionItems");
			description.addProperty("prescriptionEncounter");
			description.addProperty("location");
			description.addProperty("regime");
			description.addProperty("arvPlan");
			description.addProperty("therapeuticLine");
			description.addProperty("changeReason");
			description.addProperty("interruptionReason");
			description.addProperty("prescriptionStatus");
			description.addSelfLink();
			return description;
		} else {
			return null;
		}
	}
	
	@Override
	public Prescription newDelegate() {
		return new Prescription();
	}
	
	@Override
	public Prescription save(final Prescription delegate) {
		
		try {
			Context.getService(PrescriptionService.class).createPrescription(delegate);
		}
		catch (PharmacyBusinessException e) {
			
			throw new APIException(e);
		}
		return delegate;
	}
	
	@Override
	protected void delete(final Prescription delegate, final String reason, final RequestContext context)
	        throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public void purge(final Prescription delegate, final RequestContext context) throws ResponseException {
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	public Prescription getByUniqueId(final String uniqueId) {
		
		throw new ResourceDoesNotSupportOperationException();
	}
	
	@Override
	protected PageableResult doSearch(final RequestContext context) {
		
		final String patientUuid = context.getRequest().getParameter("patient");
		String findAllPrescribed = context.getRequest().getParameter("findAllPrescribed");
		String findAllActive = context.getRequest().getParameter("findAllActive");
		
		if (patientUuid == null) {
			return new EmptySearchResult();
		}
		
		final Patient patient = Context.getPatientService().getPatientByUuid(patientUuid);
		
		if (patient == null) {
			return new EmptySearchResult();
		}
		
		if (StringUtils.isNotBlank(findAllPrescribed)) {
			
			return findAllPrescribed(context, patient, findAllPrescribed);
		}
		
		if (StringUtils.isNotBlank(findAllActive)) {
			
			return findAllActive(context, patient, findAllActive);
		}
		
		return new EmptySearchResult();
	}
	
	@PropertySetter("prescriptionItems")
	public static void prescriptionItems(Prescription instance, List<PrescriptionItem> items) {
		for (PrescriptionItem item : items) {
			item.setPrescription(instance);
		}
		instance.setPrescriptionItems(items);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getCreatableProperties()
	 */
	@Override
	public DelegatingResourceDescription getCreatableProperties() {
		DelegatingResourceDescription description = new DelegatingResourceDescription();
		description.addProperty("prescriptionDate");
		description.addProperty("prescriptionItems");
		description.addProperty("prescriptionEncounter");
		description.addProperty("provider");
		description.addProperty("patient");
		description.addProperty("regime");
		description.addProperty("arvPlan");
		description.addProperty("therapeuticLine");
		description.addProperty("changeReason");
		description.addProperty("interruptionReason");
		description.addProperty("location");
		description.addProperty("prescriptionStatus");
		return description;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.BaseDelegatingResource#getPropertiesToExposeAsSubResources()
	 */
	@Override
	public List<String> getPropertiesToExposeAsSubResources() {
		return Arrays.asList("prescriptionItems");
	}
	
	@Override
	public String getResourceVersion() {
		return RestConstants1_11.RESOURCE_VERSION;
	}
	
	private PageableResult findAllPrescribed(RequestContext context, Patient patient, String findAllPrescribed) {

		Boolean doSearchAllPrecribed = Boolean.FALSE;

		try {
			doSearchAllPrecribed = Boolean.valueOf(findAllPrescribed).booleanValue();

		} catch (Exception e) {
		}

		if (doSearchAllPrecribed) {
			List<Prescription> prescriptions = Context.getService(PrescriptionService.class)
					.findAllPrescriptionsByPatient(patient);

			return new NeedsPaging<>(prescriptions, context);
		}

		return new EmptySearchResult();
	}
	
	private PageableResult findAllActive(RequestContext context, Patient patient, String findAllActive) {

		Boolean doSearchAllActive = Boolean.FALSE;

		try {
			doSearchAllActive = Boolean.valueOf(findAllActive).booleanValue();

		} catch (Exception e) {
		}

		if (doSearchAllActive) {
			List<Prescription> prescriptions = Context.getService(PrescriptionService.class)
					.findPrescriptionsByPatientAndActiveStatus(patient);

			return new NeedsPaging<>(prescriptions, context);
		}

		return new EmptySearchResult();
	}
}
