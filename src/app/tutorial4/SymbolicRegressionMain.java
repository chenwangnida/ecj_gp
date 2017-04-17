package app.tutorial4;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import ec.EvolutionState;
import ec.Evolve;
import ec.gp.GPIndividual;
import ec.simple.SimpleStatistics;
import ec.util.MersenneTwisterFast;
import ec.util.ParameterDatabase;

public class SymbolicRegressionMain {

	/**
	 * Field Declarations
	 */
	/** SPECT dataset name **/
	public final static String SELECTED_DATASET = "selected";

	/** Standard accuracy fitness function name **/
	public final static String ACCURACY_FITNESS_FUNCTION = "accuracy";

	/** Weighted-average fitness function name **/
	public final static String WEIGHTED_FITNESS_FUNCTION = "average";

	/** The standard classification method **/
	public final static String STANDARD_METHOD = "standard_method";

	/** The new classification method **/
	public final static String NEW_METHOD = "new_method";

	// Specify the random number generator (to be used as feature indexes)
	public static MersenneTwisterFast RANDOM_GENERATOR;

	// Specify the fitness function to be used (standard accuracy,
	// weighted-average, or others)
	public final static String FITNESS_FUNCTION = ACCURACY_FITNESS_FUNCTION;

	// Specify the classification method (standard or the new method)
	public final static String CLASSIFICATION = STANDARD_METHOD;

	// Specify the dataset name to be used
	public final static String DATASET_NAME = SELECTED_DATASET;

	/**
	 * The main method that starts and controls the main flow of the program
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		// 1- Specify the parameter values of the Evolve main method
		String[] params = new String[] { "-file", "parameters.params", "-p", "seed.0=0", "-p", "jobs=1" };

		// 2- The array of seed values
		int[] seeds = new int[] { 1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192, 16384, -3, -6, -12,
				-24, -48, -96, -192, -384, -768, -1536, -3027, -6144, -12288, -24576, -49156 };

		// 2- Start the main process of the program
		// Specify the folds variable's value
		int jobs = 30;
		for (int i = 0; i < jobs; i++) {

			// Do NOT forget the seed value goes here
			params[3] = "seed.0=" + seeds[i];

			// Construct and initialise a new EvolutionState object
			EvolutionState state = Evolve.possiblyRestoreFromCheckpoint(params);

			// Construct and initialise a new ParameterDatabase object
			ParameterDatabase parameters = Evolve.loadParameterDatabase(params);

			// Initialise the state variable using the ParametersDatabase object
			state = Evolve.initialize(parameters, 0);
			state.output.systemMessage("Job: " + i);

			// Construct the array of jobs of the state variable
			state.job = new Object[1];
			state.job[0] = new Integer(i);

			// Set the run time arguments of the state variable
			state.runtimeArguments = params;

			String jobFilePrefix = "job." + i + ".";
			state.output.setFilePrefix(jobFilePrefix);
			state.checkpointPrefix = jobFilePrefix + state.checkpointPrefix;

			// Set the Random generator which can be obtained from state
			RANDOM_GENERATOR = state.random[0];

			try {

				// Run the EvolutionState object and make sure to start fresh
				state.run(EvolutionState.C_STARTED_FRESH);

				// Get the best evolved program and print it out
				GPIndividual bestIndividual = (GPIndividual) ((SimpleStatistics) state.statistics).best_of_run[0];

				// Print out the accuracy on the training set of the best
				// evolved program
				state.output.message("The training accuracy: " + bestIndividual.fitness.fitnessToStringForHumans());

				// Evaluate the best evolved program on the test set and
				// get the result in a FitnessResult object
				MultiValuedRegression problem = (MultiValuedRegression) (state.evaluator.p_problem);
				problem.evaluate(state, bestIndividual, 0, 0);

				// After evaluation, do not forget to clean up
				Evolve.cleanup(state);

				// Also set the ParameterDatabase object to null (just in case)
				parameters = null;

			} catch (Exception ex) {
				ex.printStackTrace();
				System.exit(1);
			}

		}

	}
}
