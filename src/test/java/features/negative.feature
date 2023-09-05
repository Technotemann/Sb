Feature: Негативный тест


  @positive_1
  Scenario: Неверный запрос погоды
    Given выполнить запрос 'https://api.weatherstack.com/current' с ожиданием кода ответа '101'

  @positive_2
  Scenario: Неверный запрос погоды
    Given выполнить запрос 'https://api.weatherstack.com/current?access_key=28dbac30b8a82c1031fd52d21133b7bd&query=London' с ожиданием кода ответа '105'

  @positive_3
  Scenario: Неверный запрос погоды
    Given выполнить запрос 'http://api.weatherstack.com/historical?access_key=28dbac30b8a82c1031fd52d21133b7bd&query=New%20York&historical_date=2014-01-21' с ожиданием кода ответа '603'

  @positive_4
  Scenario: Неверный запрос погоды
    Given выполнить запрос 'http://api.weatherstack.com/current?access_key=28dbac30b8a82c1031fd52d21133b7bd&query=Kitezh' с ожиданием кода ответа '615'
