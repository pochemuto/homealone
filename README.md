Homealone
=========

Локальный запуск
----------------

1. Установите [Docker](https://www.docker.com/products/docker-desktop)
2. Установите [Docker Compose](https://docs.docker.com/compose/install/)
3. Заведите бота в Telegram через [BotFather](https://t.me/BotFather)
    1. Нужно запомнить имя бота и токен, он вида 123456789:AbcDefgHijkL-mn...
4. Получите логин и пароль для вашей почты. В случае включенной двухфакторной авторизации, нужно получить пароль
   для приложения
5. Создайте файл secret.properties в корне проекта и заполните значения:
```properties
# настройки отправки писем, ниже пример для gmail
spring.mail.host=smtp.gmail.com
spring.mail.port=587
# логин в случае gmail должен содержать в том числе @gmail.com
spring.mail.username=[логин]
spring.mail.password=[пароль]

# настройки бота
bot.token=[токен бота]
bot.username=[имя бота]

# логин и пароль от http://lk.lerchekmarafon.ru
marafon.login=[]
marafon.password=[]
```
6. Выполните в корне проекта:
```shell
docker-compose -f docker-compose.local.yml up
```
7. Создайте в Idea конфигурацию запуска HomealoneApplication. 
   Подразумевается, что установлена Intellij Idea Ultimate + Spring plugin + Lombok plugin (включены по-умолчанию)
   1. Нажмите стрелочку напротив `public class HomealoneApplication`
   2. Modify run configuration...
   3. В поле Environment properties добавьте SECRET_PROPERTIES=[полный путь до secret.properties]
   4. Active profiles: local
8. Выполните запуск
9. Напишите боту в телеграме `ping`. Он должен ответить.
