package messageformats;

import java.util.Arrays;

public class PaData {
    int padataType;
    byte[] padataValue;

    public PaData() {
    }

    public PaData(int padataType, byte[] padataValue) {
        this.padataType = padataType;
        this.padataValue = padataValue;
    }

    public int getPadataType() {
        return padataType;
    }

    public void setPadataType(int padataType) {
        this.padataType = padataType;
    }

    public byte[] getPadataValue() {
        return padataValue;
    }

    public void setPadataValue(byte[] padataValue) {
        this.padataValue = padataValue;
    }

    @Override
    public String toString() {
        return "PaData{" +
                "padataType=" + padataType +
                ", padataValue=" + Arrays.toString(padataValue) +
                '}';
    }
}
