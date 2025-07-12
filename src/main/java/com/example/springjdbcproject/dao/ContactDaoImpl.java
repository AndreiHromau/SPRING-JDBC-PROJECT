package com.example.springjdbcproject.dao;

import com.example.springjdbcproject.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component //создали экземпляр объекта для spring
public class ContactDaoImpl implements ContactDao {
    @Autowired //используется для автоматического внедрения зависимостей в классы
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String SELECT_ALL_SQL = "SELECT id, first_name, last_name, phone_number, email FROM users";
    private static final String SELECT_BY_ID_SQL = "SELECT id, first_name, last_name, phone_number, email FROM users WHERE id = :id";
    private static final String INSERT_SQL = "INSERT INTO users (first_name, last_name, phone_number, email) VALUES (:firstName, :lastName, :phoneNumber, :email) RETURNING id";
    private static final String UPDATE_PHONE_SQL = "UPDATE users SET phone_number = :phoneNumber WHERE id = :id";
    private static final String UPDATE_EMAIL_SQL = "UPDATE users SET email = :email WHERE id = :id";
    private static final String DELETE_SQL = "DELETE FROM users WHERE id = :id";

    private static final RowMapper<User> userRowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("id"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setPhoneNumber(rs.getString("phone_number"));
            user.setEmail(rs.getString("email"));
            return user;}};

    @Override
    public List<User> getAllContacts() {
        return namedParameterJdbcTemplate.query(SELECT_ALL_SQL, userRowMapper);}

    @Override
    public User getContactById(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID_SQL, params, userRowMapper); }
    @Override
    public User addContact(User user) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("firstName", user.getFirstName());
        params.addValue("lastName", user.getLastName());
        params.addValue("phoneNumber", user.getPhoneNumber());
        params.addValue("email", user.getEmail());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(INSERT_SQL, params, keyHolder);

        Number newId = keyHolder.getKey();
        if (newId != null) {
            user.setId(newId.longValue());}
        return user; }

    @Override
    public void updatePhoneNumber(Long id, String phoneNumber) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("phoneNumber", phoneNumber);
        namedParameterJdbcTemplate.update(UPDATE_PHONE_SQL, params);}

    @Override
    public void updateEmail(Long id, String email) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        params.addValue("email", email);
        namedParameterJdbcTemplate.update(UPDATE_EMAIL_SQL, params);}
    @Override
    public void deleteContact(Long id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("id", id);
        namedParameterJdbcTemplate.update(DELETE_SQL, params);}}
