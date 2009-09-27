package chemface.placing;

import chemface.*;

public interface Placer {

/**
 * Tells name of the placer.
 * 
 * This name shall be human-readable and shall give some information
 * how the algorithm works.
 * 
 */
public String getName();

/**
 * Sets reactant to be used for placing.
 *
 * @warning This method must copy the reactant.
 * 
 */
public void setReactant(final Reactant r);

/**
 * Returns placed reactant.
 * It does not make much sense to call this method without prior call
 * to placeOptimally() or when this method returns false.
 * 
 */
public Reactant getReactant();

/**
 * Finds the optimal placement.
 * 
 * @return Success of the quest for optimal placement.
 * 
 */
public boolean placeOptimally();


}

