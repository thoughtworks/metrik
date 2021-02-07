Feature: Deployment Frequency

    Background: 
        Given url baseUrl 
        Given path '/api/pipeline/metrics'

    Scenario: targeted stage status is successful and time is within the selected date range should be counted in
        When request 
        """
        {
            "endTime": 1609516799000,
            "pipelineStages": [
                {
                "pipelineId": "601cbb3425c1392117aa042a",
                "stage": "Deploy to DEV"
                }
            ],
            "startTime": 1609430400000,
            "unit": "Fortnightly"
        }
        """
        And method post 
        Then status 200
        * assert response.deploymentFrequency.summary.value == 14.0
        * assert response.deploymentFrequency.summary.level == "HIGH"
        * assert response.deploymentFrequency.details[0].value == 1

