package br.com.gustavo.iot.model;

public class Ip {
    private final int[] octets = new int[4]; // Armazena os 4 octetos do IP
    private final int prefix; // Armazena o prefixo CIDR (ex: /24)

    public Ip(String ip, int prefix) {
        this.prefix = prefix;
        String[] parts = ip.split("\\.");
        if (parts.length != 4) {
            throw new IllegalArgumentException("IP inválido.");
        }
        for (int i = 0; i < 4; i++) {
            int val = Integer.parseInt(parts[i]);
            if (val < 0 || val > 255) {
                throw new IllegalArgumentException("Octeto inválido: " + val);
            }
            octets[i] = val;
        }
    }

    public int getPrefix() {
        return prefix;
    }

    public int[] getOctets() {
        return octets;
    }

    public String getIpString() {
        return String.format("%d.%d.%d.%d", octets[0], octets[1], octets[2], octets[3]);
    }

    public String getClassType() {
        int first = octets[0];
        if (first < 128)
            return "A";
        else if (first < 192)
            return "B";
        else if (first < 224)
            return "C";
        else if (first < 240)
            return "D";
        else
            return "E";
    }

    // Máscara decimal no formato tradicional
    public String getSubnetMask() {
        int mask = 0xFFFFFFFF << (32 - prefix);
        return String.format("%d.%d.%d.%d",
                (mask >> 24) & 0xFF,
                (mask >> 16) & 0xFF,
                (mask >> 8) & 0xFF,
                mask & 0xFF
        );
    }

    // Máscara em formato binário, 4 grupos de 8 bits separados por pontos
    public String getSubnetMaskBinary() {
        int mask = 0xFFFFFFFF << (32 - prefix);
        StringBuilder binaryMask = new StringBuilder();
        for (int i = 3; i >= 0; i--) {
            int octet = (mask >> (i * 8)) & 0xFF;
            String binStr = String.format("%8s", Integer.toBinaryString(octet)).replace(' ', '0');
            binaryMask.append(binStr);
            if (i != 0) {
                binaryMask.append(".");
            }
        }
        return binaryMask.toString();
    }

    public int toInt() {
        return (octets[0] << 24) | (octets[1] << 16) | (octets[2] << 8) | octets[3];
    }

    public static String intToIp(int ip) {
        return String.format("%d.%d.%d.%d",
                (ip >> 24) & 0xFF,
                (ip >> 16) & 0xFF,
                (ip >> 8) & 0xFF,
                ip & 0xFF
        );
    }
}

