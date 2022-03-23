
public class bit {
	private int value = 0;

	public bit() { //Default constructor, default value of 0
		this.value = 0;
	}
	
	protected bit(int input) { //Constructor to set to a specific value, useful when performing operations
		switch(input) {
		case 0:
			this.value = input;
			break;
		case 1:
			this.value = input;
			break;
		default:
			System.out.println(input + " is not a binary value. Please use 1 or 0");
		}
	}
	
	/**
	 * A method used to set a bit to a specific value (though it will only set if the input is 1 or 0)
	 * @param input The desired value of the bit
	 */
	void set(int input) {
		switch(input) { //Switch statement to filter out values that aren't 1 or 0
			case 0:
				this.value = 0;
				break;
			case 1:
				this.value = 1;
				break;
			default:
				System.out.println(input + " is not a binary value. Please use 1 or 0");	
		}
	}
	
	/**
	 * Switches the value of a bit from 1 to 0 and vice-versa
	 */
	void toggle() {
		if(this.value == 0) {
			this.set(1);
		} else {
			this.set(0);
		}
	}
	
	/**
	 * Sets a bit by setting it to 1
	 */
	void set() {
		this.value = 1;
	}
	
	/**
	 * Clears a bit by setting it to 0
	 */
	void clear() {
		this.value = 0;
	}
	
	/**
	 * Getter method to retrieve the value of a bit
	 * @return The value of a bit
	 */
	int getValue() {
		return this.value;
	}
	
	/**
	 * Performs the AND operation on two bits, returns whether the bits hold 1
	 * @param other The second bit used in the operation
	 * @return A bit holding the result of the operation
	 */
	bit and(bit other) {
		if(this.value == 1) {
			if(other.value == 1) {
				bit trueBit = new bit(1); //trueBit will be used to name generated bits with a value of 1, or boolean "true"
				return trueBit;
			}
		}
		return new bit(0);
	}
	
	/**
	 * Performs the OR operation on two bits, returns whether either of the bits holds 1
	 * @param other The second bit used in the operation
	 * @return A bit holding the result of the operation
	 */
	bit or(bit other) {
		if(this.value == 1) {
			bit trueBit = new bit(1);
			return trueBit;
		} else if (other.value == 1) {
			bit trueBit = new bit(1);
			return trueBit;
		} else {
			bit falseBit = new bit(0);
			return falseBit;
		}
	}
	
	/**
	 * Method to compare two bits and return 1 if they are different, otherwise 0
	 * @param other The second bit used in the comparison
	 * @return A bit set to the 1 or 0 depending on the values of the first two bits
	 */
	bit xor(bit other) {
		if(this.value == other.value) { //Instead of checking each case, check if the values are the same, return 0
			bit falseBit = new bit(0);
			return falseBit;
		} else {
			bit trueBit = new bit(1);
			return trueBit;
		}
	}
	/**
	 * Performs NOT operation on a bit, returns bit set to the opposite value
	 * @return Bit set to the opposite value of the first bit
	 */
	bit not() {
		bit oppoBit = new bit(); //"Opposite Bit" pun
		if(this.value == 1) {
			oppoBit.set(0);
		} else if(this.value == 0){
			oppoBit.set(1);
		}
		
		return oppoBit;
	}
	
	
	/**
	 * A getter method used to bring the bit value as a string, useful for displaying to the console
	 */
	@Override
	public String toString() {
		return String.valueOf(this.value);
		
	}
}
