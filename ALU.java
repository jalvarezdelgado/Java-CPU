
public class ALU {
	public static longword doOp(bit[] operation, longword a, longword b) {
		int operationKey = determineOp(operation);
		longword result = new longword();
		switch(operationKey) {
			case 0:
				System.out.println("Invalid operation input: " + opToString(operation));
				break;
				
			case 1:
				result = a.and(b);
				break;
				
			case 2:
				result = a.or(b);
				break;
			case 3:
				result = a.xor(b);
				break;
			case 4:
				result = a.not();
				break;
			case 5:
				int leftshiftNum = 0;
				for(int i = 0; i < 5; i++) {
					if(b.getBit(i).getValue() == 1) {
						leftshiftNum += Math.pow(2, i);
					}
				}
				result = a.leftShift(leftshiftNum);
				break;
			case 6:
				int rightshiftNum = 0;
				for(int i = 0; i < 5; i++) {
					if(b.getBit(i).getValue() == 1) {
						rightshiftNum += Math.pow(2, i);
					}
				}
				result = a.rightShift(rightshiftNum);
				break;
				
			case 7:
				result = rippleAdder.add(a, b);
				break;
				
			case 8:
				result = rippleAdder.subtract(a, b);
				break;
				
			case 9:
				System.out.println("Performing multiplication:");
				result = multiplier.multiply(a, b);
				break;
		}
		return result;
	}
	
	/**
	 * Series of checks to determine which operation to perform. 
	 * If bit[] operation doesn't meet the requirements, the default 0 is returned, indicating an invalid input
	 * @param operation 4-bit number representing the desired operation
	 * @return key to direct doOp on which operation to perform
	 */
	public static int determineOp(bit[] operation) {
		int operationKey = 0;
		if(operation[0].or(operation[1]).getValue() == 0) { //First two bits are both 0, "--00", either leftshift or AND
			if(operation[2].and(operation[3]).getValue() == 1) { //Last two bits are both 1, "1100", leftshift
				operationKey = 5;
			} else if(operation[2].getValue() == 0 && operation[3].getValue() == 1) { //Third bit is 0 and fourth bit is 1, "1000", AND operation
				operationKey = 1;
			}
		} else if(operation[0].and(operation[1]).getValue() == 1) { //First two bits are both 1, "--11", either NOT, subtraction, or multiplication
			if(operation[2].and(operation[3]).getValue() == 1) { //Last two bits are both 1, "1111", subtraction
				operationKey = 8;
			} else if(operation[2].xor(operation[3]).getValue() == 1) { //Making sure that the third and fourth bits are opposite values
				if(operation[1].and(operation[2]).getValue() == 1) { //Second and third bits are both 1, "0111", can only be multiplication
					operationKey = 9;
				} else { //Third bit is a 0, "1011", NOT operation
					operationKey = 4;
				}
			}
		} else { //Only remaining case, first and second bits will have opposite values, but which comes first is unknown
			if(operation[0].getValue() == 1) { //First bit is 1, "--01", will be either OR operation or rightshift
				if(operation[2].and(operation[3]).getValue() == 1) { //"1101", rightshift
					operationKey = 6;
				} else if(operation[1].or(operation[2]).getValue() == 0 && operation[3].getValue() == 1) { //Middle two bits are 0, last bit is 1, "1001", OR operation
					operationKey = 2;
				}
			} else { //First bit isn't 1, and since the first two bits are opposites it must be 0, "--10"
				if(operation[2].xor(operation[3]).getValue() == 1 && operation[3].getValue() == 1) { //"1010", XOR operation
					operationKey = 3;
				} else if(operation[2].and(operation[3]).getValue() == 1) { //Last two bits are both 1, "1110", addition
					operationKey = 7;
				}
			}
		}
		
		return operationKey;
	}
	
	/**
	 * Helper method to easily print 4-bit numbers representing operations
	 * @return String representation of a 4-bit number
	 */
	public static String opToString(bit[] operation) {
		String output = "";
		for(int i = 3; i >= 0; i--) {
			output += operation[i];
		}
		
		return output;
	}
}
