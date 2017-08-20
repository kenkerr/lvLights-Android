package com.kennethiankerr.lvlightremote.util;

import net.wimpi.modbus.procimg.Register;

/**
 * Created by Ken on 1/3/2016.
 */
public class modbusRegister implements Register {

    private static final int MAX_HOLDING_REGISTERS = 512;

    private short[] holdingRegisters = new short[MAX_HOLDING_REGISTERS];
    protected byte[] m_Register = new byte[2];

    @Override
    public void setValue(int i) {
        m_Register[0] = (byte) (0xff & (i >> 8));
        m_Register[1] = (byte) (0xff & i);
    }

    @Override
    public void setValue(short i) {
        m_Register[0] = (byte) (0xff & (i >> 8));
        m_Register[1] = (byte) (0xff & i);
    }

    @Override
    public void setValue(byte[] bytes) {
        if (bytes.length < 2) {
            throw new IllegalArgumentException();
            } else {
                 m_Register[0] = bytes[0];
                 m_Register[1] = bytes[1];
            }
    }

    @Override
    public int getValue() {
        return ((m_Register[0] & 0xff) << 8 | (m_Register[1] & 0xff));
    }

    @Override
    public int toUnsignedShort() {
        return ((m_Register[0] & 0xff) << 8 | (m_Register[1] & 0xff));
    }

    @Override
    public short toShort() {
        return (short) ((m_Register[0] << 8) | (m_Register[1] & 0xff));
    }

    @Override
    public byte[] toBytes() {
        return m_Register;
    }
}
