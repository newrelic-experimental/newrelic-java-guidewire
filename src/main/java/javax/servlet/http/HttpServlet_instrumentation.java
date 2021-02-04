package javax.servlet.http;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

//import com.newrelic.agent.bridge.AgentBridge;
//import com.newrelic.agent.bridge.TransactionNamePriority;
import com.newrelic.api.agent.Logger;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.api.agent.Trace;
import com.newrelic.api.agent.weaver.MatchType;
import com.newrelic.api.agent.weaver.NewField;
import com.newrelic.api.agent.weaver.Weave;
import com.newrelic.api.agent.weaver.Weaver;

@Weave(originalName = "javax.servlet.http.HttpServlet", type = MatchType.BaseClass)
public abstract class HttpServlet_instrumentation {

	@NewField
	private static Map<String, String> selectedParametersMap = null;

	@SuppressWarnings("unchecked")
	@Trace(dispatcher = true)
	protected void service(HttpServletRequest request, HttpServletResponse response) {
		Logger nrLogger = NewRelic.getAgent().getLogger();
		nrLogger.log(Level.FINER, "GUIDEWIRE - Starting HttpServlet service method");
		nrLogger.log(Level.FINER, "GUIDEWIRE - browser character encoding before GW: " + request.getCharacterEncoding());
		nrLogger.log(Level.FINER, "GUIDEWIRE - Calling original HttpServlet.service method");
		Weaver.callOriginal();

		nrLogger.log(Level.FINER, "GUIDEWIRE - browser character encoding after GW: " + request.getCharacterEncoding());

		try {
			if (selectedParametersMap == null) {
				this.initMap();
			}

			if (request != null) {

				Map<String, String[]> pMap = request.getParameterMap();
				boolean bWizardAndScreenCheckNeeded = false;
				boolean bWizardAndScreenFound = false;
				boolean bEventSourceValue = false;
				String pWizard = "";
				String pScreen = "";
				String eventSource = request.getParameter("eventSource");
				// ***SAML***
				NewRelic.getAgent().getLogger().log(Level.FINER, "GUIDEWIRE - SAML getParameter Response: " + request.getParameter("SAMLResponse"));
				String sR = request.getParameter("SAMLResponse");

				if (eventSource != null && !eventSource.isEmpty()) {
					// Setting a boolean so we don't have to do string functions
					// on eventSource again later.
					bEventSourceValue = true;

					if (eventSource.indexOf("Wizard:Next_act") > -1) {
						// Setting a flag to activate additional logic to report
						// wizard and screen name parameters and include them in
						// the trans name
						bWizardAndScreenCheckNeeded = true;
					}
				}

				// ***SAML userID Start***
				try {
					if (sR != null) {

						// Rename LogIn Transcation.
						nrLogger.log(Level.FINER, "GUIDEWIRE - SAML userID Area Started");
						nrLogger.log(Level.FINER, "GUIDEWIRE - Setting transaction name to LogIn");
						NewRelic.setTransactionName("saml", "LogIn");

						//Decoding SAML Response to get userID
						byte[] decodedBytes = Base64Decoder.decode(sR);
						String decodedSAML = new String(decodedBytes);
						NewRelic.getAgent().getLogger().log(Level.FINER, "GUIDEWIRE - Decoded SAML: " + decodedSAML);

						// Adding Custom Parameter
						// Original Ping Identity Config
						// NewRelic.addCustomParameter("userID",decodedSAML.substring(decodedSAML.lastIndexOf("</saml:NameID>")- 7).substring(0, 7));

						// Updating for Okta
						NewRelic.addCustomParameter("userID", decodedSAML.substring(decodedSAML.lastIndexOf("</saml2:NameID>") - 7).substring(0, 7));
						nrLogger.log(Level.FINER, "GUIDEWIRE - adding attribute for SAML userID: " + decodedSAML.substring(decodedSAML.lastIndexOf("</saml2:NameID>") - 7).substring(0, 7));
					}

				} catch (Exception e) {
					nrLogger.log(Level.WARNING, "GUIDEWIRE -- exception parsing userId from SAML Response: " + e.toString());
				}
				// ***SAML userID End***

				// Get all request parameter keys and add custom parameters if
				// they are found in the list of known values.
				for (String pKey : pMap.keySet()) {
					nrLogger.log(Level.FINER, "GUIDEWIRE - Processing request param: " + pKey);
					String paramDisplayName = selectedParametersMap.get(pKey);

					if (paramDisplayName != null) {
						nrLogger.log(Level.FINER, "GUIDEWIRE - Found request param: " + pKey);
						String[] pValue = request.getParameterValues(pKey);
						if ((pValue != null) && (pValue.length > 0)) {
							String pValueString = "";
							for (String pThisString : pValue) {
								pValueString = pValueString + " " + pThisString;
							}
							pValueString = pValueString.trim();
							if (!pValueString.isEmpty()) {
								NewRelic.addCustomParameter(paramDisplayName, pValueString);
								nrLogger.log(Level.FINER, "GUIDEWIRE - adding attribute for request parameter: "
										+ paramDisplayName + " = " + pValueString);

							}
						}
					}

					// Check if we need to capture wizard name and screen name.
					// Separate if statement to attempt performance improvement.
					if ((bWizardAndScreenCheckNeeded) && (!bWizardAndScreenFound)) {
						// Capture wizard name and screen name in case they are
						// needed to set the transaction name. They would appear
						// in different parts of a single pKey value. We only
						// want to store this if we find both.
						if ((pKey.indexOf("Wizard:") > -1) && (pKey.indexOf("Screen:") > -1)) {
							String[] pKeyParts = pKey.split(":");
							boolean bWizardFound = false;
							boolean bScreenFound = false;
							for (String pSplitKey : pKeyParts) {
								if (pSplitKey.endsWith("Wizard")) {
									pWizard = pSplitKey.trim();
									bWizardFound = true;
								}
								if (pSplitKey.endsWith("Screen")) {
									pScreen = pSplitKey.trim();
									bScreenFound = true;
								}
								if ((bWizardFound) && (bScreenFound)) {
									bWizardAndScreenFound = true;
									break;
								}
							}
						}
					}

					// Attempt to extract name/value pairs from the
					// UserIdAndOrganization and SectionCounter
					if ((pKey.indexOf("UserIdAndOrganization") > -1) || (pKey.indexOf("SectionCounter") > -1)) {
						nrLogger.log(Level.FINER,
								"GUIDEWIRE - key contains UserIdAndOrganization or SectionCounter, attempting to split into multiple attributes.");
						String[] pValue = request.getParameterValues(pKey);
						// This field should contain ~ as a field separator
						if ((pValue != null) && (pValue.length > 0)) {
							String pValueString = "";
							for (String pThisString : pValue) {
								pValueString = pValueString + " " + pThisString;
							}
							pValueString = pValueString.trim();
							if (!pValueString.isEmpty()) {
								if (pValueString.indexOf("~") > -1) {
									nrLogger.log(Level.FINER,
											"GUIDEWIRE - Key contains an ~, attempting to split into name value pairs.");
									String[] valueFields = pValueString.split("~");
									for (String field : valueFields) {
										if (field.indexOf(":") > -1) {
											nrLogger.log(Level.FINER, "GUIDEWIRE - split string: " + field);
											String[] nameValuePair = field.split(":", 2);
											if (nameValuePair[0].indexOf("TimeStamp") == -1) {
												nrLogger.log(Level.FINER, "GUIDEWIRE - Adding attribute "
														+ nameValuePair[0] + " = " + nameValuePair[1]);
												NewRelic.addCustomParameter(nameValuePair[0], nameValuePair[1]);
											} else {
												// Overriding timestamp name to
												// avoid Insights conflicts.
												nrLogger.log(Level.FINER, "GUIDEWIRE - Adding attribute GWTimeStamp = "
														+ nameValuePair[1]);
												NewRelic.addCustomParameter("GWTimestamp", nameValuePair[1]);
											}
										}
									}
								}
							}
						}

					}

					// Policy type can be determined based on these patterns in
					// the POST parameters
					if (pKey.indexOf(":PA") > -1) {
						nrLogger.log(Level.FINER,
								"GUIDEWIRE - Reporting LOB of auto based on detecting :PA in key: " + pKey);
						NewRelic.addCustomParameter("LOB", "Auto");
					}

					if (pKey.indexOf(":HO") > -1) {
						nrLogger.log(Level.FINER,
								"GUIDEWIRE - Reporting LOB of Property based on detecting :HO in key: " + pKey);
						NewRelic.addCustomParameter("LOB", "Property");
					}

					if (pKey.indexOf(":PUP") > -1) {
						nrLogger.log(Level.FINER,
								"GUIDEWIRE - Reporting LOB of Umbrella based on detecting :PUP in key: " + pKey);
						NewRelic.addCustomParameter("LOB", "Umbrella");
					}
				}

				nrLogger.log(Level.FINER, "GUIDEWIRE - processing cookies");
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					for (int i = 0; i < cookies.length; i++) {
						Cookie cookie = cookies[i];
						String cName = cookie.getName();
						if (cName != null && cName.startsWith("JSESSIONID")) {
							NewRelic.addCustomParameter(cookie.getName(), cookie.getValue());
							nrLogger.log(Level.FINER, "GUIDEWIRE - adding attribute for JSESSIONID cookie: "
									+ cookie.getName() + " = " + cookie.getValue());
						}
					}
				}

				if (bEventSourceValue) {
					if (eventSource.equals("_refresh_")) {
						String eventParam = request.getParameter("eventParam");
						if (eventParam != null && !eventParam.isEmpty()) {
							nrLogger.log(Level.FINER,
									"GUIDEWIRE - Setting eventParam to transaction name since eventSource was _refresh_: "
											+ eventParam);
//							AgentBridge.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "eventParam", eventParam);
							NewRelic.setTransactionName("eventParam", eventParam);
						} else {
							nrLogger.log(Level.FINER,
									"GUIDEWIRE - Setting eventSource to transaction name: " + eventSource);
//							AgentBridge.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "eventSource", eventSource);
							NewRelic.setTransactionName("eventSource", eventSource);
						}
					} else {
						if ((bWizardAndScreenCheckNeeded) && (bWizardAndScreenFound)) {
							nrLogger.log(Level.FINER,
									"GUIDEWIRE - Setting Wizard Name and Screen name to transaction name since eventSource contained Wizard:Next_Act: "
											+ pWizard + ":" + pScreen);
//							AgentBridge.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "WizardAndScreenName",pWizard + ":" + pScreen);
							NewRelic.setTransactionName("WizardAndScreenName", pWizard + ":" + pScreen);
							NewRelic.addCustomParameter("Wizard", pWizard);
							NewRelic.addCustomParameter("Screen", pScreen);
						} else {
							nrLogger.log(Level.FINER,
									"GUIDEWIRE - Setting eventSource to transaction name: " + eventSource);
//							AgentBridge.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "eventSource", eventSource);
							NewRelic.setTransactionName("eventSource", eventSource);
						}
					}
				}

			}

			String name = Thread.currentThread().getName();
			NewRelic.addCustomParameter("ThreadName", name);

		} catch (Exception e) {
			nrLogger.log(Level.WARNING, "GUIDEWIRE -- exception processing servlet service method: " + e.getMessage());
		}

	}

	private synchronized void initMap() {
		if (selectedParametersMap != null) {
			// must have initialized in another thread
			NewRelic.getAgent().getLogger().log(Level.FINER, "GUIDEWIRE - parameter map already initialized");
			return;
		}
		NewRelic.getAgent().getLogger().log(Level.FINER, "GUIDEWIRE - Initializing parameter map");
		selectedParametersMap = new HashMap<String, String>();
		// Starting your parameter key with % will trigger contains logic
		// instead of expecting an exact match
		selectedParametersMap.put("eventSource", "eventSource");
		selectedParametersMap.put("eventParam", "eventParam");
		// Modify according to customer needs
		selectedParametersMap.put("SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:ClaimNumber",
				"Claim Number");
		selectedParametersMap.put("SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:PolicyNumber",
				"Policy Number");
		selectedParametersMap.put("SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:FirstName",
				"First Name");
		selectedParametersMap.put("SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:LastName",
				"Last Name");
		selectedParametersMap.put("SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:CompanyName",
				"Organization Name");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:ClaimNumber",
				"Claim Number");
		selectedParametersMap.put(
				"ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:PolicyNumber",
				"Policy Number");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:FirstName",
				"First Name");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:LastName",
				"Last Name");
		selectedParametersMap.put(
				"ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:AssignedToGroup",
				"Assigned To Group");
		selectedParametersMap.put(
				"ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:AssignedToUser",
				"Assigned To User");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:CreatedBy",
				"Created By");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:CatNumber",
				"CAT/STORM");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:VinNumber",
				"VIN");
		selectedParametersMap.put(
				"ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:lossStateActiveSearch",
				"Loss State");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:ClaimStatus",
				"Claim Status");
		selectedParametersMap.put(
				"ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:nwPolicyType", "Policy Type");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:LossType",
				"Loss Type");
		selectedParametersMap.put(
				"ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:DateSearch:DateSearchRangeValue",
				"Search For Date Since");
		selectedParametersMap.put(
				"ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:DateSearch:DateSearchStartDate",
				"Search For Data From");
		selectedParametersMap.put(
				"ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:DateSearch:DateSearchEndDate",
				"Search For Data To");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchAndResetInputSet:Search_act",
				"Search_act");
		selectedParametersMap.put(
				"ClaimNewDocumentFromTemplateWorksheet:NewDocumentFromTemplateScreen:NewTemplateDocumentDV:CreateDocument",
				"Create Document From Template");
		selectedParametersMap.put(
				"ClaimNewDocumentFromTemplateWorksheet:NewDocumentFromTemplateScreen:NewTemplateDocumentDV:CreateDocument_act",
				"Create Document From act");
		selectedParametersMap.put(
				"ClaimNewDocumentFromTemplateWorksheet:NewDocumentFromTemplateScreen:NewTemplateDocumentDV:ViewLink_link",
				"ViewLink_link");
		selectedParametersMap.put("Login:LoginScreen:LoginDV:username", "User Name");
		selectedParametersMap.put("NewSubmission:NewSubmissionScreen:ProductSettingsDV:DefaultBaseState", "State");
		//Added below Params to assist with Commercial Business monitoring
		//Below 2 are from the same Transaction
		selectedParametersMap.put("FNOLWizard:FNOLWizard_FindPolicyScreen:FNOLWizardFindPolicyPanelSet:policyNumber", "PolicyNumber");
		selectedParametersMap.put("FNOLWizard:FNOLWizard_FindPolicyScreen:FNOLWizardFindPolicyPanelSet:ClaimMode", "ClaimMode");
		//Below 4 are all found in the same Transaction.  i.e. Trying to differentiate how many claims of Each LOB are being created
		selectedParametersMap.put("FNOLWizard:FNOLWizard_FindPolicyScreen:FNOLWizardFindPolicyPanelSet:Type", "LOB");
		selectedParametersMap.put("FNOLWizard:FNOLWizard_FindPolicyScreen:FNOLWizardFindPolicyPanelSet:NewClaimPolicyGeneralPanelSet:NewClaimPolicyGeneralDV:Insured_Name", "InsuredName");
		selectedParametersMap.put("FNOLWizard:FNOLWizard_FindPolicyScreen:FNOLWizardFindPolicyPanelSet:ClaimLossType", "ClaimLossType");
		selectedParametersMap.put("FNOLWizard:FNOLWizard_FindPolicyScreen:FNOLWizardFindPolicyPanelSet:Claim_lossTime", "ClaimLossTime");
		

		NewRelic.getAgent().getLogger().log(Level.FINER,
				"GUIDEWIRE - Initialized parameter map: " + selectedParametersMap.size());
	}

}
