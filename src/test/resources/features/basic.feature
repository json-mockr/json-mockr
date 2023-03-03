Feature: Basic API Test

  Background:
    * url karate.properties['apiUrl'] + '/users'

  Scenario: Create and delete resource

    Given method GET
    Then status 200
    And match $ == []

    Given request { name: "John Smith", age:22}
    And method POST
    Then status 201
    And match $ contains {id:"#notnull"}
    * def id = $.id

    Given request { name: "Anne Wrong", age:"ABD"}
    And method POST
    Then status 400

    Given request { name: "Anne Wrong", age: 56, gender: "F"}
    And method POST
    Then status 201

    Given path id
    And method get
    Then status 200
    And match $ contains {name:"John Smith"}

    Given path id
    And method DELETE
    Then status 204

    Given path id
    And method GET
    Then status 404
