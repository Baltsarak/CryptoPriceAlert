CryptoPriceAlert – приложение для мониторинга цен криптовалют. 
Отправляет оповещение пользователю, когда цена достигает заданного значения, используя данные с Cryptocompare.com. 
Позволяет устанавливать неограниченное количество оповещений, для любого количества доступных криптовалют.

Язык программирования: Kotlin.

Используются библиотеки: 
Retrofit 2 для подключения к API. 
Dagger 2 для внедрения зависимостей. 
Room для управления локальной базой данных. 
Firebase Firestore для облачного хранения данных. 
Glide для работы с изображениями. 
MPAndroidChart для построения графиков цен. 

Архитектура: Clean Architecture, MVVM, Single Activity

В приложении организована регистрация и вход через Firebase Authentication с сохранением данных пользователя в Firebase Firestore. 
Реализована регистрация через Email и пароль, вход через Google аккаунт а также в анонимный аккаунт с возможностью зарегистрироваться позже с сохранением данных.

Структура интерфейса:
В приложении пять основных экранов, навигация по которым осуществляется с помощью Bottom Navigation Bar:
Новости: Фрагмент с лентой новостей получаемых с Cryptocompare.com. 
Список наблюдения: Фрагмент со списком избранных пользователем криптовалют. 
Популярное: Фрагмент со списком популярных криптовалют по данным Cryptocompare.com. 
Карточка криптовалюты: Фрагмент с детальной информацией, включая возможность просмотра графиков за различные периоды и функционал добавления/удаления оповещений. 
Профиль пользователя: Фрагмент с информацией об аккаунте. 

Также реализованы экран регистрации, экран входа, и фрагмент для поиска криптовалюты из списка всех доступных.

Для запуска используйте Android Studio.



CryptoPriceAlert – an application for monitoring cryptocurrency prices. 
It sends notifications to the user when the price reaches a specified value, using data from Cryptocompare.com. 
Allows setting an unlimited number of alerts for any number of available cryptocurrencies.

Programming Language: Kotlin.

Libraries Used: 
Retrofit 2 for connecting to the API. 
Dagger 2 for dependency injection. 
Room for managing the local database. 
Firebase Firestore for cloud data storage. 
Glide for working with images. 
MPAndroidChart for creating price charts. 

Architecture: Clean Architecture, MVVM, Single Activity. 

The application features registration and login via Firebase Authentication, with user data stored in Firebase Firestore. 
It supports registration via email and password, login through Google account, and anonymous account login with the option to register later while retaining data. 

Interface Structure: The application has five main screens, navigated through the Bottom Navigation Bar: 
News: A fragment with a news feed sourced from Cryptocompare.com. 
Watchlist: A fragment with a list of cryptocurrencies favorited by the user. 
Popular: A fragment with a list of popular cryptocurrencies according to Cryptocompare.com. 
Cryptocurrency: A fragment with detailed information, including chart views for different periods and the ability to add/remove alerts. 
Profile: A fragment with account information. 
Additionally, there are registration and login screens, and a fragment for searching cryptocurrencies from the list of all available ones. 

To run the application, use Android Studio.

