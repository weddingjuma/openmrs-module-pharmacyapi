/**
 * 
 */
package org.openmrs.module.pharmacyapi.api.prescriptiondispensation.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.openmrs.Drug;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Obs;
import org.openmrs.Order;
import org.openmrs.Patient;
import org.openmrs.module.pharmacyapi.api.common.exception.PharmacyBusinessException;
import org.openmrs.module.pharmacyapi.api.prescriptiondispensation.model.PrescriptionDispensation;

/**
 *
 */
public class PrescriptionDispensationDAOImpl implements PrescriptionDispensationDAO {
	
	private SessionFactory sessionFactory;
	
	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		
		this.sessionFactory = sessionFactory;
	}
	
	@Override
	public PrescriptionDispensation save(PrescriptionDispensation prescriptionDispensation) {
		
		this.sessionFactory.getCurrentSession().save(prescriptionDispensation);
		return prescriptionDispensation;
	}
	
	@Override
	public PrescriptionDispensation findByUuid(String uuid) {
		final Query query = this.sessionFactory.getCurrentSession()
		        .getNamedQuery(PrescriptionDispensationDAO.QUERY_NAME.findByUuid).setParameter("uuid", uuid);
		
		return (PrescriptionDispensation) query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<PrescriptionDispensation> findByPrescription(Encounter prescription) {
		
		final Query query = this.sessionFactory.getCurrentSession()
		        .getNamedQuery(PrescriptionDispensationDAO.QUERY_NAME.findByPrescription)
		        .setParameter("prescription", prescription);
		
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<PrescriptionDispensation> findByPatientUuid(String patientUuid) {
		
		final Query query = this.sessionFactory.getCurrentSession()
		        .getNamedQuery(PrescriptionDispensationDAO.QUERY_NAME.findByPatientUuid).setParameter("uuid", patientUuid);
		
		return query.list();
	}
	
	@Override
	public PrescriptionDispensation findByDispensationEncounter(Encounter dispensation) throws PharmacyBusinessException {
		
		final Query query = this.sessionFactory.getCurrentSession()
		        .getNamedQuery(PrescriptionDispensationDAO.QUERY_NAME.findByDispensationEncounter)
		        .setParameter("dispensation", dispensation);
		
		PrescriptionDispensation prescriptionDispensation = (PrescriptionDispensation) query.uniqueResult();
		if (prescriptionDispensation != null) {
			
			return prescriptionDispensation;
		}
		
		throw new PharmacyBusinessException("Entity PrescriptionDispensation not Found for dispensation " + dispensation);
	}
	
	@Override
	public PrescriptionDispensation findLastByPrescription(Encounter prescription) {
		
		final Query query = this.sessionFactory.getCurrentSession()
		        .getNamedQuery(PrescriptionDispensationDAO.QUERY_NAME.findLastByPrescription)
		        .setParameter("prescription", prescription);
		
		return (PrescriptionDispensation) query.uniqueResult();
		
	}
	
	@Override
	public Drug findDrugByOrderUuid(String uuid) {
		
		String Sql = "select drug.* from drug join drug_order on drug_order.drug_inventory_id = drug.drug_id join orders on orders.order_id = drug_order.order_id where orders.uuid = :orderuuid";
		Query query = this.sessionFactory.getCurrentSession().createSQLQuery(Sql).addEntity(Drug.class)
		        .setParameter("orderuuid", uuid);
		
		return (Drug) query.uniqueResult();
	}
	
	@Override
	public Encounter findEncounterByPatientAndEncounterTypeAndOrder(Patient patient, EncounterType encounterType, Order order) {
		
		String hql = "select distinct(o.encounter) from Obs o where o.order = :order and o.encounter.encounterType = :encounterType and o.encounter.patient = :patient and o.voided is false";
		Query query = this.sessionFactory.getCurrentSession().createQuery(hql).setParameter("patient", patient)
		        .setParameter("encounterType", encounterType).setParameter("order", order);
		
		return (Encounter) query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Obs> findObsByOrder(Order order) {
		
		Query query = this.sessionFactory.getCurrentSession()
		        .createQuery("select distinct o from Obs o where o.order = :order").setParameter("order", order);
		return query.list();
	}
	
	@Override
	public void retire(PrescriptionDispensation prescriptionDispensation) {
		
		this.sessionFactory.getCurrentSession().saveOrUpdate(prescriptionDispensation);
	}
}
