package Fundamentals;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by pejman on 5/23/14.
 */
public class BigTable<e,f,g> implements Serializable {

    protected HashMap< e, HashMap< f, g >> theTable = new HashMap<e, HashMap<f, g>>();

    protected void addPrimaryKey( e primaryKey ) {
        theTable.put( primaryKey, new HashMap<f, g>(  ) );
    }

    protected void addSecondaryKey( e primaryKey, f secondaryKey, g value ) {
        if( ! theTable.containsKey( primaryKey ) ) {
            addPrimaryKey( primaryKey );
            theTable.get( primaryKey ).put( secondaryKey, value );
        }
        theTable.get( primaryKey ).put( secondaryKey, value );
    }

}
