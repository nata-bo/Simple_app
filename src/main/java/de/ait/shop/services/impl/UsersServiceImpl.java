package de.ait.shop.services.impl;

import de.ait.shop.models.User;
import de.ait.shop.repositories.UsersRepository;
import de.ait.shop.services.UsersService;

import java.util.List;

/**
 * 8/23/2023
 * Introduction
 *
 * @author Marsel Sidikov (AIT TR)
 */
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository; // зависимость на хранилище пользователей

    public UsersServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public User addUser(String email, String password) { // метод добавления пользователя
        if (email == null || email.equals("") || email.equals(" ")) { // валидируем email
            throw new IllegalArgumentException("Email не может быть пустым");
        }

        if (password == null || password.equals("") || password.equals(" ")) { // валидируем password
            throw new IllegalArgumentException("Password не может быть пустым");
        }

        User existedUser = usersRepository.findOneByEmail(email); // находим пользователя по email

        if (existedUser != null) { // если такой пользователь уже есть
            throw new IllegalArgumentException("Пользователь с таким email уже есть");
        }

        User user = new User(email, password); // создаем пользователя

        usersRepository.save(user); // сохраняем пользователя в хранилище

        return user; // возвращаем пользователя как результат
    }

    @Override
    public List<User> getAllUsers() {
        return usersRepository.findAll(); // никакой дополнительной логики нет, просто запрашиваем у репозитория
    }

    @Override
    public void updateUser(Long id, String newEmail, String newPassword) {
        // сначала нужно получить этого пользователя по его id

        // вариант примитивный - воспользоваться уже существующим методом findAll
        List<User> users = usersRepository.findAll(); // получаем всех пользователей из базы

        // находим в этом списке нужного нам пользователя
        User userForUpdate = null;

        // пробегаем весь список
        for (User user : users) {
            // если у пользователя из списка id совпал с тем, который мы хотим обновить
            if (user.getId().equals(id)) {
                // запоминаем этого пользователя
                userForUpdate = user;
                // останавливаем цикл
                break;
            }
        }

        // если пользователя так и не нашли, нужно выбросить ошибку
        if (userForUpdate == null) {
            throw new IllegalArgumentException("User with id <" + id + "> not found");
        }

        // проверим, корректные ли данные на обновление и если они корректные, то дадим пользователю новые данные
        if (!newEmail.isBlank()) { // если не пустой
            // если пользователь не указал email как пробелы (он реально его хочет обновить)
            userForUpdate.setEmail(newEmail); // положили новый email
        }

        if (!newPassword.isBlank()) {
            userForUpdate.setPassword(newPassword);
        }

        // обновить данные в файле
        usersRepository.update(userForUpdate); // просто передаем обновленного пользователя в репозиторий

    }
}
