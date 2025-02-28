package CurrencyConverterP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CurrencyConverterUI extends JFrame {
    private JComboBox<String> currencyFrom;
    private JComboBox<String> currencyTo;
    private JTextField amountField;
    private JTextArea resultArea;
    private JTextArea historyArea; // Area to show conversion history
    private JButton convertButton;
    private JButton viewHistoryButton;
    private JButton deleteButton;

    private static final String[] CURRENCIES = {
        "AFN", "ALL", "DZD", "AOA", "ARS", "AMD", "AWG", "AUD", "AZN", "BSD",
        "BHD", "BDT", "BBD", "BYN", "BZD", "BMD", "BTN", "BOB", "BAM", "BRL",
        "BND", "BGN", "BIF", "CVE", "KHR", "CAD", "KYD", "CLP", "CNY", "COP",
        "CRC", "HRK", "CUP", "CYP", "CZK", "DKK", "DJF", "DOP", "EGP", "ERN",
        "ETB", "EUR", "FJD", "FKP", "GMD", "GEL", "GHS", "GIP", "GBP", "GTQ",
        "GNF", "GYD", "HKD", "HUF", "ISK", "INR", "IDR", "IRR", "IQD", "ILS",
        "JMD", "JPY", "JOD", "KZT", "KES", "KPW", "KRW", "KWD", "KGS", "LAK",
        "LBP", "LSL", "LRD", "LYD", "MAD", "MDL", "MGA", "MKD", "MOP", "MNT",
        "MAD", "MUR", "MXN", "MYR", "MVR", "NAD", "NPR", "NZD", "NIO", "NGN",
        "NOK", "OMR", "PKR", "PAB", "PGK", "PYG", "PHP", "QAR", "RON", "RUB",
        "RWF", "SVC", "SAR", "RSD", "SCR", "SGD", "SBD", "SOS", "ZAR", "LKR",
        "SDG", "SRD", "SZL", "SEK", "CHF", "SYP", "TWD", "TZS", "THB", "TOP",
        "TTD", "TND", "TRY", "UGX", "UAH", "AED", "USD", "UYU", "UZS", "VUV",
        "VES", "VND", "YER", "ZMW", "ZWL"
    };

    public CurrencyConverterUI() {
        setTitle("Currency Converter");
        setLayout(new BorderLayout());

        // Create panels for layout organization
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(5, 4, 10, 10));
        //inputPanel.setBackground(Color.white);
        
        currencyFrom = new JComboBox<>(CURRENCIES);
        currencyTo = new JComboBox<>(CURRENCIES);
        amountField = new JTextField(10);
        convertButton = new JButton("Convert");
        resultArea = new JTextArea(5, 20);
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel("From Currency:"));
        inputPanel.add(currencyFrom);
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel("To Currency:"));
        inputPanel.add(currencyTo);
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));
        inputPanel.add(convertButton);
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel("Result:"));
        inputPanel.add(new JScrollPane(resultArea));

        // Buttons for history
        viewHistoryButton = new JButton("View History");
        deleteButton = new JButton("Delete Selected Entry");

        // Adding Action Listeners
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertCurrency();
            }
        });

        viewHistoryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewConversionHistory();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteHistoryEntry();
            }
        });

        // History area
        historyArea = new JTextArea(10, 25);
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        
        JPanel historyPanel = new JPanel();
        historyPanel.setLayout(new BorderLayout());
        historyPanel.add(new JScrollPane(historyArea), BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewHistoryButton);
        buttonPanel.add(deleteButton);
        historyPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(inputPanel, BorderLayout.NORTH);
        add(historyPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 600);
        setVisible(true);
    }
    
    private void convertCurrency() {
        String fromCurrency = currencyFrom.getSelectedItem().toString();
        String toCurrency = currencyTo.getSelectedItem().toString();
        String amountText = amountField.getText().trim(); // Trim whitespace

        // Check if the amount field is empty
        if (amountText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a currency amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method early
        }

        // Try to parse the amount, catching any number format exceptions
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid amount. Please enter a valid number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder result = new StringBuilder();
        CurrencyConverterC.convertAndSave(fromCurrency, toCurrency, amount, result);
        resultArea.setText(result.toString());
    }

    private void viewConversionHistory() {
        String history = CurrencyConverterC.getConversionHistory();
        historyArea.setText(history.isEmpty() ? "No conversion history found." : history);
    }

    private void deleteHistoryEntry() {
        String input = JOptionPane.showInputDialog(this, "Enter ID of the entry to delete:");
        if (input != null) {
            try {
                int id = Integer.parseInt(input);
                CurrencyConverterC.deleteHistoryEntry(id);
                JOptionPane.showMessageDialog(this, "Entry deleted successfully.");
                viewConversionHistory(); // Refresh the history display
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid ID entered.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public static void main(String[] args) {
        new CurrencyConverterUI();
    }
}
