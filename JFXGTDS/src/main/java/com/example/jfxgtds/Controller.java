package com.example.jfxgtds;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Controller {

    @FXML private TableView<Transaction> table;
    @FXML private TableColumn<Transaction, String> itemCodeCol;
    @FXML private TableColumn<Transaction, Double> internalPriceCol;
    @FXML private TableColumn<Transaction, Double> discountCol;
    @FXML private TableColumn<Transaction, Double> salePriceCol;
    @FXML private TableColumn<Transaction, Integer> quantityCol;
    @FXML private TableColumn<Transaction, Integer> checksumCol;
    @FXML private TableColumn<Transaction, Double> profitCol;

    @FXML private TextField filePathField;
    @FXML private Label summaryLabel;
    @FXML private TextField taxRateField;

    private ObservableList<Transaction> transactions = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup table columns
        itemCodeCol.setCellValueFactory(new PropertyValueFactory<>("itemCode"));
        internalPriceCol.setCellValueFactory(new PropertyValueFactory<>("internalPrice"));
        discountCol.setCellValueFactory(new PropertyValueFactory<>("discount"));
        salePriceCol.setCellValueFactory(new PropertyValueFactory<>("salePrice"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        checksumCol.setCellValueFactory(new PropertyValueFactory<>("checksum"));
        profitCol.setCellValueFactory(new PropertyValueFactory<>("profit"));

        table.setItems(transactions);
    }

    @FXML
    private void browseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Transaction File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            filePathField.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void importTransactions() {
        String path = filePathField.getText();
        transactions.clear();
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            for (String line : lines) {
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String itemCode = parts[0];
                    double internalPrice = Double.parseDouble(parts[1]);
                    double discount = Double.parseDouble(parts[2]);
                    double salePrice = Double.parseDouble(parts[3]);
                    int quantity = Integer.parseInt(parts[4]);
                    int checksum = Integer.parseInt(parts[5]);
                    transactions.add(new Transaction(itemCode, internalPrice, discount, salePrice, quantity, checksum));
                }
            }
            summaryLabel.setText("Imported " + transactions.size() + " transactions.");
        } catch (IOException ex) {
            summaryLabel.setText("Error: " + ex.getMessage());
        }
    }

    @FXML
    private void validateTransactions() {
        long validCount = transactions.stream().filter(Transaction::isValid).count();
        long invalidCount = transactions.size() - validCount;
        summaryLabel.setText("Total Records: " + transactions.size() + ", Valid: " + validCount + ", Invalid: " + invalidCount);
    }

    @FXML
    private void calculateProfit() {
        for (Transaction t : transactions) {
            t.calculateProfit();
        }
        table.refresh();
        summaryLabel.setText("Profit calculated for all transactions.");
    }

    @FXML
    private void deleteZeroProfit() {
        transactions.removeIf(t -> t.getProfit() == 0);
        table.refresh();
        summaryLabel.setText("Deleted transactions with zero profit.");
    }

    @FXML
    private void calculateFinalTax() {
        try {
            double taxRate = Double.parseDouble(taxRateField.getText()) / 100;
            double totalProfit = transactions.stream().mapToDouble(Transaction::getProfit).sum();
            double tax = totalProfit * taxRate;
            summaryLabel.setText(String.format("Final Tax: %.2f", tax));
        } catch (NumberFormatException e) {
            summaryLabel.setText("Invalid tax rate entered.");
        }
    }

    // Transaction Class (you can move this to a separate Transaction.java if you want)
    public static class Transaction {
        private final SimpleStringProperty itemCode;
        private final SimpleDoubleProperty internalPrice;
        private final SimpleDoubleProperty discount;
        private final SimpleDoubleProperty salePrice;
        private final SimpleIntegerProperty quantity;
        private final SimpleIntegerProperty checksum;
        private final SimpleDoubleProperty profit = new SimpleDoubleProperty(0);

        public Transaction(String itemCode, double internalPrice, double discount, double salePrice, int quantity, int checksum) {
            this.itemCode = new SimpleStringProperty(itemCode);
            this.internalPrice = new SimpleDoubleProperty(internalPrice);
            this.discount = new SimpleDoubleProperty(discount);
            this.salePrice = new SimpleDoubleProperty(salePrice);
            this.quantity = new SimpleIntegerProperty(quantity);
            this.checksum = new SimpleIntegerProperty(checksum);
        }

        public String getItemCode() { return itemCode.get(); }
        public double getInternalPrice() { return internalPrice.get(); }
        public double getDiscount() { return discount.get(); }
        public double getSalePrice() { return salePrice.get(); }
        public int getQuantity() { return quantity.get(); }
        public int getChecksum() { return checksum.get(); }
        public double getProfit() { return profit.get(); }

        public void calculateProfit() {
            double p = (salePrice.get()- discount.get() * quantity.get()) - ((internalPrice.get() * quantity.get()) );
            profit.set(p);
        }

        public boolean isValid() {
            return calculateChecksum() == checksum.get()
                    && !itemCode.get().matches(".*[^a-zA-Z0-9].*")
                    && internalPrice.get() >= 0;
        }

        private int calculateChecksum() {
            int sum = 0;
            for (char c : itemCode.get().toCharArray()) {
                sum += c;
            }
            sum += (int) internalPrice.get() + (int) discount.get() + (int) salePrice.get() + quantity.get();
            return sum;
        }
    }
}