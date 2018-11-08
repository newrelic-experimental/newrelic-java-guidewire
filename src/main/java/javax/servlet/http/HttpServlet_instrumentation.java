package javax.servlet.http;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.newrelic.agent.bridge.AgentBridge;
import com.newrelic.agent.bridge.TransactionNamePriority;
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
								nrLogger.log(Level.FINER, "GUIDEWIRE - adding attribute for request parameter: " + paramDisplayName + " = " + pValueString);
								
							}
						} 
					}
					
				}
				
				nrLogger.log(Level.FINER, "GUIDEWIRE - processing cookies");
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					for (int i = 0; i < cookies.length; i++) {
						Cookie cookie = cookies[i];
						String cName = cookie.getName();
						if (cName != null && cName.startsWith("JSESSIONID")) {
							NewRelic.addCustomParameter(cookie.getName(),
									cookie.getValue());
							nrLogger.log(
									Level.FINER,
									"GUIDEWIRE - adding attribute for JSESSIONID cookie: "
											+ cookie.getName() + " = "
											+ cookie.getValue());
						}
					}
				}
				
				nrLogger.log(Level.FINER, "GUIDEWIRE - processing eventSource");
				String eventSource = request.getParameter("eventSource");
				if(eventSource != null && !eventSource.isEmpty()) {
					if(eventSource.equals("_refresh_"))
					{
						String eventParam = request.getParameter("eventParam");
						if(eventParam != null && !eventParam.isEmpty())
						{
							nrLogger.log(Level.FINER,  "GUIDEWIRE - Setting eventParam to transaction name since eventSource was _refresh_: "+ eventParam);
							AgentBridge.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "eventParam", eventParam);
						}
						else
						{
							nrLogger.log(Level.FINER, "GUIDEWIRE - Setting eventSource to transaction name: " + eventSource);
							AgentBridge.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "eventSource", eventSource);
						}
					}
					else
					{
						nrLogger.log(Level.FINER, "GUIDEWIRE - Setting eventSource to transaction name: " + eventSource);
						AgentBridge.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "eventSource", eventSource);
					}
				}
				

			}
			
			String name = Thread.currentThread().getName();
			NewRelic.addCustomParameter("ThreadName", name);
			
		} catch (Exception e) {
			nrLogger.log(Level.WARNING, "GUIDEWIRE -- exception processing servlet service method: " + e.getMessage());
		}
		
	}
	
	private synchronized void initMap()  {
		if (selectedParametersMap != null) {
			// must have initialized in another thread
			NewRelic.getAgent().getLogger().log(Level.INFO, "GUIDEWIRE parameter map already initialized");
			return;
		}
		NewRelic.getAgent().getLogger().log(Level.INFO, "GUIDEWIRE Initializing parameter map");
		selectedParametersMap = new HashMap<String, String>();
		//Starting your parameter key with % will trigger contains logic instead of expecting an exact match
		selectedParametersMap.put("eventSource", "eventSource");
		selectedParametersMap.put("eventParam", "eventParam");
		//Below entries are Nationwide specific- modify according to customer needs
		selectedParametersMap.put("SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:ClaimNumber", "Claim Number");
		selectedParametersMap.put("SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:PolicyNumber", "Policy Number");
		selectedParametersMap.put("SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:FirstName", "First Name");
		selectedParametersMap.put("SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:LastName", "Last Name");
		selectedParametersMap.put("SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:CompanyName", "Organization Name");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:ClaimNumber", "Claim Number");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:PolicyNumber", "Policy Number");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:FirstName", "First Name");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:LastName", "Last Name");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:AssignedToGroup", "Assigned To Group");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:AssignedToUser", "Assigned To User");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:CreatedBy", "Created By");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:CatNumber", "CAT/STORM");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:VinNumber", "VIN");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:lossStateActiveSearch", "Loss State");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:ClaimStatus", "Claim Status");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:nwPolicyType", "Policy Type");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:LossType", "Loss Type");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:DateSearch:DateSearchRangeValue", "Search For Date Since");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:DateSearch:DateSearchStartDate", "Search For Data From");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:DateSearch:DateSearchEndDate", "Search For Data To");
		selectedParametersMap.put("ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchAndResetInputSet:Search_act", "Search_act");
		selectedParametersMap.put("ClaimNewDocumentFromTemplateWorksheet:NewDocumentFromTemplateScreen:NewTemplateDocumentDV:CreateDocument", "Create Document From Template");
		selectedParametersMap.put("ClaimNewDocumentFromTemplateWorksheet:NewDocumentFromTemplateScreen:NewTemplateDocumentDV:CreateDocument_act", "Create Document From act");
		selectedParametersMap.put("ClaimNewDocumentFromTemplateWorksheet:NewDocumentFromTemplateScreen:NewTemplateDocumentDV:ViewLink_link", "ViewLink_link");
		selectedParametersMap.put("Login:LoginScreen:LoginDV:username", "User Name");
		selectedParametersMap.put("NewSubmission:NewSubmissionScreen:ProductSettingsDV:DefaultBaseState", "State");
		
	
		NewRelic.getAgent().getLogger().log(Level.INFO, "GUIDEWIRE Initialized parameter map: " + selectedParametersMap.size());
	}

}

