Feature: Deployment Frequency

    Background: 
        Given url baseUrl 
        Given path '/api/pipeline/metrics'

    Scenario: targeted stage status is successful and time is within the selected date range then stage should be counted in
        When request 
        """
        {
            "endTime": 1609516799000,
            "pipelineStages": [
                {
                "pipelineId": "601cbb3425c1392117aa053b",
                "stage": "Deploy to DEV"
                }
            ],
            "startTime": 1609430400000,
            "unit": "Fortnightly"
        }
        """
        And method post 
        Then status 200
        Then match response.deploymentFrequency.summary.value == 14.0
        Then match response.deploymentFrequency.summary.level == "HIGH"
        Then match response.deploymentFrequency.details[0].value == 1


    Scenario: targeted stage status is successful but time is not within the selected date range then stage should not be counted in
        When request 
        """
        {
            "endTime": 1609459200000,
            "pipelineStages": [
                {
                "pipelineId": "601cbb3425c1392117aa053b",
                "stage": "Deploy to DEV"
                }
            ],
            "startTime": 1609430400000,
            "unit": "Fortnightly"
        }
        """
        And method post 
        Then status 200
        Then match response.deploymentFrequency.summary.value == 0.0
        Then match response.deploymentFrequency.summary.level == "LOW"
        Then match response.deploymentFrequency.details[0].value == 0

    Scenario: targeted stage status is failure and selected time is within the build date range then stage should not be counted in
        When request 
        """
        {
            "endTime": 1609470000000,
            "pipelineStages": [
                {
                "pipelineId": "601cbb3425c1392117aa053b",
                "stage": "Deploy to DEV"
                }
            ],
            "startTime": 1609466400000,
            "unit": "Fortnightly"
        }
        """
        And method post 
        Then status 200
        Then match response.deploymentFrequency.summary.value == 0.0
        Then match response.deploymentFrequency.summary.level == "LOW"
        Then match response.deploymentFrequency.details[0].value == 0

    Scenario: targeted stage status is aborted and selected time is within the build date range then stage should not be counted in
        When request 
        """
        {
            "endTime": 1609470000000,
            "pipelineStages": [
                {
                "pipelineId": "601cbb3425c1392117aa053b",
                "stage": "Deploy to DEV"
                }
            ],
            "startTime": 1609466400000,
            "unit": "Fortnightly"
        }
        """
        And method post 
        Then status 200
        Then match response.deploymentFrequency.summary.value == 0.0
        Then match response.deploymentFrequency.summary.level == "LOW"
        Then match response.deploymentFrequency.details[0].value == 0


    Scenario: targeted stage start time is within the selected time range but finishing time is not within selected time range then stage should not be counted in
        When request 
        """
        {
            "endTime": 1609462832000,
            "pipelineStages": [
                {
                "pipelineId": "601cbb3425c1392117aa053b",
                "stage": "Deploy to DEV"
                }
            ],
            "startTime": 1609462500000,
            "unit": "Fortnightly"
        }
        """
        And method post 
        Then status 200
        Then match response.deploymentFrequency.summary.value == 0.0
        Then match response.deploymentFrequency.summary.level == "LOW"
        Then match response.deploymentFrequency.details[0].value == 0

    Scenario: targeted stages contains success, failure and aborted during selected time range then only success stage should be counted in
        When request 
        """
        {
            "endTime": 1609516799000,
            "pipelineStages": [
                {
                "pipelineId": "601cbb3425c1392117aa053b",
                "stage": "Deploy to DEV"
                }
            ],
            "startTime": 1609430400000,
            "unit": "Fortnightly"
        }
        """
        And method post 
        Then status 200
        Then match response.deploymentFrequency.summary.value == 14.0
        Then match response.deploymentFrequency.summary.level == "HIGH"
        Then match response.deploymentFrequency.details[0].value == 1