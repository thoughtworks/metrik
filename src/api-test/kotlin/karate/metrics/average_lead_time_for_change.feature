Feature: Single pipeline MLT and Unit conversion. Card #51
  // refer to  https://docs.google.com/document/d/1ZJpcMT-iuCxqv7oAMjC2Z-bH8JoAuAnrOjIT3wK24vY/edit  for manual testing result
  Background:
      Given url baseUrl
      Given path '/api/pipeline/metrics'

  Scenario: The deployment deployment done time is within the selected date range and the stage status is success should be counted in
      When request
      """
      {
            "endTime": 1598388400000,
            "pipelineStages": [
                {
                "pipelineId": "2",
                "stage": "deploy to prod"
                }
            ],
            "startTime": 1598288400000,
            "unit": "Fortnightly"
      }
      """
      And method post
      Then status 200
      Then match response.leadTimeForChange.summary.value == 5.08
      Then match response.leadTimeForChange.summary.level == "HIGH"
      Then match response.leadTimeForChange.details[0].value == 5.08


  Scenario: The deployment deployment done time is within the selected date range but the stage status is failed should not be counted in
      When request
        """
        {
              "endTime": 1598388400000,
              "pipelineStages": [
                  {
                  "pipelineId": "3",
                  "stage": "deploy to prod"
                  }
              ],
              "startTime": 1598288400000,
              "unit": "Fortnightly"
        }
        """
      And method post
      Then status 200
      Then match response.leadTimeForChange.summary.value == "NaN"
      Then match response.leadTimeForChange.summary.level == "INVALID"
      Then match response.leadTimeForChange.details[0].value == "NaN"


  Scenario: The deployment deployment done time is within the selected date range but the stage status is other should not be counted in
      When request
          """
          {
                "endTime": 1598388400000,
                "pipelineStages": [
                    {
                    "pipelineId": "4",
                    "stage": "deploy to prod"
                    }
                ],
                "startTime": 1598288400000,
                "unit": "Fortnightly"
          }
          """
      And method post
      Then status 200
      Then match response.leadTimeForChange.summary.value == "NaN"
      Then match response.leadTimeForChange.summary.level == "INVALID"
      Then match response.leadTimeForChange.details[0].value == "NaN"

  Scenario: The deployment deployment done time is within the selected date range but no commits, MLT should be NaN
      When request
            """
            {
                  "endTime": 1598388400000,
                  "pipelineStages": [
                      {
                      "pipelineId": "5",
                      "stage": "deploy to prod"
                      }
                  ],
                  "startTime": 1598288400000,
                  "unit": "Fortnightly"
            }
            """
      And method post
      Then status 200
      Then match response.leadTimeForChange.summary.value == "NaN"
      Then match response.leadTimeForChange.summary.level == "INVALID"
      Then match response.leadTimeForChange.details[0].value == "NaN"

  Scenario: No deployment happened during the selected time, MLT should be NaN
      When request
              """
              {
                    "endTime": 1598488400000,
                    "pipelineStages": [
                        {
                        "pipelineId": "5",
                        "stage": "deploy to prod"
                        }
                    ],
                    "startTime": 1598388400000,
                    "unit": "Fortnightly"
              }
              """
      And method post
      Then status 200
      Then match response.leadTimeForChange.summary.value == "NaN"
      Then match response.leadTimeForChange.summary.level == "INVALID"
      Then match response.leadTimeForChange.details[0].value == "NaN"

  Scenario: The previous deployment failed, The next deployment should include the commits.
      When request
                """
                {
                      "endTime": 1598292000300,
                      "pipelineStages": [
                          {
                          "pipelineId": "6",
                          "stage": "deploy to prod"
                          }
                      ],
                      "startTime": 1598288400300,
                      "unit": "Fortnightly"
                }
                """
      And method post
      Then status 200
      Then match response.leadTimeForChange.summary.value == 0.08
      Then match response.leadTimeForChange.summary.level == "ELITE"
      Then match response.leadTimeForChange.details[0].value == 0.08

  Scenario: One deployment which contains several commits , the MLT should be accounted separately, and be averaged accordingly
      When request
                  """
                  {
                        "endTime": 1598292000300,
                        "pipelineStages": [
                            {
                            "pipelineId": "7",
                            "stage": "deploy to prod"
                            }
                        ],
                        "startTime": 1598288400300,
                        "unit": "Fortnightly"
                  }
                  """
      And method post
      Then status 200
      Then match response.leadTimeForChange.summary.value == 3.08
      Then match response.leadTimeForChange.summary.level == "HIGH"
      Then match response.leadTimeForChange.details[0].value == 3.08

  Scenario: The deployment which the code commit time is within the selected date range but the deployment time not. The MLT is NaN.
      When request
                    """
                    {
                          "endTime": 1598284800000,
                          "pipelineStages": [
                              {
                              "pipelineId": "7",
                              "stage": "deploy to prod"
                              }
                          ],
                          "startTime": 1597852800000,
                          "unit": "Fortnightly"
                    }
                    """
      And method post
      Then status 200
      Then match response.leadTimeForChange.summary.value == "NaN"
      Then match response.leadTimeForChange.summary.level == "INVALID"
      Then match response.leadTimeForChange.details[0].value == "NaN"

  Scenario: The deployment which the deployment time is within the selected date range but the code commit time not. The MLT should be counted in.
      When request
                      """
                      {
                            "endTime": 1598292000200,
                            "pipelineStages": [
                                {
                                "pipelineId": "7",
                                "stage": "deploy to prod"
                                }
                            ],
                            "startTime": 1598288400000,
                            "unit": "Fortnightly"
                      }
                      """
      And method post
      Then status 200
      Then match response.leadTimeForChange.summary.value == 3.08
      Then match response.leadTimeForChange.summary.level == "HIGH"
      Then match response.leadTimeForChange.details[0].value == 3.08

  Scenario: The first Unit has the target stage, but the second Unit does not. The first value should be correct. The second value should be NaN.
      When request
                        """
                        {
                              "endTime": 1599667200000,
                              "pipelineStages": [
                                  {
                                  "pipelineId": "7",
                                  "stage": "deploy to prod"
                                  }
                              ],
                              "startTime": 1597852800000,
                              "unit": "Fortnightly"
                        }
                        """
      And method post
      Then status 200
      Then match response.leadTimeForChange.summary.value == 3.08
      Then match response.leadTimeForChange.summary.level == "HIGH"
      Then match response.leadTimeForChange.details[0].value == 3.08
      Then match response.leadTimeForChange.details[0].startTimestamp == 1597852800000
      Then match response.leadTimeForChange.details[0].endTimestamp == 1598543999999
      Then match response.leadTimeForChange.details[1].value == "NaN"
      Then match response.leadTimeForChange.details[1].startTimestamp == 1598544000000
      Then match response.leadTimeForChange.details[1].endTimestamp == 1599667200000