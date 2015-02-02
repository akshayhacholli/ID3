/*************************************************************************
 * @author Akshay
 * A class used to store train data from such that it becomes easy for building decision tree.
 * 
 * Dependencies : Attribute.java
 **/

package ID3;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class TrainData
{
	ArrayList< Attribute > attribute_list;
	Attribute class_data;

	public TrainData ( )
	{
		attribute_list = new ArrayList< Attribute >();
		class_data = new Attribute( -1, "", 2 );
	}

	public TrainData ( String fileName ) throws FileNotFoundException
	{
		this();
		readTrainFile( fileName );
	}

	/**
	 * Adds attribute information such as name, number of values it can take and position of the
	 * attribute within the file.
	 * 
	 * @param index - column position of attribute in the file
	 * @param name - name of the attribute
	 * @param val_count - number of values it can take
	 */
	protected void addAttribute( int index, String name, int val_count )
	{
		this.attribute_list.add( new Attribute( index, name, val_count ) );
	}

	/**
	 * Adds training data column wise in their respective attribute.
	 * 
	 * @param i - column position of attribute within the file
	 * @param val - training data
	 */
	protected void addTrainData( int i, int val )
	{
		this.attribute_list.get( i ).add_data( val );
	}

	/**
	 * Adds training data column wise in the class attribute.
	 * 
	 * @param val - training data
	 */
	protected void addClassTrainData( int val )
	{
		this.class_data.add_data( val );
	}

	/**
	 * Reads the file passed as a parameter and then writes the content into the custom data
	 * structure
	 * 
	 * @param fileName - Train data file name (Either absolute path or relative)
	 * @throws FileNotFoundException
	 */
	private void readTrainFile( String fileName ) throws FileNotFoundException
	{
		try
		{
			File train_file = new File( fileName );
			Scanner in;
			in = new Scanner( train_file );

			String[] tokens = in.nextLine().trim().split( "\\s+" ); // Parse the file

			int i = 0;
			int j = 0;
			while ( i < tokens.length )
				this.addAttribute( j++, tokens[ i++ ], Integer.parseInt( tokens[ i++ ] ) );


			while ( in.hasNextLine() )
			{
				tokens = in.nextLine().trim().split( "\\s+" );

				for ( j = 0; j < tokens.length - 1; j++ )
					this.addTrainData( j, Integer.parseInt( tokens[ j ] ) );

				// Last	column would be the class attribute
				this.addClassTrainData( Integer.parseInt( tokens[ tokens.length - 1 ] ) ); 
			}

			in.close();
		}
		catch ( FileNotFoundException e )
		{
			System.out.println( "Cannot find train file - " + fileName );
			throw e;
		}
	}

	/**
	 * Builds Decision tree and returns it.
	 * 
	 * @return - Decision Tree
	 */
	protected TreeData buildTree()
	{
		ArrayList< Integer > dataList = new ArrayList< Integer >( class_data.attribute_data.size() );

		for ( int i = 0; i < class_data.attribute_data.size(); i++ )
		{
			dataList.add( i );
		}

		return treeBuilder( new Entropy( class_data ), 0, new ArrayList< Integer >(), dataList );
	}

	/**
	 * This method is called recursively until the decision tree is gets all the leaf node.
	 * 
	 * @param entropy - Entropy of the parent node
	 * @param level - Current level of the node
	 * @param indexList - Contains attributes position which is already part of the branch and doesn't need to be re-looked
	 * @param trainDataList - Information regarding which rows to be looked for
	 * @return - Partially built decision tree
	 */
	private TreeData treeBuilder( Entropy entropy, int level, ArrayList< Integer > indexList, ArrayList< Integer > trainDataList )
	{
		Entropy[] ent = null;;
		Entropy[] ent_temp;
		double inform_gain = 0;
		double temp;
		Attribute attrObj;
		Attribute attr = null;
		int index = 0;

		for ( int i = 0; i < attribute_list.size(); i++ )
		{
			// Don't look for attributes which are already part of branch
			if ( !indexList.contains( i ) )
			{
				attrObj = attribute_list.get( i );
				ent_temp = calculateEntropies( attrObj, trainDataList );
				temp = calculateIG( ent_temp, entropy );

				// FInd the attribute with maximum information gain
				if ( temp >= inform_gain )
				{
					inform_gain = temp;
					ent = ent_temp;
					index = i;
					attr = attrObj;
				}
			}
		}

		TreeData treeNode = new TreeData( attr, level );

		for ( Entropy entObj : ent )
		{
			// Terminating condition
			if ( entObj.value == 0.0 )
			{
				treeNode.childList.put( attr.value( entObj.attr_val ), new TreeData( (entObj.count_of_ones == 0.0) ? 0 : 1 ) );
			}
			else if ( indexList.size() == this.attribute_list.size() - 1 )
			{
				treeNode.childList.put( attr.value( entObj.attr_val ), new TreeData( (entObj.count_of_ones > entObj.count_of_zeros) ? 1 : 0 ) );
			}
			else
			{
				indexList.add( index );
				TreeData tmpNode = treeBuilder( entObj, level + 1, indexList, entObj.trainDataList );
				treeNode.childList.put( attr.value( entObj.attr_val ), tmpNode );
				indexList.remove( ( Integer ) index );
			}
		}
		return treeNode;
	}

	/**
	 * This method calculates the entropy of the given attribute at a given level
	 * 
	 * @param attr - Attribute for which entropy needs to be calculated
	 * @param trainDataList - Information regarding which rows to be looked for
	 * @return - Array of entropies of an attribute for each value it can take
	 */
	private Entropy[] calculateEntropies( Attribute attr, ArrayList< Integer > trainDataList )
	{
		Entropy[] ent = new Entropy[ attr.value_count ];
		double count_of_ones;
		double count_of_zeros;
		ArrayList< Integer > entropyTrainDataList;

		for ( int i = 0; i < attr.value_count; i++ )
		{
			count_of_ones = 0;
			count_of_zeros = 0;
			entropyTrainDataList = new ArrayList< Integer >( trainDataList );

			for ( int j = 0; j < attr.attribute_data.size(); j++ )
			{
				if ( entropyTrainDataList.contains( j ) )
				{
					int val = attr.attribute_data.get( j );
					if ( val == i )
					{
						int temp = this.class_data.attribute_data.get( j );
						if ( this.class_data.value( temp ) == 0 )
							count_of_zeros++;
						else
							count_of_ones++;
					}
					else
						entropyTrainDataList.remove( ( Integer ) j );
				}
			}
			ent[ i ] = new Entropy( count_of_zeros, count_of_ones, i, entropyTrainDataList );
		}

		return ent;
	}
	
	/**
	 * This method calculates the Information Gain
	 * @param ent - Entropy of parent node
	 * @param entropy - Array of entropies
	 * @return - Information gain
	 */

	private double calculateIG( Entropy[] ent, Entropy entropy )
	{
		double ig = entropy.value;
		double total = entropy.count_of_ones + entropy.count_of_zeros;
		double count_of_ones = 0;
		double count_of_zeros = 0;

		for ( int i = 0; i < ent.length; i++ )
		{
			count_of_ones = ent[ i ].count_of_ones;
			count_of_zeros = ent[ i ].count_of_zeros;
			ig -= (((count_of_ones + count_of_zeros) / total) * ent[ i ].value);
		}

		return ig;
	}
	
	/**
	 * Outputs the tree on System.out
	 * Example - 
	 * | | attr2 = 0 : 
	 * | | | attr3 = 0 : 0
	 * | | | attr3 = 1 : 1
	 * | | attr2 = 1 : 
	 * | | | attr3 = 0 : 1
	 * | | | attr3 = 1 : 0
	 * 
	 * @param treeNode
	 */

	protected void printTree( TreeData treeNode )
	{
		for ( Map.Entry< Integer, TreeData > entry : treeNode.childList.entrySet() )
		{
			for ( int i = 0; i < treeNode.level; i++ )
				System.out.print( "| " );
			System.out.print( treeNode.attribute.name + " = " + entry.getKey() );

			if ( entry.getValue().attribute == null )
				System.out.println( " : " + entry.getValue().class_value );
			else
			{
				System.out.println( " : " );
				printTree( entry.getValue() );
			}
		}
	}
	
	/**
	 * Calculates accuracy of training file using expected class output with decision tree output.
	 * 
	 * @param tree
	 */

	protected void calculateAccuracyOfTrainFile( TreeData tree )
	{
		TreeData tempTree = null;
		Attribute attr = null;
		boolean found = false;
		int val, class_val = -1;
		int matched_count = 0;

		for ( int i = 0; i < this.class_data.attribute_data.size(); i++ )
		{
			tempTree = tree;

			while ( tempTree.attribute != null )
			{
				attr = this.attribute_list.get( tempTree.attribute.index );
				val = attr.attribute_data.get( i );
				val = attr.value( val );

				for ( Map.Entry< Integer, TreeData > entry : tempTree.childList.entrySet() )
				{
					if ( val == entry.getKey() )
					{
						if ( entry.getValue().attribute != null )
						{
							tempTree = entry.getValue();
							break;
						}
						else
						{
							found = true;
							class_val = entry.getValue().class_value;
							break;
						}
					}
				}

				if ( found )
				{
					found = false;
					break;
				}
			}

			val = this.class_data.attribute_data.get( i );
			val = this.class_data.value( val );
			if ( class_val == val )
				matched_count++;
		}

		DecimalFormat df = new DecimalFormat( "#.##" );
		System.out.println( "Accuracy of trained data ( " + this.class_data.attribute_data.size() + " instances ) = " + df.format( matched_count * 100.00 / this.class_data.attribute_data.size() ) );
	}

	/**
	 * Calculates accuracy of test file using expected class output with decision tree output.
	 * 
	 * @param tree
	 */
	protected void calculateAccuracyOfTestFile( TreeData tree, String test_file_name ) throws FileNotFoundException
	{
		try
		{
			File test_file = new File( test_file_name );
			Scanner in;
			in = new Scanner( test_file );

			String[] tokens;
			TreeData tempTree = null;
			boolean found = false;
			int val, class_val = -1;
			int matched_count = 0;
			int i = 0;

			in.nextLine();
			while ( in.hasNextLine() )
			{
				i++;
				tokens = in.nextLine().trim().split( "\\s+" );
				tempTree = tree;

				while ( tempTree.attribute != null )
				{
					val = Integer.parseInt( tokens[ tempTree.attribute.index ] );

					for ( Map.Entry< Integer, TreeData > entry : tempTree.childList.entrySet() )
					{
						if ( val == entry.getKey() )
						{
							if ( entry.getValue().attribute != null )
							{
								tempTree = entry.getValue();
								break;
							}
							else
							{
								found = true;
								class_val = entry.getValue().class_value;
								break;
							}
						}
					}

					if ( found )
					{
						found = false;
						break;
					}
				}

				val = Integer.parseInt( tokens[ tokens.length - 1 ] );
				if ( class_val == val )
					matched_count++;
			}

			DecimalFormat df = new DecimalFormat( "#.##" );
			System.out.println( "Accuracy of test data ( " + i + " instances ) = " + df.format( matched_count * 100.00 / i ) );
			in.close();
		}
		catch ( FileNotFoundException e )
		{
			System.out.println( "Cannot find test file - " + test_file_name );
			throw e;
		}
	}

	/**
	 * This inner class represents entropy of an attribute
	 * @author Akshay
	 */
	public class Entropy
	{
		double value = 0;
		double count_of_ones = 0;
		double count_of_zeros = 0;
		int attr_val = -1;
		ArrayList< Integer > trainDataList;

		public Entropy ( Attribute attr )
		{
			for ( int val : attr.attribute_data )
			{
				if ( attr.value( val ) == 0 )
					count_of_zeros++;
				else
					count_of_ones++;
			}

			value = calculateEntropy();
		}

		public Entropy ( double count_of_zeros, double count_of_ones, int attr_val, ArrayList< Integer > trainDataList )
		{
			this.attr_val = attr_val;
			this.count_of_zeros = count_of_zeros;
			this.count_of_ones = count_of_ones;
			this.trainDataList = trainDataList;
			value = calculateEntropy();
		}

		private double calculateEntropy()
		{
			if ( count_of_ones == 0 || count_of_zeros == 0 )
				return 0;

			double a = (count_of_ones / (count_of_ones + count_of_zeros));
			double b = (count_of_zeros / (count_of_zeros + count_of_ones));

			return (-a * Math.log( a ) / Math.log( 2 )) - (b * Math.log( b ) / Math.log( 2 ));
		}
	}
}
