[![Experimental Project header](https://github.com/newrelic/opensource-website/raw/master/src/images/categories/Experimental.png)](https://opensource.newrelic.com/oss-category/#experimental)

# New Relic Java Instrumentation for Guidewire ClaimCenter and PolicyCenter

Custom instrumentation for Guidewire applications, including ClaimCenter and PolicyCenter.

## Installation

1. Obtain the latest release from this repository.
1. Extract the release into a local directory.
1. Transfer the extension JAR file to the target server
1. Copy the extension JAR file into the agent's `extensions` directory (relative to the directory containing the `newrelic.jar` file).
    *Note:* Create the `extensions` directory if it does not exist.
1. Restart your JVM
1. After the app has reloaded, generate traffic against your app that will trigger transactions that you expect to see.
1. To debug issues, set `log_level` to `finer` in `newrelic.yml`.

## Usage

Once installed, this extension will perform the following functions:

### Transaction Renaming

Transactions are named based on the names or values of POST parameters as follows:

* eventSource
* eventParam - if eventSource is _refresh_ and eventParam is not null
* Wizard:ScreenName if eventSource contains Wizard:Next_act

### Custom Parameters

These parameters are added to the transactions to which they apply:

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

## Support

New Relic hosts and moderates an online forum where customers can interact with New Relic employees as well as other customers to get help and share best practices. Like all official New Relic open source projects, there's a related Community topic in the New Relic Explorers Hub. You can find this project's topic/threads here:

>Add the url for the support thread here

## Contributing
We encourage your contributions to improve newrelic-java-guidewire! Keep in mind when you submit your pull request, you'll need to sign the CLA via the click-through using CLA-Assistant. You only have to sign the CLA one time per project.
If you have any questions, or to execute our corporate CLA, required if your contribution is on behalf of a company,  please drop us an email at opensource@newrelic.com.

## License
newrelic-java-guidewire is licensed under the [Apache 2.0](http://apache.org/licenses/LICENSE-2.0.txt) License.
>[If applicable: The newrelic-java-guidewire also uses source code from third-party libraries. You can find full details on which libraries are used and the terms under which they are licensed in the third-party notices document.]