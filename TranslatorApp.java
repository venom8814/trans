package translator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

public class TranslatorApp extends JFrame {
    
    private JTextArea inputArea;
    private JTextArea outputArea;
    private JComboBox<String> sourceLanguage;
    private JComboBox<String> targetLanguage;
    private JButton translateButton;
    private JButton swapButton;
    private JButton clearButton;
    private DictionaryManager dictionaryManager;
    
    private static final String[] LANGUAGES = {"Русский", "English", "Deutsch"};
    private static final String[] LANG_CODES = {"ru", "en", "de"};
    
    public TranslatorApp() {
        dictionaryManager = new DictionaryManager();
        initializeUI();
        setVisible(true);
    }
    
    private void initializeUI() {
        setTitle("Офлайн Переводчик");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(500, 400));
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(245, 245, 245));
        
        JPanel languagePanel = createLanguagePanel();
        mainPanel.add(languagePanel, BorderLayout.NORTH);
        
        JPanel textPanel = createTextPanel();
        mainPanel.add(textPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createLanguagePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        panel.setOpaque(false);
        
        JLabel fromLabel = new JLabel("Исходный язык:");
        fromLabel.setFont(new Font("Arial", Font.BOLD, 12));
        sourceLanguage = new JComboBox<>(LANGUAGES);
        sourceLanguage.setPreferredSize(new Dimension(120, 30));
        sourceLanguage.setSelectedIndex(1); // English по умолчанию
        
        swapButton = new JButton("⇄");
        swapButton.setFont(new Font("Arial", Font.BOLD, 16));
        swapButton.setPreferredSize(new Dimension(50, 30));
        swapButton.setToolTipText("Поменять языки местами");
        swapButton.addActionListener(e -> swapLanguages());
        
        JLabel toLabel = new JLabel("Целевой язык:");
        toLabel.setFont(new Font("Arial", Font.BOLD, 12));
        targetLanguage = new JComboBox<>(LANGUAGES);
        targetLanguage.setPreferredSize(new Dimension(120, 30));
        targetLanguage.setSelectedIndex(0); // Русский по умолчанию
        
        panel.add(fromLabel);
        panel.add(sourceLanguage);
        panel.add(swapButton);
        panel.add(toLabel);
        panel.add(targetLanguage);
        
        return panel;
    }
    
    private JPanel createTextPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setOpaque(false);
        
        JPanel inputPanel = new JPanel(new BorderLayout(0, 5));
        inputPanel.setOpaque(false);
        JLabel inputLabel = new JLabel("Введите текст:");
        inputLabel.setFont(new Font("Arial", Font.BOLD, 12));
        inputArea = new JTextArea();
        inputArea.setFont(new Font("Arial", Font.PLAIN, 14));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        inputArea.addKeyListener(new KeyAdapter() {
            private Timer timer = new Timer(500, e -> translate());
            {
                timer.setRepeats(false);
            }
            @Override
            public void keyReleased(KeyEvent e) {
                timer.restart();
            }
        });
        
        JScrollPane inputScroll = new JScrollPane(inputArea);
        inputScroll.setBorder(null);
        inputPanel.add(inputLabel, BorderLayout.NORTH);
        inputPanel.add(inputScroll, BorderLayout.CENTER);
        
        JPanel outputPanel = new JPanel(new BorderLayout(0, 5));
        outputPanel.setOpaque(false);
        JLabel outputLabel = new JLabel("Перевод:");
        outputLabel.setFont(new Font("Arial", Font.BOLD, 12));
        outputArea = new JTextArea();
        outputArea.setFont(new Font("Arial", Font.PLAIN, 14));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(250, 250, 250));
        outputArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JScrollPane outputScroll = new JScrollPane(outputArea);
        outputScroll.setBorder(null);
        outputPanel.add(outputLabel, BorderLayout.NORTH);
        outputPanel.add(outputScroll, BorderLayout.CENTER);
        
        panel.add(inputPanel);
        panel.add(outputPanel);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);
        
        translateButton = new JButton("Перевести");
        translateButton.setFont(new Font("Arial", Font.BOLD, 14));
        translateButton.setPreferredSize(new Dimension(140, 35));
        translateButton.setBackground(new Color(70, 130, 180));
        translateButton.setForeground(Color.WHITE);
        translateButton.setFocusPainted(false);
        translateButton.addActionListener(e -> translate());
        
        clearButton = new JButton("Очистить");
        clearButton.setFont(new Font("Arial", Font.BOLD, 14));
        clearButton.setPreferredSize(new Dimension(140, 35));
        clearButton.addActionListener(e -> {
            inputArea.setText("");
            outputArea.setText("");
        });
        
        JButton copyButton = new JButton("Копировать");
        copyButton.setFont(new Font("Arial", Font.BOLD, 14));
        copyButton.setPreferredSize(new Dimension(140, 35));
        copyButton.addActionListener(e -> {
            if (!outputArea.getText().isEmpty()) {
                outputArea.selectAll();
                outputArea.copy();
                outputArea.setCaretPosition(0);
            }
        });
        
        panel.add(translateButton);
        panel.add(clearButton);
        panel.add(copyButton);
        
        return panel;
    }
    
    private void swapLanguages() {
        int sourceIndex = sourceLanguage.getSelectedIndex();
        int targetIndex = targetLanguage.getSelectedIndex();
        sourceLanguage.setSelectedIndex(targetIndex);
        targetLanguage.setSelectedIndex(sourceIndex);
        
        String inputText = inputArea.getText();
        String outputText = outputArea.getText();
        inputArea.setText(outputText);
        outputArea.setText(inputText);
    }
    
    private void translate() {
        String text = inputArea.getText().trim();
        if (text.isEmpty()) {
            outputArea.setText("");
            return;
        }
        
        String sourceLang = LANG_CODES[sourceLanguage.getSelectedIndex()];
        String targetLang = LANG_CODES[targetLanguage.getSelectedIndex()];
        
        if (sourceLang.equals(targetLang)) {
            outputArea.setText(text);
            return;
        }
        
        String translation = dictionaryManager.translate(text, sourceLang, targetLang);
        outputArea.setText(translation);
    }
}
