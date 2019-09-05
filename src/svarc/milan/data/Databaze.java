/*
 * Copyright (c) 2019. Created by Milan Švarc
 */

package svarc.milan.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.sql.*;

public class Databaze {

    private static final String JMENO_DB = "kalendar.db";
    private static final String CESTA = "jdbc:sqlite:"+ System.getProperty("user.dir")+File.separator;

    private final static String TABULKA_JMENO = "kalendar_data";
    private final static String SLOUPEC_PREDMET = "predmet";
    private final static String SLOUPEC_POZNAMKA = "poznamka";
    private final static String SLOUPEC_DATUM = "datum";

    /* SQlite dotazy*/
    private static final String VYTVOR_TABULKU_KALENDAR = "CREATE TABLE IF NOT EXISTS " + TABULKA_JMENO + " (" +
            SLOUPEC_PREDMET + " TEXT," +
            SLOUPEC_POZNAMKA + " TEXT," +
            SLOUPEC_DATUM + " TEXT);";
    private static final String NACTI_TABULKU_KALENDAR = "SELECT " +
            SLOUPEC_PREDMET + ", " +
            SLOUPEC_POZNAMKA + ", " +
            SLOUPEC_DATUM + " FROM " + TABULKA_JMENO;
    private static final String VLOZIT_UKOL = "INSERT INTO " + TABULKA_JMENO +
            '(' + SLOUPEC_PREDMET + ", " + SLOUPEC_POZNAMKA + ", " + SLOUPEC_DATUM +
            ") VALUES(?, ?, ?)";
    private static final String VYMAZAT_POLOZKU = "DELETE FROM " + TABULKA_JMENO
            + " WHERE rowid =(SELECT MIN(rowid) FROM " + TABULKA_JMENO
            + " WHERE " + SLOUPEC_PREDMET + " = ? AND " + SLOUPEC_POZNAMKA + " = ? AND " + SLOUPEC_DATUM + " = ?)";


    private static Databaze kalendar = new Databaze();
    private ObservableList<UkolLabel> dataKalendare = FXCollections.observableArrayList();

    public static Databaze getKalendar() {
        return kalendar;
    }

    /**
     * @return Vrací aktualní data načtená z databáze
     */
    public ObservableList<UkolLabel> getDataKalendare() {
        nactiKalendarData();
        return dataKalendare;
    }

    /**
     * Slouží pro vytvoření hlavní tabulky databáze v případě, že neexistuje.
     */
    public void vytvorTabulkuKalendar() {
        String sql = VYTVOR_TABULKU_KALENDAR;
        try (Connection conn = DriverManager.getConnection(CESTA + File.separator + JMENO_DB);
             Statement statement = conn.createStatement()
        ) {
            statement.execute(sql);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @return true v případě úspěšného načtení dat z databáze
     */
    private boolean nactiKalendarData() {
        String sql = NACTI_TABULKU_KALENDAR;
        dataKalendare.removeAll();
        dataKalendare.clear();

        try (Connection conn = DriverManager.getConnection(CESTA + File.separator + JMENO_DB);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Naplní dataKalendare daty z databáze
            while (rs.next()) {
                dataKalendare.add(new UkolLabel(rs.getString(SLOUPEC_PREDMET), rs.getString(SLOUPEC_POZNAMKA), rs.getString(SLOUPEC_DATUM)) {
                });
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return true;
    }

    /**
     * @param dataUkolu data, která se mají uložit
     * @return true, v případě úspěšného vložení
     */
    public boolean vlozUkolDoDatabaze(UkolLabel dataUkolu) {
        String sql = VLOZIT_UKOL;

        try (Connection conn = DriverManager.getConnection(CESTA + File.separator + JMENO_DB);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, dataUkolu.getNadpis());
            pstmt.setString(2, dataUkolu.getPoznamka());
            pstmt.setString(3, dataUkolu.getDatum());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        nactiKalendarData();
        return true;
    }

    /**
     * @param kalendarData data, která se najdou a vymažou
     * @return vrací text s popisem výsledku akce
     */
    public String vymazatUkol(UkolLabel kalendarData) {
        String sql = VYMAZAT_POLOZKU;
        String text;

        try (Connection conn = DriverManager.getConnection(CESTA + File.separator + JMENO_DB);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, kalendarData.getNadpis());
            pstmt.setString(2, kalendarData.getPoznamka());
            pstmt.setString(3, kalendarData.getDatum().toString());
            int status;
            status = pstmt.executeUpdate();
            switch (status) {
                case 0:
                    text = "Status> " + status + ". Položka nenalezena.";
                    break;
                default:
                    text = "Status> " + status + ". Položka úspěšně vymazána.";
                    nactiKalendarData();
                    break;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            text = "Položku se nepovedlo vymazat z databáze!!!";
        }
        return text;
    }

}
