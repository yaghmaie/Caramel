package LexicalAnalyser;

import java.util.HashSet;

/**
 * Created by pejman on 5/23/14.
 */
public class State {
    private boolean isFinal = false;
    private String name;
    FA fa;
    final HashSet<String> transitionSet = new HashSet<String>();

    public State( FA fa ) {
        this.fa = fa;
        this.name = String.valueOf( this.hashCode() );
    }

    public State( FA fa, String name ) {
        this.fa = fa;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName( String name ) {
        this.name = name;
    }

    public String getTransitions() {
        return transitionSet.toString();
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal( boolean isFinal ) {
        this.isFinal = isFinal;
    }
}
