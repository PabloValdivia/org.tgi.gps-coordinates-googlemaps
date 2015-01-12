package org.tgi.event;

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventTopics;
import org.compiere.model.MLocation;
import org.compiere.model.PO;
import org.compiere.util.Util;
import org.osgi.service.event.Event;
import org.tgi.util.GoogleMaps;

public class GoogleMapsEvents extends AbstractEventHandler {

	protected void initialize() {
		registerTableEvent(IEventTopics.PO_BEFORE_NEW, MLocation.Table_Name);
		registerTableEvent(IEventTopics.PO_BEFORE_CHANGE, MLocation.Table_Name);
	}

	protected void doHandleEvent(Event event) {
		PO po = getPO(event);
		MLocation loc = (MLocation)po;

		if (event.getTopic().equals(IEventTopics.PO_BEFORE_NEW) || (loc.is_ValueChanged(MLocation.COLUMNNAME_Address1) || loc.is_ValueChanged(MLocation.COLUMNNAME_Address2)
				|| loc.is_ValueChanged(MLocation.COLUMNNAME_Address3) || loc.is_ValueChanged(MLocation.COLUMNNAME_Address4)
				|| loc.is_ValueChanged(MLocation.COLUMNNAME_Postal) || loc.is_ValueChanged(MLocation.COLUMNNAME_City)
				)) {

			String location = loc.toString();
			GoogleMaps adr = new GoogleMaps(Util.deleteAccents(location));
			loc.set_ValueNoCheck("Latitude", adr.getLatitude());
			loc.set_ValueNoCheck("Longitude", adr.getLongitude());
		}
	}
}