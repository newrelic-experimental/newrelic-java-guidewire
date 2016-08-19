package javax.servlet.http;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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
	
	public HttpServlet_instrumentation() {
	
	}
	
	@SuppressWarnings("unchecked")
	@Trace(dispatcher = true)
	protected void service(HttpServletRequest request, HttpServletResponse response) {
		Logger nrLogger = NewRelic.getAgent().getLogger();
		nrLogger.log(Level.FINER, "NATIONWIDE - Starting HttpServlet service method");
		if (selectedParametersMap == null) {
			selectedParametersMap = new HashMap<String, String>();
			selectedParametersMap.put("eventSource", "eventSource");
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
			selectedParametersMap.put("ClaimNewDocumentFromTemplateWorksheet:NewDocumentFromTemplateScreen:NewTemplateDocumentDV:CreateDocument", "Create Document From Template");
			selectedParametersMap.put("Login:LoginScreen:LoginDV:username", "User Name");
			
			try {
				NewRelic.getAgent().getLogger().log(Level.INFO, "HttpServlet_instrumentation reading properties");
				
				InputStream iStream = this.getClass().getResourceAsStream("params.properties");
				
				if (iStream != null) {
					NewRelic.getAgent().getLogger().log(Level.INFO, "HttpServlet_instrumentation loading properties");
					Properties props = new Properties();
					props.load(iStream);
					Enumeration<?> e = props.propertyNames();
					while (e.hasMoreElements()) {
						String key = (String) e.nextElement();
						String value = props.getProperty(key);
						selectedParametersMap.put(key.trim(), value.trim());
					}	
				} else {
					NewRelic.getAgent().getLogger().log(Level.INFO, "HttpServlet_instrumentation could not load params.properties");
				}
			} catch (Exception e) {
				NewRelic.getAgent().getLogger().log(Level.ALL, "Error in HttpServlet_instrumentation, " + e.getMessage());
			}
			NewRelic.getAgent().getLogger().log(Level.INFO, "Initialized parameter map: " + selectedParametersMap);
		}
		
		if (request != null) {

			Map<String, String[]> pMap = request.getParameterMap();
			for (String pKey : pMap.keySet()) {
				String paramDisplayName = selectedParametersMap.get(pKey);
				if (paramDisplayName != null) {
					String[] pValue = request.getParameterValues(pKey);
					if ((pValue != null) && (pValue.length > 0)) {
						String pValueString = "";
						for (String pThisString : pValue) {
							pValueString = pValueString + " " + pThisString;
						}
						pValueString = pValueString.trim();
						if (!pValueString.isEmpty()) {
							NewRelic.addCustomParameter(paramDisplayName, pValueString);
							nrLogger.log(Level.FINER, "NATIONWIDE - request parameter: " + paramDisplayName + " = " + pValueString);
						}
					} 
				} 
			}

			String eventSource = request.getParameter("eventSource");
			if(eventSource != null && !eventSource.isEmpty()) {
				nrLogger.log(Level.FINER, "NATIONWIDE - Setting eventSource to transaction name: " + eventSource);
				AgentBridge.getAgent().getTransaction().setTransactionName(TransactionNamePriority.CUSTOM_HIGH, true, "eventSource", eventSource);
			}
			
			String reqURI = request.getRequestURI();
			if(reqURI != null && !reqURI.isEmpty()) {
				NewRelic.addCustomParameter("URI", reqURI);
				nrLogger.log(Level.FINER, "URI = " + reqURI);
				if (eventSource == null || eventSource.isEmpty()) {
					nrLogger.log(Level.FINER, "NATIONWIDE - eventSource not found. Setting URI to transaction name: " + reqURI);
					AgentBridge.getAgent().getTransaction().setTransactionName(TransactionNamePriority.FRAMEWORK_HIGH, true, "servlet", reqURI);
				}
			}
		}
		nrLogger.log(Level.FINER, "NATIONWIDE - Calling original HttpServlet.service method");
		Weaver.callOriginal();
	}
}