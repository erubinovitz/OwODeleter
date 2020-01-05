package com.minhaskamal.alphabetRecognizer.MainPackage;

import java.util.HashMap;

public class Searcher {

    private int NO_OF_CHARS = 4;
    HashMap<String, Integer> forbiddenWords;
    HashMap<Character, Integer> forbiddenChars;
    //A utility function to get maximum of two integers
    private static int max (int a, int b) { return Math.max(a,b); }
    private static int size = 3;

    public Searcher(HashMap<String,Integer> forbiddenWords, HashMap<Character, Integer> forbiddenChars){
        this.forbiddenChars=forbiddenChars;
        this.forbiddenWords=forbiddenWords;
    }

    //The preprocessing function for Boyer Moore's
    //bad character heuristic
    public void badCharHeuristic( char [] message, int badchar[])
    {


        // Initialize all occurrences as -1
        for (int i = 0; i < NO_OF_CHARS; i++)
            badchar[i] = -1;

        // Fill the actual value of last occurrence
        // of a character
        for (int i = 0; i < message.length; i++) {
            Integer valStr = forbiddenWords.get(Character.toString(message[i]));
            Integer valChar = forbiddenChars.get(message[i]);
            if (valChar == null && valStr == null){
                badchar[0] = i;
            }
            else if (valChar == null){
                badchar[valStr]=i;
            }
            else{
                badchar[valChar]=i;
            }
        }
    }

    /* A pattern searching function that uses Bad
    Character Heuristic of Boyer Moore Algorithm */
    public boolean search(char message[] )
    {
        System.out.println(forbiddenChars.toString());
        System.out.println("\n\n"+forbiddenWords.toString());
        int n = message.length;
        int m = 3;
        int badchar[] = new int[NO_OF_CHARS];

      /* Fill the bad character array by calling
         the preprocessing function badCharHeuristic()
         for given pattern */
        badCharHeuristic(message,badchar);

        int s = 0;  // s is shift of the pattern with
        // respect to text
        while(s <= (n - m))
        {
            int j = m-1;

          /* Keep reducing index j of pattern while
             characters of pattern and text are
             matching at this shift s */

            int mapVal=0;
            while(j >= 0 && (forbiddenWords.get(Character.toString(message[s+j])) != null && j % 2 == forbiddenWords.get(Character.toString(message[s+j]))||
                    (forbiddenChars.get(message[s+j] )!=null && j % 2 == forbiddenChars.get(message[s+j])))) {
                System.out.println("forbiddenwors.get("+message[s+j]+") = "+forbiddenWords.get(Character.toString(message[s+j])));
                System.out.println("forbiddcharss.get("+message[s+j]+") = "+forbiddenChars.get(message[s+j]));
                System.out.println("j is " +j + "s is " +s);
                j--;
            }
            if (s+j>=0) {
                System.out.println("forbiddenwors.get(" + message[s + j] + ") = " + forbiddenWords.get(Character.toString(message[s + j])));
                System.out.println("forbiddcharss.get(" + message[s + j] + ") = " + forbiddenChars.get(message[s + j]));
            }
            System.out.println("j is " +j + "s is " +s);
            System.out.println("newline");
          /* If the pattern is present at current
             shift, then index j will become -1 after
             the above loop */
            if (j < 0)
            {
                return true;

            }

            else {
              /* Shift the pattern so that the bad character
                 in text aligns with the last occurrence of
                 it in pattern. The max function is used to
                 make sure that we get a positive shift.
                 We may get a negative shift if the last
                 occurrence  of bad character in pattern
                 is on the right side of the current
                 character. */
                Integer valStr = forbiddenWords.get(Character.toString(message[s+j]));
                Integer valChar = forbiddenChars.get(message[s+j]);
                int val=0;
                if (valChar!=null)val=valChar;
                if (valStr!=null)val=valStr;
                s += max(1, j - badchar[val]);
            }
        }
        return false;
    }
}
