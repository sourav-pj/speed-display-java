package org.speed;

import oshi.SystemInfo;
import oshi.hardware.NetworkIF;
import java.util.List;

public class NetworkModel {
    private final List<NetworkIF> networkIFs;
    private long previousRxBytes = 0;
    private long previousTxBytes = 0;

    public NetworkModel() {
        SystemInfo si = new SystemInfo();
        this.networkIFs = si.getHardware().getNetworkIFs();
    }

    public long[] getCurrentSpeeds() {
        long currentRx = 0;
        long currentTx = 0;

        for (NetworkIF net : networkIFs) {
            net.updateAttributes();
            currentRx += net.getBytesRecv();
            currentTx += net.getBytesSent();
        }

        if (previousRxBytes == 0) {
            previousRxBytes = currentRx;
            previousTxBytes = currentTx;
            return new long[]{0, 0};
        }

        long download = currentRx - previousRxBytes;
        long upload = currentTx - previousTxBytes;

        previousRxBytes = currentRx;
        previousTxBytes = currentTx;

        return new long[]{download, upload};
    }

    public static String formatSpeed(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), "KMGTPE".charAt(exp - 1));
    }
}