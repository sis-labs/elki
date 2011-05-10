package experimentalcode.frankenb.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.ids.ModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.relation.Relation;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;
import experimentalcode.frankenb.model.ifaces.IDataSet;

/**
 * No description given.
 * 
 * @author Florian Frankenberger
 */
public final class DataSetUtils {
  private DataSetUtils() {
  }

  /**
   * Splits a data set according to the dimension and position into two data
   * sets. If dimension is 1 the data gets split into two data sets where all
   * points with x < position get into one data set and all points >= position
   * get into the other
   * 
   * @param dataSet
   * @param dimension
   * @param position
   */
  public static <V extends NumberVector<?, ?>> Pair<ModifiableDBIDs, ModifiableDBIDs> split(Relation<V> dataSet, DBIDs ids, int dimension, double position) {
    ModifiableDBIDs dataSetLower = DBIDUtil.newArray();
    ModifiableDBIDs dataSetHigher = DBIDUtil.newArray();
    for(DBID id : ids) {
      V vector = dataSet.get(id);
      if(vector.doubleValue(dimension) < position) {
        dataSetLower.add(id);
      }
      else {
        dataSetHigher.add(id);
      }
    }
    return new Pair<ModifiableDBIDs, ModifiableDBIDs>(dataSetLower, dataSetHigher);
  }

  /**
   * Returns the median of a data set by using a sampling method.
   * 
   * @param dataSet
   * @param dimension
   * @return
   */
  public static <V extends NumberVector<?, ?>> double quickMedian(IDataSet<V> dataSet, int dimension, int numberOfSamples) {
    int everyNthItem = (int) Math.max(1, Math.floor(dataSet.size() / (double) numberOfSamples));

    int counter = 0;
    List<Double> list = new ArrayList<Double>();
    for(DBID id : dataSet.iterDBIDs()) {
      if(counter++ % everyNthItem == 0) {
        NumberVector<?, ?> vector = dataSet.get(id);
        list.add(vector.doubleValue(dimension));
      }
    }

    Collections.sort(list);
    // LoggingUtil.debug(list.toString());
    double median;
    if(list.size() == 1) {
      return list.get(0);
    }
    else if(list.size() % 2 == 1) {
      median = list.get(((list.size() + 1) / 2) - 1);
    }
    else {
      double v1 = list.get(list.size() / 2);
      double v2 = list.get((list.size() / 2) - 1);
      median = (v1 + v2) / 2.0;
    }
    return median;
  }

  public static <V extends NumberVector<?, ?>> double median(IDataSet<V> dataSet, int dimension) {
    return quickMedian(dataSet, dimension, dataSet.size());
  }
}
