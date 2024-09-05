# java-filmorate
Template repository for Filmorate project.

# Возможности приложения
* Создание, обновление, удаление фильмов
* Ставить лайки на понравившиеся фильмы
* Вывод топ фильмов, зависящий от количества лайков
* Создание, обновление, удаление пользователей
* Добавление в друзья и удаление из друзей пользователей

# Модель базы данных на ER-диаграмме
![QuickDBD-export](https://github.com/user-attachments/assets/ed72b071-1468-4f69-89ba-376c3b036a96)

# Примеры SQL-запросов к базе данных
<details>
  <summary>Получение пользователя с id = 1</summary>
  &nbsp;&nbsp;&nbsp; SELECT * <br>
  &nbsp;&nbsp;&nbsp; FROM users <br>
  &nbsp;&nbsp;&nbsp; WHERE user_id = 1;
</details>
<details>
  <summary>Получение фильм с id = 2</summary>
  &nbsp;&nbsp;&nbsp; SELECT * <br>
  &nbsp;&nbsp;&nbsp; FROM film <br>
  &nbsp;&nbsp;&nbsp; WHERE film_id = 2;
</details>
