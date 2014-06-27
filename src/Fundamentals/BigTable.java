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
