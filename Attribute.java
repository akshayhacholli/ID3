/**
 * This class is used to represent the attributes from the train file.
 * It contains all the information fo the attribute such as Name, Values it can take, position, training data values
 * @author Akshay
 */
package ID3;

import java.util.ArrayList;
import java.util.Arrays;

public class Attribute
{
	int index;
	String name;
	int value_count;
	int[] values;
	ArrayList< Integer > attribute_data;

	public Attribute ( int index, String name, int val_count )
	{
		this.index = index;
		this.name = name;
		this.value_count = val_count;
		this.values = new int[ val_count ];
		this.attribute_data = new ArrayList< Integer >();
		Arrays.fill( this.values, -1 );
	}

	protected int value( int index )
	{
		return this.values[ index ];
	}

	protected void add_data( int val )
	{
		this.attribute_data.add( getIndex( val ) );
	}

	private int getIndex( int val )
	{
		int i = 0;

		while ( this.values[ i ] != -1 )
		{
			if ( this.values[ i ] == val )
				return i;
			i++;
		}

		this.values[ i ] = val;
		return i;
	}
}
