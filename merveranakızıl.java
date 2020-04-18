// Merve Rana Kýzýl
// 150119825

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class merveranakýzýl {

	public static void main(String[] args) throws Exception {

		Scanner reader = new Scanner(System.in);
		System.out.print("Enter the name of the file: ");
		String fileName = reader.nextLine();
		Scanner input = null;

		int size = 0;
		String hexStr = "";
		List<String> hex = new ArrayList<>();

		try {
			
			input = new Scanner(new File(fileName));
			
			while(input.hasNext()) {

				String number = input.next();				
				System.out.print("Byte ordering (little/ big): ");
				String order = reader.nextLine();

				if(!(order.equals("little") || order.equals("big"))) {

					input.close();
					reader.close();
					throw new Exception("Enter little for little endian, big for big endian.");					
				}

				if((number.contains("."))) {

					System.out.print("Floating point size: ");
					try {
						
						size = Integer.parseInt(reader.nextLine());
					}
					catch(Exception e) {
						
						System.out.println("Entered value is not an integer!");					
					}
										
					if(!(size == 1 || size == 2 || size == 3 || size == 4)) {

						input.close();
						reader.close();
						throw new Exception("Enter 1, 2, 3 or 4.");					
					}
					
					BigDecimal decimal = new BigDecimal(number);
					String wholeBin = "";

					if(Double.parseDouble(number) < 0) {

						decimal = decimal.multiply(new BigDecimal(-1));
					}

					if(Double.parseDouble(number) < 1 && Double.parseDouble(number) > -1) {

						int exp = findExp(expSize(size), Integer.parseInt(findMantissa(decimal, true)));

						if(exp == 0) {

							if(Double.parseDouble(number) >= 0) {

								wholeBin = "0" + padLeft("0", expSize(size)) + padRight(round(findMantissa(decimal, false), mantissaBit(size)), size);

							}
							else {

								wholeBin = "1" + padLeft("0", expSize(size)) + padRight(round(findMantissa(decimal, false), mantissaBit(size)), size);
							}
						}

						else {

							if(Double.parseDouble(number) >= 0) {

								wholeBin = "0" + padLeft(intToBinary(exp), expSize(size)) + padRight(round(findMantissa(decimal, false), mantissaBit(size)), size);


							}
							else {

								wholeBin = "1" + padLeft(intToBinary(exp), expSize(size)) + padRight(round(findMantissa(decimal, false), mantissaBit(size)), size);
							}

						}
					}
					else {										
						
						if(Double.parseDouble(number) >= 0) {

							wholeBin = "0" + padLeft(intToBinary(findExp(expSize(size), exponentGreater(decimal, mantissaBit(size)))), expSize(size)) + padRight(round(findMantissa(decimal, false), mantissaBit(size)), size);
							
						}
						else {

							wholeBin = "1" + padLeft(intToBinary(findExp(expSize(size), exponentGreater(decimal, mantissaBit(size)))), expSize(size)) + padRight(round(findMantissa(decimal, false), mantissaBit(size)), size);
							
						}
					}

					hexStr = binaryToHex(wholeBin);
					
					// Insert space between every two character
					hexStr = hexStr.replaceAll("..(?!$)", "$0 ");

					// Add hexadecimal number to the list
					if(order.equals("big")) {

						hex.add(hexStr);
					}
					else if(order.equals("little")) {

						hex.add(littleEndian(hexStr));						
					}

				}
				else if((number.contains("u"))) {

					number = number.replace("u", "");
					hexStr = binaryToHex((padLeft(intToBinary(Integer.parseInt(number)), 16)));
					hexStr = hexStr.replaceAll("..(?!$)", "$0 ");

					if(order.equals("big")) {		
						hex.add(hexStr);				
					}
					else if(order.equals("little")) {

						hex.add(littleEndian(hexStr));						
					}

				}
				else {
					int n = Integer.parseInt(number);
					if(n < 0){

						n *=-1;
						hexStr = binaryToHex(twosComplement((padLeft(intToBinary(n), 16))));
						hexStr = hexStr.replaceAll("..(?!$)", "$0 ");

						if(order.equals("big")) {

							hex.add(hexStr);
						}
						else if(order.equals("little")) {

							hex.add(littleEndian(hexStr));						
						}
					}
					else {

						hexStr = binaryToHex((padLeft(intToBinary(n), 16)));				
						hexStr = hexStr.replaceAll("..(?!$)", "$0 ");

						if(order.equals("big")) {

							hex.add(hexStr);
						}
						else if(order.equals("little")) {

							hex.add(littleEndian(hexStr));						
						}

					}
				}

			}
			input.close();
		} catch (FileNotFoundException e) {

			System.out.println(e.getMessage());
		}

		FileWriter writer = new FileWriter("output.txt"); 
		for(String str: hex) {
			writer.write(str + System.getProperty("line.separator"));
		}

		writer.close();
		reader.close();

	}

	static String intToBinary(int n) { 
		// Store binary number in an array
		int[] binaryNum = new int[32]; 

		int i = 0; 
		while (n > 0) { 
			// Store remainder
			binaryNum[i] = n % 2; 
			n = n / 2; 
			i++; 
		} 

		String binary = "";
		
		// Add the remainders in reverse order 
		for (int j = i - 1; j >= 0; j--) {

			binary = binary + binaryNum[j];			
		}

		return binary;
	}

	public static String padRight(String mantissa, int size) {
		// Add zeros to the end of the fraction

		int mantissaBit;

		if(size == 1 || size == 2) {

			mantissaBit = mantissaBit(size);
		}		
		else if(size == 3) {

			mantissaBit = 15;
		}

		else {

			mantissaBit = 21;
		}

		int i = mantissa.length();
		while(i < mantissaBit) {

			mantissa += "0";
			i++;
		}

		return mantissa;
	}

	public static String padLeft(String exp, int length) {
		// Add leading zeros to the exponent

		if (exp.length() >= length) {
			return exp;
		}
		StringBuilder sb = new StringBuilder();
		while (sb.length() < length - exp.length()) {
			sb.append('0');
		}
		sb.append(exp);

		return sb.toString();
	}

	static String twosComplement(String binary) 
	{ 
		int n = binary.length(); 
		int i; 

		String ones = ""; 
		String twos = ""; 

		// Flip every bit for ones complement 
		for (i = 0; i < n; i++) { 
			ones += flip(binary.charAt(i)); 
		} 

		// Take two's complement	
		twos = ones; 
		for (i = n - 1; i >= 0; i--) { 
			if (ones.charAt(i) == '1') { 
				twos = twos.substring(0, i) + '0' + twos.substring(i + 1); 
			}  
			else{ 
				twos = twos.substring(0, i) + '1' + twos.substring(i + 1); 
				break; 
			} 
		} 

		// If all the numbers are 1 add extra 1 at beginning 
		if (i == -1) { 
			twos = '1' + twos; 
		} 

		return twos;
	}

	static char flip(char c) { 
		return (c == '0') ? '1' : '0'; 
	}

	public static String binaryToHex(String binary) {
		// Convert binary number to hexadecimal number

		int digitNumber = 1;
		int sum = 0;
		String hex = "";
		for(int i = 0; i < binary.length(); i++){
			if(digitNumber == 1)
				sum+=Integer.parseInt(binary.charAt(i) + "")*8;
			else if(digitNumber == 2)
				sum+=Integer.parseInt(binary.charAt(i) + "")*4;
			else if(digitNumber == 3)
				sum+=Integer.parseInt(binary.charAt(i) + "")*2;
			else if(digitNumber == 4 || i < binary.length() + 1){
				sum+=Integer.parseInt(binary.charAt(i) + "")*1;
				digitNumber = 0;
				if(sum < 10)
					hex += sum;
				else if(sum == 10)
					hex += "A";
				else if(sum == 11)
					hex += "B";
				else if(sum == 12)
					hex += "C";
				else if(sum == 13)
					hex += "D";
				else if(sum == 14)
					hex += "E";
				else if(sum == 15)
					hex += "F";
				sum=0;
			}
			digitNumber++;
		}

		return hex;
	}

	public static String findMantissa(BigDecimal decimal, boolean exponent) {
		BigDecimal integer = decimal.setScale(0, RoundingMode.FLOOR);
		BigDecimal fractional = decimal.subtract(integer);	
		String mantissa;
		BigDecimal newInt = integer;
		StringBuilder sb = new StringBuilder();

		// integer part

		BigDecimal two = BigDecimal.valueOf(2);
		BigDecimal zero = BigDecimal.ZERO;

		while (integer.compareTo(zero) > 0) {
			BigDecimal[] result = integer.divideAndRemainder(two);
			sb.append(result[1]); // result[1] is remainder
			integer = result[0]; // result[0] is quotient
		}

		sb.reverse();

		// Fractional part
		int counter = 0;

		while (fractional.compareTo(zero) != 0) {
			counter++;
			fractional = fractional.multiply(two);
			sb.append(fractional.setScale(0, RoundingMode.FLOOR));
			if ( fractional.compareTo(BigDecimal.ONE) >= 0 ) {
				fractional = fractional.subtract(BigDecimal.ONE);
			}
			if ( counter >= 1000) {
				break;
			}
		}

		int oneIndex = 0;
		mantissa = sb.toString();

		if(newInt.compareTo(zero) > 0) {

			// if the integer part is greater than 0

			return sb.toString().substring(1);

		}

		else if(exponent) {

			// Return the exponent if the boolean value is true
			return String.valueOf(exponentLess(mantissa));

		}
		else {

			// if the integer part is less than 0

			// Find the first 1 in mantissa
			for (int i = 0; i < mantissa.length() ; i++) {

				if(mantissa.charAt(i) == '1') {

					oneIndex = i;
					break;
				}
			}

			return mantissa.substring(oneIndex);
		}		

	}

	public static int exponentGreater(BigDecimal decimal, int mantissaBit) {

		// Find the exponent of the number if the absolute value 
		// of the number is greater than 0

		BigDecimal integer = decimal.setScale(0, RoundingMode.FLOOR);

		StringBuilder sb = new StringBuilder();

		BigDecimal two = BigDecimal.valueOf(2);
		BigDecimal zero = BigDecimal.ZERO;

		while ( integer.compareTo(zero) > 0 ) {
			BigDecimal[] result = integer.divideAndRemainder(two);
			sb.append(result[1]); // stores remainder
			integer = result[0]; // stores quotient
		}

		sb.reverse();

		int exponent = sb.toString().length() - 1;

		return exponent;
	}

	public static int exponentLess(String mantissa) {

		// Find the exponent of the number if the absolute value 
		// of the number is between 1 and 0

		int exponent = 0;
		for (int i = 0; i < mantissa.length() ; i++) {

			if(mantissa.charAt(i) == '1') {

				exponent = i + 1;
				exponent *= -1;
				break;
			}
		}

		return exponent;
	}

	public static int mantissaBit(int size) {

		int mantissaBit;

		if (size == 1) {
			mantissaBit = 3;
		}
		else if (size == 2) {
			mantissaBit = 9;
		}
		else {
			mantissaBit = 13;
		}
		return mantissaBit;
	}

	public static int expSize(int size) {

		if(size == 1 || size == 2) 
			return size*8 -mantissaBit(size) - 1;

		else if(size == 3) 
			return 8;

		else
			return 10;
	}

	public static int findExp(int k, int exponent) {

		int exp;
		int bias = (int) Math.pow(2, k-1) - 1;
		exp = bias + exponent;
		return exp;
	}

	public static String round(String binary, int mantissaBit) {

		// Round the binary number (mantissa) using last digits 
		// of the mantissa and truncated mantissa
		
		String lastDigits;
		String truncatedMantissa;
		String mantissa = "";

		if(mantissaBit < binary.length()) {

			lastDigits = binary.substring(mantissaBit, binary.length());
			truncatedMantissa = binary.substring(0, mantissaBit);

			int i = lastDigits.length();

			while(i < 3) {

				lastDigits += "0";
				i++;
			}

			if(lastDigits.charAt(0)== '0') {

				mantissa = truncatedMantissa;
			}

			if(lastDigits.charAt(0)== '1' && lastDigits.charAt(1) == '0' && lastDigits.charAt(2) == '0' ) {

				if(truncatedMantissa.endsWith("0")){

					mantissa = truncatedMantissa;
				}
				else {

					binary = addOne(binary);
					mantissa =  binary.substring(0, mantissaBit);
				}

			}
			else if(lastDigits.charAt(0)== '1' && (lastDigits.charAt(1) != '0' || lastDigits.charAt(2) != '0')) {

				binary = addOne(binary);
				mantissa =  binary.substring(0, mantissaBit);
			}

			return mantissa;
		}

		else {

			return binary;
		}

	}

	public static String addOne(String binary) {

		// Add 1 to the binary number

		int n = binary.length();
		int i;
		for (i = n - 1; i >= 0; i--) {

			if(binary.charAt(i) == '1') {

				binary = binary.substring(0, i) + '0' + binary.substring(i + 1);
			}
			else {

				binary = binary.substring(0, i) + '1' + binary.substring(i + 1);
				break;
			}
		}
		// If all the numbers are 1 add extra 1 at beginning 
		if (i == -1) { 
			binary = '1' + binary; 
		} 

		return binary;
		
	}

	public static String littleEndian(String s) {

		String hex = "";
		List<String> list = new ArrayList<>();
		list.add(s);

		// Split the string by whitespace
		list = Arrays.asList(s.split("\\s+"));

		int l = list.size();
		int iterationNum;

		// Calculate the iteration number using the size of the list
		if(l % 2 == 0)
			iterationNum = l/2;
		else
			iterationNum = (l - 1)/2;

		// Swap the elements of the list
		for (int i = 0; i < iterationNum ; i++) {

			int end = list.size() - 1;
			String temp = list.get(i);
			list.set(i, list.get(end - i));
			list.set(end - i, temp);				
		}

		for (int i = 0; i < list.size(); i++) {

			hex += list.get(i);

		}

		// Insert space between every two character
		hex = hex.replaceAll("..(?!$)", "$0 ");

		return hex;	
	}

}

