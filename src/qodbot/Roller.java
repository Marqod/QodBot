/*
 * Copyright (C) 2015 Marqod <eltharius@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package qodbot;
import java.util.*;
import java.util.regex.*;

/**
 *
 * @author Marqod <eltharius@gmail.com>
 */
public class Roller {
    Random rng = new Random();
    
    //Constructors
    Roller() {
        
    }
    
    //Methods
    
    String[] parseRollRequest(String request, String username) throws ExcessiveNumberOfDiceException {
        //Core method of the class.
        String responseString = username + "'s ";
        int responseValue = 0;
        boolean crit = false;
        boolean fail = false;
        
        //Split request into separate rolls
        Pattern pattern = Pattern.compile("[+-]");
        String[] expressions = pattern.split(request);
        Matcher matcher = pattern.matcher(request);
        //Distinguish between + and -.
        int[] multipliers = new int[expressions.length];
        
        //DEBUG
        //System.out.println("expressions.length: " + expressions.length);
        //System.out.println("multipliers.length: " + multipliers.length);
        
        //First roll is always positive.
        multipliers[0] = 1;
        int i=1;
        while (i<multipliers.length) {
            if (matcher.find()) {
                //DEBUG
                //System.out.println("Character found: " + request.charAt(matcher.start()));
                
                if (request.charAt(matcher.start()) == '+') {
                    multipliers[i] = 1;
                    //DEBUG
                    //System.out.println("Found a +.");
                } else {
                    multipliers[i] = -1;
                    //DEBUG
                    //System.out.println("Found a -.");
                }
                i++;
            }
        }
        
        //DEBUG
        //System.out.println("Expressions found: " + Arrays.toString(expressions));
        //System.out.println("Multipliers found: " + Arrays.toString(multipliers));
        
        //Roll cases:
        //NdM, Ndf, advantage, disadvantage.
        int j=0;
        while (j<expressions.length) {
            //For each roll, call rollDice()
            //and add result to response value and string.
            if (expressions[j].equals("advantage")) {
                //Advantage roll
                int[] results = rollDice(2, 20);
                responseString+=("Advantage rolls: " + Arrays.toString(results) + " ");
                Arrays.sort(results);
                responseValue+=results[1];
                //Check for crits
                if (results[1] ==20)
                    crit=true;
                else if (results[1] ==1) {
                    fail=true;
                }
                
            } else if (expressions[j].equals("disadvantage")) {
                //disadvantage roll
                int[] results = rollDice(2, 20);
                responseString+=("Disadvantage rolls: " + Arrays.toString(results) + " ");
                Arrays.sort(results);
                responseValue+=results[0];
                
                //Check for crits
                if (results[0] ==20)
                    crit=true;
                else if (results[0] ==1) {
                    fail=true;
                }
                
            } else if (expressions[j].contains("f")) {
                //Fudge dice roll
                String[] split = expressions[j].split("df");
                if(Integer.parseInt(split[0])>100) {
                    throw new ExcessiveNumberOfDiceException();
                }
                int[] results = rollDice(Integer.parseInt(split[0]), 3);
                for (int h=0; h<results.length; h++) {
                    results[h]-=2;
                }
                responseString+=("fudge rolls: " + Arrays.toString(results) + " ");
                for (int value: results) {
                    responseValue+=value*multipliers[j];
                }    
                
            } else if (expressions[j].contains("d")) {
                //Normal dice roll
                String[] split = expressions[j].split("d");
                if(Integer.parseInt(split[0])>100) {
                    throw new ExcessiveNumberOfDiceException();
                }
                int[] results = rollDice(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
                responseString+=("d" + Integer.parseInt(split[1]) + " rolls: " + Arrays.toString(results) + " ");
                for (int value: results) {
                    responseValue+=value*multipliers[j];
                }             
                
                //Check for nat 1/20 in case of d20
                //Set value if so.
                if (Integer.parseInt(split[1]) == 20) {
                    //We're rolling d20s.
                    for (int value: results){
                        if (value ==20)
                            crit=true;
                        else if (value ==1) {
                            fail=true;
                        }
                    }
                }
                
            } else {
                //Final modifier
                int modifier = Integer.parseInt(expressions[j]);
                if (multipliers[j]<0) {
                    responseString+=("-" + modifier + " ");
                } else {
                    responseString+=("+" + modifier + " ");
                }
                responseValue+=modifier*multipliers[j];
            }
            j++;
        }
        
        //Add sum to response string.
        responseString += ("Final result: " + responseValue);
        
        //Return statement.
        String[] finalResponse = new String[3];
        
        if (responseString.equalsIgnoreCase("")) {
            finalResponse[0] = "An error occurred during processing.";
        }
        else {
            finalResponse[0] = responseString;
        }
        
        //Add crit warning if present.
        if (crit){
            finalResponse[1] = "true";
        } else {
            finalResponse[1] = null;
        }
        if (fail) {
            finalResponse[2] = "true";
        } else {
            finalResponse[2] = null;
        }
        
        return finalResponse;
    }   
    
    int[] rollDice(int amount, int dieSize){
        int[] results = new int[amount];
        for (int i=0; i<amount; i++) {
            int value = rng.nextInt(dieSize)+1;
            results[i] = value;
        }
        return results;
    }
    
    
}
