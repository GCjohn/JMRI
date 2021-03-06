package jmri.jmrix.pi;

import javax.annotation.Nonnull;
import jmri.Sensor;

/**
 * Manage the RaspberryPi specific Sensor implementation.
 *
 * System names are "PSnnn", where P is the user configurable system prefix,
 * nnn is the sensor number without padding.
 *
 * @author   Paul Bender Copyright (C) 2015
 */
public class RaspberryPiSensorManager extends jmri.managers.AbstractSensorManager {

    // ctor has to register for RaspberryPi events
    public RaspberryPiSensorManager(RaspberryPiSystemConnectionMemo memo) {
        super(memo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public RaspberryPiSystemConnectionMemo getMemo() {
        return (RaspberryPiSystemConnectionMemo) memo;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public Sensor createNewSensor(@Nonnull String systemName, String userName) {
        return new RaspberryPiSensor(systemName, userName);
    }

    /**
     * Do the sensor objects provided by this manager support configuring
     * an internal pullup or pull down resistor?
     * <p>
     * For Raspberry Pi systems, it is possible to set the pullup or 
     * pulldown resistor, so return true.
     *
     * @return true if pull up/pull down configuration is supported.
     */
    @Override
    public boolean isPullResistanceConfigurable(){
       return true;
    }
    
    /**
     * Validates to Integer Format 0-999 with valid prefix.
     * eg. PS0 to PS999
     * {@inheritDoc}
     */
    @Override
    @Nonnull
    public String validateSystemNameFormat(@Nonnull String name, @Nonnull java.util.Locale locale) throws jmri.NamedBean.BadSystemNameException {
        return this.validateIntegerSystemNameFormat(name, 0, 999, locale);
    }

}
