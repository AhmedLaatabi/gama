/**
 *  CSVfileloading
 *  Author: Patrick Taillandier
 *  Description: Shows how to import a CSV file to create agents
 */

model CSVfileloading

global {
	
	init {
		//create iris agents from the CSV file (use of the header of the CSV file).
		create iris from:file( "../includes/iris.csv") header: true with:
			[sepal_length::float(get("sepal_length")), 
				sepal_width::float(get("sepalwidth")), 
				petal_length::float(get("petallength")),
				petal_width::float(get("petal_width")), 
				type::string(get("type"))
			];	
	}
}

species iris {
	float sepal_length;
	float sepal_width;
	float petal_length;
	float petal_width;
	string type;
	rgb color ;
	
	init {
		color <- type ="Iris-setosa" ? #blue : ((type ="Iris-virginica") ? #red: #yellow);
	}
	
	aspect default {
		draw circle(petal_width) color: color; 
	}
}

experiment main type: gui{
	output {
		display map {
			species iris;
		}
	}
	
}
