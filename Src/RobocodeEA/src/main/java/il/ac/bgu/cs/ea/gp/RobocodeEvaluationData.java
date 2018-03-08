package il.ac.bgu.cs.ea.gp;

import ec.gp.GPData;

public class RobocodeEvaluationData extends GPData{
	public double data;
	
	public void copyTo(final GPData gpd)
    {
		((RobocodeEvaluationData)gpd).data = this.data;
    }
}
