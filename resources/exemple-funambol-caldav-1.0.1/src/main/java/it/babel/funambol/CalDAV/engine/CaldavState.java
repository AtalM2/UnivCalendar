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
 */

package it.babel.funambol.CalDAV.engine;

import it.babel.funambol.CalDAV.engine.source.AbstractCalDAVSyncSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.osaf.caldav4j.CalDAV4JException;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.tools.DataSourceTools;

/**
 * This class extends CaldavInterface to manage event changes thru funambol db
 * 
 * The class keeps track of all necessary changes in CalDAV into a database
 * (method update). Based on this database the methods getAllItems, getNewItems,
 * getModifiedItems, getDeletedItems return an array of ids (GUID) for CalDAV
 * entries.
 * 
 * @author <a href='mailto:rpolli _@ babel.it'>Roberto Polli</a>
 * 
 * based on work of LDAP Plugin by 
 * @author  <a href='mailto:fipsfuchs _@ users.sf.net'>Philipp Kamps</a>
 * @author  <a href='mailto:gdartigu _@ smartjog.com'>Gilles Dartiguelongue</a>
 * @version $Id$
 */
public class CaldavState extends CaldavInterface {

	private static String deletedItemsSQL = "SELECT guid FROM fnbl_client_mapping WHERE principal = ? AND sync_source = ?";

	private Connection db;
	private String dbName;

	private DataSource datasource; // the datasource created by init()

	/**
	 * The constructor saves the reference to
	 * funambol database name.
	 *
	 * @see CaldavInterface
	 * @param dbname String database name where the funambol framework saves its data
	 * 
	 */
	public CaldavState(String host, String port, String user, String pass,
			String base, boolean secure, String dbname) 
	throws SyncSourceException {

		super(host, port, user, pass, base, secure);
		logger.info("CaldavState()");
		dbName = dbname;

		try {
			Context ctx;
			ctx = new InitialContext();

			// XXX I can avoid it catching the exception?
			if ((ctx == null) || (dbName == null) )
				throw new Exception("Boom - No Context");

			//datasource = (DataSource) ctx.lookup("java:comp/env/jdbc/" + dbName);
			datasource = DataSourceTools.lookupDataSource("jdbc/" + dbName);
		} catch (NamingException e) {
			logger.error("Name exception in CalDAVState.java",e);
		} catch (SQLException e) {
			logger.error("SQL exception in CalDAVState.java",e);
		} catch (Exception e) {
			logger.error("OMG !!! Crashed ! Don't know what happened though...",e);
		}
	}

	/**
	 * TODO retrieve deleted item directly from the server?
	 * Get items that are on the Funambol database FNBL_CLIENT_MAPPING
	 *  but not on the server and consider the
	 * difference as deleted items
	 * @throws SyncSourceException 
	 */
	public List<String> getDeletedItems(AbstractCalDAVSyncSource syncSource, Timestamp since)
	throws SyncSourceException {
		String clientId = "" + syncSource.getPrincipal().getId();

		// Get all entries from the mapping database
		List<String> mappingEntries = new ArrayList<String>();

		try {
			if (datasource != null) {
				db = datasource.getConnection();
			}

			PreparedStatement query = db.prepareStatement(deletedItemsSQL);
			query.setString(1, clientId);
			query.setString(2, syncSource.getSourceURI() );
			ResultSet rset = query.executeQuery();

			while (rset.next()) {
				try {
					mappingEntries.add(rset.getString(1));
				} catch (Exception ex) {
					logger.warn("Got invalid GUID from fnbl_client_mapping table ("	+ rset.getString(1) + ")");
				}
			}
			db.close(); 
		} catch (SQLException ex) {
			logger.warn("deletedItemsSQL Error: " + ex.toString());
		}

		// Get all entries from the server
		List<String> uids = getAllUids();

		logger.trace("Count of entries: server=" + uids.size() + " REMOVE=" + mappingEntries.size() );
		mappingEntries.removeAll(uids);

		return mappingEntries;
	}


	/**
	 * Send new Event to the CalDAV server and
	 *  replace its key with an RFC-compiant UID
	 * @param newItems list of new contacts to add to the server
	 * @param created the last syncronization startTs
	 * @throws CalDAV4JException 
	 * @throws SyncSourceException 
	 * @see commitNewItems()
	 */
	public void commitNewItem(List<SyncItem> newItems, Timestamp created) 
	throws SyncSourceException {
		for (SyncItem si : newItems) {
			addNewEntry(si, created);
		}
	}
	/**
	 * Send updated events to the  server
	 * @param updatedItems list of contacts to update on the server
	 * @param lastModified the last syncronization startTs
	 * @throws SyncSourceException if something goes wrong  
	 */
	public void commitUpdatedItems (List<SyncItem> updatedItems, Timestamp lastModified) 
	throws SyncSourceException {
		for (SyncItem si: updatedItems) {
			updateCalendar(si, lastModified);
		}
	}

	/**
	 * Delete contacts on the  server
	 * @param deletedItems list of contacts to delete on the server
	 * @throws SyncSourceException
	 * TODO here're some issues
	 * 	restricting caldav search in a one-year interval can lead to a removal of not-deleted events
	 * 	what to do in case of exception on one item?
	 */
	public void commitDeletedItems(List<SyncItem> deletedItems)
	throws SyncSourceException {
		for (SyncItem si: deletedItems){
			deleteEntry(si);
		}
	}

	/**
	 * Close connection to the database(?) server
	 */
	public void close() {
		try {
			db.close();
		} catch (SQLException ex) {
			logger.warn("Error on close");
			logger.warn("modifiedItemsSQL Error: " + ex.toString());
		}
	}


}
