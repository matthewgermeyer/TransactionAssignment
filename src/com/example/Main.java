package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;

public class Main {
    public static void main(String[] args) {

        useTransactions(args[0]);
    }

    private static void useTransactions(String commitOrRoll) {
        Connection conn = null;
        try {
            conn = DatabaseUtils.getInstance().getConnection();
            System.out.println(conn.getAutoCommit());
            conn.setAutoCommit(false);
            System.out.println(conn.getAutoCommit());

            LocalDate localDate = LocalDate.of(1963, 2, 5);
            long epochDOB = localDate.atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000;

            String makePerson = "INSERT into person (name, dob) VALUES (?, ?);   ";
            PreparedStatement pStmt = conn.prepareStatement(makePerson);
            pStmt.setString(1, "Sandra " + System.currentTimeMillis());
            pStmt.setDate(2, new java.sql.Date(epochDOB));
            pStmt.executeUpdate();

            String addEmail = "INSERT into email (email, person_id) VALUES (?, ?);   ";
            pStmt = conn.prepareStatement(addEmail);
            pStmt.setString(1, "SandraDOC@supremeCourt.gov");
            pStmt.setInt(2, 9);
            pStmt.executeUpdate();

            String addAddy = "INSERT into address (street1, city, stateAbbr, zip, person_id) VALUES (?, ?, ?, ?, ?);   ";
            pStmt = conn.prepareStatement(addAddy);
            pStmt.setString(1, "1600 Pennysylvania Ave");
            pStmt.setString(2, "Washington DC");
            pStmt.setString(3, "VA");
            pStmt.setString(4, "10348");
            pStmt.setString(5, "9");
            pStmt.executeUpdate();

            ResultSet selectAllFromPeople = pStmt.executeQuery("Select * from person");
            System.out.println("name, dob, gender, contacted");

            while (selectAllFromPeople.next()) {
                StringBuilder sb = new StringBuilder();

                sb.append(selectAllFromPeople.getString("name") + ",");
                sb.append(selectAllFromPeople.getDate("dob") + ",");
                sb.append(selectAllFromPeople.getString("gender") + ",");
                sb.append(selectAllFromPeople.getDate("contacted") + ",");
                System.out.println(sb.toString());

            }

            if (commitOrRoll.equals("true")) {
                System.out.println("commit");
                conn.commit();
            } else {
                System.out.println("rollback");
                conn.rollback();
            }

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    System.out.println("rollback");
                    conn.rollback();
                } catch (SQLException e1) {
                    //Log something ->
                }
            }
            DatabaseUtils.printSQLException(e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                //Log it -->
            }
        }
    }

}
