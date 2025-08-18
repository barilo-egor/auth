[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=barilo-egor_auth&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=barilo-egor_auth)

Сервис аутентификации. Досупный функционал:
- Регистрация нового пользователя
- Аутентификация уже существующего пользователя.

Регистрация доступна по юрлу <code>/auth/register</code>, аутентификация по юрлу <code>/auth/login</code>.

Рядом с jar архивом требуется разместить директорию <code>config</code> с файлом внутри неё <code>config.yml</code> с заполненными значениями пропертей. (пример лежит в <code>config_template/config.yml</code>).

В директории <code>docker</code> есть Dockerfile для запуска сервиса внутри докер-контейнера. Также, рядом лежат примеры скриптов для запуска, перехода в bash внутрь контейнера и просмотра логов.
Скрипт для запуска следует проверить и отредактировать, если требуется: jar файл и директория config должны находится в монтируемой с хоста директории, для дебага и запуска приложения должны быть прописаны свободные порты.

Есть swagger документация, для доступа к которой можно настроить путь через config.yml.

Описание свойств config.yml:
<pre>
<code>
server:
  port: # Порт, на котором будет работать приложение.
jwt:
  secret: # Секретный ключ для генерации JWT. Минимум 32 символа.
  expiration: # Время в миллисекундах срока жизни JWT
spring:
  datasource:
    username: # Имя пользователя БД
    password: # Пароль пользователя БД
    driver-class-name: # Имя класса драйвера БД
    url: # Юрл для подключения к БД
  jpa:
    hibernate:
      ddl-auto: # Метод обновления таблиц хибернейтом
springdoc:
  swagger-ui:
    enabled: # Включен ли сваггер (отключать на production)
    path: # Путь к интерофейсу swagger
  api-docs:
    path: # Путь к json документации swagger
</code>
</pre>
