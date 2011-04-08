package beast.core.parameter;

import beast.core.Valuable;
import beast.core.Input;
import beast.core.Description;

import java.util.List;
import java.util.ArrayList;
import java.io.PrintStream;


/**
 * @author Chieh-Hsi Wu
 * 
 */
@Description("Stores a matrix of real values.")
public class MatrixParameter extends RealParameter{
    public Input<List<RealParameter>> m_values = new Input<List<RealParameter>>("parameter","reference to a real parameter",
			new ArrayList<RealParameter>(), Input.Validate.REQUIRED, Valuable.class);

    public MatrixParameter() {
       m_pValues.setRule(Input.Validate.OPTIONAL);
    }
    private Parameter[] parameters;
	public void initAndValidate() throws Exception {
		// determine dimension
        List<RealParameter> values = m_values.get();
        parameters = new Parameter[m_values.get().size()];
		for (int i = 0; i < parameters.length; i++) {
			parameters[i] = values.get(i);
		}
	}

    public int getRowDimension(){
        return parameters.length;
    }



    public double getMatrixValue(int row, int col) {
        return (Double)parameters[row].getValue(col);
    }

    public double[][] getMatrix() {
        double[][] matrix = new double[getRowDimension()][];
        for(int i = 0;i < parameters.length;i++){
            double[] matrixi = new double[parameters[i].getDimension()];
            for(int j = 0; j < matrixi.length;j++){
                matrixi[j] = (Double)parameters[i].getValue(j);
            }
        }

        return matrix;
    }

    public Parameter getParameter(int i) {
        return parameters[i];
    }

        /**
     * Loggable interface implementation follows (partly, the actual
     * logging of values happens in derived classes) *
     */
    @Override
    public void init(PrintStream out) throws Exception {
        MatrixParameter matrix = (MatrixParameter) getCurrent();
        for(int i = 0; i < getRowDimension();i++){
            for (int iValue = 0; iValue < matrix.getParameter(i).getDimension(); iValue++) {
                out.print(getID()+i +"_"+ iValue + "\t");

            }
           
           
        }

    }
    /** Loggable implementation **/
    @Override
    public void log(int nSample, PrintStream out) {
        MatrixParameter matrix = (MatrixParameter) getCurrent();
        for(int i = 0; i < matrix.getRowDimension();i++){
            matrix.getParameter(i).log(nSample, out);
        }
    }
}
