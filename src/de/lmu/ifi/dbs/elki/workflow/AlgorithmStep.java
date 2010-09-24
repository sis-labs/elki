package de.lmu.ifi.dbs.elki.workflow;

import java.util.List;

import de.lmu.ifi.dbs.elki.algorithm.Algorithm;
import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.result.AnnotationBuiltins;
import de.lmu.ifi.dbs.elki.result.IDResult;
import de.lmu.ifi.dbs.elki.result.MultiResult;
import de.lmu.ifi.dbs.elki.result.Result;
import de.lmu.ifi.dbs.elki.result.ResultUtil;
import de.lmu.ifi.dbs.elki.result.TrivialResult;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectListParameter;

/**
 * The "algorithms" step, where data is analyzed.
 * 
 * @author Erich Schubert
 *
 * @param <O> database object type
 */
public class AlgorithmStep<O extends DatabaseObject> implements Parameterizable {
  /**
   * Holds the algorithm to run.
   */
  private List<Algorithm<O, Result>> algorithms;
  
  /**
   * The algorithm output
   */
  private MultiResult result = null;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public AlgorithmStep(Parameterization config) {
    super();
    config = config.descend(this);
    // parameter algorithm
    final ObjectListParameter<Algorithm<O, Result>> ALGORITHM_PARAM = new ObjectListParameter<Algorithm<O, Result>>(OptionID.ALGORITHM, Algorithm.class);
    if(config.grab(ALGORITHM_PARAM)) {
      algorithms = ALGORITHM_PARAM.instantiateClasses(config);
    }
  }
  
  /**
   * Run algorithms.
   * 
   * @param database
   * @return Algorithm result
   */
  public MultiResult runAlgorithms(Database<O> database) {
    result = new MultiResult();
    for (Algorithm<O, Result> algorithm : algorithms) {
      final Result algResult = algorithm.run(database);
      if (result == null) {
        result = ResultUtil.ensureMultiResult(algResult);
      } else {
        result.addResult(algResult);
      }
    }
    // standard annotations from the source file
    TrivialResult trivial = new TrivialResult();
    new AnnotationBuiltins(database).prependToResult(trivial);
    trivial.addResult(new IDResult());
    
    // Add trivial "result" to algorithm result.
    result.addResult(trivial);
    return result;
  }

  /**
   * Get the algorithm result.
   * 
   * @return Algorithm result.
   */
  public MultiResult getResult() {
    return result;
  }
}