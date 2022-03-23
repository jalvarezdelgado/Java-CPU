
public class memory {
	bit[][] storage = new bit[1024][8]; //storage made of 1024 bytes == 8192 bits
	
	public memory() {
		for(int i = 0; i < 1024; i++) {
			for(int j = 0; j < 8; j++) {
				storage[i][j] = new bit(0);
			}
		}
	}
	
	/**
	 * A getter method to retrieve the bits of a byte
	 * @param address The index of the byte to be retrieved
	 * @return Longword representing the value of the byte in question
	 */
	public longword read(longword address) {
		int index = 0;
		if(address.getSigned() > 1023) {
			System.out.println("\nAddress received is too high, reading from the first 10 bits and reading from the appropriate address.");
		}
		for(int i = 0; i < 10; i++) { //Converts longword to an index (maximum of 1023 to prevent errors)
			if(address.getBit(i).getValue() == 1) {
				index += Math.pow(2, i);
			}
		}
		
		longword output = new longword();
		for(int j = 0; j < 8; j++) {
			output.setBit(j, this.storage[index][j]);
		}
		
		return output;
	}
	
	/**
	 * Setter method to assign the bits of the byte in a memory instance
	 * @param address The index of the byte to be overwritten
	 * @param value The desired value to write onto the byte
	 */
	public void write(longword address, longword value) {
		if(address.getSigned() > 1023) {
			System.out.println("\nAddress received is too high, reading from the first 10 bits and writing to the appropriate address.");
		}
		if(value.getSigned() > 255) {
			System.out.println("\nValue received is too high, reading from the first 8 bits and writing the appropriate value");
		}
		
		int index = 0;
		for(int i = 0; i < 10; i++) {
			if(address.getBit(i).getValue() == 1) {
				index += Math.pow(2, i);
			}
		}
		
		for(int j = 0; j < 8; j++) {
			this.storage[index][j] = value.getBit(j);
		}
	}
	
	/**
	 * Helper method to display bytes as 8-bit structures rather than as longwords
	 * @param index The index of the desired byte
	 * @return The byte as a string, surrounded by square brackets
	 */
	public String byteToString(int index) {
		int workingIndex = 0;
		if(index > 1023) { //Handles arguments larger than the number of bytes there are
			longword holder = new longword(index);
			for(int j = 0; j < 10; j++) {
				if(holder.getBit(j).getValue() == 1) {
					workingIndex += Math.pow(2, j);
				}
			}
		} else {
			workingIndex = index;
		}
		String output = "[";
		for(int i = 7; i >= 0; i--) {
			output += storage[workingIndex][i];
		}
		output += "]";
		return output;
	}
	
	/**
	 * Display method to show the bytes of a memory in a grid
	 */
	@Override
	public String toString() {
		String output = "\n";
		for(int i = 0; i < 1024; i++) {
			output += this.byteToString(i) + " ";
			if((i + 1) % 8 == 0) {
				output += "Addresses: " + (i - 7) + " - " + i + "\n";
			}
		}
		
		return output;
	}
}
