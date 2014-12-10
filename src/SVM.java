import libsvm.*;
import weka.core.Instance;
import weka.core.Instances;

import java.util.Collections;
import java.util.List;

public class SVM {

    public static double create(Instances trainingInstances) {

        List<TrainingRecord> trainingRecords = TrainingRecord.convert(trainingInstances);


        //Create an SVM and train it


        svm_parameter param = new svm_parameter();

        // default values

        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 3;
        param.gamma = 1d/100000; //All the same when this is 1, change to 0.01
        param.coef0 = 0;
        param.nu = 0.5;
        param.cache_size = 100;
        param.C = 2048;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[0];
        param.weight = new double[0];

        svm_problem prob = new svm_problem();
        prob.l = trainingRecords.size(); //Length;
        prob.y = new double[prob.l];
        prob.x = new svm_node[prob.l][];

        List<Double> atts = trainingRecords.get(0).getAttributes();

        //Assign all training records to the SVM
        for (int i = 0; i < trainingRecords.size(); i++) {
            prob.x[i] = new svm_node[atts.size()];

            //Only include the selected features here
            for (int j = 0; j < atts.size(); j++) {
                svm_node node = new svm_node();
                node.index = j;
                node.value = atts.get(j);
                //System.out.println(node.value);
                prob.x[i][j] = node;
            }


            prob.y[i] = trainingRecords.get(i).getClazz();
        }


        //System.out.println("Errors with SVM: " + svm.svm_check_parameter(prob,param));
        svm.svm_set_print_string_function(new svm_print_interface() {
            @Override
            public void print(String s) {
                //Don't want to fill console with garbage
                //So print nothing
            }
        });

        //Tune parameters
        double[] gammas = new double[] { 0.001,0.01,0.01,1,10,100,1000 };
        double[] costs = new double[] { 0.1, 0.25,0.5,0.75,1,2,4,8,16};
        double bestAcc = 0;
        int[] bestInd = new int[] { 0, 0 };

        //System.out.println("===Classes===");
        //for (int i = 0; i < trainingRecords.size(); i++)
        //    System.out.println(trainingRecords.get(i).getAttributes().size() + " | " + trainingRecords.get(i).getClazz());
        Collections.shuffle(trainingRecords);

        for (int g = 0; g < gammas.length; g++)
            for (int c = 0; c < costs.length; c++) {
                param.gamma = gammas[g];
                param.C = costs[c];

                double[] target = new double[prob.l];
                svm.svm_cross_validation(prob, param, 10, target);
                double correctCounter = 0;
                for (int i = 0; i < target.length; i++)
                    if (target[i] == trainingRecords.get(i).getClazz())
                        correctCounter++;

                //If all predictions are the same, set accuracy to zero because bad parameter tuning
                boolean good = false;
                for (int i = 0; i < target.length; i++)
                    for (int j = 0; j < target.length; j++)
                        if (i != j)
                            if (target[i] != target[j]) {
                                good = true;
                                break;
                            }

                if (good) {
                    double acc = correctCounter / target.length;
                    if (acc > bestAcc) {
                        bestInd[0] = g;
                        bestInd[1] = c;
                        bestAcc = acc;
                    }
                }
            }

        param.gamma = gammas[bestInd[0]];
        param.C = costs[bestInd[1]];
        svm_model model = svm.svm_train(prob, param);

        return bestAcc;
    }

    public static double predict(svm_model model, Instance instance) {

        TrainingRecord testRecord = new TrainingRecord(instance);

        List<Double> atts = testRecord.getAttributes();
        svm_node[] nodes = new svm_node[atts.size()];

        //Only include the selected features here
        for (int j = 0; j < atts.size(); j++) {
            svm_node node = new svm_node();
            node.index = j;
            node.value = atts.get(j);
            nodes[j] = node;
        }

        return svm.svm_predict(model,nodes);
    }

    public static double eval(svm_model model, Instances testInstances){

        List<TrainingRecord> testRecords = TrainingRecord.convert(testInstances);

        //Evaluate the SVM
        int correctPredictions = 0;

        for (int i = 0; i < testRecords.size(); i++) {
            List<Double> atts = testRecords.get(i).getAttributes();
            svm_node[] nodes = new svm_node[atts.size()];

            //Only include the selected features here
            for (int j = 0; j < atts.size(); j++) {
                svm_node node = new svm_node();
                node.index = j;
                node.value = atts.get(j);
                nodes[j] = node;
            }

            int predicted = (int)svm.svm_predict(model,nodes);

            //Correctly predicted
            if (predicted == testRecords.get(i).getClazz())
                correctPredictions++;

        }

        double accuracy = (double)correctPredictions/testRecords.size();

        return accuracy;
    }
}
