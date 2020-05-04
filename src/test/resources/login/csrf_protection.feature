Feature: Csrf_protection

  Scenario: Deleting account without csrf-token should lead to error-response
    Given navigate to start page
    When posting delete account
    Then http response should be 403
    And close browser