package beast.core.parameter;

import beast.core.StateNode;
import beast.core.Description;
import beast.core.Input;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chieh-Hsi Wu
 */
@Description("This class stores a list of RealParameters objects, this is like a stateNode version of the CompoundValuable.")
public class CompoundParameter extends RealParameter2{
    public Input<List<RealParameter2>> parametersInput =
                new Input<List<RealParameter2>>("parameter", "refrence to a parameter", new ArrayList<RealParameter2>(), Input.Validate.REQUIRED);


    private int[][] parameterIndex;
    private ArrayList<RealParameter2> parameters;
    private int parameterCount;
    private int dimension;

    public CompoundParameter(){
        m_pValues.setRule(Input.Validate.OPTIONAL);
    }
    public CompoundParameter(RealParameter[] parameters){
        m_pValues.setRule(Input.Validate.OPTIONAL);
        //initAndValidate(parameters);
    }

    
    public void initAndValidate(){
        List<RealParameter2> parameters = parametersInput.get();
        RealParameter2[] parameterArray = new RealParameter2[parameters.size()];
        for(int i = 0; i < parameters.size();i++){
            parameterArray[i] =  parameters.get(i);
        }

        initAndValidate(parameterArray);
    }


    public void initAndValidate(RealParameter2[] parameters){
        // determine dimension
        dimension = 0;
        parameterCount  = parameters.length;
        this.parameters = new ArrayList<RealParameter2>();
		for (RealParameter2 parameter : parameters) {
			dimension += parameter.getDimension();
            this.parameters.add(parameter);
		}
        parameterIndex = new int[dimension][];

        int indexOffset = 0;
        for(int i = 0; i < parameters.length;i++){
            int paramDim = parameters[i].getDimension();
            for(int j = 0; j < paramDim; j++){
                parameterIndex[indexOffset+j] = new int[]{i,j};

            }
            indexOffset += paramDim;
        }
        checkSharedBounds();
        m_bIsDirty = new boolean[dimension];
    }

    public void checkSharedBounds(){
        double lower0 = parameters.get(0).getLower();
        double upper0 = parameters.get(0).getUpper();
        for(RealParameter2 parameter: parameters){
            System.err.println(parameter.getLower()+" "+parameter.getUpper());
            if(parameter.getLower() != lower0 || parameter.getUpper() != upper0)
                throw new RuntimeException("All parameters must share the same upper and lower bound");
        }
        m_fLower = lower0;
        m_fUpper = upper0;

    }

    

    public void setValue(int dim, double value) {
        startEditing(null);
        parameters.get(parameterIndex[dim][0]).setValueQuietly(parameterIndex[dim][1], value);
        m_bIsDirty[parameterIndex[dim][0]] = true;
        m_nLastDirty = parameterIndex[dim][0];
    }


    public void setValueQuietly(int dim, double value) {
        parameters.get(parameterIndex[dim][0]).setValueQuietly(parameterIndex[dim][1], value);
        m_bIsDirty[parameterIndex[dim][0]] = true;
        m_nLastDirty = parameterIndex[dim][0];
    }

    public void store() {
        for (RealParameter2 parameter : parameters) {
            parameter.store();
        }
    }

    public void restore() {
        for (RealParameter2 parameter : parameters) {
            parameter.restore();
        }
    }

    @Override
    public CompoundParameter copy() {
    	try {
	        @SuppressWarnings("unchecked")
			
            RealParameter[] copiedParameters = new RealParameter[parameterCount];
	        for(int i = 0; i <  parameterCount;i++){
                copiedParameters[i]=(RealParameter)(parameters.get(i)).copy();
            }
            CompoundParameter copy = new CompoundParameter(copiedParameters);
	        return copy;
    	} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }

    @Override
    public void setEverythingDirty(final boolean isDirty) {
    	setSomethingIsDirty(isDirty);
    	for(RealParameter2 parameter: parameters){
            parameter.setEverythingDirty(isDirty);
        }

	}


    public int getDimension() {
        return parameterCount;
    }

    @Override
    public void setDimension(int nDimension) {
    	throw new RuntimeException("Cannot handle change in dimension yet.");
    }

    public Double getValue() {
        return parameters.get(0).getValue(0);
    }

    public Double getValue(int dim) {
        return parameters.get(parameterIndex[dim][0]).getValue(parameterIndex[dim][1]);
    }


    public Double[] getValues() {
        int k = 0;
        Double[] values = new Double[dimension];
        for(RealParameter2 parameter: parameters){
            int dim = parameter.getDimension();
            for(int i = 0; i < dim;i++){
                values[k++] = parameter.getValue(i);
            }

        }
        return values;
    }

    public double getArrayValue(int dim){
        return getValue(dim);
    }

    public void setValue(Double value) {
        startEditing(null);
        parameters.get(0).setValue(value);
        m_bIsDirty[0] = true;
        m_nLastDirty = 0;

    }

    public void setValue(int dim, Double value) {
        startEditing(null);
        parameters.get(parameterIndex[dim][0]).setValue(parameterIndex[dim][1],value);
        m_bIsDirty[dim] = true;
        m_nLastDirty = dim;

    }

    public String toString() {
        final StringBuffer buf = new StringBuffer();
        buf.append(m_sID).append("[").append(getDimension()).append("] ");
        buf.append("(").append(m_fLower).append(",").append(m_fUpper).append("): ");
        for(RealParameter2 parameter: parameters){
            int dim = parameter.getDimension();
            for(int i = 0; i < dim; i++) {
                buf.append(parameter.getValue(i)).append(" ");
            }
        }

        return buf.toString();
    }


    @Override
    public void assignTo(StateNode other) {
        throw new RuntimeException("Yet to be implemented. Not quite sure what this class does though ...");
    }


    @Override
    public void assignFrom(StateNode other) {
        throw new RuntimeException("Yet to be implemented. Not quite sure what this class does though ...");
    }


}
