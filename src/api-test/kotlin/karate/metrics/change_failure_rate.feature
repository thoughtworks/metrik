Feature: Single pipeline CFR. Card #53
// refer to https://docs.google.com/presentation/d/1YHNPXGW5pEiAXL5jqtyuB8tzytz37CkJyZthtKtcn1Q/edit#slide=id.gb470dde5f6_0_67 for manual testing result
    Background: 
        Given url baseUrl
        Given path '/api/pipeline/metrics'

    Scenario: case1-4km backend API should calculate CFR correctly for build which the committed date is out of date range but the deployment date is within date range should be counted when do CFR calculation
        * def query = { endTime: '1610639999000', pipelineId: '6012505c42fbb8439fc08b21', startTime: '1609430400000', targetStage: 'Deploy to DEV', unit: 'Fortnightly'}
        When params query
        And method get
        Then status 200
        Then match response.changeFailureRate.summary.value == 16.67
        Then match response.changeFailureRate.summary.level == "HIGH"
        Then match response.changeFailureRate.details[0].value == 16.67


    Scenario: case2-4km backend API should calculate CFR correctly for build which the targeted stage is aborted should not be counted in when do CFR calculation
        * def query = { endTime: '1610035199000', pipelineId: '6012505c42fbb8439fc08b21', startTime: '1609430400000', targetStage: 'Deploy to UAT', unit: 'Fortnightly'}
        When params query
        And method get
        Then status 200
        Then match response.changeFailureRate.summary.value == 25.00
        Then match response.changeFailureRate.summary.level == "HIGH"
        Then match response.changeFailureRate.details[0].value == 25.00

    Scenario: case3-4km backend API should calculate CFR correctly for build which the previous stage failed but the targeted stage is success should still be counted in when do CFR calculation
        * def query = { endTime: '1610639999000', pipelineId: '6012505c42fbb8439fc08b21', startTime: '1609430400000', targetStage: 'Deploy to UAT', unit: 'Fortnightly'}
        When params query
        And method get
        Then status 200
        Then match response.changeFailureRate.summary.value == 20.00
        Then match response.changeFailureRate.summary.level == "HIGH"
        Then match response.changeFailureRate.details[0].value == 20.00


    Scenario: case4-4km backend API should calculate CFR correctly for continuously failed builds should be counted correctly when do CRF calculation
        * def query = { endTime: '1611331199000', pipelineId: '6012505c42fbb8439fc08b21', startTime: '1610640000000', targetStage: 'Deploy to UAT', unit: 'Fortnightly'}
        When params query
        And method get
        Then status 200
        Then match response.changeFailureRate.summary.value == 40.00
        Then match response.changeFailureRate.summary.level == "MEDIUM"
        Then match response.changeFailureRate.details[0].value == 40.00

    Scenario: case5-4km backend API should calculate CFR correctly for build which the commit date is within user selected date range but deployment is out of the range should not be counted in when do CRF calculation
        * def query = { endTime: '1611849599000', pipelineId: '6012505c42fbb8439fc08b21', startTime: '1610640000000', targetStage: 'Deploy to UAT', unit: 'Fortnightly'}
        When params query
        And method get
        Then status 200
        Then match response.changeFailureRate.summary.value == 40.00
        Then match response.changeFailureRate.summary.level == "MEDIUM"
        Then match response.changeFailureRate.details[0].value == 40.00
    Scenario: case6-4km backend API should calculate CFR correctly by fortnightly and the time split works well ( data range contains completed fortnight)
        * def query = { endTime: '1611849599000', pipelineId: '6012505c42fbb8439fc08b21', startTime: '1609430400000', targetStage: 'Deploy to UAT', unit: 'Fortnightly'}
        When params query
        And method get
        Then status 200
        Then match response.changeFailureRate.summary.value == 30.00
        Then match response.changeFailureRate.summary.level == "MEDIUM"
        Then match response.changeFailureRate.details[0].value == 20.00
        Then match response.changeFailureRate.details[1].value == 40.00
    Scenario: case7-4km backend API should calculate CFR correctly by fortnightly and the time split works well (date range contains incomplete fortnight)
        * def query = { endTime: '1611849599000', pipelineId: '6012505c42fbb8439fc08b21', startTime: '1609430400000', targetStage: 'Deploy to UAT', unit: 'Fortnightly'}
        When params query
        And method get
        Then status 200
        Then match response.changeFailureRate.summary.value == 30.00
        Then match response.changeFailureRate.summary.level == "MEDIUM"
        Then match response.changeFailureRate.details[0].value == 20.00
        Then match response.changeFailureRate.details[1].value == 40.00
    Scenario: case8-4km backend API should calculate CFR correctly by monthly and the time split works well ( not cross a calendar month)
        * def query = { endTime: '1611849599000', pipelineId: '6012505c42fbb8439fc08b21', startTime: '1609430400000', targetStage: 'Deploy to UAT', unit: 'Monthly'}
        When params query
        And method get
        Then status 200
        Then match response.changeFailureRate.summary.value == 30.00
        Then match response.changeFailureRate.summary.level == "MEDIUM"
        Then match response.changeFailureRate.details[0].value == 30.00
    Scenario: case9-4km backend API should calculate CFR correctly by monthly and the time split works well ( cross a calendar month)
        * def query = { endTime: '1611849599000', pipelineId: '6012505c42fbb8439fc08b21', startTime: '1606665600000', targetStage: 'Deploy to UAT', unit: 'Monthly'}
        When params query
        And method get
        Then status 200
        Then match response.changeFailureRate.summary.value == 30.00
        Then match response.changeFailureRate.summary.level == "MEDIUM"
        Then match response.changeFailureRate.details[0].value == "NaN"
        Then match response.changeFailureRate.details[1].value == "NaN"
        Then match response.changeFailureRate.details[2].value == 30.00


