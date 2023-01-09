package com.example.projectjava;

import com.example.projectjava.model.Country;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.io.*;
import java.sql.SQLException;
import java.util.Objects;

public class Main {

    private static final String path = "countries.db";

    public static void main(String[] args) throws SQLException, IOException {
        // Подключение к базе данных
        var connection = DatabaseFactory.getDatabase(path);
        if (connection == null) {
            System.out.println("Failed to create database");
            return;
        }

        // Создание таблицы при отсутствии
        var utils = new ConnectionUtils(connection);
        try {
            utils.createCountriesTable();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return;
        }

        // Заполнение таблицы
        try (var resultat = utils.selectAll()) {
            if (!resultat.next()) fillTable(utils);
        }

        // Задача 2.
        // Выведите в консоль страну с самым высоким показателем
        // экономики среди "Latin America and Caribbean" и "Eastern Asia"
        var economicCountry = getCountryWithMaxEconomy(utils);
        System.out.println("The country with the highest economic indicator: " +
                (economicCountry != null ? economicCountry.name() : null));

        // Задача 3.
        // Найдите страну с "самыми средними показателями" среди "Western Europe" и "North America"
        var sredneeCountry = getCountryWithAverageStats(utils);
        System.out.println("The country with the most average indicators: " +
                (sredneeCountry != null ? sredneeCountry.name() : null));

        // Задача 1.
        // Сформируйте график по показателю экономики, объединив их по странам
        buildEconomyGraph(utils);
    }

    private static void fillTable(ConnectionUtils utils) throws IOException, SQLException {
        try (var read = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Main.class.getResourceAsStream("/happy.csv"))))) {
            String line;
            read.readLine();
            while ((line = read.readLine()) != null) {
                var countriess = Country.fromRow(line);
                if (countriess != null) utils.insert(countriess);
            }
        }
    }

    private static void buildEconomyGraph(ConnectionUtils utils) throws SQLException {
        var dataset = new DefaultCategoryDataset();
        try (var result = utils.selectAllOrdered()) {
            while (result.next()) {
                var country = Country.fromRow(result);
                if (country == null) continue;
                dataset.addValue(country.economy(), country.name(), "");
            }
        }

        var chart = ChartFactory.createBarChart(null, "Country", null, dataset,
                PlotOrientation.VERTICAL, true, false, false);
        var panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(1280, 720));
        var frame = new ApplicationFrame("Economic indicators");
        frame.setContentPane(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private static Country getCountryWithMaxEconomy(ConnectionUtils utils) throws SQLException {
        try (var result = utils.query("""
            SELECT * FROM COUNTRIES WHERE REGION = 'Latin America and Caribbean' OR REGION = 'Eastern Asia'
            ORDER BY ECONOMY DESC
        """)) {
            if (result.next()) return Country.fromRow(result);
        }
        return null;
    }

    private static Country getCountryWithAverageStats(ConnectionUtils utils) throws SQLException {
        try (var result = utils.query("""
            WITH AVERAGE AS (SELECT avg("HAPPINESS SCORE") + avg("STANDARD ERROR") + avg(ECONOMY) + avg(FAMILY) +
            avg(HEALTH) + avg(FREEDOM) + avg(TRUST) + avg(GENEROSITY) + avg("DYSTOPIA RESIDUAL") FROM COUNTRIES)
            SELECT * FROM COUNTRIES WHERE REGION = 'Western Europe' OR REGION = 'North America'
            ORDER BY abs("HAPPINESS SCORE" + "STANDARD ERROR" + ECONOMY + FAMILY + HEALTH + FREEDOM +
            TRUST + GENEROSITY + "DYSTOPIA RESIDUAL" - (SELECT * FROM AVERAGE));
        """)) {
            if (result.next()) return Country.fromRow(result);
        }
        return null;
    }
}
