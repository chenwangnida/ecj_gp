package app.generator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.github.jamm.MemoryMeter;

import ec.EvolutionState;
import ec.Evolve;
import ec.gp.GPIndividual;
import ec.simple.SimpleStatistics;
import ec.util.MersenneTwisterFast;
import ec.util.ParameterDatabase;

public class GeneratorClient {

	/**
	 * Field Declarations
	 */
	// Specify the random number generator (to be used as feature indexes)
	public static MersenneTwisterFast RANDOM_GENERATOR;

	/**
	 * The main method that starts and controls the main flow of the program
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		// 0- You can then use MemoryMeter in your code like this
		MemoryMeterTest memoryMeterTest = new MemoryMeterTest();
		memoryMeterTest.testMacOSX_x86_64();
		
		// 1- Specify the parameter values of the Evolve main method
		String[] params = new String[] { "-file", "parameters.params", "-p", "seed.0=0", "-p", "jobs=1", "-p",
				"generations=51", "-p", "pop.subpop.0.size=1024", "-p", "pop.subpop.0.species.pipe.source.0.prob=0.0",
				"-p", "pop.subpop.0.species.pipe.source.1.prob=0.0" };

		// 2- Initialize the folds variable's value
		int seeds = 0;
		int generations = 510;
		int individuals = 1024;
		double crossoverRate = 0.9;
		double reproductionRate = 0.1;

		// 3- Start the main process of the program
		int jobs = 1;
		for (int i = 0; i < jobs; i++) {

			// Do NOT forget the parameters value goes here
			passParameters(params, seeds, generations, individuals, crossoverRate, reproductionRate);

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

				// Print out the fitness of the best evolved program
				state.output.message("The optimal fitness: " + bestIndividual.fitness.fitnessToStringForHumans());

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

	private static void passParameters(String[] params, int seeds, int generations, double individuals,
			double crossoverRate, double reproductionRate) {

		params[3] = "seed.0=" + seeds;
		params[7] = "generations=" + generations;
		params[9] = "pop.subpop.0.size=" + individuals;
		params[11] = "pop.subpop.0.species.pipe.source.0.prob=" + crossoverRate;
		params[13] = "pop.subpop.0.species.pipe.source.1.prob=" + reproductionRate;
	}
}
