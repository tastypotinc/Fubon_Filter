package org.Fubon.Server;
/**
 * The TelnetCommand class cannot be instantiated and only serves as a
 * storehouse for telnet command constants.
 * @author Daniel F. Savarese
 * @see org.apache.commons.net.telnet.Telnet
 * @see org.apache.commons.net.telnet.TelnetClient
 */

public final class DataCommand
{
    /*** The maximum value a command code can have.  This value is 255. ***/
    public static final int MAX_COMMAND_VALUE = 255;

    /*** Interpret As Command code.  Value is 255 according to RFC 854. ***/
    public static final int FrameStartByte = 0x02;

    /*** Don't use option code.  Value is 254 according to RFC 854. ***/
    public static final int FrameEndByte = 0x03;
    
   

    // Cannot be instantiated
    private DataCommand()
    { }
}
