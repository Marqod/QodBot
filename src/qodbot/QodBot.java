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
import org.jibble.pircbot.*;
import java.util.*;

/**
 *
 * @author Marqod <eltharius@gmail.com>
 */
public class QodBot extends PircBot{
    
    //Fields
    
    Random rng = new Random();
    Roller roller = new Roller();
    
    //Constructors
    
    public QodBot() {
        this.setName("QodBot");
        
        //debug
        //System.out.println(">roll 10df".matches("^>roll (\\d)+d((\\d)+|f)([\\+\\-](\\d+))?"));
    }
    
    //Listeners.
    
    public void onMessage(String channel, String sender, String login, String hostname, String message) {

        //Some dice rolling.
        if (message.equalsIgnoreCase(">roll") || message.equalsIgnoreCase(">HELP")) {
            //Default explanation.
            sendMessage(channel, "To roll dice, type:");
            sendMessage(channel, ">roll NdM, for example: >roll 1d20");
            sendMessage(channel, "You can also add modifiers or different die types.");
            int roll = (rng.nextInt(19)+1);
            System.out.println("d20 roll: " + roll);
            sendMessage(channel, sender + "'s 1d20 roll: " + roll);
        } else if ((message).matches("^>roll ((\\d)+d((\\d)+|f)|advantage|disadvantage)([\\+\\-](\\d)+d((\\d)+|f)){0,8}([\\+\\-](\\d+))?( .*)?")){
            //This is a proper dice roll.
            String alteredMessage = message.replace(">roll ","");
            alteredMessage = alteredMessage.toLowerCase();
            //Roll the dice.
            try{
                String[] rollResult = roller.parseRollRequest(alteredMessage, sender);
                sendMessage(channel, rollResult[0]);
                //Check for crits
                if (rollResult[1]!=null){
                    //TODO
                    sendMessage(channel, (sender + " rolled a natural 20!"));
                }
                if (rollResult[2]!=null) {
                    //TODO
                    sendMessage(channel, (sender + " rolled a natural 1!"));
                }
            } catch (ExcessiveNumberOfDiceException ex) {
                sendMessage(channel, (sender + ", you can't roll more than 100 dice at once."));
            }
        } else {
            if (message.startsWith(">roll")) {
                //print usage.
                sendMessage(channel, sender + ", invalid roll.");
            }
        }
        
            
    }
}
