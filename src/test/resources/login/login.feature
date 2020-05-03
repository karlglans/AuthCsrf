Feature: Authentication

  Scenario: Login success
    Given navigate to start page
    When user logged in using username as "Brad" and password as "secret123"
    Then user should see title "Welcome!" on page
    And csrf should be set in sessionStorage

  Scenario: Login failure
    Given navigate to start page
    When user logged in using username as "Invalid" and password as "secret123"
    Then user should see title "Login" on page

  Scenario: After refreshing page when logged in then the user should still be logged in
    Given navigate to start page
    When user logged in using username as "Brad" and password as "secret123"
    Then user should see title "Welcome!" on page
    Then refresh page
    Then user should see title "Welcome!" on page