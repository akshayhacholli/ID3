package ID3;

import java.util.HashMap;

public class TreeData
{
	Attribute attribute = null;
	int level;
	int class_value = -1;
	HashMap< Integer, TreeData > childList = new HashMap< Integer, TreeData >();

	public TreeData ( Attribute attribute, int level )
	{
		this.attribute = attribute;
		this.level = level;
	}

	public TreeData ( int class_value )
	{
		this.class_value = class_value;
	}
}
