/**
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
 * 
 * @author rpolli@babel.it, pventura@babel.it
 */

package it.babel.funambol.CalDAV.engine.source;

import it.babel.funambol.CalDAV.conversion.Converter;
import it.babel.funambol.CalDAV.exceptions.ConversionException;
import it.babel.funambol.CalDAV.helper.CalendarHelper;
import it.babel.funambol.CalDAV.util.CalDavConstants;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;

import com.funambol.framework.core.AlertCode;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemImpl;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.tools.beans.LazyInitBean;

public class CalDAVSyncSourceVEvent extends AbstractCalDAVSyncSource implements SyncSource, Serializable, LazyInitBean {

	private static final long serialVersionUID = 8732186067511882991L;

	public CalDAVSyncSourceVEvent() {
		super(null);
	}

	public CalDAVSyncSourceVEvent(String name) {
		super(name);
	}

	/**
	 * Extracts the content from a syncItem.
	 * 
	 * @param syncItem
	 * @return as a String object (same as
	 *         PIMSyncSource#getContentFromSyncItem(String), but trimmed)
	 * @see PIMContactSyncSource.java
	 */
	protected String getContentFromSyncItem(SyncItem syncItem) {
		String raw = super.getContentFromSyncItem(syncItem);
		return raw.trim();
	}

	/**
	 * 1- TODO cache in allItems 2- return an array of GUID
	 * 
	 * @see SyncSource
	 * @return array of GUID
	 */
	public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {
		logger.info("getAllSyncItemKeys(" + principal + ")");

		if (caldavState == null) {
			throw new SyncSourceException("CaldavState uninitialized");
		}
		List<String> uidList = caldavState.getAllUids();
		logger.info("Got ALL sync items ( " + uidList.size() + " )");

		return AbstractCalDAVSyncSource.uidsToKeys(uidList);
	}

	/**
	 * Adds a SyncItem object to the Caldav server. The item should have a newly
	 * created UID we could too get a stream from the client, eventually adding
	 * properties we need. the syncItem should be a VEvent, so no problems
	 * should arise ;)
	 * 
	 * @param syncItem
	 *            the SyncItem representing the VEvent
	 * 
	 * @return a newly created syncItem based on the input object but with its
	 *         status set at SyncItemState.NEW
	 * @note for .ics events GUID and LUID should be the same, so that they can
	 *       be uniquely recognized
	 */
	public SyncItem addSyncItem(SyncItem syncItem) throws SyncSourceException {
		logger.info("addSyncItem(" + principal + " , " + syncItem.getKey().getKeyAsString() + ") with type:" + getSourceType());

		{
			// TODO
			// get only id, not the whole calendar
			SyncItem itemOnServer = getSyncItemFromId(syncItem.getKey());

			// Adds the contact, wraps it in sync information and uses it to
			// create a new SyncItem which is the return value of this method
			if (itemOnServer != null) {
				throw new SyncSourceException("Error adding the item " + syncItem + "still present on the server");
			}
			String content = getContentFromSyncItem(syncItem);
			String contentType = syncItem.getType();

			SyncItemImpl serverSyncItem = new SyncItemImpl(this,// syncSource
					syncItem.getKey().getKeyAsString(), // key
					null, // mappedKey
					SyncItemState.NEW,// state
					content.getBytes(), // content
					null, // format
					contentType, // type
					syncItem.getTimestamp() // timestamp
			);

			ArrayList<SyncItem> aSyncItem = new ArrayList<SyncItem>();
			aSyncItem.add(serverSyncItem);
			++howManyAdded;

			// add the item to caldav, converting it and applying UUID when
			// missing
			caldavState.commitNewItem(aSyncItem, syncContext.getSince());

			// cache?
			// allItems.add(serverSyncItem);
			// newItems.add(serverSyncItem);

			return serverSyncItem;
		}
	}

	/**
	 * @see SyncSource
	 */
	public SyncItemKey[] getDeletedSyncItemKeys(Timestamp since, Timestamp until) throws SyncSourceException {
		logger.info("getDeletedSyncItemKeys(" + principal + " , " + since + " , " + until + ")");

		List<String> uids = caldavState.getDeletedItems(this, since);
		logger.info("Got DELETED sync items ( " + uids.size() + " ) since ( " + since + " )");
		return AbstractCalDAVSyncSource.uidsToKeys(uids);

	}

	/**
	 * XXX get the item GUIDs newly created on server: it freshen the newItems
	 * array TODO how timezone is managed?
	 * 
	 * @see SyncSource
	 * @return GUIDs
	 */
	public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp until) throws SyncSourceException {
		logger.info("getNewSyncItemKeys(" + principal + " , " + since + " , " + until + ")");
		Timestamp sinceUTC = since;

		// timestamp to UTC if the device has a timezone. FIXME this should'n work
		if (deviceTimeZone != null) {
			sinceUTC = normalizeTimestamp(since);
		}

		if (syncMode == AlertCode.ONE_WAY_FROM_SERVER) {
			return getAllSyncItemKeys();
		}

		List<String> uids = caldavState.getNewEntries(sinceUTC);
		logger.info("Get NEW sync items ( " + uids.size() + " ) since ( " + sinceUTC + " ) until ( " + until + " )");

		newKeys = uids;
		return AbstractCalDAVSyncSource.uidsToKeys(uids);
	}

	/**
	 * get item with the given GUID and convert it into a SyncItem may use a
	 * cache mechanism TODO should implement all of VCALENDAR types (VEVENT,
	 * VTODO, ...)
	 * 
	 * @param syncItemKey
	 *            item GUID
	 * @return a syncItem
	 * @see SyncSource
	 */
	public SyncItem getSyncItemFromId(SyncItemKey syncItemKey) throws SyncSourceException {

		logger.info("getSyncItemsFromId(" + principal + ", " + syncItemKey + ")");

		// retrieve the item content and create a new 
		Calendar calendarRetrived = caldavState.getCalendarContentByUid(syncItemKey.getKeyAsString(), getServerTimeZone());
		
		if (calendarRetrived != null) {
						
			String calendarRetrivedAsString = null;
			String siType = CalDavConstants.TYPE_ICAL;

			logger.debug("SYNCSOURCE TYPE" + getSourceType());
			if (getSourceType().equals(CalDavConstants.TYPE_VCAL)) {
				
				// strip timezones  
				Component ctz = calendarRetrived.getComponent(Component.VTIMEZONE);
				if (ctz != null) {
					boolean removed = calendarRetrived.getComponents().remove(ctz);
					if (!removed)
						throw new SyncSourceException("Can't strip Timezone");
				}
				calendarRetrivedAsString = calendarRetrived.toString();			

				// new converison
				Converter c = new Converter(getDeviceTimeZone());
				try {
					com.funambol.common.pim.calendar.Calendar calendar = c.webCalendar2Calendar(calendarRetrivedAsString, CalDavConstants.TYPE_ICAL);
					calendarRetrivedAsString = c.calendar2webCalendar(calendar, CalDavConstants.TYPE_VCAL);
				} catch (ConversionException e) {
					throw new SyncSourceException(e);
				}
				siType = CalDavConstants.TYPE_VCAL;
			} else if (getSourceType().equals(CalDavConstants.TYPE_SIFE)) {
				siType = CalDavConstants.TYPE_SIFE;
				
				calendarRetrivedAsString = calendarRetrived.toString();
				
				// new conversion
				Converter c = new Converter(getDeviceTimeZone());
				try {
					com.funambol.common.pim.calendar.Calendar calendar = c.webCalendar2Calendar(calendarRetrivedAsString, CalDavConstants.TYPE_ICAL);
					calendarRetrivedAsString = c.calendar2sif(calendar, CalDavConstants.TYPE_SIFE);
				} catch (ConversionException e) {
					throw new SyncSourceException(e);
				}
			}

			// @see addSyncItem
			SyncItem syncItem = new SyncItemImpl(this,// syncSource
					syncItemKey.getKeyAsString(), // key
					null, // mappedKey
					SyncItemState.UNKNOWN,// state
					calendarRetrivedAsString.getBytes(), // content
					null, // format
					siType, // type
					null // timestamp
			);

			return syncItem;
		}
		return null;
	}

	/**
	 * @see SyncSource TODO WRITEME returns the key of the syncItem passed
	 */
	public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem) throws SyncSourceException {
		logger.info("getSyncItemKeysFromTwin()");

		return new SyncItemKey[0];
	}

	/**
	 * @see SyncSource
	 * @return GUIDs of updated items
	 */
	public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp since, Timestamp until) throws SyncSourceException {
		logger.info("getUpdatedSyncItemKeys(" + principal + " , " + since + " , " + until + ")");

		if (deviceTimeZone != null) {
			since = normalizeTimestamp(since);
		}

		List<String> uids = caldavState.getModifiedItemKeys(since);
		logger.info("Get UPDATED sync items ( " + uids.size() + " ) since ( " + since + " )");
		if (newKeys != null) {
			uids.removeAll(newKeys);
		}

		updatedKeys = uids;
		return AbstractCalDAVSyncSource.uidsToKeys(uids);

	}

	/**
	 * @see SyncSource
	 */
	public void removeSyncItem(SyncItemKey syncItemKey, Timestamp time, boolean softDelete) throws SyncSourceException {
		if (softDelete) {
			logger.warn("Soft Delete not implemented yet!");
			return;
		} else {
			removeSyncItem(syncItemKey, time);
		}
	}

	/**
	 * Updates a SyncItem object
	 * 
	 * @param syncItem
	 *            the SyncItem representing the
	 * 
	 * @return a newly created syncItem based on the input object but with its
	 *         status set at SyncItemState.UPDATED and the GUID retrieved by the
	 *         back-end
	 */

	@Override
	public SyncItem updateSyncItem(SyncItem syncItem) throws SyncSourceException {
		logger.info("updateSyncItem(" + principal + " , " + syncItem.getKey().getKeyAsString() + ")");

		String content = null;

		net.fortuna.ical4j.model.Calendar cal = caldavState.getCalendarContentByUid(syncItem.getKey().getKeyAsString(), getServerTimeZone());

		if (cal != null) {

			// before that, a little magic...
			// IFF the new obj is vcs or sif we may lose some data
			// so we have to merge
			// - create 2 calendar object
			// - update only fields managed by mobiles
			// create a new nice object
			if (syncItem.getType().equals(CalDavConstants.TYPE_ICAL)) {
				// do nothing, it's ok
			} else if (syncItem.getType().equals(CalDavConstants.TYPE_VCAL) || syncItem.getType().equals(CalDavConstants.TYPE_SIFE)) {
				cal = CalendarHelper.mergeCalendarWithSyncItem(cal, syncItem.getContent(), syncItem.getType(), getDeviceTimeZone());
				content = cal.toString();
			} else {
				throw new SyncSourceException("Unsupported media type" + syncItem.getType());
			}

			// create the updated SyncItem
			SyncItemImpl serverSyncItem = new SyncItemImpl(this,// syncSource
					syncItem.getKey().getKeyAsString(), // key
					// syncItem.getParentKey() , // not specified in
					null, // mappedKey
					SyncItemState.UPDATED,// state
					content.getBytes(), // content
					syncItem.getFormat(), // format
					syncItem.getType(), // type SIF or ICAL: VCAL has been
					// processed already
					null // timestamp
			);

			++howManyUpdated;
			ArrayList<SyncItem> aSyncItem = new ArrayList<SyncItem>();
			aSyncItem.add(serverSyncItem);
			caldavState.commitUpdatedItems(aSyncItem, syncContext.getSince());

			// updatedItems.add(syncItem);
			// itemOnServer = syncItem;
			syncItem.setType(getSourceType());
			return syncItem;
		} else {
			throw new SyncSourceException("item is not on server");
		}

	}

}
