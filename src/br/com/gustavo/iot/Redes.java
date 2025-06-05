package br.com.gustavo.iot;
import java.util.ArrayList;
import java.util.List;

import br.com.gustavo.iot.model.Ip;

public class Redes {
    private final Ip ip;

    public Redes(Ip ip) {
        this.ip = ip;
    }

    public int getHostsPerSubnet() {
        int hostBits = 32 - ip.getPrefix();
        return (hostBits <= 0) ? 0 : (int) Math.pow(2, hostBits) - 2;
    }

    public int getSubnetCount() {
        int defaultPrefix = switch (ip.getClassType()) {
            case "A" -> 8;
            case "B" -> 16;
            case "C" -> 24;
            default -> ip.getPrefix();
        };
        int subnetBits = ip.getPrefix() - defaultPrefix;
        if (subnetBits < 0) return 1;
        return (int) Math.pow(2, subnetBits);
    }

    public List<String> listSubnets() {
        List<String> subnets = new ArrayList<>();

        int prefix = ip.getPrefix();
        int ipBase = ip.toInt();
        int subnetMask = 0xFFFFFFFF << (32 - prefix);
        int blockSize = 1 << (32 - prefix);
        int numSubnets = getSubnetCount();

        for (int i = 0; i < numSubnets; i++) {
            int subnetAddress = (ipBase & subnetMask) + (i * blockSize);
            int broadcast = subnetAddress + blockSize - 1;
            int firstIp = subnetAddress + 1;
            int lastIp = broadcast - 1;

            if (blockSize <= 2) {
                subnets.add(String.format(
                        "Sub-rede: %s/%d\n  Broadcast: %s\n  IPs válidos: N/A (prefixo muito curto)\n",
                        Ip.intToIp(subnetAddress), prefix,
                        Ip.intToIp(broadcast)
                ));
            } else {
                subnets.add(String.format(
                        "Sub-rede: %s/%d\n  Broadcast: %s\n  IPs válidos: %s - %s\n",
                        Ip.intToIp(subnetAddress), prefix,
                        Ip.intToIp(broadcast),
                        Ip.intToIp(firstIp),
                        Ip.intToIp(lastIp)
                ));
            }
        }

        return subnets;
    }
}

