package br.com.gustavo.iot.gui;

import javax.swing.*;

import br.com.gustavo.iot.Redes;
import br.com.gustavo.iot.model.Ip;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class Screen extends JFrame {
    private JTextField ipField;
    private JTextField prefixField;
    private JTextArea resultArea;

    public Screen() {
        setTitle("Calculadora de Sub-redes IP");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Painel de entrada com campos e botão
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputPanel.add(new JLabel("IP:"));
        ipField = new JTextField("192.168.1.0"); // Valor padrão
        inputPanel.add(ipField);

        inputPanel.add(new JLabel("Prefixo (ex: 26):"));
        prefixField = new JTextField("26"); // Valor padrão
        inputPanel.add(prefixField);

        JButton calculateButton = new JButton("Calcular");
        calculateButton.addActionListener(this::calculate); // Ação do botão
        inputPanel.add(calculateButton);

        add(inputPanel, BorderLayout.NORTH);

        // Área de resultado
        resultArea = new JTextArea();
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
    }

    // Verifica se o IP inserido é válido
    private boolean isValidIp(String ip) {
        String[] parts = ip.split("\\.");
        if (parts.length != 4) return false;
        try {
            for (String part : parts) {
                int num = Integer.parseInt(part);
                if (num < 0 || num > 255) return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    // Ação executada ao clicar no botão "Calcular"
    private void calculate(ActionEvent e) {
        String ipStr = ipField.getText().trim();
        String prefixStr = prefixField.getText().trim();

        // Validação do IP
        if (!isValidIp(ipStr)) {
            resultArea.setText("Erro: IP inválido. Digite um IP como 192.168.1.0");
            return;
        }

        int prefix;
        // Validação do prefixo
        try {
            prefix = Integer.parseInt(prefixStr);
            if (prefix < 1 || prefix > 32) {
                resultArea.setText("Erro: Prefixo inválido. Use um valor entre 1 e 32.");
                return;
            }
        } catch (NumberFormatException ex) {
            resultArea.setText("Erro: Prefixo deve ser um número.");
            return;
        }

        // Realiza o cálculo e exibe os resultados
        try {
            Ip ip = new Ip(ipStr, prefix);
            Redes redes = new Redes(ip);

            StringBuilder sb = new StringBuilder();
            sb.append("IP: ").append(ip.getIpString()).append("/").append(prefix).append("\n");
            sb.append("Classe: ").append(ip.getClassType()).append("\n");
            sb.append("Máscara (decimal): ").append(ip.getSubnetMask()).append("\n");
            sb.append("Máscara (binário): ").append(ip.getSubnetMaskBinary()).append("\n");
            sb.append("Hosts por sub-rede: ").append(redes.getHostsPerSubnet()).append("\n");
            sb.append("Número de sub-redes: ").append(redes.getSubnetCount()).append("\n\n");

            List<String> subnets = redes.listSubnets();
            for (String subnetInfo : subnets) {
                sb.append(subnetInfo).append("\n");
            }

            resultArea.setText(sb.toString());

        } catch (Exception ex) {
            resultArea.setText("Erro: " + ex.getMessage());
        }
    }

    // Método principal: executa a interface gráfica
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Screen().setVisible(true));
    }
}

