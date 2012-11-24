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
 */

/* TODO rpolli 
 * this file should be a common abstract class able to manage all types of iCalendar
 *  events: VEVENTS, VTODOS, etc.etc.etc.
 *  it should only read-write from caldav server
 */
package it.babel.funambol.CalDAV.engine.source;

import it.babel.funambol.CalDAV.engine.CaldavState;
import it.babel.funambol.CalDAV.engine.VEventDAO;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.AbstractSyncSource;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.funambol.server.config.Configuration;

/**
 * This class implements a CalDAV <i>SyncSource</i>
 *
 * @author  <a href='mailto:rpolli@babel.it'>Roberto Polli</a>
 * @author pventura_at_babel.it
 * @version $Id$
 */
public abstract class AbstractCalDAVSyncSource extends AbstractSyncSource
implements SyncSource, Serializable, LazyInitBean
{
	
	public static final String EMAIL_PATTERN = "^[a-zA-Z]([\\w\\.-]*[a-zA-Z0-9])*@[a-zA-Z0-9][\\w\\.-]*[a-zA-Z0-9]\\.[a-zA-Z][a-zA-Z\\.]*[a-zA-Z]$";

	// -------------------------------------------------------- Private Data set in SyncSourceConfig.java

	protected String name             = null;
	protected TimeZone serverTimeZone = null;

	private String sourceType;

	private String caldavHost;
	private String  caldavPort;
	private String caldavBaseURL;
	private String caldavUser;
	private String caldavPass;
	private boolean isSSL;
	protected int syncMode;

	private String dbName;

	private VEventDAO vEventDAO          = null;
	protected CaldavState caldavState            = null;

	protected String   deviceTimeZoneDescr = null;
	protected TimeZone deviceTimeZone      = null;

	protected FunambolLogger logger = FunambolLoggerFactory.getLogger(LOG_NAME);

	// TODO: cache variables. think about how to implement a cache mechanism
	private List<SyncItem> allItems        = null;
	private List<SyncItem> newItems        = null;
	private List<SyncItem> deletedItems    = null;
	private List<SyncItem> updatedItems    = null;

	protected List<String> newKeys = null;
	protected List<String> updatedKeys = null;



	/**
	 * The principal to sync
	 */
	protected Sync4jPrincipal principal = null;

	/**
	 * Getter for property principal
	 * @return Sync4jPrincipal
	 */
	public Sync4jPrincipal getPrincipal() {
		logger.info("getPrincipal(" + principal + " , ...)");
		logger.info("my principal is ["+ principal.getUsername() +"]"); //rpolli

		return principal;
	}

	/**
	 * Setter for property principal
	 * @param principal Sync4jPrincipal
	 */
	public void setPrincipal(Sync4jPrincipal principal) {
		this.principal = principal;
	}

	/**
	 * The context of the sync
	 */
	protected SyncContext syncContext = null;

	/**
	 * Getter for property syncContext
	 * @return SyncContext
	 */
	public SyncContext getSyncContext() {
		logger.info("getSyncContext(" + syncContext + " , ...)");
		return syncContext;
	}


	public AbstractCalDAVSyncSource() {
		super();
		logger.trace("AbstractCalDAVSyncSource(NONAME ...)");
	}

	public AbstractCalDAVSyncSource(String name) {
		super(name);
		logger.trace("AbstractCalDAVSyncSource(" + name + " , ...)"); 
	}


	/**
	 * here we can connect to the caldav server, using <i>principal</i> data
	 * replacing %u with username
	 * 
	 * TODO use syncsource data
	 */
	public void init()
	{
		logger.info("init(" + name + " , ...)");

		// if principal is set we can customize some fields
		if (principal != null) {
			setCaldavUser(principal.getUsername());
			setCaldavPass(principal.getUser().getPassword());

			setCaldavBaseURL(getCaldavBaseURL().replaceAll("%u", principal.getUsername()));
			
			logger.info("server TIMEZONE: " + getServerTimeZone());
		}
	}

	/**
	 * A string representation of this object
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer(super.toString());

		sb.append(" - {name: ").append(getName()     );
		sb.append(" type: "   ).append(type          );
		sb.append(" uri: "    ).append(getSourceURI());
		sb.append("}"         );
		return sb.toString();
	}

	/**
	 * SyncSource's beginSync()
	 * @param syncContext @see SyncSource
	 */
	public void beginSync(SyncContext syncContext) 
	throws SyncSourceException
	{
		logger.info("beginSync()");
		super.beginSync(syncContext); // this cleans some vars

		this.syncMode = syncContext.getSyncMode();
		this.syncContext = syncContext;
		this.principal = syncContext.getPrincipal();

		init();

		try {
			caldavState  = new CaldavState(caldavHost, caldavPort,
					caldavUser, caldavPass,
					caldavBaseURL, false,
					getDbName());
			logger.info("connection established");
		} catch (SyncSourceException e) {
			String message = "can't connect to "+caldavHost+":"+caldavPort+"/"+caldavBaseURL; 
			logger.fatal(message, e);
		}

		logger.info("beginSync for CalDAVConnector ( " + this.principal
				+ " ) mode ( " + syncContext.getSyncMode() + " )");

		try {
			// Getting device information
			String deviceId = syncContext.getPrincipal().getDeviceId();
			Sync4jDevice device = getDevice(deviceId);

			String tzDesctiption = device.getTimeZone();

			if (device.getConvertDate()) {
				if (tzDesctiption != null && tzDesctiption.length() > 0) {
					deviceTimeZoneDescr = tzDesctiption;
					deviceTimeZone = TimeZone.getTimeZone(deviceTimeZoneDescr);
				}
			}
			// deviceCharset = device.getCharset();
		} catch (Exception e) {
			logger.fatal("Exception while trying to get device information.");
			throw new SyncSourceException(e.getMessage());
		}



		// initialize variables
		// initialize allItems

		if (updatedItems != null) {
			updatedItems = null;
		} else {
			updatedItems = new ArrayList<SyncItem>();
		}

		if (newItems != null) {
			newItems = null;
		} else {
			newItems = new ArrayList<SyncItem>();
		}

		if (deletedItems != null) {
			deletedItems = null;
		} else {
			deletedItems = new ArrayList<SyncItem>();
		}

		logger.info("CalDAVSyncSource beginSync end");
	}

	/**
	 * SyncSource's endSync()
	 */
	public void endSync() throws SyncSourceException
	{
		logger.info("endSync()");
		super.endSync();


		if (deletedItems != null && !deletedItems.isEmpty() ) {
			logger.info("endSync: Commiting " + howManyDeleted + " deleted items to the Caldav server");
			caldavState.commitDeletedItems( deletedItems );	
		}

		/*
		if ( !updatedItems.isEmpty() ) {
			logger.info("endSync: Commiting " + howManyUpdated + " updated items to the server");
			caldavState.commitUpdatedItems(updatedItems);	
		}
		 */		
		logger.info("endSync for CalDAVConnector (" + this.principal + ")");
	}

	/**
	 * @see SyncSource
	 */
	public void commitSync() throws SyncSourceException {
		logger.info("commitSync ");
		// TODO not useful yet but keeping for eventual future use
	}

	/**
	 * @see SyncSource
	 */
	public void setOperationStatus(String operation, int statusCode, SyncItemKey[] keys) {

		StringBuffer message = new StringBuffer("Received status code '");
		message.append(statusCode).append("' for a '");
		message.append(operation).append("'");
		message.append(" for this items: ");

		for (int i = 0; i < keys.length; i++) {
			message.append("\n- " + keys[i].getKeyAsString());
		}

		logger.info(message.toString());
	}

	public void setCaldavHost(String value) {
		caldavHost = value;
	}

	public String getCaldavHost() {
		return caldavHost;
	}

	public String getCaldavBaseURL() {
		return caldavBaseURL;
	}

	public String getCaldavPass() {
		return caldavPass;
	}

	public String getCaldavPort() {
		return caldavPort;
	}

	/**
	 * Does this SyncSource use SSL connection ?
	 * 
	 * @return use SSL connection
	 */
	public boolean getSSL() {
		return this.isSSL;
	}

	public String getCaldavUser() {
		return caldavUser;
	}

	public void setCaldavBaseURL(String string) {
		caldavBaseURL = string;
	}

	public void setCaldavPass(String string) {
		caldavPass = string;
	}

	public void setCaldavPort(String string) {
		caldavPort = string;
	}
//	public void setCaldavVersion(String string) {
//	this.info.setPreferred(string);
//	}
	public void setSSL(boolean isSSL) {
		this.isSSL = isSSL;
	}

	public void setCaldavUser(String string) {
		caldavUser = string;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String string) {
		dbName = string;
	}

	public TimeZone getServerTimeZone() {
		return serverTimeZone;
	}

	public void setServerTimeZone(TimeZone t) {
		this.serverTimeZone = t;
	}


	// -------------------------------------------------------- Private methods

	/**
	 * Return the device with the given deviceId
	 * @param deviceId String
	 * @return Sync4jDevice
	 * @throws PersistentStoreException
	 */
	private Sync4jDevice getDevice(String deviceId) throws PersistentStoreException {
		logger.info("getDevice()");
		Sync4jDevice device = new Sync4jDevice(deviceId);
		PersistentStore store = Configuration.getConfiguration().getStore();
		store.read(device);
		return device;
	}


	/**
	 * Returns a timestamp aligned to UTC
	 */
	protected Timestamp normalizeTimestamp(Timestamp t) {
		return new Timestamp(t.getTime()
				- getServerTimeZone().getOffset(t.getTime()));
	}


	/**
	 * Removes the item with the given itemKey marking the item deleted with the 
	 * give time.
	 * @param syncItemKey the key of the item to remove
	 * @param time the deletion time
	 */
	protected void removeSyncItem(SyncItemKey syncItemKey, Timestamp time)
	throws SyncSourceException {

		logger.info("removeSyncItem(" +
				principal    +
				" , "        +
				syncItemKey  +
				" , "        +
				time         +
		")");

		// everything is done by CaldavState
		if (syncItemKey!=null) { 
			logger.info("removeSyncItem( REMOVING: " + syncItemKey.getKeyAsString() + " )");
			++howManyDeleted;
			caldavState.deleteEntryByUid(syncItemKey.getKeyAsString());
		}

	}  

	/**
	 * Extracts the content from a syncItem.
	 *
	 * @param syncItem
	 * @return as a String object
	 * @see com.funambol.foundation.engine.source
	 */
	protected String getContentFromSyncItem(SyncItem syncItem) {

		byte[] itemContent = syncItem.getContent();

		// Add content processing here, if needed

		return new String(itemContent == null ? new byte[0] : itemContent);
	}

	protected static SyncItemKey[] uidsToKeys(List<String> uids) {
		SyncItemKey[] keys = new SyncItemKey[uids.size()];

		for (int i=0; i<uids.size(); i++) {
			keys[i] = new SyncItemKey(uids.get(i));
		} 
		return keys;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public TimeZone getDeviceTimeZone() {
		return deviceTimeZone;
	}
}
