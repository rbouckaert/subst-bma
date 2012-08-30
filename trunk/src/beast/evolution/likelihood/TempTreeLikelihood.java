package beast.evolution.likelihood;

import beast.core.Distribution;
import beast.core.Input;
import beast.core.State;
import beast.core.parameter.QuietRealParameter;
import beast.core.parameter.RealParameter;
import beast.evolution.alignment.Alignment;
import beast.evolution.alignment.AlignmentSubset;
import beast.evolution.branchratemodel.BranchRateModel;
import beast.evolution.sitemodel.QuietSiteModel;
import beast.evolution.sitemodel.SiteModel;
import beast.evolution.substitutionmodel.NtdBMA;
import beast.evolution.substitutionmodel.SubstitutionModel;
import beast.evolution.tree.Tree;

import java.util.List;
import java.util.Random;

/**
 * @author Chieh-Hsi Wu
 */
public class TempTreeLikelihood extends Distribution {
    public Input<Alignment> dataInput = new Input<Alignment>(
            "data",
            "sequence data for the beast.tree",
            Input.Validate.REQUIRED
    );

    public Input<QuietSiteModel> siteModelInput = new Input<QuietSiteModel>(
            "siteModel",
            "Models the evolution of a site in an alignment",
            Input.Validate.REQUIRED
    );

    public Input<Tree> treeInput = new Input<Tree>(
            "tree",
            "phylogenetic beast.tree with sequence data in the leafs",
            Input.Validate.REQUIRED
    );

    public Input<BranchRateModel.Base> branchRateModelInput = new Input<BranchRateModel.Base>(
            "branchRateModel",
            "A model describing the rates on the branches of the beast.tree."
    );

    public Input<Boolean> useAmbiguitiesInput = new Input<Boolean>(
            "useAmbiguities",
            "flag to indicate leafs that sites containing ambigue states should be handled instead of ignored (the default)",
            false
    );

    protected RealParameter defaultMu;
    protected Alignment alignment;
    protected TempSiteTreeLikelihood[] treeLiks;
    protected SubstitutionModel substModel;
    
    public void initAndValidate() throws Exception{
        defaultMu = new RealParameter(new Double[]{1.0});
        alignment = dataInput.get();
        int siteCount = alignment.getSiteCount();
        int patternCount = alignment.getPatternCount();
        treeLiks = new TempSiteTreeLikelihood[patternCount];

        //
        int[] firstPatternOccur = new int[patternCount];
        for(int iPat = 0; iPat < firstPatternOccur.length; iPat++){
            firstPatternOccur[iPat] = -1;
        }
        for(int iSite = 0; iSite < siteCount; iSite++){

            int iPat = alignment.getPatternIndex(iSite);
            if(firstPatternOccur[iPat] == -1){
                firstPatternOccur[iPat] = iSite;
            }
        }


        for(int i = 0; i < firstPatternOccur.length; i++){
            AlignmentSubset sitePattern = new AlignmentSubset(alignment,firstPatternOccur[i]);
            TempSiteTreeLikelihood treeLik = new TempSiteTreeLikelihood();
                treeLik.initByName(
                    "data", sitePattern,
                    "tree", treeInput.get(),
                    "siteModel", siteModelInput.get(),
                    "branchRateModel", branchRateModelInput.get(),
                    "useAmbiguities", useAmbiguitiesInput.get()
            );
            treeLiks[i] = treeLik;
        }

        substModel = siteModelInput.get().getSubstitutionModel();
        

    }

    public double calculateLogP(
            RealParameter modelParameters,
            RealParameter modelCode,
            RealParameter freqs,
            int site){
        try{

            if(substModel instanceof NtdBMA){
                ((QuietRealParameter)((NtdBMA)substModel).getLogKappa()).setValueQuietly(0, modelParameters.getValue(0));
                ((QuietRealParameter)((NtdBMA)substModel).getLogTN()).setValueQuietly(0, modelParameters.getValue(1));
                ((QuietRealParameter)((NtdBMA)substModel).getLogAC()).setValueQuietly(0,modelParameters.getValue(2));
                ((QuietRealParameter)((NtdBMA)substModel).getLogAT()).setValueQuietly(0,modelParameters.getValue(3));
                ((QuietRealParameter)((NtdBMA)substModel).getLogGC()).setValueQuietly(0,modelParameters.getValue(4));
                ((QuietRealParameter)((NtdBMA)substModel).getModelChoose()).setValueQuietly(0,modelCode.getValue());
                ((QuietRealParameter)((NtdBMA)substModel).getFreqs()).setValueQuietly(0,freqs.getValue(0));
                ((QuietRealParameter)((NtdBMA)substModel).getFreqs()).setValueQuietly(1,freqs.getValue(1));
                ((QuietRealParameter)((NtdBMA)substModel).getFreqs()).setValueQuietly(2,freqs.getValue(2));
                ((QuietRealParameter)((NtdBMA)substModel).getFreqs()).setValueQuietly(3,freqs.getValue(3));

            }else{
                throw new RuntimeException("Need NtdBMA");
            }
            /*NtdBMA ntdBMA = DPNtdBMA.createNtdBMA(modelParameters,modelCode,freqs);

            SiteModel siteModel = new SiteModel();
            siteModel.initByName(
                    "substModel", ntdBMA
            );*/
            int iPat = alignment.getPatternIndex(site);
            //treeLiks[iPat].setSiteModel(siteModel);
            ((NtdBMA)substModel).setUpdateMatrix(true);
            logP = treeLiks[iPat].calculateLogP();
        }catch(Exception e){
            throw new RuntimeException(e);

        }
        return logP;
    }


    public double calculateLogP(
            RealParameter modelParameters,
            RealParameter modelCode,
            RealParameter freqs,
            RealParameter rate,
            int site){
        try{

            if(substModel instanceof NtdBMA){
                ((QuietRealParameter)((NtdBMA)substModel).getLogKappa()).setValueQuietly(0,modelParameters.getValue(0));
                ((QuietRealParameter)((NtdBMA)substModel).getLogTN()).setValueQuietly(0,modelParameters.getValue(1));
                ((QuietRealParameter)((NtdBMA)substModel).getLogAC()).setValueQuietly(0,modelParameters.getValue(2));
                ((QuietRealParameter)((NtdBMA)substModel).getLogAT()).setValueQuietly(0,modelParameters.getValue(3));
                ((QuietRealParameter)((NtdBMA)substModel).getLogGC()).setValueQuietly(0,modelParameters.getValue(4));
                ((QuietRealParameter)((NtdBMA)substModel).getModelChoose()).setValueQuietly(0,modelCode.getValue());
                ((QuietRealParameter)((NtdBMA)substModel).getFreqs()).setValueQuietly(0,freqs.getValue(0));
                ((QuietRealParameter)((NtdBMA)substModel).getFreqs()).setValueQuietly(1,freqs.getValue(1));
                ((QuietRealParameter)((NtdBMA)substModel).getFreqs()).setValueQuietly(2,freqs.getValue(2));
                ((QuietRealParameter)((NtdBMA)substModel).getFreqs()).setValueQuietly(3,freqs.getValue(3));

            }else{
                throw new RuntimeException("Need NtdBMA");
            }
            /*NtdBMA ntdBMA = DPNtdBMA.createNtdBMA(modelParameters,modelCode,freqs);

            SiteModel siteModel = new SiteModel();
            siteModel.initByName(
                    "substModel", ntdBMA
            );*/
            ((NtdBMA)substModel).setUpdateMatrix(true);
            ((QuietRealParameter)siteModelInput.get().getRateParameter()).setValueQuietly(0,rate.getValue());
            int iPat = alignment.getPatternIndex(site);         
            logP = treeLiks[iPat].calculateLogP();
        }catch(Exception e){
            throw new RuntimeException(e);

        }
        return logP;
    }


    public double calculateLogP(
            RealParameter rateParameter,
            int site){
        try{
            ((QuietRealParameter)siteModelInput.get().getRateParameter()).setValueQuietly(0,rateParameter.getValue());
            int iPat = alignment.getPatternIndex(site);
            logP = treeLiks[iPat].calculateLogP();
        }catch(Exception e){
            throw new RuntimeException(e);

        }
        return logP;
    }


 

    public List<String> getConditions(){
        return null;

    }

    public List<String> getArguments(){
        return null;

    }

    public boolean requiresRecalculation(){
        System.err.println("what?");
        return false;
    }

    public void sample(State state, Random random){
        throw new RuntimeException("Not yet implemented as it doesn't make much sense to do so in this case");
    }





}
