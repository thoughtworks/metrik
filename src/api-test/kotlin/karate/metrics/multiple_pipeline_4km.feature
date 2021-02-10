Feature: Single pipeline MTTR and Unit conversion. Card #52 and Card#123
// refer to https://docs.google.com/presentation/d/12dQD9Zhkj4NXEa7Xdecb4wAr_fXIq8fJ7nW_4IZ2O_c/edit#slide=id.gb9d0735675_0_384 for manual testing result
    Background: 
        Given url baseUrl
        Given path '/api/pipeline/metrics'

    Scenario: multiple_pipelines_post_api when only 1 pipeline has deployment data
        When request
        """
        {
            "endTime": 1578153599000,
            "pipelineStages": [
                {
                  "pipelineId": "6018c32f42fbb8439fc08b24",
                  "stage": "Deploy to UAT"
                },
                {
                  "pipelineId": "600a701221048076f92c4e43",
                  "stage": "Deploy to UAT"
               },
               {
                 "pipelineId": "601a2f129deac2220dd07570",
                 "stage": "Deploy to Staging"
               }
            ],
            "startTime": 1577808000000,
            "unit": "Fortnightly"
        }
        """
        And method post
        Then status 200
        Then match response.deploymentFrequency.summary.value == 30.00
        Then match response.deploymentFrequency.summary.level == "HIGH"
        Then match response.deploymentFrequency.details[0].value == 4

        Then match response.leadTimeForChange.summary.value == 0.11
        Then match response.leadTimeForChange.summary.level == "ELITE"
        Then match response.leadTimeForChange.details[0].value == 0.11

        Then match response.meanTimeToRestore.summary.value == 0.74
        Then match response.meanTimeToRestore.summary.level == "ELITE"
        Then match response.meanTimeToRestore.details[0].value == 0.74

        Then match response.changeFailureRate.summary.value == 20.00
        Then match response.changeFailureRate.summary.level == "HIGH"
        Then match response.changeFailureRate.details[0].value == 20.00

    Scenario: multiple_pipelines_post_api when several pipelines have deployments
        When request
        """
        {
            "endTime": 1578844799000,
            "pipelineStages": [
                {
                  "pipelineId": "6018c32f42fbb8439fc08b24",
                  "stage": "Deploy to UAT"
                },
                {
                  "pipelineId": "600a701221048076f92c4e43",
                  "stage": "Deploy to UAT"
               },
               {
                 "pipelineId": "601a2f129deac2220dd07570",
                 "stage": "Deploy to Staging"
               }
            ],
            "startTime": 1578499200000,
            "unit": "Fortnightly"
        }
        """
        And method post
        Then status 200
        Then match response.deploymentFrequency.summary.value == 22.50
        Then match response.deploymentFrequency.summary.level == "HIGH"
        Then match response.deploymentFrequency.details[0].value == 3

        Then match response.leadTimeForChange.summary.value == 1.31
        Then match response.leadTimeForChange.summary.level == "HIGH"
        Then match response.leadTimeForChange.details[0].value == 1.31

        Then match response.meanTimeToRestore.summary.value == 60.48
        Then match response.meanTimeToRestore.summary.level == "MEDIUM"
        Then match response.meanTimeToRestore.details[0].value == 60.48

        Then match response.changeFailureRate.summary.value == 50/00
        Then match response.changeFailureRate.summary.level == "LOW"
        Then match response.changeFailureRate.details[0].value == 50.00

