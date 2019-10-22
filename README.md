# guidewire-policycenter
Custom instrumentation for Guidewire applications, including ClaimCenter and PolicyCenter.
### Transaction Renaming
Default transaction names for these applications are low value (policycenter.do or claimcenter.do).  Transactions are renamed based on the names or values of POST parameters as follows:
* eventSource
* eventParam - if eventSource is _refresh_ and eventParam is not null
* Wizard:ScreenName if eventSource contains Wizard:Next_act

### Custom Parameters
* JSESSIONID* cookie value
* ThreadName
* eventSource
* eventParam
* Wizard (conditional)
* Screen (conditional)
* SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:ClaimNumber
* SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:PolicyNumber
* SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:FirstName
* SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:LastName
* SimpleClaimSearch:SimpleClaimSearchScreen:SimpleClaimSearchDV:CompanyName
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:ClaimNumber
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:PolicyNumber
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:FirstName
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:LastName
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:AssignedToGroup
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:AssignedToUser
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:CreatedBy
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:CatNumber
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchRequiredInputSet:VinNumber
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:lossStateActiveSearch
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:ClaimStatus
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:nwPolicyType
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:LossType
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:DateSearch:DateSearchRangeValue
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:DateSearch:DateSearchStartDate
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchOptionalInputSet:DateSearch:DateSearchEndDate
* ClaimSearch:ClaimSearchScreen:ClaimSearchDV:ClaimSearchAndResetInputSet:Search_act
* ClaimNewDocumentFromTemplateWorksheet:NewDocumentFromTemplateScreen:NewTemplateDocumentDV:CreateDocument
* ClaimNewDocumentFromTemplateWorksheet:NewDocumentFromTemplateScreen:NewTemplateDocumentDV:CreateDocument_act
* ClaimNewDocumentFromTemplateWorksheet:NewDocumentFromTemplateScreen:NewTemplateDocumentDV:ViewLink_link
* Login:LoginScreen:LoginDV:username
* NewSubmission:NewSubmissionScreen:ProductSettingsDV:DefaultBaseState
