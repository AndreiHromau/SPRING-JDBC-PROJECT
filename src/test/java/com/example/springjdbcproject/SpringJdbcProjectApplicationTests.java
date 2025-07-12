package com.example.springjdbcproject;
import com.example.springjdbcproject.dao.ContactDao;
import com.example.springjdbcproject.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
@SpringBootTest
@TestPropertySource(properties = {
        "spring.test.database.replace=none",
        "spring.datasource.url=jdbc:postgresql://localhost:5432/contactdb",
        "spring.datasource.username=root",
        "spring.datasource.password=root"})
public class SpringJdbcProjectApplicationTests {
    @Autowired //используется для автоматического внедрения зависимостей в классы
    private ContactDao contactDao;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private DataSource dataSource;

    @BeforeAll
    static void beforeAll(
            @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            @Autowired DataSource dataSource) {
        try {
            namedParameterJdbcTemplate.getJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS users (id SERIAL PRIMARY KEY, first_name VARCHAR(255) NOT NULL, last_name VARCHAR(255), phone_number VARCHAR(20), email VARCHAR(255))");
        } catch (Exception e) {
            System.err.println("Error creating table: " + e.getMessage()); }}

    @AfterAll
    static void afterAll(
            @Autowired NamedParameterJdbcTemplate namedParameterJdbcTemplate,
            @Autowired DataSource dataSource) {
        try {
            namedParameterJdbcTemplate.getJdbcTemplate().execute("DROP TABLE IF EXISTS users");
        } catch (Exception e) {
            System.err.println("Error dropping table: " + e.getMessage());}}
    //тест базы
    @Test
    void testDatabaseConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertThat(connection).isNotNull();
            assertThat(connection.isValid(5)).isTrue();}}
    //добавление контакта
    @Test
    void AddContactAndGetContactById() {
        User newUser = new User(null, "Andrei", "Grozniy", "1111111111", "grozniy@gmail.com");
        User addedUser = contactDao.addContact(newUser);

        assertThat(addedUser.getId()).isNotNull();

        User retrievedUser = contactDao.getContactById(addedUser.getId());
        assertThat(retrievedUser).isNotNull();
        assertThat(retrievedUser.getFirstName()).isEqualTo("Andrei");
        assertThat(retrievedUser.getLastName()).isEqualTo("Grozniy");
        assertThat(retrievedUser.getPhoneNumber()).isEqualTo("1111111111");
        assertThat(retrievedUser.getEmail()).isEqualTo("grozniy@gmail.com");}
    // получение всех контактов
    @Test
    void GetAllContacts() {
        User newUser1 = new User(null, "Andrei", "Hromau", "1111111111", "andreihromau@example.com");
        User newUser2 = new User(null, "Andrei", "Grozniy", "1111111111", "grozniy@example.com");

        contactDao.addContact(newUser1);
        contactDao.addContact(newUser2);

        List<User> allContacts = contactDao.getAllContacts();
        assertThat(allContacts).hasSizeGreaterThanOrEqualTo(2);}
    //обновить номер
    @Test
    void UpdatePhoneNumber() {
        User newUser = new User(null, "Andrei", "Hromau", "1111111111", "andreihromau@example.com");
        User addedUser = contactDao.addContact(newUser);

        String newPhoneNumber = "2222222222";
        contactDao.updatePhoneNumber(addedUser.getId(), newPhoneNumber);

        User updatedUser = contactDao.getContactById(addedUser.getId());
        assertThat(updatedUser.getPhoneNumber()).isEqualTo(newPhoneNumber);}
    //обновить email
    @Test
    void UpdateEmail() {
        User newUser = new User(null, "Andrei", "Grozniy", "1111111111", "grozniy@gmail.com");
        User addedUser = contactDao.addContact(newUser);

        String newEmail = "grozniy@yandex.ru";
        contactDao.updateEmail(addedUser.getId(), newEmail);

        User updatedUser = contactDao.getContactById(addedUser.getId());
        assertThat(updatedUser.getEmail()).isEqualTo(newEmail); }
    //удалить контакт
    @Test
    void DeleteContact() {
        User newUser = new User(null, "Andrei", "Hromau", "1111111111", "andreihromau@example.com");
        User addedUser = contactDao.addContact(newUser);
        contactDao.deleteContact(addedUser.getId());
        User deletedUser = contactDao.getContactById(addedUser.getId());
        assertThat(deletedUser).isNull();}}
