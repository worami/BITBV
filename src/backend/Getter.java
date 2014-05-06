package backend;

import java.util.List;

public class Getter {
	
	Connector connection;
	
	public Getter(){
		connection = new Connector();
	}
	
	public List<String[]> getContainers(){
		List<String[]> containers = connection.sql("SELECT  PickupDate, ContainerNumber FROM modalitycontainerstatisticsinfocumm;");
		return containers;
	}

}
