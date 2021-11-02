# SOD (Сервер обмена данными)
0.1.22 (02-11-2021)
- добавил запуск служб розетки по расписанию

0.1.21 (02-11-2021)
- исправлены ошибки при выгрузке изображений на ФТП сервер
- добавлены страницы в интерфейсе для работы с мзображениями
- добавлены инструменты для выгрузки прайса на розетку
- поправлен интерфейс: не нужные разделы скрываются в зависимости от значения свойств

0.1.19 (11-10-2021)
- перешол на Spring boot 2.4.10
- добавил выгрузку картинок на ФТП сервер

0.1.18 (06-10-2021)
- полечил асинхронность

0.1.17 (06-10-2021)
- добавил лог экспорта
- кнопка принудительного запуска экспорта на пром

0.1.16 (04-10-2021)
- добавил сохранение файла заказов в файл prom/orders.xml
- исправил работу с кодировкой

0.1.15
- добавил чтение XML с заказами и его анализ для получения доп информации

0.1.13
- добавил информации при запуске

0.1.12 (19-09-2021
- исправлена кодировка

0.1.11 (10.07.2021)
- выводит результат последнего запуска Импорта заказов

0.1.10 (08.07.2021)
- добавил кнопку "Импорт заказов" для принудительного запуска

0.1.9 (12.06.2021)
- выгрузил на GitHub git@github.com:GorbounovK/sod.git
- добавил страницу "Импорт заказов" с полями из PromImportOrdersInfo, обновил контроллер PromControllers
- исправил страницы ошибок

0.1.8 (12.06.2021)
- добавил авторизацию
- обновил дизайн страницы, перевел на шаблоны
- добавил страницы ошибок

0.1.7 (10.06.2021)
- перевел запуск 1С на скрипты с регистрацией 1С баз

0.1.5 (20.05.2021)
- подключил класс FTPclean для очистки старых файлов на ftp e-mag.cn.ua
- подкорректировал EmagTask

0.1.4 (08.05.2021)
- проект перевел на Spring boot 2.4.5
- подключил /actuator
- добавил класс Image с сохранением в БД
- исправлен application.properties
- отделил функционал экспорта на пром в класс ExportService

0.1.3 (28.04.2021)
- добавил timeout на запуск exec1cCreateOrders

Версия 0.1.2 (02.02.2021)
- добавил свойства prom.ua.1c.path
- prom.ua.products.upload.1c.user
- prom.ua.products.import.1c.user
