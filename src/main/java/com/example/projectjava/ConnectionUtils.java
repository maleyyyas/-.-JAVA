package com.example.projectjava;

import com.example.projectjava.model.Country;
import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionUtils {

    private final Connection connection;

    public ConnectionUtils(Connection connection) {
        this.connection = connection;
    }

    public boolean execute(@Language("SQLite") String sql) throws SQLException {
        try (var statement = connection.createStatement()) {
            return statement.execute(sql);
        }
    }

    public int update(@Language("SQLite") String sql) throws SQLException {
        try (var statement = connection.createStatement()) {
            return statement.executeUpdate(sql);
        }
    }

    public ResultSet query(@Language("SQLite") String sql) throws SQLException {
        var statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    public boolean createCountriesTable() throws SQLException {
        return execute("""
            CREATE TABLE IF NOT EXISTS COUNTRIES (
                NAME TEXT,
                REGION TEXT,
                'HAPPINESS RANK' INTEGER,
                'HAPPINESS SCORE' REAL,
                'STANDARD ERROR' REAL,
                ECONOMY REAL,
                FAMILY REAL,
                HEALTH REAL,
                FREEDOM REAL,
                TRUST REAL,
                GENEROSITY REAL,
                'DYSTOPIA RESIDUAL' REAL
            );
        """);
    }

    public void insert(Country country) throws SQLException {
        update("INSERT INTO COUNTRIES VALUES('" +
                country.name() + "', '" +
                country.region() + "', " +
                country.happinessRank() + ", " +
                country.happinessScore() + ", " +
                country.standardError() + ", " +
                country.economy() + ", " +
                country.family() + ", " +
                country.health() + ", " +
                country.freedom() + ", " +
                country.trust() + ", " +
                country.generosity() + ", " +
                country.dystopia() + ");");
    }

    public void insert(String line) throws SQLException {
        update("INSERT INTO COUNTRIES VALUES(" + line + ");");
    }

    public ResultSet selectAll() throws SQLException {
        return query("SELECT * FROM COUNTRIES");
    }

    public ResultSet selectAllOrdered() throws SQLException {
        return query("SELECT * FROM COUNTRIES ORDER BY NAME");
    }
}
