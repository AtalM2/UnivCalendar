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


package it.babel.funambol.CalDAV.admin;

import it.babel.funambol.CalDAV.engine.source.CalDAVSyncSourceVEvent;
import it.babel.funambol.CalDAV.util.CalDavConstants;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.apache.commons.lang.StringUtils;

import com.funambol.framework.engine.source.ContentType;
import com.funambol.framework.engine.source.SyncSourceInfo;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.admin.AdminException;
import com.funambol.admin.ui.SourceManagementPanel;

/**
 * This class implements the configuration panel for CalDAVSyncSource
 * @author rpolli_at_babel.it
 * @author pventura_at_babel.it
 * @version $Id$
 */
public class CalDAVSyncSourceConfigPanel extends SourceManagementPanel implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7029633271814255635L;
	protected  static final String LOG_NAME="funambol";
	protected FunambolLogger logger = FunambolLoggerFactory.getLogger(LOG_NAME);

	/**
	 * Allowed characters for name and uri
	 */
	public static final String NAME_ALLOWED_CHARS
	= "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890-_.";

	private static final String[]         supportedTypesS    = {
		CalDavConstants.TYPE_VCAL
		,CalDavConstants.TYPE_ICAL
		,CalDavConstants.TYPE_SIFE
	};

	private static final String[]         supportedTypesV    = {
		CalDavConstants.VERSION_VCAL
		,CalDavConstants.VERSION_ICAL
		,CalDavConstants.VERSION_SIFE
	};

	/** label for the panel's name */
	private JLabel panelName = new JLabel();

	/** border to evidence the title of the panel */
	private TitledBorder  titledBorder1;

	private JLabel           nameLabel          = new JLabel();
	private JTextField       nameValue          = new JTextField();
	private JLabel           typeLabel          = new JLabel();
	private ContentType[]    supportedTypes     = null;

	private JComboBox        typeValue          = new JComboBox(supportedTypesS);
	private JLabel           timeZoneLabel      = new JLabel("Server timezone :");
	private JComboBox        timeZoneValue      = null;
	private JLabel           sourceUriLabel     = new JLabel();
	private JTextField       sourceUriValue     = new JTextField();

	private JLabel           caldavHostLabel      = new JLabel()    ;
	private JTextField       caldavHostValue      = new JTextField();
	private JLabel           caldavPortLabel      = new JLabel()    ;
	private JTextField       caldavPortValue      = new JTextField();

	private JLabel           isSSLLabel         = new JLabel();
	private JCheckBox        isSSLValue         = new JCheckBox();

	private JLabel           caldavBaseURLLabel      = new JLabel()    ;
	private JTextField       caldavBasURLeValue      = new JTextField();
	private JLabel           caldavUserLabel      = new JLabel()    ;
	private JTextField       caldavUserValue      = new JTextField();
	private JLabel           caldavPassLabel      = new JLabel()    ;
	private JPasswordField   caldavPassValue      = new JPasswordField();

	private JLabel           dbNameLabel        = new JLabel()    ;
	private JTextField       dbNameValue        = new JTextField();

	private JButton          confirmButton      = new JButton()    ;


	/**
	 * Creates a new CalDAVSyncSourceConfigPanel instance
	 */
	public CalDAVSyncSourceConfigPanel()
	{
		init();

		List<ContentType> ct = new ArrayList<ContentType>();

		for(int i=0; i<supportedTypesS.length; i++) {
			ct.add( new ContentType(supportedTypesS[i], supportedTypesV[i]) );
		}

		supportedTypes = new ContentType[supportedTypesS.length];
		ct.toArray(supportedTypes);


	}

	/**
	 * Create the panel
	 * @throws Exception if error occures during creation of the panel
	 */
	private void init()
	{
		this.setLayout(null);

		// set properties of label, position and border
		// referred to the title of the panel
		titledBorder1 = new TitledBorder("");

		panelName.setFont(titlePanelFont);
		panelName.setText("Edit CalDAV SyncSourceCalendar");
		panelName.setBounds(new Rectangle(14, 5, 316, 28));
		panelName.setAlignmentX(SwingConstants.CENTER);
		panelName.setBorder(titledBorder1);

		sourceUriLabel.setText("Source URI: ");
		sourceUriLabel.setFont(defaultFont);
		sourceUriLabel.setBounds(new Rectangle(14, 60, 150, 18));
		sourceUriValue.setFont(new java.awt.Font("Arial", 0, 12));
		sourceUriValue.setBounds(new Rectangle(170, 60, 350, 18));

		nameLabel.setText("Name: ");
		nameLabel.setFont(defaultFont);
		nameLabel.setBounds(new Rectangle(14, 90, 150, 18));
		nameValue.setFont(new java.awt.Font("Arial", 0, 12));
		nameValue.setBounds(new Rectangle(170, 90, 350, 18));

		typeLabel.setText("Type: ");
		typeLabel.setFont(defaultFont);
		typeLabel.setBounds(new Rectangle(14, 120, 150, 18));
		typeValue.setBounds(new Rectangle(170, 120, 350, 18));

		caldavHostLabel.setText("CalDAV Host: ");
		caldavHostLabel.setFont(defaultFont);
		caldavHostLabel.setBounds(new Rectangle(14, 150, 150, 18));
		caldavHostValue.setFont(new java.awt.Font("Arial", 0, 12));
		caldavHostValue.setBounds(new Rectangle(170, 150, 350, 18));

		caldavPortLabel.setText("CalDAV Port: ");
		caldavPortLabel.setFont(defaultFont);
		caldavPortLabel.setBounds(new Rectangle(14, 180, 150, 18));
		caldavPortValue.setFont(new java.awt.Font("Arial", 0, 12));
		caldavPortValue.setBounds(new Rectangle(170, 180, 350, 18));

		isSSLLabel.setText("CalDAV use SSL: ");
		isSSLLabel.setFont(defaultFont);
		isSSLLabel.setBounds(new Rectangle(14, 210, 150, 18));
		isSSLValue.setSelected(false);
		isSSLValue.setBounds(new Rectangle(170, 210, 350, 18));

		caldavBaseURLLabel.setText("CalDAV Base URI (eg. /ucaldav/user/ for Bedework): ");
		caldavBaseURLLabel.setFont(defaultFont);
		caldavBaseURLLabel.setBounds(new Rectangle(14, 240, 150, 18));
		caldavBasURLeValue.setFont(new java.awt.Font("Arial", 0, 12));
		caldavBasURLeValue.setBounds(new Rectangle(170, 240, 350, 18));

		caldavUserLabel.setText("CalDAV User: ");
		caldavUserLabel.setFont(defaultFont);
		caldavUserLabel.setBounds(new Rectangle(14, 270, 150, 18));
		caldavUserValue.setFont(new java.awt.Font("Arial", 0, 12));
		caldavUserValue.setBounds(new Rectangle(170, 270, 350, 18));

		caldavPassLabel.setText("CalDAV Password: ");
		caldavPassLabel.setFont(defaultFont);
		caldavPassLabel.setBounds(new Rectangle(14, 300, 150, 18));
		caldavPassValue.setFont(new java.awt.Font("Arial", 0, 12));
		caldavPassValue.setBounds(new Rectangle(170, 300, 350, 18));

		dbNameLabel.setText("Funambol DBMS Name: ");
		dbNameLabel.setFont(defaultFont);
		dbNameLabel.setBounds(new Rectangle(14, 330, 150, 18));
		dbNameValue.setFont(new java.awt.Font("Arial", 0, 12));
		dbNameValue.setBounds(new Rectangle(170, 330, 350, 18));

		timeZoneLabel.setFont(defaultFont);
		timeZoneLabel.setBounds(new Rectangle(14, 360, 150, 18));
		timeZoneValue = new JComboBox(TimeZone.getAvailableIDs());
		timeZoneValue.setBounds(new Rectangle(170, 360, 350, 18));

		confirmButton.setFont(defaultFont);
		confirmButton.setText("Add");
		confirmButton.setBounds(170, 420, 70, 25);

		confirmButton.addActionListener(new ActionListener()	{
			public void actionPerformed(ActionEvent event )	{
				try {
					validateValues();
					getValues();
					if (getState() == STATE_INSERT){
						CalDAVSyncSourceConfigPanel.this.actionPerformed(new ActionEvent(CalDAVSyncSourceConfigPanel.this, ACTION_EVENT_INSERT, event.getActionCommand()));
					} else{
						CalDAVSyncSourceConfigPanel.this.actionPerformed(new ActionEvent(CalDAVSyncSourceConfigPanel.this, ACTION_EVENT_UPDATE, event.getActionCommand()));
					}
				}	catch (Exception e){
					notifyError(new AdminException(e.getMessage()));
				}
			}
		});

		// add all components to the panel
		this.add(panelName        , null);
		this.add(nameLabel        , null);
		this.add(nameValue        , null);
		this.add(typeLabel        , null);
		this.add(typeValue        , null);
		this.add(sourceUriLabel   , null);
		this.add(sourceUriValue   , null);

		this.add(caldavHostLabel    , null);
		this.add(caldavHostValue    , null);
		this.add(caldavPortLabel    , null);
		this.add(caldavPortValue    , null);
		this.add(isSSLLabel       , null);
		this.add(isSSLValue       , null);
		this.add(caldavBaseURLLabel    , null);
		this.add(caldavBasURLeValue    , null);
		this.add(caldavUserLabel    , null);
		this.add(caldavUserValue    , null);
		this.add(caldavPassLabel    , null);
		this.add(caldavPassValue    , null);
		this.add(timeZoneLabel    , null);
		this.add(timeZoneValue    , null);

		this.add(dbNameLabel      , null);
		this.add(dbNameValue      , null);

		this.add(confirmButton    , null);
	}

	/**
	 * Load the current syncSource showing the name, uri and type in the panel's
	 * fields.
	 */
	public void updateForm(){
		if (!(getSyncSource() instanceof CalDAVSyncSourceVEvent)){
			notifyError(
					new AdminException(
							"This is not a CalDAV SyncSource! Unable to process SyncSource values ("+getSyncSource()+")."
					)
			);
			return ;
		}

		CalDAVSyncSourceVEvent syncSource = (CalDAVSyncSourceVEvent) getSyncSource();

		if (getState() == STATE_INSERT) {
			confirmButton.setText("Add");
		} else if (getState() == STATE_UPDATE) {
			confirmButton.setText("Save");
		}
		
		SyncSourceInfo sinfo=new SyncSourceInfo(supportedTypes, typeValue.getSelectedIndex());
		sinfo.setPreferred(typeValue.getSelectedIndex());
		
		syncSource.setInfo(sinfo);

		sourceUriValue.setText(syncSource.getSourceURI());
		nameValue.setText(syncSource.getName());
		typeValue.setSelectedItem(syncSource.getSourceType());

		if (syncSource.getServerTimeZone() != null) {
			timeZoneValue.setSelectedItem(syncSource.getServerTimeZone().getID());
		} else {
			timeZoneValue.setSelectedItem(TimeZone.getDefault().getID());
		}

		caldavHostValue.setText (syncSource.getCaldavHost()   );

		if (syncSource.getCaldavPort() != null && syncSource.getCaldavPort() != "") {
			caldavPortValue.setText(syncSource.getCaldavPort());
		} else {
			caldavPortValue.setText("8080");
		}

		isSSLValue.setSelected(syncSource.getSSL());

		caldavBasURLeValue.setText (syncSource.getCaldavBaseURL());
		caldavUserValue.setText (syncSource.getCaldavUser());
		caldavPassValue.setText (syncSource.getCaldavPass());

		if (syncSource.getDbName() != null && syncSource.getDbName() != "") {
			dbNameValue.setText(syncSource.getDbName());
		} else {
			dbNameValue.setText("fnblds");
		}

		if (syncSource.getSourceURI() != null) {
			sourceUriValue.setEditable(false);
		}
	}

	/**
	 * Checks if the values provided by the user are all valid. In caso of errors,
	 * a IllegalArgumentException is thrown.
	 *
	 * @throws IllegalArgumentException if:
	 *         <ul>
	 *         <li>name, uri, type or directory are empty (null or zero-length)
	 *         <li>the types list length does not match the versions list length
	 *         </ul>
	 */
	private void validateValues() throws IllegalArgumentException
	{
		String value = nameValue.getText();

		if (StringUtils.isEmpty(value)) {
			throw new IllegalArgumentException("Field 'Name' cannot be empty. Please provide a SyncSource name.");
		}

		if (!StringUtils.containsOnly(value, NAME_ALLOWED_CHARS.toCharArray())) {
			throw new IllegalArgumentException("Only the following characters are allowed for field 'Name': \n" + NAME_ALLOWED_CHARS);
		}

		value = sourceUriValue.getText();
		if (StringUtils.isEmpty(value)) {
			throw new IllegalArgumentException("Field 'Source URI' cannot be empty. Please provide a SyncSource URI.");
		}

		value = dbNameValue.getText();
		if (StringUtils.isEmpty(value)) {
			throw new	IllegalArgumentException("Field 'Sync DBMS name' cannot be empty. Please provide a name.");
		}
	}

	/**
	 * Set syncSource properties with the values provided by the user.
	 */
	private void getValues(){

		CalDAVSyncSourceVEvent syncSource = (CalDAVSyncSourceVEvent)getSyncSource();

		syncSource.setSourceURI			(sourceUriValue.getText().trim());
		syncSource.setName					(nameValue.getText().trim()     );
		syncSource.setCaldavHost		(caldavHostValue.getText().trim() );
		syncSource.setCaldavPort			(caldavPortValue.getText().trim() );
		syncSource.setSSL					(isSSLValue.isSelected()        );
		syncSource.setCaldavBaseURL	(caldavBasURLeValue.getText().trim() );
		syncSource.setCaldavUser		(caldavUserValue.getText().trim() );
		syncSource.setCaldavPass		(new String(caldavPassValue.getPassword()));
		syncSource.setServerTimeZone	(TimeZone.getTimeZone( (String)timeZoneValue.getSelectedItem()));
		syncSource.setDbName				(dbNameValue.getText().trim()   );

		syncSource.setSourceType((String)typeValue.getSelectedItem());
		
		SyncSourceInfo newSourceInfo = new SyncSourceInfo(supportedTypes, typeValue.getSelectedIndex());
		
		newSourceInfo.setPreferred(typeValue.getSelectedIndex());
		syncSource.setInfo( newSourceInfo );
	}
}
