package org.osaf.caldav4j.methods;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.webdav.lib.Ace;
import org.apache.webdav.lib.BaseProperty;
import org.apache.webdav.lib.Privilege;
import org.apache.webdav.lib.Property;
import org.apache.webdav.lib.PropertyName;
import org.apache.webdav.lib.properties.AclProperty;
import org.apache.webdav.lib.util.DOMUtils;
import org.apache.webdav.lib.util.QName;
import org.osaf.caldav4j.BaseTestCase;
import org.osaf.caldav4j.CalDAVConstants;
import org.osaf.caldav4j.model.request.CalendarDescription;
import org.osaf.caldav4j.model.request.DisplayName;
import org.osaf.caldav4j.model.request.PropProperty;
import org.osaf.caldav4j.model.response.CalDAVResponse;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class PropFindTest extends BaseTestCase {
	public PropFindTest(String method) {
		super(method);
		// TODO Auto-generated constructor stub
	}
	private static final Log log = LogFactory.getLog(PropFindTest.class);

	protected void setUp() throws Exception {
		super.setUp();
		mkcalendar(COLLECTION_PATH);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
//		del(COLLECTION_PATH + "/" + BaseTestCase.ICS_DAILY_NY_5PM);
		del(COLLECTION_PATH);
	}

	@SuppressWarnings("unchecked")
	public void donttestGetAcl() {
		HttpClient http = createHttpClient();
		HostConfiguration hostConfig = createHostConfiguration();

		PropFindMethod propfind = new PropFindMethod();
		propfind.setPath(caldavCredential.CALDAV_SERVER_WEBDAV_ROOT);

		PropProperty propFindTag = new PropProperty(CalDAVConstants.NS_DAV,"D","propfind");
		PropProperty aclTag = new PropProperty(CalDAVConstants.NS_DAV,"D","acl");
		PropProperty propTag = new PropProperty(CalDAVConstants.NS_DAV,"D","prop");
		propTag.addChild(aclTag);
//		propTag.addChild(new DisplayName());
//		propTag.addChild(new CalendarDescription());
		propFindTag.addChild(propTag);
		propfind.setReportRequest(propFindTag);
		propfind.setDepth(0);
		try {
			http.executeMethod(hostConfig,propfind);
			
			Enumeration<Property> myEnum = propfind.getResponseProperties(caldavCredential.CALDAV_SERVER_WEBDAV_ROOT);
			while (myEnum.hasMoreElements()) {
				AclProperty prop = (AclProperty) myEnum.nextElement();
				NodeList nl = prop.getElement().getElementsByTagName("ace");
				log.info(prop.getPropertyAsString());
				Ace[] aces = prop.getAces();
				log.info(aces[0]);

				log.info("There are aces # "+ nl.getLength() );
				for (int j=0; j<nl.getLength(); j++) {
					log.info("ace number "+ j);
					Element o = (Element) nl.item(j);
					// log.info("O:" +o.getNodeName() + o.getNodeValue() + o.getTextContent());

					NodeList nl1 = o.getElementsByTagName("grant");
					for ( int l=0; l<nl1.getLength(); l++) {
						Element o1 = (Element) nl1.item(l);
						log.info("O:" +o1.getTagName() );
						if (o1.getNodeValue() == null) {
							parseNode(o1);
						}
					}
					 nl1 = o.getElementsByTagName("principal");
						for ( int l=0; l<nl1.getLength(); l++) {
							Element o1 = (Element) nl1.item(l);
							log.info("O:" +o1.getTagName() );
							if (o1.getNodeValue() == null) {
								parseNode(o1);
							}
						}
				} // aces

				for (int k=0; k<prop.getAces().length; k++) {
					Ace ace = null;
					ace = (Ace) prop.getAces()[k];
					log.info("ace:" + prop.getElement().getChildNodes());
					log.info("inherited by: " + ace.getInheritedFrom() + ";" +
							"principal is: " + ace.getPrincipal() + ";" +
									"localname (if principal==property) e':" + ace.getProperty().getLocalName()+ ";" );
					Enumeration<Privilege> privs = ace.enumeratePrivileges();
					while (privs.hasMoreElements()) {
						Privilege priv = privs.nextElement();
						log.info("further elements: " +"ns:" + priv.getNamespace() +":"+ priv.getName() + 
								"; "+ priv.getParameter());
					}
					ace.addPrivilege(new Privilege(CalDAVConstants.NS_DAV,"spada","read"));
				}
				log.info(prop);
			}
			Ace test = new Ace("<property><owner/></property>");
			test.addPrivilege(new Privilege(CalDAVConstants.NS_DAV,"grant","read"));
			log.info(test);

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void donttestGetAcl_1() {
		HttpClient http = createHttpClient();
		HostConfiguration hostConfig = createHostConfiguration();

		PropFindMethod propfind = new PropFindMethod();
		propfind.setPath(caldavCredential.CALDAV_SERVER_WEBDAV_ROOT);

		PropProperty propFindTag = new PropProperty(CalDAVConstants.NS_DAV,"D","propfind");
		PropProperty aclTag = new PropProperty(CalDAVConstants.NS_DAV,"D","acl");
		PropProperty propTag = new PropProperty(CalDAVConstants.NS_DAV,"D","prop");
		propTag.addChild(aclTag);
		propTag.addChild(new DisplayName());
		propTag.addChild(new CalendarDescription());
		propFindTag.addChild(propTag);
		propfind.setReportRequest(propFindTag);
		propfind.setDepth(0);
		try {
			http.executeMethod(hostConfig,propfind);
			//Hashtable<String, CalDAVResponse> hashme = propfind.getResponseHashtable();

			Enumeration<Property> myEnum = propfind.getResponseProperties(caldavCredential.CALDAV_SERVER_WEBDAV_ROOT);
			while (myEnum.hasMoreElements()) {
				log.info("new Property element");
				BaseProperty e =  (BaseProperty) myEnum.nextElement();
				log.info(e.getName());
				AclProperty prop = (AclProperty) e;
				log.info(prop.getPropertyAsString());
				Ace[] aces = prop.getAces();
				log.info("There are aces # "+ aces.length );

				for (int k=0; k<aces.length; k++) {
					Ace ace = null;
					ace = (Ace) prop.getAces()[k];
					printAce(ace);
				}
				log.info(prop);
			}
			Ace test = new Ace("property");
			test.setProperty(new PropertyName(CalDAVConstants.NS_DAV, "owner"));
			test.addPrivilege(new Privilege(CalDAVConstants.NS_DAV,"grant","read"));
			printAce(test);


		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * TODO this test will work only on bedework which has a set of permission set
	 */
	public void testNewPropfind() {
		log.info("New Propfind");
		HttpClient http = createHttpClient();
		HostConfiguration hostConfig = createHostConfiguration();

		PropFindMethod propfind = new PropFindMethod();
		propfind.setPath(COLLECTION_PATH);

		PropProperty propFindTag = new PropProperty(CalDAVConstants.NS_DAV,"D","propfind");
		PropProperty aclTag = new PropProperty(CalDAVConstants.NS_DAV,"D","acl");
		PropProperty propTag = new PropProperty(CalDAVConstants.NS_DAV,"D","prop");
		propTag.addChild(aclTag);
		propTag.addChild(new DisplayName());
		propTag.addChild(new CalendarDescription());
		propFindTag.addChild(propTag);
		propfind.setReportRequest(propFindTag);
		propfind.setDepth(0);
		try {
			http.executeMethod(hostConfig,propfind);
			
			// check that Calendar-description and DisplayName matches
			log.debug("DisplayName: " + propfind.getDisplayName(COLLECTION_PATH));
			assertEquals(caldavCredential.COLLECTION.replaceAll("/$", ""), propfind.getDisplayName(COLLECTION_PATH).replaceAll("/$", ""));			

			log.debug("CalendarDescription: " +  propfind.getCalendarDescription(COLLECTION_PATH));
			assertEquals(CALENDAR_DESCRIPTION, propfind.getCalendarDescription(COLLECTION_PATH));

			// check that ACLs matches
	    	AclProperty acls = propfind.getAcl(COLLECTION_PATH);
	    	assertNotNull(acls);
	    	log.info(acls.getPropertyAsString());
			Ace[] aces = acls.getAces();
			log.info("There are aces # "+ aces.length );

			for (int k=0; k<aces.length; k++) {
				Ace ace = null;
				ace = aces[k];
				assertEquals("/user", ace.getInheritedFrom());
				switch (k) {
				case 0:
					assertEquals("property", ace.getPrincipal());
					assertEquals("owner", ace.getProperty().getLocalName());
					Privilege p = (Privilege) ace.enumeratePrivileges().nextElement();
					assertEquals("all", p.getName());
					break;

				default:
					break;
				}
				printAce(ace);
			}

		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@SuppressWarnings("unchecked")
	private void printAce(Ace ace) {
		PropProperty principal =	new PropProperty(CalDAVConstants.NS_DAV, "D", "property");
		principal.addChild(new PropProperty(ace.getProperty().getNamespaceURI(), "D", ace.getProperty().getLocalName()));
		String stringFormattedAci = String.format("ACE:" + 
				" principal: %s ", "property".equals(ace.getPrincipal()) ? prettyPrint(principal)  : ace.getPrincipal() +
						" ereditata da: " + ace.getInheritedFrom() + ";" );
		log.debug( stringFormattedAci );
		Enumeration<Privilege> privs = ace.enumeratePrivileges();
		log.debug("privileges are" );
		while (privs.hasMoreElements()) {
			Privilege priv = privs.nextElement();						
			log.debug(String.format("<privilege><%s:%s/></privilege>",  priv.getNamespace(), priv.getName() ));
		}

	}
	private void parseNode(Element e) {
		try {
			log.info("node is:" + e.getClass().getName());			
			NodeList nl = e.getChildNodes();
			for (int i=0; i< nl.getLength(); i++) {
				Element el =  DOMUtils.getFirstElement(nl.item(i) , "DAV:", "privilege");
				log.info("child is:" + el.getClass().getName());
			//	log.info("parseNode:" + el.getNodeName() + el.getNodeType() + el.getNodeValue()+el.getTextContent());
			}
		} catch (Exception ex) {
			log.warn("Error while parsing node: "+ e);
			log.warn("Error while parsing node: "+ ex);
		}
	}
}