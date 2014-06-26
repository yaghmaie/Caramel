package LexicalAnalyser;

/**
 * Created by pejman on 6/7/14.
 */
public interface FA {
    public State startState = null;

    public State getStartState();
    public void setStartState( State state );
    public void addTransition( State source, String input, State destination ) throws Exception;
}
