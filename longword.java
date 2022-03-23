
public class longword {
	public bit bitList[] = new bit[32]; //Returns as null if bits are interacted with.
										//Will either be set as a list of 0's or representative of a certain value
										//with the set() method based on constructor used
	
	public longword() {
		this.set(0);
	}
	
	public longword(int value) {
		this.set(value);
	}
	
	/**
	 * Getter method to retrieve a bit from a specified index within bitList[]
	 * @param index The index where the desired bit is located
	 * @return The bit at the index specified
	 */
	bit getBit(int index) {
		return this.bitList[index];
	}
	
	/**
	 * Setter method to set the value of a specified bit to the value of another bit
	 * @param index The index of the bit to be set (within bitList[])
	 * @param value The bit that the new value will be taken from
	 */
	void setBit(int index, bit value) {
		this.getBit(index).set(value.getValue());
	}
	
	/**
	 * Method to perform the AND operation between two longwords, returns a new longword holding the result
	 * @param other The second longword involved in the operation
	 * @return A new longword after the AND operation has been performed
	 */
	longword and(longword other) {
		longword resultAND = new longword();
		for(int i = 0; i < this.bitList.length; i++) {
			if(this.bitList[i].and(other.bitList[i]).getValue() == 1) {
				resultAND.getBit(i).set(1);
			} else {
				resultAND.getBit(i).set(0);
			}
		}
		return resultAND;
	}
	
	/**
	 * Method to perform the OR operation between two longwords, returns a new longword holding the result
	 * @param other The second longword involved in the operation
	 * @return A new longword after the OR operation has been performed
	 */
	longword or(longword other) {
		longword resultOR = new longword();
		
		for(int i = 0; i < this.bitList.length; i++) {
			if(this.getBit(i).or(other.getBit(i)).getValue() == 1) {
				resultOR.getBit(i).set(1);
			} else {
				resultOR.getBit(i).set(0);
			}
		}
		return resultOR;
	}
	
	/**
	 * Method to perform the XOR operation between two longwords, returns a new longword holding the result
	 * @param other The second longword involved in the operation
	 * @return A new longword after the XOR operation has been performed
	 */
	longword xor(longword other) {
		longword resultXOR = new longword();
		
		for(int i = 0; i < this.bitList.length; i++) {
			if(this.getBit(i).xor(other.getBit(i)).getValue() == 1) {
				resultXOR.getBit(i).set(1);
			} else {
				resultXOR.getBit(i).set(0);
			}
		}
		
		return resultXOR;
	}
	
	/**
	 * Not operation, will return a longword holding bits opposite to that of the first longword
	 * @return New longword after the NOT operation has been performed on the longword
	 */
	longword not() {
		longword resultNOT = new longword();
		
		for(int i = 0; i < this.bitList.length; i++) {
			resultNOT.setBit(i, this.getBit(i).not());
		}
		
		return resultNOT;
	}
	
	/**
	 * Method used to represent a longword as a string
	 * @return output The string representation of the longword
	 */
	@Override
	public String toString() {
		String output = "";
		
		for(int i = 31; i >= 0; i--) {
			output += this.getBit(i) + ",";
		}
		output = output.substring(0, output.length() - 1);
		return output;
	}
	
	/**
	 * A method used to shift the bits of a longword to the right by a specified number of positions
	 * @param amount The number of positions by which the longword will be shifted
	 * @return A similar longword with its bits shifted
	 */
	longword rightShift(int amount) {
		longword output = new longword();
		for(int i = 0; i + amount < 32; i++) {
			output.setBit(i, this.getBit(i + amount));
		}
		return output;
	}
	
	/**
	 * A method used to shift the bits of a longword to the left by a specified number of positions
	 * @param amount The number of positions by which the longword will be shifted
	 * @return A similar longword with its bits shifted
	 */
	longword leftShift(int amount) {
		longword output = new longword();
		for(int i = 0; i + amount < 32; i++) {
			output.setBit(i + amount, this.getBit(i));
		}
		return output;
	}
	
	/**
	 * As specified in the assignment document, the value of longword will be returned as a long
	 * @return Long representation of the longword
	 */
	long getUnsigned() {
		long result = 0;
		for(int i = 0; i < this.bitList.length; i++) {
			if(this.getBit(i).getValue() == 1) {
				result += Math.pow(2, i);
			}
		}
		return result;
	}
	
	/**
	 * The value of longword will be returned as an int
	 * @return int representation of the longword
	 */
	int getSigned() {
		int result = 0;
		for(int i = 0; i < this.bitList.length - 1; i++) {
			if(this.getBit(i).getValue() == 1) {
				result += Math.pow(2, i);
			}
		}
		if(this.getBit(this.bitList.length - 1).getValue() == 1) { //Checks the leftmost bit, which indicates sign
			return 0 - result;
		} else {
			return result;
		}
	}
	
	/**
	 * Copies the of a longword onto another longword
	 * @param other The longword being copied from
	 */
	void copy(longword other) {
		for(int i = 0; i < this.bitList.length; i++) {
			this.setBit(i, other.getBit(i));
		}
	}
	
	/**
	 * Method to set a longword from an int input (Note: The leftmost bit indicates whether the longword is positive or negative
	 * @param value The integer value desired as a longword
	 */
	void set(int value) {
		for(int i = 0; i < 32; i++) {
			this.bitList[i] = new bit(0);
		}
		
		if(value < 0) {
			this.getBit(31).set(1);
		}
		value = Math.abs(value); //Shouldn't affect positive values, but makes it easier to convert negative decimal values to binary
		int index = 0;
		while(value > 0) {
			this.getBit(index).set(value % 2);
			value = value/2;
			index++;
		}
	}
	
	boolean isNegative() {
		if(this.getBit(31).getValue() == 1) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Compare method used to check if longword a is greater than longword b
	 * @param a The first longword used for this comparison
	 * @param b The second longword used for this comparison
	 * @return Value depending on whether a is greater than, less than, or equal to b
	 */
	static int compareLongwords(longword a, longword b) {
		if(Math.abs(a.getSigned()) > Math.abs(b.getSigned())) {
			return 1;
		} else if(Math.abs(a.getSigned()) < Math.abs(b.getSigned())) {
			return -1;
		} else { //They are equal
			return 0;
		}
	}
}
