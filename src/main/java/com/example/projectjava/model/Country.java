package com.example.projectjava.model;

import org.jetbrains.annotations.NotNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public record Country(
        @NotNull String name,
        @NotNull String region,
        int happinessRank,
        double happinessScore,
        double standardError,
        double economy,
        double family,
        double health,
        double freedom,
        double trust,
        double generosity,
        double dystopia
) {

    public static Country fromRow(String row) {
        if (row == null) return null;
        var values = row.split(",");
        if (values.length < 12) return null;
        try {
            return new Country(values[0],
                    values[1],
                    Integer.parseInt(values[2]),
                    Double.parseDouble(values[3]),
                    Double.parseDouble(values[4]),
                    Double.parseDouble(values[5]),
                    Double.parseDouble(values[6]),
                    Double.parseDouble(values[7]),
                    Double.parseDouble(values[8]),
                    Double.parseDouble(values[9]),
                    Double.parseDouble(values[10]),
                    Double.parseDouble(values[11]));
        } catch (NumberFormatException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public static Country fromRow(ResultSet row) {
        try {
            return new Country(
                    row.getString("NAME"),
                    row.getString("REGION"),
                    row.getInt("HAPPINESS RANK"),
                    row.getDouble("HAPPINESS SCORE"),
                    row.getDouble("STANDARD ERROR"),
                    row.getDouble("ECONOMY"),
                    row.getDouble("FAMILY"),
                    row.getDouble("HEALTH"),
                    row.getDouble("FREEDOM"),
                    row.getDouble("TRUST"),
                    row.getDouble("GENEROSITY"),
                    row.getDouble("DYSTOPIA RESIDUAL")
            );
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }
}
