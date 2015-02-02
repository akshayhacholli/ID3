/*************************************************************************
 * @author Akshay
 * 
 * A decision tree generating program using ID3(Iterative Dichotomiser 3) algorithm.
 * 
 * Dependencies : TrainData.java, TreeData.java
 * Compilation : javac -d . directory_path\ID3\*.java
 * 
 * Input : file path of train and test data as command line arguments
 * Output : Decision tree identified using train data and accuracy of decision tree for train and test data
 * Execution: java ID3/ID3 "path_of_train_file" "path_of_test_file"
 *
 *Example
 *C:\> java ID3/ID3 "D:\Machine Learning\Homework\HW1\Data\train.dat" "D:\Machine Learning\Homework\HW1\Data\test.dat"
 *attr1 = 0 : 
 *| | attr2 = 0 : 
 *| | | attr3 = 0 : 0
 *| | | attr3 = 1 : 1
 *| | attr2 = 1 : 
 *| | | attr3 = 0 : 1
 *| | | attr3 = 1 : 0
 *| attr1 = 1 : 
 *| | attr2 = 0 : 
 *| | | attr3 = 0 : 0
 *| | | attr3 = 1 : 0
 *| | attr2 = 1 : 
 *| | | attr3 = 0 : 0
 *| | | attr3 = 1 : 0
 *Accuracy of trained data ( 800 ) = 89.38
 *Accuracy of test data ( 203 ) = 87.19
 *************************************************************************/

/**
 * This class takes train file as an argument and process it to build a decision tree, then this tree is tested for accuracy
 * on train file and test file. 
 */

package ID3;

import java.io.FileNotFoundException;

public class ID3
{
	public static void main( String[] args )
	{
		try
		{
			//We need both train file and test file as input
			if ( args.length < 2 )
			{
				System.out.println( "Please input train file and test file as command line arguments." );
				return;
			}

			TrainData train_data = new TrainData( args[ 0 ] );		// This is a custom class which stores the train data from file.
			TreeData treeData = train_data.buildTree();				// This class stores the decision tree structure

			train_data.printTree( treeData );
			train_data.calculateAccuracyOfTrainFile( treeData );
			train_data.calculateAccuracyOfTestFile( treeData, args[ 1 ] );
		}
		catch ( FileNotFoundException e )
		{
			// Do nothing already output is printed
		}
	}
}
