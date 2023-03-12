# Дипломный проект на курсе по автоматизации тестирования на Java Яндекс.Практикум

## Первая часть дипломного проекта: юнит-тесты

### Использованные технологии:
- java 11
- junit 4.12
- mockito 4.2.0
- jacoco 0.8.7

### Оценка покрытия
1. Запускаем команду mvn verify
2. Находим файл index.html в папке target/site/jacoco/


## Вторая часть дипломного проекта: API-тесты

### Использованные технологии:
- java 11
- junit 4.12
- rest-assured 4.4.0
- allure 2.15.0
- gson 2.8.9

### Запуск отчета Allure
1. Запускаем тесты командой mvn clean test
2. Запускаем сервер Allure командой mvn allure:serve


## Третья часть дипломного проекта: UI-автотесты

### Сайт: https://stellarburgers.nomoreparties.site/

### Использованные технологии:
- java 11
- junit 4.12
- rest-assured 4.4.0
- allure 2.15.0
- gson 2.8.9
- selenium 3.141.59

### Драйверы браузеров в папке recourses
1. Chromedriver для версии Chrome 110
2. YandexDriver для версии YandexBrouser 108

### Запуск отчета Allure
1. Запускаем тесты командой mvn clean test
2. Запускаем сервер Allure командой mvn allure:serve