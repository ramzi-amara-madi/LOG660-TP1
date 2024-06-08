package Algorithme;

import java.util.HashSet;
import java.util.Set;

public class UniqueCVVGenerator {
    private Set<String> generatedCombinations = new HashSet<>();
    
    public String generateCVV() {
        int firstDigit = (int) (Math.random() * 10);
        int secondDigit = (int) (Math.random() * 10);
        int thirdDigit = (int) (Math.random() * 10);
        return "" + firstDigit + secondDigit + thirdDigit;
    }
}
