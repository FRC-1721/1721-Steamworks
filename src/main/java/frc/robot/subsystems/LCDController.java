package org.frc1721.steamworks.subsystems;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * LCD Controller
 * Modified from RaiderRobotic's display software.
 * https://github.com/RaiderRobotics/FRC-LCD-Display
 * @author Brennan
 *
 */
@SuppressWarnings("unused")
public class LCDController extends Subsystem {

	@Override
	protected void initDefaultCommand() {}

	/** LCD CONSTANTS **/
	// ---------> COMMANDS <----------
	private static final int LCD_CLEARDISPLAY = 0x01;
	private static final int LCD_RETURNHOME = 0x02;
	private static final int LCD_ENTRYMODESET = 0x04;
	private static final int LCD_DISPLAYCONTROL = 0x08;
	private static final int LCD_CURSORSHIFT = 0x10;
	private static final int LCD_FUNCTIONSET = 0x20;
	private static final int LCD_SETCGRAMADDR = 0x40;
	private static final int LCD_SETDDRAMADDR = 0x80;
	
	// ---------> FLAGS <-----------
	private static final int LCD_DISPLAYON = 0x04;
	private static final int LCD_DISPLAYOFF = 0x00;
	private static final int LCD_CURSORON = 0x02;
	private static final int LCD_CURSOROFF = 0x00;
	private static final int LCD_BLINKON = 0x01;
	private static final int LCD_BLINKOFF = 0x00;
	private static final int LCD_ENTRYLEFT = 0x02;
	private static final int LCD_ENTRYSHIFTINCREMENT = 0x01;
	private static final int LCD_ENTRYSHIFTDECREMENT = 0x00;
	private static final int LCD_DISPLAYMOVE = 0x08;
	private static final int LCD_CURSORMOVE = 0x00;
	private static final int LCD_MOVERIGHT = 0x04;
	private static final int LCD_MOVELEFT = 0x00;
	private static final int LCD_8BITMODE = 0x10;
	private static final int LCD_4BITMODE = 0x00;
	private static final int LCD_2LINE = 0x08;	//for 2 or 4 lines actually
	private static final int LCD_1LINE = 0x00;
	private static final int LCD_5x10DOTS = 0x04;	//seldom used!!
	private static final int LCD_5x8DOTS = 0x00;	
	private static final int LCD_BACKLIGHT = 0x08;
	private static final int LCD_NOBACKLIGHT = 0x00;
	
	// ---------> BITMASKS <-----------
	private static final int En = 0b00000100; // Enable bit
	private static final int Rw = 0b00000010; // Read/Write bit
	private static final int Rs = 0b00000001; // Register select bit

	/**
	 * Given an I2C object (assumed to be the LCD) initialize the display.
	 * @param lcd - An I2C object assumed to be the LCD.
	 */
	public void initLCD(I2C lcd) {
//		lcd = new I2C(I2C.Port.kOnboard, 0x27);
		
		sendCMD(lcd, 0x03);
		sendCMD(lcd, 0x03);
		sendCMD(lcd, 0x03);
		sendCMD(lcd, 0x02);
		
		sendCMD(lcd, LCD_FUNCTIONSET | LCD_2LINE | LCD_5x8DOTS | LCD_4BITMODE);
		sendCMD(lcd, LCD_DISPLAYCONTROL | LCD_DISPLAYON);
		sendCMD(lcd, LCD_CLEARDISPLAY);
		sendCMD(lcd, LCD_ENTRYMODESET | LCD_ENTRYLEFT);
		i2cSleep(10);
	}
	
	private static void i2cSleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO: Handle this
		}
	}
	
	private static void sendCMD(I2C lcd, int data) {
		rawWrite(lcd, (data & 0xF0));
		rawWrite(lcd, ((data <<4) & 0xF0));
	}
	
	private static void writeChar(I2C lcd, int data) {
		rawWrite(lcd, Rs | (data & 0xF0));
		rawWrite(lcd, Rs | ((data <<4) & 0xF0));
	}
	private static void rawWrite(I2C lcd, int data) {
		lcd.write(0,  data | LCD_BACKLIGHT);
		strobe(lcd, data);
	}
	
	private static void strobe(I2C lcd, int data) {
		lcd.write(0, data | En | LCD_BACKLIGHT);
		i2cSleep(1);
		lcd.write(0, (data & ~En) | LCD_BACKLIGHT);
		i2cSleep(1);
	}
	
	/**
	 * Write a string to the LCD on a given line.
	 * @param message - String to write
	 * @param line - Line to use (1 or 2)
	 */
	public static void print(I2C lcd, String message, int line) {
		switch (line) {
			case 1:
				sendCMD(lcd, 0x80);
				break;
			case 2:
				sendCMD(lcd, 0xC0);
				break;
			default:
				return; // Don't want an invalid number to break everything
		}
		
		if (message.length() > 16) {
			message = message.substring(0, 16);
		}
		
		for (int i = 0; i < message.length(); i++) {
			writeChar(lcd, message.charAt(i));
		}
	}
	/**
	 * Clear the LCD, and then put the cursor on line 1.
	 */
	public static void clear(I2C lcd) {
		sendCMD(lcd, LCD_CLEARDISPLAY);
		sendCMD(lcd, 0x80);
	}
}
