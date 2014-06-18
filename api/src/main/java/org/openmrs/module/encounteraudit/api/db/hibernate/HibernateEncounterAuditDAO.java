/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.encounteraudit.api.db.hibernate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SQLQuery;
import org.hibernate.SessionFactory;
import org.openmrs.Encounter;
import org.openmrs.EncounterType;
import org.openmrs.Location;
import org.openmrs.module.encounteraudit.api.db.EncounterAuditDAO;

import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

/**
 * It is a default implementation of  {@link EncounterAuditDAO}.
 */
public class HibernateEncounterAuditDAO implements EncounterAuditDAO {
	protected final Log log = LogFactory.getLog(this.getClass());
	
	private SessionFactory sessionFactory;
	
	/**
     * @param sessionFactory the sessionFactory to set
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
	    this.sessionFactory = sessionFactory;
    }
    
	/**
     * @return the sessionFactory
     */
    public SessionFactory getSessionFactory() {
	    return sessionFactory;
    }

    @Override
    public List<Encounter> getAuditEncounters(Date fromDate, Date toDate, int sampleSize, Location location, EncounterType encounterType) {

        if (sampleSize < 1) {
            // by default return 25 records
            sampleSize = 25;
        }

        StringBuilder sql = new StringBuilder("select * from encounter e where ");
        sql.append(" encounter_datetime > :fromDate and ");
        sql.append(" encounter_datetime < :toDate ");
        if (location != null) {
            sql.append(" and location_id = :locationId ");
        }
        if (encounterType != null) {
            sql.append(" and encounter_type = :encounterType ");
        }
        sql.append(" order by rand() ");
        sql.append("limit 0,:sampleSize ");

        SQLQuery query = sessionFactory.getCurrentSession().createSQLQuery(sql.toString()).addEntity(Encounter.class);
        query.setDate("fromDate", fromDate);
        query.setDate("toDate", toDate);
        if (location != null) {
            query.setInteger("locationId", new Integer(location.getLocationId()));
        }
        if (encounterType != null) {
            query.setInteger("encounterType", new Integer(encounterType.getEncounterTypeId()));
        }
        query.setInteger("sampleSize", new Integer(sampleSize));

        List<Encounter> encounterList = query.list();

        return encounterList;
    }
}