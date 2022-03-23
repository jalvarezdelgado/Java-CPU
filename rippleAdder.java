public class rippleAdder {
	
	/**
	 * Addition method to find the sum of two longwords
	 * @param a The first longword being added
	 * @param b The second longword being added
	 * @return The sum of the two longwords
	 */
	public static longword add(longword a, longword b) {
		if(a.isNegative() && b.isNegative()) {
			longword result = complement(addCircuit(complement(a), complement(b))); //add the complements of a and b, then get the complement of the sum
			result.setBit(31, new bit(1)); //Since they're both negative, have to make the sum negative
			return result;
		} else if(a.getBit(31).xor(b.getBit(31)).getValue() == 1) { //Means only one of these two is negative
			if(a.isNegative()) {
				longword result = addCircuit(complement(a), b);
				if(longword.compareLongwords(a, b) == 1) { //a is negative and greater than b
					result = complement(result);
					if(!getCarry(a, b)) {
						result.setBit(31, new bit(1));
					}
				}
				return result;
			} else { // b is the negative longword
				longword result = addCircuit(a, complement(b));
				if(longword.compareLongwords(a, b) == -1) { //b is negative and greater than a
					result = complement(result);
					if(!getCarry(a, b)) {
						result.setBit(31, new bit(1));
					}
				}
				return result;
			}
		} else { //They're both positive
			return addCircuit(a, b);
		}
	}
	
	/**
	 * Subtraction method to find the difference between two numbers
	 * @param a The first longword, the minuend
	 * @param b The second longword, the subtrahend
	 * @return The difference between these longwords
	 */
	public static longword subtract(longword a, longword b) {
		longword result = new longword();
		if(a.isNegative() && !b.isNegative()) { //-a - b == -(a + b)
			result = add(a, complement(b));
			result = complement(result);
			result.setBit(31, new bit(1));
			return result;
		} else if(!a.isNegative() && b.isNegative()) { //a - (-b) == a + b
			b.setBit(31, new bit(0));
			result = add(a, b);
			return result;
		} else if(a.isNegative() && b.isNegative()) { //-a - (-b) == -a + b == b - a
			b.setBit(31, new bit(0));
			result = add(a, b);
			return result;
		} else { //Standard a - b
			result = add(a, complement(b));
			if(getCarry(a, complement(b))) {
				return result;
			} else {
				result = complement(result);
				result.setBit(31, new bit(1));
				return result;
			}
		}
	}
	
	/**
	 * Logical circuit to add two binary numbers, used in addition (and by extension, subtraction)
	 * @param a The first longword to be added
	 * @param b The second longword to be added
	 * @return The sum of the two longwords
	 */
	private static longword addCircuit(longword a, longword b) {
		longword sum = new longword();
		bit carry = new bit();
		
		for(int i = 0; i < 31; i++) {
			bit A = new bit(0);
			bit B = new bit(0);
			A.set(a.getBit(i).getValue());
			B.set(b.getBit(i).getValue());
			
			bit firstXOR = A.xor(B);
			bit firstAND = firstXOR.and(carry);
			bit secondXOR = firstXOR.xor(carry);
			bit secondAND = A.and(B);
			
			sum.setBit(i, secondXOR);
			carry = firstAND.or(secondAND);
		}
		return sum;
	}
	
	/**
	 * Method to determine whether an operation will have a carry left at the end
	 * @param a The first longword used in the operation
	 * @param b The second longword used in the operation
	 * @return A boolean value determining whether or not the carry equals 1
	 */
	private static boolean getCarry(longword a ,longword b) {
		bit carry = new bit();
		
		for(int i = 0; i < 31; i++) {
			bit A = new bit(0);
			bit B = new bit(0);
			A.set(a.getBit(i).getValue());
			B.set(b.getBit(i).getValue());
			
			bit firstXOR = A.xor(B);
			bit firstAND = firstXOR.and(carry);
			bit secondAND = A.and(B);
			carry = firstAND.or(secondAND);
		}
		if(carry.getValue() == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Helper method to get the complement of a longword
	 * @param a Longword that we need the complement of
	 * @return The desired complement
	 */
	protected static longword complement(longword a) {
		a = a.not();
		a = addCircuit(a, new longword(1));
		return a;
	}
}