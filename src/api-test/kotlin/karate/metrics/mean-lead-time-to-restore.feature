Feature: Single pipeline MTTR and Unit conversion. Card #52 and Card#123
// refer to https://docs.google.com/presentation/d/12dQD9Zhkj4NXEa7Xdecb4wAr_fXIq8fJ7nW_4IZ2O_c/edit#slide=id.gb9d0735675_0_384 for manual testing result
    Background: 
        Given url baseUrl
        Given path '/api/pipeline/metrics'

    Scenario: 4km backend API should calculate MTTR correctly for date range within which the 1st build is successful but the last build before the date range is failed
        * def query = { endTime: '1578239999000', pipelineId: '6018c32f42fbb8439fc08b24', startTime: '1578102000000', targetStage: 'Deploy to UAT', unit: 'Monthly'}
        When params query
        And method get
        Then status 200
        Then match response.meanTimeToRestore.summary.value == 0.74
        Then match response.meanTimeToRestore.summary.level == "ELITE"
        Then match response.meanTimeToRestore.details[0].value == 0.74


    Scenario: 4km backend API should calculate MTTR correctly for date range within which the 1st build is successful but the last build & the 2nd last build before the date range are both failed
        * def query = { endTime: '1578499199000', pipelineId: '6018c32f42fbb8439fc08b24', startTime: '1578355200000', targetStage: 'Deploy to UAT', unit: 'Monthly'}
        When params query
        And method get
        Then status 200
        Then match response.meanTimeToRestore.summary.value == 21.50
        Then match response.meanTimeToRestore.summary.level == "HIGH"
        Then match response.meanTimeToRestore.details[0].value == 21.50

    Scenario: 4km backend API should calculate MTTR correctly for date range within which the 1st build is successful but the last build before the date range is aborted
        * def query = { endTime: '1578842603000', pipelineId: '6018c32f42fbb8439fc08b24', startTime: '1578758400000', targetStage: 'Deploy to UAT', unit: 'Monthly'}
        When params query
        And method get
        Then status 200
        Then match response.meanTimeToRestore.summary.value == 69.59
        Then match response.meanTimeToRestore.summary.level == "MEDIUM"
        Then match response.meanTimeToRestore.details[0].value == 69.59


    Scenario: 4km backend API should calculate MTTR correctly for date range within which the 1st build is Failed but the last build before the date range is also Failed
        * def query = { endTime: '1579363199000', pipelineId: '6018c32f42fbb8439fc08b24', startTime: '1579104000000', targetStage: 'Deploy to UAT', unit: 'Monthly'}
        When params query
        And method get
        Then status 200
        Then match response.meanTimeToRestore.summary.value == 48.00
        Then match response.meanTimeToRestore.summary.level == "MEDIUM"
        Then match response.meanTimeToRestore.details[0].value == 48.00

    Scenario: 4km backend API should calculate MTTR correctly for date range within which there are several failure restored (Monthly view)
        * def query = { endTime: '1581004799000', pipelineId: '6018c32f42fbb8439fc08b24', startTime: '1577808000000', targetStage: 'Deploy to UAT', unit: 'Monthly'}
        When params query
        And method get
        Then status 200
        Then match response.meanTimeToRestore.summary.value == 71.55
        Then match response.meanTimeToRestore.summary.level == "MEDIUM"
        Then match response.meanTimeToRestore.details[0].value == 31.47
        Then match response.meanTimeToRestore.details[1].value == 312.00