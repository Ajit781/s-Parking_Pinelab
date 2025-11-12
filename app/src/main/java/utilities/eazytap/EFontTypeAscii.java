package utilities.eazytap;

public enum EFontTypeAscii {
    FONT_8_16((byte)0),
    FONT_8_24((byte)0),
    /** @deprecated */
    @Deprecated
    FONT_16_24((byte)1),
    FONT_12_24((byte)1),
    FONT_8_32((byte)2),
    /** @deprecated */
    @Deprecated
    FONT_16_48((byte)3),
    FONT_12_48((byte)3),
    FONT_16_16((byte)4),
    /** @deprecated */
    @Deprecated
    FONT_32_24((byte)5),
    FONT_24_24((byte)5),
    FONT_16_32((byte)6),
    /** @deprecated */
    @Deprecated
    FONT_32_48((byte)7),
    FONT_24_48((byte)7);

    private byte fontTypeAscii;

    private EFontTypeAscii(byte fontTypeAscii) {
        this.fontTypeAscii = fontTypeAscii;
    }

}
