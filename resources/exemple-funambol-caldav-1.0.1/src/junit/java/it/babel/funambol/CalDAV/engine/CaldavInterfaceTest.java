package it.babel.funambol.CalDAV.engine;

import java.sql.Timestamp;

import net.fortuna.ical4j.model.TimeZone;

import junit.framework.TestCase;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

public class CaldavInterfaceTest extends TestCase {

	public final FunambolLogger  log = FunambolLoggerFactory.getLogger();
	
	public void testTimestamp2UTC() {
		Timestamp ts = new Timestamp(0);
		
		assertEquals("19700101T000000Z", CaldavInterface.Timestamp2UTC(ts));
		try {
			CaldavInterface.Timestamp2UTC(null);
		} catch (NullPointerException e) {
			e.printStackTrace();
			fail("can't manage null fields");
		}
	}

	public void testTimestamp2generalized() {
		try {
			CaldavInterface.Timestamp2generalized(null,TimeZone.getDefault());
		} catch (NullPointerException e) {
			e.printStackTrace();
			fail("can't manage null fields");
		}
	}
}
