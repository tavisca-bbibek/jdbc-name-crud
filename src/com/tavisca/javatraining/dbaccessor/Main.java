package com.tavisca.javatraining.dbaccessor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class Main {

    private Scanner sc = new Scanner(System.in);
    private ResultSet rs;
    private int currentRow = 0;

    public static void main(String[] args) {
        Main app = new Main();
        String query = "select id, name from person";

        try {
            Connection conn = DbConnection.getConnection("jtraining");
            Statement statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
            app.rs = statement.executeQuery(query);
            while (app.processTableCommand(app.readCommand("Table", "Person"))) ;
        } catch (Exception e) {
            app.showError(e.getMessage());
        }
    }

    private String readCommand(String context, String item) {
        System.out.print(context + "[" + item + "]: ");
        return sc.nextLine();
    }

    private void showTableCommandUsage() {
        System.out.println("Commands:");
        System.out.println("\tshow all | <row-number>");
        System.out.println("\tadd <person-name>");
        System.out.println("\tremove <row-number>");
        System.out.println("\tupdate <row-number> <person-name>");
        System.out.println("\trow <row-number>");
        System.out.println("\tquit");
    }

    private boolean processTableCommand(String command) throws Exception {
        String[] tokens = command.split("\\s+");
        try {
            switch (tokens[0]) {
                case "show":
                    if (tokens[1].equals("all"))
                        showAll();
                    else {
                        int row = Integer.parseInt(tokens[1]);
                        show(row);
                    }
                    break;
                case "add":
                    add(tokens[1]);
                    break;
                case "remove":
                    int row = Integer.parseInt(tokens[1]);
                    remove(row);
                    break;
                case "update":
                    row = Integer.parseInt(tokens[1]);
                    String name = tokens[2];
                    update(row, name);
                    break;
                case "row":
                    if (tokens.length != 1) {
                        try {
                            currentRow = Integer.parseInt(tokens[1]);
                        } catch (Exception e) {
                            showRowCommandUsage();
                        }
                    }
                    rs.absolute(currentRow);
                    while (processRowCommand(readCommand("Row", String.valueOf(currentRow)))) ;
                    break;
                case "quit":
                    return false;
                default:
                    showTableCommandUsage();
            }
        } catch (IndexOutOfBoundsException e) {
            showTableCommandUsage();
        }
        return true;
    }

    private void add(String token) throws SQLException {
        rs.moveToInsertRow();
        rs.updateString("name", token);
        rs.insertRow();
    }

    private void showAll() throws SQLException {
        rs.beforeFirst();
        int row = 0;
        System.out.println("Row\tName");
        while (rs.next()) {
            System.out.print(++row + "\t");
            showCurrentRow();
        }
    }

    private void show(int row) throws SQLException {
        rs.absolute(row);
        showCurrentRow();
    }

    private void update(int row, String name) throws SQLException {
        rs.absolute(row);
        rs.updateString("name", name);
        rs.updateRow();
    }

    private void remove(int row) throws SQLException {
        rs.absolute(row);
        rs.deleteRow();
    }

    private void showRowCommandUsage() {
        System.out.println("Commands: ");
        System.out.println("\tnext");
        System.out.println("\tprev");
        System.out.println("\tshow");
        System.out.println("\tupdate <person-name>");
        System.out.println("\tremove");
    }

    private boolean processRowCommand(String rowCommand) throws SQLException {
        String[] tokens = rowCommand.split("\\s+");
        try {
            switch (tokens[0]) {
                case "show":
                    showCurrentRow();
                    break;
                case "update":
                    rs.updateString("name", tokens[1]);
                    rs.updateRow();
                    break;
                case "remove":
                    rs.deleteRow();
                    break;
                case "next":
                    moveToNextRow();
                    break;
                case "prev":
                    moveToPreviousRow();
                    break;
                case "quit":
                    return false;
                default:
                    showRowCommandUsage();
            }
            currentRow = rs.getRow();
        } catch (IndexOutOfBoundsException e) {
            showRowCommandUsage();
        }
        return true;
    }

    private void moveToNextRow() throws SQLException {
        if (!rs.next())
            rs.first();
    }

    private void moveToPreviousRow() throws SQLException {
        if (!rs.previous())
            rs.last();
    }

    private void showCurrentRow() throws SQLException {
        String name = rs.getString("name");
        System.out.println(name);
    }

    private void showError(String message) {
        System.err.println("Error: " + message);
    }
}
