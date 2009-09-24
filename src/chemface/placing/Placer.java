package chemface.placing;

import chemface.*;

public interface Placer {

/**
 * @warning This method must copy the reactant.
 * 
 */
public void setReactant(final Reactant r);
public boolean placeOptimally();
public Reactant getReactant();

}

