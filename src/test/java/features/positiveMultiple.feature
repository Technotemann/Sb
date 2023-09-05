Feature: Позитивный тест

  @positive
    @multiple
  Scenario Outline: Запрос погоды по нескольким городам
    Given запросить погоду в городе '<город>'
    Then валидировать результаты последнего запроса погоды в городе '<город>'
    Then сравнить значение узла 'current.weather_descriptions[0]' последнего результата запроса погоды в городе '<город>' с параметром '<погода>'


    Examples:
      | город  | погода        |
      | London | Sunny         |
      | Moscow | Partly cloudy |
      | Berlin | Sunny         |
      | Rome   | Sunny         |
