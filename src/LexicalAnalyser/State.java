/*
 * Simple solutions for creating lexical analyzer, syntax analyzer, code generator.
 *     Copyright (C) 2014  Pejman Yaghmaie.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see http://www.gnu.org/licenses/.
 */

package LexicalAnalyser;

import java.util.HashSet;

/**
 * Created by pejman on 5/23/14.
 */
public class State {
    /**
     * Set of strings this state can have transition with
     */
    final HashSet<String> transitionSet = new HashSet<String>();
    /**
     * Keeps FA this state is belongs to
     */
    FA fa;
    /**
     * Shows if state is final
     */
    private boolean isFinal = false;
    /**
     * Shows name of state
     */
    private String name;

    /**
     * Constructor sets FA for state
     * @param fa Finite automaton which owns this state.
     */
    public State( FA fa ) {
        this.fa = fa;
        this.name = String.valueOf( this.hashCode() );
    }

    /**
     * Constructor sets FA and name for state
     * @param fa Finite automaton which owns this state.
     * @param name Name of state
     */
    public State( FA fa, String name ) {
        this.fa = fa;
        this.name = name;
    }

    /**
     * Returns name of state.
     * @return Name of state.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name of state
     * @param name Name of state.
     */
    public void setName( String name ) {
        this.name = name;
    }

    /**
     * Returns a string of inputs this state can have transition.
     * @return String of all transition inputs.
     */
    public String getTransitions() {
        return transitionSet.toString();
    }

    /**
     * Checks if state is final state.
     * @return state finality.
     */
    public boolean isFinal() {
        return isFinal;
    }

    /**
     * Sets state finality.
     * @param isFinal State finality.
     */
    public void setFinal( boolean isFinal ) {
        this.isFinal = isFinal;
    }
}
