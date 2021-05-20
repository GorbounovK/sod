# SOD (Сервер обмена данными)
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
